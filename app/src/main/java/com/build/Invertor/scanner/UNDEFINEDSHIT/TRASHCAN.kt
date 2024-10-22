package com.build.Invertor.scanner.UNDEFINEDSHIT

class TRASHCAN {
    /*
    *  cameraExecutor = Executors.newSingleThreadExecutor()
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProviderFuture = ProcessCameraProvider.getInstance(contex)

        cameraProviderFuture.addListener({
            processCameraProvider = cameraProviderFuture.get()

            cameraPreview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }
            processCameraProvider.bindToLifecycle(this,cameraSelector,cameraPreview)

            var imageScanner = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor,BarcodeAnalyzer(object : callback {
                        override fun onScan(list: List<Barcode>) {
                            Toast.makeText(contex,"work",Toast.LENGTH_SHORT)
                        }
                    }))
                }
            processCameraProvider.bindToLifecycle(this,cameraSelector,imageScanner)



        },ContextCompat.getMainExecutor(contex))


    }


    //зона бесполезных функций(работают но с ошибкой и то самый последний(просто не крашит) а ошибка что нельзя сохранить файл так как primary/home и тд)
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ){
        it?.let {
            val pickedDirUri = it
            val documentId = DocumentsContract.getDocumentId(it)
            val newUri = DocumentsContract.buildDocumentUriUsingTree(it,documentId)
            createFileInDirectory(newUri,"example_${getCurrentTime().replace(":", "-")}.json", saveJsonString)
        }
    }
    private val openDirectoryLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            createJsonFolderAndSaveFile(it)
        }
    }
    private val openDirectoryLauncherNew = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            if (isValidUri(it)) {
                createJsonFile(it)
            } else {
                println("Выбранный URI недопустим ${it.toString()}")
            }
        }
    }
    private fun createSaf(){
        openDirectoryLauncher.launch(null)
    }
    private fun createWithSaf(jsonStr : String){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE,"example.json")
        }

        createFileLauncher.launch(null)


    }
    private fun createFileInDirectory(directory : Uri ,fileName :  String,jsonString : String){
        val contentResolver = requireContext().contentResolver
        try{
            val doc = DocumentsContract.createDocument(contentResolver,directory,"application/json","value.json")
            if(doc != null){
                contentResolver.openOutputStream(doc)?.use {file ->
                    file.write(jsonString.toByteArray())
                    file.flush()
                    println("$doc")
                }
            }
            else{
                println("file not create")
            }
        } catch(e : FileNotFoundException){
            e.printStackTrace()
        }


    }
    private fun createJsonFolderAndSaveFile(directoryUri: Uri) {
        val contentResolver = requireContext().contentResolver
        val folderName = "json" // Имя вашей папки
        val fileName = "${user?.id}_${getCurrentTime().replace(":", "-")}.json"
        val jsonStr = saveJsonString // Ваш JSON контент

        // Создаем новую папку
        val newFolderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            directoryUri,
            DocumentsContract.getDocumentId(directoryUri)
        )
        val newDirUri = DocumentsContract.createDocument(
            contentResolver,
            newFolderUri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            folderName
        )

        if (newDirUri != null) {
            // Создаем файл в новой директории
            val fileUri = DocumentsContract.createDocument(
                contentResolver,
                newDirUri,
                "application/json",
                fileName
            )

            if (fileUri != null) {
                // Пишем данные в созданный файл через ContentResolver
                contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    outputStream.write(jsonStr.toByteArray())
                    outputStream.flush()
                    println("Файл успешно сохранён: $fileUri")
                }
            } else {
                println("Не удалось создать файл в выбранной директории.")
            }
        }
    }
    private fun safOpen(){
        openDirectoryLauncherNew.launch(null)
    }
    private fun createJsonFile(directoryUri: Uri) {
        val contentResolver = requireContext().contentResolver
        val fileName = "${user?.id}_${getCurrentTime().replace(":", "-")}.json"
        val jsonStr = saveJsonString // Ваш JSON контент

        try {
            // Создаем JSON-файл
            val fileUri = DocumentsContract.createDocument(contentResolver, directoryUri, "application/json", fileName)

            if (fileUri != null) {
                // Пишем данные в созданный файл
                contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    outputStream.write(jsonStr.toByteArray())
                    outputStream.flush()
                    println("Файл успешно сохранён: $fileUri")
                }
            } else {
                println("Не удалось создать файл в выбранной директории.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Ошибка при создании файла: ${e.message}")
        }
    }
    private fun isValidUri(uri: Uri): Boolean {
        // Проверяем, что URI не указывает на корневую директорию
        val uriString = uri.toString()
        return uriString.contains("primary%3ADocuments") || uriString.contains("primary%3ADownloads")
    }

    *
    * */
}