package GameService;

import Models.Game;
import Models.Move;
import Server.ClientConnection;
import Shared.Packet;
import Shared.UserInformation;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class GameThread implements Runnable {
    // takes in two players

    private Thread thread;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private ArrayList<ClientConnection> GameObservers;
    private Game game;

    private ClientConnection player1;
    private ClientConnection player2;
    private UserInformation player1UserInformation;
    private UserInformation player2UserInformation;
    private ObjectOutputStream outputToPlayer1;
    private ObjectOutputStream outputToPlayer2;
    private ObjectInputStream inputFromPlayer1;
    private ObjectInputStream inputFromPlayer2;
    private boolean isPlayer1Turn = true;
    private boolean hasPlayerMadeMove = true;

    private BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>();



    public GameThread(Game game, ClientConnection player1, ClientConnection player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;

        player1UserInformation = player1.getInformation();
        player2UserInformation = player2.getInformation();

        outputToPlayer1 = player1.getOutputStream();
        outputToPlayer2 = player2.getOutputStream();
        inputFromPlayer1 = player1.getInputStream();
        inputFromPlayer2 = player2.getInputStream();
    }

    public void addMove(Move move) throws InterruptedException
    {
        System.out.println("Inside GameThread, adding move to the queue");
        moveQueue.add(move);


    }


    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning.set(false);
    }


    @Override
    public void run() {
        isRunning.set(true);

        try
        {
            // sends the username of player1
            Packet whoIsPlayer1 = new Packet(Packet.PLAYER_ONE_USERNAME, player1UserInformation, player1UserInformation.getUserName());
            player1.getOutputStream().writeObject(whoIsPlayer1);
            player2.getOutputStream().writeObject(whoIsPlayer1);
            System.out.println("sending packet about player1");

            // sends the username of player2
            Packet whoIsPlayer2 = new Packet(Packet.PLAYER_TWO_USERNAME, player2UserInformation, player2UserInformation.getUserName());
            player1.getOutputStream().writeObject(whoIsPlayer2);
            player2.getOutputStream().writeObject(whoIsPlayer2);
            System.out.println("sending packet about player2");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


        while (isRunning.get()) {

            Move newMove = null;

            try
            {
                if (! moveQueue.isEmpty())
                {
                    System.out.println("Inside game thread, removing move from the queue ");
                    newMove = moveQueue.take();


                    if (newMove.getUserInformation() == player1UserInformation)
                    {
                        System.out.println("game player1.make move is called");

                        if (game.checkIfValidMove(newMove))
                        {
                            game.player1MakeMove(newMove);

                            Packet packet = new Packet(Packet.GAME_MOVE, player1UserInformation,newMove);
                            player1.getOutputStream().writeObject(packet);
                            player2.getOutputStream().writeObject(packet);
                            System.out.println("move is outputted to both players");


                            if (game.isPlayer1Winner(newMove))
                            {
                                System.out.println("Player 1 Wins!");
                            }
                            else if (game.isTieGame())
                            {
                                System.out.println("Tie Game");
                            }
                        }
                        else
                        {
                            System.out.println("Not a valid move");
                        }



                    }
                    else if (newMove.getUserInformation() == player2UserInformation)
                    {
                        System.out.println("game player2.make move is called");

                        if (game.checkIfValidMove(newMove))
                        {
                            game.player2MakeMove(newMove);

                            Packet packet = new Packet(Packet.GAME_MOVE, player2UserInformation, newMove);
                            player1.getOutputStream().writeObject(packet);
                            player2.getOutputStream().writeObject(packet);
                            System.out.println("move is outputted to both players");



                            if (game.isPlayer2Winner(newMove))
                            {
                                System.out.println("Player 2 Wins!");
                            }
                            else if (game.isTieGame())
                            {
                                System.out.println("Tie Game");
                            }
                        }
                    }
                    else
                    {
                        System.out.println("make move if statements have been skipped");
                    }
                }

            }
            catch (InterruptedException | IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}