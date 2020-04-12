package Server;
import Shared.Packet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection implements Runnable {
    private Socket socket;
    private Server.Service service;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientConnection(Socket socket, Service service) {
        this.socket = socket;
        this.service = service;
    }

    public void stop() {
        running.set(false);
        System.out.println("Client Connection has stopped!");
    }

    @Override
    public void run() {
        running.set(true);
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            while(running.get()) {
               Packet packet = (Packet) input.readObject();
               service.handle(packet, output);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public Service getService() {
        return service;
    }

    public ObjectOutputStream getOutputStream() {
        return output;
    }
}