package com.octoreach.poct.ascensia.mapper.network;

import com.octoreach.poct.ascensia.mapper.to.GlucoseMapperTo;
import com.octoreach.poct.ascensia.mapper.to.StaffTo;
import com.octoreach.poct.ascensia.mapper.to.VersionTo;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DomainApi {

    @POST("api/mapper")
    Single<GlucoseMapperTo> mapper(@Body GlucoseMapperTo deviceTo);

    @GET("api/staff/{staffId}")
    Single<StaffTo> findStaffById(@Path("staffId") String staffId);

    @GET("api/staff/{barcode}/name")
    Single<String> findStaffName(@Path("barcode") String barcode);

    @GET("api/patient/{barcode}/name")
    Single<String> findPatientName(@Path("barcode") String barcode);

    @GET("assets/version.json")
    Single<VersionTo> getVersion();

}
