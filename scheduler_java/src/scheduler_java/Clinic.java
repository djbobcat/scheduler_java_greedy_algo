/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler_java;

import java.util.ArrayList;

/**
 *
 * @author jesseelfalan
 */
public class Clinic {
    public String siteName;
    public ArrayList<Resident> Residents;
    public String [] R1set;
    public String [] R2set;
    public String [] R3set;
    public int [] slots;
    
    public Clinic(String site, int R1_num, int R2_num, int R3_num){
        this.siteName = site;
        this.R1set = new String[R1_num];
        this.R2set = new String[R2_num];
        this.R3set = new String[R3_num];
        this.Residents = new ArrayList<Resident>();
        this.slots = new int[]{0,0,0,0,0,0,0,0,0,0};
    }
    
    public void stats(){
        System.out.println("Clinic: " + this.siteName + ", R1_num: " + this.R1set.length + ", R2_num: " + this.R2set.length + ", R3_num: " + this.R3set.length);
    }
    
    public void printResidents(){
        System.out.println("Residents for site:" + siteName );
        for(Resident resident : this.Residents){
            System.out.println(resident.label + "\n");
        }
    }
    
}
