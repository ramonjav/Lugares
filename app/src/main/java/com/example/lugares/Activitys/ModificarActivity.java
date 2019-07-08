package com.example.lugares.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

public class ModificarActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titulo, descripcion;
    private RatingBar rating;
    private Button aceptar, cancelar;
    private ImageButton imageButton;

    private  double longitud, latitud;

    private final static int RESULT_MAP = 1;

    Lugares lugar;
    Bitmap bitmap;
    String accion;

    RequestQueue colaDePeticiones;
    Volleys volleys;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        final int posicion = getIntent().getExtras().getInt(Constantes.ID);
        lugar = listLugares.get(posicion);


        //obtenemos una referencia a la cola de peticiones
        volleys = Volleys.getInstance(this);
        colaDePeticiones = volleys.getRequestQueue();

        titulo = findViewById(R.id.editText3);
        descripcion = findViewById(R.id.editText4);
        rating = findViewById(R.id.ratingBar);
        aceptar = findViewById(R.id.btn_aceptar2);
        cancelar = findViewById(R.id.btn_aceptar);
        imageButton = findViewById(R.id.imageButton);

        titulo.setText(lugar.getNombre());
        descripcion.setText(lugar.getDescripcion());
        latitud = lugar.getLatitud();
        longitud = lugar.getLongitud();
        rating.setRating(lugar.getValoracion());

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
                if(title.isEmpty() || des.isEmpty()){
                    Toast.makeText(ModificarActivity.this, "No puede dejar campoc vacios", Toast.LENGTH_SHORT).show();
                }else{
                    guardarNota("modificarNota");
                    finish();
                }
                break;

            case R.id.btn_aceptar:
                finish();
                break;

            case R.id.imageButton:
                Intent i = new Intent(ModificarActivity.this, LocalizacionActivity.class);
                i.putExtra(Constantes.LATITUD, latitud);
                i.putExtra(Constantes.LONGITUD, longitud);
                i.putExtra(Constantes.TIPO, Constantes.MODIFICAR);
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
            LugarAux.setId(lugar.getId());
            LugarAux.setImage(lugar.getImage());

        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put(Constantes.ID, String.valueOf(LugarAux.getId()));
        parametros.put(Constantes.NOMBRE, LugarAux.getNombre());
        parametros.put(Constantes.DESCRIPCION, LugarAux.getDescripcion());
        parametros.put(Constantes.LATITUD, String.valueOf(LugarAux.getLatitud()));
        parametros.put(Constantes.LONGITUD, String.valueOf(LugarAux.getLongitud()));
        parametros.put(Constantes.RATING, String.valueOf(LugarAux.getValoracion()));
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
                            String mensaje = response.getString("mensaje");
                            if(resultado == 0){
                                    lugar.setNombre(nombre);
                                    lugar.setDescripcion(description);
                                    lugar.setLatitud(latitud);
                                    lugar.setLongitud(longuitud);
                                    lugar.setValoracion(valora);
                                finish();
                            }
                            Toast.makeText(ModificarActivity.this, mensaje, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("datos", error.toString());
                Toast.makeText(ModificarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
