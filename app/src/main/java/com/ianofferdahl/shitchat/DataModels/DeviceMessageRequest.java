package com.ianofferdahl.shitchat.DataModels;

/**
 * Created by Isaac on 5/8/2015.
 */
public class DeviceMessageRequest {

    private String DeviceId;

    private String Message;

    public DeviceMessageRequest() {}

    public DeviceMessageRequest(String deviceId, String message) {
        DeviceId = deviceId;
        Message = message;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
