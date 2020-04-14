package Client;

import Shared.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReadTask implements Runnable
{
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private Client client;
    private Thread thread;
    private boolean isRunning = true;


    public ReadTask(Socket socket, Client client)
    {
        this.socket = socket;
        this.client = client;

        try
        {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }



    @Override
    public void run()
    {
        while (isRunning)
        {
            try
            {
                Packet response = (Packet) objectInputStream.readObject();




            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
