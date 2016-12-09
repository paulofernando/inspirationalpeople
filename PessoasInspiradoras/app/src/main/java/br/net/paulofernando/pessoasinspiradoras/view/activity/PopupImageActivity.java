package br.net.paulofernando.pessoasinspiradoras.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.faradaj.blurbehind.BlurBehind;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PagerInspirationsFragment;
import butterknife.ButterKnife;

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

        Bundle extras = getIntent().getExtras();
        Person person = extras.getParcelable(getResources().getString(R.string.person_details));

        byte[] photoRaw = person.photo;
        Bitmap bmp = BitmapFactory.decodeByteArray(photoRaw, 0, photoRaw.length);
        personPhotoFull.setImageBitmap(bmp);

        (findViewById(R.id.closePopup)).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PopupImageActivity.this.onBackPressed();
                    }
                }
        );

    }

    public static Intent getStartIntent(Context context, Person person) {
        Intent intent = new Intent(context, PopupImageActivity.class);

        Bundle extras = new Bundle();
        extras.putParcelable(context.getResources().getString(R.string.person_details), person);
        intent.putExtras(extras);

        return intent;
    }
}
