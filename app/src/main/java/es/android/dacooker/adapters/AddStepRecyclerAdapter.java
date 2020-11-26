package es.android.dacooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

public class AddStepRecyclerAdapter extends RecyclerView.Adapter<AddStepRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<StepModel> stepModelList;

    public AddStepRecyclerAdapter(Context context, List<StepModel> stepModelList){
        this.context = context;
        this.stepModelList = stepModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View stepItemView = layoutInflater.inflate(R.layout.add_step_adapter_item, parent, false);
        ViewHolder vh = new ViewHolder(stepItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepModel step = stepModelList.get(position);
        holder.orderStep.setText(String.valueOf(step.getStepOrder()));
        holder.descriptionStep.setText(String.valueOf(step.getDescription()));

        String timer = step.getTimerTime();
        String hours = "", minutes = "";
        if(timer != null) {
            String[] aux = timer.split(":");
            hours = aux[0];
            minutes = aux[1];
        }
        holder.hoursStep.setText(String.valueOf(hours));
        holder.minutesStep.setText(String.valueOf(minutes));

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
        ImageButton btnDelete, btnUpdate;
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
