package tech.thedevians.vmplayer.Utils

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.File

fun appLogger(message: String?) {
    if (message.isNullOrEmpty()) {
        Log.d(Constants.CONST_LOG, "VAL !")
    } else {
        Log.d(Constants.CONST_LOG, message)
    }
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showToast(message: String, context: Context, time: Int) {
    Toast.makeText(context, message, time).show()
}


fun getConvertedFile(folder: String, fileName: String): File {
    val f = File(folder)

    if (!f.exists())
        f.mkdirs()

    return File(f.path + File.separator + fileName)
}

fun refreshGallery(path: String, context: Context) {

    val file = File(path)
    try {
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            arrayOf(file.name), null
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }

}


fun onActivityAnimate(activity: Activity) {
    activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
}


fun getPathFromURI(context: Context, uri: Uri): String {
    var realPath = String()
    uri.path?.let { path ->

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/video:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            println(e)
        }
    }
    return realPath
}

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}
