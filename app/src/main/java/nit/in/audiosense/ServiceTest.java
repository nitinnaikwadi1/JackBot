package nit.in.audiosense;

/**
 * Created by nitin on 5/8/16.
 */

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ServiceTest extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 30 * 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Log.e("Log", "Running");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
                    Log.i("am.isWiredHeadsetOn()", am.isWiredHeadsetOn()+"");

                    if (am.isWiredHeadsetOn()){

                        Intent home = new Intent(ServiceTest.this,MainActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        home.putExtra("AUDIO_FLAG",true);
                        startActivity(home);
                        //Toast.makeText(ServiceTest.this, "Running : "+am.isWiredHeadsetOn(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.android.techtrainner");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }
}
