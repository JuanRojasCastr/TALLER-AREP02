package edu.eci.arep.microspringboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private Map<String, Method> methods;

    public void start(Map<String, Method> methods) throws IOException {
        this.methods = methods;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35001);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35001");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String file = "";

            while ((inputLine = in.readLine()) != null) {
//                System.out.println("Received: " + inputLine);

                if (firstLine) {
                    file = inputLine.split(" ")[1];
                    firstLine = false;
                }


                if (!in.ready()) {
                    break;
                }
            }
            String resp = "";
            for (String key : this.methods.keySet()) {
                if (file.startsWith(key)) {
                    resp = invokeMethod(this.methods.get(file));
                }
            }

            outputLine = "HTTP/1.1 " + 200 + " OK\r\n"
                    + "Content-Type: text/html \r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "</head>"
                    + "<body>"
                    + resp
                    + "</body>"
                    + "</html>" + inputLine;

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public String invokeMethod(Method method) {
        try {
            return (String)method.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return "Service error";
    }
}
