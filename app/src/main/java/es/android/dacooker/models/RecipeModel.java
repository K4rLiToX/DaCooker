package es.android.dacooker.models;

import java.util.List;

public class RecipeModel {

    /*Attributes*/
    private int id;
    private String recipeName;
    private MealType mealType;
    private int executionTime;
    private int timesCooked;
    private List<IngredientModel> ingredientsList;
    private List<StepModel> stepsList;

    /*Constructors*/

    //Empty Constructor
    public RecipeModel(){}

    //All Attributes Constructor
    public RecipeModel(int id, String recipeName, MealType mealType, int executionTime, int timesCooked){
        this.id = id;
        this.recipeName = recipeName;
        this.mealType = mealType;
        this.executionTime = executionTime;
        this.timesCooked = timesCooked;
    }

    /*Getter and Setters*/

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

    public int getExecutionTime() {
        return this.executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public int getTimesCooked() {
        return this.timesCooked;
    }

    public void setTimesCooked(int timesCooked) {
        this.timesCooked = timesCooked;
    }

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
}
