package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.esspia.model.Client;
import com.app.esspia.model.Model;
import com.app.esspia.model.api.API;
import com.app.esspia.model.api.WebApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    EditText etUser;
    EditText etPassword;
    Button btnLogin;
    String idMaintenance;
    int idEnginier;
    String nameEnginier;

    private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        idEnginier=0;
        nameEnginier="";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        idMaintenance = sdf.format(c.getTime());

        Log.e("LA FECHA ACTUAL ",idMaintenance);

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user= etUser.getText().toString();
                String password= etPassword.getText().toString();
               // Toast.makeText(MainActivity.this,"user: "+ user+"  password: "+password,Toast.LENGTH_SHORT).show();


                login(user,password);




            }
        });
    }


    public void login(String user, String password) {

        WebApi webApi=new WebApi("authentication");

        String url =webApi.getFullUrl();


        JSONObject jsonObject= new JSONObject();
        final ProgressDialog loading = ProgressDialog.show(this,"Iniciando Sesión...","Espere por favor...",false,false);


        try {
            jsonObject.put("strategy","local");
            jsonObject.put("email",user);
            jsonObject.put("password",password);

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(MainActivity.this ,Welcome.class);
                    String token = null;
                    JSONObject enginier= new JSONObject();




                    try {
                        token = response.getString("accessToken");
                        enginier = response.getJSONObject("user");
                        idEnginier=enginier.getInt("id");
                        nameEnginier= enginier.getString("name");



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i.putExtra("token",token);
                    i.putExtra("idEnginier",idEnginier);
                    i.putExtra("nameEnginier",nameEnginier);


                    loading.dismiss();
                    startActivity(i);

                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(MainActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }


}
