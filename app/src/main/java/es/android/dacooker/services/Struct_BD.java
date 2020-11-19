package es.android.dacooker.services;

public class Struct_BD {

    //Recipes Table
    private static final String RECIPE_TABLE = "RECIPES";
    private static final String RECIPE_ID = "ID";
    private static final String RECIPE_NAME = "NAME";
    private static final String RECIPE_IMAGE = "IMAGE";
    private static final String RECIPE_MEALTYPE = "MEAL_TYPE";
    private static final String RECIPE_EXEC_TIME = "EXECUTION_TIME";
    private static final String RECIPE_TIMES_COOKED = "TIMES_COOKED";

    protected static final String SQL_CREATE_RECIPE = "CREATE TABLE " + RECIPE_TABLE + " ("
            + RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RECIPE_NAME + " TEXT,"
            + RECIPE_MEALTYPE + " TEXT,"
            + RECIPE_EXEC_TIME + " TEXT,"
            + RECIPE_TIMES_COOKED + " INTEGER,"
            + RECIPE_IMAGE + " BLOB )";

    protected static final String SQL_DELETE_RECIPE = "DROP TABLE IF EXISTS " + RECIPE_TABLE;

    //Steps Table
    private static final String STEP_TABLE = "RECIPES";
    private static final String STEP_ID = "ID";
    private static final String STEP_DESCRIPTION = "DESCRIPTION";
    private static final String STEP_REQUIRED_TIMER = "REQUIRED_TIMER";
    private static final String STEP_TIMER_TIME = "TIMER_TIME";
    private static final String STEP_RECIPE = "ID_RECIPE";

    protected static final String SQL_CREATE_STEP = "CREATE TABLE " + STEP_TABLE + " ("
            + STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + STEP_DESCRIPTION + " TEXT,"
            + STEP_REQUIRED_TIMER + " BOOLEAN,"
            + STEP_TIMER_TIME + " TEXT,"
            + STEP_RECIPE + " INTEGER )";

    protected static final String SQL_DELETE_STEP = "DROP TABLE IF EXISTS " + STEP_TABLE;

    //Ingredients Table
    private static final String INGREDIENT_TABLE = "INGREDIENTS";
    private static final String INGREDIENT_ID = "ID";
    private static final String INGREDIENT_NAME = "NAME";
    private static final String INGREDIENT_QUANTITY = "QUANTITY";
    private static final String INGREDIENT_RECIPE = "ID_RECIPE";

    protected static final String SQL_CREATE_INGREDIENT = "CREATE TABLE " + INGREDIENT_TABLE + " ("
            + INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INGREDIENT_NAME + " TEXT,"
            + INGREDIENT_QUANTITY + " REAL,"
            + INGREDIENT_RECIPE + " INTEGER )";

    protected static final String SQL_DELETE_INGREDIENT = "DROP TABLE IF EXISTS " + INGREDIENT_TABLE;

}
