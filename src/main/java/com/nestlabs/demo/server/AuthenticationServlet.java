package com.nestlabs.demo.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * This servlet implements authentication of users by means of OAuth2 protocol.
 * It performs authentication in two steps. At the first step it redirects user to login page
 * of OAuth provider. At the second step it gets access token from OAuth provider using
 * authorization code received at the previous step.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 25.12.2015
 */
public class AuthenticationServlet extends HttpServlet {

    /**
     * The URL of login page of OAuth provider.
     */
    private String loginUrl;

    /**
     * The URL of OAuth provider used for receiving of access token.
     */
    private String accessTokenUrl;

    /**
     * The identifier of the application in the OAuth provider.
     */
    private String clientId;

    /**
     * The secret key of the application in the OAuth provider.
     */
    private String clientSecret;

    /**
     * The HTTP client that will be used for sending requests to OAuth provider.
     */
    private CloseableHttpAsyncClient httpClient;

    /**
     * The default value of timeout in milliseconds for receiving of HTTP connection from pool.
     */
    private static final int DEFAULT_HTTP_CONNECTION_REQUEST_TIMEOUT = 5000;

    /**
     * The default value of connect timeout in milliseconds for HTTP client.
     */
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 10000;

    /**
     * The default value of socket timeout in milliseconds for HTTP client.
     */
    private static final int DEFAULT_HTTP_SOCKET_TIMEOUT = 10000;

    private static final String OAUTH_LOGIN_URL_PROPERTY = "oauth.loginUrl";
    private static final String OAUTH_ACCESS_TOKEN_URL_PROPERTY = "oauth.accessTokenUrl";
    private static final String OAUTH_CLIENT_ID_PROPERTY = "oauth.clientId";
    private static final String OAUTH_CLIENT_SECRET_PROPERTY = "oauth.clientSecret";

    private static final String HTTP_CONNECTION_REQUEST_TIMEOUT_PROPERTY = "http.connectionRequestTimeout";
    private static final String HTTP_CONNECTION_TIMEOUT_PROPERTY = "http.connectTimeout";
    private static final String HTTP_SOCKET_TIMEOUT_PROPERTY = "http.socketTimeout";

    private static final String LOGIN_URI = "/login";

    private static final String CONFIGURATION_FILE_PARAMETER = "configurationFile";
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String CLIENT_SECRET_PARAMETER = "client_secret";
    private static final String CODE_PARAMETER = "code";
    private static final String STATE_PARAMETER = "state";
    private static final String GRANT_TYPE_PARAMETER = "grant_type";

    private static final String LOCATION_HEADER = "Location";

    private static final String AUTHORIZATION_CODE = "authorization_code";

    private static final String ACCESS_TOKEN = "access_token";

    private static final String ACCESS_TOKEN_COOKIE = "nest-token";

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // Get name of configuration file.
        String configurationFile = servletConfig.getInitParameter(CONFIGURATION_FILE_PARAMETER);
        if (configurationFile == null || configurationFile.isEmpty()) {
            throw new ServletException(
                    String.format("Value of servlet parameter '%s' is not specified", CONFIGURATION_FILE_PARAMETER)
            );
        }

        // Try to load configuration file from classpath.
        InputStream inputStream = AuthenticationServlet.class.getClassLoader().getResourceAsStream(configurationFile);
        if (inputStream == null) {
            throw new ServletException("Cannot find configuration file " + configurationFile);
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ServletException("Failed to load configuration file " + configurationFile, e);
        }

        // Read parameters required to send requests to OAuth provider.
        loginUrl = getStringProperty(properties, OAUTH_LOGIN_URL_PROPERTY);
        accessTokenUrl = getStringProperty(properties, OAUTH_ACCESS_TOKEN_URL_PROPERTY);
        clientId = getStringProperty(properties, OAUTH_CLIENT_ID_PROPERTY);
        clientSecret = getStringProperty(properties, OAUTH_CLIENT_SECRET_PROPERTY);

