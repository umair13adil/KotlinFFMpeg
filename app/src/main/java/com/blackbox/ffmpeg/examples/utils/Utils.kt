package com.blackbox.ffmpeg.examples.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    fun isSupportedFormat(f: File): Boolean {
        val ext = getFileExtension(f) ?: return false
        try {
            if (SupportedFileFormat.valueOf(ext) != null) {
                return true
            }
        } catch (e: IllegalArgumentException) {
            //Not known enum value
            return false
        }

        return false
    }

    fun getFileExtension(f: File): String? {
        val i = f.name.lastIndexOf('.')
        return if (i > 0) {
            f.name.substring(i + 1)
        } else
            null
    }

    enum class SupportedFileFormat private constructor(val fileSuffix: String) {
        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png")
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

    fun getConvertedFile(name: String): File {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val timeStamp = simpleDateFormat.format(Date())

        val path = outputPath + timeStamp + "_" + name
        return File(path)
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
}


