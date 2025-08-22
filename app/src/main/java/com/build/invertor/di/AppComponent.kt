package com.build.invertor.di

import android.app.Application
import android.content.Context
import com.build.invertor.mainModule.camera.CameraFragmentNew
import com.build.invertor.mainModule.oldFragments.StartFragment
import com.build.invertor.mainModule.start.StartFragmentController
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.database.DatabaseProvider
import com.build.invertor.model.database.Repository
import com.build.invertor.model.modelOld.json.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Component(
    modules = [FileCreator::class, GsonClass::class, DataModule::class, ControllerModule::class, DatabaseProvider::class]
)
@Singleton
interface AppComponent {

    fun injectDatabase(app : Application)
    fun injectStartFragment(startFragment: StartFragmentNew)
    fun injectCameraFragment(cameraFragmentNew: CameraFragmentNew)


    fun getDatabaseImpl() : Repository

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context : Context) : AppComponent

    }


}


