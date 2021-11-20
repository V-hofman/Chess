package nl.bd.pieces;

import nl.bd.Pieces;

import java.util.LinkedList;

public class Rook extends Pieces {

    //The variables for the location on the board
    int xLocation, yLocation;
    int xDrawLoc, yDrawLoc;
    boolean isWhite;
    LinkedList<Pieces> pieces;
    String pieceType = "Rook";

    public Rook(int xLocation, int yLocation, boolean isWhite, String pieceType, LinkedList<Pieces> pieces){
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
}
