package pusoydos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


public class HelpWindow extends JDialog {

    public HelpWindow(JFrame parent, String title, Dimension screenSize) {
        super(parent, title);
        final HelpWindow thisWindow = this;
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setSize((screenSize.width * 1 / 4), (screenSize.height * 1 / 2));
        setLocation((screenSize.width / 2) - (this.getWidth() / 2),
                (screenSize.height / 2) - (this.getHeight() / 2));
        setLayout(new BorderLayout());

        JPanel textPanel = new JPanel();
        JTextPane jtp = new JTextPane();
        JScrollPane jsp = new JScrollPane(jtp);
        jsp.setSize(jtp.getSize());
        jtp.setFont(new Font("", Font.PLAIN, 20));
        jtp.setPreferredSize(new Dimension(this.getWidth() * 9 / 10,
                this.getHeight()));
        jsp.setPreferredSize(new Dimension(this.getWidth() * 9 / 10,
                this.getHeight() * 8 / 10));
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        jtp.setParagraphAttributes(center, false);
        jtp.setEditable(false);        
        String s = "Objective of the game\n"
                + "\n"
                + "The primary objective of the game is to shed all your cards. The last player with remaining cards loses.\n"
                + "\n"
                + "Gameplay\n"
                + "\n"
                + "From a standard 52 card deck, four players are dealt with 13 cards each. The player with 3C must shed any combination with this card. Players take turns in a clockwise direction. In each turn, a player may either: shed cards which has a higher rank and has the same number cards as the previous move, or the player may pass.\n"
                + "\n"
                + "Control\n"
                + "\n"
                + "A player is in control when all players pass after his/her move or when the previous player has shed all of his/her cards. A player in control must shed any combination (not necessarily higher than the previous move) and is not allowed to pass.\n"
                + "\n"
                + "Combinations\n"
                + "\n"
                + "Singles - From lowest to highest, cards are ranked as: 3-4-5-6-7-8-9-10-J-Q-K-A-2. Cards with the same face values are ranked by suit, from low to high: Clubs-Spades-Hearts-Diamonds. Examples: 7H beats 3H. 10H beats 10C. 2D beats all cards.\n"
                + "\n"
                + "Pairs are two cards with the same face value. Pairs are ranked by face value. Two pairs of the same face value are ranked by the highest singles of the pairs. Examples: 10H-10D beats 5S-5C. 3D-3C beats 3H-3S. 2D-2* beats all other pairs.\n"
                + "\n"
                + "Three-of-a-kinds are three cards with the same face value and are ranked by face value.\n"
                + "\n"
                + "5-combinations - these are listed below, in increasing rank.\n"
                + "\n"
                + "    Straight - are 5 cards with consecutive face values. Straights are ranked by the highest single in each straight. Wrap-arounds are not allowed. Examples: 3*4*5*6*7C is the lowest straight. 1*2*3*4*5 and Q*K*A*2*3 are not straights.\n"
                + "\n"
                + "    Flush - are 5 cards with the same suite. Flushes are ranked the by the suite of each flush. Flushes of the same suite are ranked by highest single in each flush.\n"
                + "\n"
                + "    Full House - are composed of a Three-of-a-kind and a Pair. Full houses are ranked by the Three-of-a-kind used.\n"
                + "\n"
                + "    Four-of-a-kind - are composed of Four cards with the same face value, and a Single of any value. Four-of-a-kinds are ranked by the face value of the four cards used.\n"
                + "\n"
                + "    Straight flush - are 5 cards that is both a straight and a flush. Straight flushes are ranked the by the highest single in each straight flush. ";

        jtp.setText(s);
        textPanel.add(jsp, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton ok = new JButton("ok");
        ok.setPreferredSize(new Dimension(this.getWidth() * 25 / 100,
                this.getHeight() * 1 / 20));
        ok.setFont(new Font("", Font.PLAIN, 25));
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae){
                thisWindow.dispatchEvent(new WindowEvent(thisWindow, 
                        WindowEvent.WINDOW_CLOSING));
            }
        });
        buttonPanel.add(ok);
        this.add(textPanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.SOUTH);
        setVisible(false);
    }
}
