package com.joekoperski.joemino;

import android.content.Context;
import android.content.SharedPreferences;

public class Scores {
    private HighscoreEntry entry[];
    private int numScores;

    private Context mContext;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    Scores(Context context) {
        numScores = 5;
        entry = new HighscoreEntry[numScores];
        for (int i = 0; i < numScores; i++) {
            entry[i] = new HighscoreEntry();
        }// for i
        mContext = context;

        load();
    }// Scores


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void load() {
        SharedPreferences pref = mContext.getSharedPreferences("Preferences", mContext.MODE_PRIVATE); // 0 - for private mode

        for (int i = 0; i < numScores; i++) {
            entry[i].score = pref.getInt("Score" + i, mContext.getResources().getInteger(R.integer.value_highscore_default)); // getting Integer
            entry[i].name = pref.getString("Name" + i, mContext.getResources().getString(R.string.str_highscore_entry_default_text)); // getting String
        }// for i

    }// load


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void save() {
        SharedPreferences pref = mContext.getSharedPreferences("Preferences", mContext.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        for (int i = 0; i < numScores; i++) {
            editor.putString("Name" + i, entry[i].name); // Storing string
            editor.putInt("Score" + i, entry[i].score); // Storing integer
        }// for i
        editor.apply();
    }// save


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int getNumScores() {
        return numScores;
    }// getNumScores


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public HighscoreEntry getAt(int pos) {
        return entry[pos];
    }// getAt


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void insertAt(int pos, HighscoreEntry newEntry) {
        if (pos < numScores) {
            // Move subsequent entries down
            for (int i = numScores - 1; i > pos; i--) {
                entry[i].name = entry[i - 1].name;
                entry[i].score = entry[i - 1].score;
            }// for i

            entry[pos].name = newEntry.name;
            entry[pos].score = newEntry.score;
        }// if
    }// insertAt
}
