package com.pawcare.pawcare.services

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.Date

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("notification/postToken")
    fun postNotificationToken(@Body jsonObject: JsonObject) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("notification/send")
    fun sendNotification(@Body jsonObject: JsonObject) : Call<Notification>

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
    @GET("auth/verify/{userId}/{code}")
    fun verifyCodeEmail(@Path("userId") userId: String, @Path("code") code: String): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("auth/resend/{userId}")
    fun resendCodeEmail(@Path("userId") userId: String): Call<Void>

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
    @GET("sitter")
    fun getSitter() : Call<Sitter>

    @Headers("Content-Type: application/json")
    @GET("sitter/{id}")
    fun getSitterById(
        @Path(value = "id", encoded = true) id : String,
    ) : Call<Sitter>

    @Headers("Content-Type: application/json")
    @GET("user/{id}")
    fun getUserById(
        @Path(value = "id", encoded = true) id : String,
    ) : Call<User>

    @Multipart
    @POST("user/pet/update/{id}")
    fun updatePet(
        @Path(value = "id", encoded = true) id : String,
        @Part("pet") user: RequestBody,
        @Part file: MultipartBody.Part?
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

    @Headers("Content-Type: application/json")
    @GET("sitter/pictures/{id}")
    fun getPicturesSitter(
        @Path(value = "id", encoded = true) id : String,
    ) : Call<List<Picture>>

    @Headers("Content-Type: application/json")
    @GET("sitter/pictures")
    fun getPictures() : Call<List<Picture>>

    @Headers("Content-Type: application/json")
    @GET("sitter/list")
    fun getSitters(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("service") service: List<String>
    ) : Call<List<Sitter>>

    @Headers("Content-Type: application/json")
    @POST("sitter/picture/delete/{filename}")
    fun deletePicture(@Path(value = "filename", encoded = true) filename : String) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("sitter/application/submit")
    fun applicationSubmit() : Call<Void>

    @Multipart
    @POST("sitter/picture/add")
    fun addPicture(
        @Part file: MultipartBody.Part?
    ) : Call<Picture>

    @Headers("Content-Type: application/json")
    @POST("sitter/phone/sendVerification/{phoneNumber}")
    fun sendPhoneVerification(
        @Path(value = "phoneNumber", encoded = true) phoneNumber : String
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("sitter/phone/verify")
    fun verifyPhone(
        @Body jsonObject: JsonObject
    ) : Call<Void>

    @Multipart
    @POST("sitter/update")
    fun updateSitter(
        @Part("sitter") user: RequestBody,
        @Part file: MultipartBody.Part?
    ) : Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("sitter/application")
    fun getApplication() : Call<ApplicationSitter>

    @Headers("Content-Type: application/json")
    @GET("user/favourites")
    fun getFavourites() : Call<List<Sitter>>

    @Headers("Content-Type: application/json")
    @POST("user/favourite/add/{id}")
    fun addFavourite(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("user/favourite/delete/{id}")
    fun deleteFavourite(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @GET("user/favourite/{id}")
    fun getFavourite(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Favourite>

    @Headers("Content-Type: application/json")
    @POST("user/booking/add")
    fun addBooking(
        @Body jsonObject: JsonObject
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @GET("user/bookings/active")
    fun getBookingsActive() : Call<List<Booking>>

    @Headers("Content-Type: application/json")
    @GET("user/bookings/completed")
    fun getBookingsCompleted() : Call<List<Booking>>

    @Headers("Content-Type: application/json")
    @GET("sitter/bookings")
    fun getBookings(
        @Query("month") month: Int,
        @Query("year") year: Int,
    ) : Call<List<Booking>>

    @Headers("Content-Type: application/json")
    @POST("sitter/booking/update/{id}")
    fun updateStateBooking(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Booking>

    @Headers("Content-Type: application/json")
    @POST("user/booking/cancel/{id}")
    fun cancelBooking(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @POST("user/contacts/add/{id}")
    fun addContact(
        @Path(value = "id", encoded = true) id : String
    ) : Call<Void>

    @Headers("Content-Type: application/json")
    @GET("user/contacts")
    fun listContacts() : Call<List<Contact>>

    @Headers("Content-Type: application/json")
    @GET("sitter/contacts")
    fun listContactsSitter() : Call<List<Contact>>

    @Headers("Content-Type: application/json")
    @POST("user/chat/send")
    fun sendMessage(
        @Body jsonObject: JsonObject
    ) : Call<Message>

    @Headers("Content-Type: application/json")
    @GET("user/chat/messages/{id}")
    fun chatMessages(
        @Path(value = "id", encoded = true) id : String
    ) : Call<List<Message>>

    @Headers("Content-Type: application/json")
    @POST("sitter/chat/send")
    fun sendMessageSitter(
        @Body jsonObject: JsonObject
    ) : Call<Message>

    @Headers("Content-Type: application/json")
    @GET("sitter/chat/messages/{id}")
    fun chatMessagesSitter(
        @Path(value = "id", encoded = true) id : String
    ) : Call<List<Message>>

    @Headers("Content-Type: application/json")
    @GET("sitter/{id}/reviews")
    fun getReviews(
        @Path(value = "id", encoded = true) id : String
    ) : Call<List<Review>>


    @Headers("Content-Type: application/json")
    @POST("sitter/{id}/reviews/add")
    fun addReview(
        @Path(value = "id", encoded = true) id : String,
        @Body jsonObject: JsonObject
    ) : Call<Review>

    @Headers("Content-Type: application/json")
    @POST("user/delete")
    fun deleteUser() : Call<Void>

    @Headers("Content-Type: application/json")
    @GET("sitter/income")
    fun income() : Call<Income>

    class Notification {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("userId")
        var userId: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("body")
        var body: String? = null

    }

    class Income {

        @SerializedName("totalWalking")
        var totalWalking: String? = null

        @SerializedName("totalBoarding")
        var totalBoarding: String? = null

        @SerializedName("totalHouseSitting")
        var totalHouseSitting: String? = null

        @SerializedName("totalTraining")
        var totalTraining: String? = null

        @SerializedName("totalGrooming")
        var totalGrooming: String? = null

        @SerializedName("active")
        var active: String? = null

        @SerializedName("finished")
        var finished: String? = null

        @SerializedName("canceled")
        var canceled: String? = null

        @SerializedName("total")
        var total: String? = null

    }

    class Review {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userdId: String? = null

        @SerializedName("sitterId")
        var sitterId: String? = null

        @SerializedName("bookingId")
        var bookingId: String? = null

        @SerializedName("rate")
        var rate: String? = null

        @SerializedName("message")
        var message: String? = null

        @SerializedName("createdat")
        var createdat: Date? = null

        @SerializedName("user")
        var user: User? = null

    }

    class Message {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("message")
        var message: String? = null

        @SerializedName("sender")
        var sender: Sender? = null

        @SerializedName("receiver")
        var receiver: Receiver? = null

        @SerializedName("createdat")
        var createdat: Date? = null

    }

    class Sender {

        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

    }

    class Receiver {

        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

    }

    class Contact {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("sitterId")
        var sitterId: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("name")
        var name: String? = null

    }

    class Booking() {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("sitterId")
        var sitterId: String? = null

        @SerializedName("startDate")
        var startDate: String? = null

        @SerializedName("endDate")
        var endDate: String? = null

        @SerializedName("serviceType")
        var serviceType: String? = null

        @SerializedName("status")
        var status: String? = null

        @SerializedName("location")
        var location: String? = null

        @SerializedName("message")
        var message: String? = null


        @SerializedName("petpicketup")
        var petpicketup: Boolean = false

        @SerializedName("timepetpicketup")
        var timepetpicketup: String? = null

        @SerializedName("inprogress")
        var inprogress: Boolean = false

        @SerializedName("timeinprogress")
        var timeinprogress: String? = null

        @SerializedName("returning")
        var returning: Boolean = false

        @SerializedName("timereturning")
        var timereturning: String? = null

        @SerializedName("completed")
        var completed: Boolean = false

        @SerializedName("timecompleted")
        var timecompleted: String? = null


        @SerializedName("total")
        var total: String? = null


        @SerializedName("image")
        var image: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("review")
        var review: Review? = null

    }

    class Favourite() {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("sitterId")
        var sitterId: String? = null

    }

    class ApplicationSitter {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("sitterId")
        var sitterId: String? = null

        @SerializedName("date")
        var date: String? = null

    }

    class Picture {

        @SerializedName("_id")
        var id: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("filename")
        var filename: String? = null

        @SerializedName("url")
        var url: String? = null

    }

    class Sitter() : Parcelable {

        @SerializedName("_id")
        var sitterId: String? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("verified")
        var verified: Boolean? = false

        @SerializedName("headline")
        var headline: String? = null

        @SerializedName("description")
        var description: String? = null

        @SerializedName("lat")
        var lat: String? = null

        @SerializedName("long")
        var long: String? = null

        @SerializedName("phone")
        var phone: String? = null

        @SerializedName("sortcode")
        var sortcode: String? = null

        @SerializedName("accountnumber")
        var accountnumber: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("reviews")
        var reviews: List<Review>? = null


        @SerializedName("petwalking")
        var petwalking: Boolean? = false

        @SerializedName("ratewalking")
        var ratewalking: String? = null

        @SerializedName("ratewalkingaddpet")
        var ratewalkingaddpet: String? = null


        @SerializedName("petboarding")
        var petboarding: Boolean? = false

        @SerializedName("ratepetboarding")
        var ratepetboarding: String? = null

        @SerializedName("ratepetboardingaddpet")
        var ratepetboardingaddpet: String? = null


        @SerializedName("housesitting")
        var housesitting: Boolean? = false

        @SerializedName("ratehousesitting")
        var ratehousesitting: String? = null

        @SerializedName("ratehousesittingaddpet")
        var ratehousesittingaddpet: String? = null


        @SerializedName("training")
        var training: Boolean? = false

        @SerializedName("ratetraining")
        var ratetraining: String? = null

        @SerializedName("ratetrainingaddpet")
        var ratetrainingaddpet: String? = null


        @SerializedName("grooming")
        var grooming: Boolean? = false

        @SerializedName("rategrooming")
        var rategrooming: String? = null

        @SerializedName("rategroomingaddpet")
        var rategroomingaddpet: String? = null


        @SerializedName("pickupdropoff")
        var pickupdropoff: Boolean? = false

        @SerializedName("oralmedications")
        var oralmedications: Boolean? = false

        @SerializedName("injectmedications")
        var injectmedications: Boolean? = false

        constructor(parcel: Parcel) : this() {
            sitterId = parcel.readString()
            userId = parcel.readString()
            verified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            headline = parcel.readString()
            description = parcel.readString()
            lat = parcel.readString()
            long = parcel.readString()
            phone = parcel.readString()
            sortcode = parcel.readString()
            accountnumber = parcel.readString()
            name = parcel.readString()
            image = parcel.readString()
            petwalking = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            ratewalking = parcel.readString()
            ratewalkingaddpet = parcel.readString()
            petboarding = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            ratepetboarding = parcel.readString()
            ratepetboardingaddpet = parcel.readString()
            housesitting = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            ratehousesitting = parcel.readString()
            ratehousesittingaddpet = parcel.readString()
            training = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            ratetraining = parcel.readString()
            ratetrainingaddpet = parcel.readString()
            grooming = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            rategrooming = parcel.readString()
            rategroomingaddpet = parcel.readString()
            pickupdropoff = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            oralmedications = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            injectmedications = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(sitterId)
            parcel.writeString(userId)
            parcel.writeValue(verified)
            parcel.writeString(headline)
            parcel.writeString(description)
            parcel.writeString(lat)
            parcel.writeString(long)
            parcel.writeString(phone)
            parcel.writeString(sortcode)
            parcel.writeString(accountnumber)
            parcel.writeString(name)
            parcel.writeString(image)
            parcel.writeValue(petwalking)
            parcel.writeString(ratewalking)
            parcel.writeString(ratewalkingaddpet)
            parcel.writeValue(petboarding)
            parcel.writeString(ratepetboarding)
            parcel.writeString(ratepetboardingaddpet)
            parcel.writeValue(housesitting)
            parcel.writeString(ratehousesitting)
            parcel.writeString(ratehousesittingaddpet)
            parcel.writeValue(training)
            parcel.writeString(ratetraining)
            parcel.writeString(ratetrainingaddpet)
            parcel.writeValue(grooming)
            parcel.writeString(rategrooming)
            parcel.writeString(rategroomingaddpet)
            parcel.writeValue(pickupdropoff)
            parcel.writeValue(oralmedications)
            parcel.writeValue(injectmedications)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Sitter> {
            override fun createFromParcel(parcel: Parcel): Sitter {
                return Sitter(parcel)
            }

            override fun newArray(size: Int): Array<Sitter?> {
                return arrayOfNulls(size)
            }
        }


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