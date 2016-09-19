package com.editor.com.images2video.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.editor.com.images2video.controller.callback.IConvertCallback;
import com.editor.com.images2video.controller.callback.ILoadCallback;
import com.editor.com.images2video.controller.model.AudioFormat;
import com.editor.com.images2video.utils.Utils;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Images2Movie {

    private static String TAG = Images2Movie.class.getSimpleName();

    private static boolean loaded;

    private Context context;
    private File[] images;
    private AudioFormat format;
    private IConvertCallback callback;

    private Images2Movie(Context context) {
        this.context = context;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void load(Context context, final ILoadCallback callback) {
        try {
            FFmpeg.getInstance(context).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    loaded = true;
                    callback.onSuccess();
                }

                @Override
                public void onFailure() {
                    loaded = false;
                    callback.onFailure(new Exception("Failed to loaded FFmpeg lib"));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            loaded = false;
            callback.onFailure(e);
        } catch (Exception e) {
            loaded = false;
            callback.onFailure(e);
        }
    }

    public static Images2Movie with(Context context) {
        return new Images2Movie(context);
    }

    public Images2Movie setFile(File[] originalFiles) {
        this.images = originalFiles;
        return this;
    }

    public Images2Movie setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public Images2Movie setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert() {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        for (int i = 0; i < images.length; i++) {
            if (Utils.isSupportedFormat(images[i])) {
                if (images == null || !images[i].exists()) {
                    callback.onFailure(new IOException("File not exists"));
                    return;
                }
                if (!images[i].canRead()) {
                    callback.onFailure(new IOException("Can't read the file. Missing permission?"));
                    return;
                }
            }
        }
        final File outputLocation = getConvertedFile();
        //final String[] add_cmd = new String[]{"-loop", "1", "-i", images[0].getPath(), "-c:v", "libx264", "-t", "10", "-pix_fmt", "yuv420p", outputLocation.getPath()};
        final String[] add_cmd = new String[]{"-framerate", "1/15", "-i", Environment.getExternalStorageDirectory() + "/MyImages/img%d.jpg", "-c:v", "libx264", "-r", "4", "-pix_fmt", "yuv420p", outputLocation.getPath()};
        //final String[] add_cmd = new String[]{"-framerate", "1/10", "-i", Environment.getExternalStorageDirectory() + "/MyImages/img%d.jpg", "-c:v", "libx264", "-vf", "fps=25", "-r", "30", "-pix_fmt", "yuv420p", outputLocation.getPath()};

        final String[] cmd = new String[]{"-framerate", "1/30", "-i", Environment.getExternalStorageDirectory() + "/MyImages/img%d.jpg", "-i"
                , Environment.getExternalStorageDirectory() + "/MyImages/sound.wav"
                , "-c:v", "-vf", "fps=5", "-pix_fmt", "yuv420p", "-shortest", outputLocation.getPath()};

        final String[] cmd1 = new String[]{"-loop", "1", "-i",
                images[0].getPath(),"-i"
                , Environment.getExternalStorageDirectory() + "/MyImages/sound.wav"
                , "-c:v", "libx264", "-c:a", "aac","-strict","experimental","-b:a","192k", "-shortest", outputLocation.getPath()};

        final String[] cmd3 = new String[]{"-framerate", "1/2", "-i",
                images[0].getPath(),"-f",
                "lavfi","-i"
                , Environment.getExternalStorageDirectory() + "/MyImages/sound.wav",
                ":loop=999",
                "-strict","experimental",
                "-s","320",
                "-r","30","-vcodec","mpeg4","-b","150k","-ab","48000","-ac","2","-ar","22050","-shortest", outputLocation.getPath()};

        final String[] cmd4 = new String[]{
                "-y","-loop","1","-r","1","-i",images[0].getPath(),"-i",
                Environment.getExternalStorageDirectory() + "/MyImages/sound.mp3",
                "-acodec","aac","-vcodec","mpeg4","-s","480x320",
                "-strict","experimental","-b:a","32k","-shortest","-f","mp4","-r","2", outputLocation.getPath()};

        final String[] cmd5 = new String[]{
                "-y","-loop","1","-r","1","-i",Environment.getExternalStorageDirectory() + "/MyImages/img%d.jpg","-i",
                Environment.getExternalStorageDirectory() + "/MyImages/sound.mp3",
                "-acodec","aac","-vcodec","mpeg4","-s","480x320",
                "-strict","experimental","-b:a","64k","-shortest","-f","mp4","-r","5", outputLocation.getPath()};

        try {
            FFmpeg.getInstance(context).execute(cmd5, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.i(TAG, "onProgress: " + message);
                }

                @Override
                public void onSuccess(String message) {
                    refreshGallery(outputLocation.getPath());
                    callback.onSuccess(outputLocation);

                }

                @Override
                public void onFailure(String message) {
                    if (outputLocation.exists()) {
                        outputLocation.delete();
                    }
                    callback.onFailure(new IOException(message));
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private static File getConvertedFile() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String timeStamp = simpleDateFormat.format(new Date());

        /*String[] f = imageName.split("\\.");
        String filePath = timeStamp + imageName.replace(f[f.length - 1], "mp4");*/

        String path = Environment.getExternalStorageDirectory() + File.separator + "MyImages" + File.separator + timeStamp + "_vid.mp4";
        Log.i(TAG, "Output Path: " + path);
        return new File(path);
    }

    private void refreshGallery(String path) {

        File file = getConvertedFile();
        Log.i(TAG, "Path: " + file.getAbsolutePath());

        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));*/
    }

}