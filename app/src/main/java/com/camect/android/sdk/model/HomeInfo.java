package com.camect.android.sdk.model;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class HomeInfo {

    public static HomeInfo inflate(@NonNull String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);

        HomeInfo home = new HomeInfo();

        home.setCloudUrl(jsonObj.optString(JSONKeys.CLOUD_URL));
        home.setId(jsonObj.optString(JSONKeys.ID));
        home.setLocalHttpsUrl(jsonObj.optString(JSONKeys.LOCAL_HTTPS_URL));
        home.setMode(jsonObj.optString(JSONKeys.MODE));
        home.setName(jsonObj.optString(JSONKeys.NAME));

        JSONArray objectNames = jsonObj.optJSONArray(JSONKeys.OBJECT_NAME);
        if (objectNames != null) {
            ArrayList<String> names = home.getObjectNames();
            for (int i = 0; i < objectNames.length(); i++) {
                names.add(objectNames.optString(i));
            }
        }

        return home;
    }

    private final ArrayList<String> mObjectNames = new ArrayList<>();

    private String mCloudUrl;
    private String mId;
    private String mLocalHttpsUrl;
    private String mMode;
    private String mName;

    private HomeInfo() {
        // prevent construction except through inflate method
    }

    public String getCloudUrl() {
        return mCloudUrl;
    }

    public String getId() {
        return mId;
    }

    public String getLocalHttpsUrl() {
        return mLocalHttpsUrl;
    }

    public String getMode() {
        return mMode;
    }

    public String getName() {
        return mName;
    }

    @NonNull
    public ArrayList<String> getObjectNames() {
        return mObjectNames;
    }

    public void setCloudUrl(String cloudUrl) {
        mCloudUrl = cloudUrl;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setLocalHttpsUrl(String localHttpsUrl) {
        mLocalHttpsUrl = localHttpsUrl;
    }

    public void setMode(String mode) {
        mMode = mode;
    }

    public void setName(String name) {
        mName = name;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.putOpt(JSONKeys.CLOUD_URL, mCloudUrl);
            jsonObj.putOpt(JSONKeys.ID, mId);
            jsonObj.putOpt(JSONKeys.LOCAL_HTTPS_URL, mLocalHttpsUrl);
            jsonObj.putOpt(JSONKeys.MODE, mMode);
            jsonObj.putOpt(JSONKeys.NAME, mName);

            JSONArray objectNames = new JSONArray();

            jsonObj.putOpt(JSONKeys.OBJECT_NAME, objectNames);

            for (String object : mObjectNames) {
                objectNames.put(object);
            }

            return jsonObj.toString(2);
        } catch (JSONException ex) {
            ex.printStackTrace();

            return super.toString();
        }
    }

    public interface JSONKeys {
        String CLOUD_URL       = "cloud_url";
        String ID              = "id";
        String LOCAL_HTTPS_URL = "local_https_url";
        String MODE            = "mode";
        String NAME            = "name";
        String OBJECT_NAME     = "object_name";
    }
}
