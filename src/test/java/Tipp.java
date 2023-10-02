public class Tipp {
    public String info;
    public Tipp v;
    public Tipp p;
    public int x = 0;

    public Tipp(String info) {
        this.info = info;
    }

    public Tipp(String info, Tipp v, Tipp p) {
        this.info = info;
        this.v = v;
        this.p = p;
    }
}
