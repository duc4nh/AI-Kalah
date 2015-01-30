import java.util.List;
import java.util.ArrayList;

/**
 * Class that contains a set of utility functions to aid the KalahBot
 * implementation.
 */
public class Utilities
{
    private static boolean firstMove = true;
    private static boolean considerSwapForSimulate = false;

    /**
     * Method that gets a list of Nodes that represents the available valid
     * moves from a given node.
     *
     * @param parent
     *            The provided node.
     *
     * @return validMoves A list of valid moves from this state.
     */
    public static List<Node<NodeData>> getValidMoves(Node<NodeData> parent)
    {
        List<Node<NodeData>> validMoves = new ArrayList<Node<NodeData>>();

        // Get the current state of this node.
        int[][] state = parent.getData().getState();
	
        // Get the children of the parent for later comparisons.
        List<Node<NodeData>> children = parent.getChildren();

        // Get the player that made the last move and whether that player
        // should move again.
        int prevPlayer = parent.getData().getPlayer();
        boolean playerMoveAgain = parent.getData().getPlayerMoveAgain();

        // Work out the player actually making the next move.
        int player = playerMoveAgain ? prevPlayer : toggle(prevPlayer);

        // Stores the number of stones at a particular pit.
        int numStones = 0;

        // Truth value that is updated if we find a child state has already
        // been created.
        boolean dupStateFound = false;

        // Consider a possible SWAP move.
        if ((firstMove && player == 1) || considerSwapForSimulate)
        {
            int[][] curState = new int[2][8];

            for (int i = 0; i < 2; i++)
                for (int j = 0; j < parent.getData().getState()[i].length; j++)
                    curState[i][j] = parent.getData().getState()[toggle(i)][j];

            NodeData data = new NodeData(curState, player);
            Node<NodeData> n = new Node<NodeData>(data);

            n.setParent(parent);
            n.getData().setPlayerMoveAgain(false);
            n.getData().setNumSimulations(0);
            n.getData().setNumWins(0);
            n.getData().setPitNumber(0);

            validMoves.add(n);

            considerSwapForSimulate = false;
        } 
	else if (firstMove && player == 0)
        {
            considerSwapForSimulate = true;
        }

        // Go through the player's pits and check if there's a valid
        // move available i.e. There are stones in the pit.
        for (int i = 1; i < 8; i++)
        {
            // Get the number of stones at this pit.
            numStones = state[player][i];

            // If there are stones in this pit, we have found a valid move.
            if (numStones > 0)
            {
                // First check if we have already added this child as we don't
                // need to create another node for that case.
                int j = 0;
                while (!dupStateFound && j < children.size())
                {
                    if (children.get(j++).getData().getPitNumber() == i)
                    {
                        dupStateFound = true;
                        j--;
                    }
                }

                if (dupStateFound)
                {
                    validMoves.add(children.get(j));
                    dupStateFound = false;
                } 
		else
                {
                    validMoves.add(createNode(parent, numStones, i, player));
                }
            }
        }

        // No longer potential swaps.
        if (firstMove)
            firstMove = false;

        return validMoves;
    }

    /**
     * Method that creates a node that represents a possible move for a player
     * to make from a provided node.
     *
     * @param parent
     *            The provided node.
     * @param numStones
     *            The number of stones in the pit selected.
     * @param pit
     *            The pit selected.
     * @param player
     *            The player who will be making the move.
     *
     * @return n A node representing a possible move from the given node.
     */
    private static Node<NodeData> createNode(Node<NodeData> parent,
            int numStones, int pit, int player)
    {
        // Create a node that will represent the state after executing the move
        // selected.
        int[][] curState = parent.getData().getState();

        NodeData data = new NodeData(curState, player);
        Node<NodeData> n = new Node<NodeData>(data);

        // Initialize node data
        // (Set the number of stones for the pit selected to 0).
        n.setParent(parent);
        n.getData().setState(player, pit, 0);
        n.getData().setPlayer(player);
        n.getData().setPlayerMoveAgain(false);
        n.getData().setNumSimulations(0);
        n.getData().setNumWins(0);
        n.getData().setPitNumber(pit);

        // Calculate the first pit that will have stones placed inside it.
        if (++pit > 7)
            pit = 0;

        // Temporary player variable to maintain the knowledge of the player
        // that actually made this move.
        int tempPlayer = player;

        // Work out the state of the node by executing the move.
        for (int i = 0; i < numStones; i++)
        {
            // Add a stone to that pit.
            int curVal = n.getData().getState()[tempPlayer][pit];
            n.getData().setState(tempPlayer, pit, ++curVal);

            // Check if we are to cycle round to our points pit or our
            // opponent's pits.
            if (pit == 0)
            {
                // We are now looking at the opponent's pits.
                tempPlayer = toggle(tempPlayer);
                pit = 1;
            } 
	    else if (++pit > 7)
            {
                if (tempPlayer == player)
                {
                    // We are now looking at the player's points pit.
                    pit = 0;
                } 
		else
                {
                    // We don't want to add stones to our opponent's points pit.
                    tempPlayer = player;
                    pit = 1;
                }
            }
        }

        // Return to the last pit to have a stone placed inside it.
        if (--pit < 0)
        {
            // The last pit would be the player's points pit.
            pit = 0;
        } 
	else if (pit == 0)
        {
            // The last pit would be the opposite player's last pit.
            tempPlayer = toggle(tempPlayer);
            pit = 7;
        }

        // Check for special cases.
        if (tempPlayer == player && pit > 0
                && n.getData().getState()[player][pit] == 1
                && n.getData().getState()[toggle(player)][8 - pit] > 0)
        {
            // Special case 'stealing opponents stones'.
            int curVal = n.getData().getState()[player][0];
            n.getData().setState(
                    player,
                    0,
                    curVal + n.getData().getState()[toggle(player)][8 - pit]
                            + 1);

            // Set the opponents and player's pits to be empty.
            n.getData().setState(player, pit, 0);
            n.getData().setState(toggle(player), 8 - pit, 0);
        } 
	else if (tempPlayer == player && pit == 0 && !firstMove)
        {
            // Special case 'player moves again'.
            n.getData().setPlayerMoveAgain(true);
        }

        return n;
    }

    /**
     * Method that toggles an int between 0 and 1. Assumes input is 0 or 1.
     *
     * @param n
     *            The int to toggle.
     *
     * @return nToggled The result of toggling the int.
     */
    public static int toggle(int n)
    {
        return n == 0 ? 1 : 0;
    }

    public static boolean hasGameEnded(Node<NodeData> node)
    {
        int pit;
        int[] pits = node.getData().getState()[node.getData().getPlayer()];

        // Starts from 1 because 0 is the score state
        for (int i = 1; i < pits.length; i++)
        {
            pit = pits[i];
            if (pit != 0)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Method that gets a list of Nodes that represents the available valid
     * moves from a given node.
     *
     * @param parent
     *            The provided node.
     *
     * @return validMoves A list of valid moves from this state.
     */
    public static Node<NodeData> initState(int player)
    {
        int[][] initState =
        {
        { 0, 7, 7, 7, 7, 7, 7, 7 },
        { 0, 7, 7, 7, 7, 7, 7, 7 } };

        NodeData data = new NodeData(initState, player);
        return new Node<NodeData>(data);
    }

}
