package pusoydos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.text.*;

public class StatsWindow extends JDialog{
    int [] stats = new int[4];
    JTextPane textPane;
    
    public void setStats(int [] array){
        stats = array;
        setText();
    }
    
    public StatsWindow(JFrame frame, String title, Dimension screenSize){
        super(frame, title);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setSize((screenSize.width * 1 / 4), (screenSize.height * 1 / 2));
        setLocation((screenSize.width / 2) - (this.getWidth() / 2),
                (screenSize.height / 2) - (this.getHeight() / 2));
        setLayout(new BorderLayout());
        JPanel textPanel = new JPanel();
        textPanel.add(setTextPane());
        add(textPanel, BorderLayout.NORTH);
        JPanel panelButton = new JPanel();
        panelButton.add(setOkButton());
        add(panelButton, BorderLayout.SOUTH);
        setVisible(false);
        
    }
    
    private JTextPane setTextPane(){
        textPane = new JTextPane();
        textPane.setOpaque(true);
        textPane.setFont(new Font("", Font.BOLD, 30));
        textPane.setForeground(Color.white);
        textPane.setPreferredSize(new Dimension(this.getWidth() * 9 / 10 ,
                this.getHeight() * 8 / 10));
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        textPane.setParagraphAttributes(center, false);
        textPane.setEditable(false);
        String s = "\n"
                + "\n"
                + "First-Place Victories: " + stats[0] + "\n" 
                + "\n"
                + "\n"
                + "Second-Place Victories: " + stats[1] + "\n"
                + "\n"
                + "\n"
                + "Third-Place Victories: " + stats[2] + "\n"
                + "\n"
                + "\n"
                + "Last-Place Victories: " + stats[3] + "\n";
        textPane.setText(s);
        textPane.setBackground(new Color(30, 90, 25));
        return textPane;
    }
    
    private void setText(){
        String s = "\n"
                + "\n"
                + "First-Place Victories: " + stats[0] + "\n" 
                + "\n"
                + "\n"
                + "Second-Place Victories: " + stats[1] + "\n"
                + "\n"
                + "\n"
                + "Third-Place Victories: " + stats[2] + "\n"
                + "\n"
                + "\n"
                + "Last-Place Victories: " + stats[3] + "\n";
        textPane.setText(s);
    }
    
    private JButton setOkButton(){
        final StatsWindow thisWindow = this;
        JButton ok = new JButton("ok");
        ok.setPreferredSize(new Dimension(this.getWidth() * 30 / 100,
                this.getHeight() * 7 / 100));
        ok.setFont(new Font("", Font.PLAIN, 25));
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                thisWindow.dispatchEvent(new WindowEvent(thisWindow, 
                        WindowEvent.WINDOW_CLOSING));
            }
        });
        return ok;
    }
}
