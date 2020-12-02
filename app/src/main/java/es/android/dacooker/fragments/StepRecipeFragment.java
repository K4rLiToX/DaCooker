package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class StepRecipeFragment extends Fragment {

    StepModel step;
    int total;

    StepsRecipeCooking parentActivity;
    TextView stepOfTotal, stepDescription;
    ImageView arrowBack, arrowNext;
    Button btnFinish, btnCancel;

    //Chrono
    ImageButton btnTimerPlay, btnTimerStop, btnTimerPause;
    LinearLayout timerLayout;
    String stepTimeMillis;
    long totalDuration;
    long timeRemaining = 0;
    TextView crono;
    boolean isPaused;
    boolean isStopped;


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
            timerButtons();
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
    }

    private void timerButtons(){

        btnTimerPause.setOnClickListener(view -> {
            isPaused = true;
            btnTimerPause.setEnabled(false);
            btnTimerPlay.setEnabled(true);
            Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_pause), Toast.LENGTH_SHORT).show();
        });

        btnTimerStop.setOnClickListener(view -> {
            isStopped = true;
            isPaused = false;
            crono.setText(stepTimeMillis);
            btnTimerPlay.setEnabled(true);
            btnTimerPause.setEnabled(false);
            btnTimerStop.setEnabled(false);
            Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_stop), Toast.LENGTH_SHORT).show();
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

                } else if (isPaused) {
                    Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_resume), Toast.LENGTH_SHORT).show();
                    isPaused = false;
                    createNewTimer(timeRemaining);
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
                crono.setText("Finished!");
                isStopped = true;
                btnTimerPause.setEnabled(false);
                btnTimerStop.setEnabled(false);
            }
        }.start();
    }

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
        });

        arrowNext.setOnClickListener(view -> {
            this.parentActivity.goNextStep();
        });

        btnCancel.setOnClickListener(view -> {
            parentActivity.finish();
        });

        btnFinish.setOnClickListener(view -> {
            if(!parentActivity.checkTimers())
                Toast.makeText(getActivity(), getString(R.string.step_cooking_stopped_timers_err), Toast.LENGTH_SHORT).show();
            else this.parentActivity.finishCooking();
        });
    }

    public boolean isActive(){
        return isStopped;
    }
}