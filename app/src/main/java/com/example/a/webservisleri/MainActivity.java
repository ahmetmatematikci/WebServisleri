package com.example.a.webservisleri;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        liste = (ListView)findViewById(R.id.listView);

       // WebServisIleListeyiDoldur();

        WebServisiAynTask task = new WebServisiAynTask(this);
        task.execute("http://www.tcmb.gov.tr/kurlar/201705/22052017.xml");
    }


    private void WebServisIleListeyiDoldur() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String doviz_url = "http://www.tcmb.gov.tr/kurlar/201705/22052017.xml";

        List<String> doviz_list =new ArrayList<String>();
        HttpURLConnection baglanti= null;

        try{
            URL url = new URL(doviz_url);
            baglanti = (HttpURLConnection)url.openConnection();
            int baglantiDurumu = baglanti.getResponseCode();
            if (baglantiDurumu ==HttpURLConnection.HTTP_OK){
                BufferedInputStream stream = new BufferedInputStream(baglanti.getInputStream());
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
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (baglanti !=null) baglanti.disconnect();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, doviz_list );
        liste.setAdapter(adapter);


    }
}
