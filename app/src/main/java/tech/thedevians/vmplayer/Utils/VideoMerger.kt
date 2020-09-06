package tech.thedevians.vmplayer.Utils

import android.content.Context
import tech.thedevians.vmplayer.Data.Callbacks.FFMPEGMobCallback
import java.io.File
import java.io.IOException

class VideoMerger private constructor(private val context: Context) {

    private var videos: List<File>? = null
    private var callback1: FFMPEGMobCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setVideoFiles(originalFiles: List<File>): VideoMerger {
        this.videos = originalFiles
        return this
    }

    fun setCallback1(callback: FFMPEGMobCallback): VideoMerger {
        this.callback1 = callback
        return this
    }

    fun setOutputPath(output: String): VideoMerger {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoMerger {
        this.outputFileName = output
        return this
    }

    fun merge() {

        if (videos == null || videos!!.isEmpty()) {
            callback1!!.onError(IOException("File not exists"))
            return
        }

        for (v in videos!!) {
            if (!v.canRead()) {
                callback1!!.onError(IOException("Can't read the file. Missing permission?"))
                return
            }
        }

        val outputLocation = getConvertedFile(outputPath, outputFileName)


        val comm = arrayOf(
            "-y",
            "-i",
            videos!![0].path,
            "-i",
            videos!![1].path,
            "-strict",
            "experimental",
            "-filter_complex",
            "[0:v]scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v0];[1:v] scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",            "-ab",
            "32000",
            "-ac",
            "2",
            "-ar",
            "16000",
            "-s",
            "480x320",
            "-vcodec",
            "libx264",
            "-crf",
            "15",
            "-q",
            "4",
            "-preset",
            "ultrafast",
            outputLocation.path
        )

        try {
            callback1!!.onItemSelected(comm, outputLocation)
        } catch (e: Exception) {
            callback1!!.onError(e)
        }
    }

    companion object {

        fun with(context: Context): VideoMerger {
            return VideoMerger(context)
        }
    }
}
