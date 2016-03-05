package br.net.paulofernando.pessoasinspiradoras.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.net.paulofernando.pessoasinspiradoras.PersonActivity_;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.fragment.PagerInspirations;
import br.net.paulofernando.pessoasinspiradoras.model.PersonEntity;

@EViewGroup(R.layout.view_person)
public class PersonView extends LinearLayout {

    @ViewById
    TextView personName;

    @ViewById(R.id.person_amount_inspirations)
    TextView amountInspirations;

    @ViewById(R.id.medalha)
    ImageView medalha;

    @ViewById
    ImageView photo;

    @ViewById
    RelativeLayout componentPersonView;

    PersonEntity person;

    private Context context;

    public PersonView(Context context) {
        super(context);
        this.context = context;
    }

    public PersonView(PersonEntity person, Context context) {
        this(context);
        this.person = person;
    }

    @AfterViews
    void init() {
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

    @Click(R.id.componentPersonView)
    void click() {
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            Intent intent = new Intent(getContext(), PagerInspirations.class);
            intent.putExtra("name", person.name);
            intent.putExtra("photo", person.photo);
            intent.putExtra("id", person.id);
            getContext().startActivity(intent);
        /*} else {
            Intent intent = new Intent(getContext(), PersonActivity_.class);
            intent.putExtra("name", person.name);
            intent.putExtra("photo", person.photo);
            intent.putExtra("id", person.id);
            getContext().startActivity(intent);
        }*/
    }

    public PersonEntity getPerson() {
        return person;
    }

}