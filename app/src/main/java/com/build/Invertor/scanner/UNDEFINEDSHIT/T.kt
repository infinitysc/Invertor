package com.build.Invertor.scanner.UNDEFINEDSHIT
/*
class T {

    * Deprecated("устарело")
    private fun workWithCache() {
        val file = cacheFileListOpener()
        val gsonEngine = Gson()
        if (file != null) {
            val listCardFromFile = cacheListSaver(gsonEngine, file)
            if (listCardFromFile != null) {
                val listToJson = listAppend(listCardFromFile, saveCard!!)
                val jsonStr = gsonEngine.toJson(listToJson)
                val cacheFile = File(activityContext.cacheDir, "${user?.id}.json")
                toSave(jsonStr, cacheFile)
            }
        }
        else {
            val cacheFile = File(activityContext.cacheDir,"${user?.id}.json")
            val newList = listOf(saveCard)
            val jsonList = gsonEngine.toJson(newList)
            toSave(jsonList,cacheFile)
        }
    }
    @Deprecated("устарело")
    private fun toSave(str : String,cacheFile : File) {
        try{
            FileOutputStream(cacheFile).use {
                it.write(str.toByteArray())
                it.flush()
                it.close()
            }
        }catch (e : IOException){
            e.printStackTrace()
        }
    }
    @Deprecated("устарело")
    private fun cacheFileListOpener(context : Context = activityContext) : File? {
        val cacheDir = context.cacheDir
        val files : List<File>? = cacheDir.listFiles()?.toList()
        if(files != null && files.isNotEmpty()){
            files.forEach{
                if(it.name == "${user?.id}.json"){
                    return it
                }
                else {
                    it.delete()
                }
            }
        }
        return null
    }
    @Deprecated("Устарело")
    private fun cacheListSaver(gson : Gson,file : File) : List<CardInventory>? {
        return if(file != null){
            val rawJsonString = file.readText()
            val typeOfList = object : TypeToken<List<CardInventory>>() {}.type
            val list = gson.fromJson<List<CardInventory>>(rawJsonString,typeOfList)
            list
        } else {
            null
        }
    }
    @Deprecated("устарело")
    private fun listAppend(startList : List<CardInventory>,card : CardInventory) : List<CardInventory> {
        val newMutList = startList.toMutableList()
        newMutList.add(card)
        return newMutList
    }
    *
    *
    *  @Deprecated("устраело")
    fun launchBaseDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher.launch(intent)
    }
    @Deprecated("устарело")
    private fun getBaseDocumentTreeUri(): Uri? {
        val preferences = requireContext().getSharedPreferences("com.build.Invertor", Context.MODE_PRIVATE)
        val uriString = preferences.getString("filestorageuri", null)
        return uriString?.let { Uri.parse(it) }
    }



    @Deprecated("устарело")
   private fun writeJsonFile(fileName: String, jsonContent: String) {
        try {
            // Получаем сохраненный URI директории
            val uri = getBaseDocumentTreeUri()
            if (uri != null) {
                val directory = DocumentFile.fromTreeUri(requireContext(), uri)
                // Создаем JSON-файл
                val file = directory?.createFile("application/json", fileName)

                // Записываем данные в файл
                val pfd = requireContext().contentResolver.openFileDescriptor(file!!.uri, "w")
                FileOutputStream(pfd?.fileDescriptor).use { fos ->
                    fos.write(jsonContent.toByteArray())
                    fos.flush()
                }
                println("Файл успешно сохранён: ${file.uri}")
            } else {
                println("URI директории не найден!")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    *     private var baseDocumentTreeUri: Uri? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            baseDocumentTreeUri = result.data?.data
            baseDocumentTreeUri?.let { uri ->
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)

                val preferences = requireContext().getSharedPreferences("com.build.Invertor", Context.MODE_PRIVATE)
                preferences.edit().putString("filestorageuri", uri.toString()).apply()
                writeJsonFile("${user?.userName?.replace(" ","_")}.json", saveJsonString)
            }
        } else {
            Log.e("FileUtility", "Some Error Occurred: $result")
        }
    }

}

    */
