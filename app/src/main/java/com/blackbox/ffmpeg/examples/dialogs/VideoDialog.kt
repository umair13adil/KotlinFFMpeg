package com.blackbox.ffmpeg.examples.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatTextView
import android.widget.MediaController
import android.widget.VideoView
import com.blackbox.ffmpeg.examples.R
import com.blackbox.ffmpeg.examples.utils.Utils
import java.io.File


/**
 * Created by umair on 18/01/2018.
 */
class VideoDialog : DialogFragment() {

    companion object {
        val TAG = VideoDialog::javaClass.name

        lateinit var file: File

        fun show(fragmentManager: FragmentManager, file: File) {
            this.file = file
            VideoDialog().show(fragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_video_preview, null)

        val videoView = view.findViewById<VideoView>(R.id.video_view)
        val videoInfo = view.findViewById<AppCompatTextView>(R.id.video_info)

        videoView.setVideoPath(file.path)

        val mediaController = MediaController(activity)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setOnCompletionListener {
            dismiss()
        }

        videoView.setOnPreparedListener {
            @SuppressLint("SetTextI18n")
            videoInfo.text = "Duration: ${Utils.milliSecondsToTimer(videoView.duration.toLong())}\n"
        }

        videoView.start()

        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Preview")
                .setPositiveButton("Cancel") { dialog, which ->
                    dismiss()
                }
                .create()
    }
}
