package com.lewwcom.lexicalscanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 */
public class App {
    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        LexicalScanner scanner =
                new LexicalScanner(ClassLoader.getSystemResourceAsStream("dfa.dat"));

        String input = "abasrgsdc_asdf2122{123abcde 0987654321\n kjughasdkfjh 12980 \n kjha";
        System.out.println("Input: " + input);
        scanner.scan(new ByteArrayInputStream(input.getBytes()));
    }

}
