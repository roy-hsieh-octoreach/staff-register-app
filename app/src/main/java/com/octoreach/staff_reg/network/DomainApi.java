package com.octoreach.staff_reg.network;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DomainApi {
    @Headers("Content-Type: application/json")
    @POST("api/uploadNFCCard/")
    Observable<ResponseBody> uploadNFCId(@Body RequestBody requestBody);
}
