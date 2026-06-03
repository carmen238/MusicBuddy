package com.example.musicbuddy.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient - Singleton for Retrofit HTTP client
 *
 * IMPORTANT: Configure the BASE_URL based on your setup:
 * - For Android Emulator: http://10.0.2.2:3000/
 *   (10.0.2.2 is a special alias that maps to the host machine's localhost)
 *
 * - For Physical Device: http://YOUR_MACHINE_IP:3000/
 *   (Replace YOUR_MACHINE_IP with your computer's IP address on the local network)
 *   Example: http://192.168.1.100:3000/
 *
 *   To find your machine IP:
 *   - Windows: Open Command Prompt and type: ipconfig
 *   - Mac/Linux: Open Terminal and type: ifconfig
 *   Look for "IPv4 Address" or "inet" (usually starts with 192.168.x.x or 10.x.x.x)
 */
object RetrofitClient {

    // ⚠️ CHANGE THIS BASED ON YOUR SETUP
    // For emulator: http://10.0.2.2:3000/
    // For physical device: http://172.20.10.4:3000/
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private var retrofit: Retrofit? = null

    /**
     * Get or create Retrofit instance
     */
    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

            println("✅ Retrofit initialized with base URL: $BASE_URL")
        }
        return retrofit!!
    }

    /**
     * Create OkHttpClient with logging and timeouts
     */
    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            println("🌐 HTTP: $message")
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Get AuthApiService instance
     */
    fun getAuthApiService(): AuthApiService {
        return getRetrofitInstance().create(AuthApiService::class.java)
    }

    fun getPhotoApiService(): PhotoApiService {
        return getRetrofitInstance().create(PhotoApiService::class.java)
    }
}