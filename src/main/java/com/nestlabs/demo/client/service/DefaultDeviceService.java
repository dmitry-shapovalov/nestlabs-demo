package com.nestlabs.demo.client.service;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.nestlabs.demo.client.model.Device;
import com.nestlabs.demo.client.model.DeviceType;
import com.nestlabs.demo.client.model.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link DeviceService} interface.
 *
 * @author Dmitry Shapovalov
 * @version 1.0 26.12.2015
 */
class DefaultDeviceService implements DeviceService {

    /**
     * The reference to Firebase object.
     */
    @SuppressWarnings("unused")
    private final JavaScriptObject firebaseRef;

    /**
     * The array of property descriptors that are common for all devices.
     */
    private static final PropertyDescriptor[] COMMON_PROPERTY_DESCRIPTORS;

    /**
     * The associative array that contains property descriptors specific to some device types.
     */
    private static final Map<DeviceType, PropertyDescriptor[]> PROPERTY_DESCRIPTOR_MAP = new HashMap<>();

    static {
        // Initialize common property descriptors.
        COMMON_PROPERTY_DESCRIPTORS = new PropertyDescriptor[] {
                new PropertyDescriptor("Name", "name", null),
                new PropertyDescriptor("Online", "is_online", null)
        };

        // Initialize thermostat's property descriptors.
        PROPERTY_DESCRIPTOR_MAP.put(
                DeviceType.THERMOSTAT,
                new PropertyDescriptor[] {
                        new PropertyDescriptor("Target temperature", "target_temperature_c", "°C"),
                        new PropertyDescriptor("High target temperature", "target_temperature_high_c", "°C"),
                        new PropertyDescriptor("Low target temperature", "target_temperature_low_c", "°C"),
                        new PropertyDescriptor("High away temperature", "away_temperature_high_c", "°C"),
                        new PropertyDescriptor("Low away temperature", "away_temperature_low_c", "°C"),
                        new PropertyDescriptor("Ambient temperature", "ambient_temperature_c", "°C"),
                        new PropertyDescriptor("State", "hvac_state", null),
                        new PropertyDescriptor("Humidity", "humidity", "%")
                }
        );

        // Initialize smoke alarm's property descriptors.
        PROPERTY_DESCRIPTOR_MAP.put(
                DeviceType.SMOKE_CO_ALARM,
                new PropertyDescriptor[] {
                        new PropertyDescriptor("Battery health", "battery_health", null),
                        new PropertyDescriptor("Smoke state", "smoke_alarm_state", null),
                        new PropertyDescriptor("CO state", "co_alarm_state", null)
                }
        );

        // Initialize camera's property descriptors.
        PROPERTY_DESCRIPTOR_MAP.put(
                DeviceType.CAMERA,
                new PropertyDescriptor[] {
                        new PropertyDescriptor("Streaming", "is_streaming", null)
                }
        );
    }

    @Inject
    public DefaultDeviceService(Firebase firebase) {
        this.firebaseRef = firebase.getReference();
    }

    @Override
    public void get(DeviceType deviceType, Callback<List<Device>> callback) {
        if (deviceType == null || callback == null) {
            throw new NullPointerException();
        }

        get(deviceType, getPath(deviceType), callback);
    }

    @Override
    public void addChangeListener(DeviceType deviceType, DeviceChangeListener listener) {
        if (deviceType == null || listener == null) {
            throw new NullPointerException();
        }

        addChangeListener(deviceType, getPath(deviceType), listener);
    }

    private native void get(DeviceType deviceType, String path, Callback<List<Device>> callback) /*-{
        var firebaseRef = this.@com.nestlabs.demo.client.service.DefaultDeviceService::firebaseRef;

        firebaseRef.child(path).once(
            'value',
            function (snapshot) {
                var result = @java.util.ArrayList::new()();
                for (var id in snapshot.val()) {
                    var item = snapshot.child(id);
                    var deviceObject = @com.nestlabs.demo.client.service.DefaultDeviceService.DeviceObject::new(Lcom/nestlabs/demo/client/model/DeviceType;Lcom/nestlabs/demo/client/service/SnapshotObject;)(deviceType, item);
                    result.@java.util.List::add(Ljava/lang/Object;)(deviceObject);
                }
                callback.@com.nestlabs.demo.client.service.Callback::onSuccess(Ljava/lang/Object;)(result);
            },
            function (error) {
                callback.@com.nestlabs.demo.client.service.Callback::onFailure(Ljava/lang/Throwable;)(
                    @java.lang.Exception::new(Ljava/lang/String;)(error)
                );
            }
        );
    }-*/;

    private native void addChangeListener(DeviceType deviceType, String path, DeviceChangeListener listener) /*-{
        var firebaseRef = this.@com.nestlabs.demo.client.service.DefaultDeviceService::firebaseRef;

        var changeListener = function (snapshot) {
            var deviceObject = @com.nestlabs.demo.client.service.DefaultDeviceService.DeviceObject::new(Lcom/nestlabs/demo/client/model/DeviceType;Lcom/nestlabs/demo/client/service/SnapshotObject;)(deviceType, snapshot);
            listener.@com.nestlabs.demo.client.service.DeviceChangeListener::onChange(Lcom/nestlabs/demo/client/model/Device;)(deviceObject);
        };

        // Subscribe for changes for all devices of the specified type.
        firebaseRef.child(path).once(
            'value',
            function (snapshot) {
                for (var id in snapshot.val()) {
                    snapshot.child(id).ref().on('value', changeListener);
                }
            }
        );
    }-*/;

    private String getPath(DeviceType deviceType) {
        return "devices/" + deviceType.name().toLowerCase() + "s";
    }

    /**
     * Contains metadata for device property.
     */
    private static final class PropertyDescriptor {

        private final String name;

        private final String key;

        private final String unit;

        public PropertyDescriptor(String name, String key, String unit) {
            this.name = name;
            this.key = key;
            this.unit = unit;
        }

    }

    /**
     * Implementation of {@link Device} interface that creates properties on the basis of
     * property descriptors.
     */
    private static final class DeviceObject implements Device {

        private final String id;

        private final DeviceType type;

        private final Property[] properties;

        public DeviceObject(DeviceType type, SnapshotObject snapshotObject) {
            this.id = snapshotObject.getProperty("device_id");
            this.type = type;
            this.properties = createProperties(type, snapshotObject);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public DeviceType getType() {
            return type;
        }

        @Override
        public Property[] getProperties() {
            return properties;
        }

        private Property[] createProperties(DeviceType type, SnapshotObject snapshotObject) {
            PropertyDescriptor[] propertyDescriptors = PROPERTY_DESCRIPTOR_MAP.get(type);

            Property[] properties = new Property[COMMON_PROPERTY_DESCRIPTORS.length + propertyDescriptors.length];

            // Create properties on the basis of common property descriptors.
            for (int i = 0; i < COMMON_PROPERTY_DESCRIPTORS.length; i++) {
                PropertyDescriptor propertyDescriptor = COMMON_PROPERTY_DESCRIPTORS[i];
                properties[i] = new Property(
                        propertyDescriptor.name,
                        snapshotObject.getProperty(propertyDescriptor.key),
                        propertyDescriptor.unit
                );
            }

            // Create properties on the basis of device specific property descriptors.
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                properties[COMMON_PROPERTY_DESCRIPTORS.length + i] = new Property(
                        propertyDescriptor.name,
                        snapshotObject.getProperty(propertyDescriptor.key),
                        propertyDescriptor.unit
                );
            }

            return properties;
        }

    }

}
