package br.net.paulofernando.pessoasinspiradoras.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.model.ImportEntity;


@EViewGroup(R.layout.view_import_inspirations)
public class ImportInspirationsView extends LinearLayout {

    @ViewById(R.id.person_name_import)
    TextView personName;

    @ViewById(R.id.person_amount_inspirations_import)
    TextView amountInspirations;

    @ViewById(R.id.merge_icon)
    ImageView mergeIcon;

    @ViewById(R.id.check_import)
    CheckBox check;

    @ViewById(R.id.componentImportView)
    RelativeLayout componentImportView;

    ImportEntity importPerson;

    private Context context;

    public ImportInspirationsView(Context context) {
        super(context);
        this.context = context;
    }

    public ImportInspirationsView(ImportEntity importPerson, Context context) {
        this(context);
        this.importPerson = importPerson;
    }

    @AfterViews
    void init() {
        personName.setText(importPerson.getName());
        amountInspirations.setText(importPerson.getAmountInpirations() + (importPerson.getAmountInpirations() > 1 ? " " +
                context.getString(R.string.inspirations) : " " + context.getString(R.string.inspiration)));

        if (importPerson.isMerged()) {
            mergeIcon.setVisibility(View.VISIBLE);
        }

    }

    @Click(R.id.componentPersonView)
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
