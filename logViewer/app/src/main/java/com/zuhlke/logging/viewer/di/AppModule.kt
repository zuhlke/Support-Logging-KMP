package com.zuhlke.logging.viewer.di

import android.content.Context
import com.zuhlke.logger.logviewer.core.export.AndroidShareService
import com.zuhlke.logger.logviewer.core.export.JsonLogConverter
import com.zuhlke.logger.logviewer.core.export.JsonLogExporter
import com.zuhlke.logger.logviewer.core.export.KotlinSerializationJsonLogConverter
import com.zuhlke.logger.logviewer.core.export.LogExporter
import com.zuhlke.logger.logviewer.core.export.ShareService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideJsonLogConverter(): JsonLogConverter = KotlinSerializationJsonLogConverter()

    @Provides
    fun shareService(@ApplicationContext context: Context): ShareService =
        AndroidShareService(context)

    @Provides
    fun provideLogExporter(
        jsonLogConverter: JsonLogConverter,
        shareService: ShareService
    ): LogExporter = JsonLogExporter(jsonLogConverter, shareService)
}
