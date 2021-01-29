package com.PSCube.youtubethroughexo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity implements Player.EventListener {

    private static final int ITAG_FOR_AUDIO = 140;
    private boolean isError;
    SimpleExoPlayer player;
    private PlayerView playerView;

    private FrameLayout exo_ffwd, exo_rew, moreButton;
    String downloadUrl;
    VideoMeta videoMeta;

    boolean fullscreen = false;
    private List<YtFragmentedVideo> formatsToShowList;

    int currentquality = 360;
    private EditText edttxt;
    public static String youtubeLink;
    private ImageView fullscreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.video_player_view);

        exo_rew = findViewById(R.id.exoo_rew);
        exo_ffwd = findViewById(R.id.exoo_ffwd);


        edttxt = findViewById(R.id.edttxt);
        fullscreenButton = findViewById(R.id.fullscreenButton);
        moreButton = findViewById(R.id.moreButton);


        moreButton.setVisibility(View.GONE);
        fullscreenButton.setVisibility(View.GONE);
        exo_ffwd.setEnabled(false);
        exo_rew.setEnabled(false);


        exo_rew.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.d("TAG", "onSingleClick: rew");
                playerView.showController();
            }

            @Override
            public void onDoubleClick(View v) {
                long rewtime = player.getCurrentPosition() - 10000;
                if (player.getCurrentPosition() < 10000) {
                    player.seekTo(0);
                } else {
                    player.seekTo(rewtime);

                }
                Log.d("TAG", "onDoubleClick: rew " + rewtime);


            }
        });
        exo_ffwd.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.d("TAG", "onSingleClick: forward");
                playerView.showController();
            }

            @Override
            public void onDoubleClick(View v) {
                Log.d("TAG", "onDoubleClick: forward ");
                long ffwwtime = player.getCurrentPosition() + 10000;
                if (player.getCurrentPosition() > player.getDuration() - 10000) {
                    player.seekTo(player.getDuration());
                } else {
                    player.seekTo(ffwwtime);

                }
            }
        });

        setOnclickListnertoMoreButton();

        setOnclickListnertoFullscreenButton();


    }

    private void setOnclickListnertoFullscreenButton() {

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fullscreen) {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.exo_controls_fullscreen_enter));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = (int) (250 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);

                    edttxt.setVisibility(View.VISIBLE);

                    fullscreen = false;
                } else {
                     fullscreenButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.exo_controls_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    edttxt.setVisibility(View.GONE);

                    fullscreen = true;
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (!fullscreen) {
            super.onBackPressed();
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            fullscreen = false;
        }
    }

    private void setOnclickListnertoMoreButton() {

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                String quality, playbackSpeed, download;
                quality = "Quality";
                playbackSpeed = "Playback Speed";
                download = "Download";

                popupMenu.getMenu().add(quality);
                popupMenu.getMenu().add(playbackSpeed);
                popupMenu.getMenu().add(download);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString() == quality) {
                            Quality(v);

                        }
                        if (item.getTitle().toString() == playbackSpeed) {
                            playbackspeed(v);

                        }
                        if (item.getTitle().toString() == download) {

                            gotodownload(v);

                        }

                        return false;
                    }
                });

                popupMenu.show();

            }
        });


    }


    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        //   Log.d("TAG1", "addFormatToList: " + ytFiles.size());


        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }

    private void playThatVideo(String downloadUrl, boolean isError, String url, YtFragmentedVideo ytFragmentedVideo) {

        Log.d("1234", "playThatVideo: " + ytFragmentedVideo.audioFile);
        Log.d("1234", "playThatVideo: " + ytFragmentedVideo.videoFile);

        if (ytFragmentedVideo.audioFile == null) {
            if (player == null) {
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

                player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
                playerView.setPlayer(player);

                DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "exoplayer"));
                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(downloadUrl));

                player.prepare(mediaSource);
                player.addListener(this);
                player.setPlayWhenReady(true);
            } else {


                long currentduration = player.getCurrentPosition();
                player.stop();
                player = null;


                DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
                player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
                playerView.setPlayer(player);

                DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "exoplayer"));
                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(downloadUrl));

                player.prepare(mediaSource);
                player.addListener(this);
                player.setPlayWhenReady(true);
                player.seekTo(currentduration);

            }

        } else {
            long currentduration = player.getCurrentPosition();
            player.stop();
            player = null;


            DefaultTrackSelector trackSelectorforvideo = new DefaultTrackSelector(this);
            player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelectorforvideo).build();
            playerView.setPlayer(player);

            DefaultDataSourceFactory defaultDataSourceFactoryforvideo = new DefaultDataSourceFactory(
                    this, Util.getUserAgent(this, "exoplayer"));

            MediaSource mediaSourcevideo = new ProgressiveMediaSource.Factory(defaultDataSourceFactoryforvideo)
                    .createMediaSource(Uri.parse(ytFragmentedVideo.videoFile.getUrl()));
            MediaSource mediaSourceforAudio = new ProgressiveMediaSource.Factory(defaultDataSourceFactoryforvideo)
                    .createMediaSource(Uri.parse(ytFragmentedVideo.audioFile.getUrl()));

            MergingMediaSource mergedSource = new MergingMediaSource(mediaSourcevideo, mediaSourceforAudio);


            player.prepare(mergedSource);
            player.addListener(this);
            player.setPlayWhenReady(true);
            player.seekTo(currentduration);


        }


    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case SimpleExoPlayer.STATE_READY:
                /// qualitybutton.setEnabled(true);
                exo_ffwd.setEnabled(true);
                exo_rew.setEnabled(true);
                moreButton.setEnabled(true);
                fullscreenButton.setEnabled(true);
                moreButton.setVisibility(View.VISIBLE);
                fullscreenButton.setVisibility(View.VISIBLE);


                break;
        }
    }

    public void playbackspeed(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.speed, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String id = String.valueOf(item.getTitle());
                String value = id.substring(0, id.length() - 1);
                String playbackspeed = value + "f";

                Log.d("TAG", "onMenuItemClick: " + id);
                Log.d("TAG", "onMenuItemClick: " + value);
                Log.d("TAG", "onMenuItemClick: " + Float.parseFloat(playbackspeed));

                PlaybackParameters param = new PlaybackParameters(Float.parseFloat(playbackspeed));
                player.setPlaybackParameters(param);
                return false;
            }
        });
        popupMenu.show();

    }

    public void Quality(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        for (int i = 0; i < formatsToShowList.size(); i++) {
            YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
            if (ytFrVideo.height == -1) {
                Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
            } else {
                Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                        ytFrVideo.height + "p");
                popupMenu.getMenu().add(ytFrVideo.height + "p");
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int index;
                String title = item.getTitle().toString().substring(0, item.getTitle().length() - 1);
                Log.d("TAG", "onMenuItemClick: " + title);
                for (int i = 0; i < formatsToShowList.size(); i++) {
                    YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
                    if (ytFrVideo.height == -1) {
                        Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
                    } else {
                        Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                                ytFrVideo.height + "p");
                        if (ytFrVideo.height == Integer.parseInt(title)) {
                            if (ytFrVideo.height != currentquality) {
                                currentquality = Integer.parseInt(title);
                                playThatVideo(ytFrVideo.videoFile.getUrl(), false, formatsToShowList.get(0).audioFile.getUrl(), formatsToShowList.get(i));
                            }
                        }
                    }


                }

                return false;
            }
        });
        popupMenu.show();
    }


    public void gotodownload(View view) {
        Log.d("TAG", "gotodownload: ");
        startActivity(new Intent(this, MainActivity3.class));
    }

    public void playVideo(View view) {
        if (edttxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please paste youtube url here", Toast.LENGTH_SHORT).show();
        } else {
            youtubeLink = edttxt.getText().toString();
            new YouTubeExtractor(this) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    if (ytFiles != null) {

                        downloadUrl = "";
                        try {
                            isError = false;
                            //   downloadUrl = ytFiles.get(itag).getUrl();
                            videoMeta = vMeta;
                            formatsToShowList = new ArrayList<>();
                            for (int i = 0, itag; i < ytFiles.size(); i++) {
                                itag = ytFiles.keyAt(i);
                                YtFile ytFile = ytFiles.get(itag);


                                if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 144) {
                                    addFormatToList(ytFile, ytFiles);
                                }
                            }
                            Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                                @Override
                                public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                                    return lhs.height - rhs.height;
                                }
                            });
                            // playThatVideo(formatsToShowList.get(2).videoFile.getUrl(),false);
                            Log.d("TAG", "onExtractionComplete: " + formatsToShowList.size());
                            for (int i = 0; i < formatsToShowList.size(); i++) {
                                YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
                                if (ytFrVideo.height == -1) {
                                    Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
                                } else {
                                    Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                                            ytFrVideo.height + "p");
                                    if (ytFrVideo.height == currentquality) {
                                        playThatVideo(ytFrVideo.videoFile.getUrl(), false, formatsToShowList.get(0).audioFile.getUrl(), formatsToShowList.get(i));
                                    }
                                }


                            }

                        } catch (Exception e) {
                            isError = true;
                            downloadUrl = "error";
                            Log.d("TAG", "onExtractionComplete: " + e.getMessage());
                        }
                        //playThatVideo(downloadUrl, isError);
                        // Log.d("TAG", "onExtractionComplete: " + downloadUrl);
                    }
                }
            }.extract(youtubeLink, true, true);
        }
    }

    public void gotodownloadSection(View view) {
        startActivity(new Intent(this, ShowDownloadCourse.class));
    }


    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);

        public abstract void onDoubleClick(View v);
    }

    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }

    @Override
    protected void onStop() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            } else {
                player.stop();
                player.release();
            }
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
        }


        super.onPause();
    }





}

