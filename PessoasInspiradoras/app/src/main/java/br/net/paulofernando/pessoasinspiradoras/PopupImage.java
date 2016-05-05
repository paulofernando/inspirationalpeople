package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Paulo Fernando on 5/4/2016.
 */
public class PopupImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup_image);
        ImageView personPhotoFull = (ImageView) findViewById(R.id.person_photo_full);

        byte[] photoRaw = getIntent().getByteArrayExtra("photo");
        Bitmap bmp = BitmapFactory.decodeByteArray(photoRaw, 0, photoRaw.length);
        Log.i("METRICS", String.valueOf(bmp.getWidth()));
        personPhotoFull.setImageBitmap(bmp);

        //getWindow().setLayout(512, 512);
    }
}
