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

class VideoSplitter private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var segementTime = "00:00:00"

    fun setFile(originalFiles: File): VideoSplitter {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoSplitter {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): VideoSplitter {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoSplitter {
        this.outputFileName = output
        return this
    }

    fun setSegmentTime(startTime: String): VideoSplitter {
        this.segementTime = startTime
        return this
    }

    fun split() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = Utils.getConvertedFile(outputPath, "")

        val cmd = arrayOf("-i", video!!.path, "-c", "copy", "-map", "0", "-segment_time", segementTime, "-f", "segment", outputLocation.path + File.separator + outputFileName + "%03d.mp4")

        try {
            FFmpeg.getInstance(context).execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    Utils.refreshGallery(outputLocation.path, context)
                    callback!!.onSuccess(outputLocation, OutputType.TYPE_MULTIPLE_VIDEO)

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

        val TAG = "VideoSplitter"

        fun with(context: Context): VideoSplitter {
            return VideoSplitter(context)
        }
    }
}
