import java.lang.Math;
import java.util.List;

/**
 * Class that implements the parts that make up the Monte Carlo Tree Search
 * algorithm.
 */
public class MonteCarloTreeSearch
{
    // Represents a counter for the number of simulations currently executed.
    private static int numSimulations;
    private static int startingPlayer;

    private static final double C = 0.00001;

    /**
     * Method that executes the Monte Carlo Tree Search algorithm. Takes a node
     * representing the current state and returns a node representing the next
     * action to take.
     *
     * @param root
     *            The current state.
     *
     * @return theBestChild The next action to take.
     */
    public static Node<NodeData> mcts(Node<NodeData> root)
    {
        // System.out.println("Player root " + root.getData().getPlayer());
        Node<NodeData> selectedNode;
        boolean win;

        // Initialize the simulation counter.
        numSimulations = 0;

        // Repeat the steps of the MCTS as long as we have
        // not exceeded our computational budget.
        while (numSimulations < 10000)
        {
            // Select successive child nodes of root until
            // we reach a candidate for expansion.
            selectedNode = selectTreeRoute(root);

            // When we hit a node that can be expanded (non-terminal with
            // unvisited children) we add that node to the game tree.
            expand(selectedNode);

            startingPlayer = selectedNode.getData().getPlayer();

            // Play out a simulation until completion.
            win = simulate(selectedNode);

            // Update nodes in tree with results of the simulation.
            updateNodes(selectedNode, win);

            // Decrement our computational budget counter.
            numSimulations++;
        }

        return bestChild(root);
    }

    /**
     * Method that selects successive child nodes of a given node until it
     * reaches a candidate for expansion. The node selected is returned.
     *
     * @param n
     *            The provided current node.
     *
     * @return candidate The node selected for expansion.
     */
    private static Node<NodeData> selectTreeRoute(Node<NodeData> n)
    {
        boolean candidateNotFound = true;

        do
        {
            // Iterate through all the child nodes of the current node and
            // choose the node that gives the highest value after applying UCT.
            n = uct(n);

            if (expansionCandidate(n))
                candidateNotFound = false;

        } while (candidateNotFound && !n.getData().getTerminal());

        return n;
    }

    /**
     * Method that expands the tree by adding a selected node to the tree.
     *
     * @param n
     *            The node to be added.
     */
    private static void expand(Node<NodeData> n)
    {
        Node<NodeData> parent = n.getParent();
        parent.addChild(n);
    }

    /**
     * Method that executes a simulation by selecting nodes from the given
     * starting node until it reaches a terminating node.
     *
     * @param n
     *            The node to begin simulation from.
     *
     * @return result Whether this was a victory or not.
     */
    private static boolean simulate(Node<NodeData> n)
    {
        // Check if the game has ended
        if (!Utilities.hasGameEnded(n))
        {
            Node<NodeData> newNode = executeRandomMove(n);
            simulate(newNode);
        }

        return endGame(n);
    }

    /**
     * Method that updates nodes with the results of a simulation starting from
     * a given child node, and working back up the tree until it reaches the
     * root.
     *
     * @param node
     *            The child node to start the update from.
     * @param win
     *            The result of the simulation.
     */
    private static void updateNodes(Node<NodeData> node, boolean win)
    {
        Node<NodeData> currentNode = node;

        while (currentNode != null)
        {
            NodeData data = currentNode.getData();
            data.setNumSimulations(data.getNumSimulations() + 1);

            if (win && data.getPlayer() == startingPlayer)
            {
                data.setNumWins(data.getNumWins() + 1);
            } 
	    else if (!win && data.getPlayer() != startingPlayer)
            {
                data.setNumWins(data.getNumWins() + 1);
            }

            currentNode.setData(data);
            currentNode = currentNode.getParent();
        }

    }

