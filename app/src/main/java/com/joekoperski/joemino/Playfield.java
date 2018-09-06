package com.joekoperski.joemino;


import android.graphics.Point;

public class Playfield {
    private int mPlayfield[][];
    private int mDestinationPlayfield[][];
    private Point mMoveMap[][];
    private int width;
    private int height;
    private int numTiles;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    Playfield() {
        width = 12;
        height = 12;
        numTiles = 5;

        mPlayfield = new int[width][height];
        mDestinationPlayfield = new int[width][height];
        mMoveMap = new Point[width][height];
    }// Playfield


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void Set(int x, int y, int tileIndex) {
        if (isInside(x, y)) mPlayfield[x][y] = tileIndex;
    }// Set


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int Get(int x, int y) {
        if (isInside(x, y)) {
            return mPlayfield[x][y];
        }// if
        else {
            return -1;
        } // else
    }// Get

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean isValid(int x, int y) {
        return (isInside(x, y) && mPlayfield[x][y] >= 0);
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
    private Boolean isInside(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }// isInside


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetMovemap(int x, int y, Point moveTo) {
        if (isInside(x, y)) mMoveMap[x][y] = moveTo;
    }// SetMovemap


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Point GetMovemap(int x, int y) {
        if (isInside(x, y)) {
            return mMoveMap[x][y];
        }// if
        else {
            return new Point(-2, -2);
        } // else
    }// GetMovemap


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetDestinationMap(int x, int y, int tileIndex) {
        if (isInside(x, y)) mDestinationPlayfield[x][y] = tileIndex;
    }// Set


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetDestinationMap(int x, int y) {
        if (isInside(x, y)) {
            return mDestinationPlayfield[x][y];
        }// if
        else {
            return -1;
        } // else
    }// Get

}// class Playfield
