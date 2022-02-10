import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// Main has been changes to run three agents
public class Main extends Application {
    static int[][] grid;
    static int[] startPosVacClnr = new int[]{3, 1};
    static int[] startPosDrtPrdcr = new int[]{0, 0};
//    static int[] startPosDrtPrdcr2 = new int[]{2, 3};
    static LinkedList<int[]> clean = new LinkedList<>(), dirt = new LinkedList<>();
    static double flag = 0;
    static double movesToClean = 0;
    static double movesToDirt = 0;
    static Text text;
    static Text dirtProducerPerf;
    static Text vacuumCleanerPerf;
    static int cleanTiles = 0, dirtyTiles = 0;
    static int vacuumCleanerMoves = 0, dirtProducerMoves = 0;
    static LinkedList<int[]> vacuumClnrCellList = new LinkedList<>();
    static LinkedList<int[]> vacuumClnrCellListNew = new LinkedList<>();

    static LinkedList<int[]> dirtPrdcrCellList = new LinkedList<>();
    static LinkedList<int[]> dirtPrdcrCellListNew = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        ComboBox<String> mode = new ComboBox<>();
        mode.getItems().addAll("User", "AutoGenerate");

        Button submit = new Button("Submit");

        text = new Text("flag: ");
        dirtProducerPerf = new Text("Dirtproducer_perf (nbMoves/drtdTiles): ");
        vacuumCleanerPerf = new Text("Vacuumcleaner_perf (nbMoves/clndTiles): ");

        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
//        hBox.getChildren().addAll(submit, text);
        hBox.getChildren().addAll(submit, text, vacuumCleanerPerf, dirtProducerPerf);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(hBox, gridPane);

        int dimensions = 80;
//        int p0 = 15, p1 = 20;

//        grid = new int[][]
//                {
//                        {3, 1, 0, 1, 2},
//                        {1, 0, 2, 1, 0},
//                        {2, 0, 0, 2, 1},
//                        {2, 2, 1, 2, 0},
//                        {1, 1, 0, 1, 4}
//                };


//        grid = new int[][]
//                {
//                        {3, 0, 0, 1, 2, 1, 2},
//                        {2, 0, 2, 1, 0, 1, 1},
//                        {1, 0, 0, 2, 1, 1, 2},
//                        {1, 4, 2, 2, 0, 2, 1},
//                        {0, 1, 0, 0, 1, 2, 2},
//                        {2, 1, 0, 1, 2, 1, 2},
//                        {1, 0, 2, 1, 0, 1, 1},
//                        {2, 0, 0, 2, 1, 2, 1},
//                        {2, 2, 1, 2, 0, 1, 1},
//                        {1, 1, 0, 1, 2, 1, 2}
//                };

        startPosVacClnr = new int[]{3, 2};
        startPosDrtPrdcr = new int[]{1, 0};
        grid = new int[][]
                {
                        {1, 0, 0, 1, 2, 1, 2},
                        {3, 0, 2, 1, 0, 1, 1},
                        {2, 0, 0, 2, 1, 1, 2},
                        {2, 2, 4, 1, 0, 1, 1},
                        {0, 1, 0, 0, 1, 1, 2},
                        {1, 1, 0, 1, 1, 1, 2},
                        {1, 0, 2, 1, 0, 1, 1},
                        {1, 0, 0, 2, 1, 2, 1},
                        {1, 2, 1, 2, 0, 1, 1},
                        {1, 1, 0, 1, 2, 1, 2}
                };

//        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("saveGrid.txt"));
//        Object o = ois.readObject();
//        vacuumClnrCellList = (LinkedList<int[]>) o;
//
//
//        ObjectInputStream ois_dirtPrdcr = new ObjectInputStream(new FileInputStream("drtPrdcrNodes.txt"));
//        Object o_dirtPrdcr = ois_dirtPrdcr.readObject();
//        dirtPrdcrCellList = (LinkedList<int[]>) o_dirtPrdcr;

        //list of each of the dirty and clean tiles
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1)
                    dirt.add(new int[]{i, j});
                else if (grid[i][j] == 2)
                    clean.add(new int[]{i, j});
            }
        }


