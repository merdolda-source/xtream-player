// Android/app/src/main/kotlin/com/xtream/player/di/RepositoryModule.kt
package com.erdin.player.di

import com.erdin.player.data.repositories.AuthRepositoryImpl
import com.erdin.player.data.repositories.StreamRepositoryImpl
import com.erdin.player.domain.repositories.AuthRepository
import com.erdin.player.domain.repositories.StreamRepository
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
