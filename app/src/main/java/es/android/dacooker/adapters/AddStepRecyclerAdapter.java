package es.android.dacooker.adapters;

import android.content.Context;
import android.util.Log;
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
import es.android.dacooker.interfaces.AddRecipeStepClickListener;
import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.StepModel;

public class AddStepRecyclerAdapter extends RecyclerView.Adapter<AddStepRecyclerAdapter.ViewHolder> {

    private final Context context;
    List<StepModel> stepModelList;
    AddRecipeStepClickListener addRecipeStepClickListener;

    public AddStepRecyclerAdapter(Context context, List<StepModel> stepModelList, AddRecipeStepClickListener addRecipeStepClickListener){
        this.context = context;
        this.stepModelList = stepModelList;
        this.addRecipeStepClickListener = addRecipeStepClickListener;
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
        holder.orderStep.setText(String.valueOf(position + 1));
        holder.descriptionStep.setText(String.valueOf(step.getDescription()));

        String hours = step.getTimerHour()+"h", minutes = step.getTimerMinute()+"min";

        holder.hoursStep.setText(hours);
        holder.minutesStep.setText(minutes);

    }

    @Override
    public int getItemCount() {
        return stepModelList.size();
    }

    //Intern Class
    class ViewHolder extends RecyclerView.ViewHolder {
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

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRecipeStepClickListener.onDeleteListener(getAdapterPosition());
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRecipeStepClickListener.onEditListener(getAdapterPosition());
                }
            });
        }
    }

    public void setEditList(List<StepModel> stepList){
        int prevSize = this.stepModelList.size();
        this.stepModelList.clear();
        this.stepModelList.addAll(stepList);
        notifyItemRangeRemoved(0, prevSize);
        notifyItemRangeInserted(0, stepList.size());
    }

}
