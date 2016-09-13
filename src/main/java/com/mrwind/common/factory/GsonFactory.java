package com.mrwind.common.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by starmezhh on 16/3/18.
 */
public class GsonFactory {

    private static Gson gson = null;

    private GsonFactory(){}

    private static synchronized void init(){
        if(gson == null){
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
        }
    }

    public static Gson getInstance(){
        if(gson==null)
            init();
        return gson;
    }


}
