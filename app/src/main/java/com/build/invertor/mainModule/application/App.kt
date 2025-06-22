package com.build.invertor.mainModule.application

import android.app.Application
import com.build.invertor.mainModule.logCatLogger.LogCatSaver
import org.acra.BuildConfig
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class App : Application() {
    private lateinit var logCatSaver: LogCatSaver

    override fun onCreate() {
        super.onCreate()
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

