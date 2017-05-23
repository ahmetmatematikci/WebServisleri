package com.example.a.webservisleri;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by a on 5/23/17.
 */

class WebServisiAynTask  extends AsyncTask<String, String, List<String>>{

    private Context context;
    private ListView liste;
    private ProgressDialog progressDialog;

    public WebServisiAynTask(Context context) {
        this.context = context;

        liste = (ListView)((AppCompatActivity)context).findViewById(R.id.listView);
    }

    @Override
    protected void onPreExecute() {
      //  super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Lütfen Bekleyin..", "İşlem Yürütülüyor", true);

    }

    @Override
    protected List<String> doInBackground(String... params) {

        List<String> doviz_list =new ArrayList<String>();
        HttpURLConnection baglanti= null;

        try{
            URL url = new URL(params[0]);
            baglanti = (HttpURLConnection)url.openConnection();
            int baglantiDurumu = baglanti.getResponseCode();
            if (baglantiDurumu ==HttpURLConnection.HTTP_OK){
                BufferedInputStream stream = new BufferedInputStream(baglanti.getInputStream());
                publishProgress("Doviz Kurları Okunuyor....");
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(stream);

                NodeList dovizNodeList = document.getElementsByTagName("Currency");
                for (int i =0; i<dovizNodeList.getLength(); i++) {
                    Element element = (Element)dovizNodeList.item(i);

                    NodeList nodeListBirim = element.getElementsByTagName("Unit");
                    NodeList nodeListParaBirimi = element.getElementsByTagName("Isim");
                    NodeList nodeListAlis = element.getElementsByTagName("ForexBuying");
                    NodeList nodeListSatis = element.getElementsByTagName("ForexSelling");

                    String birim = nodeListBirim.item(0).getFirstChild().getNodeValue();
                    String paraBirimi = nodeListParaBirimi.item(0).getFirstChild().getNodeValue();
                    String alis = nodeListAlis.item(0).getFirstChild().getNodeValue();
                    String satis = nodeListSatis.item(0).getFirstChild().getNodeValue();

                    doviz_list.add(birim + " " + paraBirimi + " " + "Alış: " + alis  + "Satış : " + satis);


                }
                publishProgress("Şiste Güncelleniyor.....");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (baglanti !=null) baglanti.disconnect();
        }

         return doviz_list;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        //super.onProgressUpdate(values);

        progressDialog.setMessage(values[0]);

    }

    @Override
    protected void onPostExecute(List<String> strings) {
        //super.onPostExecute(strings);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, strings);
        liste.setAdapter(adapter);
        progressDialog.cancel();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
