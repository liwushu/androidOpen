package com.tcl.update.logstatistic;

import com.tcl.framework.db.annotation.Column;
import com.tcl.framework.db.annotation.GenerationType;
import com.tcl.framework.db.annotation.Id;
import com.tcl.framework.db.annotation.Table;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fanyang.sz on 2016/11/30.
 */

@Table(version = 1)
public class LogStatisticsItem implements Serializable {


    @Id(strategy = GenerationType.AUTO_INCREMENT)
    private String itemId;

    @Column
    private int ev;

    @Column
    private long time;

    @Column
    private LogStatisticsData data;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getEv() {
        return ev;
    }

    public void setEv(int ev) {
        this.ev = ev;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LogStatisticsData getData() {
        return data;
    }

    public void setData(LogStatisticsData data) {
        this.data = data;
    }

    public static String toJSON(List<LogStatisticsItem> mItems) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (LogStatisticsItem item : mItems) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ev", item.getEv());
                jsonObject.put("time", item.getTime());
                if (item.getData() != null) {
                    jsonObject.put("data", item.getData().toJSON());
                }
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }
}
