package com.pawcare.pawcare.services

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("auth/services")
    fun getServices() : Call<Void>

    @Multipart
    @POST("auth/register")
    fun registerUser(@Part("user") user: RequestBody, @Part file: MultipartBody.Part?) : Call<User>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun loginUser(@Body jsonObject: JsonObject) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("auth/sendVerificationEmailForgotPassword")
    fun sendVerificationEmailForgotPassword(@Body jsonObject: JsonObject) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("auth/verifyForgotPasswordCode/{userId}/{code}")
    fun verifyForgotPasswordCode(@Path("userId") userId: String, @Path("code") code: String): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("auth/resetPassword")
    fun resetPassword(@Body jsonObject: JsonObject) : Call<JsonObject>

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