    /**
     * Method that returns the next action to take in the decision tree. Takes a
     * node and returns the best child based on the results of the stages of the
     * MCTS algorithm.
     *
     * @param parent
     *            The parent node that a child will be selected from.
     *
     * @return child The child node selected.
     */
    private static Node<NodeData> bestChild(Node<NodeData> parent)
    {
        Node<NodeData> child = null;
        Node<NodeData> bestChild = null;
        int max = 0;

        // Get all children of this node.
        List<Node<NodeData>> children = parent.getChildren();

        // Loop through the children of the parent and find the node with
        // the highest number of simulations.
        for (int i = 0; i < children.size(); i++)
        {
            child = children.get(i);

            // Get the number of simulations of this child.
            numSimulations = child.getData().getNumSimulations();

            // Best child.
            if (numSimulations > max)
            {
                max = numSimulations;
                bestChild = child;
            }
        }
	
        return bestChild;
    }

    /**
     * Method that applies the UCT algorithm to all the children of a given node
     * and returns the node that gives the highest value for the UCT algorithm.
     *
     * @param parent
     *            The provided node whose children will be tested.
     *
     * @return child The child node selected.
     */
    private static Node<NodeData> uct(Node<NodeData> parent)
    {
        Node<NodeData> child = null;
        Node<NodeData> maxChild = null;

        double max = -1;
        double current = -1;

        // Get the valid moves that we can make from this Node.
        List<Node<NodeData>> validMoves = Utilities.getValidMoves(parent);

        // Hacky fix for terminal nodes.
        if (validMoves.size() == 0)
        {
            parent.getParent().getData().setTerminal(true);
            return parent.getParent();
        }

        // Apply the UCT algorithm to all available valid moves.
        for (int i = 0; i < validMoves.size(); i++)
        {
            // Get a single valid move from all possible valid moves.
            child = validMoves.get(i);

            // UCT algorithm.
	    int childNumWins        = child.getData().getNumWins();
	    int childNumSimulations = child.getData().getNumSimulations();
	    
            current = ( childNumWins / (childNumSimulations + C) ) + 
		      ( Math.sqrt(2.0) * 
		       (Math.sqrt(Math.log(numSimulations + 1) / 
		       (childNumSimulations + C))) );

            // A new best child has been found if the value for UCT is greater
            // than our current max.
            if (current > max)
            {
                max = current;
                maxChild = child;
            }
        }

        return maxChild;
    }

    /**
     * Method that determines if a given node is a candidate for expansion in
     * the Monte Carlo Tree Search.
     *
     * @param n
     *            The provided node to be examined.
     *
     * @return expansionCandidate Whether the node is a candidate or not.
     */
    private static boolean expansionCandidate(Node<NodeData> n)
    {
        // Check if node is in tree. This is done by checking if the parent 
	// node is aware that this node is it's child.
        Node<NodeData> parent = n.getParent();

        return !parent.isChild(n);
    }

    // Simple function to return if the bot won or not
    private static boolean endGame(Node<NodeData> n)
    {
        boolean win;

        // Get the current state.
        int player = n.getData().getPlayer();
        int[][] curState = n.getData().getState();

        // Create variables to store the score tally's.
        int northScore = curState[1][0];
        int southScore = curState[0][0];

        for (int i = 1; i < 8; i++)
        {
            northScore += curState[1][i];
            southScore += curState[0][i];
        }

        if (northScore > southScore)
        {
            win = (player == 0) ? false : true;
        } 
	else
        {
            win = (player == 0) ? true : false;
        }

        return win;
    }

    /**
     * @param n
     *            Use this node to find all valid moves Method that gets valid
     *            moves(returns list of nodes) These nodes all have updated
     *            states Get one of these nodes and pass it as next state for
     *            simulation
     * @return returns a valid move for simulate to use
     */
    private static Node<NodeData> executeRandomMove(Node<NodeData> n)
    {
        List<Node<NodeData>> validMoves = Utilities.getValidMoves(n);

        // Check if there were no valid moves (i.e the game ended).
        // Return the provided node in this case and the recursion will handle
        // termination.
        if (validMoves.isEmpty())
        {
            int player = n.getData().getPlayer();
            n.getData().setPlayer(Utilities.toggle(player));
            return n;
        }

        int moveToExecute = getRandomMove(validMoves.size() - 1);
        return validMoves.get(moveToExecute);
    }

    // Simple function to get a value between 0 and n
    private static int getRandomMove(int validMoves)
    {
        return (int) Math.round(Math.random() * (validMoves));
    }

}
