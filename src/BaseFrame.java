import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


class StoneDraw implements MouseListener {

    JPanel contentPane = null;
    Color StoneColor = Color.black;  // CPU is Black

    private static final int STONE_SIZE = 25;
    private static final int BOARD_SIZE = 15;
    private static final int EMPTY = -1, WHITE = 0, BLACK = 1;

    int  [][]OmockBoard = new int[BOARD_SIZE][BOARD_SIZE];

    /**
     * 보드 초기화
     * @param c 게임 판
     */
    public StoneDraw(JPanel c) {
        super();
        contentPane = c;

        for(int i=0; i<15; i++) {
            for(int j=0; j<15; j++) {
                OmockBoard[i][j] = EMPTY;
            }
        }
        OmockBoard[7][7] = BLACK;
    }

    /*
     * 돌의 조합에 따른 가중치(점수) 계산
     * @param x 가로 좌표
     * @param y 세로 좌표
     * @param c 돌의 색상
     * @return 가중치 반환
     */
    /*//
    public int scoreVarEstimation(int x, int y, int c){
        int scoreVar = 0;

        if(checkBlockedNinLine(x, y, c, 5, 0) > 0) scoreVar = 100_000_000;

        int[][] scores = {
                {0, 0, 10, 10000, 10000},
                {0, 0, 10, 10000, 10000}
        };

        for(int i = 2; i < 5; i++){
            int oneBlockedLine = checkBlockedNinLineWithBlank(x, y, c, i, 1);
            int freeLine = checkBlockedNinLineWithBlank(x, y, c, i, 0);

            if(freeLine > 0) scoreVar += freeLine * scores[c][i];
            if(oneBlockedLine > 0) scoreVar += oneBlockedLine * (scores[c][i] / 2);
        }

        // 3-3일 경우,
//        if(checkNinLine(x, y, c, 3) == 2
//            && checkBlockedNinLine(x, y, c, 3, 1) + checkBlockedNinLine(x, y, c, 3, 2) == 0
//        ) scoreVar = 100000;
//        if(checkNinLineWithBlank(x, y, c, 3) == 2
//                && checkBlockedNinLineWithBlank(x, y, c, 3, 1) + checkBlockedNinLineWithBlank(x, y, c, 3, 2) == 0
//        ) scoreVar = 100000;

        return scoreVar;
    }
//*/
    public int scoreVarEstimation(int x, int y, int c) {
        int score = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (OmockBoard[i][j] != EMPTY) {
                    score += evaluatePosition(i, j, OmockBoard[i][j]);
                }
            }
        }
        return score;
    }

    private int evaluatePosition(int x, int y, int player) {
        int[] dx = {1, 0, 1, 1};
        int[] dy = {0, 1, 1, -1};
        int totalScore = 0;

        for (int d = 0; d < dx.length; d++) {
            totalScore += evaluateDirection(x, y, dx[d], dy[d], player);
        }

        return totalScore;
    }

    private int evaluateDirection(int x, int y, int dx, int dy, int player) {
        int count = 1;  // Start with the current stone
        int score = 0;
        int openEnds = 0;
        int blocked = 0;

        // Check forward direction
        int steps = 1;
        while (steps < 5 && isValid(x + steps * dx, y + steps * dy) && OmockBoard[x + steps * dx][y + steps * dy] == player) {
            count++;
            steps++;
        }
        if (isValid(x + steps * dx, y + steps * dy) && OmockBoard[x + steps * dx][y + steps * dy] == EMPTY) openEnds++;

        // Check backward direction
        steps = 1;
        while (steps < 5 && isValid(x - steps * dx, y - steps * dy) && OmockBoard[x - steps * dx][y - steps * dy] == player) {
            count++;
            steps++;
        }
        if (isValid(x - steps * dx, y - steps * dy) && OmockBoard[x - steps * dx][y - steps * dy] == EMPTY) openEnds++;

        // Score calculation based on pattern length and openness
        if (count >= 5) {
            score += 10000;  // Winning condition
        } else if (count == 4) {
            if (openEnds == 2) score += 500;  // Open both ends
            else if (openEnds == 1) score += 250;  // One end open
        } else if (count == 3) {
            if (openEnds == 2) score += 100;
            else if (openEnds == 1) score += 50;
        } else if (count == 2) {
            if (openEnds == 2) score += 10;
            else if (openEnds == 1) score += 5;
        }

        return score;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    class P{
        public int i = -1;
        public int j = -1;
        int MAX = Integer.MIN_VALUE;
        int MIN = Integer.MAX_VALUE;

        public P(int _i, int _j) {
            i = _i; j = _j;
        }
        public P(){}
    }

    /**
     * 게임트리 시작
     * @param c 돌의 색상
     * @param limit 최대로 탐색할 Depth
     * @return 최적의 수를 반환 (i, j, MAX, MIN)
     */
    public P GameTree(int c, int limit){
        return getPositionForMax(c, limit, 0);
    }

    /**
     * miniMax의 Max 부분을 계산
     * @param c 돌의 색상
     * @param limit 남은 Depth
     * @param curScore 이전 Depth의 Score
     * @return P{class}: (i, j, MAX, MIN)
     */
    private P getPositionForMax(int c, int limit, int curScore) {
        P r_val = new P();

        if(limit == 0){
            r_val.MAX = curScore;
            return r_val;
        }

        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if(this.OmockBoard[i][j] == EMPTY){
                    this.OmockBoard[i][j] = c;

                    if(checkBlockedNinLine(i, j, OmockBoard[i][j], 5, 0) > 0){
                        r_val.MAX = scoreVarEstimation(i, j, c); // - scoreVarEstimation(i, j, 1-c);
                        r_val.i = i;
                        r_val.j = j;
                        this.OmockBoard[i][j] = EMPTY;

                        return r_val;
                    }

                    P temp = getPositionForMin(1 - c, limit-1, scoreVarEstimation(i, j, c));

                    if(r_val.MAX < temp.MIN){
                        r_val.MAX = temp.MIN;
                        r_val.i = i;
                        r_val.j = j;
                    }
                    this.OmockBoard[i][j] = EMPTY;
                }
            }
        }

        return r_val;
    }

    /**
     * miniMax의 Min 부분을 계산
     * @param c 돌의 색상
     * @param limit 남은 Depth
     * @param curScore 이전 Depth의 Score
     * @return P{class}: (i, j, MAX, MIN)
     */
    private P getPositionForMin(int c, int limit, int curScore) {
        P r_val = new P();

        if(limit == 0){
            r_val.MIN = curScore;
            return r_val;
        }

        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if(this.OmockBoard[i][j] == EMPTY){
                    this.OmockBoard[i][j] = c;

                    if(checkBlockedNinLine(i, j, OmockBoard[i][j], 5, 0) > 0){
                        r_val.MIN = curScore - scoreVarEstimation(i, j, c); // - scoreVarEstimation(i, j, 1-c);
                        r_val.i = i;
                        r_val.j = j;
                        this.OmockBoard[i][j] = EMPTY;

                        return r_val;
                    }

                    P temp = getPositionForMax(1 - c, limit-1, curScore - scoreVarEstimation(i, j, c));

                    if(r_val.MIN > temp.MAX){
                        r_val.MIN = temp.MAX;
                        r_val.i = i;
                        r_val.j = j;
                    }
                    this.OmockBoard[i][j] = EMPTY;
                }
            }
        }

        return r_val;
    }

    /**
     * AI의 착수 Logic
     */
    public void AIStoneDraw() {
        int color = 0;

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            color = WHITE;
        } else {
            StoneColor = Color.black;
            color = BLACK;
        }

        P nextP = GameTree(color, 5);
        int i = nextP.i;
        int j = nextP.j;

        System.out.println("CPU: " + i + ", " + j + " : " + nextP.MAX);

        OmockBoard[i][j] = color;

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);

        int cX =  i * 30 + 10 - (STONE_SIZE / 2);
        int cY =  j * 30 + 10 - (STONE_SIZE / 2);
        g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);

        if(checkBlockedNinLine(i, j, OmockBoard[i][j], 5, 0) > 0){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int i = (e.getX() - 10 + 15) / 30;
        int j = (e.getY() - 10 + 15) / 30;

        if(i < 0 || i >= 15 || j < 0 || j >= 15 || OmockBoard[i][j] != -1) return;

        int cX =  i * 30 + 10 - (STONE_SIZE / 2);
        int cY =  j * 30 + 10 - (STONE_SIZE / 2);

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            OmockBoard[i][j] = 0;
        } else {
            StoneColor = Color.black;
            OmockBoard[i][j] = 1;
        }
        System.out.println(i + ", " + j + " : " + scoreVarEstimation(i, j, OmockBoard[i][j]));

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);
        g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);

        if(checkBlockedNinLine(i, j, OmockBoard[i][j], 5, 0) > 0){
            if(StoneColor.equals(Color.BLACK)) JOptionPane.showMessageDialog(null, "Black Win");
            else JOptionPane.showMessageDialog(null,"White Win");
            System.exit(0);
        }

        AIStoneDraw();
    }

    public int checkBlockedNinLineWithBlank(int x, int y, int c, int n, int b) {
        int numOfNinLine = 0;

        int blank = 1;
        int h = 1, hb = 0;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1 && OmockBoard[i][y] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[i][y] != EMPTY){
                hb++;
                break;
            } else break;
        }
        for(int i = x - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[i][y]) h++;
            else if(blank == 1 && OmockBoard[i][y] == EMPTY){
                blank--;
                break;
            } else if(OmockBoard[i][y] != EMPTY){
                hb++;
                break;
            } else break;
        }
        if(h == n && b == hb) numOfNinLine++;

        blank = 1;
        int v = 1, vb = 0;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1 && OmockBoard[x][i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x][i] != EMPTY){
                vb++;
                break;
            } else break;
        }
        for(int i = y - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[x][i]) v++;
            else if(blank == 1 && OmockBoard[x][i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x][i] != EMPTY){
                vb++;
                break;
            } else break;
        }
        if(v == n && b == vb) numOfNinLine++;

        blank = 1;
        int ld = 1, ldb = 0; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x+i][y+i]) ld++;
            else if(blank == 1 && OmockBoard[x+i][y+i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x+i][y+i] != EMPTY){
                ldb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x-i][y-i]) ld++;
            else if(blank == 1 && OmockBoard[x-i][y-i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x-i][y-i] != EMPTY){
                ldb++;
                break;
            } else break;
        }
        if(ld == n && b == ldb) numOfNinLine++;

        blank = 1;
        int rd = 1, rdb = 0; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15; i++){
            if(c == OmockBoard[x-i][y+i]) rd++;
            else if(blank == 1 && OmockBoard[x-i][y+i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x-i][y+i] != EMPTY){
                rdb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x+i][y-i]) rd++;
            else if(blank == 1 && OmockBoard[x+i][y-i] == EMPTY){
                blank--;
                break;
            }
            else if(OmockBoard[x+i][y-i] != EMPTY){
                rdb++;
                break;
            } else break;
        }
        if(rd == n) numOfNinLine++;

        return numOfNinLine;
    }

    /**
     * checkBlockedNinLine: 몇개의 줄이 b개의 다른 색 돌로 막혀있는가?
     * @param x 가로 좌표
     * @param y 세로 좌표
     * @param c 돌의 색상
     * @param n 나열된 줄의 수
     * @param b 나열된 줄에서 막혀있는 다른 돌의 수
     * @return numOfNinLine - b개의 돌로 막혀있는 N개의 돌로 나열된 줄의 수
     */
    public int checkBlockedNinLine(int x, int y, int c, int n, int b) {
        int numOfNinLine = 0;

        int h = 1, hb = 0;  // Horizontally
        for(int i = x + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[i][y]) h++;
            else if(OmockBoard[i][y] != EMPTY){
                hb++;
                break;
            } else break;
        }
        for(int i = x - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[i][y]) h++;
            else if(OmockBoard[i][y] != EMPTY){
                hb++;
                break;
            } else break;
        }
        if(h == n && hb == b) numOfNinLine++;

        int v = 1, vb = 0;  //Vertically
        for(int i = y + 1; 0 <= i && i < 15; i++){
            if(c == OmockBoard[x][i]) v++;
            else if(OmockBoard[x][i] != EMPTY) {
                vb++;
                break;
            } else break;
        }
        for(int i = y - 1; 0 <= i && i < 15; i--){
            if(c == OmockBoard[x][i]) v++;
            else if(OmockBoard[x][i] != EMPTY){
                vb++;
                break;
            } else break;
        }
        if(v == n && vb == b) numOfNinLine++;

        int ld = 1, ldb = 0; //Left Diagoanlly
        for(int i = 1; 0 <= i+x && i+x < 15 && 0 <= i+y && i+y < 15; i++){
            if(c == OmockBoard[x+i][y+i]) ld++;
            else if(OmockBoard[x+i][y+i] != EMPTY){
                ldb++;
                break;
            } else break;
        }
        for(int i = 1;0 <= x-i && x-i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x-i][y-i]) ld++;
            else if(OmockBoard[x-i][y-i] != EMPTY){
                ldb++;
                break;
            } else break;
        }
        if(ld == n && ldb == b) numOfNinLine++;

        int rd = 1, rdb = 0; //Right Diagoanlly
        for(int i = 1; 0 <= x-i && x-i < 15 && 0 <= y+i && y+i < 15; i++){
            if(c == OmockBoard[x-i][y+i]) rd++;
            else if(OmockBoard[x-i][y+i] != EMPTY){
                rdb++;
                break;
            } else break;
        }
        for(int i = 1; 0 <= x+i && x+i < 15 && 0 <= y-i && y-i < 15; i++){
            if(c == OmockBoard[x+i][y-i]) rd++;
            else if(OmockBoard[x+i][y-i] != EMPTY){
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
    static final int STONE_SIZE = 25;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                BaseFrame frame = new BaseFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public BaseFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 480);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                for (int i = 10; i <= 430; i += 30) {
                    g.drawLine(10, i, 430, i);
                    g.drawLine(i, 10, i, 430);
                }

                int cX =  7 * 30 + 10 - (STONE_SIZE / 2);
                int cY =  7 * 30 + 10 - (STONE_SIZE / 2);

                g.fillOval(cX, cY, STONE_SIZE, STONE_SIZE);
            }
        };
        contentPane.setBackground(new Color(184, 134, 11));
        contentPane.addMouseListener(new StoneDraw(contentPane));
        setContentPane(contentPane);
    }
}
