package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.esspia.model.Product;
import com.app.esspia.model.api.WebApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class newInstallation extends AppCompatActivity {

    ListView listProducts;
    Spinner spinnerTipeService;
    Spinner spinnerProducts;

    String selectedItemTipeService;
    Integer selectedItemIndex;
    String selectedItemProduct;

    Product product;
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueueCoProducts;


    ArrayList<String> listaProductsString;
    ArrayList<Product> listProduct;
    ArrayList<Product> listProductsToSend;

    ArrayList<Product> listProductTable=new ArrayList<Product>();
    ArrayList<String> listProductStringTable=new ArrayList<String>();


    Button btnSearchProduct;
    EditText etSearchProduct;

    EditText etNombreProduct;
    EditText etDescriptionProduct;
    EditText etPriceProduct;
    EditText etQuantityProduct;
    Integer idProduct;

    Button btnAddProduct;
    Button btnNext;

    Double PriceProduct;

    Integer idClient;
    String nameClient;
    String representative;
    String address;
    String phone;
    Integer idUser;
    String nameEnginier;
    String emailClient;
    Integer itemSelectedTypeService;
    String  descriptionTypeService ;
    String  idNewInstallationReport;

    Integer id;

    TableLayout tableCotizationProd;
    TableRow trProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_installation);

        PriceProduct=0.0;



        spinnerTipeService = (Spinner) findViewById(R.id.spinnerTipeService);
        spinnerProducts = (Spinner) findViewById(R.id.spinnerProductos);

        btnSearchProduct=findViewById(R.id.btnSearchProduct);
        etSearchProduct=findViewById(R.id.etSearchProduct);
        btnAddProduct=findViewById(R.id.btnAddProduct);
        btnNext=findViewById(R.id.btnNext);

        etNombreProduct=findViewById(R.id.etNombreProduct);
        etDescriptionProduct=findViewById(R.id.etDescrptionProduct);
        etPriceProduct=findViewById(R.id.etPriceProduct);
        etQuantityProduct=findViewById(R.id.etQuantityProduct);

        tableCotizationProd=findViewById(R.id.tableCotizationProd);
        trProduct=findViewById(R.id.trProduct);

        selectedItemProduct="";


        emailClient="";
        idProduct=-1;
        id=-1;

        listProductsToSend=new ArrayList<Product>();


        Intent intent = getIntent();
        idClient = intent.getIntExtra("idClient",0);
        emailClient=intent.getStringExtra("emailClient");
        representative=intent.getStringExtra("representative");
        address=intent.getStringExtra("address");
        phone=intent.getStringExtra("phone");

        nameClient=intent.getStringExtra("nameClient");
        idUser=intent.getIntExtra("idUser",0);
        nameEnginier=intent.getStringExtra("nameEnginier");
        itemSelectedTypeService=intent.getIntExtra("itemSelectedTypeService",0);
        descriptionTypeService = intent.getStringExtra("descriptionTypeService");
        idNewInstallationReport = intent.getStringExtra("idNewInstallationReport");


        mRequestQueue = Volley.newRequestQueue(newInstallation.this);
        mRequestQueueCoProducts= Volley.newRequestQueue(newInstallation.this);

        //listProducts=findViewById(R.id.listProducts);

        spinnerTipeService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItemTipeService= (String)adapterView.getItemAtPosition(i);
               // Toast.makeText(newInstallation.this,"SELECCION"+ selectedItemTipeService,Toast.LENGTH_SHORT).show();
                selectedItemIndex=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedItemProduct=(String)adapterView.getItemAtPosition(i);

                Product p = listProduct.get(i);

                idProduct=p.getId();

                etNombreProduct.setText("");
                etDescriptionProduct.setText("");
                etPriceProduct.setText("");

                etNombreProduct.setText(p.getName());
                etDescriptionProduct.setText(p.getDescription());
              //  etPriceProduct.setText("$ "+p.getPrice().toString());

                DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
                simbolo.setDecimalSeparator('.');
                simbolo.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat("###,###.##",simbolo);
                df.setMaximumFractionDigits(2);
                PriceProduct=p.getPrice();
                etPriceProduct.setText(df.format(p.getPrice()));

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSearchProduct(etSearchProduct.getText().toString());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendProduct();

