import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BFS_DFS_Unknown {
    private int initialAngle;
    private int initialRow;
    private int initialCol;
    private int angle;
    private int row;
    private int col;
    private HashSet<String> cellsCleaned = new HashSet<>();
    private int[][] grid, visited;
    private GridPane gridPane;
    private int rectDimensions, speed;
    private Stack<int[]> stack;
//    int[] dx = {0, 1, 0, -1, -1, -1, 1, 1};
//    int[] dy = {-1, 0, 1, 0, -1, 1, -1, 1};
    int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
    int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};


    public BFS_DFS_Unknown(int initialRow, int initialCol, int[][] grid, GridPane gridPane, int dimensions, int speed) {
        this.initialRow = initialRow;
        this.initialCol = initialCol;
        this.row = initialRow;
        this.col = initialCol;
        this.grid = grid;
        this.gridPane = gridPane;
        this.speed = speed;
        visited = new int[grid.length][grid[0].length];
        stack = new Stack<>();
        rectDimensions = dimensions;

        Rectangle rectangle = new Rectangle(dimensions, dimensions);
        rectangle.setFill(Color.YELLOW);
        gridPane.add(rectangle, initialCol, initialRow);
    }

    public boolean isValid  (int x, int y) {
        if(x < 0 || x >= grid.length || y < 0 || y >= grid[0].length || grid[x][y] == 0)
            return false;

            if(visited[x][y] == 1)
                return false;

        return true;
    }

    public void dfs() throws InterruptedException {
        stack.push(new int[]{initialRow, initialCol});

        while(stack.size() > 0){
            int currX = stack.peek()[0];
            int currY = stack.peek()[1];
            stack.pop();

            if(!isValid(currX, currY))
                continue;
            if((Math.abs(initialRow-currX) + Math.abs(initialCol-currY)) > 2)
                shortestPath(new int[]{initialRow, initialCol}, new int[]{currX, currY});

            System.out.println("Current: (" + initialRow + "," + initialCol + ")");
            System.out.println("Destination: (" + currX + "," + currY + ")");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.YELLOW);
                    gridPane.add(rectangle, currY, currX);
                }
            });
            Thread.sleep(speed);

            //cleaning time
            if(grid[currX][currY] == 1) {
                Thread.sleep(speed * 2);
                grid[currX][currY] = 2;
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.rgb(0, 230, 0));
                    gridPane.add(rectangle, currY, currX);
                }
            });
            Thread.sleep(speed/2);

            //mark it as visited
            if(visited[currX][currY] == 0)
                visited[currX][currY] = 1;
                for(int i = 0; i < 8; i++)
                {
                    int adjx = currX + dx[i];
                    int adjy = currY + dy[i];
                    if(isValid(adjx, adjy) && visited[adjx][adjy] == 0)
                        stack.push(new int[]{adjx, adjy});
                }
                initialCol = currY;
                initialRow = currX;
//                if(!stack.isEmpty())
//                    shortestPath(new int[]{currX, currY}, new int[]{stack.getLast()[0], stack.getLast()[1]});


            System.out.println("(" + currX + "," + currY + ")");


        }
        //return to initial position
        shortestPath(new int[]{initialRow, initialCol}, new int[]{row, col});
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.YELLOW);
                gridPane.add(rectangle, col, row);
            }
        });
        Thread.sleep(speed);

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
        } else {
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
            } while ((p = p.prev) != null);
            System.out.println(path);

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
//        if(visited[x][y] == 1){
        int dist = parent.dist + 1;
        Cell p = cells[x][y];
        if (dist < p.dist) {
            p.dist = dist;
            p.prev = parent;
            queue.add(p);
//            visited[x][y] = 1;
//            q.add(new int[]{p.x, p.y});
        }
//        }
//        else
//            return;
    }




}



