package com.build.invertor.mainModule.singleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.build.Invertor.R
import com.build.invertor.mainModule.application.App
import com.build.invertor.mainModule.oldFragments.StartFragment
import com.build.invertor.mainModule.start.StartFragmentNew
import com.build.invertor.model.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.time.measureTime

class MainActivity : AppCompatActivity(){

    private val fragmentSupp = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_layout)

        val mainFragment = StartFragmentNew()

        supportActionBar?.hide()
        fragmentSupp.beginTransaction()
            .replace(R.id.mainFrameLayout,mainFragment)
            .commit()

    }




}
