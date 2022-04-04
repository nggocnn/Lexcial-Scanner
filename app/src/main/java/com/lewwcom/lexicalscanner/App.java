package com.lewwcom.lexicalscanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        LexicalScanner scanner =
                new LexicalScanner(ClassLoader.getSystemResourceAsStream("dfa.dat"));

        String input =
                "identifier 1+1 1-1 1/1 1*1 1<1 1>1 1<=1 1>=1 1==1 1!=1 a&&b a||b i=1 123 .123e+123 \"abc132dfz \nasdffadsf\"";
        System.out.println("Input: " + input);
        scanner.scan(new ByteArrayInputStream(input.getBytes()));
    }

}
