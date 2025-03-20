package a;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {

        List<String> nombres = List.of("Ana", "Juan", "Carlos");
        for (String nombre : nombres) {
            System.out.println(nombre);
        }

        nombres.forEach(System.out::println);

        nombres.stream().filter(n -> n.startsWith("J"))
                .forEach(System.out::println);

        nombres.stream()
                .filter(n -> n.startsWith("J"))
                .forEach(System.out::println);

    }
}
