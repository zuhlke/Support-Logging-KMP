package com.zuhlke.logging.viewer.di

import android.content.Context
import com.zuhlke.logging.viewer.export.JsonLogExporter
import com.zuhlke.logging.viewer.export.LogExporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    fun provideLogExporter(@ApplicationContext context: Context): LogExporter =
        JsonLogExporter(context)
}