import java.util.ArrayList;

public class Node  {
    String name;
    ArrayList<Node> child ;
    ArrayList<Node> parents ;
    boolean shade;
    boolean isVisit ;
    boolean fromParent;
    boolean fromChild;
    ArrayList<String> values;
    CPT cpt;

    Node(String _name ){
        name=_name;
        parents=new ArrayList<>();
        child=new ArrayList<>();
        isVisit=false;
        fromParent=false;
        fromChild=false;
        values=new ArrayList<>();
        cpt=new CPT();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
