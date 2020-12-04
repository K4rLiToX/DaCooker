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

    Animation image_anim, text_anim;
    private LinearLayout layout;
    RelativeLayout relLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        //Set initial animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        image_anim = AnimationUtils.loadAnimation(this, R.anim.image_animation);
        text_anim = AnimationUtils.loadAnimation(this, R.anim.text_animation);

        layout = findViewById(R.id.splash_linear_developers);
        relLogo = findViewById(R.id.splash_relative_image);

        layout.setAnimation(text_anim);
        relLogo.setAnimation(image_anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                //set animation transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);

    }

}