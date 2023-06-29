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

    @Headers("Content-Type: application/json")
    @POST("user/pet/add")
    fun addPet(@Body jsonObject: JsonObject) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/pets")
    fun getPets() : Call<List<Pet>>

    @Headers("Content-Type: application/json")
    @GET("{dogurl}/breeds")
    fun dogBreeds(
        @Path(value = "dogurl", encoded = true) dogurl : String,
        @Query("api_key") apiKey: String
    ) : Call<List<DogBreed>>

    @Headers("Content-Type: application/json")
    @GET("{caturl}/breeds")
    fun catBreeds(
        @Path(value = "caturl", encoded = true) caturl : String,
        @Query("api_key") apiKey: String
    ) : Call<List<CatBreed>>



    class Pet {

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("specie")
        var specie: String? = null

        @SerializedName("breed")
        var breed: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("dateOfBirth")
        var dateOfBirth: String? = null

        @SerializedName("photo")
        var photo: String? = null

        @SerializedName("vaccinated")
        var vaccinated: Boolean = false

        @SerializedName("friendly")
        var friendly: Boolean = false

        @SerializedName("microchip")
        var microchip: Boolean = false

    }

    class DogBreed {

        @SerializedName("id")
        var id: Int = 0

        @SerializedName("name")
        var name: String? = null

        override fun toString(): String {
            return name ?: ""
        }

    }

    class CatBreed {

        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        override fun toString(): String {
            return name ?: ""
        }

    }

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