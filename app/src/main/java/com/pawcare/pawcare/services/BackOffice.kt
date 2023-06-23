package com.pawcare.pawcare.services

import android.content.SharedPreferences
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.JsonObject
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
import java.util.Date
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