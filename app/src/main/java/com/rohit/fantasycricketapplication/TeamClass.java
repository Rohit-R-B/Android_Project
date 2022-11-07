package com.rohit.fantasycricketapplication;

import java.util.ArrayList;

public class TeamClass {

    public String teamName;
    public ArrayList<String> selectedPlayers;
    public int points;

    public TeamClass(){

    }

    public TeamClass(String name,ArrayList<String> sp,int p) {
        teamName = name;
        selectedPlayers = sp;
        points = p;
    }

}