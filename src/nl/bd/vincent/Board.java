package nl.bd.vincent;


import nl.bd.vincent.pieces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board
{
    //Initialize a bunch of stuff we need
    static LinkedList<Pieces> pieces = new LinkedList<>();
    public static Pieces selectedPiece = null;
    static byte turnCount = 1;

    static FileHandler handler;
    static PawnHandler pHandler;

    //Instead of using a constructor I decided to throw it in a function if it needs to ever be called again.
    public static void onCreate() throws IOException
    {

        //Print some minor details to the console in case someone wants to keep track in there and also for debugging purposes
        System.out.println("""

                The board will be printed in the console after each attempted move\s
                They will be displayed with a single Char
                P = Pawn
                R = Rook
                B = Bishop
                H = Knight/Horse
                Q = Queen
                K = King

                """);

        /**
         ** ==================
         **   SAVE FILE STUFF
         ** ==================
         **/


        String filePath = "./saves/";
        while (filePath.equals("./saves/"))
        {
            //This regex string is used to validate the input in the pop-up
            String regex = "[a-z A-Z]{3,10}";
            Pattern p = Pattern.compile(regex);
            String userInput = nameInput();
            Matcher m = p.matcher(userInput);

            //It has to match the input, or we shall loop again
            if (m.matches())
            {
                filePath = "./saves/" + userInput;
            }
        }

        //Create the file and the handler object we use
        handler = new FileHandler(filePath);
        pHandler = new PawnHandler();


        /**
         ** ==================
         **   CHESS PIECES
         ** ==================
         **/

        //Try to load the actual image
        BufferedImage pieceSprite = null;
        try
        {
            pieceSprite = ImageIO.read(new File("./chess.png"));
        } catch (Exception e)
        {
            //If anything goes wrong print in the console and display a pop-up for the user
            e.printStackTrace();
            System.out.println("Image file is missing!");
            JOptionPane.showMessageDialog(null, "Image file is missing!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        //Here we create an array of images that we use.
        Image[] imgs = new Image[12];
        int index = 0;

        //Here we do the actual cutting part
        for (int y = 0; y < 400; y += 200)
        {
            for (int x = 0; x < 1200; x += 200)
            {
                try
                {
                    imgs[index] = pieceSprite.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Image file is corrupt!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }

                index++;
            }
        }
        //Now that we have the image cut we start creating the pieces.
        pHandler.addPieces(pieces);

        //Here we create the frame which holds everything
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 512, 512);
        frame.setUndecorated(true);

        /**
         ** =============================
         **   Board Creation
         ** =============================
         ****/

        //Here we define 2 colors that we will use to get the checkered layout.
        Color dark = new Color(184, 139, 74);
        Color light = new Color(227, 193, 111);


        //The actual panel that is drawn
        JPanel panel = new JPanel()
        {

            //The override will allow us to draw the checkered screen
            @Override
            public void paint(Graphics g)
            {
                //A boolean that helps us know which colors needs to be printed
                boolean darkSquare = false;

                //Nested for-loops that paint the board
                for (int y = 0; y < 8; y++)
                {

                    for (int x = 0; x < 8; x++)
                    {

                        if (darkSquare)
                        {
                            g.setColor(dark);
                        } else
                        {
                            g.setColor(light);
                        }

                        g.fillRect(x * 64, y * 64, 64, 64);
                        //Switching colors is important
                        darkSquare = !darkSquare;
                    }
                    //got to make sure to switch when we go to the next layer as well
                    darkSquare = !darkSquare;
                }

                //Here the actual piece objects are linked to an image, so that we can draw them
                for (Pieces p : pieces)
                {
                    int indexArray = 0;
                    String tempName = p.pieceType.toLowerCase();
                    switch (tempName)
                    {
                        case "king" -> indexArray = 0;
                        case "queen" -> indexArray = 1;
                        case "bishop" -> indexArray = 2;
                        case "knight" -> indexArray = 3;
                        case "rook" -> indexArray = 4;
                        case "pawn" -> indexArray = 5;
                    }
                    if (!p.isWhite)
                    {
                        indexArray += 6;
                    }
                    g.drawImage(imgs[indexArray], p.xDrawLoc, p.yDrawLoc, this);
                }
            }
        };

        /**
         ** =============================
         **   Label creation
         ** =============================
         ****/

        // The initial text that will pop up displaying the help menu
        JLabel startText;
        startText = createLabel("Press 'H' for help!", 128, 64, 192, 192, new Color(100, 120, 250, 200), true, true);
        frame.add(startText);


        //The help screen text which won't show at the startup
        JLabel helpMenu;
        helpMenu = createLabel("<html>> S: Save Match<br>> L: Load Save<br><br>> R: Restart Match<br>> H: Toggle This Menu <br><br>> Esc: Exit</html>", 128, 128,
                192, 192, new Color(100, 100, 100, 200), true, true);
        helpMenu.setVisible(false);
        frame.add(helpMenu);

        //We need to add the panel to the frame
        panel.setPreferredSize(new Dimension(512, 512));
        frame.getContentPane().add(panel);
        frame.pack();

        /**
         ** =============================
         **   Input Handling
         ** =============================
         ****/


        //Adding a Listener that looks for keyboard input
        frame.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent ke)
            {  // handler

                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
                { //Pressing escape will get rid of the frame, which in turn closes the application
                    frame.dispose();
                    System.exit(0);
                }
                if (ke.getKeyCode() == KeyEvent.VK_R)
                { //Pressing 'r' will remove and replace the pieces.
                    while (pieces.iterator().hasNext())
                    {
                        pieces.remove();
                    }
                    pHandler.addPieces(pieces);
                    turnCount = 1;
                    consoleDisplay();
                    frame.repaint();
                }

                if (ke.getKeyCode() == KeyEvent.VK_S)
                { //Pressing 's' will save to file
                    if (pHandler.whiteTurn(turnCount))
                    {
                        String passString = getLayoutString();
                        try
                        {
                            handler.saveToFile(passString);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if (ke.getKeyCode() == KeyEvent.VK_H)
                { //Pressing 'H' will toggle help menu
                    startText.setVisible(false);
                    startText.removeAll();
                    helpMenu.setVisible(!helpMenu.isVisible());

                }

                if (ke.getKeyCode() == KeyEvent.VK_L)
                { //Pressing 'l' will load from file

                    while (pieces.iterator().hasNext())
                    {
                        pieces.remove();
                    }
                    try
                    {
                        String passString = handler.loadFile();
                        orderBoard(passString);
                        frame.repaint();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Save file corrupted!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                        pHandler.addPieces(pieces);
                        turnCount = 1;
                        consoleDisplay();
                        frame.repaint();

                    }
                }
            }
        });

        /**
         ** =============================
         **   Mouse Input
         ** =============================
         ****/

        //Added another listener but for mouse movement input
        frame.addMouseMotionListener(new MouseMotionListener()
        {

            //If we have a piece selected and the mouse button held down we need to move the piece.
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (selectedPiece != null)
                {
                    //Since 0,0 is the corner of the image we render it with an off-set of 32 which is half the image.
                    selectedPiece.xDrawLoc = e.getX() - 32;
                    selectedPiece.yDrawLoc = e.getY() - 32;
                    frame.repaint();
                }

            }

            //Not doing anything here, but we need it
            @Override
            public void mouseMoved(MouseEvent e)
            {

            }
        });

        //Now we add a listener for the mouse buttons
        frame.addMouseListener(new MouseListener()
        {
            //Not doing anything here, but we need it
            @Override
            public void mouseClicked(MouseEvent e)
            {

            }

            //When we press the button we assign the piece that is located at the mouse cursor
            @Override
            public void mousePressed(MouseEvent e)
            {
                try
                {
                    selectedPiece = pHandler.getPiece(pieces, e.getX(), e.getY());
                } catch (Exception er)
                {
                    er.printStackTrace();
                }
            }

            //When we let go of the mouse button this happens
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (selectedPiece != null) //We need to check if there is an actual object selected
                {
                    int newX = e.getX() / 64;
                    int newY = e.getY() / 64;
                    if (pHandler.whiteTurn(turnCount) == selectedPiece.isWhite) //Check if the colored piece selected has their turn
                    {
                        if (selectedPiece.move(newX, newY))
                        {
                            afterValidation(newX, newY, frame);
                        } else
                        {
                            pHandler.returnPlace(selectedPiece);
                        }

                    } else
                    {
                        System.out.println("Not your turn");

                        pHandler.returnPlace(selectedPiece);
                    }
                    consoleDisplay();
                    frame.repaint();
                }
            }

            //Not doing anything here, but we need it
            @Override
            public void mouseEntered(MouseEvent e)
            {//Not doing anything here, but we need it
            }

            //Not doing anything here, but we need it
            @Override
            public void mouseExited(MouseEvent e)
            {
            }
        });

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //We need to see the frame
        frame.setVisible(true);
    }

    //If the move is valid check for other pieces
    public static void afterValidation(int newX, int newY, Frame frame)
    {
        if (pHandler.getPiece(pieces, newX * 64, newY * 64) != null)
        {
            if (pHandler.getPiece(pieces, newX * 64, newY * 64).isWhite == selectedPiece.isWhite)
            {
                pHandler.returnPlace(selectedPiece);
            } else
            {
                if (pHandler.getPiece(pieces, newX * 64, newY * 64).isWhite != selectedPiece.isWhite)
                {
                    pHandler.killPiece(newX, newY, frame, pieces, selectedPiece);
                    pHandler.placeNew(selectedPiece, newX, newY);
                }
                turnCount++;
            }

        } else
        {
            turnCount++;
            pHandler.placeNew(selectedPiece, newX, newY);
        }
    }

    /**
     * * =============================
     * *   Label Creation
     * * =============================
     ****/

    //A base for creating a nice label
    public static JLabel createLabel(String text, int width, int height, int xPos, int yPos, Color background, boolean border, boolean Opaque)
    {
        JLabel tempLabel = new JLabel();
        tempLabel.setText(text);
        tempLabel.setOpaque(Opaque);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tempLabel.setBackground(background);
        tempLabel.setSize(width, height);
        tempLabel.setLocation(xPos, yPos);
        if (border)
        {
            tempLabel.setBorder(new LineBorder(Color.BLACK));
        }
        return tempLabel;
    }

    /**
     * * =============================
     * *   Console Printing
     * * =============================
     ****/

    //Here we print the current board to console
    public static void consoleDisplay()
    {
        System.out.println("Round number: " + turnCount);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (j == 0)
                {
                    System.out.print("| ");
                }
                if (pHandler.getPiece(pieces, j * 64, i * 64) == null)
                {
                    System.out.print(" ");
                } else
                {
                    if (pHandler.getPiece(pieces, j * 64, i * 64).pieceType.equals("knight"))
                    {
                        System.out.print("H");
                    } else
                    {
                        System.out.print(String.valueOf(pHandler.getPiece(pieces, j * 64, i * 64).pieceType.charAt(0)).toUpperCase(Locale.ROOT));
                    }
                }
                System.out.print(" | ");
            }
            System.out.print("\n");
        }
        System.out.println("================================= \n ");
    }

    /**
     * * ==========================================================
     * *   Creating and Reading Strings from the save files
     * * =======================================================
     ****/

    public static String getLayoutString()
    {
        char tempChar;
        StringBuilder tempString = new StringBuilder();

        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                try
                {
                    selectedPiece = pHandler.getPiece(pieces, x * 64, y * 64);
                    if (selectedPiece.getPieceType().equals("knight"))
                    {
                        tempChar = 'h';
                    } else
                    {
                        tempChar = selectedPiece.getPieceType().charAt(0);
                    }
                    if (selectedPiece.isWhite)
                    {
                        tempChar = Character.toLowerCase(tempChar);
                    } else
                    {
                        tempChar = Character.toUpperCase(tempChar);
                    }
                } catch (Exception er)
                {
                    tempChar = 'x';
                }
                tempString.append(tempChar);
            }
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(tempString.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void orderBoard(String loadString)
    {
        int yPlace = 0;
        if (loadString.length() / 64 == 1)
        {
            System.out.println("Corrupted save file!");
        } else
        {
            for (int i = 0; i < loadString.length(); i++)
            {
                if (i % 8 == 0 && i != 0)
                {
                    yPlace++;
                }
                if (loadString.charAt(i) != 'x')
                {
                    switch (loadString.charAt(i))
                    {
                        case 'K':
                            King wKing = new King(i % 8, yPlace, false, "king", pieces);
                            break;
                        case 'Q':
                            Queen wQueen = new Queen(i % 8, yPlace, false, "queen", pieces);
                            break;
                        case 'B':
                            Bishop wBishop = new Bishop(i % 8, yPlace, false, "bishop", pieces);
                            break;
                        case 'H':
                            Knight wKnight = new Knight(i % 8, yPlace, false, "knight", pieces);
                            break;
                        case 'R':
                            Rook wRook = new Rook(i % 8, yPlace, false, "rook", pieces);
                            break;
                        case 'P':
                            Pawn wPawn = new Pawn(i % 8, yPlace, false, "pawn", pieces);
                            break;
                        case 'k':
                            King bKing = new King(i % 8, yPlace, true, "king", pieces);
                            break;
                        case 'q':
                            Queen bQueen = new Queen(i % 8, yPlace, true, "queen", pieces);
                            break;
                        case 'b':
                            Bishop bBishop = new Bishop(i % 8, yPlace, true, "bishop", pieces);
                            break;
                        case 'h':
                            Knight bKnight = new Knight(i % 8, yPlace, true, "knight", pieces);
                            break;
                        case 'r':
                            Rook bRook = new Rook(i % 8, yPlace, true, "rook", pieces);
                            break;
                        case 'p':
                            Pawn bPawn = new Pawn(i % 8, yPlace, true, "pawn", pieces);
                            break;
                        case 'x':
                            break;
                        default:
                            System.out.println("Corrupted file!");
                            break;
                    }
                }
            }
        }
    }

    /**
     * * =============================
     * *   Username creation
     * * =============================
     ****/

    public static String nameInput()
    {
        JFrame jFrame = new JFrame();
        String getMessage = null;
        //While the input is empty or cancelled try again.
        while (getMessage == null)
        {
            getMessage = JOptionPane.showInputDialog(jFrame, "Enter a player name! \n - Between 3 - 10 Characters \n - Must be only letters");
        }
        return getMessage;
    }
}
