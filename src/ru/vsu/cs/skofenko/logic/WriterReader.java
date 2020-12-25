package ru.vsu.cs.skofenko.logic;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class WriterReader {

    public static void writeBestScore(String score){
        try(PrintWriter pw = new PrintWriter("bestScore.txt")){
            pw.println(score);
        }
        catch (Exception ignored){}
    }

    public static int readBestScore(){
        try {
            Scanner scanner =new Scanner(new File("bestScore.txt"));
            return scanner.nextInt();
        }
        catch (Exception exception){
            return 0;
        }
    }
}
