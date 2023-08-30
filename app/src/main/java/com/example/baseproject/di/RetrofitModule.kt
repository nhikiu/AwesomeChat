package com.example.baseproject.di

import com.example.baseproject.notification.GoogleAPI
import com.example.baseproject.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RetrofitModule {
    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder().baseUrl(Constants.GOOGLE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleApiService(retrofit: Retrofit) : GoogleAPI {
        return retrofit.create(GoogleAPI::class.java)
    }
}