/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler_java.apache.commons.csv.*;
import org.apache.commons.lang3.*;
import com.opencsv.*;
import static java.lang.Integer.parseInt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 *
 * @author jesseelfalan
 */
public class Scheduler_java {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String path1 = "/Users/jesseelfalan/Desktop/scheduler_data/R1_blocks.csv"; 
        String path2 = "/Users/jesseelfalan/Desktop/scheduler_data/R2_blocks.csv";
        String path3 = "/Users/jesseelfalan/Desktop/scheduler_data/R3_blocks.csv";
        String path4 = "/Users/jesseelfalan/Desktop/scheduler_data/clinics.csv";
        
        HashMap<String,Resident> R1_list = buildResidentsFromFile(path1, "R1");
        HashMap<String,Resident> R2_list = buildResidentsFromFile(path2, "R2");
        HashMap<String,Resident> R3_list = buildResidentsFromFile(path3, "R3");
        
        HashMap<String,Clinic> Clinics = buildClinicsFromFile(path4);
        Clinics.get("WCHC").stats();
        Clinics.get("CHC").stats();
        Clinics.get("PHC").stats();
        Clinics.get("MTZ").stats();
        
        //TODO:
        //Loop thru clinics
        Clinic curr_clinic = Clinics.get("WCHC");
        //generate perms for wchc
        HashSet<String []> R1_perm = 
        generatePermutations(curr_clinic.R1set.length, R1_list, "R1");  //send cap of R1, entire R1 set
        
        HashSet<String []> R2_perm = 
        generatePermutations(curr_clinic.R2set.length, R2_list, "R2");  //send cap of R2, entire R2 set
        
        HashSet<String []> R3_perm = 
        generatePermutations(curr_clinic.R3set.length, R3_list, "R3");  //send cap of R3, entire R3 set
        
