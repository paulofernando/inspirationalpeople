package br.net.paulofernando.pessoasinspiradoras.view.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.view.activity.EditPersonActivity;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PagerInspirationsFragment;
import br.net.paulofernando.pessoasinspiradoras.data.entity.PersonEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonView extends LinearLayout {

    @BindView(R.id.person_name)
    TextView personName;

    @BindView(R.id.person_amount_inspirations)
    TextView amountInspirations;

    @BindView(R.id.medalha)
    ImageView medalha;

    @BindView(R.id.photo)
    ImageView photo;

    @BindView(R.id.componentPersonView)
    RelativeLayout componentPersonView;

    PersonEntity person;

    private Context context;

    public PersonView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PersonView(PersonEntity person, Context context) {
        super(context);
        this.person = person;
        this.context = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_person, this); // your layout with <merge> as the root tag
        ButterKnife.bind(this);

        try {
            personName.setText(person.name);
            //personName.setText(SimpleCrypto.decrypt(Utils.key, person.name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        amountInspirations.setText(person.getAmountInpirations() + (person.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration)));
        loadPhoto();
        loadMedal();
    }

    private void loadMedal() {
        if (person.getMedal() != -1) {
            medalha.setImageDrawable(getResources().getDrawable(person.getMedal()));
            medalha.setVisibility(View.VISIBLE);
        }
    }

    void loadPhoto() {
        photo.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));
    }

    @OnClick(R.id.componentPersonView)
    void click() {
        Intent intent = new Intent(getContext(), PagerInspirationsFragment.class);
        intent.putExtra("name", person.name);
        intent.putExtra("photo", person.photo);
        intent.putExtra("id", person.id);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.photo)
    void clickPhoto() {
        Intent intent = new Intent(getContext(), EditPersonActivity.class);
        intent.putExtra("name", person.name);
        intent.putExtra("photo", person.photo);
        intent.putExtra("id", person.id);
        getContext().startActivity(intent);
    }

    public PersonEntity getPerson() {
        return person;
    }

}
