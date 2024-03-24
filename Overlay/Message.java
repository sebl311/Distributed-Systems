public class Message {
    
    private String message;
    private int sourceID;
    private int destinationID;

    public Message(String message, int sourceID, int destinationID){
        this.message = message;
        this.sourceID = sourceID;
        this.destinationID = destinationID;
    }

    public String getMessage(){ return message; }
    public int getSourceID(){ return sourceID; }
    public int getDestinationID(){ return destinationID; }

}