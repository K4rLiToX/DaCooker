package es.android.dacooker.fragments;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddNewRecipeActivity;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.interfaces.AddRecipeStepClickListener;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStepFragment extends Fragment implements AddRecipeStepClickListener {

    CheckBox cb_timer;
    TextInputLayout til_hours, til_minute;
    EditText til_description, input_hours, input_minute;
    Button btnAddOrUpdate;
    Button btnFinish;
    RecyclerView rw;
    AddStepRecyclerAdapter rwAdapter;
    List<StepModel> stepsList;
    int stepToUpdatePosition;

   public AddStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        stepsList = new ArrayList<>();
        rwAdapter = new AddStepRecyclerAdapter(getActivity().getApplicationContext(), stepsList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_step, container, false);

        //Initialize Elements
        btnAddOrUpdate = v.findViewById(R.id.add_step_btnAdd);

        cb_timer = v.findViewById(R.id.checkbox_requiredTimer);
        til_description = v.findViewById(R.id.step_description_input);

        til_hours = v.findViewById(R.id.til_stepTimer_hours);
        til_minute = v.findViewById(R.id.til_stepTimer_minutes);
        input_hours = v.findViewById(R.id.step_timerTime_hours_input);
        input_minute = v.findViewById(R.id.step_timerTime_minutes_input);

        rw = v.findViewById(R.id.add_step_recyclerView);
        rw.setAdapter(rwAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rw);

        cb_timer.setOnClickListener(view -> {
            boolean timerOn = cb_timer.isChecked();
            til_hours.setEnabled(timerOn);
            til_minute.setEnabled(timerOn);
        });

        btnAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAddOrUpdate.getText().toString().equalsIgnoreCase(getString(R.string.add_step_btnAdd))){
                    StepModel stepModel = new StepModel();
                    addOrUpdate(stepModel, -1);
                } else {
                    addOrUpdate(stepsList.get(stepToUpdatePosition), stepToUpdatePosition);
                }
            }
        });

        ((AddNewRecipeActivity)getActivity()).callFromEditFragment(null, null, v);

        return v;
    }

    private void addOrUpdate(StepModel step, int position){
        boolean timerOn = cb_timer.isChecked();

        if(validFields(timerOn)) {
            if(position == -1){ //Add step
                step.setStepOrder(stepsList.size() + 1);
            } else { //Update Step
                step.setStepOrder(position + 1);
            }

            step.setDescription(til_description.getText().toString());
            step.setRequiredTimer(timerOn);

            if(timerOn) {
                step.setTimerHour(Integer.parseInt(input_hours.getText().toString().trim()));
                step.setTimerMinute(Integer.parseInt(input_minute.getText().toString().trim()));
            }

            if(position == -1){ //Add Step
                stepsList.add(step);
                rwAdapter.notifyItemInserted(stepsList.size()-1);
            } else { //Update Step
                rwAdapter.notifyItemChanged(position);
                btnAddOrUpdate.setText(R.string.add_step_btnAdd);
            }

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
            Toast.makeText(getActivity().getApplicationContext(), "Description cannot be empty. " +
                    "Hours, Minutes Must Be Correct", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validFields(boolean timerOn){
       if(til_description.getText().toString().trim().isEmpty()) return false;

       if(timerOn && (input_hours.getText().toString().trim().isEmpty() || Integer.parseInt(input_hours.getText().toString()) < 0)) return false;
       return !timerOn || (!input_minute.getText().toString().trim().isEmpty() && Integer.parseInt(input_minute.getText().toString()) >= 0 && Integer.parseInt(input_minute.getText().toString()) <= 59);
    }

    @Override
    public void onEditListener(int position) {
        stepToUpdatePosition = position;
        btnAddOrUpdate.setText(R.string.add_step_btnUpdate);
        til_description.setText(stepsList.get(position).getDescription());
        if(stepsList.get(position).getTimerHour() != 0 && stepsList.get(position).getTimerMinute() != 0) {
            cb_timer.setChecked(true);
            input_hours.setEnabled(true);
            input_minute.setEnabled(true);
            input_hours.setText(String.valueOf(stepsList.get(position).getTimerHour()));
            input_minute.setText(String.valueOf(stepsList.get(position).getTimerMinute()));
        } else {
            cb_timer.setChecked(false);
            input_hours.setEnabled(false);
            input_hours.setText("");
            input_minute.setEnabled(false);
            input_minute.setText("");
        }
    }

    @Override
    public void onDeleteListener(int position) {
        stepsList.remove(position);
        rwAdapter.notifyItemRemoved(position);
        rwAdapter.notifyItemRangeChanged(position, stepsList.size());
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) viewHolder.itemView.setAlpha(0.5f);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
       public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
           int fromPosition = viewHolder.getAdapterPosition();
           int toPosition = target.getAdapterPosition();

           Collections.swap(stepsList, fromPosition, toPosition);
           recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
           rwAdapter.notifyItemRangeChanged(fromPosition, stepsList.size());
           rwAdapter.notifyItemRangeChanged(toPosition, stepsList.size());

           return false;
       }

       @Override
       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

       }
    };
}