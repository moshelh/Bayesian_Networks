
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

public class Graph {
    final static int twoToAdd = 2;
    private static ArrayList<Node> graph;
    private static ArrayList<String[]> text ;
    Graph(ArrayList<String[]> _text){

        graph=new ArrayList<>();
        text=_text;
    }
    private static void creatGraph(ArrayList<String[]> text){
        String[] temp = text.get(0)[1].split(":");
        String[] temp1 =temp[1].split(",");
        for (int i = 0; i <temp1.length ; i++) {
            if(temp1[i].contains(" ")) {
                temp1[i] = temp1[i].replaceAll(" ", "");
            }
            Node cur=new Node(temp1[i]);
            graph.add(cur);
        }
        for (int i = 0; i < text.size(); i++) {
            if(text.get(i)[0].contains("Var")){
                // set the values
                Node cur = returnNode(text.get(i)[0].charAt(4)+"");
                String [] spal = text.get(i)[1].split(":");
                spal[1]=spal[1].replaceAll(" ","");
                String [] val = spal[1].split(",");
                cur.values.addAll(Arrays.asList(val));
                // set the parents
                String[] temString1=text.get(i)[0].split(" ");
                String[] temString=text.get(i)[2].split(":");
                if(!(temString[1].contains("none"))){
                    if(temString[1].contains(",")){
                        String[] strTemp=temString[1].split(",");
                        for (int j = 0; j < strTemp.length; j++) {
                            strTemp[j]=strTemp[j].replaceAll(" ","");
                            addParent(temString1[1],strTemp[j]);
                            addChild(temString1[1],strTemp[j]);
                        }
                    }else {
                        temString[1]=temString[1].replaceAll(" ","");
                        addParent(temString1[1],temString[1]);
                        addChild(temString1[1],temString[1]);
                    }
                    cur.cpt.head=new String[cur.parents.size()+twoToAdd];
                }else{
                    cur.cpt.head=new String[twoToAdd];
                }
                // **************set the CPT tables******************8
                // if node didnt have parents
                if (cur.parents.size()==0){
                    String[] sub = Arrays.copyOfRange(text.get(i), 3+1,  text.get(i).length);
                    String[] value=sub[0].split(",");
                    BigDecimal sum=new BigDecimal("0");
                    ArrayList<String> tempor = new ArrayList<>();
                    for (int j = 0; j < value.length; j=j+2) {
                        value[j]=value[j].replace("=","");
                        tempor.add(value[j]);
                        String[] newV = Arrays.copyOfRange(value,j,j+2);
                        BigDecimal sumTemp=new BigDecimal(value[j+1]);
                        sum=sum.add(sumTemp);
                        cur.cpt.cpt.add(newV);
                    }
                    ArrayList<String> tempor1 = new ArrayList<>();
                    tempor1.addAll(cur.values);
                    tempor1.removeAll(tempor);
                    sum=sum.multiply(new BigDecimal("-1"));
                    BigDecimal one=new BigDecimal("1");
                    sum=one.add(sum);
                    String[] newV =new String[2];
                    newV[0]=tempor1.get(0);
                    newV[1]=sum.toString();
                    cur.cpt.cpt.add(newV);

                }else {
                    String[] sub = Arrays.copyOfRange(text.get(i), 3+1, text.get(i).length);
                    for (String s:sub) {
                       String [] value = new String[cur.parents.size()+2];
                       int num = s.indexOf("=");
                       String tempo = s.substring(0,num-1);
                       String [] save = tempo.split(",");
                       String tempo1 = s.substring(num,s.length());
                       String [] saveTo = tempo1.split(",");
                       BigDecimal sum=new BigDecimal("0");
                       ArrayList<String> saveValues = new ArrayList<>();
                        for (int j = 0; j < saveTo.length; j+=2) {
                            for (int k = 0; k <save.length ; k++) {
                               value[k]=save[k];
                            }
                            saveTo[j]=saveTo[j].replace("=","");
                            String[] newV = Arrays.copyOfRange(saveTo,j,j+2);
                            BigDecimal tempNum=new BigDecimal(saveTo[j+1]);
                            sum=sum.add(tempNum);
                            saveValues.add(saveTo[j]);
                            for (int k = save.length; k <value.length ; k++) {
                                 value[k]=newV[k-save.length];
                            }
                            System.out.println(Arrays.toString(value));
                            cur.cpt.cpt.add(value);
                        }
                        ArrayList<String> copyOfValuesNode= new ArrayList<>();
                        copyOfValuesNode.addAll(cur.values);
                        copyOfValuesNode.removeAll(saveValues);
                        sum=sum.multiply(new BigDecimal("-1"));
                        BigDecimal one=new BigDecimal("1");
                        String [] newV= new String[cur.parents.size()+2];
                        for (int j = 0; j <save.length ; j++) {
                            newV[j]=save[j];
                        }
                        newV[save.length] = copyOfValuesNode.get(0);
                        newV[save.length+1]=one.add(sum).toString();
                        System.out.println(Arrays.toString(newV));
                        cur.cpt.cpt.add(newV);
                    }

                }
            }

        }
    }

