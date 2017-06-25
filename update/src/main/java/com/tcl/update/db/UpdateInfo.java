package com.tcl.update.db;

import com.tcl.framework.db.annotation.Column;
import com.tcl.framework.db.annotation.Id;
import com.tcl.framework.db.annotation.Table;
import com.tcl.update.context.ACContext;
import com.tcl.update.context.DirType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by yancai.liu on 2016/10/8.
 */

@Table(version = 2)
public class UpdateInfo implements Serializable {
    public static final int STATUS_NOT_DOWNLOAD = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECT_ERROR = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_DOWNLOAD_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;
    public static final int STATUS_INSTALLED = 7;

    @Id
    private int id;
    @Column
    private String name;
    @Column
    private String pkgName;
    @Column
    private String url;
    @Column
    private int vcode;
    @Column
    private String vname;
    @Column
    private String branch;
    @Column
    private int size;
    @Column
    private String country;
    @Column
    private String imei;
    @Column
    private String model;
    @Column
    private boolean needInstall;

    private int progress;
    private String downloadPerSize;
    private int status;

    public UpdateInfo() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadPerSize() {
        return downloadPerSize;
    }

    public void setDownloadPerSize(String downloadPerSize) {
        this.downloadPerSize = downloadPerSize;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public int getVcode() {
        return vcode;
    }

    public void setVcode(int vcode) {
        this.vcode = vcode;
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

    /**
     * 如果本地没有安装此应用，也会安装，默认不安装。
     *
     * @return
     */
    public boolean isNeedInstall() {
        return needInstall;
    }

    public void setNeedInstall(boolean needInstall) {
        this.needInstall = needInstall;
    }

    public String getStatusText() {
        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Not Download";
            case STATUS_CONNECTING:
                return "Connecting";
            case STATUS_CONNECT_ERROR:
                return "Connect Error";
            case STATUS_DOWNLOADING:
                return "Downloading";
            case STATUS_PAUSED:
                return "Pause";
            case STATUS_DOWNLOAD_ERROR:
                return "Download Error";
            case STATUS_COMPLETE:
                return "Complete";
            case STATUS_INSTALLED:
                return "Installed";
            default:
                return "Not Download";
        }
    }

    public String getButtonText() {
        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Download";
            case STATUS_CONNECTING:
                return "Cancel";
            case STATUS_CONNECT_ERROR:
                return "Try Again";
            case STATUS_DOWNLOADING:
                return "Pause";
            case STATUS_PAUSED:
                return "Resume";
            case STATUS_DOWNLOAD_ERROR:
                return "Try Again";
            case STATUS_COMPLETE:
                return "Install";
            case STATUS_INSTALLED:
                return "UnInstall";
            default:
                return "Download";
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static String urlToName(String url) {
        return url.substring(url.lastIndexOf(File.separator) + 1);
    }

    public String getPath() {
        return ACContext.getDirectoryPath(DirType.apps) + File.separator + urlToName(url);
    }


    public static UpdateInfo fromJSONObject(JSONObject data) throws JSONException {
        UpdateInfo info = new UpdateInfo();
        info.id = data.getInt("id");
        info.url = data.getString("url");
        info.name = data.getString("name");
        info.vcode = data.getInt("vcode");
        info.pkgName = data.getString("pkgName");
        info.vname = data.getString("vname");
        info.branch = data.getString("branch");
        info.size = data.getInt("size");
        info.needInstall = data.getBoolean("needInstall");
        if (data.has("country")) {
            info.country = data.getString("country");
        }
        if (data.has("imei")) {
            info.imei = data.getString("imei");
        }
        if (data.has("model")) {
            info.imei = data.getString("model");
        }
        return info;
    }


}
