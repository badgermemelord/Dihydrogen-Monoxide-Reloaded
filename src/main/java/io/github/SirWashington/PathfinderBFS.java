package io.github.SirWashington;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PathfinderBFS {

    public static int[][] distanceArray = new int[9][9];

    public static int[][] distanceMapperBFS(int[][] matrix, int startX, int startY) {


        pathExists(matrix, startX, startY);
        for(int a = 0; a < distanceArray.length; a++) {
            for(int b = 0; b < distanceArray.length; b++) {
                System.out.print(distanceArray[a][b] + " ");
            }
            System.out.println();
        }
        return distanceArray;
    }


    private static void pathExists(int[][] matrix, int startX, int startY) {

        Node source = new Node(startX, startY, 0);
        Queue<Node> queue = new LinkedList<Node>();

        queue.add(source);

        while(!queue.isEmpty()) {
            Node popped = queue.poll();

            if(matrix[popped.x][popped.y] == 255 ) {

            }
            else {
                matrix[popped.x][popped.y]=-1;
                List<Node> neighbourList = addNeighbours(popped, matrix);
                queue.addAll(neighbourList);
                addToArray(popped.x, popped.y, popped.distanceFromSource);
            }
        }
    }

    private static List<Node> addNeighbours(Node popped, int[][] matrix) {

        List<Node> list = new LinkedList<Node>();

        if((popped.x-1 >= 0 && popped.x-1 < matrix.length) && matrix[popped.x-1][popped.y] != -1) {
            list.add(new Node(popped.x-1, popped.y, popped.distanceFromSource+1));
        }
        if((popped.x+1 >= 0 && popped.x+1 < matrix.length) && matrix[popped.x+1][popped.y] != -1) {
            list.add(new Node(popped.x+1, popped.y, popped.distanceFromSource+1));
        }
        if((popped.y-1 >= 0 && popped.y-1 < matrix.length) && matrix[popped.x][popped.y-1] != -1) {
            list.add(new Node(popped.x, popped.y-1, popped.distanceFromSource+1));
        }
        if((popped.y+1 >= 0 && popped.y+1 < matrix.length) && matrix[popped.x][popped.y+1] != -1) {
            list.add(new Node(popped.x, popped.y+1, popped.distanceFromSource+1));
        }
        return list;
    }

    public static void addToArray(int x, int y, int dist) {
        distanceArray[x][y] = dist;
    }

}
class Node {
    int x;
    int y;
    int distanceFromSource;

    Node(int x, int y, int dis) {
        this.x = x;
        this.y = y;
        this.distanceFromSource = dis;
    }
}
