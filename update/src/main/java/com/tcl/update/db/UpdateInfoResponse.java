package com.tcl.update.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yancai.liu on 2016/10/8.
 */

public class UpdateInfoResponse {

    private int pkgVersion;

    private long time;

    private String country;

    private List<UpdateInfo> data;

    public List<UpdateInfo> getData() {
        return data;
    }

    public void setData(List<UpdateInfo> data) {
        this.data = data;
    }

    public int getPkgVersion() {
        return pkgVersion;
    }

    public void setPkgVersion(int pkgVersion) {
        this.pkgVersion = pkgVersion;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static UpdateInfoResponse fromJSONObject(JSONObject data) throws JSONException {
        UpdateInfoResponse baseResponse = new UpdateInfoResponse();
        baseResponse.setPkgVersion(data.getInt("pkgVersion"));

        if (data.has("apks")) {
            JSONArray jsonArray = data.getJSONArray("apks");
            List<UpdateInfo> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(UpdateInfo.fromJSONObject(jsonArray.getJSONObject(i)));
            }
            baseResponse.setData(list);
        }

        if (data.has("time")) {
            baseResponse.setTime(data.getLong("time"));
        }
        if (data.has("country")) {
            baseResponse.setCountry(data.getString("country"));
        }
        return baseResponse;
    }

}
