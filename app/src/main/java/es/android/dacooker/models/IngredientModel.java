package es.android.dacooker.models;

import java.io.Serializable;
import java.util.Objects;

public class IngredientModel implements Serializable {

    /*Attributes*/
    private int id;
    private String ingredientName;
    private String quantity;
    private int idRecipe;

    /*Constructors*/

    //Empty Constructor
    public IngredientModel() {}

    //All Attributes Constructor
    public IngredientModel(int id, String ingredientName, String quantity, int idRecipe) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.idRecipe = idRecipe;
    }

    /*Getters and Setters*/

    public int getId(){ return this.id; }

    public String getIngredientName() {
        return this.ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getIdRecipe(){ return this.idRecipe; }

    public void setIdRecipe(int idRecipe){ this.idRecipe = idRecipe; }

    /* Equals / Hashcode */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientModel that = (IngredientModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
