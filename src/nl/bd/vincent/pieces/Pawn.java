package nl.bd.vincent.pieces;

import nl.bd.vincent.Board;
import nl.bd.vincent.Pieces;

import java.util.LinkedList;

public class Pawn extends Pieces {

    //The variables for the location on the board
    int xLocation, yLocation;
    int xDrawLoc, yDrawLoc;
    boolean isWhite;
    LinkedList<Pieces> pieces;
    String pieceType = "Pawn";

    public Pawn(int xLocation, int yLocation, boolean isWhite, String pieceType, LinkedList<Pieces> pieces){
        super(xLocation, yLocation, isWhite, pieceType, pieces);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        xDrawLoc = xLocation * 64;
        yDrawLoc = yLocation * 64;
        this.isWhite = isWhite;
        this.pieces = pieces;
        this.pieceType = pieceType;
        pieces.add(this);
    }

    public boolean move(int x, int y)
    {
        if(Board.getPiece(x * 64,y * 64) != null)
        {
            if(pawnStrike( x, y, Board.getPiece(x * 64,y * 64)))
            {
                this.xLocation = x;
                this.yLocation = y;
                return true;
            }

        }else
        {

            if((((!this.isWhite && y == this.yLocation + 1) || (this.isWhite && y == this.yLocation - 1)) && x == this.xLocation) ||
                    this.yLocation == 1 && (!this.isWhite && y == this.yLocation + 2) || this.yLocation == 6 && (this.isWhite && y == this.yLocation - 2))
            {
                this.xLocation = x;
                this.yLocation = y;
                return true;
            }
        }
        return false;
    }

    public boolean pawnStrike(int x, int y, Pieces killPiece)
    {
        if(killPiece != null)
        {
            return((((!this.isWhite && y == this.yLocation + 1) || (this.isWhite && y == this.yLocation - 1)) && (x == this.xLocation + 1 || x == this.xLocation - 1 )) ||
                    this.yLocation == 1 && (!this.isWhite && y == this.yLocation + 2) || this.yLocation == 6 && (this.isWhite && y == this.yLocation - 2));

        }else
        {
            return((((!this.isWhite && y == this.yLocation + 1) || (this.isWhite && y == this.yLocation - 1)) && x == this.xLocation) ||
                    this.yLocation == 1 && (!this.isWhite && y == this.yLocation + 2) || this.yLocation == 6 && (this.isWhite && y == this.yLocation - 2));
        }

    }


}
