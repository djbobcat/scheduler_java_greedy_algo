/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler_java;

import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;
/**
 *
 * @author jesseelfalan
 */
public class Block {
  //public Date date;
  public String dateRange;
  public String blockName;
  public int [] amDays;
  public int [] pmDays;
  public int [] totDays;


  public Block(String _date, String _name, String _am, String _pm){
    this.dateRange = _date;
    this.blockName = _name.replace("\"","");
    this.amDays = encodeDays(_am);
    this.pmDays = encodeDays(_pm);
    this.totDays = ArrayUtils.addAll(this.amDays, this.pmDays);
    
  }

   private int [] encodeDays(String days){
     int [] encoded = {0,0,0,0,0};
     String [] array = days.split(",");
  
      for (String day : array) {
          day = day.trim();
          
          switch (day) {
              case "M":
                  encoded[0] = 1;
                  break;
              case "T":
                  encoded[1] = 1;
                  break;
              case "W":
                  encoded[2] = 1;
                  break;
              case "Th":
                  encoded[3] = 1;
                  break;
              case "F":
                  encoded[4] = 1;
                  break;
          }
      }
   return encoded;
     }
}