/*
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                               // tableCotizationProd.removeAllViews();
                                getProductByIdService(idNewInstallationReport);
                            }
                        }, 1000);
*/
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iNewInstallation=new Intent(newInstallation.this ,Photo.class);
                iNewInstallation.putExtra("idClient",idClient);
                iNewInstallation.putExtra("nameClient",nameClient);

                iNewInstallation.putExtra("representative",representative);
                iNewInstallation.putExtra("address",address);
                iNewInstallation.putExtra("phone",phone);

                iNewInstallation.putExtra("emailClient",emailClient);
                iNewInstallation.putExtra("idUser",idUser);
                iNewInstallation.putExtra("nameEnginier",nameEnginier);
                iNewInstallation.putExtra("itemSelectedTypeService",itemSelectedTypeService);
                iNewInstallation.putExtra("descriptionTypeService",descriptionTypeService);
                iNewInstallation.putExtra("idNewInstallationReport",idNewInstallationReport);
                startActivity(iNewInstallation);
            }
        });

        getProductByIdService(idNewInstallationReport);

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


    public void getProducts() {

        WebApi webApi=new WebApi("product");

        String url =webApi.getFullUrl();

        JSONObject jsonObject= new JSONObject();
        listaProductsString =new ArrayList<String>();

        try {
             jsonObject.put("accessToken","");



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject product= new JSONObject();

                    Integer id;
                    String name;
                    String description;
                    Double price;


                     Toast.makeText(newInstallation.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("PRODUCTOS ",""+response.toString());




                    try {

                        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
                        simbolo.setDecimalSeparator('.');
                        simbolo.setGroupingSeparator(',');
                        DecimalFormat df = new DecimalFormat("###,###.##",simbolo);
                        df.setMaximumFractionDigits(2);

                        // Nota: aquí jsonArray debería venir de una API externa u otro lugar

                        JSONArray results = response.getJSONArray("data");
                        for (int i = 0; i < results.length(); i++){
                            JSONObject aux = results.getJSONObject(i);
                             id = aux.getInt("id");
                             name = aux.getString("name");
                             description = aux.getString("description");
                             price = aux.getDouble("price");


                            listaProductsString.add(name+"  "+df.format(price));

                            Product pro= new Product();
                              pro.setId(id);
                              pro.setName(name);
                              pro.setDescription(description);
                              pro.setPrice(price);

                              listProduct.add(pro);

                            Log.e("PRODUCTO ",id.toString()+"  "+name+"  "+description+ "  " +df.format(price));

                        }

                        spinnerProducts.setAdapter(new ArrayAdapter<String>(newInstallation.this, android.R.layout.simple_spinner_dropdown_item, listaProductsString));





                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(newInstallation.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(newInstallation.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSearchProduct(String filter) {

        WebApi webApi=new WebApi("product?name[$like]="+filter +"%25&$limit=50");

        String url =webApi.getFullUrl();



        JSONObject jsonObject= new JSONObject();
        listProduct=new ArrayList<Product>();
        listaProductsString=new ArrayList<String>();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {
            jsonObject.put("accessToken","");



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject product= new JSONObject();

                    Integer id;
                    String name;
                    String description;
                    Double price;


                    //Toast.makeText(newInstallation.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("PRODUCTOS ",""+response.toString());


                    try {

                        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
                        simbolo.setDecimalSeparator('.');
                        simbolo.setGroupingSeparator(',');
                        DecimalFormat df = new DecimalFormat("###,###.##",simbolo);
                        df.setMaximumFractionDigits(2);
                        // Nota: aquí jsonArray debería venir de una API externa u otro lugar

                        JSONArray results = response.getJSONArray("data");
                        for (int i = 0; i < results.length(); i++){
                            JSONObject aux = results.getJSONObject(i);
                            id = aux.getInt("id");
                            name = aux.getString("name");
                            description = aux.getString("description");
                            price = aux.getDouble("price");



                            listaProductsString.add(name+"  $ " +df.format(price));


                            Product pro= new Product();
                            pro.setId(id);
                            pro.setName(name);
                            pro.setDescription(description);
                            pro.setPrice(price);

                            listProduct.add(pro);

                            Log.e("PRODUCTO ",id.toString()+"  "+name+"  "+description+ "  " +price);

                        }

                        spinnerProducts.setAdapter(new ArrayAdapter<String>(newInstallation.this, android.R.layout.simple_spinner_dropdown_item, listaProductsString));





                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    loading.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(newInstallation.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(newInstallation.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }

    public void SendProduct() {

        if( selectedItemProduct.isEmpty() ){

            Toast.makeText(newInstallation.this,"Seleccione un producto",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etQuantityProduct.getText().toString().isEmpty() ){
            Toast.makeText(newInstallation.this,"Ingrese la cantidad del producto",Toast.LENGTH_SHORT).show();
            return;
        }


        WebApi webApi=new WebApi("cotizationproducts");

        String url =webApi.getFullUrl();


        JSONObject jsonObject= new JSONObject();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {

            jsonObject.put("idService",idNewInstallationReport);
            jsonObject.put("idProduct",idProduct);
            jsonObject.put("nameProduct", etNombreProduct.getText().toString());
            jsonObject.put("price", PriceProduct);
            jsonObject.put("quantity",Integer.parseInt(etQuantityProduct.getText().toString()));
            Double total =( PriceProduct) * Double.parseDouble(etQuantityProduct.getText().toString()) ;
            jsonObject.put("total",total);



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(MainActivity.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show()


                    spinnerProducts.setAdapter(null);
                    selectedItemProduct="";
                    etNombreProduct.setText("");
                    etDescriptionProduct.setText("");
                    etPriceProduct.setText("");
                    etQuantityProduct.setText("");

                    loading.dismiss();
                    Toast.makeText(newInstallation.this,"Producto Agregado",Toast.LENGTH_SHORT).show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // tableCotizationProd.removeAllViews();
                                      getProductByIdService(idNewInstallationReport);
                                }
                            }, 1000);

                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(newInstallation.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(newInstallation.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }


    }


    public void getProductByIdService(String idService) {

        WebApi webApi=new WebApi("cotizationproducts?idservice="+idService+"&$limit=50");

        String url =webApi.getFullUrl();



        JSONObject jsonObject= new JSONObject();
        listProductTable=new ArrayList<Product>();
        listProductStringTable=new ArrayList<String>();
        final ProgressDialog loading = ProgressDialog.show(this,"Cargando...","Espere por favor...",false,false);


        try {
            jsonObject.put("accessToken","");



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject product= new JSONObject();


                    String idService;
                    String nameProduct;
                     String nameProductShort;
                    Integer idProduct;
                    Integer quantity;
                    Double sutotal;
                    String subtotalStr;
                    Double total;
                    String totalStr;




                    //Toast.makeText(newInstallation.this,"Success response"+ response.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("PRODUCTOSCotization ",""+response.toString());


                    try {

                        Integer idP;


                        // Nota: aquí jsonArray debería venir de una API externa u otro lugar

                        JSONArray results = response.getJSONArray("data");

                        TableRow tr;
                        TextView tvId;
                        TextView tvNamePro;
                        TextView tvQuantity;
                        TextView tvPrice;
                        TextView tvIdService;
                        TextView tvTotal;
                        //TableRow totalRow;

                        Button btnDelete;

                        tr = new TableRow(newInstallation.this);


                        tvNamePro = new TextView(newInstallation.this);
                        tvQuantity = new TextView(newInstallation.this);
                        tvPrice = new TextView(newInstallation.this);
                        tvTotal= new TextView(newInstallation.this);

                        tvNamePro.setText("NOMBRE  ");
                        tvQuantity.setText("UNDS  ");
                        tvPrice.setText("TOTAL  ");

                        tvNamePro.setTypeface(Typeface.DEFAULT_BOLD);
                        tvQuantity.setTypeface(Typeface.DEFAULT_BOLD);
                        tvPrice.setTypeface(Typeface.DEFAULT_BOLD);


                        tableCotizationProd.removeAllViews();

                        tr.addView(tvNamePro);
                        tr.addView(tvQuantity);
                        tr.addView(tvPrice);

                        tableCotizationProd.addView(tr);

                        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
                        simbolo.setDecimalSeparator('.');
                        simbolo.setGroupingSeparator(',');
                        DecimalFormat df = new DecimalFormat("###,###.##",simbolo);
                        df.setMaximumFractionDigits(2);
                       // tr.removeAllViews();

                        total=0.0;

                        for (int i = 0; i < results.length(); i++){

                            tr = new TableRow(newInstallation.this);
                            tvId = new TextView(newInstallation.this);
                            tvIdService = new TextView(newInstallation.this);
                            tvNamePro = new TextView(newInstallation.this);
                            tvPrice = new TextView(newInstallation.this);
                            tvQuantity = new TextView(newInstallation.this);

                            btnDelete= new Button(newInstallation.this );
                            btnDelete.setText("Eliminar");



                            JSONObject aux = results.getJSONObject(i);
                            Integer idX = aux.getInt("id");
                            idService = aux.getString("idService");
                            idProduct = aux.getInt("idProduct");
                              nameProduct=aux.getString("nameProduct");

                            quantity = aux.getInt("quantity");
                            sutotal = aux.getDouble ("total");
                            total+=sutotal;
                           // listaProductsString.add(name+"  "+description+ "  " +price);

                            Log.e("ID DEL PRODUCTO COT  ",""+id);
                            Log.e("TOTAL   ",""+sutotal);


                            subtotalStr= df.format(sutotal);

                            tvId.setText(idX.toString());
                            tvIdService.setText(idService.toString());

                            int lengtName=nameProduct.length();

                            if (lengtName>20){
                                 nameProductShort=nameProduct.substring(0,20);
                            }else{
                                nameProductShort=nameProduct.substring(0,lengtName);
                            }



                            tvNamePro.setText(nameProductShort+"  ");

                            tvPrice.setText(subtotalStr+"  ");
                            tvQuantity.setText(quantity.toString()+"  ");


                            btnDelete.setId(i);


                            btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    deleteCotizationProduct(idX);
                                    Log.e("ID DEL PRODUCTO COT  ",""+id);

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {

                                                    getProductByIdService(idNewInstallationReport);
                                                }
                                            }, 1500);



                                }
                            });

                            tr.addView(tvNamePro);
                            tr.addView(tvQuantity);
                            tr.addView(tvPrice);
                            tr.addView(btnDelete);

                            tableCotizationProd.addView(tr);

                        }
                         totalStr= df.format(total);
                         tr = new TableRow(newInstallation.this);

                        tvTotal  = new TextView(newInstallation.this);
                        tvTotal.setText("");
                        tr.addView(tvTotal);

                        tvTotal  = new TextView(newInstallation.this);
                        tvTotal.setText("TOTAL $ ");
                      //  tvTotal.setTextColor( getResources().getColor(R.color.teal_700));
                        tvTotal.setTypeface(Typeface.DEFAULT_BOLD);
                        tr.addView(tvTotal);

                        tvTotal  = new TextView(newInstallation.this);
                       // tvTotal.setTextColor( getResources().getColor(R.color.teal_700));
                        tvTotal.setTypeface(Typeface.DEFAULT_BOLD);
                        tvTotal.setText(totalStr);
                        tr.addView(tvTotal);
                        tableCotizationProd.addView(tr);





                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                     loading.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(newInstallation.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,jsonObject,successListener,errorListener);
            mRequestQueueCoProducts.add(request);

        }catch (JSONException e) {
            Toast.makeText(newInstallation.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }



    public void deleteCotizationProduct(Integer idProduct) {

        WebApi webApi=new WebApi("cotizationproducts/"+idProduct);

        String url =webApi.getFullUrl();



        JSONObject jsonObject= new JSONObject();

        final ProgressDialog loading = ProgressDialog.show(this,"Eliminando...","Espere por favor...",false,false);


        try {
           // jsonObject.put("accessToken","");

            jsonObject.put("id",idProduct);



            Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONObject product= new JSONObject();



                    Toast.makeText(newInstallation.this,"Producto Eliminado" ,Toast.LENGTH_SHORT).show();
                    Log.e("PRODUCTOSCotization ",""+response.toString());
                    loading.dismiss();




                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(newInstallation.this,"Error response "+ url + " "+error.toString(),Toast.LENGTH_LONG).show();
                    Log.e("ERRROROROROORRR",error.toString());
                    loading.dismiss();

                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,url,null,successListener,errorListener);
            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(newInstallation.this, "JSon exception", Toast.LENGTH_SHORT).show();
        }
    }


}