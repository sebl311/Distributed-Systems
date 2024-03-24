package Overlay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int[][] matrix = {
            {0, 1, 0, 1},
            {1, 0, 1, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 1},
            {1, 0, 0, 0}
        };

        Initializer initializer = new Initializer(matrix);
        List<Node> nodesCreated = initializer.initialize();

        for(Node node : nodesCreated){
            System.out.println("Node ID: " + node.getID());
            System.out.println("Physical Connections: " + node.getPhysicalConnections());
            System.out.println("Left Node: " + node.getLeftNode().getID());
            System.out.println("Right Node: " + node.getRightNode().getID());
            System.out.println();
        }
    }

    private static List<List<Integer>> readAMatrix(String filePath) {
        List<List<Integer>> matrix = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<Integer> row = new ArrayList<>();
                for (String val : line.split("\\s+")) { 
                    row.add(Integer.parseInt(val));
                }
                matrix.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }
}