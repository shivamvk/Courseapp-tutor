package com.mindful.networklibrary

import android.content.Context
import android.content.SharedPreferences
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.sharedprefs.PreferencesHelper
import com.mindful.networklibrary.sharedprefs.PreferencesHelper.get
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Singleton

@Module
class NetworkModule(val context: Context) {

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(BuildConfig.BASEURL)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptors: ArrayList<Interceptor>
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .followRedirects(false)
        interceptors.forEach {
            clientBuilder.addInterceptor(it)
        }
        return clientBuilder.build()
    }


    @Singleton
    @Provides
    fun provideInterceptors(token: String?): ArrayList<Interceptor> {
        val interceptors = arrayListOf<Interceptor>()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            HttpLoggingInterceptor.Level.BODY
        }
        interceptors.add(loggingInterceptor)
        if (!token.isNullOrEmpty()){
            val authInterceptor = object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("token", "$token")
                        .build()
                    return chain.proceed(request)
                }
            }
            interceptors.add(authInterceptor)
        }
        return interceptors
    }

    @Singleton
    @Provides
    fun provideSharePrefs(context: Context): SharedPreferences = PreferencesHelper.appPrefs(context)

    @Singleton
    @Provides
    fun provideToken(prefs: SharedPreferences): String? = prefs[PreferencesHelper.SharedPrefKeys.USER_TOKEN.toString(), ""]

    @Singleton
    @Provides
    fun provideContext(): Context = context
}