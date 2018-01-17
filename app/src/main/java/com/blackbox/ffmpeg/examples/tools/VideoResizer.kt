package com.blackbox.ffmpeg.examples.tools

import android.content.Context
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.utils.Utils
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File
import java.io.IOException

/**
 * Created by Umair_Adil on 03/10/2016.
 */

class VideoResizer private constructor(private val context: Context) {
    private var video: File? = null
    private var callback: FFMpegCallback? = null

    fun setFile(originalFiles: File): VideoResizer {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoResizer {
        this.callback = callback
        return this
    }

    fun resize() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile("resized.mp4")


        //final String[] cmd = new String[]{"-i", video.getPath(), "-vf", "scale=320:240",outputLocation.getPath(),"-hide_banner"};
        val cmd = arrayOf("-i", video!!.path, "-vf", "scale=1920:1080", outputLocation.path, "-hide_banner")

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


        private val TAG = "VideoResizer"

        fun with(context: Context): VideoResizer {
            return VideoResizer(context)
        }
    }
}
