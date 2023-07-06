package com.pawcare.pawcare.services

import android.content.SharedPreferences
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.JsonObject
import com.pawcare.pawcare.App
import com.pawcare.pawcare.BuildConfig
import com.pawcare.pawcare.Utils
import com.pawcare.pawcare.services.Callback
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BackOffice(
    private val backendInstance: Backend
) {

    private var retrofit: Retrofit? = null
    private var apiInterface: ApiInterface
    private val preferences: SharedPreferences = backendInstance.preferences

    init {
        apiInterface = getClient().create(ApiInterface::class.java)
    }

    private fun getClient(): Retrofit {

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
            .addInterceptor { chain ->
                val token = preferences.getString("TOKEN", "")

                val originalRequest = chain.request()
                val request: Request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)

            }
            .build()

        if (retrofit == null)
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SERVICE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        return retrofit!!

    }

    fun getToken(listener: Listener<Any>?) {

        val date = preferences.getLong("TOKEN_DATE", 0)
        val token = preferences.getString("TOKEN", null)

        if (System.currentTimeMillis() >= date || token == null)
            getTokenFromService()
        else listener?.onResponse(token)
    }

    private fun getTokenFromService() {

        val secretKey = BuildConfig.SecretJWT
        val keyId = BuildConfig.KeyJWT
        val issuer = BuildConfig.IssuerJWT

        val algorithm = Algorithm.HMAC256(secretKey)

        val now = Date()
        val expiresAt = Date(now.time + 2 * 60 * 60 * 1000) //2h from now

        val token = JWT.create()
            .withIssuer(issuer)
            .withExpiresAt(expiresAt)
            .sign(algorithm)

        val editor = preferences.edit()
        editor.putString("TOKEN", token)
        editor.putLong("TOKEN_DATE", expiresAt.time)
        editor.apply()

    }

    fun loginUser(listener: Listener<Any>?, user: JsonObject) {
        apiInterface.loginUser(user).enqueue(object : Callback<JsonObject>() {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    try {

                        val user = response.body()!!.getAsJsonObject("user")
                        val token = response.body()!!.get("token")

                        val editor = preferences.edit()

                        if (user.get("_id") != null)
                            editor.putString("userId", user.get("_id").asString)
                        if (user.get("sitterId") != null)
                            editor.putString("sitterId", user.get("sitterId").asString)
                        if (user.get("fullname") != null)
                            editor.putString("fullname", user.get("fullname").asString)
                        if (user.get("dateOfBirth") != null)
                            editor.putString("dateOfBirth", user.get("dateOfBirth").asString)
                        if (user.get("phoneNumber") != null)
                            editor.putString("phoneNumber", user.get("phoneNumber").asString)
                        if (user.get("email") != null)
                            editor.putString("email", user.get("email").asString)
                        if (user.get("image") != null)
                            editor.putString("image", user.get("image").asString)

                        if (token != null) {
                            editor.putString("TOKEN", token.asString)
                            editor.putLong("TOKEN_DATE", Utils.getExpirationDateToken(token.asString))
                        }

                        editor.apply()

                        Log.d("loginUser BO", "successful")

                        listener?.onResponse(response.body())

                    } catch (e: Exception) {
                        e.printStackTrace()

                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun registerUser(listener: Listener<Any>?, user: JsonObject, file: File?) {

        val userRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), user.toString())

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.registerUser(userRequestBody, filePart).enqueue(object : Callback<ApiInterface.User>() {
            override fun onResponse(
                call: Call<ApiInterface.User>,
                response: Response<ApiInterface.User>
            ) {
                if (response.isSuccessful) {

                    try {

                        Log.d("registerUser BO", "successful")

                        listener?.onResponse(response.body())

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.User>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun addPet(listener: Listener<Any>?, pet: JsonObject, file: File?) {

        val petRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), pet.toString())

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.addPet(petRequestBody, filePart).enqueue(object : Callback<JsonObject>() {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    try {

                        Log.d("add Pet BO", "successful")

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun addPicture(listener: Listener<Any>?, file: File?) {

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.addPicture(filePart).enqueue(object : Callback<ApiInterface.Picture>() {
            override fun onResponse(
                call: Call<ApiInterface.Picture>,
                response: Response<ApiInterface.Picture>
            ) {
                if (response.isSuccessful) {

                    try {

                        Log.d("Add Picture BO", "successful")

                        listener?.onResponse(response.body())

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Picture>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun addFavourite(listener: Listener<Any>?, id: String) {

        apiInterface.addFavourite(id).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun addBooking(listener: Listener<Any>?, booking: JsonObject) {

        apiInterface.addBooking(booking).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun updateStateBooking(listener: Listener<Any>?, id: String) {

        apiInterface.updateStateBooking(id).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun listContacts(listener: Listener<Any>?) {

        apiInterface.listContacts().enqueue(object : retrofit2.Callback<List<ApiInterface.Contact>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Contact>>,
                response: Response<List<ApiInterface.Contact>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Contact>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun chatMessages(listener: Listener<Any>?, id: String) {

        apiInterface.chatMessages(id).enqueue(object : retrofit2.Callback<List<ApiInterface.Message>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Message>>,
                response: Response<List<ApiInterface.Message>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Message>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun chatMessagesSitter(listener: Listener<Any>?, id: String) {

        apiInterface.chatMessagesSitter(id).enqueue(object : retrofit2.Callback<List<ApiInterface.Message>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Message>>,
                response: Response<List<ApiInterface.Message>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Message>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun listContactsSitter(listener: Listener<Any>?) {

        apiInterface.listContactsSitter().enqueue(object : retrofit2.Callback<List<ApiInterface.Contact>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Contact>>,
                response: Response<List<ApiInterface.Contact>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Contact>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun addContact(listener: Listener<Any>?, id: String) {

        apiInterface.addContact(id).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun sendMessage(listener: Listener<Any>?, message: JsonObject) {

        apiInterface.sendMessage(message).enqueue(object : Callback<ApiInterface.Message>() {
            override fun onResponse(
                call: Call<ApiInterface.Message>,
                response: Response<ApiInterface.Message>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(response.body())

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Message>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun sendMessageSitter(listener: Listener<Any>?, message: JsonObject) {

        apiInterface.sendMessageSitter(message).enqueue(object : Callback<ApiInterface.Message>() {
            override fun onResponse(
                call: Call<ApiInterface.Message>,
                response: Response<ApiInterface.Message>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(response.body())

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Message>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun cancelBooking(listener: Listener<Any>?, id: String) {

        apiInterface.cancelBooking(id).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    try {

                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun startApplication(listener: Listener<Any>?) {

        apiInterface.startApplication().enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)

                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun sendVerificationEmailForgotPassword(listener: Listener<Any>?, user: JsonObject) {
        apiInterface.sendVerificationEmailForgotPassword(user).enqueue(object : Callback<JsonObject>() {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    try {

                        Log.d("Forgot Password code BO", "successful")

                        //val userId = response.body()!!.asJsonObject.get("userId").asString
                        listener?.onResponse(response.body()!!.asJsonObject)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun verifyForgotPasswordCode(listener: Listener<Any>?, userId: String, code: String) {

        apiInterface.verifyForgotPasswordCode(userId, code).enqueue(object : Callback<Void>() {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)

                }
                else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun resetPassword(listener: Listener<Any>?, user: JsonObject) {
        apiInterface.resetPassword(user).enqueue(object : Callback<JsonObject>() {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    try {

                        Log.d("Reset Password code BO", "successful")

                        //val userId = response.body()!!.asJsonObject.get("userId").asString
                        listener?.onResponse(null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverError(call, response, listener)
                    }

                } else {

                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                clientError(t, null)
            }

        })
    }

    fun getServices(listener: Listener<Any>?) {

        apiInterface.getServices().enqueue(object : Callback<Void>() {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)

                } else
                    serverError(call, response, listener)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getPets(listener: Listener<Any>?) {

        apiInterface.getPets().enqueue(object : retrofit2.Callback<List<ApiInterface.Pet>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Pet>>,
                response: Response<List<ApiInterface.Pet>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Pet>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getPictures(listener: Listener<Any>?) {

        apiInterface.getPictures().enqueue(object : retrofit2.Callback<List<ApiInterface.Picture>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Picture>>,
                response: Response<List<ApiInterface.Picture>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Picture>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getFavourites(listener: Listener<Any>?) {

        apiInterface.getFavourites().enqueue(object : retrofit2.Callback<List<ApiInterface.Sitter>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Sitter>>,
                response: Response<List<ApiInterface.Sitter>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Sitter>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getBookingsActive(listener: Listener<Any>?) {

        apiInterface.getBookingsActive().enqueue(object : retrofit2.Callback<List<ApiInterface.Booking>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Booking>>,
                response: Response<List<ApiInterface.Booking>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Booking>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getBookingsCompleted(listener: Listener<Any>?) {

        apiInterface.getBookingsCompleted().enqueue(object : retrofit2.Callback<List<ApiInterface.Booking>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Booking>>,
                response: Response<List<ApiInterface.Booking>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Booking>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getBookings(listener: Listener<Any>?, month: Int, year: Int) {

        apiInterface.getBookings(month, year).enqueue(object : retrofit2.Callback<List<ApiInterface.Booking>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Booking>>,
                response: Response<List<ApiInterface.Booking>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Booking>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getPicturesSitter(listener: Listener<Any>?, id: String) {

        apiInterface.getPicturesSitter(id).enqueue(object : retrofit2.Callback<List<ApiInterface.Picture>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Picture>>,
                response: Response<List<ApiInterface.Picture>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Picture>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getSitters(listener: Listener<Any>?, latitude: String, longitude: String, service: List<String>) {

        apiInterface.getSitters(latitude, longitude, service).enqueue(object : retrofit2.Callback<List<ApiInterface.Sitter>> {
            override fun onResponse(
                call: Call<List<ApiInterface.Sitter>>,
                response: Response<List<ApiInterface.Sitter>>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.Sitter>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getApplication(listener: Listener<Any>?) {

        apiInterface.getApplication().enqueue(object : Callback<ApiInterface.ApplicationSitter>() {
            override fun onResponse(
                call: Call<ApiInterface.ApplicationSitter>,
                response: Response<ApiInterface.ApplicationSitter>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.ApplicationSitter>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getFavourite(listener: Listener<Any>?, id: String) {

        apiInterface.getFavourite(id).enqueue(object : Callback<ApiInterface.Favourite>() {
            override fun onResponse(
                call: Call<ApiInterface.Favourite>,
                response: Response<ApiInterface.Favourite>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Favourite>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun deletePicture(listener: Listener<Any>?, filename : String) {

        apiInterface.deletePicture(filename).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun deleteFavourite(listener: Listener<Any>?, id : String) {

        apiInterface.deleteFavourite(id).enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun applicationSubmit(listener: Listener<Any>?) {

        apiInterface.applicationSubmit().enqueue(object : Callback<Void>() {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun sendPhoneVerification(listener: Listener<Any>?, phoneNumber: String) {

        apiInterface.sendPhoneVerification(phoneNumber).enqueue(object : Callback<Void>() {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {

                    listener?.onResponse(null)

                }
                else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun verifyPhone(listener: Listener<Any>?, user: JsonObject) {

        apiInterface.verifyPhone(user).enqueue(object : Callback<Void>() {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {

                    listener?.onResponse(null)

                }
                else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    if (jsonObj.getString("message").isNotEmpty()) {
                        listener?.onResponse(jsonObj.getString("message"))
                    }
                    else
                        serverError(call, response, listener)
                }

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getSitter(listener: Listener<Any>?) {

        apiInterface.getSitter().enqueue(object : retrofit2.Callback<ApiInterface.Sitter> {
            override fun onResponse(
                call: Call<ApiInterface.Sitter>,
                response: Response<ApiInterface.Sitter>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Sitter>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getSitterById(listener: Listener<Any>?, id: String) {

        apiInterface.getSitterById(id).enqueue(object : retrofit2.Callback<ApiInterface.Sitter> {
            override fun onResponse(
                call: Call<ApiInterface.Sitter>,
                response: Response<ApiInterface.Sitter>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.Sitter>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getUserById(listener: Listener<Any>?, id: String) {

        apiInterface.getUserById(id).enqueue(object : retrofit2.Callback<ApiInterface.User> {
            override fun onResponse(
                call: Call<ApiInterface.User>,
                response: Response<ApiInterface.User>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.User>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun updatePet(listener: Listener<Any>?, id: String, pet: JsonObject, file: File?) {

        val petRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), pet.toString())

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.updatePet(id, petRequestBody, filePart).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun updateProfile(listener: Listener<Any>?, user: JsonObject, file: File?) {

        val userRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), user.toString())

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.updateProfile(userRequestBody, filePart).enqueue(object : Callback<ApiInterface.User>() {
            override fun onResponse(
                call: Call<ApiInterface.User>,
                response: Response<ApiInterface.User>
            ) {
                if (response.isSuccessful) {

                    try {

                        val userObject = response.body()!!

                        val editor = App.instance.preferences.edit()

                        if (userObject.fullname != null)
                            editor.putString("fullname", userObject.fullname)
                        if (userObject.dateOfBirth != null)
                            editor.putString("dateOfBirth", userObject.dateOfBirth)
                        if (userObject.image != null)
                            editor.putString("image", userObject.image)

                        editor.apply()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<ApiInterface.User>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun updateSitter(listener: Listener<Any>?, sitter: JsonObject, file: File?) {

        val sitterRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), sitter.toString())

        var filePart : MultipartBody.Part? = null
        if (file != null) {
            val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        }

        apiInterface.updateSitter(sitterRequestBody, filePart).enqueue(object : Callback<JsonObject>() {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {

                    try {

                        val userObject = response.body()!!

                        Log.d("userObject", userObject.toString())

                        val editor = App.instance.preferences.edit()

                        if (userObject.get("image") != null)
                            editor.putString("image", userObject.get("image").asString)

                        editor.apply()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    listener?.onResponse(null)
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getDogBreeds(listener: Listener<Any>?) {

        apiInterface.dogBreeds(BuildConfig.DOG_API_URL, BuildConfig.DOG_API_KEY).enqueue(object : retrofit2.Callback<List<ApiInterface.DogBreed>> {
            override fun onResponse(
                call: Call<List<ApiInterface.DogBreed>>,
                response: Response<List<ApiInterface.DogBreed>>
            ) {
                if (response.isSuccessful) {
                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.DogBreed>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    fun getCatBreeds(listener: Listener<Any>?) {

        apiInterface.catBreeds(BuildConfig.CAT_API_URL, BuildConfig.CAT_API_KEY).enqueue(object : retrofit2.Callback<List<ApiInterface.CatBreed>> {
            override fun onResponse(
                call: Call<List<ApiInterface.CatBreed>>,
                response: Response<List<ApiInterface.CatBreed>>
            ) {
                if (response.isSuccessful) {
                    listener?.onResponse(response.body())
                }
                else {
                    serverError(call, response, listener)
                }
            }

            override fun onFailure(call: Call<List<ApiInterface.CatBreed>>, t: Throwable) {
                clientError(t, null)
            }

        })

    }

    private fun serverError(call: Call<*>, response: retrofit2.Response<*>, listener: Listener<Any>?) {
        try {
            if (response.code() == 401)
                getTokenFromService()


            val errorMessage = if (response.code() == 403) "403" else response.errorBody()!!.string()

            Log.d("Error ", call.request().url.toString() + "\n\t" + errorMessage)

            listener?.onResponse(errorMessage)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun clientError(t: Throwable, listener: Listener<Any>?) {
        t.printStackTrace()

        listener?.onResponse("error")
    }

}