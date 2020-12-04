package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.android.dacooker.R;
import es.android.dacooker.activities.StepsRecipeCooking;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.SoundService;
import es.android.dacooker.utilities.NotificationsPush;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepRecipeFragment extends Fragment {

    StepModel step;
    int total;

    StepsRecipeCooking parentActivity;
    TextView stepOfTotal, stepDescription, crono;
    ImageView arrowBack, arrowNext;
    Button btnFinish, btnCancel;

    //Chrono
    ImageButton btnTimerPlay, btnTimerStop, btnTimerPause;
    LinearLayout timerLayout;
    String stepTimeMillis;
    long totalDuration, timeRemaining = 0;
    boolean isPaused, isStopped, musicOn, isCancel;

    //Notifications
    AlarmManager am;
    PendingIntent pending;

    public StepRecipeFragment() {
        // Required empty public constructor
    }

    public StepRecipeFragment(StepModel s, int t) {
        this.step = s;
        this.total = t;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_step_recipe, container, false);

        initParameters(v);
        if(step.isRequiredTimer()) {
            initTimerParameters();
            initTimerButtons();
        }
        setView();
        initButtons();

        return v;
    }

    private void initParameters(View v){
        this.stepOfTotal = v.findViewById(R.id.step_fragment_orderOfTotal);
        this.stepDescription = v.findViewById(R.id.step_fragment_description);
        this.arrowBack = v.findViewById(R.id.step_fragment_btnLeft);
        this.arrowNext = v.findViewById(R.id.step_fragment_btnRight);
        this.btnCancel = v.findViewById(R.id.step_cooking_btnCancel);
        this.btnFinish = v.findViewById(R.id.step_cooking_btnFinish);

        this.timerLayout = v.findViewById(R.id.step_fragment_timer_layout);
        this.btnTimerPlay = v.findViewById(R.id.step_fragment_btn_timer_start);
        this.btnTimerPause = v.findViewById(R.id.step_fragment_btn_timer_pause);
        this.btnTimerStop = v.findViewById(R.id.step_fragment_btn_timer_stop);
        this.crono = v.findViewById(R.id.step_cooking_chrono);
        isStopped = true;

        parentActivity = (StepsRecipeCooking) getActivity();
    }


    //CountdownTimer Init
    private void initTimerParameters(){

        //Visibilidad y Duracion del Timer
        this.timerLayout.setVisibility(View.VISIBLE);
        long durationHours = TimeUnit.HOURS.toMillis(step.getTimerHour());
        long durationMinutes = TimeUnit.MINUTES.toMillis(step.getTimerMinute());
        this.totalDuration = durationHours + durationMinutes;

        //Tiempo Total en MILLIS
        this.stepTimeMillis = String.format(Locale.ENGLISH, "%02d : %02d : %02d",
                TimeUnit.MILLISECONDS.toHours(totalDuration),
                TimeUnit.MILLISECONDS.toMinutes(totalDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalDuration)),
                TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));

        this.crono.setText(this.stepTimeMillis);

        this.btnTimerPause.setEnabled(false);
        this.btnTimerStop.setEnabled(false);
    }

    private void initTimerButtons(){

        btnTimerPause.setOnClickListener(view -> {
            isPaused = true;
            btnTimerPause.setEnabled(false);
            btnTimerPlay.setEnabled(true);
            am.cancel(pending);
            Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_pause), Toast.LENGTH_SHORT).show();
        });

        btnTimerStop.setOnClickListener(view -> {

            if(!isStopped) {    //No ha finalizado el Timer
                isStopped = true;
                isPaused = false;
                crono.setText(stepTimeMillis);
                btnTimerPlay.setEnabled(true);
                btnTimerPause.setEnabled(false);
                btnTimerStop.setEnabled(false);
                Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_stop), Toast.LENGTH_SHORT).show();
                am.cancel(pending);
            } else {    // Timer Finalizado
                this.stopMusic();
            }
        });

        btnTimerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTimerPlay.setEnabled(false);
                btnTimerPause.setEnabled(true);
                btnTimerStop.setEnabled(true);
                if (isStopped) {
                    Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_start), Toast.LENGTH_SHORT).show();
                    isStopped = false;
                    createNewTimer(totalDuration);
                    initNotifications(totalDuration);

                } else if (isPaused) {
                    Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_resume), Toast.LENGTH_SHORT).show();
                    isPaused = false;
                    createNewTimer(timeRemaining);
                    initNotifications(timeRemaining);
                }
            }
        });

    }

    private void createNewTimer(long timeMillis){
        new CountDownTimer(timeMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isStopped) cancel();
                else {
                    //Convert millis to hours, minutes and seconds
                    String time = String.format(Locale.ENGLISH, "%02d : %02d : %02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    crono.setText(time);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                crono.setText(getString(R.string.step_recipe_finished));
                isStopped = true;
                btnTimerPause.setEnabled(false);
                btnTimerPlay.setEnabled(false);

                Intent finished = new Intent(getActivity(), SoundService.class);
                getActivity().startService(finished);
                musicOn = true;
            }
        }.start();
    }

    //View - Button Init
    @SuppressLint({"SetTextI18n"})
    private void setView(){

        if(this.step.getStepOrder() == total) {
            this.btnFinish.setVisibility(View.VISIBLE);
            this.arrowNext.setVisibility(View.GONE);
        }

        if(this.step.getStepOrder() == 1) {
            this.btnCancel.setVisibility(View.VISIBLE);
            this.arrowBack.setVisibility(View.GONE);
        }

        this.stepOfTotal.setText(getString(R.string.step_cooking_stepOfTotal_one) + " "
                + step.getStepOrder() + " " + getString(R.string.step_cooking_stepOfTotal_two)
                + " " + this.total);
        this.stepDescription.setText(this.step.getDescription());
    }

    private void initButtons(){

        arrowBack.setOnClickListener(view -> {
            this.parentActivity.goBackStep();
            this.stopMusic();
        });

        arrowNext.setOnClickListener(view -> {
            this.parentActivity.goNextStep();
            this.stopMusic();
        });

        btnCancel.setOnClickListener(view -> {
            parentActivity.finish();
            this.stopMusic();
            isCancel = true;
        });

        btnFinish.setOnClickListener(view -> {
            if(!parentActivity.checkTimers())
                Toast.makeText(getActivity(), getString(R.string.step_cooking_stopped_timers_err), Toast.LENGTH_SHORT).show();
            else this.parentActivity.finishCooking();
            this.stopMusic();
        });
    }

    //Utilities
    public boolean isActive(){
        return isStopped;
    }

    public void initNotifications(long millis){
        //Intent
        Intent intent = new Intent(getContext(), NotificationsPush.class);
        pending = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        long nowTime = System.currentTimeMillis();
        am.set(AlarmManager.RTC_WAKEUP, nowTime + millis, pending);
    }

    private void stopMusic(){
        if(musicOn) {
            Intent stopMusic = new Intent(getActivity(), SoundService.class);
            getActivity().stopService(stopMusic);
            musicOn = false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!isCancel) Toast.makeText(getContext(), getString(R.string.cooking_do_not_close), Toast.LENGTH_LONG).show();
    }
}