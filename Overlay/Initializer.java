

import java.util.ArrayList;
import java.util.List;

public class Initializer {
    private int[][] matrix;
    private List<Node> nodes = new ArrayList<>();

    public Initializer(int[][] matrix){
        this.matrix = matrix;
    }

    public List<Node> initialize(){
        createNodes();
        connectNodes();
        formRing();
        buildRoutingTable();
        return nodes;
    }

    private void createNodes(){
        for(int i = 0; i < matrix.length; i++){
            Node newNode = new Node(i, new ArrayList<>(),matrix.length);
            nodes.add(newNode);
        }
    }

    private void connectNodes(){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if(matrix[i][j] == 1){
                    nodes.get(i).getPhysicalConnections().add(nodes.get(j).getID());
                }
            }
        }
    }

    private void buildRoutingTable(){
        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).buildRoutingTable(matrix);
        }
        //print routing tables
        for(int i = 0; i < nodes.size(); i++){
            nodes.get(i).printRoutingTable();
        }
    }

    private void formRing(){
        for(int i = 0; i < nodes.size(); i++){
            Node leftNode = nodes.get((i-1+nodes.size())%nodes.size());
            Node rightNode = nodes.get((i+1)%nodes.size());
            nodes.get(i).setLeftNode(leftNode);
            nodes.get(i).setRightNode(rightNode);
        }
    }
}
