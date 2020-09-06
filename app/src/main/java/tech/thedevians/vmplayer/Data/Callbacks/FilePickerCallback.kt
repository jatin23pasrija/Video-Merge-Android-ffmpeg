package tech.thedevians.vmplayer.Data.Callbacks

import android.net.Uri

interface FilePickerCallback {

    fun OnFileSelected(uri: String)
}