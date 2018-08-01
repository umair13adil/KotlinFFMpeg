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

class VideoToGIF private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var duration = ""
    private var fps = ""
    private var scale = ""

    fun setFile(originalFiles: File): VideoToGIF {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoToGIF {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): VideoToGIF {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoToGIF {
        this.outputFileName = output
        return this
    }

    fun setDuration(output: String): VideoToGIF {
        this.duration = output
        return this
    }

    fun setFPS(output: String): VideoToGIF {
        this.fps = output
        return this
    }

    fun setScale(output: String): VideoToGIF {
        this.scale = output
        return this
    }

    fun create() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        val cmd = arrayOf("-i", video!!.path, "-vf", "scale=" + scale + ":-1", "-t", duration, "-r", fps, outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_GIF)

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

        val TAG = "VideoToGIF"

        fun with(context: Context): VideoToGIF {
            return VideoToGIF(context)
        }
    }
}
