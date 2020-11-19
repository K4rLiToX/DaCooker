package es.android.dacooker.models;

public class StepModel {

    /*Attributes*/
    private int id;
    private int stepOrder;
    private String description;
    private boolean requiredTimer;
    private int timerTime;

    /*Constructors*/

    //Empty Constructor
    public StepModel() {}

    //All Attributes Constructor
    public StepModel(int id, int stepOrder, String description, boolean requiredTimer, int timerTime) {
        this.id = id;
        this.stepOrder = stepOrder;
        this.description = description;
        this.requiredTimer = requiredTimer;
        this.timerTime = timerTime;
    }

    /*Getters and Setters*/

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

    public int getTimerTime() {
        return this.timerTime;
    }

    public void setTimerTime(int timerTime) {
        this.timerTime = timerTime;
    }
}
