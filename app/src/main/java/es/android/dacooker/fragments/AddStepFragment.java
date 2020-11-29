package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStepFragment extends Fragment {

    CheckBox cb_timer;
    TextInputLayout til_hours, til_minute;
    EditText til_order, til_description, input_hours, input_minute;
    Button btnAdd;
    Button btnFinish;
    RecyclerView rw;
    AddStepRecyclerAdapter rwAdapter;
    List<StepModel> stepsList;

   public AddStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        stepsList = new ArrayList<>();
        rwAdapter = new AddStepRecyclerAdapter(getActivity().getApplicationContext(), stepsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_step, container, false);

        //Initialize Elements
        btnAdd = v.findViewById(R.id.add_step_btnAdd);

        //Quitar la linea siguiente segun se decida hacerlo. Solo es por probar.
        //btnFinish = v.findViewById(R.id.btn_finish_recipe);
        //btnFinish.setVisibility(View.INVISIBLE);
        ////// Para que te hagas una idea, ahora mismo no hay boton en el fragment de Steps.
        // El boton es de la Activity y solo aparece en Step. Hay que discutir esto
        cb_timer = v.findViewById(R.id.checkbox_requiredTimer);
        til_order = v.findViewById(R.id.step_order_input);
        til_description = v.findViewById(R.id.step_description_input);

        til_hours = v.findViewById(R.id.til_stepTimer_hours);
        til_minute = v.findViewById(R.id.til_stepTimer_minutes);
        input_hours = v.findViewById(R.id.step_timerTime_hours_input);
        input_minute = v.findViewById(R.id.step_timerTime_minutes_input);

        rw = v.findViewById(R.id.add_step_recyclerView);
        rw.setAdapter(rwAdapter);

        cb_timer.setOnClickListener(view -> {
            boolean timerOn = cb_timer.isChecked();
            til_hours.setEnabled(timerOn);
            til_minute.setEnabled(timerOn);
        });

        btnAdd.setOnClickListener(view -> {

            boolean timerOn = cb_timer.isChecked();

            if(validFields(timerOn)) {
                StepModel step = new StepModel();
                step.setStepOrder(Integer.parseInt(til_order.getText().toString()));
                step.setDescription(til_description.getText().toString());
                step.setRequiredTimer(timerOn);

                String timer = null;
                if(timerOn) {
                    step.setTimerHour(Integer.parseInt(input_hours.getText().toString().trim()));
                    step.setTimerMinute(Integer.parseInt(input_minute.getText().toString().trim()));
                }

                stepsList.add(step);

                rwAdapter.notifyItemInserted(stepsList.size()-1);
                til_order.setText("");
                til_description.setText("");

                if(timerOn){
                    cb_timer.setChecked(false);
                    input_hours.setText("");
                    input_minute.setText("");
                    til_hours.setEnabled(false);
                    til_minute.setEnabled(false);
                }


            } else {
                //Errores
                Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        });

        return v;
    }

    private boolean validFields(boolean timerOn){  //Cambiar para Validación Bien
        if(til_order.getText().toString().trim().equalsIgnoreCase("") || til_order.getText().toString() == null ) return false;
        if(til_description.getText().toString().trim().equalsIgnoreCase("") || til_order.getText().toString() == null ) return false;

        String hour = input_hours.getText().toString().trim();
        String minute = input_minute.getText().toString().trim();
        if(timerOn && (hour.equalsIgnoreCase("") || hour == null) ) return false;
        return !timerOn || (!minute.equalsIgnoreCase("") && minute != null &&
                (Integer.parseInt(hour) == 0 || Integer.parseInt(minute) <= 59));
    }
}