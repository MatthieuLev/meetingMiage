package fr.miage.app.meetingmiage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SmsReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Pattern pattern;
        Matcher matcher;
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    String format = bundle.getString("format");
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    pattern = Pattern.compile("(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)");
                    matcher = pattern.matcher(message);

                    while (matcher.find()) {
                        Log.d("myDebug", "match");

                        if (message.contains("asking")) {
                            Log.d("myDebug", "asking");
                            redirectionManager(context, phoneNumber, message, "asking");

                        } else if (message.contains("accept")) {
                            Log.d("myDebug", "accept");
                            redirectionManager(context, phoneNumber, message, "accept");
                        }
                    }

                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    private void redirectionManager(Context context, String phoneNumber, String message, String type) {
        Intent myIntent = new Intent(context, ReceptionActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra("phoneNumber", phoneNumber);
        myIntent.putExtra("message", message);
        myIntent.putExtra("type", type);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager(context, pendingIntent, phoneNumber);
    }

    private void notificationManager(Context c, PendingIntent i, String phoneNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("some_channel_id", "Some Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            NotificationManager notificationManager = c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, "some_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(c.getString(R.string.app_name))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(i)
                .setAutoCancel(true)
                .setContentText("Le numéro " + phoneNumber + " vous propose un rendez-vous !");

        NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}