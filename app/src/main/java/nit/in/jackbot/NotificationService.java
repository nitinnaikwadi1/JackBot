package nit.in.jackbot;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by nitin on 3/8/16.
 */

public class NotificationService extends NotificationListenerService implements MediaPlayer.OnCompletionListener{

    Context context;

    // Media Player
    private  MediaPlayer mp;

    private Handler mHandler = new Handler();
    private SongsManager songManager;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    private int currentSongIndex = 0;
    private boolean isShuffle = true;
    private boolean isRepeat = false;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();

      /*  Log.i("Package",pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);*/

        if (text.equals("Headset is plugged in.")){
            //start playing music
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NotificationService.this, "Headset Plugged!", Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName comp = new ComponentName("cn.zte.music", "cn.zte.music.MediaPlaybackActivity");
                    intent.setComponent(comp);
                    startActivity(intent);*/

                    turnOnMusic();
                }
            });
        }

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence("android.text").toString();

        if (text.equals("Headset is plugged in.")){
            //stop playing music
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(NotificationService.this, "Headset Removed!", Toast.LENGTH_SHORT).show();
                    mp.reset();
                    mp.release();
                    mp.stop();
                }
            });

        }
    }

    public void turnOnMusic(){
        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager();

        // Listeners
        mp.setOnCompletionListener(this); // Important

        // Getting all songs list
        songsList = songManager.getPlayList();

        playSong(currentSongIndex);
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
    public void onCompletion(MediaPlayer arg0) {

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