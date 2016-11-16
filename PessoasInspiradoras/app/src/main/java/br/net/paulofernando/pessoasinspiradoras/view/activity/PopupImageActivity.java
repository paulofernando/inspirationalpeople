package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.faradaj.blurbehind.BlurBehind;

import br.net.paulofernando.pessoasinspiradoras.R;

public class PopupImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup_image);

        BlurBehind.getInstance()
                .withAlpha(100)
                .withFilterColor(ContextCompat.getColor(this.getApplicationContext(), R.color.primary_dark))
                .setBackground(this);

        ImageView personPhotoFull = (ImageView) findViewById(R.id.person_photo_full);

        byte[] photoRaw = getIntent().getByteArrayExtra("photo");
        Bitmap bmp = BitmapFactory.decodeByteArray(photoRaw, 0, photoRaw.length);
        personPhotoFull.setImageBitmap(bmp);

        ((ImageView) findViewById(R.id.closePopup)).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PopupImageActivity.this.onBackPressed();
                    }
                }
        );

    }
}
