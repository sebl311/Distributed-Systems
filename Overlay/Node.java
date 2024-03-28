import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Node {
    private int ID;
    private Node leftNode;
    private Node rightNode;
    final List<Integer> physicalConnections;
    private Map<Integer, Integer> routingTable = new HashMap<Integer, Integer>();
    private int[] distanceVector;
    private int routingTableUpdateCounter = 0;
    
    // RabbitMQ connection
    private Connection connection;
    private Channel channel;
    private String queueName;

    public Node(int ID, List<Integer> physicalConnections, int totalNodes){
        this.ID = ID;
        this.physicalConnections = physicalConnections;
        this.distanceVector = new int[totalNodes]; 
        Arrays.fill(this.distanceVector, Integer.MAX_VALUE); // Fill the array with a default value representing "infinity"
        this.distanceVector[ID] = 0; // Distance to itself is 0
        setupRabbitMQConnection();
        //keep listening for messages
        receiveHandler();
    }

    //getters
    public int getID() { return ID; }
    public Node getLeftNode(){ return leftNode; }
    public Node getRightNode(){ return rightNode; }
    public List<Integer> getPhysicalConnections(){ return physicalConnections; }

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
            System.out.println("Node [" + this.ID + "] is ready to receive messages through queue: " + queueName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addRoutingTableEntry(int destinationID, int nextHop){
        routingTable.put(destinationID, nextHop);
    }

    public int getNextHop(int destinationID){
        return routingTable.get(destinationID);
    }

    public void buildRoutingTable(int[][] matrix){     
        //set the non physical connections to infinity
        for(int i = 0; i < matrix.length; i++){
            if(!physicalConnections.contains(i)){
                if(i != this.ID){
                    distanceVector[i] = Integer.MAX_VALUE;
                } else {
                    distanceVector[i] = 0;
                }
            }
        }
        //set the physical connections to 1
        for(Integer connection : physicalConnections){
            distanceVector[connection] = 1;
        }
        //merge the distance vector with the routing table
        for(int i = 0; i < distanceVector.length; i++){
            routingTable.put(i, i);
        }

        //send the distance vector to the physical connections
        for(Integer connection : physicalConnections){
            Message message = new Message(Message.MessageType.ROUTING, null, connection, this.ID, distanceVector);
            System.out.println("Node [" + this.ID + "] is creating the message : " + message.toString());
            sendRoutingMessage(message);
        }
    }

    public void printRoutingTable(){

        System.out.println("-------------------------------------");
        System.out.println("Node [" + this.ID + "] Routing Table with Distance Vector:");

        for(Map.Entry<Integer, Integer> entry : routingTable.entrySet()){
            Integer destinationID = entry.getKey();
            Integer nextHop = entry.getValue();
            Integer distance = distanceVector[destinationID];

            System.out.println("Destination: " + destinationID + " | Next Hop: " + nextHop + " | Distance: " + distance);
        }
        System.out.println("-------------------------------------");
    }
    public void sendRoutingMessage(Message message){
        try{
            String queue = "node_" + message.getDestinationID();
            channel.basicPublish("", queue, null, message.toString().getBytes());
            // System.out.println("[" + this.ID +"] Sent routing message to node: " + message.getDestinationID() + "through queue: " + queue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLeft(String message){
        try {

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

    public void receiveHandler(){
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String m = new String(delivery.getBody(), "UTF-8");
            Message message = new Message(m);
            // System.out.println("[" + this.ID +"] Received message: " + message.toString() + " and type: " + message.getMessageType());
            //SWITCH BETWEEN THE MESSAGE TYPES
            switch(message.getMessageType()){
                case DATA:
                    receiveData(message);
                    break;
                case ROUTING:
                    receiveRouting(message);
                    break;
                case FORWARD:
                    receiveForward(message);
                    break;
            }
        };
        try{
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveData(Message message){
        try{
            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String m = new String(delivery.getBody(), "UTF-8");
                System.out.println("[" + this.ID +"] Received '" + m + "'");
            }, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveRouting(Message message){

        //check the routing table update counter this is only for testing 
        //untill we figure out a proper way to know how to stop the updates
        if(routingTableUpdateCounter < 10){
            
            int[] receivedDistanceVector = message.getDistanceVector();
            int sourceID = message.getSourceID();

            for (int i = 0; i < receivedDistanceVector.length; i++) {
                int distanceToDestinationThroughSource = receivedDistanceVector[i];
                
                // Prevent self-updates and handle infinity representation
                if (i != this.ID && distanceToDestinationThroughSource < Integer.MAX_VALUE) {
                    // Current known distance to this destination
                    int currentDistance = this.distanceVector[i];
                    // If the received distance (plus 1 for the hop to the source) is better, update
                    if (distanceToDestinationThroughSource + 1 < currentDistance) {
                        this.distanceVector[i] = distanceToDestinationThroughSource + 1;
                        // Update the routing table to note that the next hop to this destination is via the source of the message
                        this.routingTable.put(i, sourceID);
                    }
                }
            }
            System.out.println("[Node " + this.ID + "] Updated routing table with distance vector from node " + sourceID);

            //now send to the physical connections the updated distance vector
            for(Integer connection : physicalConnections){
                Message newMessage = new Message(Message.MessageType.ROUTING, null, connection, this.ID, distanceVector);
                sendRoutingMessage(newMessage);
            }
            routingTableUpdateCounter++;
        } 
    }

    public void receiveForward(Message message){
        try{
            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String m = new String(delivery.getBody(), "UTF-8");
                System.out.println("[" + this.ID +"] Received forward message: " + m);
            }, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
