import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;


public class FourAgents {
    private int[][]  visited;
    private GridPane gridPane;
    private int rectDimensions, speed;

    public FourAgents(GridPane gridPane, int dimensions){
        this.gridPane = gridPane;
        rectDimensions = dimensions;
        visited = new int[Main.grid.length][Main.grid[0].length];
    }

    public void shortestPathDirt() throws InterruptedException {

//        while(Main.dirt.size() == 0);

        LinkedList<Cell> path = new LinkedList<>();
        DistPath distPath;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < Main.dirt.size(); i++) {
            distPath = shortestPathDirty_1(Main.startPosVacClnr, Main.dirt.get(i));
            if (distPath.distance < min && distPath.distance != -1) {
                min = distPath.distance;
                path = distPath.path;
            }
        }
        if(path.size() != 0) {
            int x, y;
            x = path.getLast().x;
            y = path.getLast().y;
            for(int i = 0; i < Main.dirt.size(); i++){
                if(x == Main.dirt.get(i)[0] && y == Main.dirt.get(i)[1]){
                    Main.dirt.remove(i);
                    break;
                }
            }

            shortestPathDisplayVacClnr(path);
            Main.clean.add(new int[]{x, y});

            Main.grid[Main.startPosVacClnr[0]][Main.startPosVacClnr[1]] = 4;
            Main.flag += 5;
//            Main.text.setText("flag: " + Main.flag);
            Main.cleanTiles++;
        }
        else return;
    }

    public DistPath shortestPathDirty_1(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        int distance = -1;

        if (Main.grid[sx][sy] == 0 || Main.grid[dx][dy] == 0) {
            System.out.println("There is no path.");
//            Main.flag = 1;
            return new DistPath(new LinkedList<Cell>(), -1);
        }
        // this case is not reached, because the standing tile is marked 3 as the vacuum cleaner
        if(sx == dx && sy == dy)
            return new DistPath(new LinkedList<Cell>(), -1);

        //initialize the cells
        int m = Main.grid.length;
        int n = Main.grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(Main.grid[i][j] != 0 && Main.grid[i][j] != 3 && Main.grid[i][j] != 5 && Main.grid[i][j] != 6)
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
        }

        if (dest == null) {
            System.out.println("there is no path.");
//            Main.flag = 1;
            return new DistPath(new LinkedList<Cell>(), -1);
        } else{
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
                distance += 1;

            } while ((p = p.prev) != null);
            System.out.println("VacClnr: " + path);
            return new DistPath(path, distance);
        }
    }

    public void shortestPathDisplayVacClnr(LinkedList<Cell> path) throws InterruptedException {
        int end [] = new int[2];
        int before[] = new int[2];
        if(path.size() != 0){
            Cell c1 = path.remove();
            end[0] = c1.x;
            end[1] = c1.y;
            if(Main.grid[end[0]][end[1]] != 3 && Main.grid[end[0]][end[1]] != 5 && Main.grid[end[0]][end[1]] != 6){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.rgb(0, 230, 0));
                        gridPane.add(rectangle, c1.y, c1.x);
//                        Main.grid[c1.x][c1.y] = 2;
                        // in case the path has only one move
                    }
                });
                Thread.sleep(100);
            }
        }

        while(!path.isEmpty()){
//            if(Main.grid[end[0]][end[1]] != 3){
            // before is the parent
            before[0] = end[0];
            before[1] = end[1];
            Cell c = path.remove();
            end[0] = c.x;
            end[1] = c.y;

            if (Main.grid[c.x][c.y] == 3 || Main.grid[c.x][c.y] == 5 || Main.grid[c.x][c.y] == 6)
                return;
            Main.flag--; //disadvantage for vacuum cleaner
            Main.vacuumCleanerMoves++;
            Main.grid[before[0]][before[1]] = 2;
            Main.grid[end[0]][end[1]] = 4;
            Main.startPosVacClnr[0] = c.x;
            Main.startPosVacClnr[1] = c.y;

            //the closest dirt will not pass through a dirt
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.YELLOW);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.rgb(0, 230, 0));
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
//            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.YELLOW);
                gridPane.add(rectangle, end[1], end[0]);
            }
        });
        Thread.sleep(50);

    }

    public void shortestPathDirtAgent2() throws InterruptedException {

//        while(Main.dirt.size() == 0);

        LinkedList<Cell> path = new LinkedList<>();
        DistPath distPath;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < Main.dirt.size(); i++) {
            distPath = shortestPathDirtyAgent2_1(Main.startPosVacClnr2, Main.dirt.get(i));
            if (distPath.distance < min && distPath.distance != -1) {
                min = distPath.distance;
                path = distPath.path;
            }
        }
        if(path.size() != 0) {
            int x, y;
            x = path.getLast().x;
            y = path.getLast().y;
            for(int i = 0; i < Main.dirt.size(); i++){
                if(x == Main.dirt.get(i)[0] && y == Main.dirt.get(i)[1]){
                    Main.dirt.remove(i);
                    break;
                }
            }

            shortestPathDisplayVacClnrAgent2(path);
            Main.clean.add(new int[]{x, y});

            Main.grid[Main.startPosVacClnr2[0]][Main.startPosVacClnr2[1]] = 6;
            Main.flag += 5;
//            Main.text.setText("flag: " + Main.flag);
            Main.cleanTiles++;
        }
        else return;
    }

    public DistPath shortestPathDirtyAgent2_1(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        int distance = -1;

        if (Main.grid[sx][sy] == 0 || Main.grid[dx][dy] == 0) {
            System.out.println("There is no path.");
//            Main.flag = 1;
            return new DistPath(new LinkedList<Cell>(), -1);
        }
        // this case is not reached, because the standing tile is marked 3 as the vacuum cleaner
        if(sx == dx && sy == dy)
            return new DistPath(new LinkedList<Cell>(), -1);

        //initialize the cells
        int m = Main.grid.length;
        int n = Main.grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(Main.grid[i][j] != 0 && Main.grid[i][j] != 3 && Main.grid[i][j] != 5 || Main.grid[i][j] != 4)
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
        }

        if (dest == null) {
            System.out.println("there is no path.");
//            Main.flag = 1;
            return new DistPath(new LinkedList<Cell>(), -1);
        } else{
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
                distance += 1;

            } while ((p = p.prev) != null);
            System.out.println("VacClnr: " + path);
            return new DistPath(path, distance);
        }
    }

    public void shortestPathDisplayVacClnrAgent2(LinkedList<Cell> path) throws InterruptedException {
        int end [] = new int[2];
        int before[] = new int[2];
        if(path.size() != 0){
            Cell c1 = path.remove();
            end[0] = c1.x;
            end[1] = c1.y;
            if(Main.grid[end[0]][end[1]] != 3 && Main.grid[end[0]][end[1]] != 5 && Main.grid[end[0]][end[1]] != 4){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.rgb(0, 230, 0));
                        gridPane.add(rectangle, c1.y, c1.x);
//                        Main.grid[c1.x][c1.y] = 2;
                        // in case the path has only one move
                    }
                });
                Thread.sleep(100);
            }
        }

        while(!path.isEmpty()){
//            if(Main.grid[end[0]][end[1]] != 3){
            // before is the parent
            before[0] = end[0];
            before[1] = end[1];
            Cell c = path.remove();
            end[0] = c.x;
            end[1] = c.y;

            if (Main.grid[c.x][c.y] == 3 || Main.grid[c.x][c.y] == 5 || Main.grid[c.x][c.y] == 4)
                return;
            Main.flag--; //disadvantage for vacuum cleaner
            Main.vacuumCleanerMoves++;
            Main.grid[before[0]][before[1]] = 2;
            Main.grid[end[0]][end[1]] = 4;
            Main.startPosVacClnr2[0] = c.x;
            Main.startPosVacClnr2[1] = c.y;

            //the closest dirt will not pass through a dirt
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.YELLOW);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.rgb(0, 230, 0));
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
//            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.YELLOW);
                gridPane.add(rectangle, end[1], end[0]);
            }
        });
        Thread.sleep(50);

    }

    public void shortestPathClean() throws InterruptedException {

        if(Main.clean.size() == 0)
            return;

        LinkedList<Cell> path = new LinkedList<>();
        DistPath distPath;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < Main.clean.size(); i++) {
            distPath = shortestPathClean_1(Main.startPosDrtPrdcr, Main.clean.get(i));
            if (distPath.distance < min && distPath.distance != -1) {
                min = distPath.distance;
                path = distPath.path;
            }
        }
        if(path.size() != 0){
            int x = Main.startPosDrtPrdcr[0];
            int y = Main.startPosDrtPrdcr[1];

            // I created x, y because when the reference of startPosProd changes to (2, 0)
//            Main.grid[x][y] = 1;
            // it changes from (0, 0) to (2, 0) in the first iteration
            Main.dirt.add(new int[]{x, y});

            x = path.getLast().x;
            y = path.getLast().y;
            for(int i = 0; i < Main.clean.size(); i++){
                if(x == Main.clean.get(i)[0] && y == Main.clean.get(i)[1]){
                    Main.clean.remove(i);
                    break;
                }
            }
            shortestPathDisplayDirtPrdcr(path);

            //will it be put into one in shortestPathDisplayDirtPrdcr
            Main.grid[Main.startPosDrtPrdcr[0]][Main.startPosDrtPrdcr[1]] = 3; //update the position of the dirt producer

            Main.flag -= 5;
//            Main.text.setText("flag: " + Main.flag);
            Main.dirtyTiles++;
        } else
            return;
    }

    // I want the start position to be the position of the dirt cleaner
    // I want to find the closest next clean
    public DistPath shortestPathClean_1(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        int distance = -1;

        if (Main.grid[sx][sy] == 0 || Main.grid[dx][dy] == 0) {
            System.out.println("There is no path.");
//            Main.flag = -1;
            return new DistPath(new LinkedList<Cell>(), -1);
        }
        // this case is not reached, because the standing tile is marked 3 as the vacuum cleaner
        if(sx == dx && sy == dy)
            return new DistPath(new LinkedList<Cell>(), -1);

        //initialize the cells
        int m = Main.grid.length;
        int n = Main.grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(Main.grid[i][j] != 0 && Main.grid[i][j] != 4 && Main.grid[i][j] != 5 || Main.grid[i][j] != 6)
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
        }

        if (dest == null) {
            System.out.println("there is no path.");
//            Main.flag = -1;
            return new DistPath(new LinkedList<Cell>(), -1);
        } else{
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
                distance += 1;

            } while ((p = p.prev) != null);
            System.out.println("DirtPrdcr: " + path);
            return new DistPath(path, distance);
        }
    }


    // I want my start position to be the vacuum cleaner
    // I want to find the next closest dirt like the tsp

    public void shortestPathDisplayDirtPrdcr(LinkedList<Cell> path) throws InterruptedException {
        int end [] = new int[2];
        int before[] = new int[2];
        if(path.size() != 0){
            Cell c1 = path.remove();
            end[0] = c1.x;
            end[1] = c1.y;
            if(Main.grid[c1.x][c1.y] != 4 && Main.grid[c1.x][c1.y] != 5 && Main.grid[c1.x][c1.y] != 6){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.BLACK);
                        gridPane.add(rectangle, c1.y, c1.x);
                    }
                });
                Thread.sleep(100);
            }
        }

        while(!path.isEmpty()){
//            if(Main.grid[end[0]][end[1]] != 4){
            before[0] = end[0];
            before[1] = end[1];
            Cell c = path.remove();
            end[0] = c.x;
            end[1] = c.y;

            if (Main.grid[c.x][c.y] == 4 || Main.grid[c.x][c.y] == 5 || Main.grid[c.x][c.y] == 6) {
                System.out.println("Vacuum Cleaner faced");
                return;
            }
//                Main.grid[c.x][c.y] = 1; // in case of dirt update grid
            Main.flag++; //advantage for vacuum cleaner
            Main.dirtProducerMoves++;
            Main.grid[before[0]][before[1]] = 1;
            Main.grid[end[0]][end[1]] = 3;
            Main.startPosDrtPrdcr[0] = c.x;
            Main.startPosDrtPrdcr[1] = c.y;

            //the closest dirt will not pass through a dirt
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.ORANGE);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.BLACK);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
//            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.ORANGE);
                gridPane.add(rectangle, end[1], end[0]);

            }
        });
        Thread.sleep(50);
    }

    public void shortestPathCleanAgent2() throws InterruptedException {

        if(Main.clean.size() == 0)
            return;

        LinkedList<Cell> path = new LinkedList<>();
        DistPath distPath;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < Main.clean.size(); i++) {
            distPath = shortestPathClean_2(Main.startPosDrtPrdcr2, Main.clean.get(i));
            if (distPath.distance < min && distPath.distance != -1) {
                min = distPath.distance;
                path = distPath.path;
            }
        }
        if(path.size() != 0){
            int x = Main.startPosDrtPrdcr2[0];
            int y = Main.startPosDrtPrdcr2[1];

            // I created x, y because when the reference of startPosProd changes to (2, 0)
//            Main.grid[x][y] = 1;
            // it changes from (0, 0) to (2, 0) in the first iteration
            Main.dirt.add(new int[]{x, y});
            x = path.getLast().x;
            y = path.getLast().y;
            for(int i = 0; i < Main.clean.size(); i++){
                if(x == Main.clean.get(i)[0] && y == Main.clean.get(i)[1]){
                    Main.clean.remove(i);
                    break;
                }
            }
            shortestPathDisplayDirtPrdcr2(path);

            //will it be put into one in shortestPathDisplayDirtPrdcr
            Main.grid[Main.startPosDrtPrdcr2[0]][Main.startPosDrtPrdcr2[1]] = 5; //update the position of the dirt producer

            Main.flag -= 5;
//            Main.text.setText("flag: " + Main.flag);
            Main.dirtyTiles++;
        } else
            return;
    }

    // I want the start position to be the position of the dirt cleaner
    // I want to find the closest next clean
    private DistPath shortestPathClean_2(int[] start, int[] end) throws InterruptedException {
        int sx = start[0], sy = start[1];
        int dx = end[0], dy = end[1];

        int distance = -1;

        if (Main.grid[sx][sy] == 0 || Main.grid[dx][dy] == 0) {
            System.out.println("There is no path.");
//            Main.flag = -1;
            return new DistPath(new LinkedList<Cell>(), -1);
        }
        // this case is not reached, because the standing tile is marked 3 as the vacuum cleaner
        if(sx == dx && sy == dy)
            return new DistPath(new LinkedList<Cell>(), -1);

        //initialize the cells
        int m = Main.grid.length;
        int n = Main.grid[0].length;
        Cell[][] cells = new Cell[m][n];
        for(int i = 0; i< m; i++)
            for(int j = 0; j< n; j++){
                if(Main.grid[i][j] != 0 && Main.grid[i][j] != 4 && Main.grid[i][j] != 3 && Main.grid[i][j] != 6)
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
        }

        if (dest == null) {
            System.out.println("there is no path.");
//            Main.flag = -1;
            return new DistPath(new LinkedList<Cell>(), -1);
        } else{
            LinkedList<Cell> path = new LinkedList<>();
            p = dest;
            do {
                path.addFirst(p);
                distance += 1;

            } while ((p = p.prev) != null);
            System.out.println("DirtPrdcr: " + path);
            return new DistPath(path, distance);
        }
    }

    public void shortestPathDisplayDirtPrdcr2(LinkedList<Cell> path) throws InterruptedException {
        int end [] = new int[2];
        int before[] = new int[2];
        if(path.size() != 0){
            Cell c1 = path.remove();
            end[0] = c1.x;
            end[1] = c1.y;
            if(Main.grid[c1.x][c1.y] != 4 && Main.grid[c1.x][c1.y] != 3 && Main.grid[c1.x][c1.y] != 6){

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                        rectangle.setFill(Color.BLACK);
                        gridPane.add(rectangle, c1.y, c1.x);
                    }
                });
                Thread.sleep(100);
            }
        }

        while(!path.isEmpty()){
//            if(Main.grid[end[0]][end[1]] != 4){
            before[0] = end[0];
            before[1] = end[1];
            Cell c = path.remove();
            end[0] = c.x;
            end[1] = c.y;

            if (Main.grid[c.x][c.y] == 4 || Main.grid[c.x][c.y] == 3 || Main.grid[c.x][c.y] == 6) {
                System.out.println("Vacuum Cleaner faced");
                return;
            }
//                Main.grid[c.x][c.y] = 1; // in case of dirt update grid
            Main.flag++;
            Main.dirtProducerMoves++;
            Main.grid[before[0]][before[1]] = 1;
            Main.grid[end[0]][end[1]] = 5;
            Main.startPosDrtPrdcr2[0] = c.x;
            Main.startPosDrtPrdcr2[1] = c.y;

            //the closest dirt will not pass through a dirt
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.ORANGE);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                    rectangle.setFill(Color.BLACK);
                    gridPane.add(rectangle, c.y, c.x);
                }
            });
            Thread.sleep(200);
//            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Rectangle rectangle = new Rectangle(rectDimensions, rectDimensions);
                rectangle.setFill(Color.ORANGE);
                gridPane.add(rectangle, end[1], end[0]);

            }
        });
        Thread.sleep(50);
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


