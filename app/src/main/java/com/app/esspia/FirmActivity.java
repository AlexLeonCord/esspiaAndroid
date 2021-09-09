package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.esspia.model.CaptureBitmapView;
import com.app.esspia.model.api.WebApi;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FirmActivity extends AppCompatActivity {
    Integer idClient;
    String nameClient;
    Integer idUser;
    String nameEnginier;
    Integer itemSelectedTypeService;
    String  descriptionTypeService ;
    String  idServiceReport;
    private CaptureBitmapView mSig;
    Button btnSendInformBD;
    Button btnSendReportEmail;

    private RequestQueue mRequestQueueCoProducts;
    private RequestQueue mRequestQueueCoImages;

    ArrayList<String[]> ListProcucts;
    ArrayList<String[]> ListImages;
    ArrayList<Bitmap> ListImagesBitmap;

    String  firmBase64;


    EditText namePerson;
    EditText idPerson;
    String emailClient;
    EditText etNamePDFR;

    private RequestQueue mRequestQueue;

    private TemplatePDF templatePDF;

    private String[]header={"Und","Equipos ","   ","Precio U","Precio T"};
    private String createdBy;
    private String receiveBy;
    private String receiveId;
    private String serviceDes;

    String fecha;


    String correo, asunto, mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firm);

        btnSendInformBD=findViewById(R.id.btnSendInformBD);

       btnSendReportEmail=findViewById(R.id.btnSendReportEmail);
         btnSendReportEmail.setBackgroundColor(getResources().getColor(R.color.grey));
        btnSendReportEmail.setEnabled(false);



        namePerson=findViewById(R.id.namePerson);
        idPerson=findViewById(R.id.idPerson);
        etNamePDFR=findViewById(R.id.etNamePDFR);
        mRequestQueue = Volley.newRequestQueue(FirmActivity.this);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fecha=  "Fecha: "+sdf.format(c.getTime());


        firmBase64="";



        mRequestQueueCoProducts= Volley.newRequestQueue(FirmActivity.this);
        mRequestQueueCoImages= Volley.newRequestQueue(FirmActivity.this);

        Intent intent = getIntent();
        idClient = intent.getIntExtra("idClient",0);
        nameClient = intent.getStringExtra("nameClient");
        emailClient = intent.getStringExtra("emailClient");
        idUser=intent.getIntExtra("idUser",0);
        nameEnginier = intent.getStringExtra("nameEnginier");
        itemSelectedTypeService=intent.getIntExtra("itemSelectedTypeService",0);
        descriptionTypeService = intent.getStringExtra("descriptionTypeService");
        idServiceReport = intent.getStringExtra("idServiceReport");

        //Toast.makeText(this,"   "+idClient +"   "+ idUser+"  "+ descriptionTypeService+"   "+idServiceReport , Toast.LENGTH_SHORT).show();


        createdBy="Ingeniero: "+nameEnginier;
        receiveBy= "Recibido por: "+namePerson.getText().toString();
        receiveId="Identificación: "+idPerson.getText().toString();
        serviceDes="Descripción del servicio: "+descriptionTypeService;


        LinearLayout mContent = (LinearLayout) findViewById(R.id.signLayout);
        mSig = new CaptureBitmapView(this, null);
        mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        ListProcucts= new ArrayList<>();
        ListImages= new ArrayList<>();
        ListImagesBitmap= new ArrayList<>();

        //correo = "alexcord47@gmail.com";
        correo = emailClient;
        asunto = "Asunto";
        mensaje ="Mensaje";


      //  Bitmap signature = mSig.getBitmap();
       // mSig.ClearCanvas();

        btnSendInformBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(namePerson.getText().toString().isEmpty() ){
                    Toast.makeText(FirmActivity.this,"Ingrese el nombre",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(idPerson.getText().toString().isEmpty() ){
                    Toast.makeText(FirmActivity.this,"Ingrese la identificación",Toast.LENGTH_SHORT).show();
                    return;
                }

                SendReport();

                btnSendInformBD.setEnabled(false);
                btnSendInformBD.setBackgroundColor(getResources().getColor(R.color.grey));


                btnSendReportEmail.setEnabled(true);
                btnSendReportEmail.setBackgroundColor(getResources().getColor(R.color.teal_700));

               // namePerson.setText("");
              //  idPerson.setText("");



            }
        });
        btnSendReportEmail.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {





                btnSendReportEmail.setEnabled(false);
                btnSendReportEmail.setBackgroundColor(getResources().getColor(R.color.grey));
                templatePDF= new TemplatePDF(getApplicationContext());

                if (itemSelectedTypeService==1){
                    templatePDF.setNamePDFM(etNamePDFR.getText().toString());
                }
                if (itemSelectedTypeService==3){
                    templatePDF.setNamePDFA(etNamePDFR.getText().toString());
                }


                templatePDF.openDocument(itemSelectedTypeService);
                templatePDF.addMetaData("Clientes","Ventas","Alex");

                int annio;
                String mes;
                int dia;
                String MESES[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
                Calendar c1 = Calendar.getInstance();

                annio = c1.get(Calendar.YEAR);
                mes = MESES[c1.get(Calendar.MONTH)];
                dia= c.get(Calendar.DAY_OF_MONTH);


                try {
                    InputStream inputStream = FirmActivity.this.getAssets().open("esspialogo.jpg");
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image signature;
                    signature = Image.getInstance(stream.toByteArray());
                    //signature.setAbsolutePosition(20f, 0f);
                    //signature.scaleAbsolute(70f, 70f);
                    signature.setAlignment(Element.ALIGN_CENTER);
                    signature.scalePercent(56f);

                    templatePDF.addImage(signature);

                    templatePDF.addParagraph("Cali, Valle.");
                    String Fecha= mes+" "+dia+" de "+annio;

                    templatePDF.addParagraph(Fecha);

                    templatePDF.addParagraph(nameClient);

                   // templatePDF.addParagraph(createdBy);


                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }



                switch(itemSelectedTypeService) {
                    case 1:


                        getImagesByIdService(idServiceReport);

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // tableCotizationProd.removeAllViews();
                                        maintenanceReport();

                                        templatePDF.closeDocumment();

                                      //  templatePDF.viewPDF();

                                        SendEmail();

                                    }
                                }, 1000);




                        break;

                    case 2:




                      //  templatePDF.addTitles("REPORTE DE COTIZACION","Cliente: "+nameClient,fecha,"ID Reporte: "+idServiceReport);
                      //  templatePDF.addParagraph(createdBy);

                        getImagesByIdService(idServiceReport);

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // tableCotizationProd.removeAllViews();

                                        getProductByIdService(idServiceReport);
                                    }
                                }, 1000);





                        break;

                    case 3:

                        getImagesByIdService(idServiceReport);

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // tableCotizationProd.removeAllViews();

                                        deliveryReport();

                                        templatePDF.closeDocumment();

                                       // templatePDF.viewPDF();

                                        SendEmail();
                                    }
                                }, 1000);




                        break;
                    default:

                        break;
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

    public void SendReport() {

        WebApi webApi;
        String url;
        JSONObject jsonObject;
        final ProgressDialog loading;

        switch(itemSelectedTypeService) {
            case 1:

                webApi =new WebApi("maintenancereport");

                  url =webApi.getFullUrl();


                  jsonObject= new JSONObject();
                 loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


                try {

                   jsonObject.put("idClient",idClient);
                   jsonObject.put("idUser",idUser);
                   jsonObject.put("descriptionTypeService",descriptionTypeService);
                   jsonObject.put("idServiceReport",idServiceReport);
                   jsonObject.put("idPerson",idPerson.getText().toString());
                   jsonObject.put("namePerson",namePerson.getText().toString());

                   Bitmap firmBitmap = mSig.getBitmap();

                   firmBase64=bitmapToBase64(firmBitmap);
                   jsonObject.put("firmImage", firmBase64);


                    Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                          //   Toast.makeText(FirmActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();


                            loading.dismiss();


                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FirmActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    };

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
                    mRequestQueue.add(request);

                }catch (JSONException e) {
                    Toast.makeText(FirmActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
                }

                break;

            case 2:

                  webApi=new WebApi("cotizationreport");

                  url=webApi.getFullUrl();


                jsonObject= new JSONObject();
                loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


                try {

                    jsonObject.put("idClient",idClient);
                    jsonObject.put("idUser",idUser);
                    jsonObject.put("descriptionTypeService",descriptionTypeService);
                    jsonObject.put("idServiceReport",idServiceReport);
                    jsonObject.put("idPerson",idPerson.getText().toString());
                    jsonObject.put("namePerson",namePerson.getText().toString());

                    Bitmap firmBitmap = mSig.getBitmap();

                    firmBase64=bitmapToBase64(firmBitmap);
                    jsonObject.put("firmImage", firmBase64);


                    Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                         //   Toast.makeText(FirmActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();



                            loading.dismiss();


                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FirmActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    };

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
                    mRequestQueue.add(request);

                }catch (JSONException e) {
                    Toast.makeText(FirmActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
                }

                break;

            case 3:


                webApi =new WebApi("deliveryreport");

                url =webApi.getFullUrl();


                jsonObject= new JSONObject();
                loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


                try {

                    jsonObject.put("idClient",idClient);
                    jsonObject.put("idUser",idUser);
                    jsonObject.put("descriptionTypeService",descriptionTypeService);
                    jsonObject.put("idServiceReport",idServiceReport);
                    jsonObject.put("idPerson",idPerson.getText().toString());
                    jsonObject.put("namePerson",namePerson.getText().toString());

                    Bitmap firmBitmap = mSig.getBitmap();

                    firmBase64=bitmapToBase64(firmBitmap);
                    jsonObject.put("firmImage", firmBase64);


                    Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                          //  Toast.makeText(FirmActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();


                            loading.dismiss();


                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FirmActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    };

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
                    mRequestQueue.add(request);

                }catch (JSONException e) {
                    Toast.makeText(FirmActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
                }

                break;
            default:

                break;
        }




    }

    private String bitmapToBase64(Bitmap bitmap) {
        // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //  bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        //  byte[] byteArray = byteArrayOutputStream .toByteArray();
        //  return Base64.encodeToString(byteArray, Base64.DEFAULT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String b64) {
        //byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        //  return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        byte[] decodedBytes = Base64.decode( b64.substring(b64.indexOf(",") + 1), Base64.DEFAULT );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }




    public void getProductByIdService(String idService) {


        WebApi webApi=new WebApi("cotizationproducts?idservice="+idService+"&$limit=50");

        String url =webApi.getFullUrl();



        JSONObject jsonObject= new JSONObject();


        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);






    }



    public void getImagesByIdService(String idService) {

        WebApi webApi=new WebApi("images?idservice="+idService+"&$limit=50");

        String url =webApi.getFullUrl();



        JSONObject jsonObject= new JSONObject();


        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {
            jsonObject.put("accessToken","");



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject imageJSon= new JSONObject();


                    Integer id;
                    String idService;
                    String imageStringB64="";
                    String description;
                    Bitmap image=base64ToBitmap(imageStringB64);




                    //Toast.makeText(newInstallation.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    //  Log.e("PRODUCTOSCotization ",""+response.toString());


                    try {


                        // Nota: aquí jsonArray debería venir de una API externa u otro lugar

                        JSONArray results = response.getJSONArray("data");
                      //  templatePDF= new TemplatePDF(getApplicationContext());
                       // templatePDF.openDocument();

                        // tr.removeAllViews();
/*
                        for (int i = 0; i < results.length(); i++){

                            JSONObject aux = results.getJSONObject(i);
                           // id = aux.getInt("id");
                           // idService = aux.getString("idService");
                          //  imageStringB64 = aux.getString("imageString");
                            //description=aux.getString("description");


                         //   Bitmap  bitmap =   base64ToBitmap(imageStringB64);
                         //   ListImagesBitmap.add(bitmap);

                        }
*/
                        for (int i = 0; i < results.length(); i++){

                            JSONObject aux = results.getJSONObject(i);
                            imageStringB64 = aux.getString("imageString");
                            description=aux.getString("description");

                            Bitmap  bitmap =   base64ToBitmap(imageStringB64);


                            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);

                            Image img = Image.getInstance(stream3.toByteArray());

                          //  img.setAbsolutePosition(100, 100);
                            img.scalePercent(130);

                            templatePDF.addParagraph(""+description);
                            templatePDF.addImage(img);




                           //  Log.e("IMAGE ",""+ListImages.get(i)[2]);

                        }

                       // templatePDF.newPage();








                    } catch (JSONException e) {
                        e.printStackTrace();


                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loading.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(FirmActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueueCoImages.add(request);

        }catch (JSONException e) {
            Toast.makeText(FirmActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }


    public void maintenanceReport(){

        receiveBy= "Recibido por: "+namePerson.getText().toString();
        receiveId="Identificación: "+idPerson.getText().toString();

       // templatePDF= new TemplatePDF(getApplicationContext());
       // templatePDF.openDocument();
       // templatePDF.addMetaData("Clientes","Ventas",createdBy);
      //  templatePDF.addTitles("REPORTE DE MANTENIMIENTO","Cliente: "+nameClient,fecha,"ID Reporte: "+idServiceReport);
       // templatePDF.addParagraph(createdBy);

        templatePDF.addParagraph(serviceDes);
        templatePDF.addParagraph(receiveBy);
        templatePDF.addParagraph(receiveId);
        //  templatePDF.closeDocumment();

        ///Firm Image

        Bitmap bitmap= base64ToBitmap(firmBase64);


        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);

        Image img = null;
        try {
            img = Image.getInstance(stream3.toByteArray());
            img.scalePercent(40);
            templatePDF.addParagraph("Firma: ");
            templatePDF.addImage(img);


        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void deliveryReport(){
        receiveBy= "Recibido por: "+namePerson.getText().toString();
        receiveId="Identificación: "+idPerson.getText().toString();

       // templatePDF= new TemplatePDF(getApplicationContext());
       // templatePDF.openDocument();
       // templatePDF.addMetaData("Clientes","Ventas",createdBy);
        templatePDF.addParagraph(createdBy);

        templatePDF.addParagraph(serviceDes);
        templatePDF.addParagraph(receiveBy);
        templatePDF.addParagraph(receiveId);
        //  templatePDF.closeDocumment();

        ///Firm Image

        Bitmap bitmap= base64ToBitmap(firmBase64);


        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);

        Image img = null;
        try {
            img = Image.getInstance(stream3.toByteArray());
            img.scalePercent(40);
            templatePDF.addParagraph("Firma: ");
            templatePDF.addImage(img);


        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public void SendEmail()
    {

        switch(itemSelectedTypeService) {

            case 1:

                // Defino mi Intent y hago uso del objeto ACTION_SEND
                Intent intent = new Intent(Intent.ACTION_SEND);

                // Defino los Strings Email, Asunto y Mensaje con la función putExtra
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { correo });
                intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
                intent.putExtra(Intent.EXTRA_TEXT, mensaje);

                File root= Environment.getExternalStorageDirectory();
                String filelocation= root.getAbsolutePath() + "/"+ "ESSPDFMaintenance"+ "/" + templatePDF.getNamePDFM();
                Log.e("URI ",""+filelocation);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));


                // Establezco el tipo de Intent
                intent.setType("message/rfc822");

                // Lanzo el selector de cliente de Correo
                startActivity(Intent.createChooser(intent, "Elije un cliente de Correo:"));

                // Intent i = new Intent(FirmActivity.this,Welcome.class);
                // i.putExtra("nameEnginier",nameEnginier);
                // startActivity(i);

                btnSendReportEmail.setEnabled(false);
                namePerson.setText("");
                idPerson.setText("");

                break;

            case 3:

                // Defino mi Intent y hago uso del objeto ACTION_SEND
                Intent intentA = new Intent(Intent.ACTION_SEND);

                // Defino los Strings Email, Asunto y Mensaje con la función putExtra
                intentA.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { correo });
                intentA.putExtra(Intent.EXTRA_SUBJECT, asunto);
                intentA.putExtra(Intent.EXTRA_TEXT, mensaje);

                File rootA= Environment.getExternalStorageDirectory();
                String filelocationA= rootA.getAbsolutePath() + "/"+ "ESSPDFAct"+ "/" + templatePDF.getNamePDFA();
                Log.e("URI ",""+filelocationA);
                intentA.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocationA));


                // Establezco el tipo de Intent
                intentA.setType("message/rfc822");

                // Lanzo el selector de cliente de Correo
                startActivity(Intent.createChooser(intentA, "Elije un cliente de Correo:"));

                // Intent i = new Intent(FirmActivity.this,Welcome.class);
                // i.putExtra("nameEnginier",nameEnginier);
                // startActivity(i);

                btnSendReportEmail.setEnabled(false);
                namePerson.setText("");
                idPerson.setText("");


                break;
            default:

        }

    }


}