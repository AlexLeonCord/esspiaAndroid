package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.itextpdf.text.BaseColor;
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

public class CotizationActivity extends AppCompatActivity {
    Integer idClient;
    String nameClient;
    int numberCotizationsForClient;
    Integer idUser;
    String nameEnginier;
    Integer itemSelectedTypeService;
    String  descriptionTypeService ;
    String  idServiceReport;
    private CaptureBitmapView mSig;
    Button btnSendInformBDE;
    Button btnSendReportEmailE;

    private RequestQueue mRequestQueueCoProducts;
    private RequestQueue mRequestQueueCoImages;

    ArrayList<String[]> ListProcucts;
    ArrayList<String[]> ListImages;
    ArrayList<Bitmap> ListImagesBitmap;

    String  firmBase64;


    EditText namePerson;
    EditText idPerson;
    String representative;
    String phone;
    String address;
    String emailClient;

    private RequestQueue mRequestQueue;

    private TemplatePDF templatePDF;

    private String[]header={"Und","Equipos ","   ","Precio U","Precio T"};
    private String createdBy;
    private String receiveBy;
    private String receiveId;
    private String serviceDes;

    String fecha;



    String correo, asunto, mensaje;


    EditText  etTituloTable;
    EditText  etVigenciaCotization;

    EditText  etDescServicio;
    EditText  etFormaPago;
    EditText  etTiempoEntrega;
    EditText  etIVA;
    EditText  etGarantia;
    EditText  etObservaciones;
    EditText  etNamePDF;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotization);

        btnSendInformBDE=findViewById(R.id.btnSendInformBDE);

        btnSendReportEmailE=findViewById(R.id.btnSendReportEmailE);
        btnSendReportEmailE.setBackgroundColor(getResources().getColor(R.color.grey));
        btnSendReportEmailE.setEnabled(false);

        numberCotizationsForClient=0;



        //  namePerson=findViewById(R.id.namePerson);
        //idPerson=findViewById(R.id.idPerson);

        mRequestQueue = Volley.newRequestQueue(CotizationActivity.this);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fecha=  "Fecha: "+sdf.format(c.getTime());


        firmBase64="";



        mRequestQueueCoProducts= Volley.newRequestQueue(CotizationActivity.this);
        mRequestQueueCoImages= Volley.newRequestQueue(CotizationActivity.this);

        Intent intent = getIntent();
        idClient = intent.getIntExtra("idClient",0);
        nameClient = intent.getStringExtra("nameClient");
        representative=intent.getStringExtra("representative");
        address=intent.getStringExtra("address");
        phone=intent.getStringExtra("phone");
        emailClient = intent.getStringExtra("emailClient");
        idUser=intent.getIntExtra("idUser",0);
        nameEnginier = intent.getStringExtra("nameEnginier");
        itemSelectedTypeService=intent.getIntExtra("itemSelectedTypeService",0);
        descriptionTypeService = intent.getStringExtra("descriptionTypeService");
        idServiceReport = intent.getStringExtra("idServiceReport");

        //Toast.makeText(this,"   "+idClient +"   "+ idUser+"  "+ descriptionTypeService+"   "+idServiceReport , Toast.LENGTH_SHORT).show();


        etTituloTable = findViewById(R.id.etTituloTable);
        etVigenciaCotization= findViewById(R.id.etVigenciaCotization);

        etDescServicio= findViewById(R.id.etDescServicio);
        etFormaPago= findViewById(R.id.etFormaPago);
        etTiempoEntrega= findViewById(R.id.etTiempoEntrega);
        etIVA= findViewById(R.id.etIVA);
        etGarantia= findViewById(R.id.etGarantia);
        etObservaciones= findViewById(R.id.etObservaciones);
        etNamePDF=findViewById(R.id.etNamePDF);





        createdBy="Informe realizado por: "+nameEnginier;
      //  receiveBy= "Recibido por: "+namePerson.getText().toString();
      //  receiveId="Identificación: "+idPerson.getText().toString();
        serviceDes="Descripción del servicio: "+descriptionTypeService;


      //  LinearLayout mContent = (LinearLayout) findViewById(R.id.signLayout);
      //  mSig = new CaptureBitmapView(this, null);
      //  mContent.addView(mSig, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        ListProcucts= new ArrayList<>();
        ListImages= new ArrayList<>();
        ListImagesBitmap= new ArrayList<>();

        //correo = "alexcord47@gmail.com";
        correo = emailClient;
        asunto = "Asunto";
        mensaje ="Mensaje";


        //  Bitmap signature = mSig.getBitmap();
        // mSig.ClearCanvas();


        getAmountCotizationsForClient(idClient);

        btnSendInformBDE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                SendReport();

                btnSendInformBDE.setEnabled(false);
                btnSendInformBDE.setBackgroundColor(getResources().getColor(R.color.grey));


                btnSendReportEmailE.setEnabled(true);
                btnSendReportEmailE.setBackgroundColor(getResources().getColor(R.color.teal_700));





            }
        });
        btnSendReportEmailE.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {


                if(etTituloTable.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese la oferta comercial",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etVigenciaCotization.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese la vigencia",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etDescServicio.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese la descripción",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etFormaPago.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese la forma de pago",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etTiempoEntrega.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese el tiempo de entrega",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etGarantia.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese la garantía",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etNamePDF.getText().toString().isEmpty() ){
                    Toast.makeText(CotizationActivity.this,"Ingrese el nombre del PDF",Toast.LENGTH_SHORT).show();
                    return;
                }



                btnSendReportEmailE.setEnabled(false);
                btnSendReportEmailE.setBackgroundColor(getResources().getColor(R.color.grey));

                templatePDF= new TemplatePDF(getApplicationContext());
                templatePDF.setNamePDFC(etNamePDF.getText().toString());

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
                    InputStream inputStream = CotizationActivity.this.getAssets().open("esspialogo.jpg");
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image signature;
                    signature = Image.getInstance(stream.toByteArray());
                    //signature.setAbsolutePosition(20f, 0f);
                    //signature.scaleAbsolute(70f, 70f);
                    signature.setAlignment(Element.ALIGN_CENTER);
                    //signature.scalePercent(35f);
                    signature.scalePercent(56f);

                    templatePDF.addImage(signature);

                    templatePDF.addParagraphHeader("Cali, Valle.");
                    String Fecha= mes+" "+dia+" de "+annio;

                    templatePDF.addParagraphHeader(Fecha);

                    templatePDF.addParagraphHeader(nameClient);




                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }


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

                webApi=new WebApi("cotizationreport");

                url=webApi.getFullUrl();


                jsonObject= new JSONObject();
                loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


                try {

                    jsonObject.put("idClient",idClient);
                    jsonObject.put("idUser",idUser);
                    jsonObject.put("descriptionTypeService",descriptionTypeService);
                    jsonObject.put("idServiceReport",idServiceReport);
                  //  jsonObject.put("idPerson",idPerson.getText().toString());
                   // jsonObject.put("namePerson",namePerson.getText().toString());

                  //  Bitmap firmBitmap = mSig.getBitmap();

                   // firmBase64=bitmapToBase64(firmBitmap);
                   // jsonObject.put("firmImage", firmBase64);


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
                            Toast.makeText(CotizationActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    };

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
                    mRequestQueue.add(request);

                }catch (JSONException e) {
                    Toast.makeText(CotizationActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
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


        try {
            jsonObject.put("accessToken","");



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject product= new JSONObject();


                    String idService;
                    String nameProduct;
                    Integer idProduct;
                    Double price;
                    Integer quantity;
                    Double total;
                    Double subtotalSum=0.0;

                    String priceString;
                    String subTotalS;
                    String subTotalString;

                    String imageProduct="";




                    //Toast.makeText(newInstallation.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    //  Log.e("PRODUCTOSCotization ",""+response.toString());


                    try {


                        // Nota: aquí jsonArray debería venir de una API externa u otro lugar

                        JSONArray results = response.getJSONArray("data");

                        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
                        simbolo.setDecimalSeparator('.');
                        simbolo.setGroupingSeparator(',');
                        DecimalFormat df = new DecimalFormat("###,###.##",simbolo);
                        df.setMaximumFractionDigits(2);

                        // tr.removeAllViews();
                        for (int i = 0; i < results.length(); i++){


                            JSONObject aux = results.getJSONObject(i);
                            //  id = aux.getInt("id");
                            idService = aux.getString("idService");
                            idProduct = aux.getInt("idProduct");
                            nameProduct=aux.getString("nameProduct");
                            price= aux.getDouble("price");
                            quantity = aux.getInt("quantity");
                            total = aux.getDouble("total");
                            // imageProduct=aux.getString("image");
                           imageProduct="";

                            // listaProductsString.add(name+"  "+description+ "  " +price);
                            // DecimalFormat df = new DecimalFormat("#");
                            //df.setMaximumFractionDigits(2);
                            priceString= df.format(price);
                            subTotalS= df.format(total);
                            subtotalSum+=total;

                            Log.e("LOGO PRODUROT ",""+imageProduct);





                            ListProcucts.add(new String[]{quantity.toString(),nameProduct ,imageProduct ,priceString,subTotalS});
                            // Log.e("PRODUCTOSCotization ",""+ListProcucts.get(i)[0]);
                        }

                        // DecimalFormat dfSum = new DecimalFormat("#");
                        // dfSum.setMaximumFractionDigits(2);
                        subTotalString= df.format(subtotalSum);

                        double iva;

                        if (etIVA.getText().toString().isEmpty() || etIVA.getText().toString()=="0" || etIVA.getText().toString()=="0.0"){
                            iva=0;
                        }else{
                            iva=(subtotalSum * 19)/100;
                        }

                        String ivaStringFormat=df.format(iva);

                        Double totalFactura=iva+subtotalSum;
                        String totalFacturaStringF= df.format(totalFactura);

                        String infoManager=
                                        "Daniel Morales \n" +
                                        "Manager \n" +
                                        "Calle 35 Norte No 3n - 96 \n" +
                                        "Tel: (2) 3765574 \n" +
                                        "Cel: 312 386 11 13 \n" +
                                        "manager@esspia.com";




                                templatePDF.addParagraph(serviceDes);
                                templatePDF.createTable(header,ListProcucts,
                                etTituloTable.getText().toString(),"COTIZACION # "+numberCotizationsForClient,"VIGENCIA DE LA COTIZACION "+etVigenciaCotization.getText().toString(),
                                "Empresa: "+nameClient, "Contacto: "+representative, etDescServicio.getText().toString(),"Teléfono: "+phone,"Dirección: "+address,
                                "FORMA DE PAGO: "+etFormaPago.getText().toString(), "SUBTOTAL","$ "+subTotalString,
                                "TIEMPO DE ENTREGA: "+etTiempoEntrega.getText().toString(), "IVA","$ "+ivaStringFormat ,
                                "GARANTIA: "+etGarantia.getText().toString(),"TOTAL","$ "+totalFacturaStringF,
                                infoManager,etObservaciones.getText().toString());

                        // templatePDF.addParagraph(TotalString);


                        templatePDF.addParagraphFooter("Cordialmente", new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD));
                        templatePDF.addParagraph("");
                        templatePDF.addParagraph("");
                        Font font=new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD);
                        Font fontLink=new Font(Font.FontFamily.TIMES_ROMAN,15,Font.UNDERLINE,BaseColor.BLUE);
                        templatePDF.addParagraphFooter("Daniel Morales", font);
                        templatePDF.addParagraphFooter("Manager", font);
                        templatePDF.addParagraph("");
                        templatePDF.addParagraph("");
                        templatePDF.addParagraphFooter("", font);
                        templatePDF.addParagraphHeader(createdBy);
                        templatePDF.addParagraphFooter("Sede Cali: Calle 35 Norte No 3n - 96 Cali, Valle    Tel: +57 (2) 3765574", font);
                        templatePDF.addParagraphFooter("Cel: 312 3861113",font);
                        templatePDF.addParagraphFooter("Email: manager@esspia.com", font);
                        templatePDF.addParagraphFooter("Web:", font);
                        templatePDF.addParagraphFooter("www.esspia.com", fontLink);




                        //  templatePDF.addParagraph(receiveBy);
                        //  templatePDF.addParagraph(receiveId);
                        //  templatePDF.closeDocumment();





                        templatePDF.closeDocumment();

                    // templatePDF.viewPDF();

                        SendEmail();


                    } catch (JSONException   e) {
                        e.printStackTrace();

                    }
                    loading.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CotizationActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueueCoProducts.add(request);

        }catch (JSONException e) {
            Toast.makeText(CotizationActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
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


                        for (int i = 0; i < results.length(); i++){

                            JSONObject aux = results.getJSONObject(i);
                            imageStringB64 = aux.getString("imageString");
                            description=aux.getString("description");

                            Bitmap  bitmap =   base64ToBitmap(imageStringB64);


                            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);

                            Image img = Image.getInstance(stream3.toByteArray());

                            //  img.setAbsolutePosition(100, 100);
                            //img.scalePercent(100);

                            templatePDF.addParagraph(""+description);
                            templatePDF.addImage(img);




                            //  Log.e("IMAGE ",""+ListImages.get(i)[2]);

                        }

                       // templatePDF.newPage();


                        //  templatePDF.closeDocumment();

                        // templatePDF.viewPDF();

                        // SendEmail();





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
                    Toast.makeText(CotizationActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueueCoImages.add(request);

        }catch (JSONException e) {
            Toast.makeText(CotizationActivity.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }


    public void getAmountCotizationsForClient(int idClient) {

        WebApi webApi=new WebApi("cotizationreport?idClient="+idClient+"&$limit=0");

        String url =webApi.getFullUrl();

            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();


                    try {
                        numberCotizationsForClient=response.getInt("total") + 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CotizationActivity.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,successListener,errorListener);
            mRequestQueue.add(request);


    }



    public void SendEmail()
    {
        Intent intent=null;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            // Defino mi Intent y hago uso del objeto ACTION_SEND   TO, Uri.parse( "mailto:" +correo)
            intent = new Intent(Intent.ACTION_SEND);
        }
        else{
            // Defino mi Intent y hago uso del objeto ACTION_SEND   TO, Uri.parse( "mailto:" +correo)
              intent = new Intent(Intent.ACTION_SENDTO, Uri.parse( "mailto:" +correo));
        }

        // Defino los Strings Email, Asunto y Mensaje con la función putExtra
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { correo });
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        File root= Environment.getExternalStorageDirectory();
        String filelocation= root.getAbsolutePath() + "/"+ "ESSPDFCotization"+ "/" + templatePDF.getNamePDFC();
        Log.e("URI ",""+filelocation);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));


        // Establezco el tipo de Intent
        intent.setType("message/rfc822");

        // Lanzo el selector de cliente de Correo
        startActivity(Intent.createChooser(intent, "Elije un cliente de Correo:"));

        // Intent i = new Intent(FirmActivity.this,Welcome.class);
        // i.putExtra("nameEnginier",nameEnginier);
        // startActivity(i);

        btnSendReportEmailE.setEnabled(false);
       // namePerson.setText("");
        //idPerson.setText("");



    }


}