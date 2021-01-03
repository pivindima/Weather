package ru.weather.model.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("locations/v1/cities/autocomplete?apikey=$API_KEY&language=$LANGUAGE")
    fun searchCity(@Query("q") query: String): Observable<List<City>>

    @GET("/currentconditions/v1/{key}?apikey=$API_KEY&language=$LANGUAGE")
    fun currentConditions(@Path("key") key: String): Observable<List<CurrentConditions>>

    @GET("/forecasts/v1/daily/5day/{key}?apikey=$API_KEY&language=$LANGUAGE&details=true&metric=true")
    fun requestForecasts(@Path("key") key: String): Observable<Daily>

    companion object {

//        const val API_KEY = "gghDke5YI5miaZAjml4ZJUUqStRb5A2A"
//        const val API_KEY = "RucVhReS4kawpcAsdnRzVlLAIAbbI3GA"
//        const val API_KEY = "HEwXqX2yAL55HNzyHRgVvlxaNt9qv1bp"
//        const val API_KEY = "VtP0b17v2sxOGetZzAHGC9QdYytK1MRg"
//        const val API_KEY = "YoF640hicaDxU4Cjw9BZmWwEVFli86dA"
//        const val API_KEY = "pzcmHYDqAUfGqCQGINFiaDWwzjNile3K"
//        const val API_KEY = "mGGhzPYYGWj53MG0hDHjk55Xo4AqoI15"
//        const val API_KEY = "VUEkZ3MGqBsU46surctGn4VF0v21zpaB"
//        const val API_KEY = "t337Gn4WrQ5f9maGa0WCtEg6BjCh8Azy"
        const val API_KEY = "MdoGkXzmJGpGsOTlLWJWPl2YjpWi6ojx"
        const val LANGUAGE = "ru-ru"

        fun create(): ApiService {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://dataservice.accuweather.com/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}