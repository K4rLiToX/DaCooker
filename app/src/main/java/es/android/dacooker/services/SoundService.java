package es.android.dacooker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import es.android.dacooker.R;

/*
    Servicio iniciado en el momento que el timer de un Step del cocinado termina
        Su funcion es activar una musica de alarma, asi como hacer vibrar el telefono
        Es posible detenerlo tambien, claramente
 */

public class SoundService extends Service {

    MediaPlayer mp;
    Vibrator vibrator;
    long[] pattern;

    public void onCreate(){ //Crear y configurar el servicio de música
        super.onCreate();

        //Creamos el reproductor y le decimos que cancion sonara al activarse
        mp = MediaPlayer.create(this, R.raw.cookingmamatitle);
        mp.setLooping(true);    //Permitimos su repeticion cuando acabe la cancion (si aun no ha sido desactivada)
        mp.setVolume(40, 40);   //Seteamos el volumen con el que sonara la cancion

        //Vibracion: Creamos la vibracion dentro del conetxto de la aplicacion : Activity StepsRecipeCooking
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        pattern = new long[]{0, 500, 1000}; //Establecemos el patron de vibracion, de manera que vaya aumentando

    }

    //Metodo realizado cuando iniciamos el servicio
    public int onStartCommand(Intent intent, int flags, int startId){   //Comenzar el servicio
        mp.start(); //Comienza a sonar la musica
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));   //Comienza la vibracion
        return START_STICKY;    //Devuelve esto cuando el proceso ha sido matado o finalizado. Devuelve al estado inicial.
    }

    public void onDestroy(){    //Para poder finalizar con la música. Si está sonando, la detiene
        super.onDestroy();

        if(mp.isPlaying()) mp.stop();   //Detenemos la musica si esta sonando
        if(vibrator.hasVibrator()) vibrator.cancel();   //Detenemos la vibracion si esta vibrando

        //Liberar recursos de memoria
        mp.release();
        mp = null;
        vibrator = null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

}
