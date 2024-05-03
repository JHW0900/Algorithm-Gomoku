import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class StoneDraw implements MouseListener {

    JPanel contentPane = null;
    static final int STONE_SIZE = 25;
    Color StoneColor = Color.white;

    int  [][]OmockBoard = new int[15][15];

    public StoneDraw(JPanel c) {
        super();
        contentPane = c;

        for(int i=0; i<15; i++) {
            for(int j=0; j<15; j++) {
                OmockBoard[i][j] = -1;
            }
        }
    }

    public void AIStoneDraw() {
        // TODO: GameTree 3장 이어서

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            OmockBoard[i][j] = 0;
        } else {
            StoneColor = Color.black;
            OmockBoard[i][j] = 1;
        }

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);
        g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);

        if(victoryCondition(i, j, OmockBoard[i][j])){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int i = (e.getX() - 10 + 15) / 30;
        int j = (e.getY() - 10 + 15) / 30;

        System.out.println(i + ", " + j);

        int cX =  i * 30 + 10 - (STONE_SIZE / 2);
        int cY =  j * 30 + 10 - (STONE_SIZE / 2);

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            OmockBoard[i][j] = 0;
        } else {
            StoneColor = Color.black;
            OmockBoard[i][j] = 1;
        }

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);
        g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);

        if(victoryCondition(i, j, OmockBoard[i][j])){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }

        AIStoneDraw();
    }

    public boolean victoryCondition(int x, int y, int c) {
        int h = 1;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15 && c == OmockBoard[i][y]; i++) h++;
        for(int i = x - 1; 0 <= i && i < 15 && c == OmockBoard[i][y]; i--) h++;
        if(h >= 5) return true;

        int v = 1;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15 && c == OmockBoard[x][i]; i++) v++;
        for(int i = y - 1; 0 <= i && i < 15 && c == OmockBoard[x][i]; i--) v++;
        if(v >= 5) return true;

        int ld = 1; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15 && c == OmockBoard[x+i][y+i]; i++) ld++;
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15 && c == OmockBoard[x-i][y-i]; i++) ld++;
        if(ld >= 5) return true;

        int rd = 1; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15 && c == OmockBoard[x-i][y+i]; i++) rd++;
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15 && c == OmockBoard[x+i][y-i]; i++) rd++;
        if(rd >= 5) return true;

        return false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}


public class BaseFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BaseFrame frame = new BaseFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public BaseFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 440, 470);
        contentPane = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);

                for(int i=10; i < 450; i+=30) {	// 10, 10 시작 | 30씩 증가
                    g.drawLine(10, i, 450-20, i);
                    g.drawLine(i, 10, i, 450-20);
                }
            }
        };
        contentPane.setBackground(new Color(184, 134, 11));

        contentPane.addMouseListener(new StoneDraw(contentPane));

//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
//		contentPane.setLayout(new BorderLayout(0, 0));
    }

}
