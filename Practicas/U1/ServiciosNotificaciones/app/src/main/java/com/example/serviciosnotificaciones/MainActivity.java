package com.example.serviciosnotificaciones;
/*
Fuente con ligeras modificaciones:
http://android-er.blogspot.mx/2013/06/start-activity-once-notification-clicked.html
 */


import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.app.TaskStackBuilder;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

    private void sendNotification() {

        Intent myIntent = new Intent(this, DoSomething.class);
        // Datos que van a llegar a la actividad cuando el usuario de click en la notificación
        myIntent.putExtra("I1",1050);
        myIntent.putExtra("P1","Caguamita");
        myIntent.putExtra("P2","Anvorguesa");
        myIntent.putExtra("P3","Vistima");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), myIntent, PendingIntent.FLAG_IMMUTABLE);

        // Send the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification replyNotification;

        // Si ES OREO, MÉTELO EN LECHE Y TRAGATELO (SIN ALBUR!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";// The id of the channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            // Create reply notification
            replyNotification =
                    new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Reply received!" + (int) System.currentTimeMillis())
                            .setContentText("Ya te cargó el PAYASO!!")
                            .setContentIntent(pendingIntent)
                            .setChannelId(CHANNEL_ID)
                            .build();
        }
        else {
            // Create reply notification
            replyNotification =
                    new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Reply received!")
                            .setContentText("Ya te cargó el PAYASO!!"+ (int) System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .build();
        }
        // Cada Notificación debe tener su ID individual para que se muestre por separado
        // En caso de tener el mismo ID, sobrescribe a la anterior con el mismo ID
        notificationManager.notify(NOTIFICATION_ID+(int) System.currentTimeMillis(),  replyNotification);

    }

    Button buttonSetAlarm;
    Button buttonSetAlarm2;
    TextView info;
    int NumeroLectura;
    MyCount counter;

    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = new MyCount(5000,1000);
        info = (TextView)findViewById(R.id.info);
        Calendar now = Calendar.getInstance();

        buttonSetAlarm = (Button)findViewById(R.id.setalarm);
        buttonSetAlarm.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        buttonSetAlarm2 = (Button)findViewById(R.id.setalarm1);
        buttonSetAlarm2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                Calendar tmp = (Calendar) now.clone();
                Calendar tmp2 = (Calendar) now.clone();
                Calendar tmp3 = (Calendar) now.clone();
                Calendar tmp4 = (Calendar) now.clone();
                tmp.add(Calendar.SECOND, 30); // dentro de 30 segs
                tmp2.add(Calendar.MINUTE, 1); // dentro de 1 min
                tmp3.add(Calendar.MINUTE, 2); // dentro de 2 min
                tmp4.add(Calendar.MINUTE, 3); // dentro de 3 min
                setAlarm(tmp,  1001);
                setAlarm(tmp2, 1002);
                setAlarm(tmp3, 1003);
                setAlarm(tmp4, 1004);

                // Adaptandole el Counter Visto en Clase para que cada determinado tiempo arroje una Notificación...
                counter.start();
            }
        });



    }

    //countdowntimer is an abstract class, so extend it and fill in methods
    public class MyCount extends CountDownTimer{

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //tv.setText(”done!”);
            Calendar now = Calendar.getInstance();
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, 1);
            setAlarm(tmp, NumeroLectura);
            NumeroLectura++;
            // this.start(); // Volver a Iniciar
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //tv.setText(”Left: ” + millisUntilFinished/1000);

        }

    }

    private void setAlarm(Calendar targetCal, int R){

        info.append("\n" + "Fecha y Hora de Envio de Notificación:  " + targetCal.getTime() + "***");

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("R",R);
        intent.putExtra("E1"," ROBOT "+R);
        intent.putExtra("E2"," TRAE ");
        intent.putExtra("E3"," UNA COCA COLA");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), R, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


    }


}
