package es.android.dacooker.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RecipeModel implements Serializable {

    /*Attributes*/
    private int id;
    private String recipeName;
    private MealType mealType;
    private int executionTime_Hour;
    private int executionTime_Minute;
    private String recipeDescription;
    private int timesCooked;
    private transient Bitmap image;
    private boolean isFavourite;

    /*Constructors*/

    //Empty Constructor
    public RecipeModel(){}

    //All Attributes Constructor
    public RecipeModel(int id, String recipeName, MealType mealType, int executionTime_Hour, int executionTime_Minute, String recipeDescription, int timesCooked, Bitmap image, boolean isFavourite){
        this.id = id;
        this.recipeName = recipeName;
        this.mealType = mealType;
        this.executionTime_Hour = executionTime_Hour;
        this.executionTime_Minute = executionTime_Minute;
        this.recipeDescription = recipeDescription;
        this.timesCooked = timesCooked;
        this.image = image;
        this.isFavourite = isFavourite;
    }

    /* Getter and Setters */

    public int getId(){ return this.id; }

    public void setId(int id){ this.id = id;}

    public String getRecipeName() {
        return this.recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public MealType getMealType() {
        return this.mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public int getExecutionTimeHour() {
        return this.executionTime_Hour;
    }

    public void setExecutionTimeHour(int executionTime_Hour) { this.executionTime_Hour = executionTime_Hour; }

    public int getExecutionTimeMinute() {
        return this.executionTime_Minute;
    }

    public void setExecutionTimeMinute(int executionTime_Minute) { this.executionTime_Minute = executionTime_Minute; }

    public String getRecipeDescription(){
        return this.recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription){
        this.recipeDescription = recipeDescription;
    }

    public int getTimesCooked() {
        return this.timesCooked;
    }

    public void setTimesCooked(int timesCooked) {
        this.timesCooked = timesCooked;
    }

    public Bitmap getImage() { return this.image; }

    public void setImage(Bitmap image) { this.image = image; }

    public boolean isFavourite(){
        return this.isFavourite;
    }

    public void setFavourite(boolean isFavourite){
        this.isFavourite = isFavourite;
    }


    /* Equals / Hascode */
    public boolean equals(Object obj){
        if(obj instanceof RecipeModel) {
            RecipeModel r = (RecipeModel) obj;
            return r.getId() == this.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
