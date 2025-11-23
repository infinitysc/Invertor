package com.build.invertor.mainModule.singleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.build.Invertor.R
import com.build.invertor.mainModule.start.StartFragmentNew
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

