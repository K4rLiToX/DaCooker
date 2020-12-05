package es.android.dacooker.models;

import java.io.Serializable;
import java.util.Objects;

//CRUD para los pasos con los diferentes atributos que incluyen en la BD
// así como los getters y setters de los mismos
public class StepModel implements Serializable {

    /*Attributes*/
    private int id;                 //id del paso en la bd
    private String description;     //descripción del paso
    private boolean requiredTimer;  //comprobacion de si el paso requiere timer
    private int timer_hour;         //horas de duracion del timer
    private int timer_minute;       //minutos de duracion del timer
    private int stepOrder;          //orden del paso dentro de la receta
    private int recipe_id;          //id de la receta a la que se asocia el paso

    /*Constructors*/

    //Empty Constructor
    public StepModel() {}

    //All Attributes Constructor
    public StepModel(int id, String description, boolean requiredTimer, int timer_hour,
                     int timer_minute, int stepOrder, int recipe_id) {
        this.id = id;
        this.stepOrder = stepOrder;
        this.description = description;
        this.requiredTimer = requiredTimer;
        this.timer_hour = timer_hour;
        this.timer_minute = timer_minute;
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

    public int getTimerHour() {
        return this.timer_hour;
    }

    public void setTimerHour(int timer_hour) {
        this.timer_hour = timer_hour;
    }

    public int getTimerMinute() {
        return this.timer_minute;
    }

    public void setTimerMinute(int timer_minute) {
        this.timer_minute = timer_minute;
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
