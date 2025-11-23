package com.build.invertor.mainModule.application

import android.app.Application
import android.content.Context
import com.build.invertor.di.AppComponent
import com.build.invertor.di.DaggerAppComponent
import org.acra.BuildConfig
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)

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
    }

}

val Context.appComponent : AppComponent
    get() = when(this){
        is App -> this.appComponent
        else -> this.applicationContext.appComponent
    }
