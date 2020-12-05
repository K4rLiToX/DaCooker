package es.android.dacooker.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Objects;

//CRUD para las recetas con los diferentes atributos que incluyen en la BD
// así como los getters y setters de los mismos
public class RecipeModel implements Serializable {

    /*Attributes*/
    private int id;             //Id de la receta
    private String recipeName;  //Nombre de la receta
    private MealType mealType;  //Horario de comida -> es un enumerado
    private int executionTime_Hour; //Horas de ejecución de la receta
    private int executionTime_Minute;   //Minutos de ejecución de la receta
    private String recipeDescription;   //Descripción de la receta
    private int timesCooked;    //Veces que se ha cocinado la receta
    private transient Bitmap image; //Imagen asociada a la receta
    private boolean isFavourite;    //Comprobacion de si la receta está en favoritos o no

    /*Constructors*/

    //Empty Constructor
    public RecipeModel(){}

    //All Attributes Constructor
    public RecipeModel(int id, String recipeName, MealType mealType, int executionTime_Hour,
                       int executionTime_Minute, String recipeDescription, int timesCooked,
                       Bitmap image, boolean isFavourite){
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

    public void setRecipeDescription(String recipeDescription){ this.recipeDescription = recipeDescription; }

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


    /* Equals / Hascode */ //Realizados en torno al ID de la receta
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
