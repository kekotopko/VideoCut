package com.example.tm.videocut;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

public class Cut extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "logs";


    Button button;
    ImageButton buttonplay;
    EditText editTextend, editTextstart;
    File file;
    File file2;
    int start, end;
    int count = 5;
    SurfaceView surfaceview;
    RangeBar rangeBar;
    MediaPlayer mediaPlayer;

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut);

        editTextend = (EditText) findViewById(R.id.editText2);
        editTextstart = (EditText) findViewById(R.id.editText);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);


        buttonplay = (ImageButton) findViewById(R.id.imageButtonplay);
        buttonplay.setOnClickListener(this);

        file = new File(Environment.getExternalStorageDirectory(), "гавне.mp4");
        file2 = new File(Environment.getExternalStorageDirectory(), "висево " + count + ".mp4");

        String path = file.getAbsolutePath();
        surfaceview = (SurfaceView) findViewById(R.id.videoView);

        rangeBar = (RangeBar) findViewById(R.id.filter_range_bar);
        rangeBar.setMin(0);
        rangeBar.setMax(1000);


        rangeBar.setValueChangeListener(new RangeBar.ValueChangeListener() {
            @Override
            public void onSlide(double start, double end) {
                int stort = (int) ((int) rangeBar.getCurentstart() / 1000D * (double) mediaPlayer.getDuration());
                int endd = (int) ((int) rangeBar.getCurentend() / 1000D * (double) mediaPlayer.getDuration());
                mediaPlayer.seekTo(stort);
                if (stort == endd) {
                    mediaPlayer.stop();
                }

            }
        });


        try {
            playVideo(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void playVideo(String path) throws IOException {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
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
        mediaPlayer.prepare();
        mediaPlayer.start();


    }

    public void startPlayProgressUpdater() {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {

            mediaPlayer.start();
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button:
                try {
                    start = (int) ((int) rangeBar.getCurentstart() / 1000D * (double) mediaPlayer.getDuration());
                    end = (int) ((int) rangeBar.getCurentend() / 1000D * (double) mediaPlayer.getDuration());
                    Mp4Cutter2.startTrim(file, file2, start, end);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                count++;
                break;
            case R.id.imageButtonplay:
                startPlayProgressUpdater();
                break;

        }
    }

}
