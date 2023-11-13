import ee.ut.dendroloj.Dendrologist;

import java.util.*;

public class TippKatsed {

    static Tipp juhuslikPuu(int n, Random juhus) {
        if (n == 0) return null;
        int vasakule = juhus.nextInt(n);
        return new Tipp("", juhuslikPuu(vasakule, juhus), juhuslikPuu(n - 1 - vasakule, juhus));
    }

    static int nummerdaKeskjärjestuses(Tipp tipp, int i) {
        if (tipp == null) return i;
        i = nummerdaKeskjärjestuses(tipp.v, i);
        tipp.info = Integer.toString(i);
        i += 1;
        return nummerdaKeskjärjestuses(tipp.p, i);
    }

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);

        Tipp tipp = juhuslikPuu(20, new Random(42));
        nummerdaKeskjärjestuses(tipp, 1);

        Dendrologist.drawBinaryTree(tipp, t -> t.info, t -> t.v, t -> t.p);
        // Dendrologist.drawBinaryTree(tipp, t -> t.info + " x=" + t.x, t -> t.v, t -> t.p);
    }

}
