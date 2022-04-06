package com.lewwcom.lexicalscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class App {

    @SuppressWarnings("java:S899")
    public static void main(String[] args) throws IOException {
        InputStream dfaStream = ClassLoader.getSystemResourceAsStream("dfa.dat");
        LexicalScanner scanner = new LexicalScanner(dfaStream);

        InputStream inputCodeStream = ClassLoader.getSystemResourceAsStream("sample.c");

        String outputPath =
                Objects.requireNonNull(App.class.getResource("")).getFile() + "output.txt";
        File outputFile = new File(outputPath);
        outputFile.createNewFile();
        OutputStream outputStream = new FileOutputStream(outputFile);

        scanner.scan(inputCodeStream, outputStream);
    }

}
