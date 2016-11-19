package br.net.paulofernando.pessoasinspiradoras.viewModel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.view.View;

import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.ItemPersonBinding;

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
    }

    public String getPersonName() {
        return person.name;
    }

    public void setPersonName(String personName) {
        person.name = personName;
    }

    public View.OnClickListener onClickPerson() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPersonDetailsActivity();
            }
        };
    }

    private void launchPersonDetailsActivity() {
        /*Intent intent = MovieDetailsActivity.getStartIntent(context, movie, binding.coverIv);

        String transitionName = context.getString(R.string.cover_name);
        ActivityOptions transitionActivityOptions = ActivityOptions.
                makeSceneTransitionAnimation((Activity) context, binding.coverIv, transitionName);

        context.startActivity(intent, transitionActivityOptions.toBundle());*/
    }
}
