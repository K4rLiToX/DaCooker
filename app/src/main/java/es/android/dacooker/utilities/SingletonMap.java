package es.android.dacooker.utilities;

import java.util.HashMap;

public class SingletonMap extends HashMap<String, Object> {

    public SingletonMap(){}

    public static SingletonMap getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final SingletonMap instance = new SingletonMap();
    }
}
