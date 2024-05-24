import com.sun.tools.javac.util.Pair;

import java.util.List;

public class CalcWeight {
    static int [][]board;
    static List<Pair<Integer, Integer>> emptyPositions;
    static final int DEPTH = 3;

    static class P{
        int x, y;
        boolean defense = false;
        float MIN = Float.MAX_VALUE;
        float MAX = Float.MIN_VALUE;

        public P(){ }
        public P(int _x, int _y, float minScore, float maxScore){
            x = _x;
            y = _y;
            MIN = minScore;
            MAX = maxScore;
        }
        public P(int _x, int _y, boolean _defense){
            x = _x;
            y = _y;
            defense = _defense;
        }
    }

    public static P gameTree(int turn){
        P bestPosition = new P();

        board = Board.getBoard();
        emptyPositions = Board.getLegalMoves();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.fst, cy = p.snd;
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;

                // 1. 게임 트리 생성 Top-Down
                float curScore = getScoreEstimation(turn, cx, cy);
                P tmp = getMiniPosition(3 - turn, cx, cy, DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, curScore);
                if(tmp.defense) {
                    board[cx][cy] = Board.EMPTY;
                    return tmp; // Defense condition
                }

                // 2. 최고 점수 반환 Bottom-Up
                board[cx][cy] = Board.EMPTY;
                if(bestPosition.MAX < tmp.MIN) {
                    bestPosition.MAX = tmp.MIN;
                    bestPosition.x = cx;
                    bestPosition.y = cy;
                }
            }

            // 상대방이 1 Depth에서 열린 3이상의 점수를 가져 바로 반환될 경우, 해당 위치 바로 반환
            // 최고 점수 반환
        }

        return bestPosition;
    }

    public static P getMaxPosition(int turn, int x, int y, int depth, int alpha, int beta, float score){
        if(depth == 0 || Board.isWinner(x, y, turn)) return new P(x, y, -1, score);
        P cNode = new P();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.fst, cy = p.snd;
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;  // Black = 1, White = 2

                // 1. 게임 트리 생성 Top-Down
                float curScore = score + getScoreEstimation(turn, cx, cy);
                P tmp = getMiniPosition(3 - turn, cx, cy, depth - 1, alpha, beta, curScore);

                // 2. 최고 점수 반환 Bottom-Up
                if(cNode.MAX < tmp.MIN) cNode.MAX = tmp.MIN;

                board[cx][cy] = Board.EMPTY;

                // 점수 계산
                // MAX보다 높을 경우, 현재 상태 저장

                // MIN 호출
                // fullDepth에서 최저 점수가 3x3 이하의 점수를 가짐 -> 바로 반환하여 저지함

                // default

            }
        }
        return cNode;
    }

    public static P getMiniPosition(int turn, int x, int y, int depth, int alpha, int beta, float score){
        if(depth == 0 || Board.isWinner(x, y, turn)) return new P(x, y, score, -1);
        P cNode = new P();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.fst, cy = p.snd;
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;
                // 1. 게임 트리 생성 Top-Down
                float curScore = getScoreEstimation(turn, cx, cy);
//                float curScore = getScoreEstimation(turn, 7, 6);
                if(cx == 7 && cy == 6 && score >= 100_000) {
                    System.out.println("depth: " + depth);
                    System.out.println("score: " + curScore);
                    System.out.println("turn: " + turn);
                }

                if(curScore >= 100_000 && depth == (DEPTH - 1)){
                    board[cx][cy] = Board.EMPTY;
                    return new P(cx, cy, true);  // Winning condition
                }

                P tmp = getMaxPosition(3 - turn, cx, cy, depth - 1, alpha, beta, score - curScore);

                // 2. 최저 점수 반환 Bottom-Up
                if(cNode.MIN > tmp.MAX) cNode.MIN = tmp.MAX;
                board[cx][cy] = Board.EMPTY;
            }
        }
        return cNode;
    }

    public static float getScoreEstimation(int turn, int x, int y){
        float score = 0;

        int []dx = {1, 0, 1, 1};
        int []dy = {0, 1, 1, -1};

        // 가로, 세로, 대각선 검사
        for(int i = 0; i < dx.length; i++){
            int count = 1;
            int blankGuard = 1;
            int openEnds = 0;

            int steps = 1;
            while(steps < 6 && Board.isValidPosition(x + steps * dx[i], y + steps * dy[i])){
                if(board[x + steps * dx[i]][y + steps * dy[i]] == turn){
                    count++;
                    steps++;
                }
                else if(board[x + steps * dx[i]][y + steps * dy[i]] == Board.EMPTY && blankGuard > 0){
                    steps++;
                    if(Board.isValidPosition(x + steps * dx[i], y + steps * dy[i]) &&
                            board[x + steps * dx[i]][y + steps * dy[i]] == turn){
                        blankGuard--;
                    } else break;
                }
                else break;
            }
            if(Board.isValidPosition(x + steps * dx[i], y + steps * dy[i]) && board[x + steps * dx[i]][y + steps * dy[i]] == Board.EMPTY) openEnds++;

            steps = 1;
            while(steps < 6 && Board.isValidPosition(x - steps * dx[i], y - steps * dy[i])){
                if(board[x - steps * dx[i]][y - steps * dy[i]] == turn){
                    count++;
                    steps++;
                }
                else if(board[x - steps * dx[i]][y - steps * dy[i]] == Board.EMPTY && blankGuard > 0){
                    steps++;
                    if(Board.isValidPosition(x - steps * dx[i], y - steps * dy[i]) &&
                            board[x - steps * dx[i]][y - steps * dy[i]] == turn){
                        blankGuard--;
                    } else break;
                }
                else break;
            }
            if(Board.isValidPosition(x - steps * dx[i], y - steps * dy[i]) && board[x - steps * dx[i]][y - steps * dy[i]] == Board.EMPTY) openEnds++;

            if(blankGuard > 0){
                if(count == 5) return 100_000;
                if(count == 4 && openEnds == 2) return 100_000;
                else if(count == 4 && openEnds == 1) count += 50_000;
                else if(openEnds == 2) score += (10 * count);
                else if(openEnds == 1) score += (5 * count);
                else if(openEnds == 0) score += count;
            } else {
                if(openEnds == 2) score += (10 * count * 1.25);
                else if(openEnds == 1) score += (5 * count * 1.25);
                else if(openEnds == 0) score += (count * 1.25);
            }
        }

        return score;
    }
}
