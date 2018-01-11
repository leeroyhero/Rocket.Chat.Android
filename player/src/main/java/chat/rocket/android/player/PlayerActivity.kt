package chat.rocket.android.player

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: SimpleExoPlayer
    private var isPlayerInitialized = false
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || !isPlayerInitialized) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (!isPlayerInitialized) {
            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
            player_view.player = player
            player.playWhenReady = playWhenReady
            player.seekTo(currentWindow, playbackPosition)
            isPlayerInitialized = true
        }
        val uri = Uri.parse("http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4")
        val mediaSource = buildMediaSource(uri)
        player.prepare(mediaSource, true, false)
    }

    private fun releasePlayer() {
        if (isPlayerInitialized) {
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            playWhenReady = player.playWhenReady
            player.release()
            isPlayerInitialized = false
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource = ExtractorMediaSource(uri, DefaultHttpDataSourceFactory("rocket-chat-android-player"), DefaultExtractorsFactory(), null, null)

    private fun hideSystemUi() {
        // Read the docs for detailed explanation: https://developer.android.com/training/basics/firstapp/starting-activity.html and https://developer.android.com/design/patterns/fullscreen.html
         player_view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}