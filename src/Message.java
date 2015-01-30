/**
 * Class that represents the message sent from game engine It can also represent
 * a state of the game
 */
public class Message
{

    // type of message
    private MessageType type;
    // last move that has been made
    private int lastMove;
    // state of the game
    private int[][] curState;
    // whether this is our turn to make a move
    // 0 = our turn
    private int currentPlayer;

    // constructor
    public Message(MessageType type, int lastMove, int[][] curState,
            int currentPlayer)
    {
        this.type = type;
        this.lastMove = lastMove;
        this.currentPlayer = currentPlayer;
        this.curState = curState;
    }

    // GETTER METHODS

    public MessageType getMessageType()
    {
        return type;
    }

    public int getLastMove()
    {
        return lastMove;
    }

    public int[][] getCurState()
    {
        return curState;
    }

    public int getCurrentPlayer()
    {
        return currentPlayer;
    }
}