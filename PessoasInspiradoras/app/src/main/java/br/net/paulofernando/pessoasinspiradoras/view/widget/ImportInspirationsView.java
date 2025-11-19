package br.net.paulofernando.pessoasinspiradoras.view.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.ImportEntity;
import br.net.paulofernando.pessoasinspiradoras.databinding.ViewImportInspirationsBinding;


public class ImportInspirationsView extends LinearLayout {

    private ViewImportInspirationsBinding binding;

    ImportEntity importPerson;

    private Context context;

    public ImportInspirationsView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ImportInspirationsView(ImportEntity importPerson, Context context) {
        super(context);
        this.context = context;
        this.importPerson = importPerson;
        init();
    }

    private void init() {
        binding = ViewImportInspirationsBinding.inflate(LayoutInflater.from(getContext()), this, true);

        binding.personNameImport.setText(importPerson.getName());
        binding.personAmountInspirationsImport.setText(importPerson.getAmountInpirations() + (importPerson.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration)));

        if (importPerson.isMerged()) {
            binding.mergeIcon.setVisibility(View.VISIBLE);
        }

        binding.importItem.setOnClickListener(v -> click());
    }

    void click() {
        binding.checkImport.setChecked(!binding.checkImport.isChecked());
    }

    public boolean isChecked() {
        return binding.checkImport.isChecked();
    }

    public ImportEntity getImportPerson() {
        return importPerson;
    }

}
