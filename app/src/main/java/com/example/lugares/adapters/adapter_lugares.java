package com.example.lugares.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lugares.Activitys.ModificarActivity;
import com.example.lugares.Activitys.VerNotaActivity;
import com.example.lugares.R;
import com.example.lugares.Volleys.Volleys;
import com.example.lugares.datos.Lugares;
import com.example.lugares.maps.MapsActivity;
import com.example.lugares.utilidades.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class adapter_lugares extends ArrayAdapter<Lugares> {

    private ArrayList<Lugares> listLugares;
    private Context mcontext;

    private RequestQueue colaDePeticiones;
    private Volleys volleys;

    private static final String URL_BASE = "https://apcpruebas.es/david/media/";

    public adapter_lugares(Context context, ArrayList<Lugares> listLugares) {
        super(context, R.layout.activity_main, listLugares);

        this.listLugares = listLugares;
        this.mcontext = context;

        volleys = Volleys.getInstance(context);
        colaDePeticiones = volleys.getRequestQueue();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Lugares lugares = getItem(position);

        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.lugares_item, parent, false);
        }

        ImageButton btn_editar = convertView.findViewById(R.id.btnEditar);
        ImageButton btn_eliminar = convertView.findViewById(R.id.btnEliminar);
        ImageButton btn_ir = convertView.findViewById(R.id.btn_ir);
        final TextView txt_titulo = convertView.findViewById(R.id.txt_titulo);
        final TextView txt_descripcion = convertView.findViewById(R.id.txt_description);
        final RatingBar rating = convertView.findViewById(R.id.rating);
        final ImageView imgLugares = convertView.findViewById(R.id.imgLugar);

        // Petición para obtener la imagen
        ImageRequest request = new ImageRequest(
                URL_BASE + listLugares.get(position).getImage(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                       imgLugares.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                       imgLugares.setImageResource(R.drawable.mundo);
                        Log.d("Adapter", "Error en respuesta Bitmap: "+ error.getMessage());
                    }
                });

        // Añadir petición a la cola
        colaDePeticiones.add(request);


         txt_titulo.setText(lugares.getNombre());
         txt_descripcion.setText(lugares.getDescripcion());
         rating.setRating(lugares.getValoracion());
         final double latitud = lugares.getLatitud();
         final double longitud = lugares.getLongitud();

         btn_editar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(mcontext, "Aprenda a vivir con sus errores compa", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(mcontext, ModificarActivity.class);
                 intent.putExtra(Constantes.ID, position);
                 mcontext.startActivity(intent);
             }
         });

         btn_eliminar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 createsimpledialog(position).show();
             }
         });


         btn_ir.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mcontext, MapsActivity.class);
                 intent.putExtra(Constantes.LONGITUD, longitud);
                 intent.putExtra(Constantes.LATITUD, latitud);
                 intent.putExtra(Constantes.NOMBRE, txt_titulo.getText().toString());
                 intent.putExtra(Constantes.DESCRIPCION, txt_descripcion.getText().toString());
                 mcontext.startActivity(intent);
             }
         });

         rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
             @Override
             public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                 lugares.setValoracion(ratingBar.getRating());
             }
         });

         imgLugares.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mcontext, VerNotaActivity.class);
                 intent.putExtra(Constantes.ID, position);

                 ImageView imageView = (ImageView)v;
                 Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                 ByteArrayOutputStream btStream = new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, btStream);
                 byte[] byteArray = btStream.toByteArray();

                 intent.putExtra(Constantes.IMAGEN, byteArray);
                 mcontext.startActivity(intent);

             }
         });

        return convertView;
    }

    public AlertDialog createsimpledialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("Eliminando Nota")
                .setMessage("¿Seguro que quieres eliminar esta nota")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarVillano(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: cancelar operacion
                    }
                });

        return builder.create();
    }

    private void eliminarVillano(final int position){

        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put("id", String.valueOf(listLugares.get(position).getId()));

        Map<String, Map> cuerpo = new HashMap<String, Map>();
        cuerpo.put("eliminarNota", parametros);
        JSONObject jsonObject = new JSONObject(cuerpo);


        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                "https://apcpruebas.es/david/DB_lugares/controladores/controlNotas.php",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // array disponible

                        try {
                            int resultado = response.getInt("estado");
                            String mensaje = response.getString("mensaje");
                            if(resultado == 0){
                                //eliminar el villano de la lista
                                listLugares.remove(position);
                                notifyDataSetChanged();
                            }

                            Toast.makeText(mcontext, mensaje, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("datos", error.toString());
                Toast.makeText(mcontext, error.toString(), Toast.LENGTH_LONG).show();
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
