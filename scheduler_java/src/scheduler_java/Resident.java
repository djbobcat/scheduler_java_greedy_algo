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
public class Resident {
    public String label;
    public String name; //name of resident
    public String blockLabels;
    public ArrayList<Block> Blocks;
    
    
    public Resident(String _label, ArrayList<Block> _blocks){
        this.label =  _label;
        this.Blocks = _blocks;
        this.blockLabels = getLabels(this.Blocks);
    }
    
//    public void alterBlocks(){
//        
//    }
    
    private String getLabels(ArrayList<Block> blocks){
        String str = "";
        for(int x = 0; x < blocks.size(); x++){
            str = str.concat(blocks.get(x).blockName + ";");
        }
        return str;
    }
    
    
}
