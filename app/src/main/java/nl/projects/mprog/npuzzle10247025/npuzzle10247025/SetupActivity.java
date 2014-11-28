package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.View;

public class SetupActivity extends ActionBarActivity {

//    private static final String KEY_PICTURE = "Picture";
//    private static final String KEY_DIFFICULTY = "Difficulty";
 //   private ImageView
    String last_selected;
    String difficulty_text;
    int difficulty_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        addListenerOnButton();

        // Create spinner to choose difficulty
        Spinner spinner = (Spinner) findViewById(R.id.difficulty_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        difficulty_text = spinner.getSelectedItem().toString();
        difficulty_index = spinner.getSelectedItemPosition();

        ImageView picture_1 = (ImageView) findViewById(R.id.pic_1);
        ImageView picture_2 = (ImageView) findViewById(R.id.pic_2);
        ImageView picture_3 = (ImageView) findViewById(R.id.pic_3);
        ImageView picture_4 = (ImageView) findViewById(R.id.pic_4);
        final ImageView[] imageViews = {picture_1, picture_2, picture_3, picture_4};

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("test", "test");
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

        int tag_int = 0;
        for (ImageView imageView : imageViews) {
            imageView.setOnClickListener(clickListener);
            imageView.setTag(tag_int);
            tag_int++;
        }
    }

    public void addListenerOnButton() {
        final Context context = this;

        Button button = (Button) findViewById(R.id.start_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, GameActivity.class);
                intent.putExtra("difficulty", difficulty_index);
                intent.putExtra("picture", last_selected);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
