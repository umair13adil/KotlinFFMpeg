package com.blackbox.ffmpeg.examples.tools

import android.content.Context
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.utils.AudioFormat
import com.blackbox.ffmpeg.examples.utils.Utils
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import java.io.File
import java.io.IOException

/**
 * Created by Umair_Adil on 19/09/2016.
 */

class AudioVideoMerger private constructor(private val context: Context) {

    private var audio: File? = null
    private var video: File? = null
    private var format: AudioFormat? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setAudioFile(originalFiles: File): AudioVideoMerger {
        this.audio = originalFiles
        return this
    }

    fun setVideoFile(originalFiles: File): AudioVideoMerger {
        this.video = originalFiles
        return this
    }

    fun setFormat(format: AudioFormat): AudioVideoMerger {
        this.format = format
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioVideoMerger {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): AudioVideoMerger {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioVideoMerger {
        this.outputFileName = output
        return this
    }

    fun merge() {

        if (audio == null || !audio!!.exists() || video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!audio!!.canRead() || !video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        //Trim starting from 10 seconds and end at 16 seconds (total time 6 seconds)
        val cmd = arrayOf("-i", video!!.path, "-i", audio!!.path, "-c:v", "copy", "-c:a", "aac", "-strict", "experimental", "-map", "0:v:0", "-map", "1:a:0", "-shortest", outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation)

                }

                override fun onFailure(message: String?) {
                    if (outputLocation.exists()) {
                        outputLocation.delete()
                    }
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {}
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        }catch (e2: FFmpegCommandAlreadyRunningException) {
            callback!!.onNotAvailable(e2)
        }

    }

    companion object {

        private val TAG = "AudioVideoMerger"

        fun with(context: Context): AudioVideoMerger {
            return AudioVideoMerger(context)
        }
    }
}
