package com.blackbox.ffmpeg.examples.tools

import android.content.Context
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.utils.AudioFormat
import com.blackbox.ffmpeg.examples.utils.Utils
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File
import java.io.IOException

/**
 * Created by Umair_Adil on 19/09/2016.
 */

class VideoTrimmer private constructor(private val context: Context) {
    private var video: File? = null
    private var format: AudioFormat? = null
    private var callback: FFMpegCallback? = null

    fun setFile(originalFiles: File): VideoTrimmer {
        this.video = originalFiles
        return this
    }

    fun setFormat(format: AudioFormat): VideoTrimmer {
        this.format = format
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoTrimmer {
        this.callback = callback
        return this
    }

    fun trim() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile("trimmed.mp4")

        //Trim starting from 10 seconds and end at 16 seconds (total time 6 seconds)
        val cmd = arrayOf("-i", video!!.path, "-ss", "00:00:03", "-t", "00:00:08", "-async", "1", outputLocation.path)

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
        }

    }

    companion object {

        private val TAG = "VideoTrimmer"

        fun with(context: Context): VideoTrimmer {
            return VideoTrimmer(context)
        }
    }
}
