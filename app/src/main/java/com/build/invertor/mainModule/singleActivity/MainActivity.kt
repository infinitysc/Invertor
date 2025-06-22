package com.build.invertor.mainModule.singleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.build.Invertor.R
import com.build.invertor.mainModule.start.StartFragment
import com.build.invertor.model.Model

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
