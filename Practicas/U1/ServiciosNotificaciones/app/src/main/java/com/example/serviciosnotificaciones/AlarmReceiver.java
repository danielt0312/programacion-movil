package com.example.serviciosnotificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
//import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final int MY_NOTIFICATION_ID=1;
	NotificationManager notificationManager;
	Notification myNotification;
	public static final int NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Cuando se manda la alarma, se obtienen los datos enviados desde la actividad principal
        Integer I1= intent.getExtras().getInt("R");
        String C1= intent.getExtras().getString("E1");
        String C2= intent.getExtras().getString("E2");
        String C3= intent.getExtras().getString("E3");

        Intent myIntent = new Intent(context, DoSomething.class);
        // se mandan hacia la actividad secundaria (solo que el usuario de clic en la notificación!!)
		myIntent.putExtra("I1",I1);
		myIntent.putExtra("P1",C1);
		myIntent.putExtra("P2",C2);
		myIntent.putExtra("P3",C3);


		PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), myIntent, PendingIntent.FLAG_IMMUTABLE);

		// Send the notification
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification replyNotification;

		// Si ES OREO, MÉTELO EN LECHE Y TRAGATELO (SIN ALBUR!)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//int notifyID = 1;
			String CHANNEL_ID = "my_channel_01";// The id of the channel
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
					"Identificador tipo STRING para el CANAL",
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);

			// Create reply notification
			replyNotification =
					new Notification.Builder(context, CHANNEL_ID)
							.setGroupSummary(true)
							.setGroup("Aplicacion")
							.setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Ponte a Trabajar !"+I1)
                            .setContentText("A ver haz algo por favor !! : "+C1+C2+C3)
                            .setTicker("Notification!"+I1)
							.setAutoCancel(true)
							.setContentIntent(pendingIntent)
							.setChannelId(CHANNEL_ID) // sutil diferencia entre uno y otro!
							.build();


		}
		else {
			// Create reply notification
			replyNotification =
					new Notification.Builder(context)
							.setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Ponte a Trabajar !"+I1)
                            .setContentText("A ver haz algo por favor !! : "+C1+C2+C3)
                            .setTicker("Notification!"+I1)
							.setContentIntent(pendingIntent)
							.build();
		}


		//notificationManager.notify(NOTIFICATION_ID, newMessageNotification);
		notificationManager.notify(NOTIFICATION_ID+I1,  replyNotification);



/*        Integer I1= intent.getExtras().getInt("R");
        String C1= intent.getExtras().getString("E1");
        String C2= intent.getExtras().getString("E2");
        String C3= intent.getExtras().getString("E3");
		//Toast.makeText(context, "Alarm received!"+C1+C2+C3, Toast.LENGTH_LONG).show();

	    Intent myIntent = new Intent(context, DoSomething.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP); // NO ESTABA EN EL ORIGINAL
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra("I1",I1);
        myIntent.putExtra("P1",C1);
        myIntent.putExtra("P2",C2);
        myIntent.putExtra("P3",C3);
	    PendingIntent pendingIntent = PendingIntent.getActivity(
	    	      context, 
	    	      I1,
	    	      myIntent,
                //PendingIntent.FLAG_CANCEL_CURRENT);
                  PendingIntent.FLAG_UPDATE_CURRENT);
                  //PendingIntent.FLAG_ONE_SHOT);
	    	      //Intent.FLAG_ACTIVITY_NEW_TASK); // ORIGINAL
	    
	    myNotification = new NotificationCompat.Builder(context)
	    		.setContentTitle("Ponte a Trabajar !"+I1)
	    		.setContentText("A ver haz algo por favor !! : "+C1+C2+C3)
	    		.setTicker("Notification!"+I1)
	    		.setWhen(System.currentTimeMillis())
	    		.setContentIntent(pendingIntent)
	    		.setDefaults(Notification.DEFAULT_SOUND)
	    		.setAutoCancel(true)
	    		.setSmallIcon(R.mipmap.ic_launcher)
	    		.build();
	    
	    notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    //notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
        notificationManager.notify(I1, myNotification); */
/*
		int notificationId = 1;
		String channelId = "channel-01";
		String channelName = "Channel Name";
		int importance = NotificationManager.IMPORTANCE_HIGH;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel mChannel = new NotificationChannel(
					channelId, channelName, importance);
			notificationManager.createNotificationChannel(mChannel);
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
				//.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Ponte a Trabajar !")
				.setContentText("A ver haz algo por favor !! ");

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				0,
				PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);

		notificationManager.notify(notificationId, mBuilder.build()); */

		/*notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			// Create or update.
			int notifyID = 1;
			String CHANNEL_ID = "my_channel_01";// The id of the channel
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);

			NotificationCompat notification =
					new NotificationCompat.Builder(this,CHANNEL_ID)
							.setSmallIcon(R.mipmap.ic_launcher)
							.setContentTitle("My notification")
							.setContentText("Hello World!")
							.setChannelId(CHANNEL_ID).build();

		}

*/
	}

}
