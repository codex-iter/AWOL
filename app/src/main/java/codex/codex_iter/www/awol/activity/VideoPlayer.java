package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import codex.codex_iter.www.awol.R;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static codex.codex_iter.www.awol.utilities.Constants.VIDEO_URL;


public class VideoPlayer extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private ProgressBar progressBar;
    private ImageButton fullScreenButton;
    private boolean runnableCalled = false;
    private String videoUrl;
    private String videoId;
    private NetworkChangeReceiver networkChangeReceiver;
    private TextView errorMessage;
    private ProgressiveMediaSource mediaSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        playerView = findViewById(R.id.exo_player_view);
        progressBar = findViewById(R.id.progress_bar);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_button);
        errorMessage = findViewById(R.id.error_message);

        networkChangeReceiver = new NetworkChangeReceiver(isConnected -> {
            if (isConnected && errorMessage.getVisibility() == View.VISIBLE) {
                if (runnableCalled && player != null && mediaSource != null) {
                    player.retry();
                } else {
                    releasePlayer();
                    initExoPlayer();
                }
            } else {
                setInit();
            }
        });

        if (!isConnected(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("No internet connection")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }).create().show();
        }

        if (savedInstanceState == null) {
            setInit();
        }

        initExoPlayer();
    }

    private void setInit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            videoUrl = bundle.getString(VIDEO_URL);

        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Video Url is empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initExoPlayer() {
        if (!isConnected(this)) {
            return;
        } else {
            errorMessage.setVisibility(View.GONE);
            errorMessage.setText("");
        }

        player = ExoPlayerFactory.newSimpleInstance(this);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                //CONTENT_TYPE_MOVIE
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        player.setAudioAttributes(audioAttributes, true);
        if (playerView == null) {
            playerView = findViewById(R.id.exo_player_view);
        }
        playerView.setPlayer(player);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        } else {
            fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_exit);
        }

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // In landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_enter);
                } else {
                    // In portrait
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_exit);
                }
            }
        });

        DataSource.Factory dataSourceFactory1 = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        Uri uri = Uri.parse(videoUrl);
//        mediaSource = new HlsMediaSource.Factory(dataSourceFactory1).createMediaSource(uri);
        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory1).createMediaSource(uri);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        progressBar.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.GONE);
                        errorMessage.setText("");
                        break;
                    case ExoPlayer.STATE_ENDED:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("ExoPlaybackException", error.getMessage(), error);
                progressBar.setVisibility(View.GONE);
                if (isConnected(VideoPlayer.this)) {
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(true);

        playerView.setVisibility(View.VISIBLE);
        playerView.setKeepScreenOn(true);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.setPlayWhenReady(true);
            player.stop();
            player.release();
            player = null;
            playerView.setPlayer(null);
            playerView = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
        pausePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (playerView == null) {
            playerView = findViewById(R.id.exo_player_view);
        }
        if (fullScreenButton == null) {
            fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_button);
        }

        try {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_enter);
            } else {
                fullScreenButton.setImageResource(R.drawable.exo_controls_fullscreen_exit);
            }
        } catch (NullPointerException e) {
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onBackPressed();
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private NetworkChangeListener networkChangeListener;

        public NetworkChangeReceiver(NetworkChangeListener networkChangeListener) {
            this.networkChangeListener = networkChangeListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null && intent.getAction().equals(CONNECTIVITY_ACTION) && networkChangeListener != null)
                networkChangeListener.onNetworkChanged(isConnected(context));
        }

        private boolean isConnected(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }

        public interface NetworkChangeListener {
            void onNetworkChanged(boolean isConnected);
        }
    }

    public boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo == null) {
                return false;
            }
            return networkInfo.isConnected();
        }
    }
}

