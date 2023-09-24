import ee.ut.dendroloj.Dendrologist;
import ee.ut.dendroloj.Grow;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Katsed {
    @Grow
    static int unaarne(int x, String y) {
        if (x == 0) return 1;
        return 1 + unaarne(x - 1, y);
    }

    private static final Set<Integer> seenValues = new HashSet<>();

    @Grow
    static int fib(int n) {
        if (seenValues.contains(n)) {
            //Dendrologist.colorCurrentCall(Color.MAGENTA);
        }
        seenValues.add(n);

        if (n < 2) return n;
        return fib(n - 2) + fib(n - 1);
    }

    @Grow
    static int fib3(int n) {
        if (n < 2) return n;
        return fib3(n - 3) + fib3(n - 2) + fib3(n - 1);
    }

    @Grow
    static void tree(int w, int h) {
        if (h <= 1) {
            return;
        }
        for (int i = 0; i < w; i++) {
            tree(w, h - 1);
        }
    }

    @Grow
    static void unbalancedTree(int w, int h) {
        if (h <= 1) {
            return;
        }
        for (int i = 0; i < w; i++) {
            unbalancedTree(i, h - 1);
        }
    }

    @Grow
    static int[] pööraJupid(int[] massiiv) {
        if (Math.random() < 0.2) {
            throw new RuntimeException("Random failure");
        }
        if (massiiv.length < 2) {
            return massiiv;
        } else if (massiiv.length == 2) {
            return new int[]{massiiv[1], massiiv[0]};
        } else {
            int[] väljund = new int[massiiv.length];
            int[] väljundA = pööraJupid(Arrays.copyOfRange(massiiv, 0, massiiv.length / 2));
            System.arraycopy(väljundA, 0, väljund, 0, väljundA.length);
            int[] väljundB = pööraJupid(Arrays.copyOfRange(massiiv, massiiv.length / 2, massiiv.length));
            System.arraycopy(väljundB, 0, väljund, väljundA.length, väljundB.length);
            return väljund;
        }
    }

    static int[] summad(int[] hinnad) {
        return summad(hinnad, 0, 0);
    }

    @Grow
    static int[] summad(int[] hinnad, int i, int summa) {
        if (i == hinnad.length) {
            return new int[]{summa};
        }
        return ühenda(summad(hinnad, i + 1, summa), summad(hinnad, i + 1, summa + hinnad[i]));
    }

    static int[] ühenda(int[] a, int[] b) {
        int[] koos = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, koos, a.length, b.length);
        return koos;
    }

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);
        Dendrologist.setShowMethodNames(false);

        Dendrologist.wakeUp();

        // unbalancedTree(6, 3);
        // tree(5, 3);
        // fib3(6);

        // fib(16);
        // fib(8);
        // pööraJupid(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});

        summad(new Random().ints(5, 1, 100).toArray());
    }
}
