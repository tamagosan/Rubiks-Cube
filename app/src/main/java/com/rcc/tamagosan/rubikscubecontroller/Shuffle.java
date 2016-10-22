package com.rcc.tamagosan.rubikscubecontroller;

public class Shuffle {
    public static int[][][] color = new int[6][3][3];
    private MainActivity mact = new MainActivity();
    private int i, j, k;

    public Shuffle() {
        for (i = 0; i < 6; i++) {
            for (j = 0; j < 3; j++) {
                for (k = 0; k < 3; k++) {
                    color[i][j][k] = mact.color[i][j][k];
                }
            }
        }
    }

    public void rolling() {
        int rollrand, pict = 0;
        for (j = 0; j < 30; j++) {
            do {
                rollrand = (int) (Math.random() * 18);
            }
            while (pict + 1 >= rollrand && pict - 1 <= rollrand);
            pict = rollrand;
            rollingrun(rollrand);
        }
        for (i = 0; i < 6; i++) {
            for (j = 0; j < 3; j++) {
                for (k = 0; k < 3; k++) {
                    mact.color[i][j][k] = color[i][j][k];
                }
            }
        }
    }

    private void rollingrun(int rollingNum) {
        int mem;
        switch (rollingNum) {
            case 0:
                for (i = 0; i < 3; i++) {
                    mem = color[5][0][0];
                    color[5][0][0] = color[5][1][0];
                    color[5][1][0] = color[5][2][0];
                    color[5][2][0] = color[2][0][0];
                    color[2][0][0] = color[2][1][0];
                    color[2][1][0] = color[2][2][0];
                    color[2][2][0] = color[0][0][0];
                    color[0][0][0] = color[0][1][0];
                    color[0][1][0] = color[0][2][0];
                    color[0][2][0] = color[4][2][2];
                    color[4][2][2] = color[4][1][2];
                    color[4][1][2] = color[4][0][2];
                    color[4][0][2] = mem;
                    if (i != 1) {
                        mem = color[3][0][0];
                        color[3][0][0] = color[3][0][1];
                        color[3][0][1] = color[3][0][2];
                        color[3][0][2] = color[3][1][2];
                        color[3][1][2] = color[3][2][2];
                        color[3][2][2] = color[3][2][1];
                        color[3][2][1] = color[3][2][0];
                        color[3][2][0] = color[3][1][0];
                        color[3][1][0] = mem;
                    }
                }
                break;
            case 1:
                for (i = 0; i < 3; i++) {
                    mem = color[0][2][0];
                    color[0][2][0] = color[0][1][0];
                    color[0][1][0] = color[0][0][0];
                    color[0][0][0] = color[2][2][0];
                    color[2][2][0] = color[2][1][0];
                    color[2][1][0] = color[2][0][0];
                    color[2][0][0] = color[5][2][0];
                    color[5][2][0] = color[5][1][0];
                    color[5][1][0] = color[5][0][0];
                    color[5][0][0] = color[4][0][2];
                    color[4][0][2] = color[4][1][2];
                    color[4][1][2] = color[4][2][2];
                    color[4][2][2] = mem;
                    if (i != 1) {
                        mem = color[3][0][0];
                        color[3][0][0] = color[3][1][0];
                        color[3][1][0] = color[3][2][0];
                        color[3][2][0] = color[3][2][1];
                        color[3][2][1] = color[3][2][2];
                        color[3][2][2] = color[3][1][2];
                        color[3][1][2] = color[3][0][2];
                        color[3][0][2] = color[3][0][1];
                        color[3][0][1] = mem;
                    }
                }
                break;
            case 2:
                for (i = 0; i < 3; i++) {
                    mem = color[5][0][1];
                    color[5][0][1] = color[5][1][1];
                    color[5][1][1] = color[5][2][1];
                    color[5][2][1] = color[2][0][1];
                    color[2][0][1] = color[2][1][1];
                    color[2][1][1] = color[2][2][1];
                    color[2][2][1] = color[0][0][1];
                    color[0][0][1] = color[0][1][1];
                    color[0][1][1] = color[0][2][1];
                    color[0][2][1] = color[4][2][1];
                    color[4][2][1] = color[4][1][1];
                    color[4][1][1] = color[4][0][1];
                    color[4][0][1] = mem;
                }
                break;
            case 3:
                for (i = 0; i < 3; i++) {
                    mem = color[0][2][1];
                    color[0][2][1] = color[0][1][1];
                    color[0][1][1] = color[0][0][1];
                    color[0][0][1] = color[2][2][1];
                    color[2][2][1] = color[2][1][1];
                    color[2][1][1] = color[2][0][1];
                    color[2][0][1] = color[5][2][1];
                    color[5][2][1] = color[5][1][1];
                    color[5][1][1] = color[5][0][1];
                    color[5][0][1] = color[4][0][1];
                    color[4][0][1] = color[4][1][1];
                    color[4][1][1] = color[4][2][1];
                    color[4][2][1] = mem;
                }
                break;
            case 4:
                for (i = 0; i < 3; i++) {
                    mem = color[5][0][2];
                    color[5][0][2] = color[5][1][2];
                    color[5][1][2] = color[5][2][2];
                    color[5][2][2] = color[2][0][2];
                    color[2][0][2] = color[2][1][2];
                    color[2][1][2] = color[2][2][2];
                    color[2][2][2] = color[0][0][2];
                    color[0][0][2] = color[0][1][2];
                    color[0][1][2] = color[0][2][2];
                    color[0][2][2] = color[4][2][0];
                    color[4][2][0] = color[4][1][0];
                    color[4][1][0] = color[4][0][0];
                    color[4][0][0] = mem;
                    if (i != 1) {
                        mem = color[1][0][0];
                        color[1][0][0] = color[1][1][0];
                        color[1][1][0] = color[1][2][0];
                        color[1][2][0] = color[1][2][1];
                        color[1][2][1] = color[1][2][2];
                        color[1][2][2] = color[1][1][2];
                        color[1][1][2] = color[1][0][2];
                        color[1][0][2] = color[1][0][1];
                        color[1][0][1] = mem;
                    }
                }
                break;
            case 5:
                for (i = 0; i < 3; i++) {
                    mem = color[0][2][2];
                    color[0][2][2] = color[0][1][2];
                    color[0][1][2] = color[0][0][2];
                    color[0][0][2] = color[2][2][2];
                    color[2][2][2] = color[2][1][2];
                    color[2][1][2] = color[2][0][2];
                    color[2][0][2] = color[5][2][2];
                    color[5][2][2] = color[5][1][2];
                    color[5][1][2] = color[5][0][2];
                    color[5][0][2] = color[4][0][0];
                    color[4][0][0] = color[4][1][0];
                    color[4][1][0] = color[4][2][0];
                    color[4][2][0] = mem;
                    if (i != 1) {
                        mem = color[1][0][0];
                        color[1][0][0] = color[1][0][1];
                        color[1][0][1] = color[1][0][2];
                        color[1][0][2] = color[1][1][2];
                        color[1][1][2] = color[1][2][2];
                        color[1][2][2] = color[1][2][1];
                        color[1][2][1] = color[1][2][0];
                        color[1][2][0] = color[1][1][0];
                        color[1][1][0] = mem;
                    }
                }
                break;
            case 6:
                for (i = 0; i < 3; i++) {
                    mem = color[3][2][0];
                    color[3][2][0] = color[3][2][1];
                    color[3][2][1] = color[3][2][2];
                    color[3][2][2] = color[2][2][0];
                    color[2][2][0] = color[2][2][1];
                    color[2][2][1] = color[2][2][2];
                    color[2][2][2] = color[1][2][0];
                    color[1][2][0] = color[1][2][1];
                    color[1][2][1] = color[1][2][2];
                    color[1][2][2] = color[4][2][0];
                    color[4][2][0] = color[4][2][1];
                    color[4][2][1] = color[4][2][2];
                    color[4][2][2] = mem;
                    if (i != 1) {
                        mem = color[0][0][0];
                        color[0][0][0] = color[0][0][1];
                        color[0][0][1] = color[0][0][2];
                        color[0][0][2] = color[0][1][2];
                        color[0][1][2] = color[0][2][2];
                        color[0][2][2] = color[0][2][1];
                        color[0][2][1] = color[0][2][0];
                        color[0][2][0] = color[0][1][0];
                        color[0][1][0] = mem;
                    }
                }
                break;
            case 7:
                for (i = 0; i < 3; i++) {
                    mem = color[4][2][2];
                    color[4][2][2] = color[4][2][1];
                    color[4][2][1] = color[4][2][0];
                    color[4][2][0] = color[1][2][2];
                    color[1][2][2] = color[1][2][1];
                    color[1][2][1] = color[1][2][0];
                    color[1][2][0] = color[2][2][2];
                    color[2][2][2] = color[2][2][1];
                    color[2][2][1] = color[2][2][0];
                    color[2][2][0] = color[3][2][2];
                    color[3][2][2] = color[3][2][1];
                    color[3][2][1] = color[3][2][0];
                    color[3][2][0] = mem;
                    if (i != 1) {
                        mem = color[0][0][0];
                        color[0][0][0] = color[0][1][0];
                        color[0][1][0] = color[0][2][0];
                        color[0][2][0] = color[0][2][1];
                        color[0][2][1] = color[0][2][2];
                        color[0][2][2] = color[0][1][2];
                        color[0][1][2] = color[0][0][2];
                        color[0][0][2] = color[0][0][1];
                        color[0][0][1] = mem;
                    }
                }
                break;
            case 8:
                for (i = 0; i < 3; i++) {
                    mem = color[3][1][0];
                    color[3][1][0] = color[3][1][1];
                    color[3][1][1] = color[3][1][2];
                    color[3][1][2] = color[2][1][0];
                    color[2][1][0] = color[2][1][1];
                    color[2][1][1] = color[2][1][2];
                    color[2][1][2] = color[1][1][0];
                    color[1][1][0] = color[1][1][1];
                    color[1][1][1] = color[1][1][2];
                    color[1][1][2] = color[4][1][0];
                    color[4][1][0] = color[4][1][1];
                    color[4][1][1] = color[4][1][2];
                    color[4][1][2] = mem;
                }
                break;
            case 9:
                for (i = 0; i < 3; i++) {
                    mem = color[4][1][2];
                    color[4][1][2] = color[4][1][1];
                    color[4][1][1] = color[4][1][0];
                    color[4][1][0] = color[1][1][2];
                    color[1][1][2] = color[1][1][1];
                    color[1][1][1] = color[1][1][0];
                    color[1][1][0] = color[2][1][2];
                    color[2][1][2] = color[2][1][1];
                    color[2][1][1] = color[2][1][0];
                    color[2][1][0] = color[3][1][2];
                    color[3][1][2] = color[3][1][1];
                    color[3][1][1] = color[3][1][0];
                    color[3][1][0] = mem;
                }
                break;
            case 10:
                for (i = 0; i < 3; i++) {
                    mem = color[3][0][0];
                    color[3][0][0] = color[3][0][1];
                    color[3][0][1] = color[3][0][2];
                    color[3][0][2] = color[2][0][0];
                    color[2][0][0] = color[2][0][1];
                    color[2][0][1] = color[2][0][2];
                    color[2][0][2] = color[1][0][0];
                    color[1][0][0] = color[1][0][1];
                    color[1][0][1] = color[1][0][2];
                    color[1][0][2] = color[4][0][0];
                    color[4][0][0] = color[4][0][1];
                    color[4][0][1] = color[4][0][2];
                    color[4][0][2] = mem;
                    if (i != 1) {
                        mem = color[5][0][0];
                        color[5][0][0] = color[5][1][0];
                        color[5][1][0] = color[5][2][0];
                        color[5][2][0] = color[5][2][1];
                        color[5][2][1] = color[5][2][2];
                        color[5][2][2] = color[5][1][2];
                        color[5][1][2] = color[5][0][2];
                        color[5][0][2] = color[5][0][1];
                        color[5][0][1] = mem;
                    }
                }
                break;
            case 11:
                for (i = 0; i < 3; i++) {
                    mem = color[4][0][2];
                    color[4][0][2] = color[4][0][1];
                    color[4][0][1] = color[4][0][0];
                    color[4][0][0] = color[1][0][2];
                    color[1][0][2] = color[1][0][1];
                    color[1][0][1] = color[1][0][0];
                    color[1][0][0] = color[2][0][2];
                    color[2][0][2] = color[2][0][1];
                    color[2][0][1] = color[2][0][0];
                    color[2][0][0] = color[3][0][2];
                    color[3][0][2] = color[3][0][1];
                    color[3][0][1] = color[3][0][0];
                    color[3][0][0] = mem;
                    if (i != 1) {
                        mem = color[5][0][0];
                        color[5][0][0] = color[5][0][1];
                        color[5][0][1] = color[5][0][2];
                        color[5][0][2] = color[5][1][2];
                        color[5][1][2] = color[5][2][2];
                        color[5][2][2] = color[5][2][1];
                        color[5][2][1] = color[5][2][0];
                        color[5][2][0] = color[5][1][0];
                        color[5][1][0] = mem;
                    }
                }
                break;
            case 12:
                for (i = 0; i < 3; i++) {
                    mem = color[0][2][0];
                    color[0][2][0] = color[0][2][1];
                    color[0][2][1] = color[0][2][2];
                    color[0][2][2] = color[1][2][2];
                    color[1][2][2] = color[1][1][2];
                    color[1][1][2] = color[1][0][2];
                    color[1][0][2] = color[5][0][2];
                    color[5][0][2] = color[5][0][1];
                    color[5][0][1] = color[5][0][0];
                    color[5][0][0] = color[3][0][0];
                    color[3][0][0] = color[3][1][0];
                    color[3][1][0] = color[3][2][0];
                    color[3][2][0] = mem;
                    if (i != 1) {
                        mem = color[4][0][0];
                        color[4][0][0] = color[4][0][1];
                        color[4][0][1] = color[4][0][2];
                        color[4][0][2] = color[4][1][2];
                        color[4][1][2] = color[4][2][2];
                        color[4][2][2] = color[4][2][1];
                        color[4][2][1] = color[4][2][0];
                        color[4][2][0] = color[4][1][0];
                        color[4][1][0] = mem;
                    }
                }
                break;
            case 13:
                for (i = 0; i < 3; i++) {
                    mem = color[0][2][2];
                    color[0][2][2] = color[0][2][1];
                    color[0][2][1] = color[0][2][0];
                    color[0][2][0] = color[3][2][0];
                    color[3][2][0] = color[3][1][0];
                    color[3][1][0] = color[3][0][0];
                    color[3][0][0] = color[5][0][0];
                    color[5][0][0] = color[5][0][1];
                    color[5][0][1] = color[5][0][2];
                    color[5][0][2] = color[1][0][2];
                    color[1][0][2] = color[1][1][2];
                    color[1][1][2] = color[1][2][2];
                    color[1][2][2] = mem;
                    if (i != 1) {
                        mem = color[4][0][0];
                        color[4][0][0] = color[4][1][0];
                        color[4][1][0] = color[4][2][0];
                        color[4][2][0] = color[4][2][1];
                        color[4][2][1] = color[4][2][2];
                        color[4][2][2] = color[4][1][2];
                        color[4][1][2] = color[4][0][2];
                        color[4][0][2] = color[4][0][1];
                        color[4][0][1] = mem;
                    }
                }
                break;
            case 14:
                for (i = 0; i < 3; i++) {
                    mem = color[0][1][0];
                    color[0][1][0] = color[0][1][1];
                    color[0][1][1] = color[0][1][2];
                    color[0][1][2] = color[1][2][1];
                    color[1][2][1] = color[1][1][1];
                    color[1][1][1] = color[1][0][1];
                    color[1][0][1] = color[5][1][2];
                    color[5][1][2] = color[5][1][1];
                    color[5][1][1] = color[5][1][0];
                    color[5][1][0] = color[3][0][1];
                    color[3][0][1] = color[3][1][1];
                    color[3][1][1] = color[3][2][1];
                    color[3][2][1] = mem;
                }
                break;
            case 15:
                for (i = 0; i < 3; i++) {
                    mem = color[0][1][2];
                    color[0][1][2] = color[0][1][1];
                    color[0][1][1] = color[0][1][0];
                    color[0][1][0] = color[3][2][1];
                    color[3][2][1] = color[3][1][1];
                    color[3][1][1] = color[3][0][1];
                    color[3][0][1] = color[5][1][0];
                    color[5][1][0] = color[5][1][1];
                    color[5][1][1] = color[5][1][2];
                    color[5][1][2] = color[1][0][1];
                    color[1][0][1] = color[1][1][1];
                    color[1][1][1] = color[1][2][1];
                    color[1][2][1] = mem;
                }
                break;
            case 16:
                for (i = 0; i < 3; i++) {
                    mem = color[0][0][0];
                    color[0][0][0] = color[0][0][1];
                    color[0][0][1] = color[0][0][2];
                    color[0][0][2] = color[1][2][0];
                    color[1][2][0] = color[1][1][0];
                    color[1][1][0] = color[1][0][0];
                    color[1][0][0] = color[5][2][2];
                    color[5][2][2] = color[5][2][1];
                    color[5][2][1] = color[5][2][0];
                    color[5][2][0] = color[3][0][2];
                    color[3][0][2] = color[3][1][2];
                    color[3][1][2] = color[3][2][2];
                    color[3][2][2] = mem;
                    if (i != 1) {
                        mem = color[2][0][0];
                        color[2][0][0] = color[2][1][0];
                        color[2][1][0] = color[2][2][0];
                        color[2][2][0] = color[2][2][1];
                        color[2][2][1] = color[2][2][2];
                        color[2][2][2] = color[2][1][2];
                        color[2][1][2] = color[2][0][2];
                        color[2][0][2] = color[2][0][1];
                        color[2][0][1] = mem;
                    }
                }
                break;
            case 17:
                for (i = 0; i < 3; i++) {
                    mem = color[0][0][2];
                    color[0][0][2] = color[0][0][1];
                    color[0][0][1] = color[0][0][0];
                    color[0][0][0] = color[3][2][2];
                    color[3][2][2] = color[3][1][2];
                    color[3][1][2] = color[3][0][2];
                    color[3][0][2] = color[5][2][0];
                    color[5][2][0] = color[5][2][1];
                    color[5][2][1] = color[5][2][2];
                    color[5][2][2] = color[1][0][0];
                    color[1][0][0] = color[1][1][0];
                    color[1][1][0] = color[1][2][0];
                    color[1][2][0] = mem;
                    if (i != 1) {
                        mem = color[2][0][0];
                        color[2][0][0] = color[2][0][1];
                        color[2][0][1] = color[2][0][2];
                        color[2][0][2] = color[2][1][2];
                        color[2][1][2] = color[2][2][2];
                        color[2][2][2] = color[2][2][1];
                        color[2][2][1] = color[2][2][0];
                        color[2][2][0] = color[2][1][0];
                        color[2][1][0] = mem;
                    }
                }
                break;
        }
    }
}
