// Android/app/src/main/kotlin/com/xtream/player/di/RepositoryModule.kt
package com.xtream.player.di

import com.xtream.player.data.repositories.AuthRepositoryImpl
import com.xtream.player.data.repositories.StreamRepositoryImpl
import com.xtream.player.domain.repositories.AuthRepository
import com.xtream.player.domain.repositories.StreamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStreamRepository(impl: StreamRepositoryImpl): StreamRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
