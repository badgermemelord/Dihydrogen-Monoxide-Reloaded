package io.github.CoolMineman;

import java.util.*;

// Class to store the pairs
class Pair implements Comparable<Pair> {
    int first;
    int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair o) {
        return second - o.second;
    }
}

class GFG {
    public static int validCoord(int x, int y, int n, int m)
    {
        if (x < 0 || y < 0) {
            return 0;
        }
        if (x >= n || y >= m) {
            return 0;
        }
        return 1;
    }
    // Function to run bfs
    public static int[] bfs(int n, int m, int data[][],int x, int y, int color)
    {

        // Visiting array
        int vis[][]=new int[101][101];

        // Initialing all as zero
        for(int i=0;i<=100;i++){
            for(int j=0;j<=100;j++){
                vis[i][j]=0;
            }
        }

        // Creating queue for bfs
        Queue<Pair> obj = new LinkedList<>();

        // Pushing pair of {x, y}
        Pair pq=new Pair(x,y);
        obj.add(pq);

        // Marking {x, y} as visited
        vis[x][y] = 1;

        // Until queue is empty
        while (!obj.isEmpty())
        {
            // Extracting front pair
            Pair coord = obj.peek();
            int x1 = coord.first;
            int y1 = coord.second;
            int preColor = data[x1][y1];

            data[x1][y1] += 10;

            // Popping front pair of queue
            obj.remove();

            // For Upside Pixel or Cell
            if ((validCoord(x1 + 1, y1, n, m)==1) && vis[x1 + 1][y1] == 0 && data[x1 + 1][y1] >=0)
            {
                Pair p=new Pair(x1 +1, y1);
                obj.add(p);
                vis[x1 + 1][y1] = 1;
            }

            // For Downside Pixel or Cell
            if ((validCoord(x1 - 1, y1, n, m)==1) && vis[x1 - 1][y1] == 0 && data[x1 - 1][y1] >=0)
            {
                Pair p=new Pair(x1-1,y1);
                obj.add(p);
                vis[x1- 1][y1] = 1;
            }

            // For Right side Pixel or Cell
            if ((validCoord(x1, y1 + 1, n, m)==1) && vis[x1][y1 + 1] == 0 && data[x1][y1 + 1] >=0)
            {
                Pair p=new Pair(x1,y1 +1);
                obj.add(p);
                vis[x1][y1 + 1] = 1;
            }

            // For Left side Pixel or Cell
            if ((validCoord(x1, y1 - 1, n, m)==1) && vis[x1][y1 - 1] == 0 && data[x1][y1 - 1] >=0)
            {
                Pair p=new Pair(x1,y1 -1);
                obj.add(p);
                vis[x1][y1 - 1] = 1;
            }
        }
        int[] newData = new int[n*m];
        int counter = 0;
        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                newData[counter] = data[j][i];
                counter += 1;
            }

        }
        return newData;


        // Printing The Changed Matrix Of Pixels

        /*for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < m; j++)
            {
                System.out.print(data[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();*/
    }
    public static int[][] getData(int[][] data) {
        return data;
    }

    public static int[] printma(int[][] data, int dia, int radius) {
        System.out.println("data arrived at floodfill: " + Arrays.deepToString(data));
        int nn, mm, xx, yy, colorr;
        nn = dia;
        mm = dia;


        xx = radius;
        yy = radius;
        colorr = 3;


        // Function Call
        return bfs(nn, mm, data, xx, yy, colorr);

    }
}