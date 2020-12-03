package es.android.dacooker.dialogManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;

public class CustomDialog extends DialogFragment {
    //TAG
    private static final String TAG = "CustomDialog";
    //Views
    RadioGroup radioGroup;
    LinearLayout search_dialog_time_layout;
    TextInputEditText input_hours, input_minutes;
    RelativeLayout search_dialog_mealType_layout;
    AutoCompleteTextView selectMealType;
    Button btnSearch, btnDismiss;

    //Mealtype Adapters
    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;

    //Interface
    private OnDialogInputListener onDialogInputListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container, false);

        adapter =  new ArrayAdapter<>(getActivity(), R.layout.meal_type_dropdown_item, MEALTYPES);
        this.selectMealType = view.findViewById(R.id.search_dialog_select_mealType);
        this.selectMealType.setAdapter(adapter);

        this.radioGroup = view.findViewById(R.id.search_dialog_radio_group);
        this.search_dialog_time_layout = view.findViewById(R.id.search_dialog_time_layout);
        this.input_hours = view.findViewById(R.id.search_dialog_input_hours);
        this.input_minutes = view.findViewById(R.id.search_dialog_input_minutes);
        this.search_dialog_mealType_layout = view.findViewById(R.id.search_dialog_mealType_layout);

        this.btnSearch = view.findViewById(R.id.search_dialog_btn_search);
        this.btnDismiss = view.findViewById(R.id.search_dialog_btn_dismiss);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                changeSelection(radioGroup, i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSearch();
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });


        return view;
    }

    private void changeSelection(RadioGroup radioGroup, int i){
        if(i == R.id.search_dialog_radio_mealType){
            search_dialog_time_layout.setVisibility(View.GONE);
            search_dialog_mealType_layout.setVisibility(View.VISIBLE);
        } else if(i == R.id.search_dialog_radio_time){
            search_dialog_time_layout.setVisibility(View.VISIBLE);
            search_dialog_mealType_layout.setVisibility(View.GONE);
        } else {
            search_dialog_time_layout.setVisibility(View.GONE);
            search_dialog_mealType_layout.setVisibility(View.GONE);
        }
    }

    private void filterSearch(){
        int radioChecked = radioGroup.getCheckedRadioButtonId();

        if(radioChecked == R.id.search_dialog_radio_mealType){
            String meal = selectMealType.getText().toString();

            if(validateMealType(meal)){
                BBDD_Helper db = new BBDD_Helper(getActivity());
                List<RecipeModel> resultList;
                try {
                    resultList = BD_Operations.getRecipesByMealType(MealType.valueOf(meal), db);
                    if(resultList.isEmpty()){
                        Toast.makeText(getActivity(), "No " + meal + " Recipes", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        onDialogInputListener.sendResultList(resultList);
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Error on Retreiving Data", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.search_dialog_mealType_select_error, Toast.LENGTH_LONG).show();
            }
        } else if(radioChecked == R.id.search_dialog_radio_time){
            String hour = input_hours.getText().toString().trim();
            String minute = input_minutes.getText().toString().trim();

            if(validateTime(hour, minute)){
                BBDD_Helper db = new BBDD_Helper(getActivity());
                List<RecipeModel> resultList;
                try {
                    resultList = BD_Operations.getRecipesByLessExecutionTime(Integer.parseInt(hour), Integer.parseInt(minute), db);
                    if(resultList.isEmpty()) Toast.makeText(getActivity(), "No Recipes Matching that Time", Toast.LENGTH_SHORT).show();
                    else {
                        Log.e(TAG, resultList.size()+"");
                        onDialogInputListener.sendResultList(resultList);
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Error on Retreiving Data", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.search_dialog_time_inputs_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.search_dialog_no_radio_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateMealType(String mealType){
        return !mealType.isEmpty();
    }

    private boolean validateTime(String hour, String minute){
        return !hour.isEmpty() && !minute.isEmpty() && Integer.parseInt(hour) >= 0 && Integer.parseInt(minute) >= 0 && Integer.parseInt(minute) <= 59;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDialogInputListener = (OnDialogInputListener) getActivity();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    public interface OnDialogInputListener {
        void sendResultList(List<RecipeModel> resultList);
    }
}
