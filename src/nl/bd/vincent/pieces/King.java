package nl.bd.vincent.pieces;

import nl.bd.vincent.Pieces;

import java.util.LinkedList;

public class King extends Pieces {

    //The variables for the location on the board
    int xLocation, yLocation;
    int xDrawLoc, yDrawLoc;
    boolean isWhite;
    LinkedList<Pieces> pieces;
    String pieceType;

    public King(int xLocation, int yLocation, boolean isWhite, String pieceType, LinkedList<Pieces> pieces){
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
        System.out.println("king");
        if (x == this.xLocation + 1 || x == this.xLocation - 1 || y == this.yLocation - 1 || y == this.yLocation + 1 )
        {
            this.xLocation = x;
            this.yLocation = y;
            return true;
        }
        return false;
    }
}