    private static void addParent(String s, String s1) {
        Node cur= returnNode(s);
        Node par=returnNode(s1);
        cur.parents.add(par);
    }

    private static void addChild(String child, String parent) {
        Node c = returnNode(child);
        Node p=returnNode(parent);
        p.child.add(c);
    }

    private static Node returnNode(String node){
        Node cur=graph.get(0);
        for (int i = 0; i < graph.size(); i++) {
            if (graph.get(i).name.equals(node)) {
                return graph.get(i);
            }
        }
        return cur;
    }

    public static void StartBayes(){
        String start;
        String end;
        ArrayList<String> shade=new ArrayList<>();
        for (int i =0; i<text.size();i++)
        {
            if(text.get(i)[0].contains("Queries")){
                for (int j = 1; j < text.get(i).length; j++) {
                    if(!(text.get(i)[j].charAt(0)=='P')){
                        String[] q =text.get(i)[j].split(Pattern.quote("|"));
                        String[] stratStop=q[0].split("-");
                        start=stratStop[0];
                        end=stratStop[1];
                        if(q.length>1) {
                            if(q[1].contains(",")){
                                String[] splitBypsik=q[1].split(",");
                                for (String s:splitBypsik) {
                                    int index=s.indexOf("=");
                                    shade.add(s.substring(0,index));
                                }
                            }else {
                                int index=q[1].indexOf("=");
                                shade.add(q[1].substring(0,index));
                            }
                        }
                        System.out.println(bayesBall(start,end,shade));
                    }
                }
            }
        }

    }

    public static String bayesBall (String start , String end,ArrayList<String> shade){
        for (Node v:graph) {
            v.isVisit=false;
            v.shade=false;
            v.fromChild=false;
            v.fromParent=false;
        }
        Node Nstart = returnNode(start);
        Queue<Node> q = new LinkedList<>();
        // set all shade node in the grape to true
        if(!shade.isEmpty()) {
            for (String no : shade) {
                returnNode(no).shade = true;
            }
        }
        //init queue
        for (Node child:Nstart.child) {
            child.fromParent=true;
            q.add(child);
        }
        for (Node p:Nstart.parents) {
            p.fromChild=true;
            q.add(p);
        }
        while (!q.isEmpty()) {
            Node cur = q.poll();
            if(cur.name.equals(end)) return "no";
            if(!cur.isVisit){
                //option 1: Node came from child and didn't evidence
                if(cur.fromChild&&!(cur.shade)){
                    for (Node child:cur.child) {
                        child.fromChild=false;
                        child.fromParent=true;
                        q.add(child);
                    }
                    for (Node p:cur.parents) {
                        p.fromParent=false;
                        p.fromChild=true;
                        q.add(p);
                    }
                    cur.isVisit=true;
                }
                //option 2: Node came from parent and didn't evidence
                else if(cur.fromParent&&!(cur.shade)){
                    for (Node child:cur.child) {
                        child.fromChild=false;
                        child.fromParent=true;
                        child.isVisit=false;
                        q.add(child);
                    }
                    cur.isVisit=true;
                }
                //option 3: Node came from parent and evidence
                else if(cur.fromParent){
                    for (Node p:cur.parents) {
                        p.fromParent=false;
                        p.fromChild=true;
                        p.isVisit=false;
                        q.add(p);
                    }
                    cur.isVisit=true;
                }

            }
        }


        return "yes";
    }

    public static void main(String[] args) throws IOException {
        Graph g=new Graph(readFile.split(readFile.read(("input2.txt"))));
        creatGraph(readFile.split(readFile.read(("input2.txt"))));
        StartBayes();
    }
}
