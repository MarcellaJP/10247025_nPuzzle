package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WinnerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        final Context context = this;

        // Get puzzle picture from previous activity and display
        Intent intent = getIntent();
        String picture_path = intent.getStringExtra("picture");           // string number (0 - 3)
        int id = R.drawable.sample_0;
        if (picture_path.equals("0")) { id = R.drawable.sample_0; }
        if (picture_path.equals("1")) { id = R.drawable.sample_1; }
        if (picture_path.equals("2")) { id = R.drawable.sample_2; }
        if (picture_path.equals("3")) { id = R.drawable.sample_3; }

        final ImageView puzzle_picture = (ImageView) findViewById(R.id.puzzle_picture);
        puzzle_picture.setImageResource(id);

        // Get number of steps from previous activity and display
        int steps = intent.getIntExtra("steps", 0);
        TextView steps_solved = (TextView) findViewById(R.id.steps_solved);
        steps_solved.setText("Solved in " + steps + " steps");

        // Set action to button click: returns to setup activity to start a new game
        Button button = (Button) findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent_back = new Intent(context, SetupActivity.class);
                startActivity(intent_back);
            }
        });

    }

    @Override
    public void onBackPressed(){
        // Call on create of setup activity (instead of on resume)
        Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
