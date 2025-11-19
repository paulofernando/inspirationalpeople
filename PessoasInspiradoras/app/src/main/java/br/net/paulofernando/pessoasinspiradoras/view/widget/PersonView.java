package br.net.paulofernando.pessoasinspiradoras.view.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.ItemPersonBinding;
import br.net.paulofernando.pessoasinspiradoras.view.activity.EditPersonActivity;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PagerInspirationsFragment;

public class PersonView extends LinearLayout {

    private ItemPersonBinding binding;

    Person person;

    private Context context;

    public PersonView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PersonView(Person person, Context context) {
        super(context);
        this.person = person;
        this.context = context;
        init();
    }

    private void init() {
        binding = ItemPersonBinding.inflate(LayoutInflater.from(getContext()), this, true);

        try {
            binding.personName.setText(person.name);
            //personName.setText(SimpleCrypto.decrypt(Utils.key, person.name));
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateAmountInspirations();
        loadPhoto();
        loadMedal();

        binding.personContainer.setOnClickListener(v -> click());
        binding.photo.setOnClickListener(v -> clickPhoto());
    }

    public void updateAmountInspirations() {
        binding.personAmountInspirations.setText(person.getAmountInpirations() + (person.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration)));
    }

    private void loadMedal() {
        if (person.getMedal() != -1) {
            binding.medal.setImageDrawable(ContextCompat.getDrawable(this.getContext(), person.getMedal()));
            binding.medal.setVisibility(View.VISIBLE);
        }
    }

    void loadPhoto() {
        binding.photo.setImageBitmap(BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));
    }

    void click() {
        Intent intent = new Intent(getContext(), PagerInspirationsFragment.class);
        intent.putExtra("name", person.name);
        intent.putExtra("photo", person.photo);
        intent.putExtra("id", person.id);
        getContext().startActivity(intent);
    }

    void clickPhoto() {
        Intent intent = EditPersonActivity.getStartIntent(getContext(), person);
        getContext().startActivity(intent);
    }

    public Person getPerson() {
        return person;
    }

}
