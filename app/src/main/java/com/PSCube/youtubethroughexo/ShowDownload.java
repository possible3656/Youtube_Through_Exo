package com.PSCube.youtubethroughexo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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

import java.io.File;
import java.util.ArrayList;

public class ShowDownload extends AppCompatActivity implements Adapter.OnVideoClickListner, Player.EventListener {

    private RecyclerView recyclerView;

    Adapter adapter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    String path;
    private boolean playingVideo=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_download);

        recyclerView = findViewById(R.id.recyclerView);
        playerView = findViewById(R.id.exoplayerDownload);


        Intent intent=getIntent();
        if (intent != null) {
         path=    intent.getStringExtra("path");
        }


        File file = new File(path);

        final File listFile[] = file.listFiles();

        searchVid(file, "abc");
        searchVid(file, "cba");
        Log.d("1234", "onCreate: " + pathForVideo);


    }

    private ArrayList<File> pathForVideo = new ArrayList<>();
    private ArrayList<File> pathForAudio = new ArrayList<>();

    public void searchVid(File dir, String pattern) {
        // Log.d("1234", "searchVid: ");

        //Get the listfile of that flder
        final File listFile[] = dir.listFiles();


        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getName().endsWith(pattern)) {
                    // Do what ever u want, add the path of the video to the list
                    if (pattern == "abc") {
                        pathForVideo.add(listFile[i]);
                    }
                    if (pattern == "cba") {
                        pathForAudio.add(listFile[i]);
                    }

                    // Log.d("1234", "searchVid: " + listFile[i]);
                }
            }

            populateRecyclerView();
        } else {
            Log.d("1234", "searchVid: error");
        }
    }

    private void populateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, pathForVideo, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onVideoClicked(int position) {
        Log.d("1234", "onVideoClicked: ");
        recyclerView.setVisibility(View.GONE);
        playvideo(position);
    }

    private void playvideo(int position) {
        playingVideo=true;
        String audioUriString = null;
        Uri uriaudio = null;

        playerView.setVisibility(View.VISIBLE);
        player = new SimpleExoPlayer.Builder(this).setTrackSelector(new DefaultTrackSelector(this)).build();
        playerView.setPlayer(player);

        Uri uri = Uri.fromFile(pathForVideo.get(position));
        //  Log.d("TAG", "playvideo: " + uri);

        String videoUriWithoutFormat = uri.toString().substring(0,
                uri.toString().length() - 4);

        String videoUriString = videoUriWithoutFormat + ".mp4";
        Log.d("TAG", "playvideo: " + videoUriString);


        for (int i = 0; i < pathForAudio.size(); i++) {


            //  Log.d("TAG", "playvideo: " + uriaudio);
            Uri uriaudiotem = Uri.fromFile(pathForAudio.get(i));

            String audioUriWithoutFormat = uriaudiotem.toString().substring(0, uriaudiotem.toString().length() - 4);
            // Log.d("TAG", "" + audioUriWithoutFormat + "   " + videoUriWithoutFormat);


            if (audioUriWithoutFormat.equals(videoUriWithoutFormat)) {
                uriaudio = Uri.fromFile(pathForAudio.get(i));
            }

        }

        //  Log.d("TAG", "playvideo: " + audioUriString);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "exoplayer"));


        if (uriaudio == null) {


            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uri);
            player.prepare(mediaSource);
        } else {


            MediaSource mediaSourcevideo = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uri);
            MediaSource mediaSourceforAudio = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uriaudio);

            MergingMediaSource mergedSource = new MergingMediaSource(mediaSourcevideo, mediaSourceforAudio);

            player.prepare(mergedSource);
        }

        player.setPlayWhenReady(true);
        player.addListener(this);
        playingVideo=true;



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

    @Override
    public void onBackPressed() {
        if (playingVideo) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                    player.release();
                } else {
                    player.stop();
                    player.release();
                }
            }
            playingVideo=false;
            playerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case SimpleExoPlayer.STATE_READY:



    }
}}
