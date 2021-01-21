package com.camect.android.sdk.model;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class HomeInfo {

    public static HomeInfo inflate(@NonNull JSONObject jsonObj) throws JSONException {
        ArrayList<String> names = new ArrayList<>();

        JSONArray objectNames = jsonObj.optJSONArray(JSONKeys.OBJECT_NAME);
        if (objectNames != null) {
            for (int i = 0; i < objectNames.length(); i++) {
                names.add(objectNames.optString(i));
            }
        }

        return new HomeInfo()
                .setCloudUrl(jsonObj.optString(JSONKeys.CLOUD_URL))
                .setId(jsonObj.getString(JSONKeys.ID))
                .setLocalHttpsUrl(jsonObj.optString(JSONKeys.LOCAL_HTTPS_URL))
                .setMode(jsonObj.optString(JSONKeys.MODE))
                .setName(jsonObj.optString(JSONKeys.NAME))
                .setObjectNames(names);
    }

    private String            mCloudUrl;
    private String            mId;
    private String            mLocalHttpsUrl;
    private String            mMode;
    private String            mName;
    private ArrayList<String> mObjectNames;

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

    HomeInfo setCloudUrl(String cloudUrl) {
        mCloudUrl = cloudUrl;

        return this;
    }

    HomeInfo setId(String id) {
        mId = id;

        return this;
    }

    HomeInfo setLocalHttpsUrl(String localHttpsUrl) {
        mLocalHttpsUrl = localHttpsUrl;

        return this;
    }

    public HomeInfo setMode(String mode) {
        mMode = mode;

        return this;
    }

    public HomeInfo setName(String name) {
        mName = name;

        return this;
    }

    HomeInfo setObjectNames(ArrayList<String> objectNames) {
        mObjectNames = objectNames;

        return this;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObj = new JSONObject();
        JSONArray objectNames = new JSONArray();

        try {
            jsonObj.putOpt(JSONKeys.CLOUD_URL, mCloudUrl)
                    .putOpt(JSONKeys.ID, mId)
                    .putOpt(JSONKeys.LOCAL_HTTPS_URL, mLocalHttpsUrl)
                    .putOpt(JSONKeys.MODE, mMode)
                    .putOpt(JSONKeys.NAME, mName)
                    .putOpt(JSONKeys.OBJECT_NAME, objectNames);

            if (mObjectNames != null && mObjectNames.size() > 0) {
                for (String object : mObjectNames) {
                    objectNames.put(object);
                }
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
