package com.build.invertor.di

import android.content.Context
import com.build.invertor.mainModule.card.CardFragmentNew
import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.listFragment.ListFragmentNew
import com.build.invertor.mainModule.settings.LoaderFragment
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.DataBaseModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules =
        [
            ViewModelModule::class,
            DataBaseModule::class
        ]
)
interface AppComponent {


    fun injectStartFragment(startFragment: StartFragmentNew)

    fun injectCameraFragment(cameraFragmentNew: CameraFragmentNew)

    fun injectLoaderFragment(loader : LoaderFragment)

    fun injectListFragment(listFragment : ListFragmentNew)

    fun injectCardFragment(cardFragmentNew: CardFragmentNew)

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context : Context) : AppComponent

    }

}


