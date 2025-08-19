package Debug.logCatLogger

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class LogCatSaver(private val context : Context) {

    private val executor = Executors.newSingleThreadExecutor()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS",Locale.getDefault())

    fun startLogging() {
        executor.execute{
            try {
                val logFile = getLogFile()
                if (logFile == null) {
                    android.util.Log.e("LogcatSaver", "Не удалось создать файл для логов")
                    return@execute
                }

                if (logFile.exists()) {
                    logFile.delete()
                }
                logFile.createNewFile()


                val process = Runtime.getRuntime().exec("logcat -c")
                process.waitFor()

                val command = "logcat -v time *:E"
                val logcatProcess = Runtime.getRuntime().exec(command)

                BufferedReader(InputStreamReader(logcatProcess.inputStream)).use { reader ->
                    BufferedWriter(FileWriter(logFile, true)).use { writer ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            val currentTime = timeFormat.format(Date())
                            writer.write("[$currentTime] $line\n")
                            writer.flush()
                        }
                    }
                }
            }
            catch (e : Exception){
                Log.e("LogCatSaver","Error",e)
            }
        }
    }

    private fun getLogFile(): File? {
        return try {
            val storageDir = context.getExternalFilesDir(null) ?: context.filesDir
            val fileName = "logs_${dateFormat.format(Date())}.txt"
            File(storageDir, fileName)
        } catch (e: Exception) {
            null
        }
    }

    fun stopLogging() {
        executor.shutdownNow()
    }


}