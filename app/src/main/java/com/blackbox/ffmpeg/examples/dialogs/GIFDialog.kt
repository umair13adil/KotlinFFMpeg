package com.blackbox.ffmpeg.examples.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatTextView
import com.blackbox.ffmpeg.examples.R
import com.blackbox.ffmpeg.examples.utils.Utils
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.File


/**
 * Created by umair on 18/01/2018.
 */
class GIFDialog : DialogFragment() {

    companion object {
        val TAG = GIFDialog::javaClass.name

        lateinit var file: File

        fun show(fragmentManager: FragmentManager, file: File) {
            this.file = file
            GIFDialog().show(fragmentManager, TAG)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_gif_preview, null)

        val gifView = view.findViewById<GifImageView>(R.id.gif_view)
        val videoInfo = view.findViewById<AppCompatTextView>(R.id.video_info)

        val gif = GifDrawable(file.path)
        gifView.setImageDrawable(gif)
        gif.start()

        videoInfo.text = "Duration: ${Utils.milliSecondsToTimer(gif.duration.toLong())}" + " Frames: ${gif.numberOfFrames}"

        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Preview")
                .setPositiveButton("Cancel") { dialog, which ->
                    dismiss()
                }
                .create()
    }
}
