package Overlay;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private int ID;
    private Node leftNode;
    private Node rightNode;
    final List<String> physicalConnections;
    private Map<Integer, Node> routingTable = new HashMap<Integer, Node>();
    
    // RabbitMQ connection
    private Connection connection;
    private Channel channel;
    private String queueName;

    public Node(int ID, List<String> physicalConnections){
        this.ID = ID;
        this.physicalConnections = physicalConnections;
        setupRabbitMQConnection();
    }

    //getters
    public int getID() { return ID; }
    public Node getLeftNode(){ return leftNode; }
    public Node getRightNode(){ return rightNode; }
    public List<String> getPhysicalConnections(){ return physicalConnections; }

    //setters
    public void setLeftNode(Node leftNode){ this.leftNode = leftNode; }
    public void setRightNode(Node rightNode){ this.rightNode = rightNode; }

    public void setupRabbitMQConnection(){
        //initialize RabbitMQ connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try{
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.queueName = "node_" + ID;

            channel.queueDeclare(queueName, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addRoutingTableEntry(int destinationID, Node nextHop){
        routingTable.put(destinationID, nextHop);
    }

    public Node getNextHop(int destinationID){
        return routingTable.get(destinationID);
    }

    public void buildRoutingTable(int[][] matrix){
        
        for(int i = 0; i < matrix.length; i++){
            if(matrix[ID][i] == 1){
                addRoutingTableEntry(i, this);
            } else {
                addRoutingTableEntry(i, null);
            }
        }

        //TODO djiikstra's algorithm here
        

        System.out.println("[" + this.ID + "] Routing Table:");

        for(Map.Entry<Integer, Node> entry : routingTable.entrySet()){
            System.out.println("Destination: " + entry.getKey() + " Next Hop: " + entry.getValue().getID());
        }
    }

    public void sendLeft(String message){
        try {
            //TODO send a new message to left neigbor via next hop in routing table
            // if (leftNode != null) {
            //     //check if left node is in physical connections
            //     if(physicalConnections.contains(leftNode.getID() + "")){
            //         String leftQueueName = "node_" + leftNode.getID();
            //         channel.basicPublish("", leftQueueName, null, message.getBytes());
            //         System.out.println("[" + this.ID +"] Sent '" + message + "' to left node: " + leftNode.getID());
            //     } else {
            //         for(String connection : leftNode.getPhysicalConnections()){
            //             if(physicalConnections.contains(connection)){
            //                 String leftQueueName = "node_" + leftNode.getID();
            //                 channel.basicPublish("", leftQueueName, null, message.getBytes());
            //                 System.out.println("[" + this.ID +"] Sent '" + message + "' to left node: " + leftNode.getID());
            //                 break;
            //             }
            //         }
            //     }
            // } else {
            //     System.out.println("Left node [" + leftNode.getID() + "] is not set for node [" + this.ID + "]");
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRight(String message){
        //TODO send a new message to right neigbor via next hop in routing table
        try{
            if(rightNode != null){
                String rightQueueName = "node_" + rightNode.getID();
                channel.basicPublish("", rightQueueName, null, message.getBytes());
                System.out.println("[" + this.ID +"] Sent '" + message + "' to right node: " + rightNode.getID());
            } else {
                System.out.println("Right node [" + rightNode.getID() + "] is not set for node [" + this.ID + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive(){
        //TODO check destination ID of message if equal to ours or if redirect
        try{
            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("[" + this.ID +"] Received '" + message + "'");
            }, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}