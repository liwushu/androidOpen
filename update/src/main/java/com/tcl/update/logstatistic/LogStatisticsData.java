package com.tcl.update.logstatistic;

import android.text.TextUtils;

import com.tcl.update.TipsInfo;
import com.tcl.update.db.UpdateInfo;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by fanyang.sz on 2016/11/30.
 */

public class LogStatisticsData implements Serializable {

    private int apkId;
    private int operType;
    private int operResult;
    private String message;
    private int errorCode;

    private int id;
    private String pkgName;
    private int vcode;
    private String vname;
    private String branch;
    private String country;
    private String imei;
    private String model;



    public int getApkId() {
        return apkId;
    }

    public void setApkId(int apkId) {
        this.apkId = apkId;
    }

    public int getOperType() {
        return operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
    }

    public int getOperResult() {
        return operResult;
    }

    public void setOperResult(int operResult) {
        this.operResult = operResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (apkId != 0) {
                jsonObject.put("apkId", apkId);
            }
            if (operType != 0) {
                jsonObject.put("operType", getOperType());
            }

            jsonObject.put("operResult", getOperResult());

            if (errorCode != 0) {
                jsonObject.put("errorCode", errorCode);
            }

            if (!TextUtils.isEmpty(getMessage())) {
                jsonObject.put("message", getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getVcode() {
        return vcode;
    }

    public void setVcode(int vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void initLogData(UpdateInfo updateInfo) {
        if (updateInfo == null) {
            return;
        }

        id = updateInfo.getId();
        pkgName = updateInfo.getPkgName();
        vcode = updateInfo.getVcode();
        vname = updateInfo.getVname();
        branch = updateInfo.getBranch();
        country = updateInfo.getCountry();
        imei = updateInfo.getImei();
        model = updateInfo.getModel();
    }

    public void initLogData(TipsInfo updateInfo) {
        if (updateInfo == null) {
            return;
        }
        id = updateInfo.getId();
        pkgName = updateInfo.getPkgName();
        vcode = updateInfo.getVcode();
        vname = updateInfo.getVname();
        branch = updateInfo.getBranch();
        country = updateInfo.getCountry();
        imei = updateInfo.getImei();
    }
}
