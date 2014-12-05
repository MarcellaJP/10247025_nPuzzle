package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class changeDifficultyActivity extends ActionBarActivity {
    int difficulty_index;
    String picture_path;
    public static final String PREFS_NAME = "PrefsFile";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this;

        // Get the picture index that needs to be given as a parameter for a new game activity
        Intent intent = getIntent();
        if (null != intent) {
            picture_path = intent.getStringExtra("picture");      // string number (0 - 3)
        }
        else {
            picture_path = "0";
        }

         // Create Spinner to change difficulty
        final Spinner spinner = new Spinner(changeDifficultyActivity.this); //(Spinner) findViewById(R.id.difficulty_spinner_toast);
        // Create an ArrayAdapter using the resource string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Change background color so text is visible (not white)
        spinner.setPopupBackgroundResource(R.drawable.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                difficulty_index = spinner.getSelectedItemPosition();
                Log.i("diff index = ", "" + difficulty_index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                difficulty_index = 0;
            }
        });

        // set dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Change difficulty");
        // set dialog message
        alertDialogBuilder
//                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setView(spinner )
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity and start new game activity with parameters:

                        // Remember the difficulty preference for future games but remove all other
                        // game memories (because a new game should start)
                        SharedPreferences memory = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = memory.edit();
                        editor.clear();
                        editor.putString("picture", "None");
                        editor.putInt("preference", difficulty_index);
                        editor.commit();

                        // Start the new game activity
                        Intent intent = new Intent(context, GameActivity.class);
                        intent.putExtra("difficulty", difficulty_index);
                        intent.putExtra("picture", picture_path);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Back",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}


