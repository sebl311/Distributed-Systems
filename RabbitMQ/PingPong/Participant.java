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

        channel.queueDeclare("Ping", false, false, false, null);
        channel.queueDeclare("Pong", false, false, false, null);
        channel.exchangeDeclare("handshake", "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "handshake", "");

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
                stateWrapper.value="WAITING";
                String newMessage = "INIT_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
            }else if(messageType.equals("INIT_CON")&& ID<messageVal){
                stateWrapper.value="STARTED";
                String newMessage = "OK_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
            } else if(messageType.equals("INIT_CON")&& ID>messageVal && stateWrapper.value.equals("IDLE")){
                stateWrapper.value="WAITING";
                String newMessage = "INIT_CON("+ID+")";
                channel.basicPublish("handshake", "", null, newMessage.getBytes("UTF-8"));
            } else if(messageType.equals("OK_CON")&& ID!=messageVal){
                stateWrapper.value="STARTED";
                channel.basicPublish("Ping", "", null, "PING".getBytes("UTF-8"));
                //listen for PONG
                listenForPong(channel, ID);

            } 

            System.out.println(ID + " Received type: " + messageType + " with value: "+ messageVal);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
  
    }

    public static void listenForPong(Channel channel, int ID) throws Exception{
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if(message.equals("PONG")){
                System.out.println(ID + " Received PONG");
                channel.basicPublish("Ping", "", null, "PING".getBytes("UTF-8"));
            }
        };
        channel.basicConsume("Pong", true, deliverCallback, consumerTag -> { });
    }
}
