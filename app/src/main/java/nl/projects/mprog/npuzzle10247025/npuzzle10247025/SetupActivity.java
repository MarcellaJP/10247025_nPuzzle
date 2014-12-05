package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.View;

public class SetupActivity extends ActionBarActivity {

    private static final String PREFS_NAME = "PrefsFile";
    private static final int DEFAULT = 1;

    private Button button;
    private String last_selected;
    private int difficulty_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Set button initially disabled, only enabled if valid parameters are given (image clicked)
        button = (Button) findViewById(R.id.start_button);
        button.setEnabled(false);
        addListenerOnButton();

        // A new game is to start so previous memory must be cleared
        SharedPreferences memory = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = memory.edit();
        // First get possible saved difficulty preference, else use default difficulty
        final int selection = memory.getInt("preference", DEFAULT);
        editor.clear();
        editor.commit();

        // Create spinner to choose difficulty
        final Spinner spinner = (Spinner) findViewById(R.id.difficulty_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.difficulty, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // The spinner default must be either it's default value or the preference set by the user
        spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                difficulty_index = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                difficulty_index = 0;
            }
        });

        // Get imageviews
        ImageView picture_1 = (ImageView) findViewById(R.id.pic_1);
        ImageView picture_2 = (ImageView) findViewById(R.id.pic_2);
        ImageView picture_3 = (ImageView) findViewById(R.id.pic_3);
        ImageView picture_4 = (ImageView) findViewById(R.id.pic_4);
        final ImageView[] imageViews = {picture_1, picture_2, picture_3, picture_4};

        // Define onclicklistener for the imageviews
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(true);
                for (ImageView imageView : imageViews) {
                    if (view == imageView) {
                        imageView.setAlpha((float) 1);
                        last_selected = "" + imageView.getTag();
                    } else {
                        imageView.setAlpha((float) 0.2);
                    }
                }
            }
        };

        // Set onclicklistener on the Imageviews
        int tag_int = 0;
        for (ImageView imageView : imageViews) {
            imageView.setOnClickListener(clickListener);
            imageView.setTag(tag_int);
            tag_int++;
        }
    }

    private void addListenerOnButton() {
        final Context context = this;

        // Set onclicklistener on button
        button.setOnClickListener(new View.OnClickListener() {
            // On button click, start the game activity setup with parameters
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, GameActivity.class);
                intent.putExtra("difficulty", difficulty_index);
                intent.putExtra("picture", last_selected);
                startActivity(intent);
                finish();
            }
        });
    }

}
