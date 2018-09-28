import java.util.Comparator;

public class TellPriority implements Comparator<Node> {
    @Override
    public int compare(Node one, Node two) {
        if (one.getPriority() > two.getPriority()) {
            return 1;
        } else if (one.getPriority() < two.getPriority()) {
            return -1;
        } else {
            return 0;
        }
    }
}
