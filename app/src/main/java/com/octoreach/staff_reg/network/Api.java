package com.octoreach.staff_reg.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    //private static final String DOMAIN_HOST = "http://192.168.5.96/";
    public static String DOMAIN_HOST_PORT = "8001";

    private static String DOMAIN_HOST = "http://192.168.3.186:8001/";
//    private static String DOMAIN_HOST = "http://192.168.3.180:5000/";
//    private static String DOMAIN_HOST = "http://172.22.249.136:8002/";
//    private static final String DOMAIN_HOST = "http://192.168.3.81:8082/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    //    private static Gson gson = new GsonBuilder().setLenient().create();
    public static void setDomainHost(String domainHost) {
        DOMAIN_HOST = "http://" + domainHost + "/";
    }

    public static DomainApi getDomainApi() {
        return new Retrofit.Builder()
                .baseUrl(DOMAIN_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
                .create(DomainApi.class);
    }
}