//        int[][] grid = setGrid(10, 10, p0, p1);
        drawGrid(grid, dimensions, gridPane); // you put onside the submit later if you want
        submit.setOnAction(e -> {
            TwoAgents twoAgents = new TwoAgents(gridPane, dimensions);
            System.out.println("Clicked Submit");
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        twoAgents.shortestPathClean();
                        Main.text.setText("flag: " + String.format("%.2f", Main.flag));
//                        Thread.sleep(1);
                        Main.movesToDirt = (double) Main.dirtProducerMoves / Main.dirtyTiles;
                        Main.dirtProducerPerf.setText("Dirtproducer_perf (nbMoves/drtdTiles): " + String.format("%.2f", Main.movesToDirt));
                        Thread.sleep(1);
                    }

//                    bfs.shortestPathClean(startPosDrtPrdcr, new int[]{2, 0});
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                    for (int i = 0; i < vacuumClnrCellListNew.size(); i++) {
                        vacuumClnrCellList.add(Main.vacuumClnrCellListNew.remove());
                    }
                    Object ob = vacuumClnrCellList;

                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saveGrid.txt"));
                        oos.writeObject(ob);
                        System.out.println(vacuumClnrCellList);
                        for (int i = 0; i < dirtPrdcrCellListNew.size(); i++) {
                            dirtPrdcrCellList.add(Main.dirtPrdcrCellListNew.remove());
                        }
                        Object ob_drtPrdcr = dirtPrdcrCellList;
                        ObjectOutputStream oos_drtPrdcr = new ObjectOutputStream(new FileOutputStream("drtPrdcrNodes.txt"));
                        oos_drtPrdcr.writeObject(ob_drtPrdcr);
                        System.out.println(dirtPrdcrCellList);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
//
//                        System.out.println("Clean tiles: " + cleanTiles);
//                        System.out.println("Dirty tiles: " + dirtyTiles);
                        System.out.println("Vacuum cleaner nb of moves: " + vacuumCleanerMoves);
                        System.out.println("Dirt producer nb of moves: " + dirtProducerMoves);
//                        System.out.println(memoryList);
                }
            });
            Thread thread2 = new Thread(() -> {
                try {
                    while (true) {
                        twoAgents.shortestPathDirt();
                        Main.text.setText("flag: " + String.format("%.2f", Main.flag));
//                        Thread.sleep(1);
                        Main.movesToClean = (double) Main.vacuumCleanerMoves / Main.cleanTiles; // the number of moves to clean a tile
                        Main.vacuumCleanerPerf.setText("Vacuumcleaner_perf (nbMoves/clndTiles): " + String.format("%.2f", Main.movesToClean));
                        Thread.sleep(1);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            thread.start();
            thread2.start();


            // It depends on the interrupt time, the agents either appear or not we can add in the catch clause to make them appear
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.schedule(new Runnable() {
                @Override
                public void run() {
                    thread.interrupt();
                    thread2.interrupt();
                }
            }, 25, TimeUnit.SECONDS);

        });
//        gridPane.setOnMouseClicked(ev -> {
//            System.out.println("Clicked grid cell");
//            Node node = (Node) ev.getTarget();
//            i = GridPane.getRowIndex(node);
//            j = GridPane.getColumnIndex(node);
//            if(grid[i][j] != 0){
//                if(count == 0) {
//                    Rectangle rectangle = new Rectangle(dimensions, dimensions);
//                    rectangle.setFill(Color.YELLOW);
//                    gridPane.add(rectangle, j, i);
//                    grid[i][j] = 2; //mark it as clean
//                    count++; //equivalent to set disable}
//                } else if(count == 1){
//                    Rectangle rectangle = new Rectangle(dimensions, dimensions);
//                    rectangle.setFill(Color.ORANGE);
//                    gridPane.add(rectangle, j, i);
//                    grid[i][j] = 2; //mark it as clean
//                    count++;
//                    gridPane.setDisable(true);
//                }
//            }
//        });

        Scene scene = new Scene(root, 2000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public int[][] setGrid(int row, int col, int p0, int p1) {
        int[][] grid = new int[row][col];
        Random rnd = new Random();
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                int x = rnd.nextInt(100) + 1;
                if (x <= p1)
                    grid[i][j] = 1;
                else if (x <= (p1 + p0) && x >= p1)
                    grid[i][j] = 0;
                else if (x >= (p1 + p0))
                    grid[i][j] = 2;
            }
        return grid;
    }

    public void drawGrid(int[][] grid, int dimensions, GridPane gridPane) {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++) {
                Rectangle rectangle = new Rectangle(dimensions, dimensions);
                if (grid[i][j] == 1) { //Black will signal dirt
                    rectangle.setFill(Color.BLACK);

                    gridPane.add(rectangle, j, i);
                } else if (grid[i][j] == 0) { //Red will signal a block
                    rectangle.setFill(Color.RED);
//                    rectangle.setStroke(Color.BLACK);
//                    rectangle.setStrokeWidth(0.5);
                    gridPane.add(rectangle, j, i);
                } else if (grid[i][j] == 2) { //2 if for clean
                    gridPane.add(rectangle, j, i);
                    rectangle.setFill(Color.WHITE);
                } else if (grid[i][j] == 3) { // 3 is the vacuum cleaner
                    gridPane.add(rectangle, j, i);
                    rectangle.setFill(Color.ORANGE);
                } else if (grid[i][j] == 4) {
                    gridPane.add(rectangle, j, i);
                    rectangle.setFill(Color.YELLOW); // Yellow if for the dirt producer
                } else {
                    gridPane.add(rectangle, j, i);
                    rectangle.setFill(Color.BLUE);
                }
            }
    }
}
