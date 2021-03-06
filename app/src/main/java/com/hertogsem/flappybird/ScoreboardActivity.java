package com.hertogsem.flappybird;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ScoreboardActivity extends AppCompatActivity {
    public static final String TAG = "ScoreboardActivity";

    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_ADDED = "extra_added";

    public ScoreDb dbHelper;

    private ListView scoreListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new ScoreDb(this);

        scoreListView = (ListView) findViewById(R.id.scoreListView);

        // Add handler for the Fab.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteEntries();
                inflateList();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        // Update the view
        addPlayerScore();
        inflateList();
        super.onResume();
    }

    private void addPlayerScore() {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_SCORE) && !intent.hasExtra(EXTRA_ADDED)) {
            // Add the player's score to the score table if it isn't already there.
            Score score = new Score();
            score.name = intent.getStringExtra(EXTRA_NAME);
            score.score = intent.getIntExtra(EXTRA_SCORE, 0);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                score.saveToDb(db);
            }
            catch (Exception ex) {
                Log.e(TAG, "Exception while saving score: " + ex.getMessage());
            }
            finally {
                db.close();
            }

            // Mark the score as added.
            intent.putExtra(EXTRA_ADDED, true);
        }
    }

    private void inflateList() {
        // Inflate the ListView using the database and CursorAdapter.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = ScoreDb.selectScores(db);
        ScoreAdapter adapter = new ScoreAdapter(this, c, 0);
        scoreListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the custom menu.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_scoreboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }
}
