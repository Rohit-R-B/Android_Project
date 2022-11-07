package com.rohit.fantasycricketapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    ListView lv_leaderboard;
    Button btn_rankings;

    DBAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        lv_leaderboard = findViewById(R.id.lv_leaderboard);
        btn_rankings = findViewById(R.id.btn_rankings);
        db = new DBAdapter(this);

        // display teams in descending order of their scores
        btn_rankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> teams_ranked = db.openLeaderboard();
                ArrayAdapter<String> aa_teams = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,teams_ranked);
                lv_leaderboard.setAdapter(aa_teams);
            }
        });

    }
}