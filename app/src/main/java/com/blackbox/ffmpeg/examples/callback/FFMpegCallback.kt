package com.blackbox.ffmpeg.examples.callback

import java.io.File

interface FFMpegCallback {

    fun onProgress(progress: String)

    fun onSuccess(convertedFile: File)

    fun onFailure(error: Exception)

}