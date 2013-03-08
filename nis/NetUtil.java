package nis;

import java.io.*;
import java.net.*;
import java.util.*;

public class NetUtil {// Contains all the static methods used to transmit data across the network

    public static String getString(Socket sock) throws Exception {// Gets aString from the socket, assumes the next object is a string

        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        Object o = ois.readObject();

        if (o instanceof String) {
            return o.toString();
        } else {
            System.err.println("Error: Expected a string");
        }

        return "";

    }

    public static void sendMessage(Object message, Socket sock) throws Exception {// Sends an object across the socket

        System.out.println("Sent: " + message);
        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
        oos.writeObject(message);
        oos.flush();

    }

    public static Object getMessage(Socket sock) throws Exception {//Gets an object from the socket

        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        Object o = ois.readObject();

        return o;

    }
}
