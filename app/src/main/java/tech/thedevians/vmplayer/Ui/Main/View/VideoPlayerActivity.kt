package tech.thedevians.vmplayer.Ui.Main.View

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_player.*
import tech.thedevians.vmplayer.R
import tech.thedevians.vmplayer.Utils.Constants
import tech.thedevians.vmplayer.Utils.appLogger
import tech.thedevians.vmplayer.Utils.onActivityAnimate
import tech.thedevians.vmplayer.Utils.showToast

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        try {
            val videoUrl = intent.getStringExtra(Constants.CONST_VIDEO_STR)

            if (videoUrl != null) {
                viewPlayer.setVideoPath(videoUrl)
                val mediaController = MediaController(this@VideoPlayerActivity)
                viewPlayer.setMediaController(mediaController)
                mediaController.setAnchorView(viewPlayer)
                viewPlayer.start()
            } else {
                showToast("No Video to play", this@VideoPlayerActivity)
            }
        } catch (e: Exception) {
            appLogger(e.toString())
        }


        imgBack.setOnClickListener {
            onBackPressed()
            onActivityAnimate(this@VideoPlayerActivity)
        }

    }
}
