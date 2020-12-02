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

public class SoundService extends Service {

    MediaPlayer mp;
    Vibrator vibrator;
    long[] pattern;

    public void onCreate(){ //Crear y configurar el servicio de música
        super.onCreate();
        mp = MediaPlayer.create(this, R.raw.cookingmamatitle);
        mp.setLooping(true);
        mp.setVolume(40, 40);

        //Vibracion
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        pattern = new long[]{0, 500, 1000};

    }

    public int onStartCommand(Intent intent, int flags, int startId){   //Comenzar el servicio
        mp.start();
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        return START_STICKY;    //Devuelve esto cuando el proceso ha sido matado o finalizado. Devuelve al estado inicial.
    }

    public void onDestroy(){    //Para poder finalizar con la música. Si está sonando, la detiene
        super.onDestroy();

        if(mp.isPlaying()) mp.stop();
        if(vibrator.hasVibrator()) vibrator.cancel();

        //Liberar recursos de memoria
        mp.release();
        mp = null;
        vibrator = null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

}
