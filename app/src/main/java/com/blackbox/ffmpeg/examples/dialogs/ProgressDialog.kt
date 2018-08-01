package com.blackbox.ffmpeg.examples.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatTextView
import android.widget.Button
import com.blackbox.ffmpeg.examples.MainActivity
import com.blackbox.ffmpeg.examples.R
import com.github.hiteshsondhi88.libffmpeg.FFmpeg

/**
 * Created by umair on 18/01/2018.
 */
class ProgressDialog : DialogFragment(), MainActivity.ProgressPublish {

    var text: String = ""

    lateinit var progress_text: AppCompatTextView
    lateinit var name_text: AppCompatTextView
    lateinit var stopButton: Button

    override fun onProgress(progress: String) {
        this.text = progress
        progress_text.text = text
    }

    override fun onDismiss() {
        dismiss()
    }

    companion object {
        val TAG = ProgressDialog::javaClass.name
        var name: String = ""

        fun show(fragmentManager: FragmentManager, name: String) {
            ProgressDialog().show(fragmentManager, TAG)
            this.name = name
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        MainActivity.setProgressListener(this)

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_progress, null)

        progress_text = view.findViewById<AppCompatTextView>(R.id.txt_progress)
        name_text = view.findViewById<AppCompatTextView>(R.id.txt_name)
        stopButton = view.findViewById<Button>(R.id.stop)

        progress_text.text = text
        name_text.text = name

        stopButton.setOnClickListener {
            FFmpeg.getInstance(activity!!).killRunningProcesses()
            dismiss()
        }

        return AlertDialog.Builder(activity)
                .setCancelable(false)
                .setView(view)
                .setTitle("Running FFMpeg Commands")
                .create()
    }
}
