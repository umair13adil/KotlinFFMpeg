# Kotlin FFMpeg Tools 
### Examples of using FFMpeg library on Android with Kotlin
##### `For Video, Audio and Image/GIF operations`. 

App is pre loaded with audio, video, images, font resources which are useful for experimenting with FFmpeg library. You can add your resources as well, but keep extensions and make sure naming convions are same as used in this project to avoid any I/OExceptions.

Each example will preview result in a dialog. Files are saved in local storage directory and can be directly accessed from there.

![Alt text](pictures/image1.png?raw=true "Icon")<!-- .element height="50%" width="50%" -->
![Alt text](pictures/image2.png?raw=true "Icon")<!-- .element height="50%" width="50%" -->

### FFMpeg Examples (Video):

1. Split a video in equal segments
2. Trim a video with specified timestamps
3. Make a MP4 movie with provided images & audio in mp3
4. Resize a video in specified dimensions
5. Add Text overlay on video with specified text attributes
6. Merge an audio over video file

### FFMpeg Examples (Audio):

1. Trim a audio with specified timestamps
2. Extract audio file from video file

### FFMpeg Examples (Image):

1. Convert video to GIF 
2. Convert video to images every 'n' time

### Permissions:

Following permissions must be added to avoid IO exceptions:

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

### TODO:

* Add preview for 'Extarct Images from Video'

## Acknowledgments:
* This project is extension of work by: [WritingMinds/ffmpeg-android](https://github.com/WritingMinds/ffmpeg-android)
* [android-gif-drawable](https://github.com/koral--/android-gif-drawable?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=1147)is used for preview of GIF files.

## MIT License

##### Copyright (c) 2018 Muhammad Umair Adil

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
