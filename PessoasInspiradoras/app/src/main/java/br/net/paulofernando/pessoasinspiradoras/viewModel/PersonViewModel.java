package br.net.paulofernando.pessoasinspiradoras.viewModel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.dao.DatabaseHelper;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.ItemPersonBinding;
import br.net.paulofernando.pessoasinspiradoras.view.activity.EditPersonActivity;
import br.net.paulofernando.pessoasinspiradoras.view.fragment.PagerInspirationsFragment;
import butterknife.OnClick;

import static java.security.AccessController.getContext;

public class PersonViewModel extends BaseObservable {

    private Context context;
    private Person person;
    private ItemPersonBinding binding;

    public ObservableField<Drawable> personPhoto;

    public PersonViewModel(Context context, Person person, ItemPersonBinding binding) {
        this.context = context;
        this.person = person;
        this.binding = binding;

        personPhoto = new ObservableField<>();
        updateInspirations(person);
    }

    public Drawable getPhoto() {
        return new BitmapDrawable(context.getResources(),BitmapFactory.decodeByteArray(person.photo, 0, person.photo.length));
    }

    public String getPersonName() {
        return person.name;
    }

    public String getAmountInspirations() {
        return person.getAmountInpirations() + (person.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration));
    }


    public int getMedalVisibility() {
        if(person.getMedal() != -1){
            return View.VISIBLE;
        }
        return  View.GONE;
    }

    public Drawable getMedal() {
        if(person.getMedal() != -1) {
            return context.getResources().getDrawable(person.getMedal());
        }
        return null;
    }

    public void updateInspirations(Person _person) {
        DatabaseHelper helper = new DatabaseHelper(context);
        _person.setAmountInpirations(helper.getInspirationData(_person.id).size());
        helper.close();
    }

    public View.OnClickListener onClickPerson() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPersonDetailsActivity();
            }
        };
    }

    public View.OnClickListener onClickPhoto() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPhotoActivity();
            }
        };
    }

    private void launchPersonDetailsActivity() {
        Intent intent = PagerInspirationsFragment.getStartIntent(context, person, binding.photo);

        /*String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());*/
        context.startActivity(intent);
    }

    private void launchPhotoActivity() {
        Intent intent = EditPersonActivity.getStartIntent(context, person, binding.photo);

        /*String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());*/
        context.startActivity(intent);
    }
}
