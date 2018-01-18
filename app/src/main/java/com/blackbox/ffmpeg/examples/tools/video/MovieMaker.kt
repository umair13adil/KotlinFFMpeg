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

class MovieMaker private constructor(private val context: Context) {

    private var images: Array<File>? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: Array<File>): MovieMaker {
        this.images = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): MovieMaker {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): MovieMaker {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): MovieMaker {
        this.outputFileName = output
        return this
    }

    fun convert() {

        for (i in images!!.indices) {
            if (Utils.isSupportedFormat(images!![i])) {
                if (images == null || !images!![i].exists()) {
                    callback!!.onFailure(IOException("File not exists"))
                    return
                }
                if (!images!![i].canRead()) {
                    callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
                    return
                }
            }
        }
        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        val cmd5 = arrayOf("-y", "-analyzeduration", "20M", "-probesize", "20M", "-loop", "1", "-r", "1", "-i", Utils.outputPath + "image%d.jpg", "-i", Utils.outputPath + "audio.mp3", "-acodec", "aac", "-vcodec", "mpeg4", "-s", "480x320", "-strict", "experimental", "-b:a", "64k", "-shortest", "-f", "mp4", "-r", "5", outputLocation.path)

        try {
            FFmpeg.getInstance(context).execute(cmd5, object : ExecuteBinaryResponseHandler() {
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

        private val TAG = "MovieMaker"

        fun with(context: Context): MovieMaker {
            return MovieMaker(context)
        }
    }
}