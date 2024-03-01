package juan.taller4;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import juan.taller4.apiMovie.Cache;
import juan.taller4.apiMovie.WebServer;

/**
 * Main class to start the application
 * 
 * @author Juan cepeda
 * 
 * 
 */
public class Main {

    public static void main(String[] args) {
        try {

            WebServer.handleGetRequest("/hello", (path) -> {
                return "/hello.html";
            });

            WebServer.handlePostRequest("/hellopost", (path) -> {
                return "/post.html";
            });

            WebServer.startServer();
        } catch (Exception e) {
            System.out.println("server error: " + e.getMessage());
        }

    }
}