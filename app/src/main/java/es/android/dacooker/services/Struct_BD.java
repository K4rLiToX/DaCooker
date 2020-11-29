package es.android.dacooker.services;

public class Struct_BD {

    //Recipes Table
    protected static final String RECIPE_TABLE = "RECIPES";
    protected static final String RECIPE_ID = "ID";
    protected static final String RECIPE_NAME = "NAME";
    protected static final String RECIPE_IMAGE = "IMAGE";
    protected static final String RECIPE_MEALTYPE = "MEAL_TYPE";
    protected static final String RECIPE_EXEC_TIME_HOUR = "EXECUTION_HOUR";
    protected static final String RECIPE_EXEC_TIME_MINUTE = "EXECUTION_MINUTE";
    protected static final String RECIPE_DESCRIPTION = "RECIPE_DESCRIPTION";
    protected static final String RECIPE_TIMES_COOKED = "TIMES_COOKED";

    protected static final String SQL_CREATE_RECIPE = "CREATE TABLE " + RECIPE_TABLE + " ("
            + RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RECIPE_NAME + " TEXT,"
            + RECIPE_MEALTYPE + " TEXT,"
            + RECIPE_EXEC_TIME_HOUR + " INTEGER,"
            + RECIPE_EXEC_TIME_MINUTE + " INTEGER,"
            + RECIPE_DESCRIPTION + " TEXT,"
            + RECIPE_TIMES_COOKED + " INTEGER,"
            + RECIPE_IMAGE + " BLOB )";

    protected static final String SQL_DELETE_RECIPE = "DROP TABLE IF EXISTS " + RECIPE_TABLE;

    //Steps Table
    protected static final String STEP_TABLE = "STEPS";
    protected static final String STEP_ID = "ID";
    protected static final String STEP_DESCRIPTION = "DESCRIPTION";
    protected static final String STEP_REQUIRED_TIMER = "REQUIRED_TIMER";
    protected static final String STEP_TIMER_HOUR = "TIMER_HOUR";
    protected static final String STEP_TIMER_MINUTE = "TIMER_MINUTE";
    protected static final String STEP_ORDER = "ORDER_S";
    protected static final String STEP_RECIPE = "ID_RECIPE";

    protected static final String SQL_CREATE_STEP = "CREATE TABLE " + STEP_TABLE + " ("
            + STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + STEP_DESCRIPTION + " TEXT,"
            + STEP_REQUIRED_TIMER + " BOOLEAN,"
            + STEP_TIMER_HOUR + " INTEGER,"
            + STEP_TIMER_MINUTE + " INTEGER,"
            + STEP_ORDER + " INTEGER,"
            + STEP_RECIPE + " INTEGER )";

    protected static final String SQL_DELETE_STEP = "DROP TABLE IF EXISTS " + STEP_TABLE;

    //Ingredients Table
    protected static final String INGREDIENT_TABLE = "INGREDIENTS";
    protected static final String INGREDIENT_ID = "ID";
    protected static final String INGREDIENT_NAME = "NAME";
    protected static final String INGREDIENT_QUANTITY = "QUANTITY";
    protected static final String INGREDIENT_RECIPE = "ID_RECIPE";

    protected static final String SQL_CREATE_INGREDIENT = "CREATE TABLE " + INGREDIENT_TABLE + " ("
            + INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INGREDIENT_NAME + " TEXT,"
            + INGREDIENT_QUANTITY + " TEXT,"
            + INGREDIENT_RECIPE + " INTEGER )";

    protected static final String SQL_DELETE_INGREDIENT = "DROP TABLE IF EXISTS " + INGREDIENT_TABLE;

}
