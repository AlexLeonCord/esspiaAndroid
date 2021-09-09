package com.app.esspia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class TemplatePDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle= new Font(Font.FontFamily.TIMES_ROMAN,21,Font.BOLD);
    private Font fSubTitle= new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD);
    private Font fSubTitleTable= new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD,BaseColor.WHITE);
    //private Font IdReport= new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD);
    private Font fText= new Font(Font.FontFamily.TIMES_ROMAN,13,Font.BOLD);
    private Font fTableSubT= new Font(Font.FontFamily.TIMES_ROMAN,12,Font.BOLD,BaseColor.WHITE);
    //private Font IdReport= new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD);
    private Font fTextMedium= new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD);
    private Font fHighText= new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD, BaseColor.RED);

    String namePDFM;
    String namePDFC;
    String namePDFA;


    //TextView tv = (TextView) findViewById(R.id.CustomFontText);
   // tv.setTypeface(tf);



    public TemplatePDF(Context context) {
        this.context= context;
         this.namePDFM="";
        this.namePDFC="";
        this.namePDFA="";

    }
    public void openDocument(int itemSelectedTypeService){
        createFile(itemSelectedTypeService);
        try {
            document= new Document(PageSize.A4);
            document.setMargins(70f, 70f, 70f, 70f); // 0.5 inch margins
            pdfWriter= PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
            document.open();

        }catch (Exception e){
            Log.e("openDocument",e.toString());
        }
    }
    private void createFile(int itemSelectedTypeService){


        switch(itemSelectedTypeService) {
            case 1:
                File folderM  = new File(Environment.getExternalStorageDirectory().toString(),"ESSPDFMaintenance");
                if (!folderM.exists()){
                    folderM.mkdirs();

                }
                // pdfFile= new File(folder,"ReportePDF.pdf");
                pdfFile= new File(folderM,namePDFM);
                break;
            case 2:
                File  folderC= new File(Environment.getExternalStorageDirectory().toString(),"ESSPDFCotization");
                if (!folderC.exists()){
                    folderC.mkdirs();

                }
                // pdfFile= new File(folder,"ReportePDF.pdf");
                pdfFile= new File(folderC,namePDFC);
                break;
            case 3:
                File folderA= new File(Environment.getExternalStorageDirectory().toString(),"ESSPDFAct");
                if (!folderA.exists()){
                    folderA.mkdirs();

                }
                // pdfFile= new File(folder,"ReportePDF.pdf");
                pdfFile= new File(folderA,namePDFA);
                break;

            default:

        }



    }

    public void closeDocumment(){

        document.close();
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);

    }
    public void addTitles(String title, String subTitle, String date,String idReport){
        try {


        paragraph=new Paragraph();
        addChildP(new Paragraph(title,fTitle));
        addChildP(new Paragraph(subTitle,fSubTitle));
        addChildP(new Paragraph(date, fSubTitle ));
        addChildP(new Paragraph(idReport,fHighText));
        paragraph.setSpacingAfter(30);
        document.add(paragraph);
        }
        catch (Exception e){
            Log.e("addTitles",e.toString());
        }

    }
    private void addChildP(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);

    }
    public void addParagraph(String text){
        try {


        paragraph= new Paragraph(text, fText);
            paragraph.setSpacingBefore(5);
            paragraph.setSpacingAfter(5);

        document.add(paragraph);
        }
        catch (Exception e){
            Log.e("addParagraph",e.toString());

        }
    }

    public void addParagraphHeader(String text){
        try {


            paragraph= new Paragraph(text, fTextMedium);
            paragraph.setSpacingBefore(5);
            paragraph.setSpacingAfter(5);

            document.add(paragraph);
        }
        catch (Exception e){
            Log.e("addParagraph",e.toString());

        }
    }

    public void addParagraph2(String text){
        try {


            paragraph= new Paragraph(text, fText);

            document.add(paragraph);
        }
        catch (Exception e){
            Log.e("addParagraph",e.toString());

        }
    }

    public void addParagraphFooter(String text,Font fonf){
        try {


            Paragraph  paragraph= new Paragraph(text, fonf);
           // paragraph.setSpacingAfter(1);
           // paragraph.setSpacingBefore(1);
            document.add(paragraph);
        }
        catch (Exception e){
            Log.e("addParagraph",e.toString());

        }
    }
    public void createTable(
                            String [] header, ArrayList<String[]> products,String TituloTable,
                            String etNumberCotization,String etVigenciaCotization,String nameCompany,String etContact,String etDescServicio,
                            String phone, String address, String formaPago,String subtotalText,String subtotalSum ,
                            String tiempoEntrega,String ivaString,String ivaValue,
                            String gatantia, String totalString,String totalValue, String infoManager,
                            String observacion){

        try  {
            paragraph=new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(20);
            float[] columnWidths = new float[]{8f, 35f,13, 17f, 23f};
            pdfPTable.setWidths(columnWidths);


            PdfPCell celdaTitleTable = new PdfPCell(new Phrase(" "+TituloTable,fTitle));
            celdaTitleTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTitleTable.setColspan(5);
            //celdaTitleTable.setFixedHeight(40);

            PdfPCell celdaEmpty1 = new PdfPCell(new Phrase(" "));
            celdaEmpty1.setColspan(5);
           // celdaEmpty1.setFixedHeight(40);

            PdfPCell celdaCotiNum = new PdfPCell(new Phrase(" "+etNumberCotization, fTableSubT));
            celdaCotiNum.setColspan(2);
            celdaCotiNum.setBackgroundColor(new BaseColor(79,98,40));
            //celdaCotiNum.setFixedHeight(40);

            PdfPCell celdaVigCot = new PdfPCell(new Phrase(" "+etVigenciaCotization, fTableSubT));
            celdaVigCot.setColspan(3);
            celdaVigCot.setBackgroundColor(new BaseColor(79,98,40));
           // celdaVigCot.setFixedHeight(40);

            PdfPCell celdaEmpre = new PdfPCell(new Phrase(" "+nameCompany ));
            celdaEmpre.setColspan(2);
          //  celdaEmpre.setFixedHeight(40);

            PdfPCell celdaEmpty2 = new PdfPCell(new Phrase(" "));
            celdaEmpty2.setColspan(3);
           // celdaEmpty2.setFixedHeight(40);

            PdfPCell celdaContact = new PdfPCell(new Phrase(" "+etContact));
            celdaContact.setColspan(2);
           // celdaContact.setFixedHeight(40);

            PdfPCell celdaService = new PdfPCell(new Phrase(" "+etDescServicio,fTableSubT));
            celdaService.setColspan(3);
            celdaService.setBackgroundColor(new BaseColor(79,98,40));
          //  celdaService.setFixedHeight(40);

            PdfPCell celdaEmpty3 = new PdfPCell(new Phrase(" "+phone));
            celdaEmpty3.setColspan(2);
           // celdaEmpty3.setFixedHeight(40);

            PdfPCell celdaEmpty4 = new PdfPCell(new Phrase(" "));
            celdaEmpty4.setColspan(3);
           // celdaEmpty4.setFixedHeight(40);

            PdfPCell celdaEmpty5 = new PdfPCell(new Phrase(" "+address));
            celdaEmpty5.setColspan(2);
            // celdaEmpty3.setFixedHeight(40);

            PdfPCell celdaEmpty6 = new PdfPCell(new Phrase(" "));
            celdaEmpty6.setColspan(3);
            // celdaEmpty4.setFixedHeight(40);

            PdfPCell celdaEmpty7 = new PdfPCell(new Phrase(" "));
            celdaEmpty7.setColspan(5);
          //  celdaEmpty5.setFixedHeight(40);
            PdfPCell celdaEmpty8 = new PdfPCell(new Phrase(" "));
            celdaEmpty8.setColspan(5);
            //  celdaEmpty5.setFixedHeight(40);

            pdfPTable.addCell(celdaTitleTable);
            pdfPTable.addCell(celdaEmpty1);
            pdfPTable.addCell(celdaCotiNum);
            pdfPTable.addCell(celdaVigCot);
            pdfPTable.addCell(celdaEmpre);
            pdfPTable.addCell(celdaEmpty2);
            pdfPTable.addCell(celdaContact);
            pdfPTable.addCell(celdaService);
            pdfPTable.addCell(celdaEmpty3);
            pdfPTable.addCell(celdaEmpty4);
            pdfPTable.addCell(celdaEmpty5);
            pdfPTable.addCell(celdaEmpty6);
            pdfPTable.addCell(celdaEmpty7);
            pdfPTable.addCell(celdaEmpty8);




        PdfPCell pdfPCell;
        int indexC=0;
        while (indexC<header.length) {



            pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitleTable));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(new BaseColor(79,98,40));
            pdfPCell.setFixedHeight(40);
            pdfPTable.addCell(pdfPCell);
        }
        boolean colorGreen=false;

        for (int indexR = 0; indexR < products.size(); indexR++) {
                String[] row = products.get(indexR);
                for (indexC = 0; indexC < header.length; indexC++) {

                  // Log.e("LOGO PRODUCT ",""+row[indexC]);

                    if(indexC==0){
                        pdfPCell = new PdfPCell(new Phrase(row[indexC],fText));
                    }else{
                        pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    }


                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);


                    if (indexR%2==0){
                        pdfPCell.setBackgroundColor(new BaseColor(214,227,188));
                        colorGreen=true;
                    }else {
                        colorGreen=false;
                    }




                   // pdfPCell.setFixedHeight(40);

                    pdfPTable.addCell(pdfPCell);

                }

            }


            PdfPCell celdaFinal1 = new PdfPCell(new Phrase("\n"+formaPago,fTextMedium));
            celdaFinal1.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal1.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celdaFinal2 = new PdfPCell(new Phrase("\n"+subtotalText,fTextMedium));
            celdaFinal2.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal2.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celdaFinal3 = new PdfPCell(new Phrase("\n"+subtotalSum,fTextMedium));
            //celdaFinal2.setColspan(4);
           // celdaFinal2.setBorder(0);
            celdaFinal3.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celdaFinal4 = new PdfPCell(new Phrase("\n"+tiempoEntrega,fTextMedium));
            celdaFinal4.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal4.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celdaFinal5 = new PdfPCell(new Phrase("\n"+ivaString,fTextMedium));
            celdaFinal5.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal5.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celdaFinal6 = new PdfPCell(new Phrase("\n"+ivaValue,fTextMedium));
            //celdaFinal2.setColspan(4);
            // celdaFinal2.setBorder(0);
            celdaFinal6.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celdaFinal7 = new PdfPCell(new Phrase("\n"+gatantia,fTextMedium));
            celdaFinal7.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal7.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celdaFinal8 = new PdfPCell(new Phrase("\n"+totalString,fTextMedium));
            celdaFinal8.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal8.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celdaFinal9 = new PdfPCell(new Phrase("\n"+totalValue,fTextMedium));
            //celdaFinal2.setColspan(4);
            // celdaFinal2.setBorder(0);
            celdaFinal9.setHorizontalAlignment(Element.ALIGN_CENTER);



            PdfPCell celdaFinal10= new PdfPCell(new Phrase("\n"+infoManager,fTextMedium));
            celdaFinal10.setColspan(2);
            //celdaFinal1.setBorder(0);
            celdaFinal10.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celdaFinal11 = new PdfPCell(new Phrase("\n"+"OBSERVACIONES: "+observacion,fTextMedium));
            celdaFinal11.setColspan(3);
            //celdaFinal1.setBorder(0);
            celdaFinal11.setHorizontalAlignment(Element.ALIGN_LEFT);

            if(colorGreen){

                celdaFinal1.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal2.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal3.setBackgroundColor(new BaseColor(Color.WHITE));

                celdaFinal4.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal5.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal6.setBackgroundColor(new BaseColor(214,227,188));

                celdaFinal7.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal8.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal9.setBackgroundColor(new BaseColor(Color.WHITE));


            }else{

                celdaFinal1.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal2.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal3.setBackgroundColor(new BaseColor(214,227,188));

                celdaFinal4.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal5.setBackgroundColor(new BaseColor(Color.WHITE));
                celdaFinal6.setBackgroundColor(new BaseColor(Color.WHITE));

                celdaFinal7.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal8.setBackgroundColor(new BaseColor(214,227,188));
                celdaFinal9.setBackgroundColor(new BaseColor(214,227,188));
            }


            // Indicamos cuantas columnas ocupa la celda
            //celdaFinal.setColspan(4);

            pdfPTable.addCell(celdaFinal1);
            pdfPTable.addCell(celdaFinal2);
            pdfPTable.addCell(celdaFinal3);
            pdfPTable.addCell(celdaFinal4);
            pdfPTable.addCell(celdaFinal5);
            pdfPTable.addCell(celdaFinal6);
            pdfPTable.addCell(celdaFinal7);
            pdfPTable.addCell(celdaFinal8);
            pdfPTable.addCell(celdaFinal9);
            pdfPTable.addCell(celdaFinal10);
            pdfPTable.addCell(celdaFinal11);



            paragraph.add(pdfPTable);
            document.add(paragraph);





        }
        catch (Exception e){
                Log.e("createTable",e.toString());

            }

        }

        public void viewPDF(){
            Intent intent= new Intent(context,ViewPDF.class);
            intent.putExtra("path",pdfFile.getAbsolutePath() );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);




        }

        public void addImage(Image image) {

            try {
           document.add(image);

        }catch (Exception e){
                Log.e("ERRORRR IMAGEN ",""+e.getMessage());
            }
    }

    public PdfPCell createpdfPCell(Phrase phrase){
        PdfPCell pdfPCell= new PdfPCell(phrase);
              return  pdfPCell;
    }
    public void newPage(){
         document.newPage();
    }



    public void setNamePDFM(String namePDFM){
        if (namePDFM.isEmpty()){
            this.namePDFM="ESSPIA SECURITY SYSTEMS M.pdf";
        }else{
            this.namePDFM=namePDFM+".pdf";
        }

    }


    public String getNamePDFM() {

        return  this.namePDFM ;
    }


    public void setNamePDFC(String namePDFC){
        if (namePDFC.isEmpty()){
            this.namePDFC="ESSPIA SECURITY SYSTEMS C.pdf";
        }else{
            this.namePDFC=namePDFC+".pdf";
        }

    }


    public String getNamePDFC() {

        return  this.namePDFC ;
    }


    public void setNamePDFA(String namePDFA){
        if (namePDFA.isEmpty()){
            this.namePDFA="ESSPIA SECURITY SYSTEMS A.pdf";
        }else{
            this.namePDFA=namePDFA+".pdf";
        }

    }


    public String getNamePDFA() {

        return  this.namePDFA ;
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




}
