package com.build.invertor.mainModule.singleActivity

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
        enableEdgeToEdge()
        val mainFragment = StartFragmentNew()

        fragmentSupp.beginTransaction()
            .replace(R.id.mainFrameLayout,mainFragment)
            .commit()
    }

}

