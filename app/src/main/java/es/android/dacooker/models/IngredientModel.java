package es.android.dacooker.models;

public class IngredientModel {

    /*Attributes*/
    private int id;
    private String ingredientName;
    private String quantity;

    /*Constructors*/

    //Empty Constructor
    public IngredientModel() {}

    //All Attributes Constructor
    public IngredientModel(int id, String ingredientName, String quantity) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

    /*Getters and Setters*/

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
}
