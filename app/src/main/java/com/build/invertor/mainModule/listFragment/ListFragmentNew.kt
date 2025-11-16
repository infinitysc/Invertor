package com.build.invertor.mainModule.listFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.build.Invertor.R
import com.build.Invertor.databinding.RecyclerLayoutBinding
import com.build.invertor.mainModule.Card.CardFragmentNew
import com.build.invertor.mainModule.application.appComponent
import com.build.invertor.mainModule.listFragment.recycler.Adapter
import com.build.invertor.mainModule.oldFragments.CardFragment
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.modelOld.json.csv.NewUser
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragmentNew : Fragment() {


    private val binding : RecyclerLayoutBinding by lazy { RecyclerLayoutBinding.inflate(layoutInflater) }

    private var list : List<Int> = emptyList()
    private var user : NewUser? = null

    @Inject
    lateinit var factory : DaggerViewModelFactory


    private val viewModel : ListFragmentViewModel by viewModels {factory}

    override fun onAttach(context: Context) {
        context.appComponent.injectListFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBundle()
        binding.recyclerMama.layoutManager = LinearLayoutManager(requireContext())

        val listWithData: MutableList<CardEntity> = mutableListOf()
        lifecycleScope.launch {
            listWithData.clear()
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.getFlowListData(list).collect {
                    listWithData.add(it)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                binding.recyclerMama.adapter = Adapter(listWithData, user!!){card, user ->
                    this@ListFragmentNew.activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.mainFrameLayout, CardFragmentNew.newInstance(bundle = Bundle().apply {
                            putInt("index",card.index)
                            putParcelable("user",user)
                        }))
                        ?.addToBackStack("list")
                        ?.commit()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }


    private fun checkBundle() {
        try {
            if(this.arguments != null) {
                user = arguments?.getParcelable("user")
                list = arguments?.getIntegerArrayList("list")!!.toList()
            }
        }
        catch (e : NullPointerException) {
            Log.e("NullPointerException", e.message.toString())
            this.activity?.supportFragmentManager?.popBackStack()
        }
    }

companion object {
    fun newInstance(bundle : Bundle) : ListFragmentNew {
        val fra = ListFragmentNew()
        fra.arguments = bundle
        return fra
    }
}
}