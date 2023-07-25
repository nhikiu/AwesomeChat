package com.example.baseproject.di

import com.example.baseproject.respository.AuthRepository
import com.example.baseproject.respository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(database: FirebaseDatabase, auth: FirebaseAuth) : AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }

}