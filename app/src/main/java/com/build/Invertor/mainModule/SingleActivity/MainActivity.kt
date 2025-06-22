package com.build.Invertor.mainModule.SingleActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.NavHostFragment
import com.build.Invertor.R
import com.build.Invertor.mainModule.Start.StartFragment
import com.build.Invertor.model.Model
import com.google.gson.GsonBuilder

class MainActivity : AppCompatActivity(), ModelSharedInterface{


    private val fragmentSupp = supportFragmentManager
    private var model : Model? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_layout)

        val mainFragment = StartFragment()
        fragmentSupp.beginTransaction()
            .replace(R.id.mainFrameLayout,mainFragment)
            .commit()
    }

    override fun getModel(): Model? {
        return null
    }

    override fun haveFiles(): Boolean {
        return false
    }

    override fun updateModel(flag : Boolean) {

    }

}
