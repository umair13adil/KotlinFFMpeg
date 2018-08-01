package com.blackbox.ffmpeg.examples

import com.blackbox.ffmpeg.examples.utils.Utils
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    lateinit var videos: ArrayList<String>
    private var outputPath = ""
    private var outputFileName = ""


    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {

        val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        videos = arrayListOf()
        videos.add("Path1")
        videos.add("Path2")
        videos.add("Path3")

        val inputCommand = arrayListOf<String>()
        inputCommand.add("-y")

        for (i in videos) {
            inputCommand.add("-i")
            inputCommand.add(i)
        }

        val videoCommand = arrayListOf<String>()
        for (i in 0 until videos.size) {
            videoCommand.add("[$i:v]scale=480x640,setsar=1[v$i];")
        }

        val concatCommand = arrayListOf<String>()
        for (i in 0 until videos.size) {
            concatCommand.add("[v$i][$i:a]")
        }

        //val cmd = arrayOf<String>("concat=n=${videos.size}:v=1:a=1", "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "480x640", "-vcodec", "libx264", "-crf", "27", "-preset", "ultrafast", outputLocation.path)
        val cmd = arrayOf<String>("concat=n=${videos.size}:v=1:a=1", "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "480x640", "-vcodec", "libx264", "-crf", "27", "-preset", "ultrafast", outputLocation.path)

        val s = (inputCommand + "-filter_complex" + videoCommand + concatCommand + cmd).toTypedArray()

        s.forEach { println(it) }
    }
}