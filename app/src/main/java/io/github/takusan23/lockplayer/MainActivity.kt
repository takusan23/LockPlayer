package io.github.takusan23.lockplayer

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.video.VideoListener
import com.google.android.exoplayer2.video.VideoSize
import io.github.takusan23.lockplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val exoPlayer by lazy { SimpleExoPlayer.Builder(this).build() }

    private val callback = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        setMediaItem(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        // ActionBarけす
        supportActionBar?.hide()

        // 動画ファイルを選択させる
        callback.launch(arrayOf("video/*"))

        // ExoPlayer初期化
        initExoPlayer()

        // SystemBar非表示
        hideSystemBar()

    }

    /** SystemBarを消す */
    private fun hideSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 以上と分岐
            window?.insetsController?.apply {
                // スワイプで一時的に表示可能
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // StatusBar + NavigationBar 非表示
                hide(WindowInsets.Type.systemBars())
                // ノッチにも侵略
                window?.attributes?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        } else {
            // Android 10 以前。
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    /** ExoPlayer初期化 */
    private fun initExoPlayer() {
        exoPlayer.setVideoSurfaceView(viewBinding.surfaceView)
    }

    /** ExoPlayerにUriをセットする */
    private fun setMediaItem(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    /** ボリュームキーのイベントを検知 */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}