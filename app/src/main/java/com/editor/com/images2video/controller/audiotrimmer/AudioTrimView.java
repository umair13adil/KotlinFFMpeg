/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.editor.com.images2video.controller.audiotrimmer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.editor.com.images2video.R;
import com.editor.com.images2video.controller.audiotrimmer.interfaces.OnK4LVideoListener;
import com.editor.com.images2video.controller.audiotrimmer.interfaces.OnProgressVideoListener;
import com.editor.com.images2video.controller.audiotrimmer.interfaces.OnRangeSeekBarListener;
import com.editor.com.images2video.controller.audiotrimmer.interfaces.OnTrimVideoListener;
import com.editor.com.images2video.controller.audiotrimmer.utils.BackgroundExecutor;
import com.editor.com.images2video.controller.audiotrimmer.utils.UiThreadExecutor;
import com.editor.com.images2video.controller.audiotrimmer.view.ProgressBarView;
import com.editor.com.images2video.controller.audiotrimmer.view.RangeSeekBarView;
import com.editor.com.images2video.controller.audiotrimmer.view.Thumb;
import com.editor.com.images2video.controller.audiotrimmer.view.TimeLineView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class AudioTrimView extends FrameLayout {

    private static final String TAG = AudioTrimView.class.getSimpleName();
    private static final int MIN_TIME_FRAME = 1000;
    private static final int SHOW_PROGRESS = 2;

    private SeekBar mHolderTopView;
    private RangeSeekBarView mRangeSeekBarView;
    private View mTimeInfoContainer;
    private TextView mTextSize;
    private TextView mTextTimeFrame;
    private TextView mTextTime;
    private TimeLineView mTimeLineView;

    private ProgressBarView mVideoProgressIndicator;
    private Uri mSrc;
    private String mFinalPath;

    private int mMaxDuration;
    private List<OnProgressVideoListener> mListeners;

    private OnTrimVideoListener mOnTrimVideoListener;
    private OnK4LVideoListener mOnK4LVideoListener;

    private int mDuration = 0;
    private int mTimeVideo = 0;
    private int mStartPosition = 0;
    private int mEndPosition = 0;

    private long mOriginSizeFile;
    private boolean mResetSeekBar = true;
    private final MessageHandler mMessageHandler = new MessageHandler(this);

    public AudioTrimView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioTrimView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.audio_view_time_line, this, true);

        mHolderTopView = ((SeekBar) findViewById(R.id.handlerTop));
        mVideoProgressIndicator = ((ProgressBarView) findViewById(R.id.timeVideoView));
        mRangeSeekBarView = ((RangeSeekBarView) findViewById(R.id.timeLineBar));
        mTimeInfoContainer = findViewById(R.id.timeText);
        mTextSize = ((TextView) findViewById(R.id.textSize));
        mTextTimeFrame = ((TextView) findViewById(R.id.textTimeSelection));
        mTextTime = ((TextView) findViewById(R.id.textTime));
        mTimeLineView = ((TimeLineView) findViewById(R.id.timeLineView));

        setUpListeners();
        setUpMargins();
    }

    private void setUpListeners() {
        mListeners = new ArrayList<>();
        mListeners.add(new OnProgressVideoListener() {
            @Override
            public void updateProgress(int time, int max, float scale) {
                updateVideoProgress(time);
            }
        });
        mListeners.add(mVideoProgressIndicator);

        findViewById(R.id.btCancel)
                .setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCancelClicked();
                            }
                        }
                );

        findViewById(R.id.btSave)
                .setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onSaveClicked();
                            }
                        }
                );

        final GestureDetector gestureDetector = new
                GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        onClickVideoPlayPause();
                        return true;
                    }
                }
        );


        mRangeSeekBarView.addOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value) {
                onSeekThumbs(index, value);
            }

            @Override
            public void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value) {
                onStopSeekThumbs();
            }
        });
        mRangeSeekBarView.addOnRangeSeekBarListener(mVideoProgressIndicator);

        mHolderTopView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onPlayerIndicatorSeekChanged(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onPlayerIndicatorSeekStart();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onPlayerIndicatorSeekStop(seekBar);
            }
        });

        //TODO Call Audio Listeners Here (Prepared/Completed)
    }

    private void setUpMargins() {
        int marge = mRangeSeekBarView.getThumbs().get(0).getWidthBitmap();
        int widthSeek = mHolderTopView.getThumb().getMinimumWidth() / 2;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHolderTopView.getLayoutParams();
        lp.setMargins(marge - widthSeek, 0, marge - widthSeek, 0);
        mHolderTopView.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mTimeLineView.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mTimeLineView.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) mVideoProgressIndicator.getLayoutParams();
        lp.setMargins(marge, 0, marge, 0);
        mVideoProgressIndicator.setLayoutParams(lp);
    }

    private void onSaveClicked() {
        if (mStartPosition <= 0 && mEndPosition >= mDuration) {
            if (mOnTrimVideoListener != null)
                mOnTrimVideoListener.getResult(mSrc);
        } else {
            //TODO Pause Audio Here

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(getContext(), mSrc);
            long METADATA_KEY_DURATION = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

            final File file = new File(mSrc.getPath());

            if (mTimeVideo < MIN_TIME_FRAME) {

                if ((METADATA_KEY_DURATION - mEndPosition) > (MIN_TIME_FRAME - mTimeVideo)) {
                    mEndPosition += (MIN_TIME_FRAME - mTimeVideo);
                } else if (mStartPosition > (MIN_TIME_FRAME - mTimeVideo)) {
                    mStartPosition -= (MIN_TIME_FRAME - mTimeVideo);
                }
            }

            //notify that video trimming started
            if (mOnTrimVideoListener != null)
                mOnTrimVideoListener.onTrimStarted();

            BackgroundExecutor.execute(
                    new BackgroundExecutor.Task("", 0L, "") {
                        @Override
                        public void execute() {
                            try {
                                //TrimVideoUtils.startTrim(file, getDestinationPath(), mStartPosition, mEndPosition, mOnTrimVideoListener);
                            } catch (final Throwable e) {
                                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                            }
                        }
                    }
            );
        }
    }

    private void onClickVideoPlayPause() {
        //TODO Check Audio Playing Here
      /*  if (mVideoView.isPlaying()) {
            mMessageHandler.removeMessages(SHOW_PROGRESS);
            //TODO Pause Audio Here
        } else {
            mPlayView.setVisibility(View.GONE);

            if (mResetSeekBar) {
                mResetSeekBar = false;
                mVideoView.seekTo(mStartPosition);
            }

            mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
            mVideoView.start();
        }*/
    }

    private void onCancelClicked() {
        //TODO Stop Audio  Here
        if (mOnTrimVideoListener != null) {
            mOnTrimVideoListener.cancelAction();
        }
    }

    private String getDestinationPath() {
        if (mFinalPath == null) {
            File folder = Environment.getExternalStorageDirectory();
            mFinalPath = folder.getPath() + File.separator;
            Log.d(TAG, "Using default path " + mFinalPath);
        }
        return mFinalPath;
    }

    private void onPlayerIndicatorSeekChanged(int progress, boolean fromUser) {

        int duration = (int) ((mDuration * progress) / 1000L);

        if (fromUser) {
            if (duration < mStartPosition) {
                setProgressBarPosition(mStartPosition);
                duration = mStartPosition;
            } else if (duration > mEndPosition) {
                setProgressBarPosition(mEndPosition);
                duration = mEndPosition;
            }
            setTimeVideo(duration);
        }
    }

    private void onPlayerIndicatorSeekStart() {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        //TODO Pause Audio  Here
        notifyProgressUpdate(false);
    }

    private void onPlayerIndicatorSeekStop(@NonNull SeekBar seekBar) {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        //TODO Pause Audio  Here

        int duration = (int) ((mDuration * seekBar.getProgress()) / 1000L);
        //mVideoView.seekTo(duration);
        setTimeVideo(duration);
        notifyProgressUpdate(false);
    }

    private void onVideoPrepared(@NonNull MediaPlayer mp) {

        //TODO Get Audio Duration Here
        //mDuration = mVideoView.getDuration();
        setSeekBarPosition();

        setTimeFrames();
        setTimeVideo(0);

        if (mOnK4LVideoListener != null) {
            mOnK4LVideoListener.onVideoPrepared();
        }
    }

    private void setSeekBarPosition() {

        if (mDuration >= mMaxDuration) {
            mStartPosition = mDuration / 2 - mMaxDuration / 2;
            mEndPosition = mDuration / 2 + mMaxDuration / 2;

            mRangeSeekBarView.setThumbValue(0, (mStartPosition * 100) / mDuration);
            mRangeSeekBarView.setThumbValue(1, (mEndPosition * 100) / mDuration);

        } else {
            mStartPosition = 0;
            mEndPosition = mDuration;
        }

        setProgressBarPosition(mStartPosition);
        //TODO Seek Audio Duration Here
        //mVideoView.seekTo(mStartPosition);

        mTimeVideo = mDuration;
        mRangeSeekBarView.initMaxWidth();
    }

    private void setTimeFrames() {
        String seconds = getContext().getString(R.string.short_seconds);
        mTextTimeFrame.setText(String.format("%s %s - %s %s", stringForTime(mStartPosition), seconds, stringForTime(mEndPosition), seconds));
    }

    private void setTimeVideo(int position) {
        String seconds = getContext().getString(R.string.short_seconds);
        mTextTime.setText(String.format("%s %s", stringForTime(position), seconds));
    }

    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case Thumb.LEFT: {
                mStartPosition = (int) ((mDuration * value) / 100L);
                //TODO Seek Audio Duration Here
                //mVideoView.seekTo(mStartPosition);
                Log.i(TAG,"Start: "+getTimeString(mStartPosition));
                break;
            }
            case Thumb.RIGHT: {
                mEndPosition = (int) ((mDuration * value) / 100L);
                Log.i(TAG,"End: "+getTimeString(mEndPosition));
                break;
            }
        }
        setProgressBarPosition(mStartPosition);

        setTimeFrames();
        mTimeVideo = mEndPosition - mStartPosition;
    }

    private void onStopSeekThumbs() {
        mMessageHandler.removeMessages(SHOW_PROGRESS);
        //TODO Pause Audio Here
    }


    private void notifyProgressUpdate(boolean all) {
        if (mDuration == 0) return;

        //TODO Get Audio Current Position Here
        int position = 0;
        if (all) {
            for (OnProgressVideoListener item : mListeners) {
                item.updateProgress(position, mDuration, ((position * 100) / mDuration));
            }
        } else {
            mListeners.get(1).updateProgress(position, mDuration, ((position * 100) / mDuration));
        }
    }

    private void updateVideoProgress(int time) {


        if (time >= mEndPosition) {
            mMessageHandler.removeMessages(SHOW_PROGRESS);
            //TODO Pause Audio Here
            mResetSeekBar = true;
            return;
        }

        if (mHolderTopView != null) {
            // use long to avoid overflow
            setProgressBarPosition(time);
        }
        setTimeVideo(time);
    }

    private void setProgressBarPosition(int position) {
        if (mDuration > 0) {
            long pos = 1000L * position / mDuration;
            mHolderTopView.setProgress((int) pos);
        }
    }

    /**
     * Set video information visibility.
     * For now this is for debugging
     *
     * @param visible whether or not the videoInformation will be visible
     */
    public void setVideoInformationVisibility(boolean visible) {
        mTimeInfoContainer.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Listener for events such as trimming operation success and cancel
     *
     * @param onTrimVideoListener interface for events
     */
    @SuppressWarnings("unused")
    public void setOnTrimVideoListener(OnTrimVideoListener onTrimVideoListener) {
        mOnTrimVideoListener = onTrimVideoListener;
    }

    /**
     * Listener for some {@link VideoView} events
     *
     * @param onK4LVideoListener interface for events
     */
    @SuppressWarnings("unused")
    public void setOnK4LVideoListener(OnK4LVideoListener onK4LVideoListener) {
        mOnK4LVideoListener = onK4LVideoListener;
    }

    /**
     * Sets the path where the trimmed video will be saved
     * Ex: /storage/emulated/0/MyAppFolder/
     *
     * @param finalPath the full path
     */
    @SuppressWarnings("unused")
    public void setDestinationPath(final String finalPath) {
        mFinalPath = finalPath;
        Log.d(TAG, "Setting custom path " + mFinalPath);
    }

    /**
     * Cancel all current operations
     */
    public void destroy() {
        BackgroundExecutor.cancelAll("", true);
        UiThreadExecutor.cancelAll("");
    }

    /**
     * Set the maximum duration of the trimmed video.
     * The trimmer interface wont allow the user to set duration longer than maxDuration
     *
     * @param maxDuration the maximum duration of the trimmed video in seconds
     */
    @SuppressWarnings("unused")
    public void setMaxDuration(int maxDuration) {
        mMaxDuration = maxDuration * 1000;
    }

    /**
     * Sets the uri of the video to be trimmer
     *
     * @param videoURI Uri of the video
     */
    @SuppressWarnings("unused")
    public void setVideoURI(final Uri videoURI) {
        mSrc = videoURI;

        if (mOriginSizeFile == 0) {
            File file = new File(mSrc.getPath());

            mOriginSizeFile = file.length();
            long fileSizeInKB = mOriginSizeFile / 1024;

            if (fileSizeInKB > 1000) {
                long fileSizeInMB = fileSizeInKB / 1024;
                mTextSize.setText(String.format("%s %s", fileSizeInMB, getContext().getString(R.string.megabyte)));
            } else {
                mTextSize.setText(String.format("%s %s", fileSizeInKB, getContext().getString(R.string.kilobyte)));
            }
        }

        //TODO Set Audio Source Here
        /*mVideoView.setVideoURI(mSrc);
        mVideoView.requestFocus();*/

        mTimeLineView.setVideo(mSrc);
    }

    private static class MessageHandler extends Handler {

        @NonNull
        private final WeakReference<AudioTrimView> mView;

        MessageHandler(AudioTrimView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            AudioTrimView view = mView.get();
            //TODO Check Audio Playing Here
           /* if (view == null || view.mVideoView == null) {
                return;
            }

            view.notifyProgressUpdate(true);
            if (view.mVideoView.isPlaying()) {
                sendEmptyMessageDelayed(0, 10);
            }*/
        }
    }

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        Formatter mFormatter = new Formatter();
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf.append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }
}
