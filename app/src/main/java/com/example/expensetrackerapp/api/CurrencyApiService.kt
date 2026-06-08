package com.example.expensetrackerapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {

    @GET("v6/{apiKey}/latest/{base}")
    fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): Call<CurrencyResponse>
}