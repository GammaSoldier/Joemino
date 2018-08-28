package com.joekoperski.joemino;

import java.util.Random;

public class Playfield {
    private int mPlayfield[][];
    private int width;
    private int height;
    private int numTiles;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Playfield() {
        width = 12;
        height = 12;
        numTiles = 5;
        Random rnd = new Random();

        mPlayfield = new int[ width][ height ];
    }// Playfield


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void Set( int x, int y, int tileIndex ) {
        if( isInside( x, y ) ) mPlayfield[x][y] = tileIndex;
    }// Set


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int Get( int x, int y ) {
        if( isInside(x, y ) ) {
            return mPlayfield[x][y];
        }// if
        else {
            return -1;
        } // else
    }// Get

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean isValid( int x, int y ) {
        if( isInside(x, y ) && mPlayfield[x][y] >= 0 )
            return true;
        else
            return false;
    }// isValid

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetWidth() {
        return width;
    }// GetWidth


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetHeight() {
        return height;
    }// GetHeight


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetNumTiles() {
        return numTiles;
    }// GetNumTiles


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean isInside( int x, int y ){
        if( x >= 0 && x < width && y >=0 && y < height ) {
            return true;
        }// if
         else {
            return false;
        }// else
    }// isInside

}// class Playfield
