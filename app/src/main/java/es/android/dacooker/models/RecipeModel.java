package es.android.dacooker.models;

import android.graphics.Bitmap;

import java.util.List;

public class RecipeModel {

    /*Attributes*/
    private int id;
    private String recipeName;
    private MealType mealType;
    private String executionTime;
    private int timesCooked;
    private Bitmap image;
    private List<IngredientModel> ingredientsList;
    private List<StepModel> stepsList;

    /*Constructors*/

    //Empty Constructor
    public RecipeModel(){}

    //All Attributes Constructor
    public RecipeModel(int id, String recipeName, MealType mealType, String executionTime, int timesCooked, Bitmap image){
        this.id = id;
        this.recipeName = recipeName;
        this.mealType = mealType;
        this.executionTime = executionTime;
        this.timesCooked = timesCooked;
        this.image = image;
    }

    /* Getter and Setters */

    public int getId(){ return this.id; }

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

    public String getExecutionTime() {
        return this.executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    public int getTimesCooked() {
        return this.timesCooked;
    }

    public void setTimesCooked(int timesCooked) {
        this.timesCooked = timesCooked;
    }

    public Bitmap getImage() { return this.image; }

    public void setImage(Bitmap image) { this.image = image; }


    /* List */

    public List<IngredientModel> getIngredientsList() {
        return this.ingredientsList;
    }

    public void setIngredientsList(List<IngredientModel> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public List<StepModel> getStepsList() {
        return this.stepsList;
    }

    public void setStepsList(List<StepModel> stepsList) {
        this.stepsList = stepsList;
    }

    /* Equals */
    public boolean equals(Object obj){
        if(obj instanceof RecipeModel) {
            RecipeModel r = (RecipeModel) obj;
            return r.getId() == this.getId();
        }
        return false;
    }
}
