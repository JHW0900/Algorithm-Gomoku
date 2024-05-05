import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class StoneDraw implements MouseListener {

    JPanel contentPane = null;
    static final int STONE_SIZE = 25;
    Color StoneColor = Color.white;  // CPU is white

    int scoreB = 0;
    int scoreW = 0;

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

    public int scoreVarEstimation(int x, int y, int c){
        int scoreVar = 0;

        /**
         * 1. 점수 누적 방식 (중복연산)
         * 2: 20, 3: 50, 4: 90, 5: 140
         */
        /*//
        for(int i = 2; i <= 5; i++){
            if(checkNinLine(x, y, c, i) > 0) scoreVar += (i * 10);
        }
        //*/

        /**
         * 2. 개별 점수 방식 (중복연산 x)
         */
        /*//
        if(checkNinLine(x, y, c, 5) > 0){
            scoreVar += 120;
        }
        else if(checkNinLine(x, y, c, 4) > 0) scoreVar += 100;
        else if(checkNinLine(x, y, c, 3) > 0) scoreVar += 80;
        else if(checkNinLine(x, y, c, 2) > 0) scoreVar += 60;
        //*/

        /**
         * 3. 점수 반환 방식 (줄을 계산하여 true/false가 아니라 줄의 개수를 반환)
         */
        for(int i = 2; i < 5; i++){
            scoreVar = (checkNinLine(x, y, c, i) - check1BlockedNinLine(x, y, c, i, 1) - check1BlockedNinLine(x, y, c, i, 2)) * i * 10;
            scoreVar += check1BlockedNinLine(x, y, c, i, 1) * i * 3;
        }

        if( // 3-3 일 경우,
            checkNinLine(x, y, c, 3) == 2 &&
            check1BlockedNinLine(x, y, c, 3, 1) + check1BlockedNinLine(x, y, c, 3, 2) == 0
        ) scoreVar = 100000;
        // TODO: 열리고 띄어진 3, 4 등 어떻게 처리할 것인가?

        if(checkNinLine(x, y, c, 5) >= 1)
            scoreVar = checkNinLine(x, y, c, 5) * 1000000;

        return scoreVar;
    }

    public void AIStoneDraw() {
        int color = 0;
        int i = 0, j = 0;

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            color = 0;
        } else {
            StoneColor = Color.black;
            color = 1;
        }
        OmockBoard[i][j] = color;
        

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);

        int cX =  i * 30 + 10 - (STONE_SIZE / 2);
        int cY =  j * 30 + 10 - (STONE_SIZE / 2);
        g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);

        if(checkNinLine(i, j, OmockBoard[i][j], 5) > 0){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int i = (e.getX() - 10 + 15) / 30;
        int j = (e.getY() - 10 + 15) / 30;

        if(OmockBoard[i][j] != -1) return;

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

        if(checkNinLine(i, j, OmockBoard[i][j], 5) > 0){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }

        AIStoneDraw();
    }

    public int checkNinLine(int x, int y, int c, int n) {
        int numOfNinLine = 0;

        int h = 1;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15 && c == OmockBoard[i][y]; i++) h++;
        for(int i = x - 1; 0 <= i && i < 15 && c == OmockBoard[i][y]; i--) h++;
        if(h == n) numOfNinLine++;

        int v = 1;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15 && c == OmockBoard[x][i]; i++) v++;
        for(int i = y - 1; 0 <= i && i < 15 && c == OmockBoard[x][i]; i--) v++;
        if(v == n) numOfNinLine++;

        int ld = 1; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15 && c == OmockBoard[x+i][y+i]; i++) ld++;
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15 && c == OmockBoard[x-i][y-i]; i++) ld++;
        if(ld == n) numOfNinLine++;

        int rd = 1; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15 && c == OmockBoard[x-i][y+i]; i++) rd++;
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15 && c == OmockBoard[x+i][y-i]; i++) rd++;
        if(rd == n) numOfNinLine++;

        return numOfNinLine;
    }

    public int checkNinLineWithBlank(int x, int y, int c, int n) {
        int numOfNinLine = 0;

        int blank = 1;
        int h = 1;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1){ // TODO: 이렇게 하면 다른 돌로 막혀도 체크됨
                h++;
                blank--;
                break;
            } else break;
        }
        for(int i = x - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1){
                h++;
                blank--;
                break;
            } else break;
        }
        if(h == n) numOfNinLine++;

        blank = 1;
        int v = 1;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1){
                v++;
                blank--;
                break;
            } else break;
        }
        for(int i = y - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1){
                v++;
                blank--;
                break;
            } else break;
        }
        if(v == n) numOfNinLine++;

        blank = 1;
        int ld = 1; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x+i][y+i]) ld++;
            else if(blank == 1){
                ld++;
                blank--;
                break;
            } else break;
        }
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x-i][y-i]) ld++;
            else if(blank == 1){
                ld++;
                blank--;
                break;
            } else break;
        }
        if(ld == n) numOfNinLine++;

        blank = 1;
        int rd = 1; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15; i++){
            if(c == OmockBoard[x-i][y+i]) rd++;
            else if(blank == 1){
                rd++;
                blank--;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x+i][y-i]) rd++;
            else if(blank == 1){
                rd++;
                blank--;
                break;
            } else break;
        }
        if(rd == n) numOfNinLine++;

        return numOfNinLine;
    }

    public int checkBlockedNinLineWithBlank(int x, int y, int c, int n, int b) {
        int numOfNinLine = 0;

        int blank = 1;
        int h = 1, hb = 0;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1){ // TODO: 이렇게 하면 다른 돌로 막혀도 체크됨
                h++;
                blank--;
                break;
            }
            else if(OmockBoard[i][y] != -1){
                hb++;
                break;
            } else break;
        }
        for(int i = x - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1){
                h++;
                blank--;
                break;
            } else if(OmockBoard[i][y] != -1){
                hb++;
                break;
            } else break;
        }
        if(h == n && b == hb) numOfNinLine++;

        blank = 1;
        int v = 1, vb = 0;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1){
                v++;
                blank--;
                break;
            }
            else if(OmockBoard[x][i] != -1){
                vb++;
                break;
            } else break;
        }
        for(int i = y - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1){
                v++;
                blank--;
                break;
            }
            else if(OmockBoard[x][i] != -1){
                vb++;
                break;
            } else break;
        }
        if(v == n && b == vb) numOfNinLine++;

        blank = 1;
        int ld = 1, ldb = 0; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x+i][y+i]) ld++;
            else if(blank == 1){
                ld++;
                blank--;
                break;
            }
            else if(OmockBoard[x+i][y+i] != -1){
                ldb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x-i][y-i]) ld++;
            else if(blank == 1){
                ld++;
                blank--;
                break;
            }
            else if(OmockBoard[x-i][y-i] != -1){
                ldb++;
                break;
            } else break;
        }
        if(ld == n && b == ldb) numOfNinLine++;

        blank = 1;
        int rd = 1, rdb = 0; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15; i++){
            if(c == OmockBoard[x-i][y+i]) rd++;
            else if(blank == 1){
                rd++;
                blank--;
                break;
            }
            else if(OmockBoard[x-i][y+i] != -1){
                rdb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x+i][y-i]) rd++;
            else if(blank == 1){
                rd++;
                blank--;
                break;
            }
            else if(OmockBoard[x+i][y-i] != -1){
                rdb++;
                break;
            } else break;
        }
        if(rd == n) numOfNinLine++;

        return numOfNinLine;
    }

    /**
     * check1BlockedNinLine: 몇개의 줄이 b개의 다른 색 돌로 막혀있는가?
     * @param x: 가로 좌표
     * @param y: 세로 좌표
     * @param c: 돌의 색상
     * @param n: 나열된 줄의 수
     * @param b: 나열된 줄에서 막혀있는 다른 돌의 수
     * @return numOfNinLine: b개의 돌로 막혀있는 N개의 돌로 나열된 줄의 수
     */
    public int check1BlockedNinLine(int x, int y, int c, int n, int b) {
        int numOfNinLine = 0;

        int h = 1, hb = 0;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[i][y]) h++;
            else if(OmockBoard[i][y] != -1){
                hb++;
                break;
            } else break;
        }
        for(int i = x - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[i][y]) h++;
            else if(OmockBoard[i][y] != -1){
                hb++;
                break;
            } else break;
        }
        if(h == n && hb == b) numOfNinLine++;

        int v = 1, vb = 0;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[x][i]) v++;
            else if(OmockBoard[x][i] != -1) {
                vb++;
                break;
            } else break;
        }
        for(int i = y - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[x][i]) v++;
            else if(OmockBoard[x][i] != -1){
                vb++;
                break;
            } else break;
        }
        if(v == n && vb == b) numOfNinLine++;

        int ld = 1, ldb = 0; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x+i][y+i]) ld++;
            else if(OmockBoard[x+i][y+i] != -1){
                ldb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x-i][y-i]) ld++;
            else if(OmockBoard[x-i][y-i] != -1){
                ldb++;
                break;
            } else break;
        }
        if(ld == n && ldb == b) numOfNinLine++;

        int rd = 1, rdb = 0; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15; i++){
            if(c == OmockBoard[x-i][y+i]) rd++;
            else if(OmockBoard[x-i][y+i] != -1){
                rdb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x+i][y-i]) rd++;
            else if(OmockBoard[x+i][y-i] != -1){
                rdb++;
                break;
            } else break;
        }
        if(rd == n && rdb == b) numOfNinLine++;

        return numOfNinLine;
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
