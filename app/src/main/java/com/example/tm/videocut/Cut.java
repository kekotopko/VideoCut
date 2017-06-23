package com.example.tm.videocut;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class Cut extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "logs";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    ProgressBar progressBar;
    ImageView play_button;
    Button button;
    File file;
    File file2;
    int start, end;
    int count = 5;
    Context context;
    SurfaceView surfaceview;
    RangeBar rangeBar;
    MediaPlayer mediaPlayer;

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut);

        play_button = (ImageView) findViewById(R.id.play_button);
        play_button.setOnClickListener(this);
        play_button.setVisibility(View.INVISIBLE);

        progressBar= (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        file = new File(getIntent().getStringExtra("uri"));
        file2 = new File(Environment.getExternalStorageDirectory(), String.valueOf(sdf.format( System.currentTimeMillis() )+".mp4"));



        String path = file.getAbsolutePath();
        surfaceview = (SurfaceView) findViewById(R.id.videoView);
        surfaceview.setOnClickListener(this);

        rangeBar = (RangeBar) findViewById(R.id.filter_range_bar);
        rangeBar.setMin(0);
        rangeBar.setMax(1000);

        rangeBar.setValueChangeListener(new RangeBar.ValueChangeListener() {
            @Override
            public void onSlide(double start, double end) {
                int stort = (int) ((int) rangeBar.getCurentstart() / 1000D * (double) mediaPlayer.getDuration());
                int endd = (int) ((int) rangeBar.getCurentend() / 1000D * (double) mediaPlayer.getDuration());
                mediaPlayer.seekTo(stort);

            }
        });

        try {
            playVideo(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }

    public void playVideo(String path) throws IOException {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        surfaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int widh, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        onPrepared(mediaPlayer);



    }

    /*public void startPlayProgressUpdater() throws IOException {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            onPrepared(mediaPlayer);
           // mediaPlayer.start();
        }
    }*/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                    start = (int) ((int) rangeBar.getCurentstart() / 1000D * (double) mediaPlayer.getDuration());
                    end = (int) ((int) rangeBar.getCurentend() / 1000D * (double) mediaPlayer.getDuration());
                Async cutVideo = new Async();
                cutVideo.execute(file,file2,start,end);
                count++;
                break;
            case R.id.videoView:
                mediaPlayer.pause();
                play_button.setVisibility(View.VISIBLE);
                break;
            case R.id.play_button:
                //mediaPlayer.start();
                onPrepared(mediaPlayer);
                play_button.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        mediaPlayer.start();
    }


    class Async extends AsyncTask<Object, Void, Void> {
        File cutedFile;
        @Override
        protected Void doInBackground(Object... params) {
            try {
               Mp4Cutter2.startTrim((File) params[0], (File) params[1], (Integer) params[2], (Integer) params[3]);
                cutedFile = (File) params[1];
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(cutedFile));
            sendBroadcast(intent);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }
    }
}


