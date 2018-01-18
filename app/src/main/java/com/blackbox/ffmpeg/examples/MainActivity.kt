package com.blackbox.ffmpeg.examples

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.dialogs.AudioDialog
import com.blackbox.ffmpeg.examples.dialogs.ProgressDialog
import com.blackbox.ffmpeg.examples.dialogs.VideoDialog
import com.blackbox.ffmpeg.examples.tools.OutputType
import com.blackbox.ffmpeg.examples.tools.audio.AudioTrimmer
import com.blackbox.ffmpeg.examples.tools.image.VideoToGIF
import com.blackbox.ffmpeg.examples.tools.image.VideoToImages
import com.blackbox.ffmpeg.examples.tools.video.*
import com.blackbox.ffmpeg.examples.utils.AudioFormat
import com.blackbox.ffmpeg.examples.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), FFMpegCallback {

    private val TAG = "MainActivity"
    private var context: Context? = null

    lateinit var audio: File
    lateinit var audio2: File
    lateinit var video: File
    lateinit var images: Array<File>
    lateinit var font: File

    var inProgress = false

    interface ProgressPublish {
        fun onProgress(progress: String)

        fun onDismiss()
    }

    companion object {
        lateinit var onProgress: ProgressPublish

        fun setProgressListener(onProgress: ProgressPublish) {
            this.onProgress = onProgress
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.context = this

        //Copy Audio, Video & Images from resources to Storage Directory
        audio = Utils.copyFileToExternalStorage(R.raw.audio, "audio.mp3", applicationContext)
        audio2 = Utils.copyFileToExternalStorage(R.raw.audio, "audio2.mp3", applicationContext)
        video = Utils.copyFileToExternalStorage(R.raw.video, "video.mp4", applicationContext)
        font = Utils.copyFileToExternalStorage(R.font.roboto_black, "myFont.ttf", applicationContext)

        images = arrayOf(
                Utils.copyFileToExternalStorage(R.drawable.image1, "image1.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image2, "image2.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image3, "image3.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image4, "image4.jpg", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image5, "image5.jpg", applicationContext))


        btn_create_movie.setOnClickListener {

            if (!inProgress) {
                MovieMaker.with(context!!)
                        .setFile(images)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("movie_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .convert()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_trim_audio.setOnClickListener {

            if (!inProgress) {
                AudioTrimmer.with(context!!)
                        .setFile(audio2)
                        .setStartTime("00:00:05")
                        .setEndTime("00:00:20")
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("trimmed_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .trim()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }


        btn_trim_video.setOnClickListener {

            if (!inProgress) {
                VideoTrimmer.with(context!!)
                        .setFile(video)
                        .setStartTime("00:00:15") // Start from 15 seconds
                        .setEndTime("00:00:25") //End on 25 seconds
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("trimmed_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .trim()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_split_video.setOnClickListener {

            if (!inProgress) {
                VideoSplitter.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "video")
                        .setSegmentTime("00:00:15") //Split into 15 seconds segment
                        .setOutputFileName("splittedVideo")
                        .setCallback(this@MainActivity)
                        .split()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_resize_video.setOnClickListener {

            if (!inProgress) {
                VideoResizer.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("resized_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .resize()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_merge_audio_video.setOnClickListener {

            if (!inProgress) {
                AudioVideoMerger.with(context!!)
                        .setAudioFile(audio)
                        .setVideoFile(video)
                        .setFormat(AudioFormat.MP3)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_video_to_gif.setOnClickListener {

            if (!inProgress) {
                VideoToGIF.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("myGif_" + System.currentTimeMillis() + ".gif")
                        .setDuration("5")
                        .setScale("500")
                        .setFPS("10")
                        .setCallback(this@MainActivity)
                        .create()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_video_to_images.setOnClickListener {

            if (!inProgress) {
                VideoToImages.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("images")
                        .setInterval("0.25") // Extact image every quarter of second (0.25)
                        .setCallback(this@MainActivity)
                        .extract()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_text_on_video.setOnClickListener {

            if (!inProgress) {
                TextOnVideo.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("textOnVideo_" + System.currentTimeMillis() + ".mp4")
                        .setFont(font)
                        .setText("This is my text that is displayed on this Video!!")
                        .setColor("#50b90e")
                        .setSize("34")
                        .addBorder(true)
                        .setPosition(TextOnVideo.POSITION_CENTER_BOTTOM)
                        .setCallback(this@MainActivity)
                        .draw()


                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onProgress(progress: String) {

        Log.i(TAG, "Running: $progress")

        //Set this flag to disable any other action until first one is completed
        inProgress = true

        onProgress.run {
            onProgress(progress)
        }
    }

    override fun onSuccess(convertedFile: File, type: String) {
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()

        when {
            type.equals(OutputType.TYPE_VIDEO) -> VideoDialog.show(supportFragmentManager, convertedFile)
            type.equals(OutputType.TYPE_AUDIO) -> AudioDialog.show(supportFragmentManager, convertedFile)
            type.equals(OutputType.TYPE_GIF) -> VideoDialog.show(supportFragmentManager, convertedFile)
        }
    }

    override fun onFailure(error: Exception) {
        error.printStackTrace()
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()

        //Allow other actions
        inProgress = false


        onProgress.run {
            onDismiss()
        }
    }

    override fun onFinish() {

        //Allow other actions
        inProgress = false

        onProgress.run {
            onDismiss()
        }
    }

    override fun onNotAvailable(error: Exception) {
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()

        onProgress.run {
            onDismiss()
        }
    }
}
