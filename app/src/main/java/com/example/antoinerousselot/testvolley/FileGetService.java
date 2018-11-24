package com.example.antoinerousselot.testvolley;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FileGetService {
    @GET("infosImage")
    Call<ResponseBody> getImageDetails();
}
