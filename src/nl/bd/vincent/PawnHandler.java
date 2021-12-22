package nl.bd.vincent;

import nl.bd.vincent.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class PawnHandler
{
    public void addPieces(LinkedList<Pieces> pieces)
    {
        //While it says they aren't used they are. I have no idea why.
        /** !!!!!DON'T REMOVE THIS PART!!!!!*/

        King wKing = new King(4, 0, false, "king", pieces);
        King bKing = new King(4, 7, true, "king", pieces);
        Queen wQueen = new Queen(3, 0, false, "queen", pieces);
        Queen bQueen = new Queen(3, 7, true, "queen", pieces);

        Rook wRook1 = new Rook(0, 0, false, "rook", pieces);
        Rook wRook2 = new Rook(7, 0, false, "rook", pieces);

        Rook bRook1 = new Rook(0, 7, true, "rook", pieces);
        Rook bRook2 = new Rook(7, 7, true, "rook", pieces);

        Bishop wBishop1 = new Bishop(2, 0, false, "bishop", pieces);
        Bishop wBishop2 = new Bishop(5, 0, false, "bishop", pieces);

        Bishop bBishop1 = new Bishop(2, 7, true, "bishop", pieces);
        Bishop bBishop2 = new Bishop(5, 7, true, "bishop", pieces);

        Knight wKnight1 = new Knight(1, 0, false, "knight", pieces);
        Knight wKnight2 = new Knight(6, 0, false, "knight", pieces);

        Knight bKnight1 = new Knight(1, 7, true, "knight", pieces);
        Knight bKnight2 = new Knight(6, 7, true, "knight", pieces);

        for (int i = 0; i < 8; i++)
        {
            Pawn wPawn = new Pawn(i, 1, false, "pawn", pieces);
            Pawn bPawn = new Pawn(i, 6, true, "pawn", pieces);
        }
    }

    public boolean whiteTurn(byte turnCount)
    {
        return !(turnCount % 2 == 0);
    }


    //Here we make sure we can grab the piece
    public Pieces getPiece(LinkedList<Pieces> pieces, int x, int y)
    {
        int xp = x / 64;
        int yp = y / 64;
        for (Pieces p : pieces)
        {
            if (p.xLocation == xp && p.yLocation == yp)
            {
                return p;
            }
        }
        return null;
    }

    //Here we actually place the piece at its new location
    public void placeNew(Pieces p, int x, int y)
    {
        p.xLocation = x;
        p.yLocation = y;
        p.xDrawLoc = x * 64;
        p.yDrawLoc = y * 64;
    }

    //In case something went wrong this is used to return the piece to where it was selected.
    public void returnPlace(Pieces p)
    {
        p.xDrawLoc = p.xLocation * 64;
        p.yDrawLoc = p.yLocation * 64;
    }

    //In here we actually do the piece removing
    public void killPiece(int x, int y, Frame frame, LinkedList<Pieces> pieces, Pieces selectedPiece)
    {
        if (getPiece(pieces, x * 64, y * 64) != null)
        {
            if (getPiece(pieces, x * 64, y * 64).getPieceType().equals("king")) //We need to check if they win
            {
                if (selectedPiece.isWhite)
                {
                    JOptionPane.showMessageDialog(null, "WHITE HAS WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
                } else
                {
                    JOptionPane.showMessageDialog(null, "BLACK HAS WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
                }
                frame.dispose();
            }
            getPiece(pieces, x * 64, y * 64).KillPiece();
            pieces.remove(getPiece(pieces, x * 64, y * 64));
        }
    }
}
