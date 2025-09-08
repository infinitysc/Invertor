package com.build.invertor.mainModule.application

import android.app.Application
import Debug.logCatLogger.LogCatSaver
import android.content.Context
import com.build.invertor.di.AppComponent
import com.build.invertor.di.DaggerAppComponent
import com.build.invertor.model.database.Repository

import org.acra.BuildConfig
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import javax.inject.Inject

class App : Application() {

    lateinit var appComponent: AppComponent

    private lateinit var logCatSaver: LogCatSaver



    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        logCatSaver = LogCatSaver(this)
        logCatSaver.startLogging()

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.KEY_VALUE_LIST


            mailSender{
                mailTo = "mudunad@gmail.com"
                subject = "Crash Report: ${BuildConfig.VERSION_NAME}"
                body = "произошла ошибка"
                reportAsFile = true
                reportFileName = "crash_log.txt"
                enabled = true
            }

            dialog {
                title = "Ошибка"
                text = "Приложение столкнулось с проблемой. Отправить отчет разработчику?"
                positiveButtonText = "Отправить"
                negativeButtonText = "Отмена"
            }
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        logCatSaver.stopLogging()
    }

}

val Context.appComponent : AppComponent
    get() = when(this){
        is App -> this.appComponent
        else -> this.applicationContext.appComponent
    }
