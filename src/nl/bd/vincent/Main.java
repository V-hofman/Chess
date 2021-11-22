package nl.bd.vincent;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {


        Board board = new Board();

        try {
            Board.onCreate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
