package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends ActionBarActivity {

    private static final String PREFS_NAME = "PrefsFile";
    private static int number_of_steps;
    private int difficulty_index;
    private String picture_path;
    private ArrayList<Integer> index_array = new ArrayList<Integer>();
    SharedPreferences memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        memory = getSharedPreferences(PREFS_NAME, 0);

        // Get parameters set in setupActivity or from closed game: difficulty and picture
        String picture = memory.getString("picture", "None");
        Intent intent = getIntent();
        if (picture.equals("None")) {
            picture_path = intent.getStringExtra("picture");            // string number (0 - 3)
            difficulty_index = intent.getIntExtra("difficulty", 0);     // 0:easy, 1:medium, 2:hard
            number_of_steps = 0;    // Reset number of steps
            Log.i("intent is ", "" + intent);
        } else {
            Log.i("from memory ", "" + picture);
            picture_path = memory.getString("picture", "None");
            difficulty_index = memory.getInt("difficulty", 0);
            number_of_steps = memory.getInt("step_number", 0);
        }
        Log.i("picture path ", "" + picture_path);

        // Convert picture reference string to picture id
        final ImageView puzzle_picture = (ImageView) findViewById(R.id.puzzle_picture);
        int id = getPictureId(picture_path);

        // Initialize puzzle values
        final RelativeLayout relative_layout = (RelativeLayout) findViewById(R.id.complete_layout);
        final DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        final int DIMENSION = metrics.widthPixels - (2 * relative_layout.getPaddingRight());
        final int tiles_on_row = difficulty_index + 3;
        final int number_of_tiles = (int) Math.pow((tiles_on_row), 2);
        int counter = 0;
        int table_index = 0;
        final int tile_size = (DIMENSION / tiles_on_row);

        Bitmap bMap_old = BitmapFactory.decodeResource(getResources(), id);
        final Bitmap bMap = Bitmap.createScaledBitmap(bMap_old, DIMENSION, DIMENSION, true);
        puzzle_picture.setImageBitmap(bMap);
        final TableLayout table_puzzle = new TableLayout(this);
        final TableRow[] table_array = new TableRow[tiles_on_row];
        final ImageView[] image_array = new ImageView[number_of_tiles];
        final Bitmap[] bitmap_array = new Bitmap[number_of_tiles];

        // For loop that fills the image view array with tile images (= parts of the picture)
        for (int i = 0; i < number_of_tiles; i++ ) {
            if (i == (tiles_on_row) * table_index) {
                table_index = table_index + 1;
            }
            counter = counter + 1;
            image_array[i] = new ImageView(this);
            bitmap_array[i] = Bitmap.createBitmap(bMap, ((counter-1) * tile_size),
                    (table_index-1) * tile_size, tile_size, tile_size);
            image_array[i].setImageBitmap(bitmap_array[i]);
            image_array[i].setPadding(5, 5, 5, 5);
            image_array[i].setAdjustViewBounds(true);

            if (counter == tiles_on_row) {
                counter = 0;
            }
        }

        // Makes an ordered index list as is the solved state
        final ArrayList<Integer> solved_array = new ArrayList<Integer>();
        for (int i = 0; i < image_array.length; i++) {
            solved_array.add(i);
        }

        // A list of indexes for the imageview array is made and shuffled
        if (picture == "None") {
            for (int j = 0; j < image_array.length - 1; j++) {
                index_array.add(j);
            }
            // Shuffle the list of image indexes: in this random order the table will be filled
            index_array = getShuffledList(index_array);
            index_array.add((int) image_array.length - 1);   // Last tile is not random
        } else {
            index_array.clear();
            for(int i = 0; i < image_array.length; i++) {
                index_array.add(Integer.parseInt(memory.getString("index_" + i, null)));
            }
        }
        // Initialize step number text
        TextView step_view = (TextView) relative_layout.findViewById(R.id.step_number);
        step_view.setText("" + number_of_steps);

        // Initiate the puzzle and make the last tile invisible
        changePuzzle(number_of_tiles, tiles_on_row, DIMENSION, table_array, index_array,
                image_array, table_puzzle, relative_layout);
        image_array[number_of_tiles - 1].setVisibility(View.INVISIBLE);

        // Makes the puzzle invisible and shows the solution for 3 seconds
        table_puzzle.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                table_puzzle.setVisibility(View.VISIBLE);
                puzzle_picture.setVisibility(View.GONE);
            }
        }, 3000);

        // Make onclicklistener to contain all the necessary acts on tile click
        View.OnClickListener click_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the clicked tile is adjacent to empty tile and can be swapped
                if (checkMove ((ImageView) view, number_of_tiles, tiles_on_row, image_array)) {
                    // If so, swap the tile numbers in the indexlist
                    Collections.swap(index_array, Integer.parseInt(view.getTag().toString()),
                            index_array.indexOf(number_of_tiles - 1));
                    // Then change the puzzle layout accordingly
                    changePuzzle(number_of_tiles, tiles_on_row, DIMENSION, table_array, index_array,
                            image_array, table_puzzle, relative_layout);
                    // Update the number of steps displayed
                    number_of_steps ++;
                    TextView stepView = (TextView) relative_layout.findViewById(R.id.step_number);
                    stepView.setText("" + number_of_steps);
                    // Check if the new puzzle state equals the solved puzzle state
                    isSolved(solved_array, index_array, number_of_steps);
                }
            }
        };

        // Set onclicklistener for all the tiles
        for (int i = 0; i < number_of_tiles; i++) {
            image_array[i].setOnClickListener(click_listener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // An editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences memory = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = memory.edit();
        editor.putInt("difficulty", difficulty_index);
        editor.putString("picture", picture_path);
        editor.putInt("step_number", number_of_steps);

        for(int i=0; i < index_array.size(); i++) {
            int a = index_array.get(i);
            editor.putString("index_" + i, String.valueOf(a));
        }

        // Commit the edits
        editor.commit();
    }

    private ArrayList getShuffledList(ArrayList index_array) {
        // Create an solvable index list (so even number of permutations)
        for (int i = 0; i < 50; i++){
            Random r = new Random();
            int index_1 = r.nextInt(index_array.size() - 1);
            int index_2 = r.nextInt(index_array.size() - 1);

            while (index_1 == index_2){
                index_2 = r.nextInt(index_array.size() - 1);
            }
            Collections.swap(index_array, index_1, index_2);
        }
        return index_array;
    }

    public boolean isSolved(ArrayList solvedArray, ArrayList<Integer> index_array, int number_of_steps) {
        for (int i = 0; i < solvedArray.size(); i++) {
            if (solvedArray.get(i) != index_array.get(i)) {
                return false;
            }
        }

        // If solved (so not false) finish this activity and call next activity
        Intent intent = getIntent();
        String pic_path = intent.getStringExtra("picture");

        Intent intent_won = new Intent(getApplicationContext(), WinnerActivity.class);
        intent_won.putExtra("picture", pic_path);
        intent_won.putExtra("steps", number_of_steps);
        startActivity(intent_won);
        finish();
        return true;
    }

    public boolean checkMove (ImageView clickedView, int number_of_tiles, int tiles_on_row, ImageView[] image_array ){
        ImageView blanc_tile = image_array[number_of_tiles -  1];
        int blanc_pos = Integer.parseInt(blanc_tile.getTag().toString());
        int clicked_pos = Integer.parseInt(clickedView.getTag().toString());

        // Check if clicked tile is adjacent to blanc tile, if so return true
        if (clicked_pos == (blanc_pos + tiles_on_row)) { return true; }
        if (clicked_pos == (blanc_pos - tiles_on_row)) { return true; }
        if (clicked_pos == (blanc_pos + 1)) {
            if (clickedView.getParent() == blanc_tile.getParent()) {
                return true;
            }
        }
        if (clicked_pos == (blanc_pos - 1)) {
            if (clickedView.getParent() == blanc_tile.getParent()) {
                return true; }
            }
        return false;
    }

    public void changePuzzle(int number_of_tiles, int tiles_on_row, int DIMENSION,
                             TableRow[] table_arrray, ArrayList<Integer> index_array,
                             ImageView[] image_array, TableLayout table_puzzle,
                             RelativeLayout relativeLayout) {
        int table_index = 0;
        int counter = 0;

        // First clear tablerows from previous views
        for (TableRow tableRow: table_arrray) {
            if (tableRow != null) {
                if ((int) tableRow.getChildCount() != 0) {
                    tableRow.removeAllViewsInLayout();
                }
            }
        }

        // Clear table from previous views
        if (table_puzzle != null){relativeLayout.removeView(table_puzzle);}

        // Fill the tablerows again with imageviews based on indexnumber as given by the index array
        for (int i = 0; i < number_of_tiles; i++) {

            // If a tablerow is filled, starts a new tablerow
            if (i == (tiles_on_row) * table_index) {
                table_arrray[table_index] = new TableRow(this);
                table_arrray[table_index].setLayoutParams(new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                table_index = table_index + 1;
            }

            counter = counter + 1;
            image_array[index_array.get(i)].setId(index_array.get(i));      // unique property for every imageView
            image_array[index_array.get(i)].setTag((i));
            image_array[index_array.get(i)].setLayoutParams(new TableRow.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            table_arrray[table_index - 1].addView(image_array[index_array.get(i)]);

            // Add the finished table row to table view
            if (counter == tiles_on_row) {
                table_puzzle.addView(table_arrray[table_index - 1], new TableLayout.LayoutParams(DIMENSION, DIMENSION));
                counter = 0;
            }
        }

        // Shows the puzzle table
        relativeLayout.addView(table_puzzle);
    }

    public int getPictureId(String picture_path){
        // Returns the id of the drawable (image) for given setup parameter
        if (picture_path.equals("0")) { return R.drawable.sample_0; }
        if (picture_path.equals("1")) { return R.drawable.sample_1; }
        if (picture_path.equals("2")) { return R.drawable.sample_2; }
        if (picture_path.equals("3")) { return R.drawable.sample_3; }

        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = getIntent();
        String pic_path = intent.getStringExtra("picture");

        if (id == R.id.change_difficulty) {
            // Open new activity
            Intent intent_diff = new Intent(getApplicationContext(), changeDifficultyActivity.class);
            intent_diff.putExtra("picture", pic_path);
            startActivity(intent_diff);
        } if (id == R.id.shuffle) {
            // Restarts the game with same parameters
            int difficulty = intent.getIntExtra("difficulty", 0);
            SharedPreferences.Editor editor = memory.edit();
            editor.remove("picture");
            editor.commit();
            Intent intent_shuffle = new Intent(getApplicationContext(), GameActivity.class);
            intent_shuffle.putExtra("picture", pic_path);
            intent_shuffle.putExtra("difficulty", difficulty);
            startActivity(intent_shuffle);
        } if (id == R.id.show_solution){
            // Shows the solution image in a dialog
            final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.complete_layout);
            final DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            final int DIMENSION = metrics.widthPixels - (2* relativeLayout.getPaddingRight());
            int picture_id = getPictureId(pic_path);
            Bitmap bMap_old = BitmapFactory.decodeResource(getResources(), picture_id);
            Bitmap bMap = Bitmap.createScaledBitmap(bMap_old, DIMENSION, DIMENSION, true);
            ImageView solution = new ImageView(this);
            solution.setImageBitmap(bMap);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setView(solution)
                    .show();

            AlertDialog dialog = dialogBuilder.create();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount=0.5f;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
