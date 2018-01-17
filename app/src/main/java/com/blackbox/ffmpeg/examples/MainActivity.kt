package com.blackbox.ffmpeg.examples

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.tools.*
import com.blackbox.ffmpeg.examples.utils.AudioFormat
import com.blackbox.ffmpeg.examples.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), FFMpegCallback {

    private val TAG = "MainActivity"
    private var context: Context? = null

    lateinit var audio: File
    lateinit var video: File
    lateinit var images: Array<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.context = this

        //Copy Audio, Video & Images from resources to Storage Directory
        audio = Utils.copyFileToExternalStorage(R.raw.audio, "audio.mp3", applicationContext)
        video = Utils.copyFileToExternalStorage(R.raw.video, "video.mp4", applicationContext)

        images = arrayOf(
                Utils.copyFileToExternalStorage(R.drawable.image1, "image1.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image2, "image2.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image3, "image3.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image4, "image4.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image5, "image5.jpg", applicationContext))


        btn_create_movie.setOnClickListener {
            MovieMaker.with(context!!)
                    .setFile(images)
                    .setFormat(AudioFormat.MP3)
                    .setOutputPath(Utils.outputPath + "video")
                    .setOutputFileName("movie.mp4")
                    .setCallback(this@MainActivity)
                    .convert()
        }

        btn_trim_audio.setOnClickListener {
            AudioTrimmer.with(context!!)
                    .setFile(audio)
                    .setStartTime("00:00:00")
                    .setEndTime("00:00:10")
                    .setOutputPath(Utils.outputPath + "audio")
                    .setOutputFileName("trimmedAudio.mp3")
                    .setFormat(AudioFormat.MP3)
                    .setCallback(this@MainActivity)
                    .trim()
        }


        btn_trim_video.setOnClickListener {
            VideoTrimmer.with(context!!)
                    .setFile(video)
                    .setFormat(AudioFormat.MP3)
                    .setOutputPath(Utils.outputPath + "video")
                    .setOutputFileName("trimmedVideo.mp4")
                    .setCallback(this@MainActivity)
                    .trim()
        }

        btn_resize_video.setOnClickListener {
            VideoResizer.with(context!!)
                    .setFile(video)
                    .setOutputPath(Utils.outputPath + "video")
                    .setOutputFileName("resized.mp4")
                    .setCallback(this@MainActivity)
                    .resize()
        }

        btn_merge_audio_video.setOnClickListener {
            AudioVideoMerger.with(context!!)
                    .setAudioFile(audio)
                    .setVideoFile(video)
                    .setFormat(AudioFormat.MP3)
                    .setOutputPath(Utils.outputPath + "video")
                    .setOutputFileName("merged.mp4")
                    .setCallback(this@MainActivity)
                    .merge()
        }


    }

    override fun onProgress(progress: String) {
        txt_output.text = progress
    }

    override fun onSuccess(convertedFile: File) {
        txt_output.text = convertedFile.name
        Log.i(TAG, "Done: " + convertedFile.name)
    }

    override fun onFailure(error: Exception) {
        error.printStackTrace()
        txt_output.text = error.message
        Log.e(TAG, "Error: " + error)
    }

    override fun onNotAvailable(error: Exception) {
        txt_output.text = error.message
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }
}
