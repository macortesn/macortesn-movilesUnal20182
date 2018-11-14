package macortesn.swenterprisemanagersystem;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import macortesn.swenterprisemanagersystem.DB.EnterpriseOperations;
import macortesn.swenterprisemanagersystem.Model.Enterprise;

public class ViewAllEnterprises extends ListActivity{

    private EnterpriseOperations enterpriseOps;
    private List<Enterprise> enterprises;
    private ArrayAdapter<Enterprise> adapter;
    private EditText filter;
    private CheckBox fliterConsulting, filterDevelopment, filterFactory;
    private String stringFilter;



    private static final String TAG = "ViewAllEnterprises";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_enterprises);


        filter = (EditText) findViewById(R.id.edit_text_filter_name);
        fliterConsulting = (CheckBox) findViewById(R.id.check_box_consulting);
        filterDevelopment = (CheckBox) findViewById(R.id.check_box_development);
        filterFactory = (CheckBox) findViewById(R.id.check_box_factory);

        stringFilter ="";

        enterpriseOps = new EnterpriseOperations(this);
        enterpriseOps.open();
        enterprises = enterpriseOps.getAllEnterprises();
        enterpriseOps.close();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, enterprises);
        setListAdapter(adapter);


        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stringFilter = (String) s.toString();
                (ViewAllEnterprises.this).adapter.getFilter().filter(stringFilter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.check_box_consulting:
                if (checked) {
                    (ViewAllEnterprises.this).adapter.getFilter().filter("Consulting");
                    Log.d(TAG, "onCheckboxClicked: Consulting is checked");
                }
                else {
                    (ViewAllEnterprises.this).adapter.getFilter().filter("");
                    Log.d(TAG, "onCheckboxClicked: Consulting is unchecked");
                }
                // Remove the meat
                break;
            case R.id.check_box_development:
                if (checked) {
                    (ViewAllEnterprises.this).adapter.getFilter().filter("Customdevelopment");
                    Log.d(TAG, "onCheckboxClicked: Develop is checked");
                }
                else
                    (ViewAllEnterprises.this).adapter.getFilter().filter("");
                break;
            case R.id.check_box_factory:
                if (checked)
                    (ViewAllEnterprises.this).adapter.getFilter().filter("Softwarefactory");
                else
                    (ViewAllEnterprises.this).adapter.getFilter().filter("");
                break;
        }
    }
}