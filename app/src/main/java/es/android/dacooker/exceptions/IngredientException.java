package es.android.dacooker.exceptions;

public class IngredientException extends Exception{

    //Constructor
    public IngredientException(){

    }

    //Método que muestra el mensaje pasado por parámetro cuando se ejecuta la excepción
    public IngredientException(String msg){
        super(msg);
    }

}
