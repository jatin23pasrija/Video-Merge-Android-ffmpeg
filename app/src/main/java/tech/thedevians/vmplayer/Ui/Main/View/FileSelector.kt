package tech.thedevians.vmplayer.Ui.Main.View

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.view_file_picker.*
import tech.thedevians.vmplayer.Data.Callbacks.FilePickerCallback
import tech.thedevians.vmplayer.R
import tech.thedevians.vmplayer.Utils.Constants
import tech.thedevians.vmplayer.Utils.appLogger
import tech.thedevians.vmplayer.Utils.getPathFromURI
import tech.thedevians.vmplayer.Utils.showToast


class FileSelector(private val filePickerCallback: FilePickerCallback) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_file_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSelectFile.setOnClickListener {
            KotlinPermissions.with(requireActivity())
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onForeverDenied {
                    showToast(
                        "Need permissions to continue, Please enable from settings",
                        requireContext()
                    )
                }
                .onDenied {
                    showToast("Need permissions to continue", requireContext())
                }
                .onAccepted {
                    onSelectVideo()
                }
                .ask()
        }

    }


    private fun onSelectVideo() {
        try {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).also {
                it.type = "video/*"
                startActivityForResult(it, Constants.CONST_SELECT_VIDEO_GALLERY)
            }
        } catch (e: Exception) {
            appLogger(e.toString())
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == Constants.CONST_SELECT_VIDEO_GALLERY && resultCode == RESULT_OK) {

                try {
                    val selectedVideo = data?.data
                    if (selectedVideo != null) {
                        filePickerCallback.OnFileSelected(
                            getPathFromURI(
                                requireContext(),
                                selectedVideo
                            )
                        )
                        this@FileSelector.dismissAllowingStateLoss()
                    } else {
                        showToast("Unable to fetch video", requireContext())
                    }

                } catch (e: Exception) {
                    appLogger(e.toString())
                }
            }
        } catch (e: Exception) {
            appLogger(e.toString())
        }


    }
}