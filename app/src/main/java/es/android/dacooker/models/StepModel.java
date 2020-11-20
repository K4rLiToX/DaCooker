package es.android.dacooker.models;

import java.util.Objects;

public class StepModel {

    /*Attributes*/
    private int id;
    private String description;
    private boolean requiredTimer;
    private String timerTime;
    private int stepOrder;
    private int recipe_id;

    /*Constructors*/

    //Empty Constructor
    public StepModel() {}

    //All Attributes Constructor
    public StepModel(int id, String description, boolean requiredTimer, String timerTime, int stepOrder, int recipe_id) {
        this.id = id;
        this.stepOrder = stepOrder;
        this.description = description;
        this.requiredTimer = requiredTimer;
        this.timerTime = timerTime;
        this.recipe_id = recipe_id;
    }

    /*Getters and Setters*/

    public int getId(){ return this.id; }

    public int getStepOrder() {
        return this.stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequiredTimer() {
        return this.requiredTimer;
    }

    public void setRequiredTimer(boolean requiredTimer) {
        this.requiredTimer = requiredTimer;
    }

    public String getTimerTime() {
        return this.timerTime;
    }

    public void setTimerTime(String timerTime) {
        this.timerTime = timerTime;
    }

    public int getRecipe_Id() {
        return this.recipe_id;
    }

    public void setRecipe_ID(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    /* Equals / Hashcode */
    public boolean equals(Object obj){
        if(obj instanceof StepModel) {
            StepModel s = (StepModel) obj;
            return this.getId() == s.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
