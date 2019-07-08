package com.example.lugares.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.lugares.R;
import com.example.lugares.datos.Lugares;
import com.example.lugares.utilidades.Constantes;

import static com.example.lugares.utilidades.listDatos.listLugares;

public class VerNotaActivity extends AppCompatActivity {

    TextView title;
    TextView description;
    ImageView imagen;
    RatingBar ratingBar;
    FloatingActionButton button;

    Lugares lugar;
    Bitmap bitmap;

    //variables para hacer zoom en la imagen
    ScaleGestureDetector scaleGestureDetector;
    float scala;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_nota);

        title = findViewById(R.id.txt_titulo);
        description = findViewById(R.id.txt_description);
        imagen = findViewById(R.id.imgLugar);
        ratingBar = findViewById(R.id.rating);
        button= findViewById(R.id.fbEdit);

        final int posicion = getIntent().getExtras().getInt(Constantes.ID);
        lugar = listLugares.get(posicion);

        final byte[] byteArray = getIntent().getByteArrayExtra(Constantes.IMAGEN);
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        title.setText(lugar.getNombre());
        description.setText(lugar.getDescripcion());
        ratingBar.setRating(lugar.getValoracion());
        if(bitmap!=null){
            imagen.setImageBitmap(bitmap);
        }else{
            imagen.setImageResource(R.drawable.mundo);
        }

        //para el zoom
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        scala = 1.0f;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VerNotaActivity.this, EditFotoActivity.class);
                i.putExtra(Constantes.IMAGEN, byteArray);
                i.putExtra(Constantes.ID, posicion);
                i.putExtra(Constantes.TIPO, Constantes.MODIFICAR);
                startActivity(i);
            }
        });
    }

    // Este método redirecciona todos los eventos touch de la actividad hacia el gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // Cuando se detecta el gesto de escala este método redimensiona la imagen
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            //establecemos un tamaño máximo y mínimo para la escala
            scala = Math.max(0.1f, Math.min(scala, 5.0f));
            scala *= scaleGestureDetector.getScaleFactor();
            imagen.setScaleX(scala);
            imagen.setScaleY(scala);
            return true;
        }
    }
}
