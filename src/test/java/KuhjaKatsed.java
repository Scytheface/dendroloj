import ee.ut.dendroloj.Dendrologist;

import java.util.List;

public class KuhjaKatsed {

    private static int vasakAlluv(List<Integer> kuhi, int i) {
        int v = 2 * i + 1;
        return v < kuhi.size() ? v : -1;
    }

    private static int paremAlluv(List<Integer> kuhi, int i) {
        int p = 2 * i + 2;
        return p < kuhi.size() ? p : -1;
    }

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);

        List<Integer> kuhi = List.of(10, 6, 7, 3, 5, 2, 3, 9, 4, 12, 5, -7, 5, 3, 6, 6, 5, 16);
        Dendrologist.drawBinaryTree(
                0,
                i -> String.valueOf(kuhi.get(i)),
                i -> vasakAlluv(kuhi, i) == -1 ? null : vasakAlluv(kuhi, i),
                i -> paremAlluv(kuhi, i) == -1 ? null : paremAlluv(kuhi, i)
        );

    }

}
