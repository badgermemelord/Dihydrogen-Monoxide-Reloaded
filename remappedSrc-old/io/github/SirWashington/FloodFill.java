package io.github.SirWashington;

public class FloodFill {

    public static int[][] flood(int[][] data, int x, int y) {
        dfsFill(data, x, y);
        return data;
    }
    public static void dfsFill(int[][] data, int i, int j){
        if(i >= 0 && j >=0 && i < data.length && j < data[0].length && data[i][j]>= -1 && data[i][j] < 10){
            if (data[i][j] >= 0) {
                data[i][j]+=10;
                dfsFill(data, i + 1, j);
                dfsFill(data, i - 1, j);
                dfsFill(data, i, j + 1);
                dfsFill(data, i, j - 1);
            }
            else {
                data[i][j] = -2;
            }
        }
    }
}