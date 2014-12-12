package nl.projects.mprog.npuzzle10247025.npuzzle10247025;
// Marcella Wijngaarden - marcellawijngaarden@hotmail.com - 10247025

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
    private SharedPreferences memory;
    private RelativeLayout relative_layout;
    private DisplayMetrics metrics;
    private int dimension;
    private int tiles_on_row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get parameters set in setupActivity or from closed game: difficulty and picture
        memory = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = memory.edit();
        String picture = memory.getString("picture", "None");
        boolean reload = memory.getBoolean("reload", false);
        Intent intent = getIntent();

        if (picture.equals("None")|| !reload) {
            picture_path = intent.getStringExtra("picture");            // string number (0 - 3)
            difficulty_index = intent.getIntExtra("difficulty", 0);     // 0:easy, 1:medium, 2:hard
            number_of_steps = 0;                                        // Reset number of steps
            editor.remove("reload");
            editor.putBoolean("reload", true);
            editor.commit();
        } else {
            picture_path = memory.getString("picture", "None");
            difficulty_index = memory.getInt("difficulty", 0);
            number_of_steps = memory.getInt("step_number", 0);
        }

        // Convert picture reference string to picture id
        final ImageView puzzle_picture = (ImageView) findViewById(R.id.puzzle_picture);
        int id = getPictureId(picture_path);

        // Initialize puzzle values
        final RelativeLayout relative_layout = (RelativeLayout) findViewById(R.id.complete_layout);
        metrics = this.getResources().getDisplayMetrics();
        dimension = metrics.widthPixels - (2 * relative_layout.getPaddingRight());
        tiles_on_row = difficulty_index + 3;
        final int number_of_tiles = (int) Math.pow((tiles_on_row), 2);
        final int tile_size = (dimension / tiles_on_row);
        int counter = 0;
        int table_index = 0;

        Bitmap bmap_old = BitmapFactory.decodeResource(getResources(), id);
        final Bitmap bmap = Bitmap.createScaledBitmap(bmap_old, dimension, dimension, true);
        puzzle_picture.setImageBitmap(bmap);
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
            bitmap_array[i] = Bitmap.createBitmap(bmap, ((counter - 1) * tile_size),
                    (table_index - 1) * tile_size, tile_size, tile_size);
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
        if (picture.equals("None") || !reload) {
            for (int j = 0; j < image_array.length - 1; j++) {
                index_array.add(j);
            }
            // Shuffle the list of image indexes: in this random order the table will be filled
            index_array = getShuffledList(index_array);
            index_array.add(image_array.length - 1);   // Last tile is not random
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
        changePuzzle(number_of_tiles, tiles_on_row, dimension, table_array, index_array,
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
                    changePuzzle(number_of_tiles, tiles_on_row, dimension, table_array, index_array,
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
        memory = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
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

    public boolean isSolved(ArrayList solvedArray, ArrayList<Integer> index_array,
                            int number_of_steps) {
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

    public boolean checkMove (ImageView clicked_view, int number_of_tiles, int tiles_on_row,
                              ImageView[] image_array ){
        ImageView blanc_tile = image_array[number_of_tiles -  1];
        int blanc_pos = Integer.parseInt(blanc_tile.getTag().toString());
        int clicked_pos = Integer.parseInt(clicked_view.getTag().toString());

        // Check if clicked tile is adjacent to blanc tile, if so return true
        if (clicked_pos == (blanc_pos + tiles_on_row)) { return true; }
        if (clicked_pos == (blanc_pos - tiles_on_row)) { return true; }
        if (clicked_pos == (blanc_pos + 1)) {
            if (clicked_view.getParent() == blanc_tile.getParent()) {
                return true;
            }
        }
        if (clicked_pos == (blanc_pos - 1)) {
            if (clicked_view.getParent() == blanc_tile.getParent()) {
                return true; }
            }
        return false;
    }

    public void changePuzzle(int number_of_tiles, int tiles_on_row, int dimension,
                             TableRow[] table_arrray, ArrayList<Integer> index_array,
                             ImageView[] image_array, TableLayout table_puzzle,
                             RelativeLayout relativeLayout) {
        int table_index = 0;
        int counter = 0;

        // First clear tablerows from previous views
        for (TableRow tableRow: table_arrray) {
            if (tableRow != null) {
                if (tableRow.getChildCount() != 0) {
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
            image_array[index_array.get(i)].setId(index_array.get(i));
            image_array[index_array.get(i)].setTag((i));
            image_array[index_array.get(i)].setLayoutParams(new TableRow.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            table_arrray[table_index - 1].addView(image_array[index_array.get(i)]);

            // Add the finished table row to table view
            if (counter == tiles_on_row) {
                table_puzzle.addView(table_arrray[table_index - 1],
                        new TableLayout.LayoutParams(dimension, dimension));
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
            // Open new activity to change difficulty
            Intent intent_diff = new Intent(getApplicationContext(), ChangeDifficultyActivity.class);
            intent_diff.putExtra("picture", pic_path);
            startActivity(intent_diff);
        } if (id == R.id.shuffle) {
            // Restarts the game with same picture and difficulty
            memory = getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = memory.edit();
            editor.clear();
            editor.putBoolean("reload", false);
            editor.commit();

            // Start the game activity with picture and difficulty
            int difficulty = intent.getIntExtra("difficulty", 0);
            Intent intent_shuffle = new Intent(getApplicationContext(), GameActivity.class);
            intent_shuffle.putExtra("picture", pic_path);
            intent_shuffle.putExtra("difficulty", difficulty);
            startActivity(intent_shuffle);
            finish();
        } if (id == R.id.show_solution){
            // Shows the solution image in a dialog
            int picture_id = getPictureId(pic_path);

            // Get picture and set correct layout
            ImageView solution = new ImageView(this);
            solution.setImageResource(picture_id);
            solution.setAdjustViewBounds(true);

            // Make dialog and set correct layout and view
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setView(solution)
                    .show();
            AlertDialog dialog = dialogBuilder.create();
            WindowManager.LayoutParams layout_parameters = dialog.getWindow().getAttributes();
            layout_parameters.dimAmount = 0.5f;
            layout_parameters.width = LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layout_parameters);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } if (id == R.id.quit) {
            // Quits current game and returns to the setup screen
            onBackPressed();
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
