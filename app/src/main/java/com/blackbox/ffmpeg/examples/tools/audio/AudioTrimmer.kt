package com.blackbox.ffmpeg.examples.tools.audio

import android.content.Context
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.tools.OutputType
import com.blackbox.ffmpeg.examples.utils.Utils
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import java.io.File
import java.io.IOException

/**
 * Created by Umair_Adil on 19/09/2016.
 */

class AudioTrimmer private constructor(private val context: Context) {

    private var audio: File? = null
    private var callback: FFMpegCallback? = null

    private var startTime = "00:00:00"
    private var endTime = "00:00:00"
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: File): AudioTrimmer {
        this.audio = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioTrimmer {
        this.callback = callback
        return this
    }

    fun setStartTime(startTime: String): AudioTrimmer {
        this.startTime = startTime
        return this
    }

    fun setEndTime(endTime: String): AudioTrimmer {
        this.endTime = endTime
        return this
    }

    fun setOutputPath(output: String): AudioTrimmer {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioTrimmer {
        this.outputFileName = output
        return this
    }

    fun trim() {

        if (audio == null || !audio!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!audio!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        //Trim starting from start Time to End time
        val cmd = arrayOf("-i", audio!!.path, "-ss", startTime, "-t", endTime, "-c", "copy", outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_AUDIO)

                }

                override fun onFailure(message: String?) {
                    if (outputLocation.exists()) {
                        outputLocation.delete()
                    }
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {
                    callback!!.onFinish()
                }
            })

        } catch (e: Exception) {
            callback!!.onFailure(e)
        } catch (e2: FFmpegCommandAlreadyRunningException) {
            callback!!.onNotAvailable(e2)
        }

    }

    companion object {

        val TAG = "AudioTrimmer"

        fun with(context: Context): AudioTrimmer {
            return AudioTrimmer(context)
        }
    }
}
