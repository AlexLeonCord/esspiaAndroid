package com.app.esspia;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.esspia.model.CaptureBitmapView;
import com.app.esspia.model.api.WebApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Photo extends AppCompatActivity {
    Button btnPhoto;
    Button btnNextToFirm;
    ImageView ivPhoto;
    ArrayList<Bitmap> bitmaps ;
    private RequestQueue mRequestQueue;
    Button btnSavePhoto;
    Bitmap imageBitmap;

    String idReporte;
    EditText etInfoPhoto;
    private CaptureBitmapView mSig;


    Integer idClient;
    String nameClient;

    String representative;
    String phone;
    String address;

    String emailClient;
    Integer idUser;
    String nameEnginier;
    Integer itemSelectedTypeService;
    String  descriptionTypeService ;
    String  idServiceReport;

    // Banderas que indicarán si tenemos permisos
    private boolean tienePermisoCamara = false;
    private boolean tienePermisoAlmacenamiento = false;

    // Código de permiso
    private static final int CODIGO_PERMISOS_CAMARA = 1;
    private static final int CODIGO_PERMISOS_ALMACENAMIENTO = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        btnPhoto= findViewById(R.id.btnPhoto);
        ivPhoto= findViewById(R.id.ivPhoto);
        bitmaps= new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(Photo.this);
        imageBitmap=null;
        btnSavePhoto = findViewById(R.id.btnSavePhotos);
        btnNextToFirm = findViewById(R.id.btnNextToFirm);



        etInfoPhoto=findViewById(R.id.etInfoPhoto);
        etInfoPhoto.setText("");

        emailClient="";


        Intent intent = getIntent();
        idClient = intent.getIntExtra("idClient",0);
        nameClient = intent.getStringExtra("nameClient");
        representative=intent.getStringExtra("representative");
        address=intent.getStringExtra("address");
        phone=intent.getStringExtra("phone");
        emailClient= intent.getStringExtra("emailClient");
        idUser=intent.getIntExtra("idUser",0);
        nameEnginier = intent.getStringExtra("nameEnginier");
        itemSelectedTypeService=intent.getIntExtra("itemSelectedTypeService",0);
        descriptionTypeService = intent.getStringExtra("descriptionTypeService");


        if(itemSelectedTypeService==1){
            idServiceReport = intent.getStringExtra("idMaintenanceReport");
        }
        if(itemSelectedTypeService==2){
            idServiceReport = intent.getStringExtra("idNewInstallationReport");
        }
        if(itemSelectedTypeService==3){
            idServiceReport = intent.getStringExtra("idActReport");
        }

        LinearLayout mContent = (LinearLayout) findViewById(R.id.layoutPhoto);
        mSig = new CaptureBitmapView(this, null);
        mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);



        Log.e("LA FECHA ACTUAL ",idServiceReport);



        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (imageBitmap!=null && !etInfoPhoto.getText().toString().isEmpty()){

                    String bitmapToBase64= bitmapToBase64(imageBitmap);
                    Log.e("LA IMAGEN BASE64: ",bitmapToBase64);

                sendPhoto(idServiceReport,bitmapToBase64,etInfoPhoto.getText().toString());

               //  Log.w("IMAGEN BASE64",bitmapToBase64) ;



                }else{
                    Toast.makeText(Photo.this,"Escriba la descripción de la fotografía ",Toast.LENGTH_SHORT).show();
                }

            }
        });
