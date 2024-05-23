package com.example.reservationapp.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NaverMapRequest {
    // Base URL
    public static String BASE_URL = "https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/";

    private static Retrofit retrofit;
    public static Retrofit getClient(){

        System.out.println("BASE_URL: " + BASE_URL);

        Gson gson = new GsonBuilder().setLenient().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor); // 로깅 인터셉터 추가

        if(retrofit == null){
            retrofit = new Retrofit.Builder() // retrofit 객체 생성
                    .baseUrl(BASE_URL) // BASE_URL로 통신
                    .addConverterFactory(GsonConverterFactory.create(gson)) // setLenient(true) 추가
                    .client(httpClient.build()) // OkHttpClient 설정
                    .build();
        }

        String completeUrl = retrofit.baseUrl().toString();
        System.out.println("Complete URL: " + completeUrl);
        return retrofit;
    }

}
