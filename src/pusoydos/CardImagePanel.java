package pusoydos;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

class CardImagePanel extends JPanel implements MouseListener, 
        MouseMotionListener{

    private BufferedImage cImage;
    private int value;
    private boolean rotate;
    private boolean clicked;

    public CardImagePanel() {
        cImage = null;
        value = -1;
        rotate = false;
        clicked = false;
    }

    public CardImagePanel(BufferedImage image, int num) {
        cImage = image;
        value = num;
        rotate = false;
        clicked = false;
    }

    public BufferedImage getCImage() {
        return cImage;
    }

    public int getValue() {
        return value;
    }
    
    public void setClicked(boolean cond){
        clicked = cond;
    }
    
    public boolean getClicked(){
        return clicked;
    }

    public void setRotate(boolean cond) {
        rotate = cond;
    }
    
    public void addMouseListeners(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void removeListeners(){
        this.removeMouseListener(this);
        this.removeMouseMotionListener(this);
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (rotate) {
            return new Dimension(cImage.getHeight(), cImage.getWidth());
        } else {
            return new Dimension(cImage.getWidth(), cImage.getHeight());
        }
    }
    
    public void resetPosition(){
        if(clicked){
            this.setLocation(this.getX(), this.getY() + 50);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(cImage != null){
            if(rotate){
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
                g2d.rotate(Math.toRadians(90));
                g2d.translate(-cImage.getWidth(this) / 2, -cImage.getHeight(this) / 2);
            }
            g.drawImage(cImage, 0, 0, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        clicked = !clicked;
    }

    @Override
    public void mousePressed(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if(!clicked){
            this.setLocation(this.getX(), this.getY() - 50);
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if(!clicked){
            this.setLocation(this.getX(), this.getY() + 50);
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
