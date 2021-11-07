package com.example.autohelm1000;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Autohelm1000Api {
    @GET("/setRelativeBearing")
    Call<ResponseBody> setRelativeBearing(@Query("bearing") int bearing);
}
