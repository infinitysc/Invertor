package com.build.invertor.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.build.invertor.mainModule.settings.LoaderViewModel
import com.build.invertor.mainModule.start.StartViewModel
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface ViewModelModule {



    @Binds
    @IntoMap
    @ViewModelKey(value = LoaderViewModel::class)
    fun bindLoaderViewModel(loader : LoaderViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(value = StartViewModel::class)
    fun bindStartViewModel(startViewModel: StartViewModel) : ViewModel


    @Binds
    fun bindViewModelFactory(daggerViewModeLFactory : DaggerViewModelFactory) : ViewModelProvider.Factory
}