package nit.in.audiosense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nitin on 4/8/16.
 */
public class NotificationService extends NotificationListenerService {

    Context context;

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

        Log.i("Package",pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);

        if (text.equals("Headset is plugged in.")){
            //start playing music
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NotificationService.this, "Headset Plugged!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

        Intent home = new Intent(NotificationService.this,MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(home);

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
                }
            });
        }
    }
}