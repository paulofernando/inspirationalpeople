package br.net.paulofernando.pessoasinspiradoras;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.faradaj.blurbehind.BlurBehind;

/**
 * Created by Paulo Fernando on 5/4/2016.
 */
public class PopupImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup_image);

        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.parseColor("#eeeeee"))
                .setBackground(this);

        ImageView personPhotoFull = (ImageView) findViewById(R.id.person_photo_full);

        byte[] photoRaw = getIntent().getByteArrayExtra("photo");
        Bitmap bmp = BitmapFactory.decodeByteArray(photoRaw, 0, photoRaw.length);
        personPhotoFull.setImageBitmap(bmp);

        /*int maxImageMeasure = (int) getResources().getDimension(R.dimen.max_image_measure);
        getWindow().setLayout(bmp.getWidth(), bmp.getHeight() + ((int) getResources().getDimension(R.dimen.popup_bar_height)));*/
    }
}
