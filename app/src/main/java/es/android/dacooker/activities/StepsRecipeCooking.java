package es.android.dacooker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.List;

import es.android.dacooker.R;
import es.android.dacooker.adapters.StepsCookingPagerAdapter;
import es.android.dacooker.fragments.StepRecipeFragment;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.BBDD_Helper;
import es.android.dacooker.services.BD_Operations;
import es.android.dacooker.utilities.SingletonMap;

public class StepsRecipeCooking extends AppCompatActivity {
    //SingletonMap Key
    private static final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";

    //Lista de pasos a mostrar
    private List<StepModel> stepList;

    //Adaptadores y vistas necesarias para mostrar los fragmentos
    StepsCookingPagerAdapter vpAdapter;
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos el layout correspondiente
        setContentView(R.layout.activity_steps_recipe_cooking);
        initParameters();
    }

    //Inicializamos los parámetros a utilizar
    private void initParameters(){
        //Obtenemos la lista de paso del SingletonMap
        this.stepList = (List<StepModel>) SingletonMap.getInstance().get(SHARE_STEPLIST_KEY);

        //Creamos el adaptador para el viewpager
        this.vp = findViewById(R.id.step_cooking_viewPager);
        this.vpAdapter = new StepsCookingPagerAdapter(getSupportFragmentManager());
        //Para cada paso en la lista de pasos creamos un fragmento y lo añadimos al adaptador del viewpager
        for(StepModel s : this.stepList) {
            StepRecipeFragment srf = new StepRecipeFragment(s, this.stepList.size());
            vpAdapter.addFragment(srf);
        }
        //Seteamos el adapatador al viewpager
        this.vp.setAdapter(this.vpAdapter);
        //Definimos que queremos cargar los fragmentos previamente creados para no perder información cuando nos desplacemos de una a otro
        this.vp.setOffscreenPageLimit(vpAdapter.getCount());

    }

    /*Métodos para controlar los pasos de la receta y los contadores*/

    //Pasar al siguiente paso
    public void goNextStep(){
        int nextStep = this.vp.getCurrentItem() + 1;
        this.vp.setCurrentItem(nextStep);
    }
    //Retroceder al paso anterior
    public void goBackStep(){
        int backStep = this.vp.getCurrentItem() - 1;
        this.vp.setCurrentItem(backStep);
    }
    //Controlar si hay algún contador activo antes de finalizar la recta
    public boolean checkTimers() {
        boolean res = true;
        //Para cada paso en la lista de pasos
        for(StepModel s : stepList){
            if(s.isRequiredTimer()) { //Si el paso tiene un tiempo
                //Obtengo el fragmento del paso
                StepRecipeFragment rsf = (StepRecipeFragment) this.vpAdapter.getItem(s.getStepOrder()-1);
                //Si el fragmento no está activo (no está en pantalla), entonces pongo res a false
                if(!rsf.isActive()) {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }

    //Finalizar receta
    public void finishCooking() {
        try {
            //Obtengo el id de la receta asociada a los pasos
            int recipe_id = this.stepList.get(0).getRecipe_Id();
            BBDD_Helper dbHelper = new BBDD_Helper(this);
            //Actualizo la variable en BD del número de veces cocinado y destruyo la actividad
            BD_Operations.updateTimesCooked(recipe_id, dbHelper);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}