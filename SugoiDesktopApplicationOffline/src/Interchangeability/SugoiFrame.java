/*
 * أعوذ بالله من الشيطان الرجيم
 * بسم الله الرحمن الرحيم
 * 26 March 2014
 * Created by: Zubeida C. Khan zkhan@csir.co.za / zubzzz@hotmail.com
 */
package Interchangeability;
import java.awt.*;
import javax.swing.*;
import java.io.*;


/**
 *
 * @author Zubeida
 */
public class SugoiFrame {
    public static void main(String[] args) {
       // File f = new File("f");
        
       
        Sugoi sugoi = new Sugoi();
        sugoi.init();
        JFrame myFrame = new JFrame("Sugoi! Offline Desktop Application"); // create frame with title        
        sugoi.init(); //start applet
        myFrame.add(sugoi, BorderLayout.CENTER);
        myFrame.pack(); // set window to appropriate size (for its elements)
        myFrame.setVisible(true); // usual step to make frame visible
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
