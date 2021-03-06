package com.example.safkat.messagelater.Reciever;


import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.safkat.messagelater.Activity.MainActivity;
import com.example.safkat.messagelater.Class.SaveLog;
import com.example.safkat.messagelater.Class.SaveLogOpenHelper;
import com.example.safkat.messagelater.R;

/**
 * Created by Safkat on 7/13/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private String num,u_id,msg;
    private Intent intent;


    @Override
    public void onReceive(Context context, Intent intent) {
        num=intent.getStringExtra("num");
        u_id=intent.getStringExtra("u_id");
        msg=intent.getStringExtra("msg");



        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(num, null, msg, null, null);
            SendNotificationToTheUi(context);
            DeleteLogFromDatabse(u_id, context);

        }catch (Exception e) {
           // Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("sms Error",e.toString());
            Dialog dialog = new Dialog(context);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

            builder.setMessage("Please allow all the required permissions")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });

            dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
        }

    }

    private void DeleteLogFromDatabse(String u_id, Context context) {

        SaveLogOpenHelper mDbhelper=new SaveLogOpenHelper(context);
        SQLiteDatabase db=mDbhelper.getWritableDatabase();

        String selection= SaveLog.DefineTable.COLUMN_NAME_UID + " LIKE ?";
        String [] selectionArgs={u_id};
        db.delete(SaveLog.DefineTable.TABLE_NAME,selection,selectionArgs);
    }

    private void SendNotificationToTheUi(Context context) {
        NotificationCompat.Builder myBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Message Later")
                .setContentText("SMS sent!");

        Intent resultIntent=new Intent(context, MainActivity.class);


        PendingIntent resultPendingIntent=PendingIntent.getActivity(context,0,resultIntent,0);

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,myBuilder.build());

       // myBuilder.setContent(resultPendingIntent);


    }
}