        // Create HTTP client.
        httpClient = createHttpClient(properties);
    }

    @Override
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Failed to close HTTP client", e);
            } finally {
                httpClient = null;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (httpClient == null) {
            throw new IllegalStateException("Servlet is not properly initialized");
        }

        if (request.getRequestURI().endsWith(LOGIN_URI)) {
            // Redirect client to login page of OAuth provider .
            String state = UUID.randomUUID().toString();
            String url = loginUrl + "?" + CLIENT_ID_PARAMETER + "=" + clientId + "&" + STATE_PARAMETER + "=" + state;
            response.sendRedirect(url);
        } else {
            // Complete authentication, receive access token from OAuth provider.
            receiveAccessToken(request, response);
        }
    }

    private void receiveAccessToken(final HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get value of authorization code from request.
        String code = request.getParameter(CODE_PARAMETER);
        if (code == null || code.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Switch to asynchronous mode.
        final AsyncContext asyncContext = request.startAsync(request, response);

        // Create HTTP POST request.
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair(CLIENT_ID_PARAMETER, clientId));
        parameters.add(new BasicNameValuePair(CLIENT_SECRET_PARAMETER, clientSecret));
        parameters.add(new BasicNameValuePair(CODE_PARAMETER, code));
        parameters.add(new BasicNameValuePair(GRANT_TYPE_PARAMETER, AUTHORIZATION_CODE));

        HttpPost httpPost = new HttpPost(accessTokenUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));

        // Send request.
        httpClient.execute(
                httpPost,
                new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(HttpResponse httpResponse) {
                        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();

                        int statusCode = httpResponse.getStatusLine().getStatusCode();
                        if (statusCode == HttpServletResponse.SC_OK) {
                            HttpEntity httpEntity = httpResponse.getEntity();
                            if (httpEntity != null) {
                                try {
                                    // Try to read value of access token from response.
                                    ObjectMapper mapper = new ObjectMapper();
                                    JsonNode node =  mapper.readTree(httpEntity.getContent());
                                    node = node.path(ACCESS_TOKEN);
                                    if (node.isMissingNode() || !node.isTextual()) {
                                        log.error("Invalid response was received");
                                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                    } else {
                                        // Add value of access token into cookie.
                                        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE, node.asText());
                                        cookie.setPath("/");
                                        response.addCookie(cookie);
                                        // Redirect client to root page.
                                        response.setHeader(LOCATION_HEADER, request.getContextPath());
                                        response.setStatus(HttpServletResponse.SC_FOUND);
                                    }
                                } catch (IOException e) {
                                    log.error("Failed to read response body", e);
                                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                } finally {
                                    EntityUtils.consumeQuietly(httpEntity);
                                }
                            } else {
                                log.error("Empty response was received");
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }
                        } else if (statusCode == HttpServletResponse.SC_GATEWAY_TIMEOUT ||
                                statusCode == HttpServletResponse.SC_SERVICE_UNAVAILABLE ||
                                statusCode == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                        } else {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }

                        asyncContext.complete();
                    }

                    @Override
                    public void failed(Exception e) {
                        log.error("Failed to send request to OAuth provider", e);

                        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                        asyncContext.complete();
                    }

                    @Override
                    public void cancelled() {
                        log.warn("Request was cancelled");
                    }
                }
        );
    }

    private CloseableHttpAsyncClient createHttpClient(Properties properties) throws ServletException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(
                        getIntProperty(
                                properties,
                                HTTP_CONNECTION_REQUEST_TIMEOUT_PROPERTY,
                                DEFAULT_HTTP_CONNECTION_REQUEST_TIMEOUT
                        )
                )
                .setConnectTimeout(
                        getIntProperty(
                                properties,
                                HTTP_CONNECTION_TIMEOUT_PROPERTY,
                                DEFAULT_HTTP_CONNECT_TIMEOUT
                        )
                )
                .setSocketTimeout(
                        getIntProperty(
                                properties,
                                HTTP_SOCKET_TIMEOUT_PROPERTY,
                                DEFAULT_HTTP_SOCKET_TIMEOUT
                        )
                )
                .build();

        httpClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        httpClient.start();

        return httpClient;
    }

    private String getStringProperty(Properties properties, String propertyName) throws ServletException {
        String value = properties.getProperty(propertyName);
        if (value == null || value.isEmpty()) {
            throw new ServletException(
                    String.format("Value of configuration property '%s' is not specified", propertyName)
            );
        }

        return value;
    }

    private int getIntProperty(Properties properties, String propertyName, int defaultValue) throws ServletException {
        String value = properties.getProperty(propertyName);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ServletException(
                    String.format("Value of configuration property '%s' is not valid integer number", propertyName)
            );
        }
    }

}
