package com.editor.com.images2video;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.editor.com.images2video.controller.AudioTrimmer;
import com.editor.com.images2video.controller.AudioVideoMerger;
import com.editor.com.images2video.controller.Images2Movie;
import com.editor.com.images2video.controller.VideoTrimmer;
import com.editor.com.images2video.controller.callback.IConvertCallback;
import com.editor.com.images2video.controller.model.AudioFormat;
import com.editor.com.images2video.models.Collection;
import com.editor.com.images2video.utils.Constants;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Context context;

    private ArrayList<Collection> listStore = new ArrayList<Collection>();
    private Collection collection;

    Button btn_make_movie, btn_trim_audio,btn_trim_video,btn_merge;

    File[] myImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        listStore = new ArrayList<Collection>();

        myImages = getMyCollection();

        btn_make_movie = (Button) findViewById(R.id.button);
        btn_trim_audio = (Button) findViewById(R.id.button2);
        btn_trim_video = (Button) findViewById(R.id.button3);
        btn_merge = (Button) findViewById(R.id.button4);


        btn_make_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IConvertCallback callback = new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Log.i(TAG, "Done: " + convertedFile.getName());
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.e(TAG, "Error: " + error);
                    }
                };
                Toast.makeText(context, "Creating Movie...", Toast.LENGTH_SHORT).show();
                Images2Movie.with(context)
                        .setFile(myImages)
                        .setFormat(AudioFormat.MP3)
                        .setCallback(callback)
                        .convert();
            }
        });

        btn_trim_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IConvertCallback callback = new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Log.i(TAG, "Done: " + convertedFile.getName());
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.e(TAG, "Error: " + error);
                    }
                };
                Toast.makeText(context, "Trimming Audio...", Toast.LENGTH_SHORT).show();
                File audio = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER + File.separator + "sound.mp3");
                AudioTrimmer.with(context)
                        .setFile(audio)
                        .setFormat(AudioFormat.MP3)
                        .setCallback(callback)
                        .trim();
            }
        });


        btn_trim_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IConvertCallback callback = new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Log.i(TAG, "Done: " + convertedFile.getName());
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.e(TAG, "Error: " + error);
                    }
                };
                Toast.makeText(context, "Trimming Movie...", Toast.LENGTH_SHORT).show();
                File video = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER + File.separator + "video.mp4");
                VideoTrimmer.with(context)
                        .setFile(video)
                        .setFormat(AudioFormat.MP3)
                        .setCallback(callback)
                        .trim();
            }
        });

        btn_merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IConvertCallback callback = new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Log.i(TAG, "Done: " + convertedFile.getName());
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.e(TAG, "Error: " + error);
                    }
                };
                Toast.makeText(context, "Merging Movie...", Toast.LENGTH_SHORT).show();
                File video = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER + File.separator + "video.mp4");
                File audio = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER + File.separator + "sound.mp3");
                AudioVideoMerger.with(context)
                        .setAudioFile(audio)
                        .setVideoFile(video)
                        .setFormat(AudioFormat.MP3)
                        .setCallback(callback)
                        .merge();
            }
        });


    }

    private File[] getMyCollection() {
        File list[] = new File[0];
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.MY_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (directory.exists()) {
            list = directory.listFiles();
            Log.i(TAG, "File: " + list[0].getName());
        } else {
            Log.i(TAG, "No Directory.");
        }
        return list;
    }
}
