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
    final List<Integer> physicalConnections;
    private Map<Integer, Integer> routingTable = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> distanceVector = new HashMap<Integer, Integer>();
    
    // RabbitMQ connection
    private Connection connection;
    private Channel channel;
    private String queueName;

    public Node(int ID, List<Integer> physicalConnections){
        this.ID = ID;
        this.physicalConnections = physicalConnections;
        setupRabbitMQConnection();
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
        for(Integer connection : physicalConnections){
            routingTable.put(connection, connection);
            distanceVector.put(connection, 1);
        }
        for(Integer connection : physicalConnections){
            Message message = new Message(MessageType.ROUTING, null, connection, this.ID, distanceVector);
            sendRoutingMessage(message);
        }

        System.out.println("-------------------------------------");
        System.out.println("Node [" + this.ID + "] Routing Table:");

        for(Map.Entry<Integer, Integer> entry : routingTable.entrySet()){
            System.out.println("Destination: " + entry.getKey() + " Next Hop: " + entry.getValue());
        }
        System.out.println("-------------------------------------");
    }

    public void sendRoutingMessage(Message message){
        try{
            String queue = "node_" + message.getDestinationID();
            channel.basicPublish("", queue, null, message.toString().getBytes());
            System.out.println("[" + this.ID +"] Sent routing message to right node: " + rightNode.getID());
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

    public void receive(){
        try{
            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String m = new String(delivery.getBody(), "UTF-8");
                Message message = new Message(m);
                //SWITCH BETWEEN THE MESSAGE TYPES
                message.getMessageType();
                System.out.println("[" + this.ID +"] Received '" + message + "'");
            }, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
