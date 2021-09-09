package com.app.esspia.model.api;

 import static androidx.core.content.ContextCompat.startActivity;

 import android.app.Application;
 import android.content.Context;
 import android.content.Intent;
 import android.widget.Toast;

 import androidx.activity.result.contract.ActivityResultContracts;
 import androidx.appcompat.app.AppCompatActivity;

 import com.android.volley.Request;
 import com.android.volley.RequestQueue;
 import com.android.volley.Response;
 import com.android.volley.VolleyError;
 import com.android.volley.toolbox.JsonObjectRequest;
 import com.android.volley.toolbox.Volley;
 import com.app.esspia.MainActivity;
 import com.app.esspia.Welcome;
 import com.app.esspia.model.Client;

 import org.json.JSONException;
 import org.json.JSONObject;

public class WebApi     {

    public static final String BASE_URL= "http://192.168.1.200:3030/";
    private String fullUrl ;



    public WebApi(String url) {
          this.fullUrl=BASE_URL+url;
    }

    public String getFullUrl() {
        return fullUrl;
    }
}
