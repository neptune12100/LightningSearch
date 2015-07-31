package fwslash.torrentsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.IllegalFormatException;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Keyboard's Search button
        EditText editQuery = (EditText) findViewById(R.id.edit_query);
        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v);
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void onResume() {
        reloadSites();
        super.onResume();
    }

    private void reloadSites() {
        Spinner spinnerSite = (Spinner) findViewById(R.id.spinner_site);
        ArrayAdapter<SearchEngine> adapter = new ArrayAdapter<SearchEngine>(this,
                android.R.layout.simple_spinner_item,
                SearchEngine.getSites(this));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSite.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_edit_sites:
                startActivity(new Intent(this, CustomizeActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Called by giant Search button
    public void search(View view) {
        Spinner spinner_site = (Spinner) findViewById(R.id.spinner_site);
        SearchEngine site = (SearchEngine) spinner_site.getSelectedItem();

        EditText editText = (EditText) findViewById(R.id.edit_query);
        String query = editText.getText().toString();

        Uri searchURI = null;
        try {
            searchURI = site.getSearchUri(query);
            Intent intent = new Intent(Intent.ACTION_VIEW, searchURI);
            startActivity(intent);
        } catch (IllegalFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_format), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_invalid_uri), Toast.LENGTH_SHORT).show();
        }
    }

}
