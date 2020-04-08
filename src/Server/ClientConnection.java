package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection implements Runnable {
    private Socket socket;
    private Service service;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientConnection(Socket socket, Service service) {
        this.socket = socket;
        this.service = service;
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        try {
            while(running.get()) {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public Service getService() {
        return service;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }
}
