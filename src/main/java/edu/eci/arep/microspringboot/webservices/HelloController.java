package edu.eci.arep.microspringboot.webservices;

import edu.eci.arep.microspringboot.annotations.RequestMapping;

public class HelloController {
    @RequestMapping("/hello")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
}
