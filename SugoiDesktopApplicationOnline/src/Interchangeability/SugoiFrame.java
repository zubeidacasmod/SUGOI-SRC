/*
 * أعوذ بالله من الشيطان الرجيم
 * بسم الله الرحمن الرحيم
 * 26 March 2014
 * Created by: Zubeida C. Khan zkhan@csir.co.za / zubzzz@hotmail.com
 */
package Interchangeability;
import java.awt.*;
import javax.swing.*;
import java.util.Properties;
import java.io.*;

/**
 *
 * @author Zubeida
 */
public class SugoiFrame {
    
    public static void main(String[] args) {
        String [] proparray = new String[2];
        for (int i=0;i< proparray.length;i++){
            proparray[i]="";
        }
        File propertyfile = new File("proxy.txt");
        BufferedReader reader = null;
        Boolean b= false;
        
   Properties systemSettings = System.getProperties();    
try {
    reader = new BufferedReader(new FileReader(propertyfile));
     int i=0;
     String temp;
    while ((temp = reader.readLine()) != null){
    //String temp =reader.readLine();
    
        
        temp =temp.trim();
    //System.out.println(temp);
    proparray = temp.split("=");
    i++;
    if ( proparray.length==2 ){
        System.out.println(proparray[0]);
        System.out.println(proparray[1]);
        proparray[0] =proparray[0].trim();
        proparray[1] =proparray[1].trim();
        
        if (proparray[0].equals("Sugoi.proxyHost")){
           systemSettings.put("http.proxyHost", proparray[1]);
          System.setProperties(systemSettings); 
       
         
        }
        
        else if (proparray[0].equals("Sugoi.proxyPort")){
           systemSettings.put("http.proxyPort", proparray[1]);
         System.setProperties(systemSettings);
        }
        
        else if (proparray[0].equals("Sugoi.proxyUser")){
            systemSettings.put("http.proxyUser", proparray[1]);
        System.setProperties(systemSettings);
        }
        
        else if (proparray[0].equals("Sugoi.proxyPassword")){
            systemSettings.put("http.proxyPassword", proparray[1]);
            System.setProperties(systemSettings);             
        }
    }
  //  temp =reader.readLine();
    }
    
} catch (Exception e) {
    System.out.println(e.toString()+" ff");
    e.printStackTrace();
}

    

        Sugoi sugoi = new Sugoi();
        sugoi.init();
        JFrame myFrame = new JFrame("Sugoi! Online Desktop Application"); // create frame with title        
        sugoi.init(); //start applet
        myFrame.add(sugoi, BorderLayout.CENTER);
        myFrame.pack(); // set window to appropriate size (for its elements)
        myFrame.setVisible(true); // usual step to make frame visible
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
