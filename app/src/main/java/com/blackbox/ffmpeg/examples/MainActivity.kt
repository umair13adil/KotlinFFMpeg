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
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
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
    lateinit var videoSmall1: File
    lateinit var images: Array<File>
    lateinit var font: File

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

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                MovieMaker.with(context!!)
                        .setAudio(audio2)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("movie_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .convert()

                ProgressDialog.show(supportFragmentManager, MovieMaker.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will extract audio as .mp3 from a video
        btn_extract_audio.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                AudioExtractor.with(context!!)
                        .setFile(video2)
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("audio_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .extract()

                ProgressDialog.show(supportFragmentManager, AudioExtractor.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will cut audio for given start and end times
        btn_trim_audio.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                AudioTrimmer.with(context!!)
                        .setFile(audio2)
                        .setStartTime("00:00:05") //Start at 5 seconds
                        .setEndTime("00:00:10") //End at 10 seconds
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("trimmed_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .trim()

                ProgressDialog.show(supportFragmentManager, AudioTrimmer.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will cut video for given start and end times
        btn_trim_video.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                VideoTrimmer.with(context!!)
                        .setFile(video)
                        .setStartTime("00:00:15") // Start from 15 seconds
                        .setEndTime("00:00:25") //End on 25 seconds
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("trimmed_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .trim()

                ProgressDialog.show(supportFragmentManager, VideoTrimmer.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will split video into given time segments
        btn_split_video.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                VideoSplitter.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "video")
                        .setSegmentTime("00:00:15") //Split into 15 seconds segment
                        .setOutputFileName("splittedVideo")
                        .setCallback(this@MainActivity)
                        .split()

                ProgressDialog.show(supportFragmentManager, VideoSplitter.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will resize video in given size
        //Note: Size must be in this format = width:height
        btn_resize_video.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                VideoResizer.with(context!!)
                        .setFile(video2)
                        .setSize("320:480") //320 X 480
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("resized_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .resize()

                ProgressDialog.show(supportFragmentManager, VideoResizer.TAG)

            } else {
                showInProgressToast()
            }
        }

        //This will combine audio with video file.
        //Original Audio of video will be replaced.
        btn_merge_audio_video.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                AudioVideoMerger.with(context!!)
                        .setAudioFile(audio3)
                        .setVideoFile(video2)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager, AudioVideoMerger.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will convert video to GIF.
        btn_video_to_gif.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                VideoToGIF.with(context!!)
                        .setFile(video)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("myGif_" + System.currentTimeMillis() + ".gif")
                        .setDuration("5") //Gif duration
                        .setScale("500") //Size of GIF
                        .setFPS("10") //Frame rate of GIF
                        .setCallback(this@MainActivity)
                        .create()

                ProgressDialog.show(supportFragmentManager, VideoToGIF.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will extract images from video in provided time
        btn_video_to_images.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                VideoToImages.with(context!!)
                        .setFile(video2)
                        .setOutputPath(Utils.outputPath + "images")
                        .setOutputFileName("images")
                        .setInterval("0.25") // Extract image every quarter of second (0.25)
                        .setCallback(this@MainActivity)
                        .extract()

                ProgressDialog.show(supportFragmentManager, VideoToImages.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will add text overlay on video
        btn_text_on_video.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
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


                ProgressDialog.show(supportFragmentManager, TextOnVideo.TAG)
            } else {
                showInProgressToast()
            }
        }


        //This will merge two different audio files
        btn_merge_audio.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            if (!isRunning()) {
                AudioMerger.with(context!!)
                        .setFile1(audio2)
                        .setFile2(audio3)
                        .setOutputPath(Utils.outputPath + "audio")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp3")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager, AudioMerger.TAG)
            } else {
                showInProgressToast()
            }
        }

        //This will merge multiple mp4 video files into single mp4 file
        btn_merge_videos.setOnClickListener {

            //Kill previous running process
            stopRunningProcess()

            val videoList = arrayListOf<File>(videoSmall1, video, video2)

            if (!isRunning()) {
                VideoMerger.with(context!!)
                        .setVideoFiles(videoList)
                        .setOutputPath(Utils.outputPath + "video")
                        .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                        .setCallback(this@MainActivity)
                        .merge()

                ProgressDialog.show(supportFragmentManager, VideoMerger.TAG)
            } else {
                showInProgressToast()
            }
        }
    }

    override fun onProgress(progress: String) {

        //Prints log of progress
        Log.i(TAG, "Running: $progress")

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

        onProgress.run {
            onDismiss()
        }
    }

    override fun onFinish() {

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


    private fun setUpResources() {
        //Copy Audio, Video & Images from resources to Storage Directory
        audio = Utils.copyFileToExternalStorage(R.raw.audio, "audio.mp3", applicationContext)
        audio2 = Utils.copyFileToExternalStorage(R.raw.audio2, "audio2.mp3", applicationContext)
        audio3 = Utils.copyFileToExternalStorage(R.raw.audio3, "audio3.mp3", applicationContext)
        video = Utils.copyFileToExternalStorage(R.raw.video, "video.mp4", applicationContext)
        video2 = Utils.copyFileToExternalStorage(R.raw.video2, "video2.mp4", applicationContext)
        videoSmall1 = Utils.copyFileToExternalStorage(R.raw.video_small_1, "video_small_1.mp4", applicationContext)
        font = Utils.copyFileToExternalStorage(R.font.roboto_black, "myFont.ttf", applicationContext)
        images = arrayOf(
                Utils.copyFileToExternalStorage(R.drawable.image1, "image1.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image2, "image2.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image3, "image3.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image4, "image4.png", applicationContext)
                , Utils.copyFileToExternalStorage(R.drawable.image5, "image5.png", applicationContext))
    }

    private fun stopRunningProcess() {
        FFmpeg.getInstance(this).killRunningProcesses()
    }

    private fun isRunning(): Boolean {
        return FFmpeg.getInstance(this).isFFmpegCommandRunning
    }

    private fun showInProgressToast(){
        Toast.makeText(this, "Operation already in progress! Try again in a while.", Toast.LENGTH_SHORT).show()
    }
}
