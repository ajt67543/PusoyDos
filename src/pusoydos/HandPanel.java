package pusoydos;

import java.awt.*;
import javax.swing.*;

public class HandPanel extends JPanel{
    boolean rotated;
    
    public HandPanel(){
        rotated = false;
    }
    public void setRotation(boolean value){
        rotated = value;
    }
    public boolean getRotation(){
        return rotated; 
    }
    @Override
    public void paintComponent(Graphics g){       
        if(rotated){
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
            //System.out.println(this.getWidth() + " " + this.getHeight());
            g2d.rotate(Math.toRadians(90));
            g2d.translate(-this.getWidth() / 2, -this.getHeight() / 2);
            g2d.dispose();
            super.paintComponent(g2d);
        }
        else{
            super.paintComponent(g);
        }
        
    }
    
}
