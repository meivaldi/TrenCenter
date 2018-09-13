package com.meivaldi.trencenter.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.activity.pendukung.Pendukung;
import com.meivaldi.trencenter.activity.relawan.MainActivity;
import com.meivaldi.trencenter.activity.super_admin.Dashboard_SuperAdmin;
import com.meivaldi.trencenter.activity.tim_pemenangan.Tim_Pemenangan;
import com.meivaldi.trencenter.adapter.LogistikAdapter;
import com.meivaldi.trencenter.adapter.ProgramAdapter;
import com.meivaldi.trencenter.app.AppConfig;
import com.meivaldi.trencenter.helper.HttpHandler;
import com.meivaldi.trencenter.helper.SQLiteHandler;
import com.meivaldi.trencenter.model.Logistik;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LogistikActivity extends AppCompatActivity {

    private ListView listView;
    private LogistikAdapter adapter;
    private ArrayList<Logistik> logistikList;

    private Toolbar toolbar;
    private SQLiteHandler db;

    private String tipe;
    private static final String TAG = LogistikActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistik);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.logistik_list);
        logistikList = new ArrayList<>();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        tipe = user.get("type");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tipe.equals("super_admin")){
                    Intent intent = new Intent(getApplicationContext(),
                            Dashboard_SuperAdmin.class);
                    startActivity(intent);
                } else if(tipe.equals("relawan")){
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intent);
                } else if(tipe.equals("pendukung")){
                    Intent intent = new Intent(getApplicationContext(),
                            Pendukung.class);
                    startActivity(intent);
                } else if(tipe.equals("tim_pemenangan")) {
                    Intent intent = new Intent(getApplicationContext(),
                            Tim_Pemenangan.class);
                    startActivity(intent);
                }
            }
        });

        new GetLogistic().execute();
    }

    private class GetLogistic extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(AppConfig.URL_GET_LOGISTIC);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray programs = jsonObj.getJSONArray("logistik");

                    for (int i = 0; i < programs.length(); i++) {
                        JSONArray program = programs.getJSONArray(i);

                        String nama = program.getString(1);
                        String tanggalMulai = program.getString(2);
                        String lokasi = program.getString(4);
                        String foto = program.getString(7);

                        logistikList.add(new Logistik(nama, tanggalMulai, lokasi, foto));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new LogistikAdapter(getApplicationContext(), logistikList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), DetailLogistik.class);
                    intent.putExtra("INDEX", i);
                    startActivity(intent);

                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(tipe.equals("super_admin")){
            Intent intent = new Intent(getApplicationContext(),
                    Dashboard_SuperAdmin.class);
            startActivity(intent);
        } else if(tipe.equals("relawan")){
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(intent);
        } else if(tipe.equals("pendukung")){
            Intent intent = new Intent(getApplicationContext(),
                    Pendukung.class);
            startActivity(intent);
        } else if(tipe.equals("tim_pemenangan")) {
            Intent intent = new Intent(getApplicationContext(),
                    Tim_Pemenangan.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
