public class NTipp {
    public String info;
    public NTipp[] alluvad;

    public NTipp(String info) {
        this.info = info;
    }

    public NTipp(String info, NTipp[] alluvad) {
        this.info = info;
        this.alluvad = alluvad;
    }
}
