package dev.gtcl.eulerityproject.service;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import dev.gtcl.eulerityproject.models.ImageURL;
import dev.gtcl.eulerityproject.models.Status;
import dev.gtcl.eulerityproject.models.UploadURL;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface EulerityAPI {
    String URL = "http://eulerity-hackathon.appspot.com";

    @GET("/image")
    Call<List<ImageURL>> getImages();

    @GET("/upload")
    Call<UploadURL> getUploadUrl();

    @Multipart
    @POST
    Call<Status> uploadImage(
            @Url String uploadURL,
            @PartMap Map<String, RequestBody> map);

    static EulerityAPI create(){
        Gson gson = new Gson();

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor((message) -> Log.d("Eulerity", message));
        logger.level(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(EulerityAPI.class);
    }
}
