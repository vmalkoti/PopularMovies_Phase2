package com.example.malkoti.popularmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    private ApiClient() {}

    private static Retrofit getRetrofitClient() {
        // need to add interceptors

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(MovieApiRetrofitInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static MovieApiRetrofitInterface getApiInterface() {
        return getRetrofitClient().create(MovieApiRetrofitInterface.class);
    }
}
