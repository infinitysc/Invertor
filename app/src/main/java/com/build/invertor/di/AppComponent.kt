package com.build.invertor.di

import android.app.Application
import android.content.Context
import com.build.invertor.mainModule.Card.CardFragmentNew
import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.listFragment.ListFragmentNew
import com.build.invertor.mainModule.settings.LoaderFragment
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.DataBaseCreator
import com.build.invertor.model.database.Repository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [FileCreator::class,GsonClass::class, ViewModelModule::class, DataBaseCreator::class]
)
interface AppComponent {

    fun injectDatabase(app : Application)

    fun injectStartFragment(startFragment: StartFragmentNew)

    fun injectCameraFragment(cameraFragmentNew: CameraFragmentNew)

    fun injectLoaderFragment(loader : LoaderFragment)

    fun injectListFragment(listFragment : ListFragmentNew)

    fun injectCardFragment(cardFragmentNew: CardFragmentNew)

    fun getDatabaseImpl() : Repository

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context : Context) : AppComponent

    }


}


