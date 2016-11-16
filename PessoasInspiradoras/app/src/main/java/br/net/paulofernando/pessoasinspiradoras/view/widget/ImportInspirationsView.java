package br.net.paulofernando.pessoasinspiradoras.view.widget;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.data.entity.ImportEntity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ImportInspirationsView extends LinearLayout {

    @BindView(R.id.person_name_import)
    TextView personName;

    @BindView(R.id.person_amount_inspirations_import)
    TextView amountInspirations;

    @BindView(R.id.merge_icon)
    ImageView mergeIcon;

    @BindView(R.id.check_import)
    CheckBox check;

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
        inflate(getContext(), R.layout.view_import_inspirations, this); // your layout with <merge> as the root tag
        ButterKnife.bind(this);

        personName.setText(importPerson.getName());
        amountInspirations.setText(importPerson.getAmountInpirations() + (importPerson.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration)));

        if (importPerson.isMerged()) {
            mergeIcon.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.check_import)
    void click() {
        check.setChecked(!check.isChecked());
    }

    public boolean isChecked() {
        return check.isChecked();
    }

    public ImportEntity getImportPerson() {
        return importPerson;
    }

}
