import javafx.util.Pair;

import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

class Board implements MouseListener {
    JPanel contentPane = null;
    static Color StoneColor = Color.BLACK;  // CPU is Black

    static final int STONE_SIZE = 25;
    static final int BOARD_SIZE = 15;
    static final int EMPTY = 0, BLACK = 1, WHITE = 2;
    int turnCount = 0;

    static int curTurn = BLACK;

    static int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    public static int getBoardSize(){ return BOARD_SIZE; }
    public static int[][] getBoard(){ return board; }


    public Board(JPanel c) {
        super();
        contentPane = c;
        initBoard(BLACK);
    }

    // 보드 초기화
    public void initBoard(int turn){
        for(int i=0; i<15; i++) {
            for(int j=0; j<15; j++) {
                board[i][j] = EMPTY;
            }
        }

        StoneColor = Color.BLACK;
        curTurn = BLACK;

        contentPane.repaint();
        if(turn == WHITE) {
            turnCount = 1;
            drawStone(7, 7);
        }

    }

    // 돌 착수
    public boolean drawStone(int x, int y){
        System.out.println(x + " : " + y + " : " + board[x][y]);

        if(x < 0 || x >= 15 || y < 0 || y >= 15 || board[x][y] != EMPTY) return false;

        board[x][y] = curTurn;

//        CalcWeight.isGameOver(board);

        int cx =  x * 30 + 10 - (STONE_SIZE / 2);
        int cy =  y * 30 + 10 - (STONE_SIZE / 2);

        Graphics g = contentPane.getGraphics();
        g.setColor(StoneColor);
        g.fillOval(cx, cy, STONE_SIZE, STONE_SIZE);

        if(StoneColor.equals(Color.BLACK)) {
            StoneColor = Color.white;
            curTurn = WHITE;
        } else {
            StoneColor = Color.black;
            curTurn = BLACK;
        }

        contentPane.repaint();
        turnCount++;
        return true;
    }

    // 유효한 움직임 반환
    public static List<Pair<Integer, Integer>> getLegalMoves(){
        List<Pair<Integer, Integer>> moves = new ArrayList<>();
        for(int y = 0; y < BOARD_SIZE; y++){
            for(int x = 0; x < BOARD_SIZE; x++){
                if(board[x][y] == EMPTY) moves.add(new Pair<>(x, y));
            }
        }
        return moves;
    }

    // 유효한 위치인지 체크
    public static boolean isValidPosition(int x, int y){
        return (0 <= x && x < BOARD_SIZE && 0 <= y && y < BOARD_SIZE);
    }

    // 승리 체크
    public static boolean isWinner(int x, int y, int turn){
        if(board[x][y] != turn) return false;
        int [][]directions = {
                {1, 0}, {0, 1}, {1, 1}, {1, -1}
        };
        for(int []p : directions){
            int dx = p[0], dy = p[1];
            int lines = 0;
            for(int i = -4; i < 5; i++){
                int cx =  x + i * dx, cy = y + i * dy;
                if(isValidPosition(cx, cy) && board[cx][cy] == turn){
                    if(lines++ == 5) return true;
                } else lines = 0;
            }
        }
        return false;
    }

    // AI의 차례
    public void AIStoneDraw(){
        int x = 6;
        int y = 6;

        if(turnCount != 1) {
            CalcWeight.P nextP = CalcWeight.gameTree(curTurn);
            x = nextP.x;
            y = nextP.y;
        }

        drawStone(x, y);
    }

    // 사용자의 차례
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = (e.getX() - 10 + 15) / 30;
        int y = (e.getY() - 10 + 15) / 30;

        if(!drawStone(x, y)) return;
//        drawStone(x, y);
        AIStoneDraw();
    }
    @Override
    public void mousePressed(MouseEvent e) { }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
}

public class BaseFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static BaseFrame frame;
    private static Board board;
    private JPanel contentPane;
    static final int STONE_SIZE = 25;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new BaseFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 보드 초기화
    void initBoard(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 460,  510);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int i = 10; i <= 430; i += 30) {
                    g.drawLine(10, i, 430, i);
                    g.drawLine(i, 10, i, 430);
                }

                for(int i = 0; i < Board.getBoardSize(); i++){
                    for(int j = 0; j < Board.getBoardSize(); j++){
                        int cx =  i * 30 + 10 - (STONE_SIZE / 2);
                        int cy =  j * 30 + 10 - (STONE_SIZE / 2);

                        if(Board.board[i][j] == Board.BLACK){
                            g.setColor(Color.BLACK);
                            g.fillOval(cx, cy, STONE_SIZE, STONE_SIZE);
                        }
                        else if(Board.board[i][j] == Board.WHITE){
                            g.setColor(Color.WHITE);
                            g.fillOval(cx, cy, STONE_SIZE, STONE_SIZE);
                        }
                    }
                }
            }
        };
        contentPane.setBackground(new Color(184, 134, 11));
        board = new Board(contentPane);
        contentPane.addMouseListener(board);
        setContentPane(contentPane);
    }

    // 프레임 생성
    public BaseFrame() {
        initBoard();
        createMenu();
    }

    // 상단 메뉴바 액션 리스너
    class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            switch(cmd) { // 메뉴 아이템의 종류 구분
                case "init-Black" :
                    board.initBoard(Board.BLACK);
                    break;
                case "init-White" :
                    board.initBoard(Board.WHITE);
                    break;
                case "Exit" :
                    System.exit(0); break;
            }
        }
    }

    // 상단 메뉴바 생성
    void createMenu() {
        JMenuBar mb = new JMenuBar(); // 메뉴바 생성
        JMenuItem [] menuItem = new JMenuItem [2];
        String[] itemTitle = {"init-Black", "init-White"};
        JMenu screenMenu = new JMenu("init");

        MenuActionListener listener = new MenuActionListener();
        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(listener);
        for(int i=0; i<menuItem.length; i++) {
            menuItem[i] = new JMenuItem(itemTitle[i]);
            menuItem[i].addActionListener(listener);
            screenMenu.add(menuItem[i]);
        }
        mb.add(screenMenu);
        mb.add(itemExit);
        this.setJMenuBar(mb); // 메뉴바를 프레임에 부착
    }
}