        ArrayList<ArrayList<int []>> block_clinic_heuristic = generateHeuristic(R1_list, R1_perm); //arraylist of resident assignments for blocks in the entire year
               
        
                //TODO: Clean up
                //alterWorkingSets()
                
        
        
        
        
    }
    
    public static HashMap<String, Resident> buildResidentsFromFile(String path, String type){
    ArrayList<Block> blocks = new ArrayList<Block>();
        try{
        
        CSVReader reader = new CSVReader(new FileReader(path));
        
        while(true){
        String [] data = reader.readNext();     
            if (data == null){
                break;
            }
          
            if(data[0].equals("Date")){
            System.out.println("headers");
            
           }else{
                //System.out.println(data);
                
                Block newBlock = new Block(data[0], data[1], data[2], data[3]);
                blocks.add(newBlock);
                
                System.out.println(newBlock.dateRange + " name:" + newBlock.blockName
        + " am: " + java.util.Arrays.toString(newBlock.amDays) + " pm: " + java.util.Arrays.toString(newBlock.pmDays) + " totDays: " + java.util.Arrays.toString(newBlock.totDays));
            }
        }
        
        }catch (Exception e){
            System.out.println("Error occured during file parse, Exception: " + e.toString());
        }
        
        return buildResidentsFromBlocks(type, blocks);
    }
    public static HashMap<String, Resident> buildResidentsFromBlocks(String type, ArrayList<Block> blocks){
       HashMap<String, Resident> residentMap = new HashMap<String, Resident>();
       System.out.println("done reading...\nBuilding Residents...");
       Resident newResident = null;
        char marker = 'A';
        int length = 13;

        for(int x=0; x < length; x++){
        String label = type + "-" + marker;

          if(x == 0){
            newResident = new Resident(label,blocks);
          }else{
            blocks = rotate(blocks, 2);
            newResident = new Resident(label,blocks);
          }
          residentMap.put(newResident.label,newResident);
          System.out.println("label: " + newResident.label + ", blocks: " + newResident.blockLabels + "\n");

          newResident = null;
          marker += 1;
        }
        return residentMap; 

    }
    
    public static <T> ArrayList<T> rotate(ArrayList<T> aL, int shift)
{
    if (aL.size() == 0)
        return aL;

    T element = null;
    //rotate left 2 times
    for(int i = 0; i < shift; i++)
    {
        element = aL.remove(0);
        aL.add(element);
    }

    return aL;
}

    public static HashMap<String,Clinic> buildClinicsFromFile(String path) {
        HashMap<String,Clinic> clinics  = new HashMap<String, Clinic>();
        
        try{
            CSVReader reader = new CSVReader(new FileReader(path));
            while(true){
              String [] data = reader.readNext();  
               if (data == null){
                    break;
                }
              if((data[0]).equals("Clinic")){
                  System.out.println("Headers");
              }else{              
                Clinic c = new Clinic(data[0],parseInt(data[1]),parseInt(data[2]),parseInt(data[3]));
                clinics.put(c.siteName, c);
              }
            }
            
        }catch(Exception e){
            System.out.println("Error on clinic read, Exception: " + e.toString());
        }
        return clinics;
    }
    
    public static HashSet<String []> generatePermutations(int k, HashMap<String,Resident> resident_list, String type){
        
    HashSet<String []> perm = new HashSet<String []>();
    //  ArrayList<String []> perm = new ArrayList<String []>();  
        ArrayList<int[]> subsets = new ArrayList<>();
        int[] s = new int[k];  
        int [] input = new int [resident_list.size()]; //i.e. 13 residents 
        for(int x = 0;x < resident_list.size(); x++){
            input[x] = x+1;
        }
        
        //generate all non-repeat combinations. 
        //Resource: http://stackoverflow.com/questions/29910312/algorithm-to-get-all-the-combinations-of-size-n-from-an-array-java
        if (k <= input.length) {
    // first index sequence: 0, 1, 2, ...
    for (int i = 0; (s[i] = i) < k - 1; i++);  
    subsets.add(getSubset(input, s));
    for(;;) {
        int i;
        // find position of item that can be incremented
        for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--); 
        if (i < 0) {
            break;
        } else {
            s[i]++;                    // increment this item
            for (++i; i < k; i++) {    // fill up remaining items
                s[i] = s[i - 1] + 1; 
            }
            subsets.add(getSubset(input, s));
            }
        }
    } 
        for(int [] set: subsets){
        System.out.println(java.util.Arrays.toString(set));
        String [] letters = new String[set.length];
        
        for(int y = 0; y < set.length; y++){
            
            int val = set[y];
            String label = "";
            switch (val){
                case 1:
                    label = type + "-A";
                    letters[y] = label;
                    break;
                case 2:
                    label = type + "-B";
                    letters[y] = label;
                    break;
                case 3:
                    label = type + "-C";
                    letters[y] = label;
                    break;
                case 4:
                    label = type + "-D";
                    letters[y] = label;
                    break;
                case 5:
                    label = type + "-E";
                    letters[y] = label;
                    break;
                case 6:
                    label = type + "-F";
                    letters[y] = label;
                    break;
                case 7:
                    label = type + "-G";
                    letters[y] = label;
                    break;
                case 8:
                    label = type + "-H";
                    letters[y] = label;
                    break;
                case 9:
                    label = type + "-I";
                    letters[y] = label;
                    break;
                case 10:
                    label = type + "-J";
                    letters[y] = label;
                    break;
                case 11:
                    label = type + "-K";
                    letters[y] = label;
                    break;
                case 12:
                    label = type + "-L";
                    letters[y] = label;
                    break;
                case 13:
                    label = type + "-M";
                    letters[y] = label;
                    break;
               
                    
                            }
        }
        perm.add(letters);
    }
        System.out.println("\n Letter Conversion:");
        for(String [] set : perm){
            System.out.println(java.util.Arrays.toString(set));
        }
        
        System.out.println("\nTotal perm: " + perm.size());
        return perm;
    }
    
    public static int[] getSubset(int[] input, int[] subset) {
    int[] result = new int[subset.length]; 
    for (int i = 0; i < subset.length; i++) 
        result[i] = input[subset[i]];
    return result;
}
    
    public static ArrayList<ArrayList<int[]>> generateHeuristic(HashMap<String, Resident> resident_list, HashSet<String []> perm_list){
        ArrayList<ArrayList<int []>> heuristic_list = new ArrayList<ArrayList<int []>>();
        
        for(String [] perm : perm_list){
            ArrayList<int []> rotation = new ArrayList<int []>();
          for(int x = 0; x < 26; x++){ //for 26 blocks, assign days
            int [] sample = new int [] {0,0,0,0,0,0,0,0,0,0};
            for(int y = 0;y < perm.length; y++){  //each resident in the combo, ex: [R1-B, R1-E, R1-F, R1-M]
                Resident r = resident_list.get(perm[y]); //get by label
                 int [] totDays = r.Blocks.get(x).totDays;
                    for(int z = 0; z < totDays.length; z++){
                      sample[z] += totDays[z];    
                        }
                    }
                rotation.add(sample);
                }
          heuristic_list.add(rotation);
            }
        
        System.out.println("heuristics for permutations: ");
        for(ArrayList<int []> item_list : heuristic_list){
            System.out.println("list size: " + item_list.size());
//            for(int [] sample : item_list){
//            System.out.println(java.util.Arrays.toString(sample));
//            }
        }
        System.out.println("count: " + heuristic_list.size());
        return heuristic_list;
    }
}
