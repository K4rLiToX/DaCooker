package es.android.dacooker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.interfaces.AddRecipeStepClickListener;
import es.android.dacooker.models.StepModel;

public class AddStepRecyclerAdapter extends RecyclerView.Adapter<AddStepRecyclerAdapter.ViewHolder> {

    //Contexto del adaptador
    private final Context context;
    //Interfaz auxiliar para delegar funcionalidad al fragmento/activity correspondiente
    AddRecipeStepClickListener addRecipeStepClickListener;
    //Lista de pasos a mostrar
    List<StepModel> stepModelList;

    //Constructor
    public AddStepRecyclerAdapter(Context context, List<StepModel> stepModelList, AddRecipeStepClickListener addRecipeStepClickListener){
        this.context = context;
        this.stepModelList = stepModelList;
        this.addRecipeStepClickListener = addRecipeStepClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Infla la vista de la card de paso
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View stepItemView = layoutInflater.inflate(R.layout.add_step_adapter_item, parent, false);
        return new ViewHolder(stepItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Obtenemos el paso en la posición position
        StepModel step = stepModelList.get(position);
        //Seteamos las vistas con los valores del paso obtenido
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

    //Devuelve la lista de pasos
    public List<StepModel> getStepModelList(){
        return this.stepModelList;
    }

    //Setea una nueva lista de pasos
    public void setEditList(List<StepModel> stepList){
        //Guardamos el tamaña de la lista a modificar
        int prevSize = this.stepModelList.size();
        //Limpiamos la lista
        this.stepModelList.clear();
        //Si la lista que me pasan por parámtro es nula, la inicializo a vacia
        if(stepList == null) stepList = new ArrayList<>();
        //Añado la lista que me pasan por parámetro a la lista vaciada
        this.stepModelList.addAll(stepList);
        //Notifico al adaptador que el rango de item se ha eliminado
        notifyItemRangeRemoved(0, prevSize);
        //Notifico que se ha insertado un nuevo rango de items
        notifyItemRangeInserted(0, stepList.size());
    }

    //Clase interna
    class ViewHolder extends RecyclerView.ViewHolder {
        //Vistas a utilizar
        ImageView btnDelete, btnUpdate;
        TextView orderStep, descriptionStep, hoursStep, minutesStep;

        //Inicializo las vistas que va a utlizar el método onBindViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.btnDelete = itemView.findViewById(R.id.add_step_listView_btnDelete);
            this.btnUpdate = itemView.findViewById(R.id.add_step_listView_btnUpdate);
            this.orderStep = itemView.findViewById(R.id.step_order_add_listView);
            this.descriptionStep = itemView.findViewById(R.id.step_description_add_listView);
            this.hoursStep = itemView.findViewById(R.id.step_hours_add_listView);
            this.minutesStep = itemView.findViewById(R.id.step_minutes_add_listView);

            //Añado funcionalidad al botón eliminar
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Llamo a la interfaz pasándole la posición en la que he pulsado el botón para que ejecute la eliminación
                    addRecipeStepClickListener.onDeleteListener(getAdapterPosition());
                }
            });
            //Añado funcionalidad al botón editar
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Llamo a la interfaz pasándole la posición en la que he pulsado el botón para que ejecute la edición
                    addRecipeStepClickListener.onEditListener(getAdapterPosition());
                }
            });
        }
    }

}
