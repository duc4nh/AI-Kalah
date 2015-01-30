import java.util.Arrays;

/**
 * Class that represents the type of data that a Node can hold.
 */
public class NodeData
{
    // Which pit choice this node is representing (1-7)
    private int pitNumber;

    // What the current state of the game looks like.
    private int[][] state;

    // The player that made this move.
    private int player;

    // Whether the player that made this move should go again or not.
    private boolean playerMoveAgain;

    // The number of simulations that a node has been a part of.
    private int numSimulations;

    // The number of game wins that a node has been a part of.
    private int numWins;

    // Whether this is a terminal node or not.
    private boolean terminal;

    public NodeData(int[][] state, int player)
    {
        this.state = state;
        this.player = player;
        this.playerMoveAgain = false;
    }

    public int[][] getState()
    {
        int[][] curState = new int[2][8];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < this.state[i].length; j++)
                curState[i][j] = this.state[i][j];

        return curState;
    }

    public void setState(int first, int second, int val)
    {
        this.state[first][second] = val;
    }

    public int getPitNumber()
    {
        int newPit = new Integer(this.pitNumber);
        return newPit;
    }

    public void setPitNumber(int val)
    {
        this.pitNumber = val;
    }

    public int getPlayer()
    {
        int newPlayer = new Integer(this.player);
        return newPlayer;
    }

    public void setPlayer(int val)
    {
        this.player = val;
    }

    public boolean getPlayerMoveAgain()
    {
        boolean b = new Boolean(this.playerMoveAgain);
        return b;
    }

    public void setPlayerMoveAgain(boolean val)
    {
        this.playerMoveAgain = val;
    }

    public int getNumSimulations()
    {
        int newSimulations = new Integer(this.numSimulations);
        return newSimulations;
    }

    public void setNumSimulations(int val)
    {
        this.numSimulations = val;
    }

    public int getNumWins()
    {
        int newWins = new Integer(this.numWins);
        return newWins;
    }

    public void setTerminal(boolean val)
    {
        this.terminal = val;
    }

    public boolean getTerminal()
    {
        boolean newTerminal = new Boolean(this.terminal);
        return newTerminal;
    }

    public void setNumWins(int val)
    {
        this.numWins = val;
    }

    public String toString()
    {
        return "\nPlayer South " + Arrays.toString(this.state[0])
                + "\nPlayer North" + Arrays.toString(this.state[1]) + "\n";
    }
}
