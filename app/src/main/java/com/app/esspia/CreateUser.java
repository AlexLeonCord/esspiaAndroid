package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.app.esspia.model.api.WebApi;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUser extends AppCompatActivity {

    EditText etIdUser;
    EditText etNameUser;
    EditText etEmailUser;
    EditText  etPasswordUser;
    EditText etPhoneUser;
    Button btnSaveUser;
    Button btnExitUser;

    private RequestQueue mRequestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        etIdUser=findViewById(R.id.etIdUser);
        etNameUser=findViewById(R.id.etNameUser);
        etEmailUser=findViewById(R.id.etEmailUser);
        etPasswordUser=findViewById(R.id.etPasswordUser);
        etPhoneUser=findViewById(R.id.etPhoneUser);
        btnSaveUser=findViewById(R.id.btnSaveUser);
        btnExitUser=findViewById(R.id.btnExitUser);

        mRequestQueue = Volley.newRequestQueue(CreateUser.this);


        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUser();
            }
        });

        btnExitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_createUser) {

            Intent intent = new Intent(this, CreateUser.class);
            startActivity(intent);
            return true;

        }
        if (id == R.id.action_createClient) {
            Intent intent = new Intent(this, CreateClient.class);
            startActivity(intent);
        }
        if (id == R.id.action_createProduct) {
            Intent intent = new Intent(this, CreateProduct.class);
            startActivity(intent);
        }
        if (id == R.id.action_exit) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendUser() {


        if(etIdUser.getText().toString().isEmpty() ){
            Toast.makeText(CreateUser.this,"Ingrese la Identificación",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etNameUser.getText().toString().isEmpty() ){
            Toast.makeText(CreateUser.this,"Ingrese el nombre",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etEmailUser.getText().toString().isEmpty() ){
            Toast.makeText(CreateUser.this,"Ingrese el email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etPasswordUser.getText().toString().isEmpty() ){
            Toast.makeText(CreateUser.this,"Ingrese la Contraseña",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etPhoneUser.getText().toString().isEmpty() ){
            Toast.makeText(CreateUser.this,"Ingrese el teléfono",Toast.LENGTH_SHORT).show();
            return;
        }



        WebApi webApi=new WebApi("users");

        String url =webApi.getFullUrl();


        JSONObject jsonObject= new JSONObject();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {

            jsonObject.put("cedula",Integer.parseInt( etIdUser.getText().toString()));
            jsonObject.put("name", etNameUser.getText().toString());
            jsonObject.put("email", etEmailUser.getText().toString());
            jsonObject.put("password", etPasswordUser.getText().toString());
            jsonObject.put("phone", etPhoneUser.getText().toString());



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show()

                    etIdUser.setText("");
                    etNameUser.setText("");
                    etEmailUser.setText("");
                    etPasswordUser.setText("");
                    etPhoneUser.setText("");

                    loading.dismiss();
                    Toast.makeText(CreateUser.this,"Usuario Creado",Toast.LENGTH_SHORT).show();



                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreateUser.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(CreateUser.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }


    }
}






