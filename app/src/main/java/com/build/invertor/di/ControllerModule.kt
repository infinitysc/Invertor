package com.build.invertor.di

import com.build.invertor.mainModule.AbstractController
import com.build.invertor.mainModule.camera.CameraController
import com.build.invertor.mainModule.start.StartFragmentController
import dagger.Binds
import dagger.Module
import kotlin.reflect.KClass

@Module
interface ControllerModule {

    @Binds
    @example(StartFragmentController::class)
    fun createStartController(startFragmentController: StartFragmentController) : AbstractController

    @Binds
    fun createCameraController(cameraController: CameraController) : AbstractController


}



annotation class example(val value : KClass<out AbstractController>)