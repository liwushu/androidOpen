package com.flying.test.bean;

import java.util.List;

public class NewsList {
    public String categoryCode;
    public List<News> newsList;
    public News topNews;

    @Override
    public String toString() {
        return "NewsList{" + "categoryCode='" + categoryCode + '\'' + ", newsList=" + newsList + ", topNews=" + topNews
                + '}';
    }


}
