package com.zuhlke.logging.viewer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object DispatcherModule {

    @Provides
    @Named(DISPATCHER_IO)
    fun provideUserId(): CoroutineDispatcher = Dispatchers.IO
}

const val DISPATCHER_IO = "IO"