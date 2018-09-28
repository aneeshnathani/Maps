import java.util.ArrayList;

public class Node {
    private String id;
    private double lat;
    private double lon;
    private double priority;
    private ArrayList<Node> edgeTo;
    private Node cameFrom;

    public Node(String idd, double latt, double lonn) {
        id = idd;
        lat = latt;
        lon = lonn;
        edgeTo = new ArrayList<>();
        priority = Double.POSITIVE_INFINITY;
    }

    public ArrayList<Node> getEdgeTo() {
        return edgeTo;
    }

    public String getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getPriority() {
        return priority;
    }

    public void changePriority(double a) {
        priority = a;
    }

    public Node getCameFrom() {
        return cameFrom;
    }

    public void changeCameFrom(Node a) {
        cameFrom = a;
    }
}
