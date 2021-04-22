package gui;

import connection.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class serverController {
    @FXML private TableColumn<Object, Object> c1;
    @FXML private TableColumn<Object, Object> c2;
    @FXML private TableView<Log> logs;
    @FXML private Pane blocker;
    @FXML private Label errorMessage;
    @FXML private BorderPane errorPopup;
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Button actionButton;

    private final Calendar cal;
    private final ArrayList<Log> logList;
    private Thread serverThread;

    public serverController() {
        cal = Calendar.getInstance();
        logList = new ArrayList<Log>();
    }

    @FXML
    public void initialize() {
        // Create table
        c1.setCellValueFactory(new PropertyValueFactory<>("time"));
        c2.setCellValueFactory(new PropertyValueFactory<>("message"));
    }

    public void startStop(ActionEvent actionEvent) {
        // Check if server is started
        if(actionButton.isCancelButton()) {
            stop();
        } else {
            start();
        }
    }

    public void start() {
        // Get ip and port
        String ip = ipField.getText();
        String sPort = portField.getText();

        if(ip.length() == 0) {
            ip = "127.0.0.1";
        }

        if(sPort.length() == 0) {
            sPort = "8008";
        }

        int port;
        try {
            port = Integer.parseInt(sPort);
        }
        catch(NumberFormatException e) {
            // Create error message
            errorMessage.setText("Ports can only be whole, positive numbers between 0 and 65535");
            blocker.setVisible(true);
            errorPopup.setVisible(true);
            return;
        }

        // Disable fields
        ipField.setDisable(true);
        portField.setDisable(true);

        // Change button
        actionButton.setText("Stop Server");
        actionButton.setDefaultButton(false);
        actionButton.setCancelButton(true);

        // Start server
        Server server = new Server(port);
        serverThread = new Thread(server, "ServerThread");
        serverThread.setDaemon(true);
        serverThread.start();
        printRunningThreads();

        // Log successful start
        log("Server started on " + ip + ":" + port);
    }

    public void stop() {
        // Kill server thread
        if(serverThread == null) {
            return;
        } else {
            serverThread.interrupt();
        }
        printRunningThreads();

        // Enable fields
        ipField.setDisable(false);
        portField.setDisable(false);

        // Change button
        actionButton.setText("Start Server");
        actionButton.setCancelButton(false);
        actionButton.setDefaultButton(true);

        // Log successful stop
        log("Server stopped");
    }

    public void closeError(ActionEvent actionEvent) {
        errorPopup.setVisible(false);
        blocker.setVisible(false);
    }

    public void log(String message) {
        String currentTime = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        Log currentLog = new Log(currentTime, message);

        logList.add(currentLog);
        logs.getItems().add(currentLog);
    }

    private void printRunningThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        int count = 0;
        for(Thread t : threads) {
            String name = t.getName();
            if(name.equals("ServerThread")) {
                Thread.State state = t.getState();
                System.out.println("Name: " + name + "\tState: " + state);
                count++;
            }
        }

        System.out.println("Running threads: " + count);
    }
}
