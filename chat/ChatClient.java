import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends JFrame implements ActionListener {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton leaveButton;

    private Chatroom chatroom;
    private Participant participant;

    private Participant participantStub;
    private String name;

    public ChatClient(String name, int clientPort, String host, int port) {
        super("Chat Room - Participant: " + name);
        this.name = name;

        // Initialize GUI components
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        leaveButton = new JButton("Leave");
        sendButton.addActionListener(this);
        leaveButton.addActionListener(this);

        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);
        inputPanel.add(leaveButton);

        // Add components to frame
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(chatScrollPane, BorderLayout.CENTER);
        contentPane.add(inputPanel, BorderLayout.SOUTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Connect to the chat server
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            chatroom = (Chatroom) registry.lookup("Chatservice");

            participant = new ParticipantImpl(name, chatroom, this);
            participantStub = (Participant) UnicastRemoteObject.exportObject(participant, clientPort);

            // Remote method invocation
            chatroom.join(participantStub);
        } catch (Exception e) {
            System.err.println("Error on client: " + e);
            e.printStackTrace();
        }
    }

    public void displayMessage(String message) {
        chatArea.append(message + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                  participantStub.send(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                messageField.setText("");
            }
        } else if (e.getSource() == leaveButton) {
            try {
                chatroom.leave(participantStub);
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                System.out.println("Usage: java ChatClient <Client name> <Client Port> <rmiregistry host> <rmiregistry port>");
                return;
            }

            String name = args[0];
            int clientPort = Integer.parseInt(args[1]);
            String host = args[2];
            int port = Integer.parseInt(args[3]);

            SwingUtilities.invokeLater(() -> new ChatClient(name, clientPort, host, port));
        } catch (Exception e) {
            System.err.println("Error on client: " + e);
            e.printStackTrace();
        }
    }
}
