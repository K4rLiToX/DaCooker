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

    private LinearLayout layout;
    RelativeLayout relLogo;
    Animation image_anim, text_anim;

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

    /*

    private LinearLayout layout;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().hide();

        //Set initial animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //Set activity to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.img = findViewById(R.id.splash_screen_img);
        this.layout = findViewById(R.id.splash_screen_text);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.mytransition);

        this.img.setAnimation(animation);
        this.layout.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                //set animation transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 2500);

    }


     */
}