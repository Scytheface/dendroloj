import ee.ut.dendroloj.Dendrologist;

import java.util.Arrays;
import java.util.Random;

public class NTippKatsed {

    static NTipp juhuslikPuu(int h, Random juhus) {
        if (h == 0) return null;
        int alluvaid = 2 + juhus.nextInt(4);
        NTipp[] alluvad = new NTipp[alluvaid];
        for (int i = 0; i < alluvaid; i++) {
            if (juhus.nextBoolean())
                alluvad[i] = juhuslikPuu(h - 1, juhus);
        }
        return new NTipp("", alluvad);
    }

    static int nummerdaEesjärjestuses(NTipp tipp, int i) {
        if (tipp == null) return i;
        tipp.info = Integer.toString(i);
        i += 1;
        for (NTipp alluv : tipp.alluvad) {
            i = nummerdaEesjärjestuses(alluv, i);
        }
        return i;
    }

    public static void main(String[] args) {
        NTipp tipp = juhuslikPuu(5, new Random(42));
        nummerdaEesjärjestuses(tipp, 1);

        Dendrologist.drawTree(tipp, t -> t.info, t -> Arrays.asList(t.alluvad));
    }

}
