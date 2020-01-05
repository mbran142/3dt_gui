import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TTTSocket {

    //static constants
    public final static int
            HOST = 1,
            JOIN = 2,
            DISCONNECT = -1,
            CONNECTED = -2,
            READY = -3;

    //instance
    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;

    //returns either a status or a move in the form of a 3 digit number
    public int requestMove() {
        try {

            String msg = inStream.readUTF();

            if (msg.equals("ready"))
                return READY;

            else return ((msg.charAt(0) - '0') * 100) +
                    ((msg.charAt(1) - '0') * 10) +
                    msg.charAt(2) - '0';

        } catch (Exception e) {
            return DISCONNECT;
        }
    }

    public int sendMove(String move) {
        try {
            outStream.writeUTF(move);
            return CONNECTED;
        } catch (Exception e) {
            return DISCONNECT;
        }
    }

    abstract public void close();
    abstract public boolean isHost();
}

class TTTServer extends TTTSocket {

    private ServerSocket serverSocket;

    public TTTServer(int port) throws IOException {

        //if this block throws IOException, connection failed
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(port));
//        serverSocket = new ServerSocket(port);
//        serverSocket.setReuseAddress(true);
        socket = serverSocket.accept();
        inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    public void close() {
        try {
            serverSocket.close();
            socket.close();
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHost() {
        return true;
    }
}

class TTTClient extends TTTSocket {

    public TTTClient(int port, String ip) throws IOException {

        //if this block throws IOException, connection failed
        socket = new Socket(ip, port);
        inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    public void close() {
        try {
            inStream.close();
            outStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHost() {
        return false;
    }
}
