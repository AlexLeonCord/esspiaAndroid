package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Welcome extends AppCompatActivity     {
    private EditText etBuscarCliente;
    private EditText etCliente;
    private EditText etId;
    private EditText etRepCompany;
    private EditText etAddress;
    private EditText etPhone;
    private EditText etEngenier;
    private Button btnCliente;
    private EditText etEmail;
    private Button btnSave;
    private RequestQueue mRequestQueue;

    private EditText etDescription;

    private Integer itemSelectedTypeService;
    private String descriptionTypeService;

    String idMaintenanceReport;
    String idNewInstallationReport;
    String idActReport;

    String nameClient;
    String emailClient;
    String representative;
    String phone;
    String address;

    int idEnginier;
    String nameEnginier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);



       // idMaintenanceReport =  "M"+sdf.format(c.getTime());
       // idNewInstallationReport= "C"+sdf.format(c.getTime());
       // idActReport="A"+sdf.format(c.getTime());

        idMaintenanceReport =  "";
        idNewInstallationReport=  "";
        idActReport= "";

       etBuscarCliente= findViewById(R.id.etSearchClient);
       btnCliente=findViewById(R.id.btnCliente);
       etCliente=findViewById(R.id.etClient);
       etId=findViewById(R.id.etId);
       etRepCompany=findViewById(R.id.etRepCompany);
       etAddress=findViewById(R.id.etAddress);
       etPhone=findViewById(R.id.etPhone);
       etEngenier=findViewById(R.id.etEngenier);
       etEmail=findViewById(R.id.etEmail);
       btnSave= findViewById(R.id.btnSave);
       etDescription=findViewById(R.id.etDescription);
       mRequestQueue = Volley.newRequestQueue(Welcome.this);
       itemSelectedTypeService=0;
       descriptionTypeService="";
       emailClient="";
       representative="";
       phone="";
       address="";


        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        idEnginier=intent.getIntExtra("idEnginier",0);
        nameEnginier = intent.getStringExtra("nameEnginier");
        etEngenier.setText(nameEnginier);

        Toast.makeText(Welcome.this,"Bienvenido "+nameEnginier  ,Toast.LENGTH_LONG).show();



        if(token != null){

           // Toast.makeText(Welcome.this,"RESPUESTA: token: "+ token +  "enginier: "+nameEnginier  ,Toast.LENGTH_SHORT).show();

        }

        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

                idMaintenanceReport =  "M"+sdf.format(c.getTime());
                idNewInstallationReport= "C"+sdf.format(c.getTime());
                idActReport="A"+sdf.format(c.getTime());

                Log.e("idMaintenanceReport",""+idMaintenanceReport);
                Log.e("idNewInstallationReport",""+idNewInstallationReport);
                Log.e("idActReport",""+idActReport);

                Spinner spinner = (Spinner) findViewById(R.id.spinnerTipeService);



                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        itemSelectedTypeService= i;
                       // Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(i)+" index"+i, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });



                getClient(etBuscarCliente.getText().toString(), token);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

           btnSave.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   if (!etDescription.getText().toString().isEmpty()){

                       startActivity(itemSelectedTypeService);


                   }else{
                       Toast.makeText(Welcome.this,"Escriba la descripción del Servicio ",Toast.LENGTH_SHORT).show();
                   }





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

    public void getClient(String id,String token) {

        WebApi webApi=new WebApi("cliente/"+id);

        String url =webApi.getFullUrl();

        JSONObject jsonObject= new JSONObject();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);



        try {
            jsonObject.put("accessToken",token);



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    String Cliente;
                    Integer Identification;
                    String Address;
                    String Phone;
                    String representativeCompany;
                    String Engenier;
                    String Email;

                  //   Toast.makeText(Welcome.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();

                    try {
                        Cliente = response.getString("compania");
                        Identification = response.getInt("id");
                        representativeCompany = response.getString("representante");
                        Address = response.getString("direccion");
                        Phone = response.getString("telefono");
                       // Engenier = response.getString("");





                        Email = response.getString("email");

                         etCliente.setText(Cliente);
                         nameClient=Cliente;
                         etId.setText(Identification.toString());
                         etRepCompany.setText(representativeCompany);
                         representative=representativeCompany;
                         address=Address;
                         phone=Phone;
                         etAddress.setText(Address);
                         etPhone.setText(Phone);
                       //  etEngenier.setText(Engenier);
                         etEmail.setText(Email);
                         emailClient=Email;





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        loading.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Welcome.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(Welcome.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }

    public  void startActivity(int index){
        descriptionTypeService=etDescription.getText().toString();

        switch(index) {
            case 1:
                Intent iMaintenanceReport=new Intent(Welcome.this ,Photo.class);
                 iMaintenanceReport.putExtra("idClient",Integer.parseInt(etId.getText().toString()));
                 iMaintenanceReport.putExtra("nameClient",nameClient);
                 iMaintenanceReport.putExtra("emailClient",emailClient);
                 iMaintenanceReport.putExtra("representative",representative);
                 iMaintenanceReport.putExtra("address",address);
                 iMaintenanceReport.putExtra("phone",phone);
                 iMaintenanceReport.putExtra("idUser",idEnginier);
                 iMaintenanceReport.putExtra("nameEnginier",nameEnginier);
                 iMaintenanceReport.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                 iMaintenanceReport.putExtra("descriptionTypeService",descriptionTypeService);
                 iMaintenanceReport.putExtra("idMaintenanceReport",idMaintenanceReport);

                 startActivity(iMaintenanceReport);

                break;
            case 2:
                Intent iNewInstallation=new Intent(Welcome.this ,newInstallation.class);
                iNewInstallation.putExtra("idClient",Integer.parseInt(etId.getText().toString()));
                iNewInstallation.putExtra("nameClient",nameClient);
                iNewInstallation.putExtra("emailClient",emailClient);
                iNewInstallation.putExtra("representative",representative);
                iNewInstallation.putExtra("address",address);
                iNewInstallation.putExtra("phone",phone);
                iNewInstallation.putExtra("idUser",idEnginier);
                iNewInstallation.putExtra("nameEnginier",nameEnginier);
                iNewInstallation.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                iNewInstallation.putExtra("descriptionTypeService",descriptionTypeService);
                iNewInstallation.putExtra("idNewInstallationReport",idNewInstallationReport);
                startActivity(iNewInstallation);
                break;
            case 3:
                Intent iDeliveryCertificate=new Intent(Welcome.this ,Photo.class);
                iDeliveryCertificate.putExtra("idClient",Integer.parseInt(etId.getText().toString()));
                iDeliveryCertificate.putExtra("nameClient",nameClient);
                iDeliveryCertificate.putExtra("emailClient",emailClient);
                iDeliveryCertificate.putExtra("idUser",idEnginier);
                iDeliveryCertificate.putExtra("nameEnginier",nameEnginier);
                iDeliveryCertificate.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                iDeliveryCertificate.putExtra("descriptionTypeService",descriptionTypeService);
                iDeliveryCertificate.putExtra("idActReport",idActReport);
                startActivity(iDeliveryCertificate);
                break;
            default:

                break;
        }


    }




}