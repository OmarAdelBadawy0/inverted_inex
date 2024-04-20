/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;

import java.util.*;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            if (p.next != null) {                       // if not the last element
                System.out.print("" + p.docId + "," );
            }else{
                System.out.print("" + p.docId );
            }
            p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();          // set the iterator to the beginning of the hash table
        while (it.hasNext()) {                          // while there are more elements in the hash table
            Map.Entry pair = (Map.Entry) it.next();     // get the next element in the hash table
            DictEntry dd = (DictEntry) pair.getValue(); // get the value of the element
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {       // for each file in the collection
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));  // add the file to the sources
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {    // read the file line by line
                    flen += indexOneLine(ln, fid);
                    /// -2-  complete here
                    ///** hint   flen +=  __(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {   // if the file is not found
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }
    //------------------------------------------------------------------

    public void buildIndexBiword(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {       // for each file in the collection
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));  // add the file to the sources
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {    // read the file line by line
                    flen += indexOneLineBiWord(ln, fid);
                    /// -2-  complete here
                    ///** hint   flen +=  __(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {   // if the file is not found
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

    //----------------------------------------------------------------------------

    public int indexOneLine(String ln, int fid) {       // check the line for stop words and stem the words
        int flen = 0;

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {    // if the word is not in the posting list
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }


    //---------------------------------------------------------------------------
    public int indexOneLineBiWord(String ln, int fid) {       // check the line for stop words and stem the words
        int flen = 0;

        String[] words = ln.split("\\W+");
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;

        String prevWord = null;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (prevWord == null){
                prevWord = word;
                continue;
            }
            String wordTmp = word;
            word = prevWord + '_' + word;

            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {    // if the word is not in the posting list
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

            prevWord = wordTmp;
        }
        return flen;
    }
//----------------------------------------------------------------------------
public int indexOneLinePositionalIndex(String ln, int fid) {       // check the line for stop words and stem the words
    int flen = 0;

    String[] words = ln.split("\\W+");
    //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
    flen += words.length;
    for (String word : words) {
        word = word.toLowerCase();
        if (stopWord(word)) {
            continue;
        }
        word = stemWord(word);
        // check to see if the word is not in the dictionary
        // if not add it
        if (!index.containsKey(word)) {
            index.put(word, new DictEntry());
        }
        // add document id to the posting list
        if (!index.get(word).postingListContains(fid)) {    // if the word is not in the posting list
            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
            if (index.get(word).pList == null) {
                index.get(word).pList = new Posting(fid);
                index.get(word).last = index.get(word).pList;
            } else {
                index.get(word).last.next = new Posting(fid);
                index.get(word).last = index.get(word).last.next;
            }
        } else {
            index.get(word).last.dtf += 1;
        }
        //set the term_fteq in the collection
        index.get(word).term_freq += 1;
        if (word.equalsIgnoreCase("lattice")) {

            System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
        }

    }
    return flen;
}
//----------------------------------------------------------------------------




    boolean stopWord(String word) {     // check if the word is a stop word
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------  

    String stemWord(String word) { //skip for now

        // Stemmer s = new Stemmer();
        // s.addString(word);
        // s.stem();
        // return s.toString();
        return word;
    }

    //----------------------------------------------------------------------------  
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;

        while (pL1 != null && pL2 != null) {                    // 2 while p1  != NIL and p2  != NIL

            if (pL1.docId == pL2.docId) {                       // 3 do if docID ( p 1 ) = docID ( p2 )

                if (answer == null) {
                    answer = new Posting(pL1.docId, pL1.dtf);   // 4   then ADD ( answer, docID ( p1 ))
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId, pL1.dtf);
                    last = last.next;
                }

                pL1 = pL1.next;     //          5       p1 ← next ( p1 )
                pL2 = pL2.next;     //          6       p2 ← next ( p2 )
                
            } else if (pL1.docId < pL2.docId) { //7 else if docID ( p1 ) < docID ( p2 )


                pL1 = pL1.next;     // 8 then p1 ← next ( p1 )
            } else {

                pL2 = pL2.next; // 9 else p2 ← next ( p2 )
            }

        }
        return answer;  // 10 return answer
    }

    public String find_24_01(String phrase) { // any mumber of terms non-optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        
        try {
            words = sort(words);    
            Posting posting = index.get(words[0].toLowerCase()).pList;
            int i = 1;
            while (i < len) {
                posting = intersect(posting, index.get(words[i].toLowerCase()).pList);  // intersect the posting lists
                i++;
            }
            while (posting != null) {
                //System.out.println("\t" + sources.get(num));
                result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
                posting = posting.next;
            }
        } catch (Exception e) { // if the word is not in the dictionary
            System.out.println("There is no such word in the collection " + phrase);
            result = "";
        }

        //fix this if word is not in the hash table will crash...
        return result;
    }

    public String find_position(String phrase) { // any mumber of terms non-optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        try {
            words = sort(words);
            Posting posting = index.get(words[0].toLowerCase()).pList;
            int i = 1;
            while (i < len) {
                posting = intersect(posting, index.get(words[i].toLowerCase()).pList);  // intersect the posting lists
                i++;
            }
            while (posting != null) {
                //System.out.println("\t" + sources.get(num));
                result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
                posting = posting.next;
            }
        } catch (Exception e) { // if the word is not in the dictionary
            System.out.println("There is no such word in the collection " + phrase);
            result = "";
        }

        //fix this if word is not in the hash table will crash...
        return result;
    }
    
    //---------------------------------
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {    
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {      // if the first word is greater than the second word
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

     //---------------------------------

    public void store(String storageName) {     // store the index in a file
        try {
            String pathToStorage = storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================    
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------    
    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/"+storageName;    // the local path to the file
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));     
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {    // read the file line by line   
                if (ln.equalsIgnoreCase("section2")) {  
                    break;
                }
                String[] ss = ln.split(",");        // split the line into fields by the comma
                int fid = Integer.parseInt(ss[0]);        // get the document id
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {   // if the line is end then break
                    break;
                }
                String[] ss1 = ln.split(";");       // split the line into fields by the semicolon
                String[] ss1a = ss1[0].split(",");  // split the first field into term, doc_freq, term_freq
                String[] ss1b = ss1[1].split(":");  // split the second field into the posting list
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) { // if the posting list is empty
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1])); // add the posting list to the dictionary
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1])); 
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
