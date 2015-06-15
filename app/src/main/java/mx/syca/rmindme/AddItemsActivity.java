package mx.syca.rmindme;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;


public class AddItemsActivity extends Activity {

    private AsyncTask<Void,Void,String> readFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        readItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readItems(){

        readFileTask =  new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                return  getItemsFromFile();
            }

            @Override
            protected void onPostExecute(String s) {
                displayItems(s);
            }
        }.execute();


    }

    public void onClickSaveItems(View view){

        String items = ((EditText) findViewById(R.id.panel_text_items)).getText().toString();

        saveItemsInFile(items);

        Toast.makeText(this,getString(R.string.items_saved),Toast.LENGTH_SHORT).show();

        finish();

    }

    private void saveItemsInFile(String items ){
        try
        {

            FileOutputStream outputStream = openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            outputStream.write(items.getBytes());
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getItemsFromFile(){

        try
        {

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(getString(R.string.file_name))));
            String inputString;

            StringBuffer stringBuffer = new StringBuffer();

            while ((inputString = inputReader.readLine()) != null)
            {
                stringBuffer.append(inputString + "\n");
            }


            return stringBuffer.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void displayItems(String items){
        ((EditText) findViewById(R.id.panel_text_items)).setText(items);

    }


}
