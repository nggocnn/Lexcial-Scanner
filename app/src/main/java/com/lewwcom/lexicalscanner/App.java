package com.lewwcom.lexicalscanner;

import java.io.*;
import java.util.Objects;

import static com.google.common.io.Resources.getResource;

public class App {
    public static void main(String[] args) throws IOException {
        InputStream dfaStream = ClassLoader.getSystemResourceAsStream("dfa.dat");
        LexicalScanner scanner = new LexicalScanner(dfaStream);

        InputStream inputCodeStream = ClassLoader.getSystemResourceAsStream("sample.c");
        String inputCode = FileUtil.readTextFromFile(inputCodeStream);

        String outputPath = Objects.requireNonNull(App.class.getResource("")).getFile() + "output.txt";
        File outputFile = new File(outputPath);
        boolean newFile = outputFile.createNewFile();
        OutputStream outputStream = new FileOutputStream(outputFile);

        System.out.println("Input: " + inputCode);
        scanner.scan(new ByteArrayInputStream(inputCode.getBytes()), outputStream);
    }

}
