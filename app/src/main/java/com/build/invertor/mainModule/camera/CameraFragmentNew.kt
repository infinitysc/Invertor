package com.build.invertor.mainModule.camera

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.build.Invertor.R
import com.build.Invertor.databinding.NewCameraFragmentLayoutBinding
import com.build.invertor.mainModule.Card.CardFragmentNew
import com.build.invertor.mainModule.application.App
import com.build.invertor.mainModule.application.appComponent
import com.build.invertor.mainModule.oldFragments.ListChoiceFragment
import com.build.invertor.mainModule.listFragment.ListFragmentNew
import com.build.invertor.mainModule.oldFragments.CameraController
import com.build.invertor.mainModule.utils.CameraUtils
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import com.build.invertor.model.database.card.CardEntity
import com.build.invertor.model.database.card.Codes
import com.build.invertor.model.modelOld.json.csv.NewUser
import com.build.invertor.model.modelOld.json.json.CardInventory
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CameraFragmentNew : Fragment(){


    private var list : List<CardEntity> = emptyList()

    private val binding : NewCameraFragmentLayoutBinding by lazy {
        NewCameraFragmentLayoutBinding.inflate(layoutInflater)
    }

    private var codes : List<String> = emptyList()


    @Inject
    lateinit var factory : DaggerViewModelFactory

    private val alertDialog : AlertDialog by lazy { alertDialogCreator() }

    private val viewModel : CameraViewModel by viewModels { factory }

    private lateinit var controller: CameraController

    private val sound : MediaPlayer by lazy { MediaPlayer.create(requireContext(), R.raw.scanner_beep) }
    private var valueString : String = "defaultStringValue"
    private var user : NewUser? = null

    private val callback = BarcodeCallback { result ->
        if(result != null){
            sound.start()
            binding.value.text = result.text
            valueString = result.text
            viewModel.setNewValueTo(valueString)
            binding.barcodeView.pause()
        }
    }


        override fun onAttach(context: Context) {
        context.appComponent.injectCameraFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("camera","$this create")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){
            user = arguments?.getParcelable("user")
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.flo.collect { it ->
                    codes = CameraUtils.listCodesToListString(it)
                }
            }
        }

        Log.i("CODES","$codes")
        viewModel.valueString.observe(viewLifecycleOwner) {
            if(it != "" || it != "defaultValue") {
                viewModel.createFlowData(it)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.m.collect {
                    list = it
                    Log.i("FLOW","$it")
                }
            }
        }


        binding.userValue.text = user!!.user?.userName
        binding.barcodeView.decoderFactory = DefaultDecoderFactory(viewModel.formats)
        binding.barcodeView.decodeContinuous(callback)
        binding.testCard.text = "${user!!.user},${user!!.adress},${user!!.cabinet}"
    }

    override fun onStart() {
        super.onStart()

        binding.value.setOnClickListener {
            alertDialog.show()
        }

        binding.barcodeView.resume()

        binding.refreshButton.setOnClickListener{
            binding.barcodeView.pause()
            binding.barcodeView.resume()
            viewModel.setNewValueTo("")
            binding.value.text = ""
        }

        binding.switchTorch.setOnCheckedChangeListener{_ , isCheked ->
            Log.d("Scanner","фонарик $this")
            if(isCheked){
                binding.barcodeView.setTorch(true)
            }
            else {
                binding.barcodeView.setTorch(false)
            }

        }


        Log.i("CODE","$codes")
        binding.buttonToNextFragment.setOnClickListener {
            if(list.isNotEmpty()) {
                when(viewModel.checkCard(list)) {
                    StateCard.EMPTY -> useToast("Сканируйте или введите значение")
                    StateCard.ONE_ELEMENT -> startCardFragment(list)
                    StateCard.MULTIPLY_ELEMENTS -> startListFragment(list)
                }
            }
        }
    }

    private fun startCardFragment(card : List<CardEntity>) {
        Log.d("NEXT","$card, $user")
        val bundle = createBundle(this.user!!,card[0].index)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, CardFragmentNew.newInstance(bundle))
            .addToBackStack("papa")
            .commit()
    }

    private fun startListFragment(card : List<CardEntity>) {
        val newIndexList : () -> List<Int> = {
            val list = mutableListOf<Int>()
            card.forEach {
                list.add(it.index)
            }
            list
        }

        val bundle = Bundle()
        bundle.putParcelable("user",user)
        bundle.putIntArray("indexArray",newIndexList.invoke().toIntArray())
        useToast("Обьектов найдено ${card.size}")
        runBlocking {
            delay(1000)
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, ListFragmentNew.newInstance(bundle))
            .addToBackStack("camera")
            .commit()

    }

    private fun alertDialogCreator() : AlertDialog {

        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_edit_value,null)
        val array = ArrayAdapter(requireContext(),R.layout.spinner,codes)
        val auto = layout.findViewById<AutoCompleteTextView>(R.id.alertAutoCompleteTextView)
        auto.setAdapter(array)
        Log.i("CODE","$codes")
        return AlertDialog.Builder(requireContext())
            .setTitle("Редактирование значения")
            .setView(layout)
            .setPositiveButton("ОК") {dialog, _ ->
                binding.value.setText(auto.text.toString())
                valueString = auto.text.toString()
                viewModel.setNewValueTo(auto.text.toString())
                dialog.dismiss()
            }
            .setNegativeButton("Отмена"){dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun createBundle(user : NewUser,cardIndex : Int) : Bundle {
        return Bundle().apply {
            putParcelable("user",user)
            putInt("cardIndex",cardIndex)
        }
    }

    private fun useToast(text : String) {
        Toast.makeText(requireContext(),text, Toast.LENGTH_SHORT).show()
    }

    companion object{
        fun newInstance(bundle : Bundle) : CameraFragmentNew {
            val fragment = CameraFragmentNew()
            fragment.arguments = bundle
            return  fragment
        }
    }

}