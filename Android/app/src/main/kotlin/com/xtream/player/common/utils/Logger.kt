// Android/app/src/main/kotlin/com/xtream/player/common/utils/Logger.kt
package com.xtream.player.common.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Logger {
    private const val TAG = "XtreamPlayer"
    private lateinit var logFile: File
    private var isEnabled = true
    
    fun init(context: Context) {
        isEnabled = BuildConfig.DEBUG
        logFile = File(context.filesDir, "app.log")
        
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }
    
    fun debug(message: String) = log("DEBUG", message)
    fun info(message: String) = log("INFO", message)
    fun warning(message: String) = log("WARNING", message)
    fun error(message: String) = log("ERROR", message)
    fun error(message: String, throwable: Throwable) {
        log("ERROR", message)
        Log.e(TAG, throwable.stackTraceToString())
    }
    
    private fun log(level: String, message: String) {
        if (!isEnabled) return
        
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val logMessage = "[$timestamp] [$level] $message"
        
        // Console logging
        when (level) {
            "DEBUG" -> Log.d(TAG, message)
            "INFO" -> Log.i(TAG, message)
            "WARNING" -> Log.w(TAG, message)
            "ERROR" -> Log.e(TAG, message)
        }
        
        // File logging
        try {
            logFile.appendText("$logMessage\n")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    fun exportLogs(): File? {
        return if (::logFile.isInitialized && logFile.exists()) {
            logFile
        } else {
            null
        }
    }
    
    fun clearLogs() {
        try {
            if (::logFile.isInitialized && logFile.exists()) {
                logFile.writeText("")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear logs", e)
        }
    }
}
