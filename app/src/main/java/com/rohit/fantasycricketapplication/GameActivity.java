package com.rohit.fantasycricketapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    RadioButton rb_bat,rb_bwl,rb_ar,rb_wk;
    RadioGroup radio_group;
    Button btn_save, btn_view, btn_delete;
    ListView lv_selected,lv_available;
    TextView tv_num_players,tv_name;

    int wk_count,player_count,total_points;
    ArrayList<String> av_players,selected_players;
    ArrayAdapter<String> aa_available,aa_selected;
    String team_name;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // this needs to be called the first time the game is played to load the player names, their roles, and their points
        // into the Players table in the database
        insertPlayers();

        rb_ar=findViewById(R.id.rb_ar);
        rb_wk=findViewById(R.id.rb_wk);
        rb_bat=findViewById(R.id.rb_bat);
        rb_bwl=findViewById(R.id.rb_bwl);
        radio_group = findViewById(R.id.radioGroup);
        btn_save=findViewById(R.id.btn_save);
        btn_view=findViewById(R.id.btn_view_leaderboard);
        btn_delete=findViewById(R.id.btn_delete);
        lv_available=findViewById(R.id.lv_available);
        lv_selected=findViewById(R.id.lv_selected);
        tv_num_players=findViewById(R.id.tv_num_players);
        tv_name=findViewById(R.id.tv_team_name);
        db = new DBAdapter(this);

        wk_count=0;
        player_count=0;
        total_points=0;

        selected_players = new ArrayList<>();
        aa_selected = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,selected_players);

        // set username as the team name
        Intent intent = getIntent();
        team_name = intent.getStringExtra("team_name");
        Log.d("rohit_intent", team_name);
        tv_name.setText(team_name);

        // load team if it exists in the database
        if(db.teamExists(tv_name.getText().toString()))
        {
            TeamClass team = db.openTeam(team_name);
            selected_players = team.selectedPlayers;
            aa_selected =  new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,selected_players);
            lv_selected.setAdapter(aa_selected);

            player_count = selected_players.size();
            tv_num_players.setText(String.valueOf(player_count));
            total_points = team.points;

            if(selected_players.contains("Dinesh Karthik") | selected_players.contains("Matthew Wade"))
            {
                if(selected_players.contains("Dinesh Karthik") | selected_players.contains("Matthew Wade"))
                    wk_count=2;
                else
                    wk_count=1;
            }
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create TeamClass object
                TeamClass team = new TeamClass(team_name,selected_players,total_points);

                // update existing team
                if(db.teamExists(tv_name.getText().toString()))
                {
                    db.updateExistingTeam(team);
                }
                // insert new team
                else
                {
                    db.saveNewTeam(team);
                }
            }
        });

        // goto leaderboard activity on clicking this button
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        // delete a team from the database
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteTeam(team_name);
                tv_num_players.setText("0");
                player_count = 0;
                refreshLists();
            }
        });

        // when a particular radio button is checked, all players for that role are loaded into the lv_available listview
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioId);
                av_players = db.retrievePlayers((String) radioButton.getText());
                aa_available = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,av_players);
                lv_available.setAdapter(aa_available);
            }
        });

        // when a player from the lv_available listview is clicked, he is added to the lv_selected listview
        lv_available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(player_count<11){

                    String current_player = ((TextView)view).getText().toString();

                    //prevent a player from getting selected twice
                    if(selected_players.contains(current_player)){
                        Toast.makeText(getApplicationContext(),current_player+" is already selected!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // if a user has selected 10 non wicketkeepers, and tries to select a non-wicketkeeper for his 11th player
                        if(player_count==10 && wk_count==0 && !current_player.equals("Dinesh Karthik") && !current_player.equals("Matthew Wade"))
                        {
                            // the user is forced to select a wicketkeeper
                            Toast.makeText(getApplicationContext(),"Select a wicket keeper!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // add player to selected list
                            selected_players.add(current_player);
                            aa_selected.notifyDataSetChanged();
                            lv_selected.setAdapter(aa_selected);

                            // increment player count and total points of the team
                            player_count += 1;
                            total_points+=db.getPoints(current_player);

                            // increment wicketkeeper count on selecting a wicketkeeper
                            if(current_player.equals("Dinesh Karthik") || current_player.equals("Matthew Wade"))
                            {
                                wk_count+=1;
                            }
                            tv_num_players.setText(String.valueOf(player_count));

                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You already have 11 players!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // remove a player from lv_selected listview upon clicking that player
        lv_selected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(player_count>0){
                    String current_player = ((TextView)view).getText().toString();

                    // remove player from selected list
                    selected_players.remove(current_player);
                    aa_selected.notifyDataSetChanged();
                    lv_selected.setAdapter(aa_selected);

                    player_count -= 1;
                    total_points-=db.getPoints(current_player);

                    if(current_player.equals("Dinesh Karthik") || current_player.equals("Matthew Wade"))
                    {
                        wk_count-=1;
                    }
                    tv_num_players.setText(String.valueOf(player_count));

                }
                else{
                    Toast.makeText(getApplicationContext(),"You have no player to remove!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // clears the lv_selected listview
    public void refreshLists()
    {
        selected_players.clear();
        aa_selected.notifyDataSetChanged();
        lv_selected.setAdapter(aa_selected);
    }

    // generate random no.s between 1 and 10 to apply as player's fantasy points
    public void insertPlayers()
    {
        db = new DBAdapter(this);

        Random randomGenerator=new Random();
        int[] arr = new int[22];
        for(int i=0;i<22;i++)
        {
            arr[i] = randomGenerator.nextInt(10) + 1;
        }

        db.insertPlayers("KL Rahul","BAT",arr[0]);
        db.insertPlayers("Rohit Sharma","BAT",arr[1]);
        db.insertPlayers("Virat Kohli","BAT",arr[2]);
        db.insertPlayers("Suryakumar Yadav","BAT",arr[3]);
        db.insertPlayers("Hardik Pandya","AR",arr[4]);
        db.insertPlayers("Dinesh Karthik","WK",arr[5]);
        db.insertPlayers("Axar Patel","AR",arr[6]);
        db.insertPlayers("R. Ashwin","AR",arr[7]);
        db.insertPlayers("Mohd. Shami","BWL",arr[8]);
        db.insertPlayers("Bhuvaneshwar Kumar","BWL",arr[9]);
        db.insertPlayers("Jasprit Bumrah","BWL",arr[10]);

        db.insertPlayers("Aaron Finch","BAT",arr[11]);
        db.insertPlayers("David Warner","BAT",arr[12]);
        db.insertPlayers("Steve Smith","BAT",arr[13]);
        db.insertPlayers("Mitchell Marsh","BAT",arr[14]);
        db.insertPlayers("Glenn Maxwell","AR",arr[15]);
        db.insertPlayers("Matthew Wade","WK",arr[16]);
        db.insertPlayers("Marcus Stoinis","AR",arr[17]);
        db.insertPlayers("Pat Cummins","AR",arr[18]);
        db.insertPlayers("Mitchell Starc","BWL",arr[19]);
        db.insertPlayers("Josh Hazlewood","BWL",arr[20]);
        db.insertPlayers("Adam Zampa","BWL",arr[21]);
    }

}