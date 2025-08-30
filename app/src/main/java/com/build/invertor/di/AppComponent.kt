package com.build.invertor.di

import android.app.Application
import android.content.Context
import com.build.invertor.mainModule.Card.CardFragmentNew
import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.listFragment.ListChoiceFragment
import com.build.invertor.mainModule.listFragment.ListFragmentController
import com.build.invertor.mainModule.oldFragments.StartFragment
import com.build.invertor.mainModule.settings.Settings
import com.build.invertor.mainModule.start.StartFragmentController
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.database.Repository
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Component(
    modules = [FileCreator::class, GsonClass::class]
)
@Singleton
interface AppComponent {

    fun injectDatabase(app : Application)
    fun injectStartFragment(startFragment: StartFragmentNew)
    fun injectCameraFragment(cameraFragmentNew: CameraFragmentNew)
    fun injectSettings(settings: Settings)
    fun injectListFragment(listChoiceFragment: ListChoiceFragment)
    fun injectCardFragment(cardFragmentNew: CardFragmentNew)
    fun getDatabaseImpl() : Repository

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context : Context) : AppComponent

    }


}


