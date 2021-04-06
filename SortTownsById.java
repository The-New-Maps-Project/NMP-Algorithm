import java.util.Comparator;

public class SortTownsById implements Comparator<Town>{
    public int compare(Town a, Town b){
        return a.getId()-b.getId();
    }
}
