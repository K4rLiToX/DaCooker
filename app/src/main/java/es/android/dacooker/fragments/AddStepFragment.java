package es.android.dacooker.fragments;

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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.activities.AddUpdateRecipeActivity;
import es.android.dacooker.adapters.AddStepRecyclerAdapter;
import es.android.dacooker.interfaces.AddRecipeStepClickListener;
import es.android.dacooker.models.StepModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStepFragment extends Fragment implements AddRecipeStepClickListener {

    //Vistas
    TextInputEditText til_description, input_hours, input_minute;
    TextInputLayout til_hours, til_minute;
    Button btnAddOrUpdate;
    CheckBox cb_timer;

    //Adaptador
    AddStepRecyclerAdapter rwAdapter;
    RecyclerView rw;

    //Lista de pasos
    List<StepModel> stepsList;
    //Posición del paso que se quiere editar (Seguimos dentro del añadir paso)
    int stepToUpdatePosition;

    //Constructor
   public AddStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Inicializo la lista de pasos
        stepsList = new ArrayList<>();
        //Creo el adaptador para el recyclerview de pasos
        rwAdapter = new AddStepRecyclerAdapter(getActivity().getApplicationContext(), stepsList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflo la vista del fragmento de añadir pasos
        View v = inflater.inflate(R.layout.fragment_add_step, container, false);

        initView(v);
        initButtons();

        return v;
    }

    //Inicializa las vistas
    private void initView(View v){
        btnAddOrUpdate = v.findViewById(R.id.add_step_btnAdd);
        cb_timer = v.findViewById(R.id.checkbox_requiredTimer);
        til_description = v.findViewById(R.id.step_description_input);
        til_hours = v.findViewById(R.id.til_stepTimer_hours);
        til_minute = v.findViewById(R.id.til_stepTimer_minutes);
        input_hours = v.findViewById(R.id.step_timerTime_hours_input);
        input_minute = v.findViewById(R.id.step_timerTime_minutes_input);

        rw = v.findViewById(R.id.add_step_recyclerView);
        //Setea el adaptador al recyclerview de pasos
        rw.setAdapter(rwAdapter);
        //Creo y añado al recyclerview de pasos opciones para cuando seleccione las cards de pasos que contiene
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rw);

        ((AddUpdateRecipeActivity)getActivity()).callFromEditFragment(null, null, v);
    }

    //Inicializa los botones
    private void initButtons(){
       //Añado funcionalidad al check box de timer
        cb_timer.setOnClickListener(view -> {
            //Si el checked box esta seleccionado
            boolean timerOn = cb_timer.isChecked();
            //Habilito los inputs de horas y minutos
            til_hours.setEnabled(timerOn);
            input_hours.setText("");
            input_minute.setText("");
            til_minute.setEnabled(timerOn);
        });
        //Añado funcionalidad al botón de añadir o actulizar paso
        btnAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            //Cuando lo pulso
            public void onClick(View v) {
                if(btnAddOrUpdate.getText().toString().equalsIgnoreCase(getString(R.string.add_step_btnAdd))){ //Si el botón tiene como título Añadir paso
                    //Creo un nuevo paso y lo añado
                    StepModel stepModel = new StepModel();
                    addOrUpdate(stepModel, -1);
                } else { //SI el botón tiene como título actualizar paso
                    //Actualizo el paso en la posición que he pulsado
                    addOrUpdate(stepsList.get(stepToUpdatePosition), stepToUpdatePosition);
                }
            }
        });
    }

    //Añadir o actualizar un paso dado un paso y su posición
    private void addOrUpdate(StepModel step, int position){
        //Guardo si el check box de timer esta seleccionado
        boolean timerOn = cb_timer.isChecked();

        if(validFields(timerOn)) { //Valido los campos de tiempo y descripción
            if(position == -1){ //Si la posición que recibo por parámetro es -1
                //Cambio el orden del paso al último de la lista. Así se mantiene el orden de los pasos, siempre se añade al final para que el orden sea Paso 1, Paso 2, ...
                step.setStepOrder(stepsList.size() + 1);
            } else { //Si la posición que recibo por parámetro es distinta a -1
                //Cambio el orden del paso a la posición que me pasan por parámetro. Como la lista empieza en 0, el +1 es para ponerlo en la posición que se corresponde
                step.setStepOrder(position + 1);
            }

            //Cambio la descripción del paso por la del input
            step.setDescription(til_description.getText().toString());
            //Cambio si necesita timer o no
            step.setRequiredTimer(timerOn);

            if(timerOn) { //En caso de necesitarlo
                //Cambio la hora y los minutos del paso por los de los inputs
                step.setTimerHour(Integer.parseInt(input_hours.getText().toString().trim()));
                step.setTimerMinute(Integer.parseInt(input_minute.getText().toString().trim()));
            }

            if(position == -1){ //Si la posición que me pasan por parámtro es -1
                //Añado el paso a la lista de pasos
                stepsList.add(step);
                //Notifico al adaptador que se ha insertado un elemento en la última posición. Quito el +1 que se ha añadido antes para corrsponder con la lista
                rwAdapter.notifyItemInserted(stepsList.size()-1);
            } else { //Si la posición que me pasan por parámetro es distinta de -1
                //Notifico al adaptador que se ha modificado un item en dicha posición (Con esta llamada, el adaptadaor actualiza los campos de la card automáticamente y lo muestra en la interfaz)
                rwAdapter.notifyItemChanged(position);
                //Cambio el texto del botón a añadir paso (salimos del modo editar)
                btnAddOrUpdate.setText(R.string.add_step_btnAdd);
            }

            //Vacio el campo de descripción
            til_description.setText("");

            if(timerOn){ //Si el check box del timer está seleccionado
                //Lo deselecciono, vacio los campos y los deshabilito
                cb_timer.setChecked(false);
                input_hours.setText("");
                input_minute.setText("");
                til_hours.setEnabled(false);
                til_minute.setEnabled(false);
            }
        } else { //Si los campos de tiempo o descripción no son válidos notifico con mensaje
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.addStep_err), Toast.LENGTH_SHORT).show();
        }
    }

    //Valida los campos
    private boolean validFields(boolean timerOn){
       //Si la descripción está vacía devuelvo false
       if(til_description.getText().toString().trim().isEmpty()) return false;
       //Si la check box de timer está seleccionada y los campos horas y minutos son vacíos o la hora o los minutos es menor que 0 o los minutso son mayores que 59 retorno false
        //En caso contrario retorno true
       if(timerOn && (input_hours.getText().toString().trim().isEmpty() || Integer.parseInt(input_hours.getText().toString()) < 0)) return false;
       return !timerOn || (!input_minute.getText().toString().trim().isEmpty() && Integer.parseInt(input_minute.getText().toString()) >= 0 && Integer.parseInt(input_minute.getText().toString()) <= 59);
    }

    @Override
    //Método para editar el paso dada la posición de la card que he pulsado
    public void onEditListener(int position) {
        //Le asigno la posición a la variable globar creada
        stepToUpdatePosition = position;
        //Cambio el texto del botón a actualizar cambios (modo edición activo)
        btnAddOrUpdate.setText(R.string.add_step_btnUpdate);
        //Muestro la descripción del paso seleccionado
        til_description.setText(stepsList.get(position).getDescription());

        if(stepsList.get(position).getTimerHour() != 0 && stepsList.get(position).getTimerMinute() != 0) { //Si el paso tiene tiempo asignado
            //Activo el check box de timer, habilito los inputs de horas y minutos y muestro los valores que tiene el paso
            cb_timer.setChecked(true);
            input_hours.setEnabled(true);
            input_minute.setEnabled(true);
            input_hours.setText(String.valueOf(stepsList.get(position).getTimerHour()));
            input_minute.setText(String.valueOf(stepsList.get(position).getTimerMinute()));
        } else { //Si no tiene tiempo asignado
            //Desactivo el check box de timer, deshabilito los inputs de horas y minutos y los vacío
            cb_timer.setChecked(false);
            input_hours.setEnabled(false);
            input_hours.setText("");
            input_minute.setEnabled(false);
            input_minute.setText("");
        }
    }

    @Override
    //Método para eliminar el paso dada la posición de la card que se ha pulsado
    public void onDeleteListener(int position) {
        //Elimino el paso de la lista de pasos
        stepsList.remove(position);
        //Notifico al adaptador que se ha eliminado un item en la posición position
        rwAdapter.notifyItemRemoved(position);
        //Notifico al adaptador que el rango de items ha cambiado (Para actualizar la posición de los pasos posteriores al eliminado. De esta manera también se actualizan sus títulos)
        rwAdapter.notifyItemRangeChanged(position, stepsList.size());
    }

    //Creo un callback para poder arrastrar los pasos cuando mantengo pulsado una card
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {

        @Override
        //Cuando mantengo pulsada una card y la arrastro
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            //Si el estado de la card coincide con ser arrastrada cambio su transparencia para que se vea que estoy arrastrando dicha card
            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) viewHolder.itemView.setAlpha(0.5f);
        }

        @Override
        //Cuando se deja de arrastar la card
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            //Vuelvo a poner su transparencia como antes
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        //Cuando muevo la card
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
           //Guardo la posición en la que estaba
           int fromPosition = viewHolder.getAdapterPosition();
           //Guardo la nueva posición en la que se ha colocado
           int toPosition = target.getAdapterPosition();

           //Intercambio las cards
           Collections.swap(stepsList, fromPosition, toPosition);
           //Notifico al adaptador que se ha movido un elemento de una posición a otra
           recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
           //Notifico al adaptador que se ha actualizado el rango de items de la posición de inicio
           rwAdapter.notifyItemRangeChanged(fromPosition, stepsList.size());
           //Notifico al adaptador que se ha actualizado el rango de items de la posición final
           rwAdapter.notifyItemRangeChanged(toPosition, stepsList.size());
           //Los dos métodos anteriores hacen que cuando se cambie una card de sitio, la cards involucradas cambien su posición en la lista y así actualizar su título que contiene el orden del paso

           return false;
       }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Este método es para realizar acciones cuando deslizamos la card de izq a der o de der a izq
       }
    };
}