package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        parentActivity = (StepsRecipeCooking) getActivity();


        //CHRONO
        this.crono = v.findViewById(R.id.step_cooking_chrono);
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
        if(this.step.isRequiredTimer()) this.crono.setVisibility(View.VISIBLE);
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