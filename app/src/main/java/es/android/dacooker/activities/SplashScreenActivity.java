package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import es.android.dacooker.R;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView img;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
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
}