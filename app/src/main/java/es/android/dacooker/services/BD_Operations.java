package es.android.dacooker.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.android.dacooker.models.IngredientModel;
import es.android.dacooker.models.MealType;
import es.android.dacooker.models.RecipeModel;
import es.android.dacooker.models.StepModel;

/*
    IMPLEMENTED METHODS
    * Almost All Methods Require BBDD_Helper instance as their last parameter

    > RECIPES
     - getLastID : Get The last ID to Add Ingredients and Steps to Recipe New
     - getRecipes : Get All Recipes
     - addRecipe: Add Recipe
     - updateRecipe: Update Recipe
     - deleteRecipe: Delete Recipe. His ingredients and steps, too
     - deleteRecipe: Delete Recipe by its ID. His ingredients and steps, too
     - getRecipeById: Get Recipe By ID
     - getRecipesByMealType: Get Recipes with Same MealType
     - getRecipesOrderByExecutionTime: Get Recipes Ordered By Execution
     - getRecipesOrderByLessExecutionTime: Get Recipes With Less Execution Time Than Introduced
     - getRecipesOrderByTimesCooked: Get 5 or Less Recipes Most Cooked

        > STEPS
     - addStep: Add Step
     - updateStep: Updated Step
     - updateStepOrdersPlus: Re-order steps from a Recipe
     - updateStepOrdersLess: Re-order steps from a Recipe
     - deleteStep: Delete Step
     - deleteStepsFromRecipe: Delete Steps From Recipe
     - deleteStepsFromRecipeId: Delete Steps From Recipe ID
     - getStepById: Get Step (ID)
     - getStepsFromRecipeOrdered: Get Ordered Steps From Recipe
     - getStepsFromRecipeIdOrdered: Get Ordered Steps From Recipe ID
     - getStepsLookingRequiredTimer: Get Steps That Require (or No) Timer

    > INGREDIENT
     - addIngredient: Add Ingredient
     - updateIngredient: Update Ingredient
     - deleteIngredient: Delete Ingredient
     - deleteIngredientsFromRecipe: Delete Ingredients From Recipe
     - deleteIngredientsFromRecipeId: Delete Ingredients From Recipe ID
     - getIngredientById: Get Ingredient By ID
     - getIngredientsByIdRecipe: Get Ingredients By Recipe
     - getIngredientsByIdRecipe: Get Ingredients By Recipe ID

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

    public static int getLastID(BBDD_Helper dbHelper){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.RECIPE_ID
        };

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );

        Integer ids = -1;

        if(cursor.moveToLast()){

            ids = cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID));

        }
        cursor.close();
        db.close();
        return ids;

    }

    public static void addRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception {

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Struct_BD.RECIPE_NAME, r.getRecipeName());
        values.put(Struct_BD.RECIPE_MEALTYPE, r.getMealType().toString());
        values.put(Struct_BD.RECIPE_EXEC_TIME, r.getExecutionTime());
        values.put(Struct_BD.RECIPE_TIMES_COOKED, r.getTimesCooked());
        values.put(Struct_BD.RECIPE_IMAGE, BitmapToArray(r.getImage()));

        // Insert the new row, returning the primary key value of the new row
        long row = db.insert(Struct_BD.RECIPE_TABLE, null, values);

        if(row == -1) throw new Exception("Error Ocurred. Not Possible Addition.");

    }

    public static void updateRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Struct_BD.RECIPE_NAME, r.getRecipeName());
        values.put(Struct_BD.RECIPE_MEALTYPE, r.getMealType().toString());
        values.put(Struct_BD.RECIPE_EXEC_TIME, r.getExecutionTime());
        values.put(Struct_BD.RECIPE_DESCRIPTION, r.getRecipeDescription());
        values.put(Struct_BD.RECIPE_TIMES_COOKED, r.getTimesCooked());
        values.put(Struct_BD.RECIPE_IMAGE, BitmapToArray(r.getImage()));

        // Which row to update, based on the title
        String selection = Struct_BD.RECIPE_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(r.getId()) };

        int row = db.update(
                Struct_BD.RECIPE_TABLE,
                values,
                selection,
                selectionArgs);

        if(row <= 0) throw new Exception("Error Ocurred. Not Possible Edition.");

    }

    public static void deleteRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception {

        deleteStepsFromRecipe(r, dbHelper);
        deleteIngredientsFromRecipe(r, dbHelper);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.RECIPE_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(r.getId()) };

        int deletedRows = db.delete(Struct_BD.RECIPE_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Recipe couldn't be deleted. Try later");

    }

    public static void deleteRecipe(int id_recipe, BBDD_Helper dbHelper) throws Exception {

        deleteStepsFromRecipeId(id_recipe, dbHelper);
        deleteIngredientsFromRecipeId(id_recipe, dbHelper);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.RECIPE_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id_recipe) };

        int deletedRows = db.delete(Struct_BD.RECIPE_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Recipe couldn't be deleted. Try later");

    }

    public static List<RecipeModel> getRecipes(BBDD_Helper dbHelper) {
        //Gets the data repository in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Obtener la tabla recetas
        String[] projection = {
            Struct_BD.RECIPE_ID,
            Struct_BD.RECIPE_NAME,
            Struct_BD.RECIPE_MEALTYPE,
            Struct_BD.RECIPE_EXEC_TIME,
            Struct_BD.RECIPE_DESCRIPTION,
            Struct_BD.RECIPE_TIMES_COOKED,
            Struct_BD.RECIPE_IMAGE
        };

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );

        List<RecipeModel> recipes = new ArrayList<>();

        if(cursor.moveToFirst()){
            do {
                //Tratamos la imagen
                byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
                Bitmap imageSaved;
                if(imageBD == null || imageBD.length == 0) imageSaved = null;
                else imageSaved = ArrayToBitmap(imageBD);

                //Tratamos el MealType
                MealType mt = null;
                String mtDB = cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_MEALTYPE));
                for(MealType e : MealType.values()){
                    if(mtDB.equalsIgnoreCase(e.toString())) mt = e;
                }


                RecipeModel r = new RecipeModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                        mt,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                        imageSaved
                );
                recipes.add(r);
            } while(cursor.moveToNext());
        }
            cursor.close();
            db.close();
            return recipes;
    }

    public static RecipeModel getRecipeById(int id_recipe, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Le decimos que queremos obtener de la BD; es decir, qué columnas
        String[] projection = {
            Struct_BD.RECIPE_ID,
            Struct_BD.RECIPE_NAME,
            Struct_BD.RECIPE_MEALTYPE,
            Struct_BD.RECIPE_EXEC_TIME,
            Struct_BD.RECIPE_DESCRIPTION,
            Struct_BD.RECIPE_TIMES_COOKED,
            Struct_BD.RECIPE_IMAGE
        };

        String selection = Struct_BD.RECIPE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id_recipe) };

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );

        if(cursor.moveToFirst()) {  //Si obtenemos resultados

            //Tratamos la imagen
            byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
            Bitmap imageSaved;
            if(imageBD == null || imageBD.length == 0) imageSaved = null;
            else imageSaved = ArrayToBitmap(imageBD);

            //Tratamos el MealType
            MealType mt = null;
            String mtDB = cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_MEALTYPE));
            for(MealType e : MealType.values()){
                if(mtDB.equalsIgnoreCase(e.toString())) mt = e;
            }

            //Creamos la Receta
            RecipeModel r = new RecipeModel(
                    id_recipe,
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                    mt,
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                    imageSaved
            );

            cursor.close();
            db.close();
            return r;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Recipe not Found");
        }

    }

    public static List<RecipeModel> getRecipesByMealType(MealType mealType, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.RECIPE_ID,
                Struct_BD.RECIPE_NAME,
                Struct_BD.RECIPE_MEALTYPE,
                Struct_BD.RECIPE_EXEC_TIME,
                Struct_BD.RECIPE_DESCRIPTION,
                Struct_BD.RECIPE_TIMES_COOKED,
                Struct_BD.RECIPE_IMAGE
        };

        String selection = Struct_BD.RECIPE_MEALTYPE + " = ?";
        String[] selectionArgs = { mealType.name() };

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );


        List<RecipeModel> recipes = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                //Tratamos la imagen
                byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
                Bitmap imageSaved;
                if(imageBD == null || imageBD.length == 0) imageSaved = null;
                else imageSaved = ArrayToBitmap(imageBD);

                RecipeModel r = new RecipeModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                        mealType,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                        imageSaved
                );
                recipes.add(r);
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return recipes;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Recipes Not Found");
        }

    }

    //NO SE SI FUNCIONARÁ, LA VERDAD
    public static List<RecipeModel> getRecipesByLessExecutionTime(String executionTime, BBDD_Helper dbHelper) throws Exception{
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.RECIPE_ID,
                Struct_BD.RECIPE_NAME,
                Struct_BD.RECIPE_MEALTYPE,
                Struct_BD.RECIPE_EXEC_TIME,
                Struct_BD.RECIPE_DESCRIPTION,
                Struct_BD.RECIPE_TIMES_COOKED,
                Struct_BD.RECIPE_IMAGE
        };

        String selection = Struct_BD.RECIPE_EXEC_TIME + " <= ?";
        String[] selectionArgs = { executionTime };

        // How you want the results sorted in the resulting Cursor
        //String sortOrder = Struct_BBDD.NOMBRE_COLUMNA_2 + " DESC";

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null //sortOrder   // The sort order
        );


        List<RecipeModel> recipes = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do{
                //Tratamos la imagen
                byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
                Bitmap imageSaved;
                if(imageBD == null || imageBD.length == 0) imageSaved = null;
                else imageSaved = ArrayToBitmap(imageBD);

                //Tratamos el MealType
                MealType mt = null;
                String mtDB = cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_MEALTYPE));
                for(MealType e : MealType.values()){
                    if(mtDB.equalsIgnoreCase(e.toString())) mt = e;
                }

                RecipeModel r = new RecipeModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                        mt,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                        imageSaved
                );
                recipes.add(r);
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return recipes;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Recipes Not Found");
        }

    }

    public static List<RecipeModel> getRecipesOrderByExecutionTime(BBDD_Helper dbHelper) throws Exception{
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.RECIPE_ID,
                Struct_BD.RECIPE_NAME,
                Struct_BD.RECIPE_MEALTYPE,
                Struct_BD.RECIPE_EXEC_TIME,
                Struct_BD.RECIPE_DESCRIPTION,
                Struct_BD.RECIPE_TIMES_COOKED,
                Struct_BD.RECIPE_IMAGE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Struct_BD.RECIPE_EXEC_TIME + " DESC";

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        List<RecipeModel> recipes = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do{
                //Tratamos la imagen
                byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
                Bitmap imageSaved;
                if(imageBD == null || imageBD.length == 0) imageSaved = null;
                else imageSaved = ArrayToBitmap(imageBD);

                //Tratamos el MealType
                MealType mt = null;
                String mtDB = cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_MEALTYPE));
                for(MealType e : MealType.values()){
                    if(mtDB.equalsIgnoreCase(e.toString())) mt = e;
                }

                RecipeModel r = new RecipeModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                        mt,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                        imageSaved
                );
                recipes.add(r);
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return recipes;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Recipes Not Found");
        }

    }

    public static List<RecipeModel> getRecipesOrderByTimesCooked(BBDD_Helper dbHelper) throws Exception{
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.RECIPE_ID,
                Struct_BD.RECIPE_NAME,
                Struct_BD.RECIPE_MEALTYPE,
                Struct_BD.RECIPE_EXEC_TIME,
                Struct_BD.RECIPE_DESCRIPTION,
                Struct_BD.RECIPE_TIMES_COOKED,
                Struct_BD.RECIPE_IMAGE
        };

        String sortOrder = Struct_BD.RECIPE_TIMES_COOKED + " DESC";

        Cursor cursor = db.query(
                Struct_BD.RECIPE_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                null,
                null,
                null,              // don't group the rows
                null,               // don't filter by row groups
                sortOrder     // The sort order
        );


        List<RecipeModel> recipes = new ArrayList<>();
        int untilFive = 0;

        if(cursor.moveToFirst()) {
            do{
                //Tratamos la imagen
                byte[] imageBD = cursor.getBlob(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_IMAGE));
                Bitmap imageSaved;
                if(imageBD == null || imageBD.length == 0) imageSaved = null;
                else imageSaved = ArrayToBitmap(imageBD);

                //Tratamos el MealType
                MealType mt = null;
                String mtDB = cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_MEALTYPE));
                for(MealType e : MealType.values()){
                    if(mtDB.equalsIgnoreCase(e.toString())) mt = e;
                }

                RecipeModel r = new RecipeModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_NAME)),
                        mt,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_EXEC_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.RECIPE_TIMES_COOKED)),
                        imageSaved
                );
                recipes.add(r);
                untilFive--;
            } while(cursor.moveToNext() && untilFive > 0);

            cursor.close();
            db.close();
            return recipes;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Recipes Not Found");
        }

    }

    //
    // STEPS METHODS
    //

    public static void addStep(StepModel s, int id_recipe, BBDD_Helper dbHelper) throws Exception {

        updateStepOrdersPlus(s, dbHelper);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Struct_BD.STEP_DESCRIPTION, s.getDescription());
        values.put(Struct_BD.STEP_REQUIRED_TIMER, BooleanToInt(s.isRequiredTimer()));
        values.put(Struct_BD.STEP_TIMER_TIME, s.getTimerTime());
        values.put(Struct_BD.STEP_ORDER, s.getStepOrder());
        values.put(Struct_BD.STEP_RECIPE, id_recipe);

        // Insert the new row, returning the primary key value of the new row
        long row = db.insert(Struct_BD.STEP_TABLE, null, values);

        if(row == -1) throw new Exception("Error Ocurred. Not Possible Addition.");

    }

    public static void updateStep(StepModel s, BBDD_Helper dbHelper) throws Exception {

        updateStepOrdersPlus(s, dbHelper);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Struct_BD.STEP_DESCRIPTION, s.getDescription());
        values.put(Struct_BD.STEP_REQUIRED_TIMER, s.isRequiredTimer());
        values.put(Struct_BD.STEP_TIMER_TIME, s.getTimerTime());
        values.put(Struct_BD.STEP_ORDER, s.getStepOrder());

        // Which row to update, based on the title
        String selection = Struct_BD.STEP_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(s.getId()) };

        int row = db.update(
                Struct_BD.STEP_TABLE,
                values,
                selection,
                selectionArgs);

        if(row <= 0) throw new Exception("Error Ocurred. Not Possible Edition.");

    }

    public static void updateStepOrdersPlus(StepModel s, BBDD_Helper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] param = { String.valueOf(s.getId()),
                            String.valueOf(s.getStepOrder()) };
        Cursor cursor = db.rawQuery("SELECT order_s FROM STEPS" +
                " WHERE id = ? and order_s >= ?  ", param);

        if(cursor.moveToFirst()) {
            do {
                boolean boolSaved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                StepModel sn = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        boolSaved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))

                );

                ContentValues values = new ContentValues();
                values.put(Struct_BD.STEP_ORDER, sn.getStepOrder()+1);

                // Which row to update, based on the title
                String selection = Struct_BD.STEP_ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(sn.getId()) };

                db.update(
                        Struct_BD.STEP_TABLE,
                        values,
                        selection,
                        selectionArgs);

                cursor.moveToNext();
            } while(cursor.moveToNext());
        }
    }

    public static void updateStepOrdersLess(StepModel s, BBDD_Helper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] param = { String.valueOf(s.getId()),
                String.valueOf(s.getStepOrder()) };
        Cursor cursor = db.rawQuery("SELECT order_s FROM STEPS" +
                " WHERE id = ? and order_s >= ?  ", param);

        if(cursor.moveToFirst()) {
            do {
                boolean boolSaved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                StepModel sn = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        boolSaved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))

                );

                ContentValues values = new ContentValues();
                values.put(Struct_BD.STEP_ORDER, sn.getStepOrder()-1);

                // Which row to update, based on the title
                String selection = Struct_BD.STEP_ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(sn.getId()) };

                db.update(
                        Struct_BD.STEP_TABLE,
                        values,
                        selection,
                        selectionArgs);

                cursor.moveToNext();
            } while(cursor.moveToNext());
        }
    }

    public static void deleteStep(StepModel s, BBDD_Helper dbHelper) throws Exception {

        updateStepOrdersLess(s, dbHelper);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.STEP_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(s.getId()) };

        int deletedRows = db.delete(Struct_BD.STEP_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Step couldn't be deleted. Try later");

    }

    public static void deleteStep(int id_step, BBDD_Helper dbHelper) throws Exception {

        updateStepOrdersLess(getStepById(id_step, dbHelper), dbHelper);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.STEP_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id_step) };

        int deletedRows = db.delete(Struct_BD.STEP_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Step couldn't be deleted. Try later");

    }

    public static void deleteStepsFromRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] param = { String.valueOf(r.getId()) };
        Cursor cursor = db.rawQuery("SELECT * FROM STEPS" +
                " WHERE id_recipe = ? ;", param);

        if(cursor.moveToFirst()) {
            do {
                boolean boolSaved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                StepModel sn = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        boolSaved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))

                );

                // Which row to update, based on the title
                String selection = Struct_BD.STEP_ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(sn.getId()) };

                db.delete(
                        Struct_BD.STEP_TABLE,
                        selection,
                        selectionArgs);

                cursor.moveToNext();
            } while(cursor.moveToNext());
        }

    }

    public static void deleteStepsFromRecipeId(int id_recipe, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] param = { String.valueOf(id_recipe) };
        Cursor cursor = db.rawQuery("SELECT * FROM STEPS" +
                " WHERE id_recipe = ? ;", param);

        if(cursor.moveToFirst()) {
            do {
                boolean boolSaved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                StepModel sn = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        boolSaved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))

                );

                // Which row to update, based on the title
                String selection = Struct_BD.STEP_ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(sn.getId()) };

                db.delete(
                        Struct_BD.STEP_TABLE,
                        selection,
                        selectionArgs);

                cursor.moveToNext();
            } while(cursor.moveToNext());
        }

    }

    public static StepModel getStepById(int id_step, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Le decimos que queremos obtener de la BD; es decir, qué columnas
        String[] projection = {
                Struct_BD.STEP_ID,
                Struct_BD.STEP_DESCRIPTION,
                Struct_BD.STEP_REQUIRED_TIMER,
                Struct_BD.STEP_TIMER_TIME,
                Struct_BD.STEP_ORDER,
                Struct_BD.STEP_RECIPE
        };

        String selection = Struct_BD.STEP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id_step) };

        Cursor cursor = db.query(
                Struct_BD.STEP_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );

        if(cursor.moveToFirst()) {  //Si obtenemos resultados

            //Tratamos el booleano
            boolean saved = IntToBoolean(cursor.getInt(
                            cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

            //Creamos el Step
            StepModel s = new StepModel(
                    id_step,
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                    saved,
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))
            );

            cursor.close();
            db.close();
            return s;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Step not Found");
        }

    }

    public static List<StepModel> getStepsFromRecipeOrdered(RecipeModel r, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.STEP_ID,
                Struct_BD.STEP_DESCRIPTION,
                Struct_BD.STEP_REQUIRED_TIMER,
                Struct_BD.STEP_TIMER_TIME,
                Struct_BD.STEP_ORDER,
                Struct_BD.STEP_RECIPE
        };

        String selection = Struct_BD.STEP_RECIPE + " = ?";
        String[] selectionArgs = { String.valueOf(r.getId()) };

        String sortOrder = Struct_BD.STEP_ORDER + " ASC";

        Cursor cursor = db.query(
                Struct_BD.STEP_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                sortOrder     // The sort order
        );


        List<StepModel> steps = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                //Tratamos el booleano
                boolean saved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                //Creamos el Step
                StepModel s = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        saved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))
                );
                steps.add(s);
                cursor.moveToNext();    //Pasamos a la siguiente posición
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return steps;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Steps Not Found");
        }

    }

    public static List<StepModel> getStepsFromRecipeIdOrdered(int id_recipe, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.STEP_ID,
                Struct_BD.STEP_DESCRIPTION,
                Struct_BD.STEP_REQUIRED_TIMER,
                Struct_BD.STEP_TIMER_TIME,
                Struct_BD.STEP_ORDER,
                Struct_BD.STEP_RECIPE
        };

        String selection = Struct_BD.STEP_RECIPE + " = ?";
        String[] selectionArgs = { String.valueOf(id_recipe) };

        String sortOrder = Struct_BD.STEP_ORDER + " ASC";

        Cursor cursor = db.query(
                Struct_BD.STEP_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                sortOrder     // The sort order
        );


        List<StepModel> steps = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do{
                //Tratamos el booleano
                boolean saved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                //Creamos el Step
                StepModel s = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        saved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))
                );
                steps.add(s);
                cursor.moveToNext();    //Pasamos a la siguiente posición
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return steps;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Steps Not Found");
        }

    }

    public static List<StepModel> getStepsLookingRequiredTimer(boolean req_timer, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.STEP_ID,
                Struct_BD.STEP_DESCRIPTION,
                Struct_BD.STEP_REQUIRED_TIMER,
                Struct_BD.STEP_TIMER_TIME,
                Struct_BD.STEP_ORDER,
                Struct_BD.STEP_RECIPE
        };

        int boolBD = BooleanToInt(req_timer);

        String selection = Struct_BD.STEP_RECIPE + " = ?";
        String[] selectionArgs = { String.valueOf(boolBD) };

        Cursor cursor = db.query(
                Struct_BD.STEP_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                null,
                null,
                null,              // don't group the rows
                null,               // don't filter by row groups
                null
        );


        List<StepModel> steps = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                StepModel s = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        req_timer,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))
                );
                steps.add(s);
                cursor.moveToNext();    //Pasamos a la siguiente posición
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return steps;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Steps Not Found");
        }

    }

    //
    // INGREDIENTS METHODS
    //

    public static void addIngredient(IngredientModel i, int id_recipe, BBDD_Helper dbHelper) throws Exception {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Struct_BD.INGREDIENT_NAME, i.getIngredientName());
        values.put(Struct_BD.INGREDIENT_QUANTITY, i.getQuantity());
        values.put(Struct_BD.INGREDIENT_RECIPE, id_recipe);

        // Insert the new row, returning the primary key value of the new row
        long row = db.insert(Struct_BD.INGREDIENT_TABLE, null, values);

        if(row == -1) throw new Exception("Error Ocurred. Not Possible Addition.");

    }

    public static void updateIngredient(IngredientModel i, BBDD_Helper dbHelper) throws Exception {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Struct_BD.INGREDIENT_NAME, i.getIngredientName());
        values.put(Struct_BD.INGREDIENT_QUANTITY, i.getQuantity());
        values.put(Struct_BD.INGREDIENT_RECIPE, i.getIdRecipe());

        // Which row to update, based on the title
        String selection = Struct_BD.INGREDIENT_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(i.getId()) };

        int row = db.update(
                Struct_BD.INGREDIENT_TABLE,
                values,
                selection,
                selectionArgs);

        if(row <= 0) throw new Exception("Error Ocurred. Not Possible Edition.");

    }

    public static void deleteIngredient(IngredientModel i, BBDD_Helper dbHelper) throws Exception {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.INGREDIENT_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(i.getId()) };

        int deletedRows = db.delete(Struct_BD.INGREDIENT_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Ingredient couldn't be deleted. Try later");

    }

    public static void deleteIngredientsFromRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = Struct_BD.INGREDIENT_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(r.getId()) };

        int deletedRows = db.delete(Struct_BD.INGREDIENT_TABLE, selection, selectionArgs);

        if(deletedRows <= 0) throw new Exception("Ingredient(s) couldn't be deleted. Try later");

    }

    public static void deleteIngredientsFromRecipeId(int id_recipe, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] param = { String.valueOf(id_recipe) };
        Cursor cursor = db.rawQuery("SELECT * FROM STEPS" +
                " WHERE id_recipe = ? ;", param);

        if(cursor.moveToFirst()) {
            do{
                boolean boolSaved = IntToBoolean(cursor.getInt(
                        cursor.getColumnIndexOrThrow(Struct_BD.STEP_REQUIRED_TIMER)));

                StepModel sn = new StepModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_DESCRIPTION)),
                        boolSaved,
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.STEP_TIMER_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_ORDER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.STEP_RECIPE))

                );

                // Which row to update, based on the title
                String selection = Struct_BD.STEP_ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(sn.getId()) };

                db.delete(
                        Struct_BD.STEP_TABLE,
                        selection,
                        selectionArgs);

                cursor.moveToNext();
            } while(cursor.moveToNext());
        }

    }

    public static IngredientModel getIngredientById(int id_ingredient, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Le decimos que queremos obtener de la BD; es decir, qué columnas
        String[] projection = {
                Struct_BD.INGREDIENT_ID,
                Struct_BD.INGREDIENT_NAME,
                Struct_BD.INGREDIENT_QUANTITY,
                Struct_BD.INGREDIENT_RECIPE
        };

        String selection = Struct_BD.INGREDIENT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id_ingredient) };

        Cursor cursor = db.query(
                Struct_BD.INGREDIENT_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null//sortOrder     // The sort order
        );

        if(cursor.moveToFirst()) {  //Si obtenemos resultados

            //Creamos el Ingredient
            IngredientModel i = new IngredientModel(
                    id_ingredient,
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_QUANTITY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_RECIPE))
            );

            cursor.close();
            db.close();
            return i;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Ingredient not Found");
        }

    }

    public static List<IngredientModel> getIngredientsByRecipe(RecipeModel r, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.INGREDIENT_ID,
                Struct_BD.INGREDIENT_NAME,
                Struct_BD.INGREDIENT_QUANTITY,
                Struct_BD.INGREDIENT_RECIPE
        };

        String selection = Struct_BD.INGREDIENT_RECIPE + " = ?";
        String[] selectionArgs = { String.valueOf(r.getId()) };

        Cursor cursor = db.query(
                Struct_BD.INGREDIENT_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null     // The sort order
        );


        List<IngredientModel> ingredients = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                //Creamos el Ingredient
                IngredientModel i = new IngredientModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_RECIPE))
                );
                ingredients.add(i);
                cursor.moveToNext();    //Pasamos a la siguiente posición
            } while(cursor.moveToNext());
            cursor.close();
            db.close();
            return ingredients;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Ingredients Not Found");
        }

    }

    public static List<IngredientModel> getIngredientsByIdRecipe(int id_recipe, BBDD_Helper dbHelper) throws Exception{

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Struct_BD.INGREDIENT_ID,
                Struct_BD.INGREDIENT_NAME,
                Struct_BD.INGREDIENT_QUANTITY,
                Struct_BD.INGREDIENT_RECIPE
        };

        String selection = Struct_BD.INGREDIENT_RECIPE + " = ?";
        String[] selectionArgs = { String.valueOf(id_recipe) };

        Cursor cursor = db.query(
                Struct_BD.INGREDIENT_TABLE,     // The table to query
                projection,                 // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,              // don't group the rows
                null,               // don't filter by row groups
                null     // The sort order
        );


        List<IngredientModel> ingredients = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                //Creamos el Ingredient
                IngredientModel i = new IngredientModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Struct_BD.INGREDIENT_RECIPE))
                );
                ingredients.add(i);
            } while(cursor.moveToNext());

            cursor.close();
            db.close();
            return ingredients;

        } else{
            cursor.close();
            db.close();
            throw new Exception("Ingredients Not Found");
        }

    }

    //
    // EXTRAS
    //

    // Transform Array to Bitmap -> To Show In App
    public static Bitmap ArrayToBitmap(byte [] bitmapData){
        if(bitmapData == null){
            Bitmap solved = null;
            return solved;
        } else return  BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

    // Transform BitMap to Array -> To Save In BD
    public static byte[] BitmapToArray(Bitmap bmp){
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
    private static boolean IntToBoolean(int i){
        if(i == 1) return true;
        else return false;
    }

    //Transform Value From Boolean to DB
    private static int BooleanToInt(Boolean b){
        if(b) return 1;
        else return 0;
    }


}
