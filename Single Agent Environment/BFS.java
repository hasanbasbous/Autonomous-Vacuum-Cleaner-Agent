import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sun.awt.image.ImageWatched;

import java.util.*;

public class BFS {

    int column, row;
    private int[][] grid, visited;
    private GridPane gridPane;
    private ImageView pacman, green;
    private int rectDimensions, speed;
    private Queue<int[]> q;
    private int initialRow, initialColumn;

    int[] dx = {-1, 0, 1, 0, -1, -1, 1, 1};
    int[] dy = {0, 1, 0, -1, -1, 1, -1, 1};

    public BFS(int row, int column, int[][] grid, GridPane gridPane, int dimensions, int speed) {
        initialRow = row;
        initialColumn = column;
        this.column = column;
        this.row = row;
        this.grid = grid;
        visited = new int[grid.length][grid[0].length];
        rectDimensions = dimensions;
        this.gridPane = gridPane;
        this.speed = speed;
        q = new LinkedList<>();
        Rectangle rectangle = new Rectangle(dimensions, dimensions);
        rectangle.setFill(Color.YELLOW);
        gridPane.add(rectangle, column, row);

    }

    public boolean isValid(int row, int column) {
        if (row < 0 || row >= grid.length || column < 0 || column >= grid[0].length)
            return false;

        if (grid[row][column] == 0) return false; // case of an obstacle

        if (visited[row][column] == 1) return false;

        return true;
    }

    public void bfs() throws InterruptedException {
        q.add(new int[]{row, column});
        visited[row][column] = 1;


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.rgb(0, 230, 0));
                gridPane.add(rectangle, column, row);
            }
        });

        if(grid[row][column] == 1)
            Thread.sleep(speed); //cleaning time

        int[] poll = new int[2];

        while (!q.isEmpty()) {
            int currX = q.peek()[0];
            int currY = q.peek()[1];
            poll = q.poll();

            for (int i = 0; i < 8; i++) {
                if (isValid(currX + dx[i], currY + dy[i])) {

                    int newX = currX + dx[i];
                    int newY = currY + dy[i];

                    //to terminate the jumps
                    //shortest path between current and following one
                    shortestPath(new int[]{row, column}, new int[]{currX, currY});

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                            rectangle.setFill(Color.YELLOW);
                            gridPane.add(rectangle, newY, newX);
                        }
                    });
                    Thread.sleep(speed);
                    //cleaning time
                    if(grid[newX][newY] == 1) {
                        Thread.sleep(speed * 2);
                        grid[newX][newY] = 2;
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                            rectangle.setFill(Color.rgb(0, 230, 0));
                            gridPane.add(rectangle, newY, newX);
                        }
                    });
                    Thread.sleep(speed / 2);

                    q.add(new int[]{newX, newY});
                    row = newX;
                    column = newY;
                    visited[newX][newY] = 1;
                    System.out.println("Visited (" + newX + "," + newY + ")");

                }
            }
        }

        //return to the start position from source the last visited
        if (shortestPath(new int[]{row, column}, new int[]{initialRow, initialColumn})) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.YELLOW);
                    gridPane.add(rectangle, initialColumn, initialRow);
                }
            });
            Thread.sleep(speed);
        }

}
    public boolean shortestPath(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        if(sx == dx && sy == dy)
            return true;

        //initialize the cells
        int m = grid.length;
        int n = grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(grid[i][j] != 0)
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
            visit(cells, queue, p.x - 1, p.y, p);
            // moving down
            visit(cells, queue, p.x + 1, p.y, p);
            // moving left
            visit(cells, queue, p.x, p.y - 1, p);
            //moving right
            visit(cells, queue, p.x, p.y + 1, p);
            //left bottom corner
            visit(cells, queue, p.x - 1, p.y - 1, p);
            //left upper corner
            visit(cells, queue, p.x - 1, p.y + 1, p);
            //right bottom corner
            visit(cells, queue, p.x + 1, p.y - 1, p);
            //right upper corner
            visit(cells, queue, p.x + 1, p.y + 1, p);
        }

        if (dest == null) {
            System.out.println("there is no path.");
            return false;
        } else if(dest.dist > 1){
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
            } while ((p = p.prev) != null);
            System.out.println(path);

            //move the pacman along the path
            while(!path.isEmpty()){
                Cell c = path.remove();
                visited[c.x][c.y] = 1; //added lately
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.YELLOW);
                        gridPane.add(rectangle, c.y, c.x);
                    }
                });
                Thread.sleep(speed);
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

    private void visit(Cell[][] cells, LinkedList<Cell> queue, int x, int y, Cell parent){
        //out of bound
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length || cells[x][y] == null) {
            return;
        }

        int dist = parent.dist + 1;
        Cell p = cells[x][y];
        if (dist < p.dist) {
            p.dist = dist;
            p.prev = parent;
            queue.add(p);

        }
    }

}