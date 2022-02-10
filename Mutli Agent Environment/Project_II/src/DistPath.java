import java.util.LinkedList;

public class DistPath {
    LinkedList<Cell> path;
    int distance;
    public DistPath(LinkedList<Cell> path, int distance){
        this.distance = distance;
        this.path = path;
    }
}
