package com.lewwcom.lexicalscanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class FileUtil {
    static public String readTextFromFile(InputStream input) {
        Scanner myReader = new Scanner(input);
        StringBuilder stringBuilder = new StringBuilder();
        while (myReader.hasNextLine()) {
            String line = myReader.nextLine();
            stringBuilder.append(line).append("\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        myReader.close();
        return stringBuilder.toString();
    }
}
