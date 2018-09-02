package com.joekoperski.joemino;

import android.graphics.Point;

import java.util.Random;

public class GameRules {

    public static final int GAMEOVER = 0;
    public static final int CONTINUE = 1;
    public static final int FORBIDDEN = 2;
    public static final int CONTINUE_MOVE = 3;

    private static final int STATE_DELETE = 0;
    private static final int STATE_DROP = 1;
    private static final int STATE_COMPRESS = 2;
    private static final int STATE_FINISHED = 3;
    private int mScore;
    private int moveState;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    GameRules( Playfield playfield ) {
        mScore = playfield.GetHeight() * playfield.GetWidth();

        Random rnd = new Random();

/*
        int testMap[][] = {
                 {-1,-1, 2,-1,-1,-1}
                ,{ 1, 1, 3, 3, 4,-1}
                ,{ 0, 1, 3, 3, 1,-1}
                ,{ 1, 1, 2, 2, 2, 3}
                ,{ 1, 0, 2, 4, 2, 1}
                ,{ 1, 1, 2, 3, 2, 3}
        };
*/


        for( int j=0; j< playfield.GetHeight(); j++ ) {
            for( int i=0; i< playfield.GetWidth(); i++ ) {
                playfield.Set( i, j , rnd.nextInt( playfield.GetNumTiles()) );
//                playfield.Set( i, j , testMap[j][i] );
                playfield.SetDestinationMap( i, j , playfield.Get(i, j) );
                playfield.SetMovemap(i, j, new Point( i, j ));
            }// for i
        }// for j

// debug
/*
        for( int j=0; j< playfield.GetHeight(); j++ ) {
            playfield.Set( playfield.GetWidth() - 2, j , 1 );
            playfield.SetDestinationMap( playfield.GetWidth() - 2, j , 1 );
        }
*/

        moveState = STATE_DELETE;
    }// GameRules


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int makeMove( Playfield playfield, int x, int y ) {
        int actualTile;
        Point keepMoveIndex = new Point();
        int retVal = 1;

        // init movemap
        initMoveMap( playfield );

        switch(moveState) {
            case STATE_DELETE:     // delete neighbours
                // is validTile
                if ( playfield.isValid(x, y) ) {
                    actualTile = playfield.Get( x, y );
                    keepMoveIndex = playfield.GetMovemap(x, y);
                    int removed = deleteNeighbours( playfield, x, y );
                    if( removed == 1 ){ // only 1 tile removable
                        // restore clicked tile
                        playfield.Set( x, y, actualTile );
                        playfield.SetDestinationMap( x, y, actualTile );
                        playfield.SetMovemap(x, y, keepMoveIndex);
                        retVal = FORBIDDEN;
                    }// if
                    else {
                        mScore -= removed;
                        retVal = CONTINUE_MOVE;
                        moveState = STATE_DROP;
                    }// else
                }// if
                break;
            case STATE_DROP:     // drop column
                syncMaps( playfield );
                dropColumn( playfield );
                retVal = CONTINUE_MOVE;
                moveState = STATE_COMPRESS;
                break;
            case STATE_COMPRESS:     // compress
                syncMaps( playfield );
                compress( playfield );
                retVal = CONTINUE_MOVE;
                moveState = STATE_FINISHED;
                break;
            case STATE_FINISHED:
                syncMaps( playfield );
                if( !pairsLeft( playfield ) ) {
                    retVal = GAMEOVER;
                }// if
                else {
                    retVal = CONTINUE;
                }// else
                moveState = STATE_DELETE;

            default:    // illegal state

        }// switch

        return retVal;
    }// makeMove


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int deleteNeighbours( Playfield playfield, int x, int y ) {

        int numTiles = 0;
        int actualTile = playfield.Get( x, y );

        playfield.Set( x, y, -1 );
        playfield.SetDestinationMap( x, y, -1 );
        playfield.SetMovemap( x, y, new Point(-1, -1) );

        // check upper tile
        if( y > 0 )	 {
            if( playfield.Get(x, y-1) == actualTile ) {		// is neighbour the same tile as the actual?
                numTiles += deleteNeighbours( playfield, x, y-1 );
            }// if
        }// if

        // check lower tile
        if( y+1 < playfield.GetHeight() )	 {
            if( playfield.Get(x, y+1) == actualTile ) {		// is neighbour the same tile as the actual?
                numTiles += deleteNeighbours( playfield, x, y+1 );
            }// if
        }// if

        // check left tile
        if( x > 0 )	 {
            if( playfield.Get(x-1, y) == actualTile ) {		// is neighbour the same tile as the actual?
                numTiles += deleteNeighbours( playfield, x-1, y );
            }// if
        }// if

        // check right tile
        if( x+1 < playfield.GetWidth() )	 {
            if( playfield.Get(x+1, y) == actualTile ) {		// is neighbour the same tile as the actual?
                numTiles += deleteNeighbours( playfield, x+1, y );
            }// if
        }// if

        numTiles++;
        return numTiles;
    }// deleteNeighbours


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void dropColumn( Playfield playfield ) {

        for( int i = 0; i < playfield.GetWidth(); i++ ) {
            for( int j = playfield.GetHeight() - 1; j >= 0; j-- ) {
                if( playfield.Get( i, j) == -1 ) {
                    // found deleted tile
                    if( isUpperColumn(playfield, i, j)) {
                        dropTile( playfield, i, j );
                        break;      // leave loop
                    }// if
                }// if
            }//  for j
        }// for i
    }// dropColumn


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void dropTile( Playfield playfield, int x, int y ) {
        int dropStep = 1;

        if( y > 0 ) {
            for( int i = y - 1; i >= 0; i-- ){
                int tile = playfield.Get( x, i);
                if( tile == -1 ) {
                    dropStep++;
                }  // if
                else {
                    Point moveTo = playfield.GetMovemap(x, i);
                    moveTo.y += dropStep;
                    playfield.SetMovemap(x, i, moveTo);
                    playfield.SetDestinationMap( x, i + dropStep, playfield.Get(x, i) );
                    playfield.SetDestinationMap( x, i, -1 );
                }
            }// for i
        }// if
    }// dropTile


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void compress( Playfield playfield ) {
        int moveStep = 0;
        Boolean found = false;

        for( int i = playfield.GetWidth() - 1; i >= 0; i--) {
            if(  playfield.Get( i, playfield.GetHeight() - 1 ) == -1 ) {
                moveStep++;
                found = true;
            }// if
            else{
                if( found ){
                    for( int j=0; j<playfield.GetHeight(); j++) {
                        Point moveTo = playfield.GetMovemap(i, j);
                        moveTo.x += moveStep;
                        playfield.SetMovemap( i, j, moveTo );

                        playfield.SetDestinationMap( i + moveStep, j, playfield.Get( i, j ));
                        playfield.SetDestinationMap( i, j, -1);
                    }// for j
                }
            }// else
        }//for i
    }// alignColumn


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean isUpperColumn ( Playfield playfield, int x ,int y ) {
        if( y <= 0 ) return false;
        for( int i = y-1; i >= 0; i-- ) {
            if( playfield.Get( x, i ) != -1 )
                return true;
        }// for i
        return false;
    }// isUpperColumn


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean pairsLeft( Playfield playfield ) {
        int tile;

        for(int j=0; j < playfield.GetHeight(); j++) {
            for( int i=0; i<playfield.GetWidth(); i++) {
                tile = playfield.Get( i, j );

                if( tile >= 0 ) {
                    if( i < playfield.GetWidth() -1) {
                        if ( tile == playfield.Get( i+1, j ))
                            return true;
                    }// if

                    if( j < playfield.GetHeight() -1) {
                        if ( tile == playfield.Get( i, j+1 ) )
                            return true;
                    }// if
                }// if
            }// for i

        }// for j
        return false;
    }// pairsLeft


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int getScore() {
        return mScore;
    }// getScore


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean compareScore( int score ) {
        if( score < 0 ) {
            return true;
        }// if
        else {
            return (mScore <= score );
        }// else
    }// compareScore


    private void initMoveMap( Playfield playfield ) {
        for( int j=0; j< playfield.GetHeight(); j++ ) {
            for( int i=0; i< playfield.GetWidth(); i++ ) {
                playfield.SetMovemap(i, j, new Point( i, j ));
            }// for i
        }// for j

    }// initMoveMap


    public void syncMaps( Playfield playfield ) {
        for( int j=0; j< playfield.GetHeight(); j++ ) {
            for( int i=0; i< playfield.GetWidth(); i++ ) {
                playfield.Set( i, j , playfield.GetDestinationMap(i, j) );
                playfield.SetMovemap(i, j, new Point( i, j ));
            }// for i
        }// for j
    }
}
