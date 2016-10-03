package com.editor.com.images2video.utils;

import android.app.Application;

import com.editor.com.images2video.controller.AudioTrimmer;
import com.editor.com.images2video.controller.AudioVideoMerger;
import com.editor.com.images2video.controller.Images2Movie;
import com.editor.com.images2video.controller.VideoResizer;
import com.editor.com.images2video.controller.VideoTrimmer;
import com.editor.com.images2video.controller.callback.ILoadCallback;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Images2Movie.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                error.printStackTrace();
            }
        });

        AudioTrimmer.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                error.printStackTrace();
            }
        });

        VideoTrimmer.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                error.printStackTrace();
            }
        });

        AudioVideoMerger.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                error.printStackTrace();
            }
        });

        VideoResizer.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                error.printStackTrace();
            }
        });
    }
}