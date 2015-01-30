/**
 * Class that represents an AI bot that can play a game of Kalah. It uses Monte
 * Carlo Tree Search to decide it's next move at a given state in the game tree.
 */
public class KalahBot
{
    // The bot's player number (either 0 or 1).
    private int playerNumber;

    /**
     * Constructor for a KalahBot.
     *
     * @param playerNumber
     *            The bot's player number (either 0 or 1).
     */
    public KalahBot(int playerNumber)
    {
        this.playerNumber = playerNumber;
    }

    /**
     * Method that executes a play of the game Kalah. Takes a player number that
     * determines if the bot is moving first or second.
     */
    public void play()
    {
        // The next move that the bot will make.
        Node<NodeData> moveToMake;

        // Boolean value that is set the false when the game has finished.
        // Determines when the bot should stop playing.
        boolean gameNotFinished = true;

        // Represents the current state that the game is in i.e. The current
        // node we are at in the game tree.
        Node<NodeData> currentNode = Utilities.initState(Utilities
                .toggle(this.playerNumber));

        // If we are player 2, wait for player 1 to move.
        if (playerNumber == 1)
        {
            currentNode = waitForOpponent(null);
        }

        // Play out the game.
        while (gameNotFinished)
        {
            // Choose the best move to make.
            moveToMake = MonteCarloTreeSearch.mcts(currentNode);

            executeMove(moveToMake);

            currentNode = waitForOpponent(moveToMake.getData().getState());

            // End the game if we receive the end game message.
            if (currentNode == null)
                gameNotFinished = false;
        }
    }

    /**
     * Method that executes the bot's next move. Takes the move to execute as
     * input and returns a boolean stating whether the move was able to be
     * executed or not.
     *
     * @param move
     *            The move to be executed.
     */
    private void executeMove(Node<NodeData> move)
    {
        Interfacing.InterpretAndSend(move.getData().getPitNumber());
    }

    /**
     * Method that waits for the opponent to make his/her/it's move. Returns the
     * new state the game is in.
     *
     * @return newState The new state that the game is in.
     */
    private Node<NodeData> waitForOpponent(int[][] curState)
    {
        Node<NodeData> n;

        // Will check if it's our turn again.
        Message m = Interfacing.getAndInterpret();
        while (m.getCurrentPlayer() != 0)
        {
            m = Interfacing.getAndInterpret();
        }

        if (m.getMessageType() == MessageType.END)
	{
            n = null;
	}
        else if (m.getMessageType() == MessageType.CHANGE)
        {
            curState = m.getCurState();
            int player = Utilities.toggle(playerNumber);
            NodeData data = new NodeData(curState, player);
            n = new Node<NodeData>(data);

        } 
	else
        {
	    // Swap.
            for (int i = 0; i < 8; i++)
            {
                int temp = curState[0][i];
                curState[0][i] = curState[1][i];
                curState[1][i] = temp;
            }

            NodeData data = new NodeData(curState, playerNumber);
            playerNumber = Utilities.toggle(playerNumber);
            n = new Node<NodeData>(data);
        }

        return n;
    }

}
