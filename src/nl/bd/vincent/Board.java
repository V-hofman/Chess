package nl.bd.vincent;


import nl.bd.vincent.pieces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class Board {

    static LinkedList<Pieces> pieces = new LinkedList<>();
    public static Pieces selectedPiece = null;
    static byte turnCount = 1;
    static byte[] saveFile;
    static File file;

    public static void onCreate() throws IOException {

        System.out.println("\nThe board will be printed in the console after each attempted move \nThey will be displayed with a single Char\n" +
                "P = Pawn\nR = Rook\nB = Bishop\nH = Knight/Horse\nQ = Queen\nK = King\n\n");

        //Here we define 2 colors that we will use to get the checkered layout.
        Color dark = new Color(184,139,74);
        Color light = new Color(227,193,111);

        //Here we try to grab the image and cut it up into smaller parts. After which we set the index to link it later.
            BufferedImage pieceSprite = ImageIO.read(new File("./chess.png"));
            file = new File("./save");
            Image imgs[] = new Image[12];
            int index = 0;
            for(int y = 0; y<400; y+=200){
                for(int x=0;x<1200;x+=200){
                    imgs[index]= pieceSprite.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    index++;
                }
            }
            addPieces();

        //Here we create the frame which holds everything
        JFrame frame = new JFrame();
        frame.setBounds(10,10,512,512);
        frame.setUndecorated(true);

        //The actual panel that is drawn
        JPanel panel = new JPanel(){

            //The override will allow us to draw the checkered screen
            @Override
            public void paint(Graphics g) {
                //A boolean that helps us know which colors needs to be printed
                boolean darkSquare = false;

                //Nested for-loops that paint the board
                for (int y = 0; y < 8; y++) {

                    for (int x = 0; x < 8; x++) {

                        if (darkSquare) {
                            g.setColor(dark);
                        } else {
                            g.setColor(light);
                        }

                        g.fillRect(x * 64, y * 64, 64, 64);
                        //Switching colors is important
                        darkSquare = !darkSquare;
                    }
                    darkSquare = !darkSquare;
                }

                //Here the actual piece objects are linked to an image, so that we can draw them
                for(Pieces p: pieces){
                    int indexArray =0;
                    String tempName = p.pieceType.toLowerCase();
                    switch (tempName) {
                        case "king" -> indexArray = 0;
                        case "queen" -> indexArray = 1;
                        case "bishop" -> indexArray = 2;
                        case "knight" -> indexArray = 3;
                        case "rook" -> indexArray = 4;
                        case "pawn" -> indexArray = 5;
                    }
                    if(!p.isWhite){
                        indexArray+=6;
                    }
                    g.drawImage(imgs[indexArray], p.xDrawLoc, p.yDrawLoc, this);
                }
            }
        };

        // The initial text that will pop up displaying the help menu
        JLabel startText;
        startText = createLabel("Press 'H' for help!",128,64,192,192,new Color(100, 120, 250, 200),true,true);
        frame.add(startText);


        //The help screen text which won't show at the startup
        JLabel helpMenu;
        helpMenu = createLabel("<html>> R: Restart Match<br><br>> H: Toggle This Menu <br><br>> Esc: Exit</html>", 128, 128,
                192,192, new Color(100,100,100,200), true, true);
        helpMenu.setVisible(false);
        frame.add(helpMenu);

        //We need to add the panel to the frame
        panel.setPreferredSize(new Dimension(512,512));
        frame.getContentPane().add(panel);
        frame.pack();

        //Adding a Listener that looks for keyboard input
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {  // handler

                if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) { //Pressing escape will get rid of the frame, which in turn closes the application
                    frame.dispose();
                }
                if(ke.getKeyCode() == KeyEvent.VK_R){ //Pressing 'r' will remove and replace the pieces.
                    while(pieces.iterator().hasNext())
                    {
                        pieces.remove();
                    }
                    addPieces();
                    turnCount = 1;
                    consoleDisplay();
                    frame.repaint();
                }

                if(ke.getKeyCode() == KeyEvent.VK_S) { //Pressing 's' will save to file
                    if(whiteTurn(turnCount))
                    {
                        try {
                            System.out.println("Trying to save");
                            save(pieces);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("cant save");
                        }
                    }
                }

                if(ke.getKeyCode() == KeyEvent.VK_H) { //Pressing 'H' will toggle help menu
                    startText.setVisible(false);
                    startText.removeAll();
                    helpMenu.setVisible(!helpMenu.isVisible());

                }

                if(ke.getKeyCode() == KeyEvent.VK_L) { //Pressing 'l' will load from file
                    try {
                        while(pieces.iterator().hasNext())
                        {
                            pieces.remove();
                        }
                        pieces = (LinkedList<Pieces>) load();
                        frame.repaint();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        //Added another listener but for mouse movement input
        frame.addMouseMotionListener(new MouseMotionListener() {


            //If we have a piece selected and the mouse button held down we need to move the piece.
            @Override
            public void mouseDragged(MouseEvent e) {
                if(selectedPiece != null)
                {
                    //Since 0,0 is the corner of the image we render it with an off-set of 32 which is half the image.
                    selectedPiece.xDrawLoc = e.getX() -32;
                    selectedPiece.yDrawLoc = e.getY() -32;
                    frame.repaint();
                }

            }
            //Not doing anything here, but we need it
            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        //Now we add a listener for the mouse buttons
        frame.addMouseListener(new MouseListener() {
            //Not doing anything here, but we need it
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            //When we press the button we assign the piece that is located at the mouse cursor
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    selectedPiece = getPiece(e.getX(), e.getY());
                }
                catch (Exception er)
                {
                    er.printStackTrace();
                }
            }

            //When we let go of the mouse button this happens
            @Override
            public void mouseReleased(MouseEvent e) {
                if(selectedPiece != null) //We need to check if there is an actual object selected
                {
                    int newX = e.getX() / 64;
                    int newY = e.getY() / 64;
                    if(whiteTurn(turnCount) == selectedPiece.isWhite) //Check if the colored piece selected has their turn
                    {
                        if(selectedPiece.pieceType.equals("pawn") ) //Pawns require special annoying rules
                        {
                            if(isValidPawnStrike(selectedPiece, newX, newY, Board.getPiece(newX * 64, newY * 64)))
                            {
                                afterValidation( newX, newY, frame);
                            }else
                            {
                                returnPlace(selectedPiece);
                            }
                        }else
                        {
                            if(isValidMove(selectedPiece, newX, newY))
                            {
                                afterValidation(newX, newY, frame);
                            }else
                            {
                                returnPlace(selectedPiece);
                            }
                        }
                    }else
                    {
                        returnPlace(selectedPiece);
                    }
                    consoleDisplay();
                    frame.repaint();
                }
            }
            //Not doing anything here, but we need it
            @Override
            public void mouseEntered(MouseEvent e) {//Not doing anything here, but we need it
            }
            //Not doing anything here, but we need it
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        //This sets it so the program closes when the frame is closed
        frame.setDefaultCloseOperation(3);
        //We need to see the frame
        frame.setVisible(true);

    }

    public static void addPieces()
    {
        //While it says they aren't used they are. They are just added to a linked list upon creation
        Pieces wKing = new King(4,0,false,"king", pieces);
        Pieces bKing = new King(4,7,true,"king", pieces);
        Pieces wQueen = new Queen(3,0,false,"queen", pieces);
        Pieces bQueen = new Queen(3,7,true,"queen", pieces);

        Pieces wRook1 = new Rook(0,0,false,"rook", pieces);
        Pieces wRook2 = new Rook(7,0,false,"rook", pieces);

        Pieces bRook1 = new Rook(0,7,true,"rook", pieces);
        Pieces bRook2 = new Rook(7,7,true,"rook", pieces);

        Pieces wBishop1 = new Bishop(2,0,false,"bishop", pieces);
        Pieces wBishop2 = new Bishop(5,0,false,"bishop", pieces);

        Pieces bBishop1 = new Bishop(2,7,true,"bishop", pieces);
        Pieces bBishop2 = new Bishop(5,7,true,"bishop", pieces);

        Pieces wKnight1 = new Knight(1,0,false,"knight", pieces);
        Pieces wKnight2 = new Knight(6,0,false,"knight", pieces);

        Pieces bKnight1 = new Knight(1,7,true,"knight", pieces);
        Pieces bKnight2 = new Knight(6,7,true,"knight", pieces);

        for(int i = 0;i < 8; i++ ){
            Pieces wPawn = new Pawn(i, 1, false, "pawn", pieces);
            Pieces bPawn = new Pawn(i, 6, true, "pawn", pieces);
        }


    }
    //We use this to check if the selected color has the current turn
    public static boolean whiteTurn(byte turnCount)
    {
        return!(turnCount % 2 == 0);

    }


    //Here we make sure we can grab the piece
    public static Pieces getPiece(int x, int y){
        int xp = x/64;
        int yp = y/64;
        for(Pieces p: pieces)
        {
            if(p.xLocation == xp  &&  p.yLocation == yp)
            {
                return p;
            }
        }
        return null;
    }

    //This is used to check the move we want to make is valid
    public static boolean isValidMove(Pieces p, int x, int y)
    {
        switch (p.pieceType)
        {
            case "king":
                return (x == p.xLocation + 1 || x == p.xLocation - 1 || y == p.yLocation - 1 || y == p.yLocation + 1 );

            case "pawn":
                if(Board.getPiece(x,y) != null)
                {
                    return (isValidPawnStrike(p, x, y, Board.getPiece(x,y)));
                }else
                {
                    return ((((!p.isWhite && y == p.yLocation + 1) || (p.isWhite && y == p.yLocation - 1)) && x == p.xLocation) ||
                            p.yLocation == 1 && (!p.isWhite && y == p.yLocation + 2) || p.yLocation == 6 && (p.isWhite && y == p.yLocation - 2));
                }

            case "queen":
                return((x - p.xLocation == p.yLocation - y || x - p.xLocation == -(p.yLocation - y)) || x == p.xLocation || y == p.yLocation);

            case "bishop":
                return(x - p.xLocation == p.yLocation - y || x - p.xLocation == -(p.yLocation - y));
            case "rook":
                return (x == p.xLocation || y == p.yLocation);

            case "knight":
                return ((x - p.xLocation == -1 && y - p.yLocation == -2) || (x - p.xLocation == 1 && y - p.yLocation == -2) ||
                        (x - p.xLocation == 1 && y - p.yLocation == 2) || (x - p.xLocation == -1 && y - p.yLocation == 2) ||
                        (x - p.xLocation == 2 && y - p.yLocation == 1) || (x - p.xLocation == 2 && y - p.yLocation == -1) ||
                        (x - p.xLocation == -2 && y - p.yLocation == 1) || (x - p.xLocation == -2 && y - p.yLocation == -1));

        }
        return false;
    }

    //Pawn's have special rules because why not so we seperate part of them
    public static boolean isValidPawnStrike(Pieces p, int x, int y, Pieces killPiece)
    {
        if(killPiece != null)
        {
            return((((!p.isWhite && y == p.yLocation + 1) || (p.isWhite && y == p.yLocation - 1)) && (x == p.xLocation + 1 || x == p.xLocation - 1 )) ||
                    p.yLocation == 1 && (!p.isWhite && y == p.yLocation + 2) || p.yLocation == 6 && (p.isWhite && y == p.yLocation - 2));

        }else
        {
            return((((!p.isWhite && y == p.yLocation + 1) || (p.isWhite && y == p.yLocation - 1)) && x == p.xLocation) ||
                    p.yLocation == 1 && (!p.isWhite && y == p.yLocation + 2) || p.yLocation == 6 && (p.isWhite && y == p.yLocation - 2));
        }

    }

    //Here we actually place the piece at its new location
    public static void placeNew(Pieces p, int x, int y)
    {
        p.xLocation = x;
        p.yLocation = y;
        p.xDrawLoc = x * 64;
        p.yDrawLoc = y * 64;
    }

    //Incase something went wrong this is used to return the piece to where it was selected.
    public static void returnPlace(Pieces p)
    {
        p.xDrawLoc = p.xLocation * 64;
        p.yDrawLoc = p.yLocation * 64;
    }

    //We check if the piece is a king
    public static boolean checkWin(Pieces p)
    {
        return(p.pieceType.equals("king"));
    }

    //In here we actually do the piece removing
    public static void killPiece(Pieces p, int x, int y, Frame frame)
    {
        if(checkWin(Board.getPiece(x * 64,y * 64))) //We need to check if they win
        {
            if(selectedPiece.isWhite)
            {
                JOptionPane.showMessageDialog(null,  "WHITE HAS WON!","GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            }else
            {
                JOptionPane.showMessageDialog(null, "BLACK HAS WON!","GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            }
            frame.dispose();

        }
        Board.getPiece(x * 64,y * 64 ).KillPiece();
        pieces.remove(Board.getPiece(x * 64,y * 64));
    }

    //If the move is valid check for other pieces
    public static void afterValidation(int newX, int newY, Frame frame)
    {
        if(Board.getPiece(newX * 64, newY * 64) != null)
        {
            if(Board.getPiece(newX * 64,newY * 64 ).isWhite==selectedPiece.isWhite){
                returnPlace(selectedPiece);
            }else
            {
                if(Board.getPiece(newX * 64,newY * 64 ).isWhite!=selectedPiece.isWhite){
                        killPiece(selectedPiece, newX, newY, frame);
                        placeNew(selectedPiece, newX, newY);
                }
                turnCount++;
            }

        }else
        {
            turnCount++;
            placeNew(selectedPiece, newX, newY);
        }
    }

    //A base for creating a nice label
    public static JLabel createLabel(String text, int width, int height, int xPos, int yPos, Color background, boolean border, boolean Opaque)
    {
        JLabel tempLabel = new JLabel();
        tempLabel.setText(text);
        tempLabel.setOpaque(Opaque);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tempLabel.setBackground(background);
        tempLabel.setSize(width,height);
        tempLabel.setLocation(xPos,yPos);
        if(border)
        {
            tempLabel.setBorder(new LineBorder(Color.BLACK));
        }
        return  tempLabel;
    }

    //Here we print the current board to console
    public static void consoleDisplay()
    {
        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                if(j == 0)
                {
                    System.out.print("| ");
                }
                if(getPiece(j * 64,i * 64) == null)
                {
                    System.out.print(" ");
                }else
                {
                    if(getPiece(j * 64,i * 64).pieceType.equals("knight"))
                    {
                        System.out.print("H");
                    }else
                    {
                        System.out.print(String.valueOf(getPiece(j * 64,i * 64).pieceType.charAt(0)).toUpperCase(Locale.ROOT));
                    }
                }
                System.out.print(" | ");
            }
            System.out.print("\n");
        }
        System.out.println("================================= \n ");
    }

    //This will be used to save the objects to a file.
    public static void save(LinkedList<Pieces> pieces) throws IOException
    {
        if(Objects.nonNull(pieces))
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream os = new ObjectOutputStream(bos))
            {
                os.writeObject(pieces);
            }

            writeToFile(bos.toByteArray());
        }else
        {
            System.out.println("null");
        }
    }

    //In here we will load the file and objects again.
    public static Object load() throws IOException, ClassNotFoundException
    {
        if (file.exists()) {
            Path path = Paths.get("./save");
            byte[] data = Files.readAllBytes(path);
            if (Objects.nonNull(data)) {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInput in = new ObjectInputStream(bis);
                return in.readObject();
            }

        }
            System.out.println("Failed");
            return null;
    }

    public static void writeToFile(byte[] saveData)
    {
        try {


            // Initialize a pointer
            // in file using OutputStream
            OutputStream os = new FileOutputStream(file);
            // Starts writing the bytes in it
            if(file.exists())
            {
                file.delete();
            }
            os.write(saveData);

            // Close the file
            os.close();

        }

        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
