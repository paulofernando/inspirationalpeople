package br.net.paulofernando.pessoasinspiradoras.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.Person;
import br.net.paulofernando.pessoasinspiradoras.databinding.ItemPersonBinding;
import br.net.paulofernando.pessoasinspiradoras.viewModel.PersonViewModel;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.BindingHolder> {

    private List<Person> mPeople;
    private Context mContext;

    public PersonAdapter(Context context) {
        mContext = context;
        mPeople = new ArrayList<>();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPersonBinding movieBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_person,
                parent,
                false);
        return new BindingHolder(movieBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        ItemPersonBinding personBinding = holder.binding;
        personBinding.setViewModel(new PersonViewModel(mContext, mPeople.get(position), personBinding));
    }

    @Override
    public int getItemCount() {
        return mPeople.size();
    }

    public void setItems(List<Person> people) {
        mPeople = people;
        notifyDataSetChanged();
    }

    public void addItem(Person person) {
        mPeople.add(person);
        notifyItemInserted(1);
    }

    public void addList(List people){
        mPeople.addAll(people);
        notifyItemInserted(people.size());
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemPersonBinding binding;

        public BindingHolder(ItemPersonBinding binding) {
            super(binding.personContainer);
            this.binding = binding;
        }
    }
}
