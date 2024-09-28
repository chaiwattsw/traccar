package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("box_devices")
public class BoxDevice {

    private long id;
    private String deviceName;
    private String protocol;
    private String port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "BoxDevice{" +
                "id=" + id +
                ", deviceName='" + deviceName + '\'' +
                ", protocol='" + protocol + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}