package com.tcl.update.db;

import com.tcl.framework.db.annotation.Column;
import com.tcl.framework.db.annotation.GenerationType;
import com.tcl.framework.db.annotation.Id;
import com.tcl.framework.db.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by yancai.liu on 2016/10/17.
 */
@Table(version = 1)
public class PackageNames implements Serializable {

    @Id(strategy = GenerationType.AUTO_INCREMENT)
    private int id;

    @Column
    private int pkgVersion;

    @Column
    private String[] pkgNames;

    public int getPkgVersion() {
        return pkgVersion;
    }

    public void setPkgVersion(int pkgVersion) {
        this.pkgVersion = pkgVersion;
    }

    public String[] getPkgNames() {
        return pkgNames;
    }

    public void setPkgNames(String[] pkgNames) {
        this.pkgNames = pkgNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static PackageNames fromJSONObject(JSONObject data) throws JSONException {
        PackageNames info = new PackageNames();
        info.pkgVersion = data.getInt("pkgVersion");
        JSONArray jsonArray = data.getJSONArray("pkgNames");
        info.pkgNames = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            info.pkgNames[i] = jsonArray.getString(i);
        }
        return info;
    }

    @Override
    public String toString() {
        return "pkgVersion:" + pkgVersion + "," + pkgNames;
    }

    public String toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("pkgVersion", getPkgVersion());
            String names = "";
            for (int i = 0; i < getPkgNames().length; i++) {
                if (0 == i) {
                    names += "[";
                    names += getPkgNames()[i] + ",";
                } else if (getPkgNames().length - 1 == i) {
                    names += getPkgNames()[i];
                    names += "]";
                } else {
                    names += getPkgNames()[i] + ",";
                }
            }
            object.put("pkgNames", names);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
