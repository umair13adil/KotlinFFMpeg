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

class AudioMerger private constructor(private val context: Context) {

    private var audio1: File? = null
    private var audio2: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile1(originalFiles: File): AudioMerger {
        this.audio1 = originalFiles
        return this
    }

    fun setFile2(originalFiles: File): AudioMerger {
        this.audio2 = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioMerger {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): AudioMerger {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioMerger {
        this.outputFileName = output
        return this
    }

    fun merge() {

        if ((audio1 == null || !audio1!!.exists()) || (audio2 == null || !audio2!!.exists())) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }

        if (!audio1!!.canRead() || !audio2!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        //Merge two audio files
        val cmd = arrayOf("-y", "-i", audio1!!.path, "-i", audio2!!.path, "-filter_complex", "amix=inputs=2:duration=first:dropout_transition=0", "-codec:a", "libmp3lame", "-q:a", "0", outputLocation.path)

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

        val TAG = "AudioMerger"

        fun with(context: Context): AudioMerger {
            return AudioMerger(context)
        }
    }
}
