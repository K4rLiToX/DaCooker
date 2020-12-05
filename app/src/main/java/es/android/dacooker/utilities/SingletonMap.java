package es.android.dacooker.utilities;

import java.util.HashMap;

/*
    Patron Singleton con el que enviar instancias de informacion de una
        activity-fragment a otros en el momento que lo necesitemos
 */
public class SingletonMap extends HashMap<String, Object> {

    public SingletonMap(){}

    public static SingletonMap getInstance(){
        return SingletonHolder.instance;    //Obtiene la instancia
    }

    private static class SingletonHolder {
        private static final SingletonMap instance = new SingletonMap();
    }
}
