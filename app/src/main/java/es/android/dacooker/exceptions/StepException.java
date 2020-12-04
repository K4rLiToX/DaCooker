package es.android.dacooker.exceptions;

public class StepException extends Exception {

    //Constructor
    public StepException(){

    }

    //Método que muestra el mensaje pasado por parámetro cuando se ejecuta la excepción
    public StepException(String msg){
        super(msg);
    }

}
