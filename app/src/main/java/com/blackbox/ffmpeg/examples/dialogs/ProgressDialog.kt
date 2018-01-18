package com.blackbox.ffmpeg.examples.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatTextView
import com.blackbox.ffmpeg.examples.MainActivity
import com.blackbox.ffmpeg.examples.R

/**
 * Created by umair on 18/01/2018.
 */
class ProgressDialog : DialogFragment(), MainActivity.ProgressPublish {

    var text: String = ""
    lateinit var progress_text: AppCompatTextView

    override fun onProgress(progress: String) {
        this.text = progress
        progress_text.text = text
    }

    override fun onDismiss() {
        dismiss()
    }

    companion object {
        val TAG = ProgressDialog::javaClass.name

        fun show(fragmentManager: FragmentManager) {
            ProgressDialog().show(fragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        MainActivity.setProgressListener(this)

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_progress, null)

        progress_text = view.findViewById<AppCompatTextView>(R.id.txt_progress)
        progress_text.text = text

        return AlertDialog.Builder(activity)
                .setCancelable(false)
                .setView(view)
                .setTitle("Progress")
                .create()
    }
}
