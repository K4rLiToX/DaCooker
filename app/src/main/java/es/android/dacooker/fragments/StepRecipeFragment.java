package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    Button btnFinish, btnCancel, btnTimer;
    LinearLayout timerLayout;
    boolean isPaused = false;
    long timeRemaining = 0;

    //Provi
    TextView crono;

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
        this.btnTimer = v.findViewById(R.id.step_fragment_btn_timer);
        this.crono = v.findViewById(R.id.step_cooking_chrono);

        parentActivity = (StepsRecipeCooking) getActivity();
    }

    @SuppressLint("SetTextI18n")
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

        //CHRONO
        if(this.step.isRequiredTimer()){

            this.timerLayout.setVisibility(View.VISIBLE);
            long durationHours = TimeUnit.HOURS.toMillis(step.getTimerHour());
            long durationMinutes = TimeUnit.MINUTES.toMillis(step.getTimerMinute());
            long duration = durationHours + durationMinutes;
            btnTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btnTimer.getText().equals("Start")){
                        isPaused = false;
                        btnTimer.setText(getString(R.string.step_cooking_btnTimerStop));
                        new CountDownTimer(duration, 1000){

                            @Override
                            public void onTick(long millisUntilFinished) {
                                if(isPaused){
                                    cancel();
                                } else {
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
                                btnTimer.setVisibility(View.GONE);
                            }
                        }.start();

                    } else if(btnTimer.getText().equals("Stop")){
                        isPaused = true;
                        btnTimer.setText(getString(R.string.step_cooking_btnTimerContinue));
                    } else { //si es continue
                        isPaused = false;
                        btnTimer.setText(getString(R.string.step_cooking_btnTimerStop));
                        long duration = timeRemaining;
                        new CountDownTimer(duration , 1000){

                            @Override
                            public void onTick(long millisUntilFinished) {
                                if(isPaused){
                                    cancel();
                                } else {
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
                                btnTimer.setVisibility(View.GONE);
                            }
                        }.start();
                    }
                }
            });
        }
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
            this.parentActivity.finishCooking();
        });

    }
}