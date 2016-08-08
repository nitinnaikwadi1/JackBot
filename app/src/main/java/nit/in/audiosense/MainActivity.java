package nit.in.audiosense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnCompletionListener{

    LinearLayout stop;
    ImageView stopPlayer;
    ImageView playNext;
    ImageView playPrevious;

    // Media Player
    private  MediaPlayer mp;

    private SongsManager songManager;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    private int currentSongIndex = 0;
    private boolean isShuffle = true;
    private boolean isRepeat = false;

    boolean audioFlag= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        audioFlag = getIntent().getBooleanExtra("AUDIO_FLAG",false);

        if(audioFlag){
            turnOnMusic();
        }else{
            turnOffMusic();
        }

        playNext = (ImageView)findViewById(R.id.playNext);
        playPrevious = (ImageView)findViewById(R.id.playPrevious);
        stop = (LinearLayout)findViewById(R.id.stop);
        stopPlayer = (ImageView)findViewById(R.id.stopPlayer);


        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // check if next song is there or not
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });


        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */

        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffMusic();
            }
        });

        stopPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffMusic();
            }
        });

        startService(new Intent(this, ServiceTest.class));

        //LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            if (text.equals("Headset is plugged in.")){
                turnOnMusic();
            }
        }
    };

    public void turnOnMusic(){
        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager();

        // Listeners
        mp.setOnCompletionListener(this); // Important

        // Getting all songs list
        songsList = songManager.getPlayList();

        Random rand = new Random();
        currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
        playSong(currentSongIndex);
    }

    public void turnOffMusic(){
        if (mp != null){
            mp.pause();
            //mp.release();
        }
    }

    /**
     * Function to play a song
     * @param songIndex - index of song
     * */
    public void  playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
    // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }
}