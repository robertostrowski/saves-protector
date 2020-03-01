package rozi;

import lombok.Data;

public class HelloWorld {

    public static void main(String[] args) {
        System.out.println(new World().getText());
    }
}

@Data
class World {
    private String text;
}