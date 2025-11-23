package com.build.invertor.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.build.invertor.mainModule.card.CardViewModel
import com.build.invertor.mainModule.camera.CameraViewModel
import com.build.invertor.mainModule.listFragment.ListFragmentViewModel
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
    @ViewModelKey(value = ListFragmentViewModel::class)
    fun bindListFragmentViewModel(listFragmentViewModel: ListFragmentViewModel) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(value = CardViewModel::class)
    fun bindCardViewModel(cardViewModel: CardViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(value = CameraViewModel::class)
    fun bindCameraViewModel(cameraViewModel: CameraViewModel) : ViewModel

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