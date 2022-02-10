import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class DFS {
        private int initialAngle;
        private int initialRow;
        private int initialCol;
        private int row;
        private int col;
        private int[][] grid, visited;
        private GridPane gridPane;
        private int rectDimensions, speed;
        private Stack<int[]> stack;
        int[] dx = {0, 1, 0, -1, -1, -1, 1, 1};
        int[] dy = {-1, 0, 1, 0, -1, 1, -1, 1};


        public DFS(int initialRow, int initialCol, int[][] grid, GridPane gridPane, int dimensions, int speed) {
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
            if(isValid(stack.peek()[0], stack.peek()[1]))
            {
                while (stack.size() > 0) {
                    int currX = stack.peek()[0];
                    int currY = stack.peek()[1];
                    stack.pop();
            //Uncomment to remove jumps
                if(!isValid(currX, currY))
                    continue;

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                            rectangle.setFill(Color.YELLOW);
                            gridPane.add(rectangle, currY, currX);
                        }
                    });
                    Thread.sleep(speed);

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
                    Thread.sleep(speed / 2);

                    //mark it as visited
                    if (visited[currX][currY] == 0) {
                        visited[currX][currY] = 1;
                        for (int i = 0; i < 8; i++) {
                            int adjx = currX + dx[i];
                            int adjy = currY + dy[i];
                            if (isValid(adjx, adjy))
                                stack.push(new int[]{adjx, adjy});
                        }
                    }

                    System.out.println("(" + currX + "," + currY + ")");


                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.YELLOW);
                        gridPane.add(rectangle, initialCol, initialRow);
                    }
                });
                Thread.sleep(speed);
            }

        }




    }



