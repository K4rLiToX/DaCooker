package es.android.dacooker.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

/*
    IMPLEMENTED METHODS

    > RECIPES
     - addRecipe: Add a recipe with all their attributes
     - updateRecipe: Update the values from a recipe
     - deleteRecipe: Delete a recipe. His ingredients and steps, too
     - getRecipeById: Get a Recipe Through It ID
     - getRecipesByMealType: Get a List based on it mealType
     - getRecipesByExecutionTime: Get a List based on Execution Time (less than introduced)
     - getRecipesOrderByExecutionTime: Get a List of All Recipes Order By ExecutionTime
     - getRecipesOrderByTimesCooked: Get a List With 5 Most Cooked Recipes

    > STEPS
     - addStep: Add a Step Into a Recipe
     - updateStep: Updated a Step Into a Recipe
     - deleteStep: Delete a Step Into a Recipe. Order the steps that remains
     - getStepById: Get a Step By Id
     - getStepsByIdRecipe: Get a Step List Based on a Recipe ID
     - getStepsOrderByStepOrder: Get a Step List from a Same Recipe Order by stepOrder
     - getStepsWithRequiredTimer: Get a List of Steps That Require Timer

    > INGREDIENT
     - addIngredient: Add an Ingredient Into a Recipe
     - updateIngredient: Update an Ingredient In The Recipe
     - deleteIngredient: Delete Ingredient
     - getIngredientById: get an Ingredient By Its ID
     - getIngredientsByIdRecipe: get Ingredients Based on a recipe ID


    > EXTRAS
     - bitmapToArray: transform BitMap to Array -> To Save in DB
     - arrayToBitmap: transform an array to Bitmap -> To Show Image
     - intToBoolean: transform an Int to a Boolean -> To Show Boolean
     - booleanToInt: transform a Boolean to a Int -> To Save in DB
 */

public class BD_Operations {

    //
    // RECIPES METHODS
    //


    //
    // STEPS METHODS
    //


    //
    // INGREDIENTS METHODS
    //


    //
    // EXTRAS
    //

    // Transform Array to Bitmap -> To Show In App
    public Bitmap ArrayToBitmap(byte [] bitmapData){
        if(bitmapData == null){
            Bitmap solved = null;
            return solved;
        } else return  BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

    // Transform BitMap to Array -> To Save In BD
    public byte[] BitmapToArray(Bitmap bmp){
        if(bmp == null){
            byte[] solved = null;
            return solved;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //Transform Value From DB to Boolean
    private boolean IntToBoolean(int i){
        if(i == 1) return true;
        else return false;
    }

    //Transform Value From Boolean to DB
    private int BooleanToInt(Boolean b){
        if(b) return 1;
        else return 0;
    }


}
