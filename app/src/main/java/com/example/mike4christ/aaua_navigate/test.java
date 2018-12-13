package com.example.mike4christ.aaua_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class test extends AppCompatActivity {
    private TextView test;
    JSONObject object;
    ArrayList<Model> world;
    ArrayList<String> worldlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.test);
        test=findViewById(R.id.test_json);
        JSONObject data=JSONConnect.getJSONfromURL("https://aauanav.000webhostapp.com/testing-file.php");
        try {

            JSONArray jArray = data.getJSONArray("data");
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    object = jArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Model worldpop = new Model();

                worldpop.setPlace(object.optString("places"));
                worldpop.setmy_lng(object.optDouble("longitude"));
                worldpop.setmy_lat(object.optDouble("latitude"));

                world.add(worldpop);

                // Populate spinner with country names
                worldlist.add(object.optString("places"));

            }

        }catch (JSONException e){
            e.printStackTrace();
        }





    }

    private String readFile()
    {
        String myData = "";
        File myExternalFile = new File("C:\\Users\\Mike4Christ\\Documents\\Aaua_Navigate\\app\\src\\main\\java\\com\\example\\mike4christ\\aaua_navigate\\json_data.json");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myData;
    }







    private String  readFile(String filepath)throws IOException {

        BufferedReader br=new BufferedReader(new FileReader(filepath));

        try {
            StringBuilder sb=new StringBuilder();
            String line=br.readLine();
            while (line!=null){
                sb.append(line);
                sb.append("\n");
                line=br.readLine();

            }
            return  sb.toString();

        } finally {
            br.close();
        }

    }
}


