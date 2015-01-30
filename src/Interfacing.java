import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Interfacing class that is a layer between our BOT and the game engine. This
 * class also does the validation
 */
public class Interfacing
{

    /**
     * Input from the game engine.
     */
    private static Reader input = new BufferedReader(new InputStreamReader(
            System.in));

    /**
     * Sends a message to the game engine.
     * 
     * @param msg
     *            The message.
     */
    private static void sendMsg(String msg)
    {
        System.out.println(msg);
        System.out.flush();
    }

    /**
     * Receives a message from the game engine. Messages are terminated by a
     * '\n' character.
     * 
     * @return The message.
     * @throws IOException
     *             if there has been an I/O error.
     */
    private static String recvMsg() throws IOException, EOFException
    {
        StringBuilder message = new StringBuilder();
        int newCharacter;

        try
        {
            do
            {
                newCharacter = input.read();
                if (newCharacter == -1)
                    throw new EOFException("This is a stupid error");
                message.append((char) newCharacter);
            } while ((char) newCharacter != '\n');
        } 
	catch (Exception E)
        {
            System.out.println("Stupid error 1");
        }

        return message.toString();
    }

    /**
     * This method interprets the message to "something" that our agent can
     * understand.
     * 
     * @param s
     *            Message from game engine
     * 
     * @return An (Object) Message (see Message class for more details)
     * @throws IOException
     */
    public static Message getAndInterpret()
    {
        Message m;
        String s = null;

        try
        {
            s = recvMsg();
        } 
	catch (Exception e)
        {
            System.out.println("Error 2");
        }

        // interpret type of message
        switch (s.charAt(0))
        {
        // START message
        // return a message (START type), the initial game status, and who
        // to make a move first.
            case 'S':
                int[][] initState =
                {
                { 0, 7, 7, 7, 7, 7, 7, 7 },
                { 0, 7, 7, 7, 7, 7, 7, 7 } };
                if (s.charAt(6) == 'S')
                    m = new Message(MessageType.START, -1, initState, 0);
                else
                    m = new Message(MessageType.START, -1, initState, 1);
                break;

            // CHANGE message, so we know that either we or the opponent has
            // made a move
            // return a message (CHANGE type), with status of the game, last
            // move, and who to make a move next
            // if opponent made a SWAP, return a message of SWAP type (instead
            // of CHANGE).
            case 'C':
                if (s.charAt(7) == 'S')
                    m = new Message(MessageType.SWAP, 0, null, 0);
                else
                {

                    int[] south = new int[7];
                    int[] north = new int[7];

                    String[] s1 = s.split(";");
                    String[] s2 = s1[2].split(",");

                    // last move
                    int lastMove = Integer.parseInt(s1[1]);

                    // my table
                    int j = 0;
                    for (int i = 8; i <= 14; i++)
                    {
                        south[j] = Integer.parseInt(s2[i]);
                        j++;
                    }

                    // opponent table
                    j = 0;
                    for (int i = 0; i <= 6; i++)
                    {
                        north[j] = Integer.parseInt(s2[i]);
                        j++;
                    }

                    // my pot
                    int southPot = Integer.parseInt(s2[15]);

                    // opponent pot
                    int northPot = Integer.parseInt(s2[7]);

                    // my move
                    int currentPlayer;
                    if (s1[3].charAt(0) == 'O')
                        currentPlayer = 1;
                    else
                        currentPlayer = 0;

                    // create message
                    int[][] curState = new int[2][8];
                    curState[0][0] = southPot;
                    curState[1][0] = northPot;
                    for (int i = 1; i < 8; i++)
                    {
                        curState[0][i] = south[i - 1];
                        curState[1][i] = north[i - 1];
                    }
                    m = new Message(MessageType.CHANGE, lastMove, curState,
                            currentPlayer);
                }
                break;

            // END message, so we know that the game has ended (for whatever
            // reasons)
            // return a message (END type)
            default:
                m = new Message(MessageType.END, -1, null, 1);
                break;
        }

        return m;
    }

    /**
     * This method interpret a move to a string message and send it to game
     * engine. If the move is not valid, throw an exception
     * 
     * @param move
     *            A move (can be a integer from 1 to 7, i assume that 0 mean
     *            "SWAP")
     * 
     * @param m
     *            Current state of the game
     */
    public static void InterpretAndSend(int move)
    {
        String MessageSentToGameEngine;
        if (move == 0)
            MessageSentToGameEngine = "SWAP";
        else
            MessageSentToGameEngine = "MOVE;" + move;

        sendMsg(MessageSentToGameEngine);
    }
}
