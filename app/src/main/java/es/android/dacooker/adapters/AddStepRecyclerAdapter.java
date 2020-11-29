package es.android.dacooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

public class AddStepRecyclerAdapter extends RecyclerView.Adapter<AddStepRecyclerAdapter.ViewHolder> {

    private final Context context;
    List<StepModel> stepModelList;

    public AddStepRecyclerAdapter(Context context, List<StepModel> stepModelList){
        this.context = context;
        this.stepModelList = stepModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View stepItemView = layoutInflater.inflate(R.layout.add_step_adapter_item, parent, false);
        return new ViewHolder(stepItemView);
    }

    public List<StepModel> getStepModelList(){
        return this.stepModelList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepModel step = stepModelList.get(position);
        holder.orderStep.setText(String.valueOf(step.getStepOrder()));
        holder.descriptionStep.setText(String.valueOf(step.getDescription()));

        String hours = step.getTimerHour()+"h", minutes = step.getTimerMinute()+"min";

        holder.hoursStep.setText(hours);
        holder.minutesStep.setText(minutes);

        holder.btnDelete.setOnClickListener( view -> {
            stepModelList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, stepModelList.size());
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
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnDelete, btnUpdate;
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
