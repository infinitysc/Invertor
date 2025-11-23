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
import com.build.invertor.mainModule.card.CardFragmentNew
import com.build.invertor.mainModule.application.appComponent
import com.build.invertor.mainModule.listFragment.recycler.Adapter
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import com.build.invertor.model.modelOld.json.csv.NewUser
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragmentNew : Fragment(){

    @Inject
    lateinit var factory : DaggerViewModelFactory

    private val binding : RecyclerLayoutBinding by lazy { RecyclerLayoutBinding.inflate(layoutInflater) }
    private val viewModel : ListFragmentViewModel by viewModels {factory}

    private var list : List<Int> = emptyList()
    private var user : NewUser? = null
    private var code : String = ""

    override fun onAttach(context: Context) {
        context.appComponent.injectListFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBundle()
        binding.cod1c.text = this.code
        binding.recyclerMama.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.startWorking(code)
            }
        }

        viewModel.publicData.observe(viewLifecycleOwner) {
            binding.recyclerMama.adapter = Adapter(
                viewModel.publicData.value?.toMutableList() ?: mutableListOf()
                , user!!){ card, user ->
                this@ListFragmentNew.activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.mainFrameLayout, CardFragmentNew.newInstance(bundle = Bundle().apply {
                        putParcelable("user",user)
                        putInt("cardIndex",card.index).also{
                            Log.d("Index","index from recyclerView = ${card.index}")
                        }
                    }))
                    ?.addToBackStack("list")
                    ?.commit()
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
                list = arguments?.getIntArray("indexArray")!!.toList()
                code = arguments?.getString("Code1c") ?: ""
            }
        }
        catch (e : NullPointerException) {
            Log.e("NullPointerException", e.message.toString())
            this.activity?.supportFragmentManager?.popBackStack()
        }
    }

companion object {
    fun newInstance(bundle : Bundle) : ListFragmentNew {
        return ListFragmentNew().apply {
            arguments = bundle
        }
    }
}
}