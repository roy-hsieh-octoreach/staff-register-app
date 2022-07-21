package com.octoreach.poct.ascensia.mapper.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static final String DOMAIN_HOST = "http://192.168.5.96:1323/";
//    private static final String DOMAIN_HOST = "http://172.22.249.136:1323/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static DomainApi getDomainApi() {
        return new Retrofit.Builder()
                .baseUrl(DOMAIN_HOST)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
                .create(DomainApi.class);
    }

}
