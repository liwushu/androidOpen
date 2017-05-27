package com.flying.test.http;

import com.flying.test.bean.HttpBean;
import com.flying.test.bean.NewsList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {

    @GET("/api/open/recommend/news")
    Call<HttpBean<NewsList>> loadRecommendNews(@Query("queryType") int queryType, @Query("pageSize") int pageSize, @Query("types") String types);

    @GET
    Call<String> loadHttp();
}
