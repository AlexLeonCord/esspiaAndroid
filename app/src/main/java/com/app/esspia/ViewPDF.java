package com.app.esspia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPDF extends AppCompatActivity {
    private PDFView pdfView;
    File file;

    String correo, asunto, mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView=  findViewById(R.id.pdfView);

        // Obtengo los elementos de la interface
        correo = "alexcord47@gmail.com";
        asunto = "Prueba de correo";
        mensaje ="Esto es una prueba";




        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // tableCotizationProd.removeAllViews();
                        Bundle bundle= getIntent().getExtras();
                        if (bundle!=null){
                            file=new File(bundle.getString("path",""));
                        }
                        pdfView.fromFile(file)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .enableAntialiasing(true)
                                .load();
                    }
                }, 1000);

      //  SendEmail();

    }




        public void SendEmail( )
        {


            // Defino mi Intent y hago uso del objeto ACTION_SEND
            Intent intent = new Intent(Intent.ACTION_SEND);

            // Defino los Strings Email, Asunto y Mensaje con la función putExtra
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] { correo });
            intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
            intent.putExtra(Intent.EXTRA_TEXT, mensaje);

            File root= Environment.getExternalStorageDirectory();
            String filelocation= root.getAbsolutePath() + "/"+ "PDF"+ "/" + "TemplatePDF.pdf";
            Log.e("URI ",""+filelocation);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));


            // Establezco el tipo de Intent
            intent.setType("message/rfc822");

            // Lanzo el selector de cliente de Correo
            startActivity(
                    Intent
                            .createChooser(intent,
                                    "Elije un cliente de Correo:"));
        }
    }
