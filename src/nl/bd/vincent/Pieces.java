package nl.bd.vincent;

import java.io.Serializable;
import java.util.LinkedList;

public abstract class Pieces implements Serializable {

    //The variables for the location on the board
    int xLocation, yLocation;
    int xDrawLoc, yDrawLoc;
    boolean isWhite;
    LinkedList<Pieces> pieces;
    String pieceType;

    public Pieces(int xLocation, int yLocation, boolean isWhite, String pieceType, LinkedList<Pieces> pieces){
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.xDrawLoc = xLocation * 64;
        this.yDrawLoc = yLocation * 64;
        this.isWhite = isWhite;
        this.pieces = pieces;
        this.pieceType = pieceType;
        pieces.add(this);
    }

    public abstract boolean move(int x, int y);

    public void KillPiece()
    {
        pieces.remove(this);
    }

    public String getPieceType()
    {
        return this.pieceType;
    }
}

