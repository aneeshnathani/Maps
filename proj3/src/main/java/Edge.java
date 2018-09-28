import java.util.ArrayList;

public class Edge {
    boolean valid;
    private String wayId;
    private ArrayList<Node> connects = new ArrayList<>();

    public Edge(String idd) {
        wayId = idd;
        valid = false;
    }

    public void setValid(Boolean p) {
        valid = p;
    }

    public ArrayList<Node> getConnects() {
        return connects;
    }

    public void setConnects(Node r) {
        connects.add(r);
    }
}
