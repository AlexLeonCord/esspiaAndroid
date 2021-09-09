package com.app.esspia.model.api;

import com.app.esspia.model.Client;

public interface API {

    void login (String user,String password);

    void getClient (int id);

}
