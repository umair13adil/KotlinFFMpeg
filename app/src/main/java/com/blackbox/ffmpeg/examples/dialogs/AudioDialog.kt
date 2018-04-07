package com.blackbox.ffmpeg.examples.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatTextView
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import com.blackbox.ffmpeg.examples.R
import com.blackbox.ffmpeg.examples.utils.Utils
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by umair on 18/01/2018.
 */
class AudioDialog : DialogFragment() {

    var startTime: Long = 0
    var finalTime: Long = 0
    lateinit var mediaPlayer: MediaPlayer
    lateinit var countDownTimer: CountDownTimer

    companion object {
        val TAG = AudioDialog::javaClass.name

        lateinit var file: File

        fun show(fragmentManager: FragmentManager, file: File) {
            this.file = file
            AudioDialog().show(fragmentManager, TAG)
        }
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_audio_preview, null)

        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        seekBar.isEnabled = false

        val info = view.findViewById<AppCompatTextView>(R.id.video_info)

        //set audio file to MediaPlayer
        if (file != null) {
            mediaPlayer = MediaPlayer.create(activity, Uri.fromFile(file))
            mediaPlayer.start()
        }

        finalTime = mediaPlayer.duration.toLong()
        startTime = mediaPlayer.currentPosition.toLong()
        seekBar.max = finalTime.toInt()

        //Close this dialog on completion
        mediaPlayer.setOnCompletionListener {
            dismiss()
        }

        seekBar.setMax(mediaPlayer.duration)

        //This will diable seekbar touch events
        seekBar.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return true
            }
        })

        //This will set current progress on seekbar
        countDownTimer = object : CountDownTimer(mediaPlayer.duration.toLong(), 250) {
            override fun onTick(millisUntilFinished: Long) {

                startTime = mediaPlayer.currentPosition.toLong()
                info.text = "Duration: ${Utils.milliSecondsToTimer(mediaPlayer.duration.toLong())}\n" + getTime()
                seekBar.progress = seekBar.progress + 250

            }

            override fun onFinish() {
            }
        }.start()

        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Preview")
                .setPositiveButton("Cancel") { dialog, which ->
                    dismiss()
                }
                .create()
    }

    fun getTime(): String {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)))

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        countDownTimer.cancel()
    }
}
