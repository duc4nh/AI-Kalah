import java.util.ArrayList;
import java.util.List;

public class Node<T>
{
    // The direct children of the node
    private List<Node<T>> children = new ArrayList<Node<T>>();

    // Parent of the node (if applicable)
    private Node<T> parent = null;

    // Data carried in the node e.g. probability
    private T data = null;

    // Constructors
    public Node(T data)
    {
        this.data = data;
    }

    public Node(T data, Node<T> parent)
    {
        this.data = data;
        this.parent = parent;
    }

    // Override for the equals method
    @SuppressWarnings("unchecked")
    public boolean equals(Object other)
    {
        if (other == null)
            return false;
        else
        {
            return this.data.equals(((Node<T>) other).getData());
        }
    }

    // Gets the direct children of the node
    public List<Node<T>> getChildren()
    {
        return this.children;
    }

    // Get the parent of the node
    public Node<T> getParent()
    {
        return this.parent;
    }

    // Set the parent of the node
    public void setParent(Node<T> parent)
    {
        this.parent = parent;
    }

    // Add a child to the node's direct children
    public void addChild(T data)
    {
        Node<T> child = new Node<T>(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node<T> child)
    {
        child.setParent(this);
        this.children.add(child);
    }

    // Removes a child based on equality of data
    public void removeChild(T data)
    {
        for (int i = 0; i < children.size(); i++)
        {
            if (this.children.get(i).data.equals(data))
            {
                this.children.remove(i);
            }
        }
    }

    // Removes a child using the equals method on nodes
    public void removeChild(Node<T> node)
    {
        children.remove(node);
    }

    // Returns whether a given node is a child of this node.
    public boolean isChild(Node<T> node)
    {
        boolean found = false;
        int i = 0;

        while (!found && i < children.size())
        {
            if (children.get(i++).equals(node))
            {
                found = true;
            }
        }

        return found;
    }

    public T getData()
    {
        return this.data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    // A node is root if it has no parent
    public boolean isRoot()
    {
        return (this.parent == null);
    }

    // A node is a leaf if it has no children
    public boolean isLeaf()
    {
        return this.children.isEmpty();
    }

    // Removes the parent
    public void removeParent()
    {
        this.parent = null;
    }

    public String toString()
    {
        return this.data.toString();
    }
}
