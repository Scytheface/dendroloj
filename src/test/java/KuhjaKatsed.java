import ee.ut.dendroloj.Dendrologist;

import java.util.List;

public class KuhjaKatsed {

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);

        List<Integer> kuhi = List.of(10, 6, 7, 3, 5, 2, 3, 9, 4, 12, 5, -7, 5, 3, 6, 6, 5, 16);
        Dendrologist.drawBinaryTree(
                0,
                i -> String.valueOf(kuhi.get(i)),
                i -> 2 * i + 1 < kuhi.size() ? 2 * i + 1 : null,
                i -> 2 * i + 2 < kuhi.size() ? 2 * i + 2 : null
        );

    }

}
