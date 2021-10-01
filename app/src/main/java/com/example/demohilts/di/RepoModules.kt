package com.example.demohilts.di

import com.example.demohilts.data.repo.MainRepo
import com.example.demohilts.data.repo.MainRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepoModules {

    @Binds
    abstract fun bindMainRepo(
        mainRepoImpl: MainRepoImpl
    ): MainRepo
}