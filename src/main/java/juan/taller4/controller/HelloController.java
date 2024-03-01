package juan.taller4.controller;

import juan.taller4.interfaces.Component;
import juan.taller4.interfaces.GetMapping;

@Component
public class HelloController {
    @GetMapping("/spring")
    public static String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/compoQuery")
    public static String componentQuery(String query) {
        return "Su query es: " + query;
    }

    @GetMapping("/potencia")
    public static Double potencia(String query) {
        double base = Double.parseDouble(query);
        return Math.pow(base, 4);
    }

}
