package com.build.Invertor.mainModule.SingleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.build.Invertor.R
import com.build.Invertor.mainModule.Start.StartFragment

class MainActivity : AppCompatActivity() {


    private val fragmentSupp = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_layout)

        val mainFragment = StartFragment()
        fragmentSupp.beginTransaction()
            .replace(R.id.mainFrameLayout,mainFragment)
            .commit()
    }

}