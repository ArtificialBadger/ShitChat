package com.ianofferdahl.shitchat.DataModels;

/**
 * Created by Isaac on 5/8/2015.
 */
public class DeviceRequest {

    private String DeviceId;

    public DeviceRequest() {}

    public DeviceRequest(String deviceId) {
        this.DeviceId = deviceId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }
}
