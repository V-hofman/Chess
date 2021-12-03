package nl.bd.vincent.pieces;

import nl.bd.vincent.Pieces;

import java.util.LinkedList;

public class Bishop extends Pieces {

    //The variables for the location on the board
    int xLocation, yLocation;
    int xDrawLoc, yDrawLoc;
    boolean isWhite;
    LinkedList<Pieces> pieces;
    String pieceType = "Bishop";

    public Bishop(int xLocation, int yLocation, boolean isWhite, String pieceType, LinkedList<Pieces> pieces){
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
        System.out.println("Bishop");
        if(x - this.xLocation == this.yLocation - y || x - this.xLocation == -(this.yLocation - y))
        {
            this.xLocation = x;
            this.yLocation = y;
            return true;
        }
        return false;
    }
}
