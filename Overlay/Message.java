import java.util.Map;

public class Message {
    private String message;
    private int destinationID;
    private int sourceID;
    private MessageType messageType;

    private enum MessageType {
        DATA,
        ROUTING,
        FORWARD
    }

    private int[] distanceVector;

    public Message(String unparsedMessage){
        String[] messageParts = unparsedMessage.split("_");
        this.message = messageParts[0].split(":")[1];
        this.destinationID = Integer.parseInt(messageParts[1].split(":")[1]);
        this.sourceID = Integer.parseInt(messageParts[2].split(":")[1]);
        this.messageType = MessageType.valueOf(messageParts[3].split(":")[1]);
        String[] distanceVectorString = messageParts[4].split(":")[1].split(",");
        int[] distanceVector = new int[distanceVectorString.length];
        for(int i = 0; i < distanceVectorString.length; i++){
            distanceVector[i] = Integer.parseInt(distanceVectorString[i]);
        }
        this.distanceVector = distanceVector;
    }
    
    public Message(MessageType messageType,String Message, int destinationID, int sourceID, int[] distanceVector){
        this.message = Message;
        this.destinationID = destinationID;
        this.sourceID = sourceID;
        this.messageType = messageType;
        this.distanceVector = distanceVector;
    }

    public String getMessage(){
        return message;
    }
    public int getDestinationID(){
        return destinationID;
    }
    public int getSourceID(){
        return sourceID;
    }
    public MessageType getMessageType(){
        return messageType;
    }
    public String toString(){
        return "Message:" + message + "_Destination:" + destinationID + "_Source:" + sourceID + "_Type:" + messageType + "_DistanceVector:" + distanceVector.toString();
    }

}
