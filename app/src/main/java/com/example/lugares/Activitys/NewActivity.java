package com.example.lugares.Activitys;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lugares.R;
import com.example.lugares.Volleys.Volleys;
import com.example.lugares.datos.Lugares;
import com.example.lugares.maps.LocalizacionActivity;
import com.example.lugares.utilidades.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lugares.utilidades.listDatos.listLugares;

public class NewActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titulo, descripcion;
    private RatingBar rating;
    private Button aceptar, cancelar;
    private ImageButton imageButton;

    private  double longitud, latitud;

    boolean ubicacion_seleccionada = false;

    private final static int RESULT_MAP = 1;


    RequestQueue colaDePeticiones;
    Volleys volleys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Bundle ext = getIntent().getExtras();

        //obtenemos una referencia a la cola de peticiones
        volleys = Volleys.getInstance(this);
        colaDePeticiones = volleys.getRequestQueue();

    if(ext!=null){
        longitud = ext.getDouble(Constantes.LONGITUD);
        latitud = ext.getDouble(Constantes.LATITUD);
    }
        titulo = findViewById(R.id.editText3);
        descripcion = findViewById(R.id.editText4);
        rating = findViewById(R.id.ratingBar);
        aceptar = findViewById(R.id.btn_aceptar2);
        cancelar = findViewById(R.id.btn_aceptar);
        imageButton = findViewById(R.id.imageButton);

        aceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        imageButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_aceptar2:
                String title = titulo.getText().toString();
                String des = descripcion.getText().toString();
                if(title.isEmpty() || des.isEmpty() || !ubicacion_seleccionada){
                    Toast.makeText(NewActivity.this, "No puede dejar campos vacios o no seleccionar la ubicación al guardar", Toast.LENGTH_SHORT).show();
                }else{
                    guardarNota("nuevaNota");
                    startActivity(new Intent(NewActivity.this, MainActivity.class));
                    finish();
                }
                break;

            case R.id.btn_aceptar:
                finish();
                break;

            case R.id.imageButton:
                Intent i = new Intent(NewActivity.this, LocalizacionActivity.class);
                i.putExtra(Constantes.TIPO, Constantes.NUEVO);
                startActivityForResult(i, RESULT_MAP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_MAP){
            try{
                latitud = data.getExtras().getDouble(Constantes.LATITUD);
                longitud = data.getExtras().getDouble(Constantes.LONGITUD);
                ubicacion_seleccionada = data.getExtras().getBoolean(Constantes.UBUCACION_SELECCIONADA);
            }catch (Exception e){

            }
        }
    }

    private void guardarNota(String nombreArray){

        final Lugares LugarAux;
        final String nombre = titulo.getText().toString();
        final String description = descripcion.getText().toString();
        final float valora= rating.getRating();
        final double latitud = this.latitud;
        final double longuitud = this.longitud;
        LugarAux = new Lugares(0,nombre, description, latitud, longuitud, valora, "");

        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put(Constantes.ID, String.valueOf(LugarAux.getId()));
        parametros.put(Constantes.NOMBRE, LugarAux.getNombre());
        parametros.put(Constantes.DESCRIPCION, LugarAux.getDescripcion());
        parametros.put(Constantes.LATITUD, String.valueOf(LugarAux.getLatitud()));
        parametros.put(Constantes.LONGITUD, String.valueOf(LugarAux.getLongitud()));
        parametros.put(Constantes.RATING, String.valueOf(LugarAux.getValoracion()));
        parametros.put("imagen", " ");
        parametros.put("id_usuarios", "1");

        Map<String, Map> cuerpo = new HashMap<String, Map>();
        cuerpo.put(nombreArray, parametros);
        JSONObject jsonObject = new JSONObject(cuerpo);

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                "https://apcpruebas.es/david/DB_lugares/controladores/controlNotas.php",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // array disponible
                        Log.i("Respuesta", "Respuesta del servidor correcta");
                        try {
                            int resultado = response.getInt("estado");
                            if(resultado == 0){
                                int mensaje = response.getInt("mensaje");
                                LugarAux.setId(mensaje);
                                listLugares.add(LugarAux);
                                finish();
                            }
                            Toast.makeText(NewActivity.this, "Nota agregada correctamente", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("datos", error.toString());
                Toast.makeText(NewActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
}
