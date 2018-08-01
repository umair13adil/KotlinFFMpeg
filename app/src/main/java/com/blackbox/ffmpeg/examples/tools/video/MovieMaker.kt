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
    private var audio: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: Array<File>): MovieMaker {
        this.images = originalFiles
        return this
    }

    fun setAudio(originalFiles: File): MovieMaker {
        this.audio = originalFiles
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

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        //Here the images are from image1.png to image5.png
        //framerate 1/3.784 means, each image runs for 3.784 seconds
        //c:v libx264: video codec H.264
        //r 30: output video fps 30
        //pix_fmt yuv420p: output video pixel format
        //c:a aac: encode the audio using aac
        //shortest: end the video as soon as the audio is done.
        val cmd5 = arrayOf("-analyzeduration", "1M", "-probesize", "1M", "-y", "-framerate", "1/3.79", "-i", Utils.outputPath + "image%d.png", "-i", audio!!.path, "-c:v", "libx264", "-r", "30", "-pix_fmt", "yuv420p", "-c:a", "aac", "-shortest", outputLocation.path)

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

        val TAG = "MovieMaker"

        fun with(context: Context): MovieMaker {
            return MovieMaker(context)
        }
    }
}