package com.build.invertor.mainModule.listFragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.build.invertor.mainModule.application.appComponent

class ListChoiceFragmentNew : Fragment() {

    override fun onAttach(context: Context) {
        context.appComponent.injectListFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }


}