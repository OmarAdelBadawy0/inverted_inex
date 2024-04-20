/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import invertedIndex.Index5;

import javax.xml.transform.Result;

/**
 *
 * @author ehab
 */
public class Test {

    public static void main(String args[]) throws IOException {
        Index5 index = new Index5();
        //|**  change it to your collection directory
        //|**  in windows "C:\\tmp11\\rl\\collection\\"

        //C:\Users\aseme\Desktop\tmp11
        //C:\Users\aseme\Desktop\tmp11\tmp11\rl\collection
        String files = "C:/Users/aseme/Desktop/tmp11/tmp11/rl/collection/";


        File file = new File(files);

        //| String[]     list()
        //|  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();
        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }

        Scanner inn = new Scanner(System.in);
        String choice = inn.nextLine();


        if (choice.contains("\"")){
            choice = choice.replace(' ','_');
            if (choice.charAt(0) == ('\"')){
                index.buildIndexBiword(fileList);
            }else {
                Index5 indexnono = new Index5();
                index.buildIndex(fileList);
                indexnono.buildIndexBiword(fileList);
                String ByWordKey = choice.substring(choice.indexOf('"')+1,choice.length()-1).toLowerCase();
                String InvertedIndexKey = choice.substring(0,choice.indexOf('"')).toLowerCase();
                System.out.println(ByWordKey);
                System.out.println(InvertedIndexKey);
                try {
                    Posting posting = indexnono.index.get(ByWordKey).pList;
                    String res = index.Bounsfind_24_01(InvertedIndexKey,posting);
                    System.out.println(res + " From Res");
                }
                catch (Exception e){
                    System.out.println("There is no " + ByWordKey + " in the collection");
                }

            }
        }else {
            System.out.println("Which index type u want to use:\n 1- inverted index\n 2- Positional index ");
            Scanner input = new Scanner(System.in);
            int chos = input.nextInt();

            if (chos == 1){                 // the defult inverted index
                index.buildIndex(fileList);
            } else if (chos == 2) {
                String result = index.BuildPositional(fileList,choice.toLowerCase());
                System.out.println(result);
            }
        }



        index.store("index");
        index.printDictionary();

        String test3 = "data  should plain greatest comif"; // data  should plain greatest comif
        System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrase = "";

        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();


/// -3- ** complete here **
            String result = index.find_24_01(phrase);
            if (!phrase.isEmpty() && !result.equals("")) {
                System.out.println("The intersection between phrase  = \n" + result);
            }

        } while (!phrase.isEmpty());


    }
}
