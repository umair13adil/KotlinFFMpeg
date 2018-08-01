package com.blackbox.ffmpeg.examples.tools.video

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

class VideoTrimmer private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var startTime = "00:00:00"
    private var endTime = "00:00:00"

    fun setFile(originalFiles: File): VideoTrimmer {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoTrimmer {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): VideoTrimmer {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoTrimmer {
        this.outputFileName = output
        return this
    }

    fun setStartTime(startTime: String): VideoTrimmer {
        this.startTime = startTime
        return this
    }

    fun setEndTime(endTime: String): VideoTrimmer {
        this.endTime = endTime
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

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        val cmd = arrayOf("-i", video!!.path, "-ss", startTime, "-t", endTime, "-c", "copy", outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_VIDEO)

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

        val TAG = "VideoTrimmer"

        fun with(context: Context): VideoTrimmer {
            return VideoTrimmer(context)
        }
    }
}
