import ee.ut.dendroloj.Dendrologist;

public class GraafKatsed {

    public static void main(String[] args) {
        int[][] M = {
                {0, 11, 1, 12, -1},
                {4, 0, 2, -1, 6},
                {-1, 2, 0, 3, -1},
                {12, -1, -1, 0, -1},
                {-1, -1, 5, 4, 0}
        };
        Dendrologist.drawGraph(M, String::valueOf);
    }

}
