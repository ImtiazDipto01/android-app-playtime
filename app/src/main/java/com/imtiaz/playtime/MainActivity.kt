package com.imtiaz.playtime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Player.EventListener {

    private var player : SimpleExoPlayer? = null
    private var mPlayWhenReady = true
    private var mPlayerCurrentWindow = 0
    private var mPlaybackPosition : Long =  0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun initializePlayer(){
        player = ExoPlayerFactory.newSimpleInstance(this)
        playerView.player = player

        val uri = Uri.parse(getString(R.string.media_url_mp4))
        val mediaSource = buildMediaSource(uri)

        player?.apply {
            playWhenReady = mPlayWhenReady
            seekTo(mPlayerCurrentWindow, mPlaybackPosition)
            addListener(this@MainActivity)
            prepare(mediaSource, false, false)
        }
    }

    private fun buildMediaSource(uri : Uri) : MediaSource{
        val dataSourceFactory = DefaultDataSourceFactory(this, resources.getString(R.string.app_name))
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }


    private fun hideSystemUi(){
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer(){
        player?.apply {
            mPlayWhenReady = playWhenReady
            mPlaybackPosition = currentPosition
            mPlayerCurrentWindow = currentWindowIndex
            removeListener(this@MainActivity)
            release()
            player = null
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when(playbackState){

            ExoPlayer.STATE_IDLE -> {
                Log.e("EXOPLAYER_STATE", "STATE_IDLE")
                pbLoader.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Video Loading Failed..", Toast.LENGTH_SHORT).show()
            }

            ExoPlayer.STATE_BUFFERING -> {
                Log.e("EXOPLAYER_STATE", "STATE_BUFFERING")
                pbLoader.visibility = View.VISIBLE
            }

            ExoPlayer.STATE_READY -> {
                Log.e("EXOPLAYER_STATE", "STATE_READY")
            }

            ExoPlayer.STATE_ENDED -> {
                Log.e("EXOPLAYER_STATE", "STATE_ENDED")
            }


        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if(isPlaying){
            Log.e("EXOPLAYER_STATE", "PLAYING")
            pbLoader.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        if(Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if((Util.SDK_INT < 24 || player == null)){
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


}
