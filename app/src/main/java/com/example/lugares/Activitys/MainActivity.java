package com.example.lugares.Activitys;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lugares.R;
import com.example.lugares.Volleys.Volleys;
import com.example.lugares.adapters.adapter_lugares;
import com.example.lugares.datos.Lugares;
import com.example.lugares.utilidades.listDatos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lugares.utilidades.listDatos.listLugares;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lista;
    private adapter_lugares adapter;
    private FloatingActionButton btn;

    RequestQueue colaDePeticiones;
    Volleys volleys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista = findViewById(R.id.lista);
        btn = findViewById(R.id.floatingActionButton2);

        volleys = Volleys.getInstance(this);
        colaDePeticiones = volleys.getRequestQueue();

        if(listLugares.size() == 0){
            getDatos();
        }

        adapter = new adapter_lugares(this, listLugares);
        lista.setAdapter(adapter);
        btn.setOnClickListener(this);
    }

    void getDatos() {
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET,
                "https://apcpruebas.es/david/DB_lugares/controladores/controlNotas.php",

                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // array disponible

                        Log.d("Debug",response.toString());

                        try {
                            JSONArray array = response.getJSONArray("mensaje");
                            getListaDatos(array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("datos", error.toString());
            }

        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Auto", "68439CAE4835D7D3D7AD94F79924BA93");
                return headers;
            }
        };

        colaDePeticiones.add(peticion);
    }

    public void getListaDatos(JSONArray jsonArray) throws JSONException {

        listLugares= listDatos.getListLugares();

        for(int i = 0 ; i < jsonArray.length() ; i++){
            JSONObject lugar = jsonArray.getJSONObject(i);
            int id = lugar.getInt("id");
            String nombre = lugar.getString("nombre");
            String descripcion = lugar.getString("descripcion");
            double latitud = lugar.getDouble("latitud");
            double longuitud = lugar.getDouble("longitud");
            float valoracion =(float) lugar.getDouble("valoracion");
            String image = lugar.getString("imagen");
            listLugares.add(new Lugares(id, nombre, descripcion, latitud, longuitud,valoracion,image));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, NewActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
