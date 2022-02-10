import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Random;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setDisable(true);

        GridPane legends = new GridPane();
        legends.setHgap(5);
        legends.setVgap(5);
        legends.setDisable(true);
        Rectangle rect_Yellow = new Rectangle(20, 20);
        Rectangle rect_Black = new Rectangle(20, 20);
        Rectangle rect_Red = new Rectangle(20, 20);
        Rectangle rect_Green = new Rectangle(20, 20);
        rect_Yellow.setFill(Color.YELLOW);
        legends.add(rect_Yellow,0, 0 );
        legends.add(new Label("Pacman"), 1, 0);
        rect_Black.setFill(Color.BLACK);
        legends.add(rect_Black,0, 1 );
        legends.add(new Label("Dirt"), 1, 1);
        rect_Red.setFill(Color.RED);
        legends.add(rect_Red,0, 2 );
        legends.add(new Label("Block"), 1, 2);
        rect_Green.setFill(Color.rgb(0, 230, 0));
        legends.add(rect_Green,0, 3 );
        legends.add(new Label("Visited"), 1, 3);

        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(legends, gridPane);

        SubScene subSceneTwo = new SubScene(hBox, 1000, 800);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 2000, 1000);

        // I want it to find the closest unvisited dirty node in the matrix
        Label row_label = new Label("Rows: ");
        TextField x1 = new TextField("10");
        Label column_label = new Label("Columns: ");
        TextField y1 = new TextField("10");
        TextField probDirty = new TextField("10");
        TextField probBlock = new TextField("10");
        TextField probClean = new TextField("80");

        x1.setPrefWidth(60);
        y1.setPrefWidth(60);
        probDirty.setPrefWidth(60);
        probBlock.setPrefWidth(60);
        probClean.setPrefWidth(60);

        ComboBox<String> mode = new ComboBox<>();
        mode.getItems().addAll("User", "AutoGenerate");

        ComboBox<String> tileStatus = new ComboBox<>();
        tileStatus.getItems().addAll("Dirt", "Block");

        //combo box to choose which algorithm to run
        ComboBox<String> comboBox = new ComboBox();
        comboBox.getItems().addAll("DFS_Unknown", "DFS_BFS_Unknown","BFS_Unknown", "CDT", "Spiral");
        comboBox.setDisable(true);

        ComboBox<String> speeds = new ComboBox<>();
        speeds.getItems().addAll("Slow", "Medium", "Fast");
        speeds.setDisable(true);

        Button start = new Button("Start");
        start.setDisable(true);

        Button submit = new Button("Submit");

        Button reset = new Button("Reset");

        Button stop = new Button("Exit");
        Button confirm = new Button("Confirm");
        confirm.setDisable(true);

        HBox hbox = new HBox(15);
        hbox.getChildren().addAll(mode);
        hbox.setAlignment(Pos.CENTER);
        SubScene subSceneOne = new SubScene(hbox, 2000, 50);

        root.getChildren().addAll(subSceneOne, subSceneTwo);

        mode.setOnAction(event -> {
            hbox.getChildren().clear();
           if(mode.getSelectionModel().getSelectedIndex() == 0)
             hbox.getChildren().addAll(tileStatus,  mode, row_label,x1, column_label, y1,
                      speeds, comboBox, submit, start, reset, confirm);
           else
               hbox.getChildren().addAll(mode, row_label,x1, column_label, y1,
                       new Label("%dirty"), probDirty, new Label("%block"),
                       probBlock, speeds, comboBox, submit, start, reset, confirm);
        });

        submit.setOnAction(event -> {
            if (x1.getText() != null && y1.getText()!= null){
                confirm.setDisable(false);
                int row = Integer.valueOf(x1.getText());
                int column = Integer.valueOf(y1.getText());
                int max = Math.max(row, column);
                int dimensions = (int) (800 - max * 5) / (max); //round to the lower integer
                System.out.println("Image dimensions: " + dimensions);
                int[][] grid = new int[row][column];

                int modeSelected = mode.getSelectionModel().getSelectedIndex();
                if(modeSelected == 1){
                    int p1 = Integer.parseInt(probDirty.getText());
                    int p0 = Integer.parseInt(probBlock.getText());

                    Random rnd = new Random();
                    for (int i = 0; i < row; i++)
                        for (int j = 0; j < column; j++) {
                            int x = rnd.nextInt(100) + 1;
                            if (x <= p1)
                                grid[i][j] = 1;
                            else if (x <= (p1 + p0) && x >= p1)
                                grid[i][j] = 0;
                            else if(x >= (p1+p0))
                                grid[i][j] = 2;
                        }

                    for (int i = 0; i < grid.length; i++)
                        for (int j = 0; j < grid[0].length; j++) {
                            Rectangle rectangle = new Rectangle(dimensions, dimensions);
                            if (grid[i][j] == 1) {
                                rectangle.setFill(Color.BLACK);
                                gridPane.add(rectangle, j, i);
                            } else if (grid[i][j] == 0) { //Red will signal a block
                                rectangle.setFill(Color.RED);
                                gridPane.add(rectangle, j, i);
                            } else {
                                gridPane.add(rectangle, j, i);
                                rectangle.setFill(Color.WHITE);
                            }
                        }
                    gridPane.setDisable(false);
                } else{
                    for (int i = 0; i < grid.length; i++)
                        for (int j = 0; j < grid[0].length; j++) {
                            Rectangle rectangle = new Rectangle(dimensions, dimensions);
                            gridPane.add(rectangle, j, i);
                            rectangle.setFill(Color.WHITE);
                            grid[i][j] = 2;
                        }
                    gridPane.setDisable(false);
                    gridPane.setOnMouseClicked(e -> {
                        Node node = (Node) e.getTarget();
                        int i = GridPane.getRowIndex(node); //row
                        int j = GridPane.getColumnIndex(node); //column
                        int tile = tileStatus.getSelectionModel().getSelectedIndex();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Rectangle rectangle = new Rectangle(dimensions, dimensions);
                                if(tile == 0) {
                                    rectangle.setFill(Color.BLACK);
                                    grid[i][j] = 1;
                                }
                                else {
                                    rectangle.setFill(Color.RED);
                                    grid[i][j] = 0;
                                }
                                gridPane.add(rectangle, j, i);
                            }
                        });
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        submit.setDisable(true);
                    });
                    confirm.setDisable(false);
                }
                        confirm.setOnAction(e -> {
                            submit.setDisable(true);
                            LinkedList<int[]> q = spiralPrint(grid.length, grid[0].length, grid);
                            gridPane.setOnMouseClicked(event1 -> {
                                Node node = (Node) event1.getTarget();
                                int initialRow = GridPane.getRowIndex(node);
                                int initialCol = GridPane.getColumnIndex(node);
                                System.out.println("(" + initialRow + "," + initialCol + ")");
                                while(grid[initialRow][initialCol] == 0){
                                }
                                gridPane.setDisable(true);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Rectangle rectangle = new Rectangle(dimensions, dimensions);
                                        rectangle.setFill(Color.YELLOW);
                                        gridPane.add(rectangle, initialCol, initialRow);
                                    }
                                });
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }

                                speeds.setDisable(false);
                                speeds.setOnAction(speedsEvent -> {
                                    int speedsIndex = speeds.getSelectionModel().getSelectedIndex();
                                    int speed;
                                    if(speedsIndex == 0)
                                        speed = 250;
                                    else if(speedsIndex == 1)
                                        speed = 75;
                                    else
                                        speed = 40;

                                    comboBox.setDisable(false);
                                    comboBox.setOnAction(event2 -> {
                                        int selectedIndex = comboBox.getSelectionModel().getSelectedIndex();
                                        System.out.println("Selected Index: " + selectedIndex);
                                        if(selectedIndex == 0){
                                            DFS dfsInstance = new DFS(initialRow, initialCol, grid, gridPane, dimensions, speed);
                                            start.setDisable(false);
                                            start.setOnAction(event_dfs -> {
                                                start.setDisable(true);
                                                submit.setDisable(true); //you can't generate another grid while pacman working
//                                                confirm.setDisable(true);
                                                Thread thread = new Thread(() -> {
                                                    try {
                                                        dfsInstance.dfs();
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });

                                                thread.setDaemon(true);
                                                thread.start();
                                            });
                                        } else if(selectedIndex == 1){
                                            BFS_DFS_Unknown d = new BFS_DFS_Unknown(initialRow, initialCol, grid, gridPane, dimensions, speed);

                                            start.setDisable(false);

                                            start.setOnAction(event_dfs -> {
                                                submit.setDisable(true); //you can't generate another grid while pacman working
                                                start.setDisable(true);
                                                Thread thread = new Thread(() -> {
                                                    try {
                                                        d.dfs();
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });

                                                thread.setDaemon(true);
                                                thread.start();
                                            });
                                        } else if(selectedIndex == 2){
                                            BFS r = new BFS(initialRow, initialCol, grid, gridPane, dimensions, speed);
                                            start.setDisable(false);

                                            start.setOnAction(event_bfs -> {
                                                start.setDisable(true);
                                                submit.setDisable(true); //you can't generate another grid while pacman working
                                                Thread thread = new Thread(() -> {
                                                    try {
                                                        r.bfs();
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });

                                                thread.setDaemon(true);
                                                thread.start();
                                            });
                                        } else if(selectedIndex == 3){
                                            CDT r = new CDT(new int[]{initialRow, initialCol}, grid, gridPane, q, dimensions, speed);
                                            start.setDisable(false);

                                            start.setOnAction(event_tsp -> {
                                                start.setDisable(true);
                                                submit.setDisable(true); //you can't generate another grid while pacman working
                                                Thread thread = new Thread(() -> {
                                                    try {
                                                        r.shortestPath();
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });

                                                thread.setDaemon(true);
                                                thread.start();
                                            });
                                        } else {
                                            Spiral spiral = new Spiral(new int[]{initialRow, initialCol}, grid, gridPane, q, dimensions, speed);
                                            start.setDisable(false);

                                            start.setOnAction(event_spiral -> {
                                                start.setDisable(true);
                                                submit.setDisable(true); //you can't generate another grid while pacman working
                                                Thread thread = new Thread(() -> {
                                                    try {
                                                        spiral.shortestPath();
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException exception) {
                                                        exception.printStackTrace();
                                                    }
                                                });

                                                thread.setDaemon(true);
                                                thread.start();
                                            });

                                        }
                                    });
                                });
                            });
                        });

            }
        });

        reset.setOnAction(event -> {
            gridPane.getChildren().clear();
            x1.setText("10");
            y1.setText("10");
            probDirty.setText("10");
            probBlock.setText("10");
            probClean.setText("80");
            submit.setDisable(false);
            comboBox.setDisable(true);
            speeds.setDisable(true);
            start.setDisable(true);
        });

        stop.setOnAction(event -> {
            Platform.exit();
        });

        primaryStage.setTitle("Vacuum Cleaner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static LinkedList<int[]> spiralPrint(int m, int n, int[][] a) {
        int i, k = 0, l = 0;
        LinkedList<int[]> queue = new LinkedList<>();

        /*  k - starting row index
        m - ending row index
        l - starting column index
        n - ending column index
        i - iterator
        */

        while (k < m && l < n) {
            // Print the first row from the remaining rows
            for (i = l; i < n; ++i) {

                if(a[k][i] == 1) {
                    queue.add(new int[]{k, i});
//                    System.out.print("(" + k + ","+ i + ")");
                }
            }
            k++;

            // Print the last column from the remaining
            // columns
            for (i = k; i < m; ++i) {

                if(a[i][(n-1)] == 1) {
                    queue.add(new int[]{i, (n - 1)});
//                    System.out.print("(" + i +"," + (n - 1) + ")");
                }
            }
            n--;

            // Print the last row from the remaining rows */
            if (k < m) {
                for (i = n - 1; i >= l; --i) {

                    if(a[(m-1)][i] == 1) {
//                        System.out.print("(" + (m-1) + "," + i + ")");
                        queue.add(new int[]{(m - 1), i});
                    }
                }
                m--;
            }

            // Print the first column from the remaining
            // columns */
            if (l < n) {
                for (i = m - 1; i >= k; --i) {

                    if(a[i][l] == 1) {
//                        System.out.print("(" + i + "," + l + ")");
                        queue.add(new int[]{i, l});
                    }
                }
                l++;
            }
        }
        return queue;
    }
}
