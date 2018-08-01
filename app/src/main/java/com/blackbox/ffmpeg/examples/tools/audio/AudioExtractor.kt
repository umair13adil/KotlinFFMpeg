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

class AudioExtractor private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: File): AudioExtractor {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): AudioExtractor {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): AudioExtractor {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): AudioExtractor {
        this.outputFileName = output
        return this
    }

    fun extract() {

        video?.let {

            if (!video!!.exists()) {
                callback!!.onFailure(IOException("File not exists"))
                return
            }
            if (!video!!.canRead()) {
                callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
                return
            }

            val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

            //Create Audio File with 192Kbps
            //Select .mp3 format
            val cmd = arrayOf("-i", video!!.path, "-vn", "-ar", "44100", "-ac", "2", "-ab", "192", "-f", "mp3", outputLocation.path)

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

    }

    companion object {

        val TAG = "AudioExtractor"

        fun with(context: Context): AudioExtractor {
            return AudioExtractor(context)
        }
    }
}
