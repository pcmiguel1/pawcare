package com.pawcare.pawcare.services

import android.content.SharedPreferences
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.pawcare.pawcare.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            getTokenFromServices()
        else listener?.onResponse(token)
    }

    private fun getTokenFromServices() {

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

}