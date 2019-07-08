package com.example.lugares.Activitys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.lugares.utilidades.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lugares.utilidades.listDatos.listLugares;

public class EditFotoActivity extends AppCompatActivity {
    EditText title, descrption;
    ImageView image;
    RatingBar ratingBar;
    Button btnAceptar;

    Lugares lugar;
    Bitmap bitmap;

    double latitud, longuitud;

    String accion;

    RequestQueue colaDePeticiones;
    Volleys volleys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //obtenemos una referencia a la cola de peticiones
        volleys = Volleys.getInstance(this);
        colaDePeticiones = volleys.getRequestQueue();

        title = findViewById(R.id.editTitulo);
        descrption = findViewById(R.id.EditDesc);
        image = findViewById(R.id.imgLugar2);
        ratingBar = findViewById(R.id.rating2);
        btnAceptar = findViewById(R.id.btnModificar);

        accion = getIntent().getExtras().getString(Constantes.TIPO);


        if(accion.equals(Constantes.MODIFICAR)){
            modificarVillano();
        }


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    guardarNota("modificarNota");
            }
        });

    }

    private void modificarVillano(){
        final int posicion = getIntent().getExtras().getInt(Constantes.ID);
        lugar = listLugares.get(posicion);

        final byte[] byteArray = getIntent().getByteArrayExtra(Constantes.IMAGEN);
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        title.setText(lugar.getNombre());
        descrption.setText(lugar.getDescripcion());
        ratingBar.setRating(lugar.getValoracion());
        image.setImageBitmap(bitmap);
        latitud = lugar.getLatitud();
        longuitud = lugar.getLongitud();


    }

    private void guardarNota(String nombreArray){

        final Lugares LugarAux;
        final String nombre = title.getText().toString();
        final String description = descrption.getText().toString();
        final float valora= ratingBar.getRating();
        final double latitud = this.latitud;
        final double longuitud = this.longuitud;
        LugarAux = new Lugares(0,nombre, description, latitud, longuitud, valora, "");
        if(accion.equals(Constantes.MODIFICAR)){
            LugarAux.setId(lugar.getId());
            LugarAux.setImage(lugar.getImage());
        }

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
                                if(accion.equals(Constantes.MODIFICAR)){
                                    lugar.setNombre(nombre);
                                    lugar.setDescripcion(description);
                                    lugar.setLatitud(latitud);
                                    lugar.setLongitud(longuitud);
                                    lugar.setValoracion(valora);
                                }else{
                                    //TODO:conseguir el id del nuevo villano
                                    listLugares.add(LugarAux);
                                }

                                finish();
                            }
                            Toast.makeText(EditFotoActivity.this, mensaje, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("datos", error.toString());
                Toast.makeText(EditFotoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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