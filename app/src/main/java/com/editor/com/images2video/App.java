package com.editor.com.images2video;

import android.app.Application;

import com.editor.com.images2video.controller.Images2Movie;
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
    }
}