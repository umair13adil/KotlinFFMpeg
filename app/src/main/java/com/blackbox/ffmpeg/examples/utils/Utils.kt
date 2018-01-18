package com.blackbox.ffmpeg.examples.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


object Utils {

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    fun copyFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val pathSDCard = outputPath + resourceName
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            inputStream.toFile(pathSDCard)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return File(pathSDCard)
    }

    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString

        // return timer string
        return finalTimerString
    }
}


