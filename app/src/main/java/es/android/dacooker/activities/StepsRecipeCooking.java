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

    private final String SHARE_STEPLIST_KEY = "SHARED_STEPLIST_KEY";
    private List<StepModel> stepList;

    StepsCookingPagerAdapter vpAdapter;
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_recipe_cooking);
        initParameters();
    }

    //Init Buttons - View
    private void initParameters(){
        this.stepList = (List<StepModel>) SingletonMap.getInstance().get(SHARE_STEPLIST_KEY);

        this.vp = findViewById(R.id.step_cooking_viewPager);
        this.vpAdapter = new StepsCookingPagerAdapter(getSupportFragmentManager());
        for(StepModel s : this.stepList) {
            StepRecipeFragment srf = new StepRecipeFragment(s, this.stepList.size());
            vpAdapter.addFragment(srf);
        }
        this.vp.setAdapter(this.vpAdapter);
        this.vp.setOffscreenPageLimit(vpAdapter.getCount());

    }

    public void finishCooking(){
        try {
            int recipe_id = this.stepList.get(0).getRecipe_Id();
            BBDD_Helper dbHelper = new BBDD_Helper(this);
            BD_Operations.updateTimesCooked(recipe_id, dbHelper);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goNextStep(){
        int nextStep = this.vp.getCurrentItem() + 1;
        this.vp.setCurrentItem(nextStep);
    }

    public void goBackStep(){
        int backStep = this.vp.getCurrentItem() - 1;
        this.vp.setCurrentItem(backStep);
    }

    //Check if any timer is active before FinishRecipe
    public boolean checkTimers() {
        boolean res = true;

        for(StepModel s : stepList){
            if(s.isRequiredTimer()) {
                StepRecipeFragment rsf = (StepRecipeFragment) this.vpAdapter.getItem(s.getStepOrder()-1);
                if(!rsf.isActive()) {
                    res = false;
                    break;
                }
            }
        }
        return res;
    }
}