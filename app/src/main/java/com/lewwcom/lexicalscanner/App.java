package com.lewwcom.lexicalscanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        LexicalScanner scanner =
                new LexicalScanner(ClassLoader.getSystemResourceAsStream("dfa.dat"));

        String input =
                "float exampleFunction(boolean var1, int var2) {\n" +
                        "\tif(var1) {\n" +
                        "\t\treturn (float)var2;\n" +
                        "\t} else {\n" +
                        "\t\treturn 94e-1;\n" +
                        "\t}\n" +
                        "}\n" +
                        "\n" +
                        "int main() {\n" +
                        "\tboolean b = true;\n" +
                        "\tboolean c = !b;\n" +
                        "\tint d = 4420;\n" +
                        "\tfloat r = exampleFunction(c, d);\n" +
                        "\treturn 0;\n" +
                        "}";
        System.out.println("Input: " + input);
        scanner.scan(new ByteArrayInputStream(input.getBytes()));
    }

}
