package pro.asdgroup.bizon.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import pro.asdgroup.bizon.activity.MainActivity;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.activity.EventActivity;

/**
 * Created by Voronov Viacheslav on 27.05.2015.
 */
public class GcmIntentService extends IntentService {

    private static final String EVENT_TYPE = "EVENT_INVITE";

    public static final int NOTIFICATION_EVENT_INVITATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
            handleMessage(intent);
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void handleMessage(Intent intent){
        Bundle extras = intent.getExtras();

        JSONObject data;
        try {
            data = new JSONObject(extras.getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String type = data.optString("type");

        if (EVENT_TYPE.equals(type)){
            displayEventInvitation(extras, data);
        }
    }

    private void displayEventInvitation(Bundle extras, JSONObject data){
        String message = extras.getString("message");
        String title = getString(R.string.app_name);
        boolean sound = extras.getBoolean("sound", true);
        boolean vibrate = extras.getBoolean("vibrate", true);

        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EventActivity.ARG_EVENT_ID, data.optInt("event_id"));
        intent.putExtra(EventActivity.ARG_EVENT_NAME, data.optString("event_name"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        /*stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addParentStack(MainActivity.class);*/
        Intent mainIntent = new Intent(this, MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_CANCEL_CURRENT);

        /*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);*/

        sendNotification(title, message, sound, vibrate, contentIntent);
    }

    private void sendNotification(String title, String msg, boolean sound, boolean vibrate, PendingIntent contentIntent) {
        sendNotification(title, msg, sound, vibrate, contentIntent, R.drawable.ic_community_inactive);
    }

    private void sendNotification(String title, String msg, boolean sound, boolean vibrate, PendingIntent contentIntent, int icon) {

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setTicker(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        if (sound){
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(soundUri);
        }

        //if (vibrate){
            //TODO: handle vibration
        //}

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_EVENT_INVITATION_ID, builder.build());
    }
}
