package es.android.dacooker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

public class AddStepRecyclerAdapter extends RecyclerView.Adapter<AddStepRecyclerAdapter.ViewHolder> {

    private List<StepModel> stepModelList;

    public AddStepRecyclerAdapter(List<StepModel> stepModelList){
        this.stepModelList = stepModelList;
    }

    @NonNull
    @Override
    public AddStepRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View stepItemView = layoutInflater.inflate(R.layout.add_step_adapter_item, parent, false);
        AddStepRecyclerAdapter.ViewHolder vh = new AddStepRecyclerAdapter.ViewHolder(stepItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AddStepRecyclerAdapter.ViewHolder holder, int position) {
        StepModel step = stepModelList.get(position);
        holder.orderStep.setText(step.getStepOrder());
        holder.descriptionStep.setText(step.getDescription());

        String timer = step.getTimerTime();
        String hours = "", minutes = "";
        if(timer != null) {
            String[] aux = timer.split(":");
            hours = aux[0];
            minutes = aux[1];
        }
        holder.hoursStep.setText(hours);
        holder.minutesStep.setText(minutes);

        holder.btnDelete.setOnClickListener( view -> {
            stepModelList.remove(position);
            notifyItemRemoved(position);
        });

        holder.btnUpdate.setOnClickListener( view -> {
            //Probar hacerlo con Singleton
            //Método del Fragment que se ejecute al pulsar el Editar
            // y copie los datos
        });
    }

    @Override
    public int getItemCount() {
        return stepModelList.size();
    }

    //Intern Class
    class ViewHolder extends RecyclerView.ViewHolder {
        Button btnDelete, btnUpdate;
        TextView orderStep, descriptionStep, hoursStep, minutesStep;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_step_listView_btnDelete);
            this.btnUpdate = itemView.findViewById(R.id.add_step_listView_btnUpdate);
            this.orderStep = itemView.findViewById(R.id.step_order_add_listView);
            this.descriptionStep = itemView.findViewById(R.id.step_description_add_listView);
            this.hoursStep = itemView.findViewById(R.id.step_hours_add_listView);
            this.minutesStep = itemView.findViewById(R.id.step_minutes_add_listView);
        }
    }

}
