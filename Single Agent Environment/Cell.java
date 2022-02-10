public class Cell {
    int x, y, dist;
    Cell prev; //parent cell in the path

    public Cell(int x, int y, int dist, Cell prev) {
        this.x = x;
        this.y = y;
        this.dist = dist;
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "(" +
                 x +
                "," + y +
                ')';
    }
}
