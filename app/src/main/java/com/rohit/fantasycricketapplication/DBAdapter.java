package com.rohit.fantasycricketapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
public class DBAdapter {
    public static final String DBNAME = "cricket_database";
    private DBHelper helper;

    public DBAdapter(Context context){
        helper = new DBHelper(context, DBNAME);
    }

    // insert user details into the database
    public Boolean insertData(String username, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", username);
        contentValues.put("Password", password);
        long result = db.insert("users", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    // check whether user exists in the database
    public Boolean checkUsername(String username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    // check whether the entered username and password matches with ones in database
    public Boolean checkUsernamepassword(String username, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? and password=?", new String[]{username, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean insertPlayers(String name, String role, int points) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("player_name", name);
        cv.put("role", role);
        cv.put("points", points);

        try {
            db.insert("Players", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();

        return true;
    }

    // retrieve all players having a particular role
    public ArrayList retrievePlayers(String role) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<String> PlayerList = new ArrayList<>();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Players WHERE Role='" + role + "'", null);
            if (cursor.moveToFirst()) {
                do {
                    PlayerList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return PlayerList;
    }

    // returns the points for a particular player from the Players table
    public int getPoints(String player) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT Points FROM Players WHERE Player_name='" + player + "'", null);
            if (cursor.moveToFirst())
                return cursor.getInt(0);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    // check whether a team exists in the Teams table
    public boolean teamExists(String team_name) {
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Teams WHERE Team_name = '" + team_name + "'", null);
            if (cursor.moveToFirst()) return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // returns the an existing team team_name, players (arraylist), and total points of a team from the Teams table
    // in a TeamClass object
    public TeamClass openTeam(String team_name) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM Teams WHERE Team_name = '" + team_name + "'", null);

            if (cursor.moveToFirst()) {
                ArrayList<String> sel_pla = CsStringtoArrayList(cursor.getString(1));
                int points = cursor.getInt(2);
                TeamClass team = new TeamClass(team_name, sel_pla, points);
                return team;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    // saves/inserts a new team into the Teams table
    public boolean saveNewTeam(TeamClass team) {
        String sel_pla = ArrayListToCsString(team.selectedPlayers);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Team_name", team.teamName);
        cv.put("Selected_players", sel_pla);
        cv.put("Total_points", team.points);

        try {
            db.insert("Teams", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return true;
    }

    // update existing team in the Teams table
    public boolean updateExistingTeam(TeamClass team) {
        String sel_pla = ArrayListToCsString(team.selectedPlayers);

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Selected_players", sel_pla);
        cv.put("Total_points", team.points);

        try {
            db.update("Teams", cv, "Team_name=?", new String[]{team.teamName});
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return true;
    }

    // delete a team from the Teams table
    public void deleteTeam(String team_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete("Teams", "Team_name=?", new String[]{team_name});
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    // retreive the teams from the Teams table in decreasing order of their total points
    public ArrayList<String> openLeaderboard() {
        SQLiteDatabase db = helper.getReadableDatabase();

        ArrayList<String> teams_ranked = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT Team_name, Total_points FROM Teams ORDER BY Total_Points desc", null);
            if (cursor.moveToFirst()) {
                do {
                    teams_ranked.add(cursor.getString(0) + " - " + String.valueOf(cursor.getInt(1) + " points"));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return teams_ranked;
    }

    // converts arraylist to comma-separated string
    public ArrayList<String> CsStringtoArrayList(String strValues) {
        String[] strParts = strValues.split(",");

        ArrayList<String> aList =
                new ArrayList<String>(Arrays.asList(strParts));

        return aList;
    }

    // converts comma-separated string to arraylist
    public String ArrayListToCsString(ArrayList<String> playerlist) {
        StringBuilder sbString = new StringBuilder("");

        //iterate through ArrayList
        for (String player : playerlist) {

            //append ArrayList element followed by comma
            sbString.append(player).append(",");
        }

        //convert StringBuffer to String
        String strList = sbString.toString();

        //remove last comma from String if you want
        if (strList.length() > 0)
            strList = strList.substring(0, strList.length() - 1);

        return strList;
    }

}

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context,String DBNAME) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Table1 = "CREATE TABLE Users(Username TEXT PRIMARY KEY, Password TEXT)";
        String Table2 = "CREATE TABLE Players (Player_name TEXT PRIMARY KEY, Role TEXT, Points INTEGER)";
        String Table3 = "CREATE TABLE Teams (Team_name TEXT PRIMARY KEY, Selected_players TEXT, Total_Points INTEGER)";

        db.execSQL(Table1);
        db.execSQL(Table2);
        db.execSQL(Table3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table if exists Users");
        db.execSQL("drop Table if exists Players");
        db.execSQL("drop Table if exists Teams");

        onCreate(db);
    }

}