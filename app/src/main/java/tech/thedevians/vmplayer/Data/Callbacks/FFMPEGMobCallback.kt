package tech.thedevians.vmplayer.Data.Callbacks

import java.io.File

interface FFMPEGMobCallback {

    fun onItemSelected(array: Array<String>, outputPath: File)

    fun onError(e: Exception)

}