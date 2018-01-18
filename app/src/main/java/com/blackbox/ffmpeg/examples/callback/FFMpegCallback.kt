package com.blackbox.ffmpeg.examples.callback

import java.io.File

interface FFMpegCallback {

    fun onProgress(progress: String)

    fun onSuccess(convertedFile: File, type: String)

    fun onFailure(error: Exception)

    fun onNotAvailable(error: Exception)

    fun onFinish()

}