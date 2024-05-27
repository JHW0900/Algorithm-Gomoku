package com.jhw0900.gomoku;

import java.util.List;

public class CalcWeight {
    static int [][]board;
    static List<Pair<Integer, Integer>> emptyPositions;
    static final int DEPTH = 3;

    static P offPos = new P();
    static P defPos = new P();

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

        public P(int _x, int _y, float minScore, float maxScore, boolean _defense){
            x = _x;
            y = _y;
            MIN = minScore;
            MAX = maxScore;
            defense = _defense;
        }
    }

    public static P gameTree(int turn){
        P bestPosition = new P();

        offPos = new P();
        defPos = new P();

        board = Board.getBoard();
        emptyPositions = Board.getLegalMoves();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.getFirst(), cy = p.getSecond();
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;

                // 1. 게임 트리 생성 Top-Down
                float curScore = getScoreEstimation(turn, cx, cy);
                if(curScore >= 100_000 && offPos.MAX < curScore){
                    offPos = new P(cx, cy, -1, curScore, true);
                }
                P tmp = getMiniPosition(3 - turn, cx, cy, DEPTH - 1, Float.MIN_VALUE, Float.MAX_VALUE, curScore);

                // 2. 최고 점수 반환 Bottom-Up
                board[cx][cy] = Board.EMPTY;
                if(bestPosition.MAX < tmp.MIN) {
                    bestPosition.MAX = tmp.MIN;
                    bestPosition.x = cx;
                    bestPosition.y = cy;
                }
            }
        }

        if(offPos.defense && defPos.defense){
            if(offPos.MAX >= (-1) * (defPos.MIN)) return offPos;
            else return defPos;
        }
        else if(offPos.defense) return offPos;
        else if(defPos.defense) return defPos;

        return bestPosition;
    }

    public static P getMaxPosition(int turn, int x, int y, int depth, float alpha, float beta, float score){
        if(depth == 0 || Board.isWinner(x, y, turn)) return new P(x, y, -1, score);
        P cNode = new P();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.getFirst(), cy = p.getSecond();
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;  // Black = 1, White = 2

                // 1. 게임 트리 생성 Top-Down
                float curScore = score + getScoreEstimation(turn, cx, cy);
                P tmp = getMiniPosition(3 - turn, cx, cy, depth - 1, alpha, beta, curScore);

                // 2. 최고 점수 반환 Bottom-Up
                if(cNode.MAX < tmp.MIN) cNode.MAX = tmp.MIN;

                board[cx][cy] = Board.EMPTY;

                // 알파-베타 가지치기
                if (beta <= cNode.MAX) {
                    return cNode; // 베타 컷
                }
                alpha = Math.max(alpha, cNode.MAX);
            }
        }
        return cNode;
    }

    public static P getMiniPosition(int turn, int x, int y, int depth, float alpha, float beta, float score){
        if(depth == 0 || Board.isWinner(x, y, turn)) return new P(x, y, score, -1);
        P cNode = new P();

        for(Pair<Integer, Integer> p : emptyPositions){
            int cx = p.getFirst(), cy = p.getSecond();
            if(Board.isValidPosition(cx, cy) && board[cx][cy] == Board.EMPTY){
                board[cx][cy] = turn;
                // 1. 게임 트리 생성 Top-Down
                float curScore = getScoreEstimation(turn, cx, cy);

                if(depth == (DEPTH - 1) && cx == 12 && cy == 8){
                    board[cx][cy] = turn;
                }
                if(curScore >= 100_000 && depth == (DEPTH - 1) && defPos.MIN > (-1) * (curScore)){
                    defPos = new P(cx, cy, (-1) * (curScore), -1, true);
                }

                P tmp = getMaxPosition(3 - turn, cx, cy, depth - 1, alpha, beta, score - curScore);

                // 2. 최저 점수 반환 Bottom-Up
                if(cNode.MIN > tmp.MAX) cNode.MIN = tmp.MAX;
                board[cx][cy] = Board.EMPTY;

                if (cNode.MIN <= alpha) {
                    return cNode; // 알파 컷오프
                }
                beta = Math.min(beta, cNode.MIN);
            }
        }
        return cNode;
    }

    public static float getScoreEstimation(int turn, int x, int y){
        float score = 0;

        int []dx = {1, 0, 1, 1};
        int []dy = {0, 1, 1, -1};

        int openThree = 0;
        int openFour = 0;

        int openBlThree = 0;
        int openBlFour = 0;

        int oneEndThree = 0;
        int oneEndFour = 0;

        int oneEndBlThree = 0;
        int oneEndBlFour = 0;

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
                    } else {
                        steps--;
                        break;
                    }
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
                    } else {
                        steps--;
                        break;
                    }
                }
                else break;
            }
            if(Board.isValidPosition(x - steps * dx[i], y - steps * dy[i]) && board[x - steps * dx[i]][y - steps * dy[i]] == Board.EMPTY) openEnds++;

            if(blankGuard > 0){
                if(count == 5) return 200_000;
                if(count == 4 && openEnds == 2) {
                    openFour++;
                    score += 100_000;
                }
                else if(count == 4 && openEnds == 1) {
                    oneEndFour++;
                    score += 50_000;
                }
                else if(count == 3 && openEnds == 2) {
                    openThree++;
                    score += (10 * count);
                }
                else if(count == 3 && openEnds == 1) {
                    oneEndThree++;
                    score += (5 * count);
                }
                else if(openEnds == 2) score += (10 * count);
                else if(openEnds == 1) score += (5 * count);
                else if(openEnds == 0) score += count;
            } else {
                if(count == 4 && openEnds == 2){
                    openBlFour++;
                    score += (10 * count * 1.25);
                }
                else if(count == 4 && openEnds == 1){
                    oneEndBlFour++;
                    score += (5 * count * 1.25);
                }
                else if(count == 3 && openEnds == 2){
                    openBlThree++;
                    score += (10 * count * 1.25);
                }
                else if(count == 3 && openEnds == 1){
                    oneEndBlThree++;
                    score += (5 * count * 1.25);
                }
                else if(openEnds == 2) score += (10 * count * 1.25);
                else if(openEnds == 1) score += (5 * count * 1.25);
                else if(openEnds == 0) score += (count * 1.25);
            }
        }
        // 4x4
        if(openFour == 2 || (openBlFour == 1 && openFour == 1) || openBlFour == 2) return 170_000;
        // 4x3
        else if((openFour == 1 || openBlFour == 1) && (openFour + openBlFour + openBlThree + openThree) >= 2) return 150_000;
        // 1막 3x4 or 4x3
        else if((oneEndThree == 1) && (openBlFour == 1 || openFour == 1)) return 135_000;
        else if((oneEndFour == 1) && (openBlThree == 1 || openThree == 1)) return 135_000;
        // 3x3
        else if((openThree >= 1 || openBlThree >= 1) && (openBlThree + openThree) >= 2) return 130_000;
        // 4
        else if(openFour == 1) return 100_000;

        return score;
    }
}
