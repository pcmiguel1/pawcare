package com.pawcare.pawcare.services

import android.os.Parcel
import android.os.Parcelable
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

    @Multipart
    @POST("user/pet/add")
    fun addPet(
        @Part("pet") user: RequestBody,
        @Part file: MultipartBody.Part?
    ) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("sitter/application/start")
    fun startApplication() : Call<Void>

    @Headers("Content-Type: application/json")
    @GET("user/pets")
    fun getPets() : Call<List<Pet>>

    @Headers("Content-Type: application/json")
    @GET("sitter/{id}")
    fun getSitter(
        @Path(value = "id", encoded = true) id : String,
    ) : Call<Sitter>

    @Headers("Content-Type: application/json")
    @POST("user/pet/update/{id}")
    fun updatePet(
        @Path(value = "id", encoded = true) id : String,
        @Body jsonObject: JsonObject
    ) : Call<Void>

    @Multipart
    @POST("user/update")
    fun updateProfile(
        @Part("user") user: RequestBody,
        @Part file: MultipartBody.Part?
    ) : Call<User>

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


    class Sitter {

        @SerializedName("_id")
        var sitterId: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("verified")
        var verified: Boolean? = false

    }

    class Pet() : Parcelable {

        @SerializedName("_id")
        var id: String? = null

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

        constructor(parcel: Parcel) : this() {
            userId = parcel.readString()
            name = parcel.readString()
            specie = parcel.readString()
            breed = parcel.readString()
            gender = parcel.readString()
            dateOfBirth = parcel.readString()
            photo = parcel.readString()
            vaccinated = parcel.readByte() != 0.toByte()
            friendly = parcel.readByte() != 0.toByte()
            microchip = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(userId)
            parcel.writeString(name)
            parcel.writeString(specie)
            parcel.writeString(breed)
            parcel.writeString(gender)
            parcel.writeString(dateOfBirth)
            parcel.writeString(photo)
            parcel.writeByte(if (vaccinated) 1 else 0)
            parcel.writeByte(if (friendly) 1 else 0)
            parcel.writeByte(if (microchip) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Pet> {
            override fun createFromParcel(parcel: Parcel): Pet {
                return Pet(parcel)
            }

            override fun newArray(size: Int): Array<Pet?> {
                return arrayOfNulls(size)
            }
        }

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