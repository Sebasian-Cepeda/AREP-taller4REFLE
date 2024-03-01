package juan.taller4.apiMovie;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import juan.taller4.interfaces.Component;
import juan.taller4.interfaces.GetMapping;

/**
 * Web server class to use the web application
 * 
 * @author juan cepeda
 */
public class WebServer {

    private static final int PORT = 35000;
    private static final WebServer _instance = getInstace();
    private static final Map<String, WebService> handlers = new HashMap<>();
    private static final APIMovies apiMovie = new APIMovies();
    private static final Map<String, Method> methods = new HashMap<>();

    /**
     * method that returns the instance of this class
     * 
     * @return the instance of this class
     */
    public static WebServer getInstace() {
        return _instance;
    }

    /**
     * Method that start the web server
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void startServer()
            throws Exception {
        ServerSocket serverSocket = null;
        classes();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            try (Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("Listo para recibir ...");
                String inputLine = in.readLine();
                String method = inputLine.split(" ")[0];
                String petition = inputLine.split(" ")[1];
                String outputLine;

                String query = extractQuery(petition);

                if (isSparkRequest(petition)) {
                    String newUri = removeSparkPrefix(petition);
                    outputLine = handleSparkRequest(newUri, query, clientSocket.getOutputStream());
                } else if (isComponent(petition)) {
                    String uri = removeComponentPrefix(petition);
                    System.out.println("prueba: " + uri);

                    if (methods.containsKey(uri)) {
                        Method invokedMethod = methods.get(uri);
                        System.out.println("methodo: " + invokedMethod);
                        outputLine = (invokedMethod.getParameterCount() == 1)
                                ? petitionPage("/component.html",
                                        clientSocket.getOutputStream()).replace("{resp}",
                                                invokedMethod.invoke(null, (Object) query).toString())
                                : petitionPage("/component.html",
                                        clientSocket.getOutputStream()).replace("{resp}",
                                                invokedMethod.invoke(null).toString());

                    } else {
                        outputLine = errorPage("/notFound.html", clientSocket.getOutputStream());
                    }
                } else {
                    boolean mv = petition.contains("/film?name=");
                    outputLine = (mv) ? movieInfo(query, clientSocket.getOutputStream())
                            : petitionPage(petition, clientSocket.getOutputStream());
                }

                out.println(outputLine);
            } catch (IOException e) {
                System.err.println("Error handling request: " + e.getMessage());
            }
        }
        serverSocket.close();

    }

    private static String extractQuery(String petition) throws URISyntaxException {
        URI myURI = new URI(petition);
        String query = myURI.getQuery();
        return (query != null) ? query.split("=")[1] : "";
    }

    private static boolean isSparkRequest(String petition) {
        return petition.startsWith("/spark");
    }

    private static boolean isComponent(String petition) {
        return petition.startsWith("/component");
    }

    private static String removeComponentPrefix(String petition) throws URISyntaxException {
        URI uri = new URI(petition.replace("/component", ""));
        return uri.getPath();
    }

    private static String removeSparkPrefix(String petition) {
        return petition.replace("/spark", "");
    }

    private static String handleSparkRequest(String newUri, String query, OutputStream outputStream) {
        try {
            URI uri = new URI(newUri);
            String path = uri.getPath();

            if (handlers.containsKey(path)) {
                return petitionPage(handlers.get(path).handle(query), outputStream).replace("{name}", query);
            } else if (path.contains("css") || path.contains("jpg") || path.contains("js")) {
                return petitionPage(path, outputStream);
            } else {
                return errorPage("/notFound.html", outputStream);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        }
    }

    private static String ok() {
        return "HTTP/1.1 200 OK\r\n" + "Content-Type: ";
    }

    private static String not() {
        return "HTTP/1.1 404 NOT FOUND\r\n" + "Content-Type: ";
    }

    private static String errorPage(String file, OutputStream op) {
        return not() + getMimeType(file) + "\r\n" + "\r\n" + getStaticFile(file, op);
    }

    private static String petitionPage(String filePetition, OutputStream op) {
        return ok() + getMimeType(filePetition) + "\r\n" + "\r\n" + getStaticFile(filePetition, op);
    }

    private static String movieInfo(String name, OutputStream ops) {
        try {
            JsonObject resp = apiMovie.searchMovie(name);
            JsonElement title = resp.get("Title");
            JsonElement poster = resp.get("Poster");
            JsonElement director = resp.get("Director");
            JsonElement plot = resp.get("Plot");

            String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type:text/html\r\n" + "\r\n"
                    + getStaticFile("/movieInfo.html", ops).replace("{Title}", title.toString())
                            .replace("\"{Poster}\"", poster.toString()).replace("{Directors}", director.toString())
                            .replace("{Plot}", plot.toString());

            return outputLine;
        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        }
    }

    private static String mainPage(String file, OutputStream ops) {
        String contentType = getMimeType(file);
        String content = getStaticFile(file, ops);

        String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type:" + contentType + "\r\n" + "\r\n" + content;

        return outputLine;
    }

    private static String getMimeType(String file) {
        if (file.endsWith(".html") || file.endsWith("/")) {
            return "text/html";
        } else if (file.endsWith(".css")) {
            return "text/css";
        } else if (file.endsWith(".js")) {
            return "application/javascript";
        } else if (file.endsWith(".jpg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }

    private static String getStaticFile(String file, OutputStream ops) {
        Path path = (file.equals("/")) ? Paths.get(getStaticFilesDirectory() + "/movie.html")
                : Paths.get(getStaticFilesDirectory() + file);

        try {
            Charset charset = Charset.forName("UTF-8");
            StringBuilder outputLine = new StringBuilder();
            byte[] bytes;

            if (file.endsWith(".jpg")) {
                bytes = getAnImage(file);
                String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: image/jpeg\r\n" + "Content-Length: "
                        + bytes.length + "\r\n" + "\r\n";
                ops.write(response.getBytes());
                ops.write(bytes);
            } else {
                try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputLine.append(line).append("\n");
                    }
                }
            }

            return outputLine.toString();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return "HTTP/1.1 404 Not Found\r\n\r\n";
        }
    }

    private static byte[] getAnImage(String file) {

        Path image = Paths.get("target/classes/public/static" + file);

        try {
            return Files.readAllBytes(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handleGetRequest(String path, WebService handler) {
        handlers.put(path, handler);
    }

    public static void handlePostRequest(String path, WebService handler) {
        handlers.put(path, handler);
    }

    private static void classes() throws IOException, ClassNotFoundException {
        Set<String> file = new HashSet<>();
        try (DirectoryStream<Path> tm = Files
                .newDirectoryStream(Paths.get("target/classes/juan/taller4/controller"))) {
            for (Path path : tm) {
                if (!Files.isDirectory(path)) {
                    file.add(path.toString());
                }
            }
        }

        for (String files : file) {
            String name = files.replace(".class", "").replace("target\\classes\\", "").replace("\\", ".");
            Class<?> c = Class.forName(name);

            if (c.isAnnotationPresent(Component.class)) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        System.out.println(m.getAnnotation(GetMapping.class).value());
                        methods.put(m.getAnnotation(GetMapping.class).value(), m);
                    }
                }
            }
        }
    }

    public static String getStaticFilesDirectory() {
        return "target/classes/public/static";
    }
}
