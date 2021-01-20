package com.camect.android.sdk.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class Camera {
    public static Camera inflate(@NonNull JSONObject jsonObj) throws JSONException {

        return new Camera()
                .setAlertDisabled(jsonObj.optBoolean(JSONKeys.IS_ALERT_DISABLED))
                .setDisabled(jsonObj.optBoolean(JSONKeys.DISABLED))
                .setHeight(jsonObj.optInt(JSONKeys.HEIGHT))
                .setId(jsonObj.getString(JSONKeys.ID))
                .setIpAddress(jsonObj.optString(JSONKeys.IP_ADDR))
                .setMacAddress(jsonObj.optString(JSONKeys.MAC_ADDR))
                .setMake(jsonObj.optString(JSONKeys.MAKE))
                .setModel(jsonObj.optString(JSONKeys.MODEL))
                .setName(jsonObj.optString(JSONKeys.NAME))
                .setOnWifi(jsonObj.optBoolean(JSONKeys.IS_ON_WIFI))
                .setStreaming(jsonObj.optBoolean(JSONKeys.IS_STREAMING))
                .setStreamingUrl(jsonObj.optString(JSONKeys.STREAMING_URL))
                .setUrl(jsonObj.optString(JSONKeys.URL))
                .setWidth(jsonObj.optInt(JSONKeys.WIDTH));
    }

    private boolean mAlertDisabled;
    private boolean mDisabled;
    private int     mHeight;
    private String  mId;
    private String  mIpAddress;
    private String  mMacAddress;
    private String  mMake;
    private String  mModel;
    private String  mName;
    private boolean mOnWifi;
    private boolean mStreaming;
    private String  mStreamingUrl;
    private String  mUrl;
    private int     mWidth;

    private Camera() {
        // prevent construction except through inflate method
    }

    public int getHeight() {
        return mHeight;
    }

    public String getId() {
        return mId;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getMake() {
        return mMake;
    }

    public String getModel() {
        return mModel;
    }

    public String getName() {
        return mName;
    }

    public String getStreamingUrl() {
        return mStreamingUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getWidth() {
        return mWidth;
    }

    public boolean isAlertDisabled() {
        return mAlertDisabled;
    }

    public boolean isDisabled() {
        return mDisabled;
    }

    public boolean isOnWifi() {
        return mOnWifi;
    }

    public boolean isStreaming() {
        return mStreaming;
    }

    Camera setAlertDisabled(boolean alertDisabled) {
        mAlertDisabled = alertDisabled;

        return this;
    }

    Camera setDisabled(boolean disabled) {
        mDisabled = disabled;

        return this;
    }

    Camera setHeight(int height) {
        mHeight = height;

        return this;
    }

    Camera setId(String id) {
        mId = id;

        return this;
    }

    Camera setIpAddress(String ipAddress) {
        mIpAddress = ipAddress;

        return this;
    }

    Camera setMacAddress(String macAddress) {
        mMacAddress = macAddress;

        return this;
    }

    Camera setMake(String make) {
        mMake = make;

        return this;
    }

    Camera setModel(String model) {
        mModel = model;

        return this;
    }

    Camera setName(String name) {
        mName = name;

        return this;
    }

    Camera setOnWifi(boolean onWifi) {
        mOnWifi = onWifi;

        return this;
    }

    Camera setStreaming(boolean streaming) {
        mStreaming = streaming;

        return this;
    }

    Camera setStreamingUrl(String streamingUrl) {
        mStreamingUrl = streamingUrl;

        return this;
    }

    Camera setUrl(String url) {
        mUrl = url;

        return this;
    }

    Camera setWidth(int width) {
        mWidth = width;

        return this;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.putOpt(JSONKeys.DISABLED, mDisabled)
                    .putOpt(JSONKeys.HEIGHT, mHeight)
                    .putOpt(JSONKeys.ID, mId)
                    .putOpt(JSONKeys.IP_ADDR, mIpAddress)
                    .putOpt(JSONKeys.IS_ALERT_DISABLED, mAlertDisabled)
                    .putOpt(JSONKeys.IS_ON_WIFI, mOnWifi)
                    .putOpt(JSONKeys.IS_STREAMING, mStreaming)
                    .putOpt(JSONKeys.MAC_ADDR, mMacAddress)
                    .putOpt(JSONKeys.MAKE, mMake)
                    .putOpt(JSONKeys.MODEL, mModel)
                    .putOpt(JSONKeys.NAME, mName)
                    .putOpt(JSONKeys.STREAMING_URL, mStreamingUrl)
                    .putOpt(JSONKeys.URL, mUrl)
                    .putOpt(JSONKeys.WIDTH, mWidth);

            return jsonObj.toString(2);
        } catch (JSONException ex) {
            ex.printStackTrace();

            return super.toString();
        }
    }

    public interface JSONKeys {
        String DISABLED          = "disabled";
        String HEIGHT            = "height";
        String ID                = "id";
        String IP_ADDR           = "ip_addr";
        String IS_ALERT_DISABLED = "is_alert_disabled";
        String IS_ON_WIFI        = "is_on_wifi";
        String IS_STREAMING      = "is_streaming";
        String MAC_ADDR          = "mac_addr";
        String MAKE              = "make";
        String MODEL             = "model";
        String NAME              = "name";
        String STREAMING_URL     = "streaming_url";
        String URL               = "url";
        String WIDTH             = "width";
    }
}
