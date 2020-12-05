package es.android.dacooker.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import es.android.dacooker.R;

/*
    Clase con metodos estaticos cuya funcion es la creacion de notifiaciones en determinados casos
        Se usa en StepsRecipeCooking, concretamente en los fragmentos de la misma para que,
        cuando la app esta ejecutandose en background, ademas de la musica y vibracion
        envie una notificacion al telefono
 */

//Hereda de la clase BroadcastReceiver que permite registrar eventos que ocurriran en el sistema
    //Avisan a todos los metodos onReceive que definamos en la aplicacion
    //Por ello es necesario el canal de notifiacion
public class NotificationsPush extends BroadcastReceiver {

    //Crea un canal que se usara para emitir las notificaciones : estas siempre se envian
        // a traves de canales con un nombre definido y una descripcion del mismo
        //Se usa en la MainActivity para iniciar el canal lo antes posible : asi las
            // notifiaciones podran establecerse desde el inicio
    public static void createNotifyChannel(Context c, String channelName, String channelDescription){
        createNotificationChannel(c, channelName, channelDescription);
    }

    private static void createNotificationChannel(Context context, String channelName, String channelDescription) {
        // Create NotificationChannel (only API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   //Solo podemos usar estos canales en versiones mayores a Android Oreo
            int importance = NotificationManager.IMPORTANCE_HIGH;   //Definimos la importancia de las notificaciones del canal
            //Creamos el canal con un id con el que registraremos sus notifiaciones cuando las usemos, su nombre e importancia
            NotificationChannel channel = new NotificationChannel("notifyEat", channelName, importance);
            channel.setDescription(channelDescription); //Seteamos la descripcion

            //Llamamos a la clase NotificationManager quien tiene la capacidad de asociar el canal al sistema y creamos el canal
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    //Metodo para crear notifiaciones. Requiere de:
        //contexto donde se ejecutaran
        //titulo y descripcion: informacion que se vera en la notificacion
        //id del canal al que se asociara la notificacion
    public void createNotification(Context context, String title, String content, String channel_id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_notify_icon)    //Seteamos... el icono
                .setContentTitle(title)        //...titulo
                .setContentText(content)    //...descripcion
                .setPriority(NotificationCompat.PRIORITY_HIGH); //...prioridad de la notificacion ->
                                                                    // se ve afectada por la del canal al que se asocia

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);

        //Setamos la vibracion producida cuando la notifiacion llegue, en este caso:
            //1 seg de vibracion + 1 segundo de pausa (x2)
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
        //Establecemos el LED que se vera al llegar la notificacion: rojo por el color del logo
        builder.setLights(Color.RED, 3000, 3000);

        //ID de la notificacion dentro del sistema: cada notifiacion debe tener uno unico
        nm.notify(200, builder.build());

    }

    @Override   //En este caso soo tenemos una notificacion en el sistema, asi que creamos aqui el
                        //onReceive que creara la notificacion
    public void onReceive(Context context, Intent intent) {

        this.createNotification(context, context.getString(R.string.notify_title),
                context.getString(R.string.notify_description), "notifyEat");

    }
}
