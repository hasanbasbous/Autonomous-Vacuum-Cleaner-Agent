import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.LinkedList;

public class CDT {
    int[] start;
    private int[][] grid, visited;
    private GridPane gridPane;
    private LinkedList<int[]> q;
    private int rectDimensions, speed;

    public CDT(int[] start, int[][] grid, GridPane gridPane, LinkedList<int[]> q, int dimensions, int speed) {
        this.start = start;
        this.grid = grid;
        this.gridPane = gridPane;
        this.q = q;
        this.speed = speed;
        visited = new int[grid.length][grid[0].length];
        rectDimensions = dimensions;
    }

    public void shortestPath() throws InterruptedException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.YELLOW);
                gridPane.add(rectangle, start[1], start[0]);
            }
        });
        Thread.sleep(speed);
        int[] source = start;
        int[] poll = new int[2];
        int min = Integer.MAX_VALUE;

        while(q.size() > 0) {
            int index = 0;
            for (int i = 0; i < q.size(); i++) {
                int dist = shortestPath(start, q.get(i));
                if (dist < min && dist != -1 && dist != 0) {
                    min = dist;
                    poll = q.get(i);
                    index = i;
                }
            }
            shortestPath_1(start, poll);
            start = poll;
            q.remove(index);
            min = Integer.MAX_VALUE;

        }

        System.out.println("Last cell polled: " + Arrays.toString(source));
        shortestPath_1(start, source); //return to the start position from source the last visited
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.YELLOW);
                gridPane.add(rectangle, source[1], source[0]);
            }
        });
        Thread.sleep(speed);

    }

    //return -1 if no path
    //return 0 if start same as end tile
    //return distance between start and end tile
    //used to compute the distance between a dirty src tiles and all the other dirty tiles
    public int shortestPath(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];
        int distance = -1;
        //if start or end value is 0, return
        if (grid[sx][sy] == 0 || grid[dx][dy] == 0) {
            System.out.println("There is no path.");
            return -1;
        }

        if(sx == dx && sy == dy)
            return 0;

        //initialize the cells
        int m = grid.length;
        int n = grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(grid[i][j] != 0) //why not equal to 1
                    cells[i][j] = new Cell(i, j, Integer.MAX_VALUE, null);
            }

        LinkedList<Cell> queue = new LinkedList<>();
        Cell src = cells[sx][sy];
        src.dist = 0;
        queue.add(src);
        Cell dest = null;
        Cell p;
        while ((p = queue.poll()) != null){
            if(p.x == dx && p.y == dy){
                dest = p;
                break;
            }
            // moving up
            visit(cells, queue, p.x -1, p.y, p);
            // moving down
            visit(cells, queue, p.x +1, p.y, p);
            // moving left
            visit(cells, queue, p.x, p.y - 1, p);
            //moving right
            visit(cells, queue, p.x, p.y + 1, p);
            //bottom left corner
            visit(cells, queue, p.x -1, p.y-1, p);
            //top left corner
            visit(cells, queue, p.x -1, p.y+1, p);
            //bottom right corner
            visit(cells, queue, p.x+1, p.y - 1, p);
            //top right corner
            visit(cells, queue, p.x+1, p.y + 1, p);
        }

        if (dest == null) {
            System.out.println("there is no path.");
            return -1;
        } else {

            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
                distance += 1;
            } while ((p = p.prev) != null);
            System.out.println(path);

            return distance;
        }

    }

    private boolean visit(Cell[][] cells, LinkedList<Cell> queue, int x, int y, Cell parent){
        //out of bound
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length || cells[x][y] == null) {
            return false;
        }
        int dist = parent.dist + 1;
        Cell p = cells[x][y];
        if (dist < p.dist) {
            p.dist = dist;
            p.prev = parent;
            queue.add(p);
        }
        return true;
    }

    //after settling on the shortest dirty tile
    //this method will do the actual moves on the grid
    public boolean shortestPath_1(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        //if start or end value is 0, return
        if (grid[sx][sy] == 0 || grid[dx][dy] == 0) {
            System.out.println("There is no path.");
            return false;
        }

        if(sx == dx && sy == dy)
            return true;

        //initialize the cells
        int m = grid.length;
        int n = grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(grid[i][j] != 0) //why not equal to 1
                    cells[i][j] = new Cell(i, j, Integer.MAX_VALUE, null);
            }

        LinkedList<Cell> queue = new LinkedList<>();
        Cell src = cells[sx][sy];
        src.dist = 0;
        queue.add(src);
        Cell dest = null;
        Cell p;
        while ((p = queue.poll()) != null){
            if(p.x == dx && p.y == dy){
                dest = p;
                break;
            }
            // moving up
            visit(cells, queue, p.x -1, p.y, p);
            // moving down
            visit(cells, queue, p.x +1, p.y, p);
            // moving left
            visit(cells, queue, p.x, p.y - 1, p);
            //moving right
            visit(cells, queue, p.x, p.y + 1, p);

            visit(cells, queue, p.x -1, p.y-1, p);

            visit(cells, queue, p.x -1, p.y+1, p);

            visit(cells, queue, p.x+1, p.y - 1, p);

            visit(cells, queue, p.x+1, p.y + 1, p);
        }

        if (dest == null) {
            System.out.println("there is no path.");
            return false;
        } else {
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
            } while ((p = p.prev) != null);
            System.out.println(path);

            while(!path.isEmpty()){
                Cell c = path.remove();
                visited[c.x][c.y] = 1;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.YELLOW);
                        gridPane.add(rectangle, c.y, c.x);
                    }
                });
                Thread.sleep(speed);

                if(grid[c.x][c.y] == 1) {
                    Thread.sleep(speed * 2);
                    grid[c.x][c.y] = 2;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.rgb(0, 230, 0));
                        gridPane.add(rectangle, c.y, c.x);
                    }
                });
                Thread.sleep(speed/2);
            }
        }
        return true;

    }
}

