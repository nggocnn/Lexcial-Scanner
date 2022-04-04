package com.lewwcom.lexicalscanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        LexicalScanner scanner =
                new LexicalScanner(ClassLoader.getSystemResourceAsStream("dfa.dat"));

        String input = "abc{123abcde 0987654321\n";
        System.out.println("Input: " + input);
        scanner.scan(new ByteArrayInputStream(input.getBytes()));
    }

}
