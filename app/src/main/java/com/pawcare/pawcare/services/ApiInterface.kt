package com.pawcare.pawcare.services

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @GET("auth/services")
    fun getServices() : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    fun registerUser(@Body jsonObject: JsonObject) : Call<User>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun loginUser(@Body jsonObject: JsonObject) : Call<JsonObject>

    class User {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("fullname")
        var fullname: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("password")
        var password: String? = null

        @SerializedName("phoneNumber")
        var phoneNumber: String? = null

        @SerializedName("dateOfBirth")
        var dateOfBirth: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("verified")
        var verified: Boolean? = null

    }

}