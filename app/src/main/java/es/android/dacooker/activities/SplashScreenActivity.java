package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import es.android.dacooker.R;

public class SplashScreenActivity extends AppCompatActivity {
    //Animacions a ejecutar
    Animation image_anim, text_anim;
    //Vistas a las que asignaremos las animaciones
    LinearLayout layout;
    RelativeLayout relLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cambiamos la actividad a una full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Asignamos el layout correspondiente a mostrar
        setContentView(R.layout.activity_splash_screen);
        //Ocultamos la appbar por defecto
        getSupportActionBar().hide();

        //Reescribimos la animación de inicio de la actividad
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //Asignamos las animaciones a las vistas
        image_anim = AnimationUtils.loadAnimation(this, R.anim.image_animation);
        text_anim = AnimationUtils.loadAnimation(this, R.anim.text_animation);

        layout = findViewById(R.id.splash_linear_developers);
        relLogo = findViewById(R.id.splash_relative_image);

        layout.setAnimation(text_anim);
        relLogo.setAnimation(image_anim);

        //Creamos un manejador de tiempo, es decir, una hebra que se ejecuta en segundo plano que cuenta el tiempo que está el splashscreen en pantalla
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                //Reescribimos la animación de salida de la actividad
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //Destruimos la actividad para que el usuario no pueda volver a ella pulsando el botón atrás
                finish();
            }
        }, 3000);

    }

}