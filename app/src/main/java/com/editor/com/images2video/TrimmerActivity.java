package com.editor.com.images2video;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.editor.com.images2video.controller.videotrimmer.VideoTrimView;
import com.editor.com.images2video.utils.Constants;

import java.io.File;

public class TrimmerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        VideoTrimView videoTrimmer = ((VideoTrimView) findViewById(R.id.timeLine));
        File video = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER + File.separator + "video.mp4");
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(Uri.parse(video.getAbsolutePath()));
        }
        videoTrimmer.setMaxDuration(180);
    }
}
