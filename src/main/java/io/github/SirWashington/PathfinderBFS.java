package io.github.SirWashington;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PathfinderBFS {

    public static int[][] distanceMapperBFS(int[][] matrix, List<Node> holes) {
        holes.forEach((h) -> matrix[h.x][h.y] = 0);
        pathExists(matrix, holes);
        return matrix;
    }

    private static void pathExists(int[][] matrix, List<Node> holes) {
        Queue<Node> queue = new LinkedList<>(holes);

        while (!queue.isEmpty()) {
            Node popped = queue.poll();

            if (matrix[popped.x][popped.y] == -3) {
            } else {
                if (matrix[popped.x][popped.y] > popped.distanceFromSource) {
                    matrix[popped.x][popped.y] = popped.distanceFromSource;
                }
                addNeighbours(popped, matrix, queue);
            }
        }
    }

    private static void addNeighbours(Node popped, int[][] matrix, Queue<Node> queue) {
        if ((popped.x - 1 >= 0 && popped.x - 1 < matrix.length) && matrix[popped.x - 1][popped.y] != -1 && matrix[popped.x - 1][popped.y] > popped.distanceFromSource + 1) {
            queue.add(new Node(popped.x - 1, popped.y, popped.distanceFromSource + 1));
        }
        if ((popped.x + 1 >= 0 && popped.x + 1 < matrix.length) && matrix[popped.x + 1][popped.y] != -1 && matrix[popped.x + 1][popped.y] > popped.distanceFromSource + 1) {
            queue.add(new Node(popped.x + 1, popped.y, popped.distanceFromSource + 1));
        }
        if ((popped.y - 1 >= 0 && popped.y - 1 < matrix.length) && matrix[popped.x][popped.y - 1] != -1 && matrix[popped.x][popped.y - 1] > popped.distanceFromSource + 1) {
            queue.add(new Node(popped.x, popped.y - 1, popped.distanceFromSource + 1));
        }
        if ((popped.y + 1 >= 0 && popped.y + 1 < matrix.length) && matrix[popped.x][popped.y + 1] != -1 && matrix[popped.x][popped.y + 1] > popped.distanceFromSource + 1) {
            queue.add(new Node(popped.x, popped.y + 1, popped.distanceFromSource + 1));
        }
    }

    public static class Node {
        int x;
        int y;
        int distanceFromSource;

        public Node(int x, int y, int dis) {
            this.x = x;
            this.y = y;
            this.distanceFromSource = dis;
        }
    }
}

