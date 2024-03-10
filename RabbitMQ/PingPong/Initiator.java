package RabbitMQ.PingPong;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Initiator {

    public static void main(String[] argv) throws Exception {

        int starter = Integer.parseInt(argv[0]);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("handshake", "fanout");
  
            String message = "START("+starter+")";
            channel.basicPublish("handshake", "", null, message.getBytes("UTF-8"));
            System.out.println(" Initiator Sent '" + message + "'");

            /* 
            String message1 = "START(1)";
            String message2 = "START(1)";
  
            channel.basicPublish("handshake", "", null, message1.getBytes("UTF-8"));
            System.out.println(" Initiator Sent '" + message1 + "'");
            try{
              Thread.sleep(1000);
            }catch(Exception e){}
            channel.basicPublish("handshake", "", null, message2.getBytes("UTF-8"));
            System.out.println(" Initiator Sent '" + message2 + "'");

            */
      }
    }
  }
