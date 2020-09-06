package tech.thedevians.vmplayer.Ui.Main.View

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.arthenica.mobileffmpeg.Config
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.activity_main.*
import tech.thedevians.vmplayer.BuildConfig
import tech.thedevians.vmplayer.Data.Api.ApiHelper
import tech.thedevians.vmplayer.Data.Api.ApiServiceImplementor
import tech.thedevians.vmplayer.Data.Callbacks.FFMPEGMobCallback
import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback
import tech.thedevians.vmplayer.Data.Callbacks.FilePickerCallback
import tech.thedevians.vmplayer.R
import tech.thedevians.vmplayer.Ui.Base.ViewModelFactory
import tech.thedevians.vmplayer.Ui.Main.ViewModel.MainViewModel
import tech.thedevians.vmplayer.Utils.*
import java.io.File


class MainActivity : AppCompatActivity(), FilePickerCallback, FFMPEGMobCallback, FFmCallback {

    private lateinit var videos: ArrayList<String>
    private lateinit var vidUri: Uri

    private var isProcessing = false

    private lateinit var mainViewModel: MainViewModel

    private lateinit var outputPath: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videos = ArrayList()
        btnStartCapture.setOnClickListener {
            KotlinPermissions.with(this@MainActivity)
                .permissions(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onAccepted {
                    if (!isProcessing) {
                        startVideoCapture()
                    } else {
                        showToast(
                            "You cannot capture a video while the previous video is still processing",
                            this@MainActivity
                        )
                    }

                }
                .onDenied {
                    showToast("Need permissions to continue", this@MainActivity)
                }
                .onForeverDenied {
                    showToast(
                        "Need permissions to continue, Please enable from settings",
                        this@MainActivity
                    )
                }
                .ask()

        }

        mainViewModel = ViewModelProviders.of(
            this@MainActivity,
            ViewModelFactory(ApiHelper(ApiServiceImplementor()))
        )
            .get(MainViewModel::class.java)
//        val fileSelector = FileSelector(this@MainActivity)
//        fileSelector.show(supportFragmentManager, "FileSelector")
    }

    private fun startVideoCapture() {
        try {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { intent ->
                val mediaFile =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + "/videoCap.mp4")
                vidUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mediaFile
                )
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, vidUri)
                intent.resolveActivity(packageManager)?.also {
                    startActivityForResult(intent, Constants.CONST_REQUEST_VIDEO_CAPTURE)
                }
            }
        } catch (e: Exception) {
            appLogger(e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == Constants.CONST_REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

                val videoUri: Uri? = data?.data
                appLogger(videoUri.toString())
                appLogger(videoUri?.path)
                if (videoUri != null) {
                    //videos.add(videoUri.path.toString())
                    videos.add(getPathFromURI(this@MainActivity, videoUri))
                    val fileSelector = FileSelector(this@MainActivity)
                    fileSelector.show(supportFragmentManager, "FileSelector")
                } else {
                    showToast("Unable to fetch captured video", this@MainActivity)
                }
            }
        } catch (e: Exception) {
            appLogger(e.toString())
        }

    }

    override fun OnFileSelected(uri: String) {
        try {
            appLogger(uri)
            val videoList = arrayListOf(
                File(videos[0]),
                File(uri)
            )
            for (i in videoList) {
                if (!i.canRead()) {
                    appLogger("Unable to read" + i.path)
                    showToast("Unable to read files", this@MainActivity)
                    return
                }
            }
            progressBar.visibility = View.VISIBLE
            isProcessing = true
            Handler().postDelayed({
                VideoMerger.with(this@MainActivity)
                    .setVideoFiles(videoList)
                    .setOutputPath(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + "/video")
                    .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
                    .setCallback1(this@MainActivity)
                    .merge()

            }, 500)


        } catch (e: Exception) {
            appLogger(e.toString())
        }

    }

    private fun onFileProcessed(convertedFile: File) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Processing Completed")
        builder.setMessage("Your video has been merged. Do you like to see the merged video?")
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            startActivity(
                Intent(this@MainActivity, VideoPlayerActivity::class.java)
                    .putExtra(
                        Constants.CONST_VIDEO_STR,
                        convertedFile.path
                    )
            )
            onActivityAnimate(this@MainActivity)
        }
        builder.setNegativeButton(android.R.string.no, null)
        builder.show()
    }

    override fun onItemSelected(array: Array<String>, outputPath: File) {
        this.outputPath = outputPath
        mainViewModel.getMergedStatus(com = array, callback = this@MainActivity)

    }

    override fun onError(e: Exception) {
        appLogger(e.toString())
        if (!e.toString().contains("IllegalStateException")) {
            isProcessing = false
            dismissProgress()
        }
    }

    private fun dismissProgress() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.GONE
        }
    }

    override fun onResponse(result: Int) {
        runOnUiThread {
            when (result) {
                Config.RETURN_CODE_SUCCESS -> {
                    isProcessing = false
                    appLogger("Success NCode")
                    dismissProgress()
                    refreshGallery(outputPath.path, this@MainActivity)
                    onFileProcessed(outputPath)
                }
                Config.RETURN_CODE_CANCEL -> {
                    isProcessing = false
                    appLogger("Cancel NCode")
                    if (outputPath.exists()) {
                        outputPath.delete()
                    }
                    dismissProgress()
                }
                else -> {
                    isProcessing = false
                    appLogger("Error NCode")
                    showToast("Video Not Supported", this@MainActivity)
                    dismissProgress()
                    Config.printLastCommandOutput(Log.INFO)
                }
            }
        }


    }


}
