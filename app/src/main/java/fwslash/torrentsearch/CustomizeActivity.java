package fwslash.torrentsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;


public class CustomizeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<SearchEngine> sites;
    private ArrayAdapter<SearchEngine> adapter;
    private Spinner spinner;
    private SearchEngine currentSite;
    private AtomicBoolean switching = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        spinner = (Spinner) findViewById(R.id.spinner_site_customize);
        spinner.setOnItemSelectedListener(this);

        EditText editName, editUri;
        editName = (EditText) findViewById(R.id.edit_name);
        editUri = (EditText) findViewById(R.id.edit_uri);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!switching.get()) {
                    currentSite.setName(s.toString());
                    TextView currentView = (TextView) spinner.getSelectedView();
                    if (currentView != null) {
                        currentView.setText(s.toString());
                    }
                }
            }
        });
        editUri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!switching.get()) {
                    currentSite.setUriFormat(s.toString());
                }
            }
        });


        reloadSites();
        setEditTexts();

    }

    public void onPause() {
        saveSites(null);
        super.onPause();
    }

    private void setEditTexts() {
        EditText editName, editUri;
        editName = (EditText) findViewById(R.id.edit_name);
        editUri = (EditText) findViewById(R.id.edit_uri);
        switching.set(true);
        editName.setText(currentSite.getName());
        editUri.setText(currentSite.getUriFormat());
        switching.set(false);
    }

    private void reloadSites() {
        SearchEngine[] sitesArray = SearchEngine.getSites(this);
        sites = new ArrayList<SearchEngine>(sitesArray.length);
        Collections.addAll(sites, sitesArray);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                sites);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerSite = (Spinner) findViewById(R.id.spinner_site_customize);
        spinnerSite.setAdapter(adapter);
        currentSite = (SearchEngine) spinnerSite.getSelectedItem();
    }

    public void deleteSite(View view) {
        adapter.remove(currentSite);
        setEditTexts();
    }

    public void newSite(View view) {
        SearchEngine site = new SearchEngine();
        adapter.add(site);
        spinner.setSelection(adapter.getCount() - 1);
    }

    public void saveSites(View view) {
        SearchEngine.saveSites(sites.toArray(new SearchEngine[sites.size()]), this);
        Toast toast = Toast.makeText(this, R.string.sites_saved, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSite = (SearchEngine) parent.getSelectedItem();
        setEditTexts();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        newSite(null);
    }
}
