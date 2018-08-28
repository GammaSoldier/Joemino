package com.joekoperski.joemino;

import java.util.Random;

public class GameRules {

    public final int GAMEOVER = 0;
    public final int CONTINUE = 1;
    public final int FORBIDDEN = 2;

    private int mScore;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public GameRules( Playfield playfield ) {
        mScore = playfield.GetHeight() * playfield.GetWidth();

        Random rnd = new Random();

        for( int j=0; j< playfield.GetHeight(); j++ ) {
            for( int i=0; i< playfield.GetWidth(); i++ ) {
                playfield.Set( i, j , rnd.nextInt( playfield.GetNumTiles()) );
            }// for i
        }// for j
    }// GameRules


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int makeMove( Playfield playfield, int x, int y ) {
        int actualTile;
        int retVal = 1;
        int removed = 0;
        // is validTile
        if ( playfield.isValid(x, y) ) {
            actualTile = playfield.Get( x, y );
            removed = deleteNeighbours( playfield, x, y );
            if( removed == 1 ){
                playfield.Set( x, y, actualTile );
                retVal = FORBIDDEN;
            }// if
            else {
                mScore -= removed;
                dropColumn( playfield );
                compress( playfield );
                if( !pairsLeft( playfield ) ) {
                    retVal = GAMEOVER;
                }// if
                else {
                    retVal = CONTINUE;
                }// else
            }// else
        }// if
        return retVal;
    }// makeMove


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int deleteNeighbours( Playfield playfield, int x, int y ) {

        int numTiles = 0;
        int actualTile = playfield.Get( x, y );

        playfield.Set( x, y, -1 );

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
        for( int j = playfield.GetHeight() - 1; j >= 0; j-- ) {
            for( int i = 0; i < playfield.GetWidth(); i++ ) {
                if( playfield.Get( i, j) == -1 ) {
                    if( isUpperColumn( playfield, i, j ) ) {
                        dropTile( playfield, i, j );
                    }// if
                }// if
            }//  for i
        }// for j
    }// dropColumn


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean isUpperColumn ( Playfield playfield, int x ,int y ) {
        if( y <= 0 ) return false;
        for( int i = y-1; i >= 0; i-- ) {
            if( playfield.Get( x, i ) != -1 )
                return true;
        }// for i
        return false;

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean dropTile( Playfield playfield, int x, int y ) {
        Boolean retVal = false;
        int tile = playfield.Get( x, y - 1);

        if( y > 0 ) {
            if( tile == -1 ) {
                dropTile( playfield, x, y - 1 );
            }//if
            retVal = true;
            playfield.Set( x, y, playfield.Get( x, y - 1) );
            playfield.Set( x,y-1,-1 );
        }// if
        return retVal;
    }// dropTile


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void compress( Playfield playfield ) {
        for( int i =playfield.GetWidth() - 1; i > 0; i--) {
            if(  playfield.Get( i, playfield.GetHeight() - 1 ) == -1 ) {
                alignColumn( playfield, i );
            }// if
        }//for i
    }// compress


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void alignColumn( Playfield playfield, int i ) {
        if( i > 0) {
            if( playfield.Get( i-1, playfield.GetHeight() - 1 ) == -1 ) {
                alignColumn( playfield, i-1 );
            }//if

            for( int j=0; j<playfield.GetHeight(); j++) {
                playfield.Set( i, j, playfield.Get( i-1, j ));
                playfield.Set( i-1, j, -1 );
            }// for j
        }// if
    }// alignColumn


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
        if( mScore > score ) {
            return false;
        }// if
        return true;
    }// compareScore

}
