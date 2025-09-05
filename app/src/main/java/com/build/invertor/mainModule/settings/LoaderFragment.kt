package com.build.invertor.mainModule.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.build.Invertor.R
import com.build.invertor.mainModule.application.App
import com.build.invertor.mainModule.viewModelFactory.DaggerViewModelFactory
import java.io.*
import javax.inject.Inject

class LoaderFragment : Fragment() {

    private lateinit var importButton : Button
    private lateinit var exportButton : Button
    private lateinit var importExcelButton : Button


    private var selectedExcelFileUri : Uri? = null
    private var saveFileName: String? = null
    private var selectedJsonFileUri: Uri? = null

    @Inject
    lateinit var factory : DaggerViewModelFactory

    private val viewModel: LoaderViewModel by viewModels { factory }

    override fun onAttach(context: Context) {
        (requireActivity().application as App).dagger.injectLoaderFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.loader_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        importButton = view.findViewById(R.id.importButton)
        exportButton = view.findViewById(R.id.exportButton)
        importExcelButton = view.findViewById(R.id.excelButton)
    }

    override fun onStart() {
        super.onStart()

        importExcelButton.setOnClickListener{
            provideComplete{
                intentConfiguratorExcel()
            }

            viewModel.loadFromFileExcel(File(requireContext().filesDir,"data.xlsx"))

        }

        importButton.setOnClickListener(){
            provideComplete {
                launchJsonFilePicker()
            }

            viewModel.loadFromFileJson(File(requireContext().filesDir,"jso.json"))

        }



        exportButton.setOnClickListener(){
            provideComplete {
                if(File(requireContext().filesDir,"jso.json").exists()){
                    toExportJsonFileFromInternalStorage("jso.json")
                }else {
                    Toast.makeText(requireContext(),"Вы не загрузили файл", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkVersion() : Boolean{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            true
        }
        else false
    }

    private fun requestPermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat
                .requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            false
        }
        else true
    }

    private fun provideComplete(func : () -> Unit ) : Unit {
        when {
            checkVersion() -> func.invoke()
            requestPermission() -> func.invoke()
            else -> Toast.makeText(requireContext(),"Permission is not granted",Toast.LENGTH_SHORT)
                .show()
                .apply {
                    Log.i("Permission","Permission is not granted")
                }
        }
    }

    private val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            selectedJsonFileUri = result.data?.data
            selectedJsonFileUri?.let { uri ->
                copyJsonFileToInternalStorage(uri)
            }
        } else {

            Log.e("FileUtility", "Some Error Occurred: $result")
        }
    }

    private fun launchJsonFilePicker() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json" // Тип файла, который будем выбирать
        }

        register.launch(intent)
    }

    private fun copyJsonFileToInternalStorage(uri : Uri){

        requireContext().contentResolver.openInputStream(uri)
            .use { input ->
                requireContext().openFileOutput("jso.json",Context.MODE_PRIVATE).use { output->
                    input?.copyTo(output)
                }
                input?.close()
            }

        //ConverterJson(inputStream!!,requireContext()) // TEST 26.07.25

        Toast.makeText(requireContext(),"Импорт файла успешно завершен",Toast.LENGTH_SHORT).show()
        Log.d("FileUtility", "JSON file copied to internal storage ")
    }

    private val saveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val saveUri = result.data?.data
            if (saveUri != null && saveFileName != null) {
                exportJsonFileToUri(saveFileName!!, saveUri)
            } else {
                Log.e("FileUtility", "Save URI или имя файла не найдено!")
                Toast.makeText(requireContext(), "Не удалось сохранить файл.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("FileUtility", "Действие было отменено или произошла ошибка: $result")
        }
    }

    private fun toExportJsonFileFromInternalStorage(fileName: String) {
        // Сохраняем имя файла для дальнейшего использования
        saveFileName = fileName

        // Запуск действия "Сохранить как"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        saveLauncher.launch(intent)
    }

    private fun exportJsonFileToUri(fileName: String, saveUri: Uri) {
        try {
            val inputStream = requireContext().openFileInput(fileName)
            val outputStream = requireContext().contentResolver.openOutputStream(saveUri)

            if (outputStream != null) {
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                Toast.makeText(requireContext(), "Экспорт файла успешно завершен", Toast.LENGTH_SHORT).show()
                Log.d("FileUtility", "Файл успешно экспортирован: $saveUri")
            } else {
                Log.e("FileUtility", "Не удалось открыть OutputStream для Uri: $saveUri")
                Toast.makeText(requireContext(), "Ошибка при открытии OutputStream.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка при экспорте файла.", Toast.LENGTH_SHORT).show()
            Log.e("FileUtility", "Ошибка при экспорте файла: ${e.message}")
        }
    }

    private fun intentConfiguratorExcel() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        launcherExcel.launch(intent)
    }

    private val launcherExcel = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->

        if(result.resultCode == Activity.RESULT_OK){
            selectedExcelFileUri = result.data?.data!!

            selectedExcelFileUri.let {uri ->
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri!!, takeFlags)

                chooseExcel(uri)
            }

        }
        else {
            Log.d("FileWork","Excel файл не удалось найти $this")
        }
    }

    private fun chooseExcel(uri : Uri) {

        val fileName = "data.xlsx"
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = requireContext().openFileOutput(fileName,Context.MODE_PRIVATE)

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }

        Toast.makeText(requireContext(),"Импорт файла успешно завершен",Toast.LENGTH_SHORT).show()
        Log.d("FileWork", "Excel файл скопирован во внутреннее хранилище $fileName")

    }
}