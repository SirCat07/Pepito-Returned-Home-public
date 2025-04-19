package game;

public class Server {
//    public static void init() {
//        // Start the command listener in a separate thread
//        new Thread(() -> startCommandListener()).start();
//
//        // Periodically send IP to the server
//        while (true) {
//            send();
//            try {
//                Thread.sleep(5000); // Wait 5 seconds before sending IP again
//            } catch (InterruptedException e) {
//                System.out.println("IP sender interrupted: " + e.getMessage());
//            }
//        }
//    }
//
//    private static void startCommandListener() {
//        int port = 12346;
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            System.out.println("Client listening for commands on port " + port);
//            while (true) {
//                try (Socket socket = serverSocket.accept();
//                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//                    System.out.println("Received connection from " + socket.getInetAddress().getHostAddress());
//                    String input;
//                    while ((input = in.readLine()) != null) {
//                        System.out.println("- FROM IP " + socket.getInetAddress().getHostAddress() + " -");
//                        System.out.println("RECEIVED: " + input);
//
//                        // Simulate command execution (replace with actual logic)
//                        Console.currentlyTyping = input;
//                        Console.enter(true);
//
//                        out.println("EXECUTED COMMAND " + input + " AT IP: " + socket.getLocalAddress().getHostAddress());
//                    }
//                    System.out.println("Command connection closed by " + socket.getInetAddress().getHostAddress());
//                } catch (IOException e) {
//                    System.out.println("Error in command listener: " + e.getMessage());
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Failed to start command listener: " + e.getMessage());
//        }
//    }
//
//    public static void send() {
//        try (Socket socket = new Socket("26.82.137.59", 12345);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//            String localIP = socket.getLocalAddress().getHostAddress();
//            out.println("IP HERE: " + localIP);
//            System.out.println("Sent IP: " + localIP);
//
//            // Wait for server acknowledgment
//            String response = in.readLine();
//            System.out.println("Server response: " + response);
//        } catch (Exception e) {
//            System.out.println("Failed to send IP: " + e.getMessage());
//        }
//    }
//    
}