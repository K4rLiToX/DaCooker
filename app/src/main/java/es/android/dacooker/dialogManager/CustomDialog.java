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

    //Vistas
    RadioGroup radioGroup;
    LinearLayout search_dialog_time_layout;
    TextInputEditText input_hours, input_minutes;
    RelativeLayout search_dialog_mealType_layout;
    AutoCompleteTextView selectMealType;
    Button btnSearch, btnDismiss;

    //Adaptador de la enumeración MealType
    MealType[] MEALTYPES = MealType.values();
    ArrayAdapter<MealType> adapter;

    //Interfaz que delega las funcionalidades necesarias a la activity main
    private OnDialogInputListener onDialogInputListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflo la vista del dialogo personalizado
        View view = inflater.inflate(R.layout.search_dialog, container, false);
        //Creo el adaptador
        adapter =  new ArrayAdapter<>(getActivity(), R.layout.meal_type_dropdown_item, MEALTYPES);
        //Inicializo las vistas necesarias
        this.selectMealType = view.findViewById(R.id.search_dialog_select_mealType);
        //Seteo el adapter al select de mealtype
        this.selectMealType.setAdapter(adapter);

        this.radioGroup = view.findViewById(R.id.search_dialog_radio_group);
        this.search_dialog_time_layout = view.findViewById(R.id.search_dialog_time_layout);
        this.input_hours = view.findViewById(R.id.search_dialog_input_hours);
        this.input_minutes = view.findViewById(R.id.search_dialog_input_minutes);
        this.search_dialog_mealType_layout = view.findViewById(R.id.search_dialog_mealType_layout);

        this.btnSearch = view.findViewById(R.id.search_dialog_btn_search);
        this.btnDismiss = view.findViewById(R.id.search_dialog_btn_dismiss);

        //Añado funcionalidad al grupo de radio buttons
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            //Se ejecuta cuando se pulsa algunos de los radio buttons del grupo
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                changeSelection(radioGroup, i);
            }
        });

        //Añado funcionalidad al botón de confirmación del diálogo personalizado
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            //Cuando se pulsa se filtra la lista de recetas de acuerdo a los filtros seleccionados
            public void onClick(View view) {
                filterSearch();
            }
        });
        //Añado funcionalidad al botón cancelar del diálogo personalizado
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            //Cuando se pulsa cierro el diálogo personalizado
            public void onClick(View view) {
               dismiss();
            }
        });
        return view;
    }

    /*Métodos del diálogo personalizado*/

    //Cambia la vista del diálogo personalizad en función del radio button que se seleccione
    private void changeSelection(RadioGroup radioGroup, int i){
        if(i == R.id.search_dialog_radio_mealType){ //Si se pulsa el radio button de Mealtype
            //Oculto el layout del tiempo
            search_dialog_time_layout.setVisibility(View.GONE);
            //Hago visible el select de mealtype
            search_dialog_mealType_layout.setVisibility(View.VISIBLE);
        } else if(i == R.id.search_dialog_radio_time){ //Si se pulsa el radio button de Tiempo
            //Hago visible el layout del tiempo
            search_dialog_time_layout.setVisibility(View.VISIBLE);
            //Oculto el select de mealtype
            search_dialog_mealType_layout.setVisibility(View.GONE);
        } else { //En caso de que ninguno esté seleccionado oculto las dos vistas
            search_dialog_time_layout.setVisibility(View.GONE);
            search_dialog_mealType_layout.setVisibility(View.GONE);
        }
    }

    //Realiza el filtro de recetas según el radio button seleccionado
    private void filterSearch(){
        //Obtengo el id del radio button seleccionado
        int radioChecked = radioGroup.getCheckedRadioButtonId();

        if(radioChecked == R.id.search_dialog_radio_mealType){ //Si está seleccionado el radio button de Mealtype
            //Obtendo el valor del select
            String meal = selectMealType.getText().toString();

            if(validateMealType(meal)){ //Si el valor es válido
                BBDD_Helper db = new BBDD_Helper(getActivity());
                //Creo la lista resultado de recetas
                List<RecipeModel> resultList;
                try {
                    //Obtengo las recetas filtradas por el tipo de comida
                    resultList = BD_Operations.getRecipesByMealType(MealType.valueOf(meal), db);
                    //Si la lista es vacía mando mensaje de que no hay ninguna receta con ese tipo de comida
                    //En caso contrario envío la lista resultado junto con el filtro 1 (mealtype) y el tipo de comida a la actividad principal mediante la interfaz
                    if(resultList.isEmpty()) Toast.makeText(getActivity(),getString(R.string.search_dialog_err_no_meal_results) + meal, Toast.LENGTH_SHORT).show();
                    else onDialogInputListener.sendResultList(resultList, "1", meal);
                } catch (Exception e){
                    //En caso de que ocurra algún error envío mensaje notificando
                    Toast.makeText(getActivity(), getString(R.string.search_dialog_err_retrieving), Toast.LENGTH_SHORT).show();
                }
                //Por último, pase lo que pase, cierro el diálogo personalizado
                dismiss();
            } else { //Si el valor obtenido del select no es válido significa que no se ha seleccionado ninguna opción
                //Envió mensaje pidiendo que se seleccione una opción
                Toast.makeText(getActivity(), R.string.search_dialog_mealType_select_error, Toast.LENGTH_LONG).show();
            }
        } else if(radioChecked == R.id.search_dialog_radio_time){ // Si está seleccionado el radio button de Tiempo
            //Obtengo los datos de los inputs
            String hour = input_hours.getText().toString().trim();
            String minute = input_minutes.getText().toString().trim();

            if(validateTime(hour, minute)){ //Si los datos son válidos
                BBDD_Helper db = new BBDD_Helper(getActivity());
                //Creo la lista resultado
                List<RecipeModel> resultList;
                try {
                    //Obtengo las recetas que tengan un tiempo menor al que me han dado en los inputs
                    resultList = BD_Operations.getRecipesByLessExecutionTime(Integer.parseInt(hour), Integer.parseInt(minute), db);
                    //Si la lista resultado está vacía mando un mensaje notificando que no hay recetas que cumplan con esa propiedad
                    //En caso contrario envío la lista resultado junto con el filtro 3 (tiempo) y una cadena de texto conteniendo las horas y los minutos obtenidos de los inputs
                    if(resultList.isEmpty()) Toast.makeText(getActivity(), getString(R.string.search_dialog_no_recipes_matching), Toast.LENGTH_SHORT).show();
                    else onDialogInputListener.sendResultList(resultList, "2", hour+":"+minute);
                } catch (Exception e){
                    //En caso de error notifico
                    Toast.makeText(getActivity(), getString(R.string.search_dialog_err_retrieving), Toast.LENGTH_SHORT).show();
                }
                //Por último, pase lo que pase, cierro el diálogo personalizado
                dismiss();
            } else Toast.makeText(getActivity(), R.string.search_dialog_time_inputs_error, Toast.LENGTH_LONG).show(); //Si los valores de tiempo obtenidos de los inputs no son válidos envío mensaje pidiendo que se escriban valores correctos
        } else Toast.makeText(getActivity(), R.string.search_dialog_no_radio_selected, Toast.LENGTH_SHORT).show(); //Si no hay ningún radio button seleccionado envío mensaje pidiendo que se seleccione una opción
    }

    //Valida que el tipo de comida sea válido, es decir, que no sea vacío (no se selcciona opción)
    private boolean validateMealType(String mealType){
        return !mealType.isEmpty();
    }

    //Valida que los valores de horas y minutos sean válidos, es decir, que ningún campo esté vacio, que las horas y los minutos no sean negativos y que los minutos no sean superiores a 59
    private boolean validateTime(String hour, String minute){
        return !hour.isEmpty() && !minute.isEmpty() && Integer.parseInt(hour) >= 0
                && Integer.parseInt(minute) >= 0 && Integer.parseInt(minute) <= 59;
    }

    @Override
    //Método necesario para utilizar una interfaz en el diálogo personalizado
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { onDialogInputListener = (OnDialogInputListener) getActivity();
        } catch (ClassCastException e){ Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage()); }
    }

    //Creación de la interfaz utilizada junto con el método y los parámetros que necesita
    public interface OnDialogInputListener {
        void sendResultList(List<RecipeModel> resultList, String filter, String search);
    }
}
