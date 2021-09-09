package com.app.esspia.model;

import android.app.Application;

import com.app.esspia.model.api.API;
import com.app.esspia.model.api.WebApi;

public class Model {

    private final Application mApplication;
    private static Model sInstance=null;




    private Model(Application application){
        mApplication=application;
       // mApi = new WebApi(mApplication);
    }

    public static Model getInstance(Application aplication) {
        if (sInstance == null){
            sInstance=new Model(aplication);
        }
        return sInstance;
    }
    public  Application getApplication(){
        return mApplication;
    }

    public void login(String user, String password){

    }
}
























