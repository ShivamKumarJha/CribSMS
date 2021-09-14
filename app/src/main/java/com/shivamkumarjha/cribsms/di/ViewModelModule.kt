package com.shivamkumarjha.cribsms.di

import android.content.ContentResolver
import android.content.Context
import com.shivamkumarjha.cribsms.repository.ContentRepository
import com.shivamkumarjha.cribsms.repository.ContentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun getContentRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ContentRepository {
        val contentResolver: ContentResolver = context.contentResolver
        return ContentRepositoryImpl(contentResolver, ioDispatcher)
    }
}