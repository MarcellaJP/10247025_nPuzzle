package nl.projects.mprog.npuzzle10247025.npuzzle10247025;

import android.support.annotation.DrawableRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

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

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.complete_layout);

        // Initialize puzzle values
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        final int DIMENSION = metrics.widthPixels - (2* relativeLayout.getPaddingRight());
        int tiles_on_row = difficulty_index+3;
        int number_of_tiles = (int) Math.pow((tiles_on_row), 2);
        int counter = 0;
        int table_index = 0;

        Bitmap bMap_old = BitmapFactory.decodeResource(getResources(), id);
        Bitmap bMap = Bitmap.createScaledBitmap(bMap_old, DIMENSION, DIMENSION, true);

        TableLayout table_puzzle = new TableLayout(this);

        TableRow[] tableArray = new TableRow[tiles_on_row];
        ImageView[] imageViewArray = new ImageView[number_of_tiles];
        Bitmap[] bitmapArray = new Bitmap[number_of_tiles];

        // For loop that fills the puzzle table with tiles (= parts of the picture)
        for (int i = 0; i < number_of_tiles; i++ ) {
            Log.i("counter ", "" + counter);
            Log.i("row-1 ",  ""+ (tiles_on_row-1));
            Log.i("table_index ", "" + table_index);
            if (i == (tiles_on_row)*table_index){
                Log.i("start new row", ""+ table_index);
                tableArray[table_index] = new TableRow(this);
                tableArray[table_index].setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                table_index = table_index + 1;
            }
            counter = counter + 1;
            imageViewArray[i] = new ImageView(this);
            imageViewArray[i].setId(i);     // unique property for every imageView
            bitmapArray[i] = Bitmap.createBitmap(bMap, ((counter-1) *(DIMENSION / tiles_on_row)), (table_index-1) * DIMENSION/tiles_on_row, DIMENSION/tiles_on_row, DIMENSION/tiles_on_row);
            imageViewArray[i].setImageBitmap(bitmapArray[i]);
            tableArray[table_index-1].addView(imageViewArray[i]);

            if (counter == tiles_on_row){
                Log.i("Try add row", ""+ tableArray[table_index-1]);
                table_puzzle.addView(tableArray[table_index - 1], new TableLayout.LayoutParams(DIMENSION, DIMENSION));
                Log.i("Added row", ""+ (table_index-1));
                counter = 0;
            }
        }

        // Shows the puzzle table
        relativeLayout.addView(table_puzzle);
        }

//    public void initializePuzzle(int tiles_on_row, Bitmap bMap){
//        int tile_number;
//        Bitmap tile = Bitmap.createBitmap(bMap, 0, 0, 60/tiles_on_row, 60/tiles_on_row);
//
////        for (tile_number=0, tile_number < tiles_on_row*tiles_on_row, tile_number ++){
////            Bitmap tile_t = Bitmap.createBitmap(bMap, 0, 0, 60/tiles_on_row, 60/tiles_on_row);
//        }

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