/*
        if(validaPermisos()){
            btnPhoto.setEnabled(true);
        }else{
            btnPhoto.setEnabled(false);
        }
*/
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // dispatchTakePictureIntent();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verificarYPedirPermisosDeCamara();
                    verificarYPedirPermisosDeAlmacenamiento();

                    if(tienePermisoCamara && tienePermisoAlmacenamiento){
                        dispatchTakePictureIntent();
                    }
                    else{
                        Toast.makeText(Photo.this,"No tiene permisos de camara y almacenamiento",Toast.LENGTH_SHORT).show();

                    }

                }else{
                    dispatchTakePictureIntent();

                }
            }

        });

        btnNextToFirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(itemSelectedTypeService==2){

                    Intent intentToCotization=new Intent(Photo.this ,CotizationActivity.class);
                    intentToCotization.putExtra("idClient",idClient);
                    intentToCotization.putExtra("nameClient",nameClient);
                    intentToCotization.putExtra("representative",representative);
                    intentToCotization.putExtra("address",address);
                    intentToCotization.putExtra("phone",phone);
                    intentToCotization.putExtra("emailClient",emailClient);
                    intentToCotization.putExtra("idUser",idUser);
                    intentToCotization.putExtra("nameEnginier",nameEnginier);
                    intentToCotization.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                    intentToCotization.putExtra("descriptionTypeService",descriptionTypeService);
                    intentToCotization.putExtra("idServiceReport",idServiceReport);
                    startActivity(intentToCotization);


                }else {

                    Intent intentToFirm=new Intent(Photo.this ,FirmActivity.class);
                    intentToFirm.putExtra("idClient",idClient);
                    intentToFirm.putExtra("nameClient",nameClient);
                    intentToFirm.putExtra("emailClient",emailClient);
                    intentToFirm.putExtra("idUser",idUser);
                    intentToFirm.putExtra("nameEnginier",nameEnginier);
                    intentToFirm.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                    intentToFirm.putExtra("descriptionTypeService",descriptionTypeService);
                    intentToFirm.putExtra("idServiceReport",idServiceReport);
                    startActivity(intentToFirm);

                }



            }
        });




    }



        static final int REQUEST_IMAGE_CAPTURE = 1;

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
             bitmaps.add(imageBitmap);

                 Log.i("bitmap"," LONGITUD "+bitmaps.size());

            ivPhoto.setImageBitmap(imageBitmap);
        }
    }



    private String bitmapToBase64(Bitmap bitmap) {
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

       bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
       byte[] byteArray = byteArrayOutputStream .toByteArray();

        Log.e("IMAGE EN STRING ",""+Base64.encodeToString(byteArray, Base64.DEFAULT));

       return Base64.encodeToString(byteArray, Base64.DEFAULT);

       // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      //  bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
       // return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);



    }



    private Bitmap ase64ToBitmap(String b64) {
       //byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
      //  return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);


        byte[] decodedBytes = Base64.decode( b64.substring(b64.indexOf(",") + 1), Base64.DEFAULT );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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

    public void sendPhoto(String idService,String image, String description) {

        WebApi webApi=new WebApi("images");

        String url =webApi.getFullUrl();

        final ProgressDialog loading = ProgressDialog.show(this,"Subiendo...","Espere por favor...",false,false);
        JSONObject jsonObject= new JSONObject();


        try {
            jsonObject.put("idService", idService);
           // jsonObject.put("image",image);
            jsonObject.put("imageString",image);
            jsonObject.put("description",description);


            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                     etInfoPhoto.setText("");
                    Toast.makeText(Photo.this,"Imagen guardada correctamente",Toast.LENGTH_SHORT).show();



                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                     Toast.makeText(Photo.this,"Ocurrio un error, intente de nuevo ",Toast.LENGTH_LONG).show();
                     Log.e("EL ERROR ",""+error);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(Photo.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap convertX(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode( base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convertY(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(Photo.this, CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoDeCamaraConcedido();
        } else {
            // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(Photo.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }
    private void verificarYPedirPermisosDeAlmacenamiento() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(Photo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoDeAlmacenamientoConcedido();
        } else {
            // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(Photo.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODIGO_PERMISOS_ALMACENAMIENTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeCamaraConcedido();
                } else {
                    permisoDeCamaraDenegado();
                }
                break;

            case CODIGO_PERMISOS_ALMACENAMIENTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeAlmacenamientoConcedido();
                } else {
                    permisoDeAlmacenamientoDenegado();
                }
                break;

            // Aquí más casos dependiendo de los permisos
            // case OTRO_CODIGO_DE_PERMISOS...

        }
    }


    private void permisoDeAlmacenamientoConcedido() {
        // Aquí establece las banderas o haz lo que
        // ibas a hacer cuando el permiso se concediera. Por
        // ejemplo puedes poner la bandera en true y más
        // tarde en otra función comprobar esa bandera
        Toast.makeText(Photo.this, "El permiso para el almacenamiento está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoAlmacenamiento = true;
    }

    private void permisoDeAlmacenamientoDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(Photo.this, "El permiso para el almacenamiento está denegado", Toast.LENGTH_SHORT).show();
    }

    private void permisoDeCamaraConcedido() {
        // Aquí establece las banderas o haz lo que
        // ibas a hacer cuando el acceso a la cámara se condeciera
        // Por ejemplo puedes poner la bandera en true y más
        // tarde en otra función comprobar esa bandera
        Toast.makeText(Photo.this, "El permiso para la cámara está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoCamara = true;
    }

    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(Photo.this, "El permiso para la cámara está denegado", Toast.LENGTH_SHORT).show();
    }




    /*

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
             cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(Photo.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
                }
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                btnPhoto.setEnabled(true);
            }
            //else{
            //    solicitarPermisosManual();
           // }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(Photo.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }


     */

}

