package RabbitMQ.PingPong;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

  

public class Participant {
    public static void main(String[] argv) throws Exception {
        int ID = Integer.parseInt(argv[0]);
        var stateWrapper=new Object(){String value;};
        stateWrapper.value="IDLE";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("handshake", "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "handshake", "");

        channel.exchangeDeclare("Ping", "direct", true);
        String queueName1 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName1, "Ping", "");

        channel.exchangeDeclare("Pong", "direct", true);
        String queueName2 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName2, "Pong", "");

        System.out.println(ID+": Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            //Parsing Message of Syntax "TYPE(VAL)" into type and value
            String message = new String(delivery.getBody(), "UTF-8");
            String messageType= message.substring(0,message.indexOf('('));
            int messageVal=-1;
            try{
                messageVal=Integer.parseInt(message.substring(message.indexOf('(')+1, message.indexOf(')')));
            }catch (NumberFormatException e){}
            
            if(messageType.equals("START")&&messageVal==ID&&stateWrapper.value.equals("IDLE")){
                System.out.println(ID + " Received type: " + messageType + " with value: "+ messageVal);
                stateWrapper.value="WAITING";
                String newMessage = "INIT_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
                System.out.println(ID + " Send: " + newMessage);

            }else if(messageType.equals("INIT_CON")&& ID<messageVal){
                System.out.println(ID + " Received type: " + messageType + " with value: "+ messageVal);
                stateWrapper.value="STARTED";
                String newMessage = "OK_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
                System.out.println(ID + " Send: " + newMessage);
                try{
                    listenForPing(channel, ID, queueName1);
                } catch(Exception e){
                    System.out.println("Error in listening for PING");
                    e.printStackTrace(System.out);
                }
            } else if(messageType.equals("INIT_CON")&& ID>messageVal && stateWrapper.value.equals("IDLE")){
                System.out.println(ID + " Received type: " + messageType + " with value: "+ messageVal);
                stateWrapper.value="WAITING";
                String newMessage = "INIT_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
                System.out.println(ID + " Send: " + newMessage);
            } else if(messageType.equals("OK_CON")&& ID!=messageVal){
                System.out.println(ID + " Received type: " + messageType + " with value: "+ messageVal);
                stateWrapper.value="STARTED";
                channel.basicPublish("Ping", "", null, "PING".getBytes("UTF-8"));
                System.out.println(ID + " Send: Ping");
                try{
                    listenForPong(channel, ID, queueName2);
                } catch(Exception e){
                    System.out.println("Error in listening for PONG");
                    e.printStackTrace(System.out);
                }
            } 
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
  
    }

    public static void listenForPing(Channel channel, int ID, String queueName) throws Exception{
        DeliverCallback deliverCallbackPing = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if(message.equals("PING")){
                System.out.println(ID + " Received PING");
                try{
                    Thread.sleep(1000);
                }catch(Exception e){}
                channel.basicPublish("Pong", "", null, "PONG".getBytes("UTF-8"));
                System.out.println(ID + " Send: Pong");
            }
        };
        channel.basicConsume(queueName, true, deliverCallbackPing, consumerTag -> { });
    }

    public static void listenForPong(Channel channel, int ID, String queueName) throws Exception{
        DeliverCallback deliverCallbackPong = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if(message.equals("PONG")){
                System.out.println(ID + " Received PONG");
                try{
                    Thread.sleep(1000);
                }catch(Exception e){}
                channel.basicPublish("Ping", "", null, "PING".getBytes("UTF-8"));
                System.out.println(ID + " Send: Ping");
            }
        };
        channel.basicConsume(queueName, true, deliverCallbackPong, consumerTag -> { });
    }
}
