package com.blackbox.ffmpeg.examples

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blackbox.ffmpeg.examples.callback.FFMpegCallback
import com.blackbox.ffmpeg.examples.dialogs.AudioDialog
import com.blackbox.ffmpeg.examples.dialogs.GIFDialog
import com.blackbox.ffmpeg.examples.dialogs.ProgressDialog
import com.blackbox.ffmpeg.examples.dialogs.VideoDialog
import com.blackbox.ffmpeg.examples.tools.OutputType
import com.blackbox.ffmpeg.examples.tools.audio.AudioExtractor
import com.blackbox.ffmpeg.examples.tools.audio.AudioMerger
import com.blackbox.ffmpeg.examples.tools.audio.AudioTrimmer
import com.blackbox.ffmpeg.examples.tools.image.VideoToGIF
import com.blackbox.ffmpeg.examples.tools.image.VideoToImages
import com.blackbox.ffmpeg.examples.tools.video.*
import com.blackbox.ffmpeg.examples.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), FFMpegCallback {

    private val TAG = "MainActivity"
    private var context: Context? = null

    lateinit var audio: File
    lateinit var audio2: File
    lateinit var audio3: File
    lateinit var video: File
    lateinit var video2: File
    lateinit var images: Array<File>
    lateinit var font: File

    var inProgress = false

    //Used to publish progress to dialog fragment
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

        //Ask for permissions
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2222)
        } else if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2222)
        }

        //This will copy resources to storage directory
        setUpResources()

        //This will create movie using audio & images saved in output directory name 'image%d.png'
        btn_create_movie.setOnClickListener {

            if (!inProgress) {
                MovieMaker.with(context!!)
                        .setAudio(audio2)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("movie_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .convert()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will extract audio as .mp3 from a video
        btn_extract_audio.setOnClickListener {

            if (!inProgress) {
                AudioExtractor.with(context!!)
                        .setFile(video2)
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("audio_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .extract()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will cut audio for given start and end times
        btn_trim_audio.setOnClickListener {

            if (!inProgress) {
                AudioTrimmer.with(context!!)
                        .setFile(audio2)
                        .setStartTime("00:00:05") //Start at 5 seconds
                        .setEndTime("00:00:10") //End at 10 seconds
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("trimmed_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .trim()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will cut video for given start and end times
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

        //This will split video into given time segments
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

        //This will resize video in given size
        //Note: Size must be in this format = width:height
        btn_resize_video.setOnClickListener {

            if (!inProgress) {
                VideoResizer.with(context!!)
                        .setFile(video2)
                        .setSize("320:480") //320 X 480
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("resized_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .resize()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will combine audio with video file.
        //Original Audio of video will be replaced.
        btn_merge_audio_video.setOnClickListener {

            if (!inProgress) {
                AudioVideoMerger.with(context!!)
                        .setAudioFile(audio3)
                        .setVideoFile(video2)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will convert video to GIF.
        btn_video_to_gif.setOnClickListener {

            if (!inProgress) {
                VideoToGIF.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("myGif_" + System.currentTimeMillis() + ".gif")
                        .setDuration("5") //Gif duration
                        .setScale("500") //Size of GIF
                        .setFPS("10") //Frame rate of GIF
                        .setCallback(this@MainActivity)
                        .create()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will extract images from video in provided time
        btn_video_to_images.setOnClickListener {

            if (!inProgress) {
                VideoToImages.with(context!!)
                        .setFile(video2)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("images")
                        .setInterval("0.25") // Extract image every quarter of second (0.25)
                        .setCallback(this@MainActivity)
                        .extract()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }

        //This will add text overlay on video
        btn_text_on_video.setOnClickListener {

            if (!inProgress) {
                TextOnVideo.with(context!!)
                        .setFile(video2)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("textOnVideo_" + System.currentTimeMillis() + ".mp4")
                        .setFont(font) //Font .ttf of text
                        .setText("Text Displayed on Video!!") //Text to be displayed
                        .setColor("#50b90e") //Color of Text
                        .setSize("34") //Size of text
                        .addBorder(true) //This will add background with border on text
                        .setPosition(TextOnVideo.POSITION_CENTER_BOTTOM) //Can be selected
                        .setCallback(this@MainActivity)
                        .draw()


                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }


        //This will merge two different audio files
        btn_merge_audio.setOnClickListener {

            if (!inProgress) {
                AudioMerger.with(context!!)
                        .setFile1(audio2)
                        .setFile2(audio3)
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager)
            } else {
                Toast.makeText(this, "Error: Operation already in progress!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onProgress(progress: String) {

        //Prints log of progress
        Log.i(TAG, "Running: $progress")

        //Set this flag to disable any other action until first one is completed
        inProgress = true

        onProgress.run {
            onProgress(progress)
        }
    }

    override fun onSuccess(convertedFile: File, type: String) {
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()

        //Show preview of outputs for after checking type of media
        when {
            type.equals(OutputType.TYPE_VIDEO) -> VideoDialog.show(supportFragmentManager, convertedFile)
            type.equals(OutputType.TYPE_AUDIO) -> AudioDialog.show(supportFragmentManager, convertedFile)
            type.equals(OutputType.TYPE_GIF) -> GIFDialog.show(supportFragmentManager, convertedFile)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2222) {
            setUpResources()
        }
    }


    fun setUpResources() {
        //Copy Audio, Video & Images from resources to Storage Directory
        audio = Utils.copyFileToExternalStorage(R.raw.audio, "audio.mp3", applicationContext)
        audio2 = Utils.copyFileToExternalStorage(R.raw.audio2, "audio2.mp3", applicationContext)
        audio3 = Utils.copyFileToExternalStorage(R.raw.audio3, "audio3.mp3", applicationContext)
        video = Utils.copyFileToExternalStorage(R.raw.video, "video.mp4", applicationContext)
        video2 = Utils.copyFileToExternalStorage(R.raw.video2, "video2.mp4", applicationContext)
        font = Utils.copyFileToExternalStorage(R.font.roboto_black, "myFont.ttf", applicationContext)
        images = arrayOf(
                Utils.copyFileToExternalStorage(R.drawable.image1, "image1.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image2, "image2.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image3, "image3.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image4, "image4.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image5, "image5.png", applicationContext))
    }
}
