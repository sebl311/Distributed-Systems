import java.util.List;

public class Node {

    private int ID;
    private Node leftNode;
    private Node rightNode;
    private List<String> physicalConnections;

    public Node(int ID, Node leftNode, Node rightNode, List<String> physicalConnections) {
        this.ID = ID;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.physicalConnections = physicalConnections;
    }


}