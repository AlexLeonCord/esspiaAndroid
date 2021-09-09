package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateClient extends AppCompatActivity {

    Spinner  spinnerTypeId;
    EditText etIdClient;
    EditText etNameCompany;
    EditText etNameRepLegal;
    EditText etIdRepresent;
    EditText etEmailClient;
    EditText etPhoneClient;
    EditText etAddressClient;
    Button btnSaveClient;
    Button btnExitClient;

    private RequestQueue mRequestQueue;

    String selectedItemTipeID;

    Integer selectedItemIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);

   spinnerTypeId=findViewById(R.id.spinnerTypeId);
   etIdClient=findViewById(R.id.etIdClient);
   etNameCompany=findViewById(R.id.etNameCompany);
   etNameRepLegal=findViewById(R.id.etNameRepLegal);
   etIdRepresent=findViewById(R.id.etIdRepresent);
   etEmailClient=findViewById(R.id.etEmailClient);
   etPhoneClient=findViewById(R.id.etPhoneClient);
   etAddressClient=findViewById(R.id.etAddressClient);
   btnSaveClient=findViewById(R.id.btnSaveClient);
   btnExitClient =findViewById(R.id.btnExitClient);

   spinnerTypeId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
       @Override
       public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
           selectedItemTipeID= (String)adapterView.getItemAtPosition(i);
           // Toast.makeText(newInstallation.this,"SELECCION"+ selectedItemTipeService,Toast.LENGTH_SHORT).show();
           selectedItemIndex=i;
       }

       @Override
       public void onNothingSelected(AdapterView<?> adapterView) {

       }
   });


           mRequestQueue = Volley.newRequestQueue(CreateClient.this);

        btnSaveClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendClient();
            }
        });

   btnExitClient.setOnClickListener(new View.OnClickListener() {
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



    public void SendClient() {

        if( selectedItemIndex==0 ){

            Toast.makeText(CreateClient.this,"Seleccione el tipo de Identificación",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etNameCompany.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese el nombre de la Compañia",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etNameRepLegal.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese el nombre del representante",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etIdRepresent.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese la Identificación del representante",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etEmailClient.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese el email del cliente",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etPhoneClient.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese el teléfono del cliente",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etAddressClient.getText().toString().isEmpty() ){
            Toast.makeText(CreateClient.this,"Ingrese la dirección del cliente",Toast.LENGTH_SHORT).show();
            return;
        }


        WebApi webApi=new WebApi("cliente");

        String url =webApi.getFullUrl();


        JSONObject jsonObject= new JSONObject();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {

            jsonObject.put("id",Integer.parseInt( etIdClient.getText().toString()));
            jsonObject.put("tipoId",selectedItemTipeID);
            jsonObject.put("compania", etNameCompany.getText().toString());
            jsonObject.put("representante", etNameRepLegal.getText().toString());
            jsonObject.put("idRepresentante",Integer.parseInt(etIdRepresent.getText().toString()));
            jsonObject.put("email", etEmailClient.getText().toString());
            jsonObject.put("telefono",Integer.parseInt(etPhoneClient.getText().toString()));
            jsonObject.put("direccion", etAddressClient.getText().toString());


            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show()

                    selectedItemIndex=0;
                    etIdClient.setText("");
                    etNameCompany.setText("");
                    etNameRepLegal.setText("");
                    etIdRepresent.setText("");
                    etEmailClient.setText("");
                    etPhoneClient.setText("");
                    etAddressClient.setText("");

                    loading.dismiss();
                    Toast.makeText(CreateClient.this,"Cliente Creado",Toast.LENGTH_SHORT).show();



                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreateClient.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(CreateClient.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }


    }
}










