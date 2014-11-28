package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.support.annotation.DrawableRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String picture_path;
        int difficulty_index;
        int id;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get parameters set in setupActivity : difficulty and picture
        Intent intent = getIntent();
        if (null != intent) {
            picture_path = intent.getStringExtra("picture");            // string number (0 - 3)
            difficulty_index = intent.getIntExtra("difficulty", 0);     // 0:easy, 1:medium, 2:hard
        }
        else {
            picture_path = "None";          // Default values
            difficulty_index = 0;
        }

        // Convert picture reference string to picture id
        ImageView puzzlePicture = (ImageView) findViewById(R.id.puzzle_picture);
        id = R.drawable.sample_0;
        if (picture_path.equals("0")) { id = R.drawable.sample_0; }
        if (picture_path.equals("1")) { id = R.drawable.sample_1; }
        if (picture_path.equals("2")) { id = R.drawable.sample_2; }
        if (picture_path.equals("3")) { id = R.drawable.sample_3; }

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.complete_layout);

        // Initialize puzzle values
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        final int DIMENSION = metrics.widthPixels - (2* relativeLayout.getPaddingRight());
        final int tiles_on_row = difficulty_index+3;
        final int number_of_tiles = (int) Math.pow((tiles_on_row), 2);
        int counter = 0;
        int table_index = 0;
        final int tile_size = (DIMENSION / tiles_on_row);

        Bitmap bMap_old = BitmapFactory.decodeResource(getResources(), id);
        Bitmap bMap = Bitmap.createScaledBitmap(bMap_old, DIMENSION, DIMENSION, true);

        final TableLayout table_puzzle = new TableLayout(this);

        final TableRow[] tableArray = new TableRow[tiles_on_row];
        final ImageView[] imageViewArray = new ImageView[number_of_tiles];
        final Bitmap[] bitmapArray = new Bitmap[number_of_tiles];

        // For loop that fills the image view array with tile images (= parts of the picture)
        for (int i = 0; i < number_of_tiles; i++ ) {
            if (i == (tiles_on_row)*table_index){
                table_index = table_index + 1;
            }
            counter = counter + 1;
            imageViewArray[i] = new ImageView(this);
            imageViewArray[i].setId(i);     // unique property for every imageView
            bitmapArray[i] = Bitmap.createBitmap(bMap, ((counter-1) * tile_size), (table_index-1) * tile_size, tile_size, tile_size);
            imageViewArray[i].setImageBitmap(bitmapArray[i]);

            if (counter == tiles_on_row){
                counter = 0;
            }
        }

        // A list of indexes for the imageview array is made and shuffled
        final ArrayList<Integer> indexArray = new ArrayList<Integer>();
        for (int j = 0; j < imageViewArray.length-1; j++){
            indexArray.add(j);
        }
        Collections.shuffle(indexArray);
        Collections.shuffle(indexArray);
        indexArray.add((int) imageViewArray.length-1 );

        // Here..
        changePuzzle(number_of_tiles, tiles_on_row, DIMENSION, tableArray, indexArray, imageViewArray, table_puzzle, relativeLayout);
        int index_empty_tile = number_of_tiles - 1;


        for (int i = 0; i < number_of_tiles; i++) {
            final int finalI = i;
            imageViewArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(GameActivity.this, "Swapped array"+ (finalI-1) + "with " + indexArray.get(number_of_tiles-1), Toast.LENGTH_SHORT).show();
                    Log.i("indexArray before swap ", "" + indexArray);
                    Log.i("index van lege tile ", "" + indexArray.get(number_of_tiles-1));
                    Log.i("index van tile ", ""+(finalI));
                    Collections.swap(indexArray, finalI, indexArray.get(number_of_tiles-1));
//                    Log.i("Swapped array "+ (finalI-), "with " + (indexArray.get(number_of_tiles-1)));
                    Log.i("indexArray after swap ", "" + indexArray);
                    changePuzzle(number_of_tiles, tiles_on_row, DIMENSION, tableArray, indexArray, imageViewArray, table_puzzle, relativeLayout);
                }
            });
        }

        }

    public boolean checkMove (int number_of_tiles, int tiles_on_row, int DIMENSION, TableRow[] tableArray, ArrayList<Integer> indexArray, ImageView[] imageViewArray, TableLayout table_puzzle, RelativeLayout relativeLayout ){

    return true;
    }
//    public void initializePuzzle(int tiles_on_row, Bitmap bMap){
//        int tile_number;
//        Bitmap tile = Bitmap.createBitmap(bMap, 0, 0, 60/tiles_on_row, 60/tiles_on_row);
//
////        for (tile_number=0, tile_number < tiles_on_row*tiles_on_row, tile_number ++){
////            Bitmap tile_t = Bitmap.createBitmap(bMap, 0, 0, 60/tiles_on_row, 60/tiles_on_row);
//        }

    // The table is filled in random order with image tiles, except for last tile
    public void changePuzzle(int number_of_tiles, int tiles_on_row, int DIMENSION, TableRow[] tableArray, ArrayList<Integer> indexArray, ImageView[] imageViewArray, TableLayout table_puzzle, RelativeLayout relativeLayout ) {
        int table_index = 0;
        int counter = 0;

        for (TableRow tableRow: tableArray) {
            if (tableRow != null) {
                if ((int) tableRow.getChildCount() != 0) {
                    tableRow.removeAllViewsInLayout();
                }
            }
        }

        if (table_puzzle != null){relativeLayout.removeView(table_puzzle);}

        for (int i = 0; i < number_of_tiles; i++) {
            if (i == (tiles_on_row) * table_index) {
                tableArray[table_index] = new TableRow(this);
                tableArray[table_index].setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                table_index = table_index + 1;
            }

            counter = counter + 1;
            tableArray[table_index - 1].addView(imageViewArray[indexArray.get(i)]);

            if (counter == tiles_on_row) {
//                Log.i("Try add row", "" + tableArray[table_index - 1]);
                table_puzzle.addView(tableArray[table_index - 1], new TableLayout.LayoutParams(DIMENSION, DIMENSION));
//                Log.i("Added row", "" + (table_index - 1));
                counter = 0;
            }

            // Make the last tile transparent
            if (i == number_of_tiles - 1) {
                imageViewArray[i].setVisibility(View.INVISIBLE);
            }
        }

        // Shows the puzzle table
        relativeLayout.addView(table_puzzle);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
