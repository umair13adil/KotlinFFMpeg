package com.blackbox.ffmpeg.examples.tools.image

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

class VideoToImages private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var interval = ""

    fun setFile(originalFiles: File): VideoToImages {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoToImages {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): VideoToImages {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoToImages {
        this.outputFileName = output
        return this
    }

    fun setInterval(output: String): VideoToImages {
        this.interval = output
        return this
    }

    fun extract() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, "")

        val cmd = arrayOf("-i", video!!.path, "-r", interval, outputLocation.path + File.separator + outputFileName + "_%04d.jpg")

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_IMAGES)

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

        val TAG = "VideoToImages"

        fun with(context: Context): VideoToImages {
            return VideoToImages(context)
        }
    }
}
