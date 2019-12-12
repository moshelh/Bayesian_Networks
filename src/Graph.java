
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Graph {
    final static int twoToAdd = 2;
    private static ArrayList<Node> graph;
    private static ArrayList<String[]> text ;
    private static int plusOperation;
    private static int multOperation;
    Graph(ArrayList<String[]> _text){

        graph=new ArrayList<>();
        text=_text;
        plusOperation=0;
        multOperation=0;
    }
    public static void creatGraph(){
        String[] temp = text.get(0)[1].split(":");
        String[] temp1 =temp[1].split(",");
        for (int i = 0; i <temp1.length ; i++) {
            if(temp1[i].contains(" ")) {
                temp1[i] = temp1[i].replaceAll(" ", "");
            }
            Node cur=new Node(temp1[i]);
            graph.add(cur);
        }
        for (String[] strings : text) {
            if (strings[0].contains("Var")) {
                // set the values
                Node cur = returnNode(strings[0].charAt(4) + "");
                String[] spal = strings[1].split(":");
                spal[1] = spal[1].replaceAll(" ", "");
                String[] val = spal[1].split(",");
                cur.values.addAll(Arrays.asList(val));
                // set the parents
                String[] temString1 = strings[0].split(" ");
                String[] temString = strings[2].split(":");
                if (!(temString[1].contains("none"))) {
                    if (temString[1].contains(",")) {
                        String[] strTemp = temString[1].split(",");
                        for (int j = 0; j < strTemp.length; j++) {
                            strTemp[j] = strTemp[j].replaceAll(" ", "");
                            addParent(temString1[1], strTemp[j]);
                            addChild(temString1[1], strTemp[j]);
                        }
                    } else {
                        temString[1] = temString[1].replaceAll(" ", "");
                        addParent(temString1[1], temString[1]);
                        addChild(temString1[1], temString[1]);
                    }
                    cur.cpt.head = new String[cur.parents.size() + twoToAdd];
                    for (int j = 0; j < cur.parents.size(); j++) {
                        cur.cpt.head[j] = cur.parents.get(j).name;
                    }
                    cur.cpt.head[cur.parents.size()] = cur.name;
                    cur.cpt.head[cur.parents.size() + 1] = "p";
                } else {
                    cur.cpt.head = new String[twoToAdd];
                    cur.cpt.head[0] = cur.name;
                    cur.cpt.head[1] = "p";
                }
                // **************set the CPT tables******************8
                // if node didnt have parents
                if (cur.parents.size() == 0) {
                    String[] sub = Arrays.copyOfRange(strings, 3 + 1, strings.length);
                    String[] value = sub[0].split(",");
                    BigDecimal sum = new BigDecimal("0");
                    ArrayList<String> tempor = new ArrayList<>();
                    for (int j = 0; j < value.length; j = j + 2) {
                        value[j] = value[j].replace("=", "");
                        tempor.add(value[j]);
                        String[] newV = Arrays.copyOfRange(value, j, j + 2);
                        BigDecimal sumTemp = new BigDecimal(value[j + 1]);
                        sum = sum.add(sumTemp);
                        cur.cpt.cpt.add(newV);
                    }
                    ArrayList<String> tempor1 = new ArrayList<>(cur.values);
                    tempor1.removeAll(tempor);
                    sum = sum.multiply(new BigDecimal("-1"));
                    BigDecimal one = new BigDecimal("1");
                    sum = one.add(sum);
                    String[] newV = new String[2];
                    newV[0] = tempor1.get(0);
                    newV[1] = sum.toString();
                    cur.cpt.cpt.add(newV);

                }
                //if the node have parents
                else {
                    String[] sub = Arrays.copyOfRange(strings, 3 + 1, strings.length);
                    for (String s : sub) {
                        String[] value = new String[cur.parents.size() + 2];
                        int num = s.indexOf("=");
                        String tempo = s.substring(0, num - 1);
                        String[] save = tempo.split(",");
                        String tempo1 = s.substring(num);
                        String[] saveTo = tempo1.split(",");
                        BigDecimal sum = new BigDecimal("0");
                        ArrayList<String> saveValues = new ArrayList<>();
                        for (int j = 0; j < saveTo.length; j += 2) {
                            System.arraycopy(save, 0, value, 0, save.length);
                            saveTo[j] = saveTo[j].replace("=", "");
                            String[] newV = Arrays.copyOfRange(saveTo, j, j + 2);
                            BigDecimal tempNum = new BigDecimal(saveTo[j + 1]);
                            sum = sum.add(tempNum);
                            saveValues.add(saveTo[j]);
                            if (value.length - save.length >= 0)
                                System.arraycopy(newV, 0, value, save.length, value.length - save.length);
                            cur.cpt.cpt.add(value);
                            value = new String[cur.parents.size() + 2];
                        }
                        ArrayList<String> copyOfValuesNode = new ArrayList<>(cur.values);
                        copyOfValuesNode.removeAll(saveValues);
                        sum = sum.multiply(new BigDecimal("-1"));
                        BigDecimal one = new BigDecimal("1");
                        String[] newV = new String[cur.parents.size() + 2];
                        System.arraycopy(save, 0, newV, 0, save.length);
                        newV[save.length] = copyOfValuesNode.get(0);
                        newV[save.length + 1] = one.add(sum).toString();
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
        for (Node value : graph) {
            if (value.name.equals(node)) {
                return value;
            }
        }
        return cur;
    }

    public static String startBayes(String row){
        String string;
        String start;
        String end;
        ArrayList<String> shade=new ArrayList<>();
        String[] q =row.split(Pattern.quote("|"));
        String[] startStop=q[0].split("-");
        start=startStop[0];
        end=startStop[1];
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
        string=bayesBall(start,end,shade);
        string=string+"\n";
        return string;
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

    public static String startVariableElimination(String row){
        String string;
        ArrayList<String[]> evidences = new ArrayList<>();
        ArrayList<String> hiddenVariables = new ArrayList<>();
        int tempIndex=row.indexOf("(");
        String newS = row.substring(tempIndex+1);
        tempIndex=newS.indexOf(")");
        String s = newS.substring(tempIndex+2);
        String [] queries;
        if (s.length()>0) {
            newS = newS.substring(0, tempIndex);
            String[] strings = s.split("-");
            hiddenVariables.addAll(Arrays.asList(strings));
            tempIndex =newS.indexOf("|");
            String quer =newS.substring(0,tempIndex);
            queries = quer.split("=");
            newS=newS.substring(tempIndex+1);
            strings =newS.split(",");
            for (String evi:strings) {
                String[] evidence = evi.split("=");
                evidences.add(evidence);
            }
        }else {
            newS = newS.substring(0, tempIndex);
            tempIndex =newS.indexOf("|");
            String quer =newS.substring(0,tempIndex);
            queries = quer.split("=");
            newS=newS.substring(tempIndex+1);
            String[] strings = newS.split(",");
            for (String evi:strings) {
                String[] evidence = evi.split("=");
                evidences.add(evidence);
            }
        }
        multOperation=0;
        plusOperation=0;
        string=variableElimination(queries,evidences,hiddenVariables);
        string=string+"\n";
        return string;
    }

    public static String variableElimination(String[] queries,ArrayList<String[]> evidence,ArrayList<String> hiddenVariables){
        String ans;
        ArrayList<CPT> copyCpt=getCPT(queries,evidence,hiddenVariables);
        ans = checkIfDirectAnswer(queries,evidence,copyCpt);
        if (ans.isEmpty()) {
            //remove not evidence form CPT
            cleanCpt(copyCpt, evidence);
            for (String hiddenVariable : hiddenVariables) {
                PriorityQueue<CPT> q = new PriorityQueue<>();
                for (int j = 0; j < copyCpt.size(); j++) {
                    if (copyCpt.get(j).containsVar(hiddenVariable)) {
                        q.add(copyCpt.get(j));
                        copyCpt.remove(j);
                        j--;
                    }
                }
                while (q.size() > 1) {
                    // join between two smallest table
                    CPT one = q.poll();
                    CPT two = q.poll();
                    CPT joinBoneAndTwo = join(one, two, hiddenVariable);
                    q.add(joinBoneAndTwo);
                }
                // eliminate the last table by hidden
                CPT last = q.poll();
                assert last != null;
                CPT finalCpt = eliminate(last, hiddenVariable);
                // add to copyCpt the new table
                copyCpt.add(finalCpt);
            }
            PriorityQueue<CPT> cptPriorityQueue = new PriorityQueue<>(copyCpt);
            // join and eliminate the queries var
            while (cptPriorityQueue.size()>1){
                CPT cpt = cptPriorityQueue.poll();
                CPT cpt1 = cptPriorityQueue.poll();
                CPT cpt2 = join(cpt,cpt1,queries[0]);
                cptPriorityQueue.add(cpt2);
            }
            CPT cpt = cptPriorityQueue.poll();
            BigDecimal bigDecimal =BigDecimal.valueOf(0);
            assert cpt != null;
            int indexP = cpt.findIndexOfVar("p");
            bigDecimal=bigDecimal.add(new BigDecimal(cpt.cpt.get(0)[indexP]));
            BigDecimal answer =BigDecimal.valueOf(0);
            int finalIndex = cpt.findIndexOfVar(queries[0]);
            for (int i = 0; i <cpt.cpt.size() ; i++) {
                if(cpt.cpt.get(i)[finalIndex].equals(queries[1])){
                    answer=answer.add(new BigDecimal(cpt.cpt.get(i)[indexP]));
                }
            }
            for (int i = 1; i < cpt.cpt.size(); i++) {
                bigDecimal=bigDecimal.add(new BigDecimal(cpt.cpt.get(i)[indexP]));
                plusOperation++;
            }
            // ans from the last table
            MathContext mc = new MathContext(5, RoundingMode.HALF_UP);
            answer = answer.divide(bigDecimal,mc).setScale(5,RoundingMode.HALF_UP);
            ans = answer.toString()+","+plusOperation+","+multOperation;
        }
        return ans;
    }

    private static String checkIfDirectAnswer(String[] queries, ArrayList<String[]> evidence, ArrayList<CPT> copyCpt) {
        String ans ="";
        int index;
        for (CPT cpt:copyCpt) {
            if (cpt.head[cpt.head.length-2].equals(queries[0])){
                for (String [] strings:cpt.cpt) {
                    boolean thisRow = true;
                    for (String[] value : evidence) {
                        if (cpt.containsVar(value[0])) {
                            index = cpt.findIndexOfVar(value[0]);
                            if (!strings[index].equals(value[1])) {
                                thisRow = false;
                            }
                        } else {
                            thisRow = false;
                        }
                    }
                    if (thisRow){
                        index = cpt.findIndexOfVar(queries[0]);
                        if (strings[index].equals(queries[1])){
                            BigDecimal bigDecimal = new BigDecimal(strings[strings.length-1]).setScale(5,RoundingMode.HALF_UP);
                            return bigDecimal.toString()+","+0+","+0;
                        }
                    }
                }
            }
        }
        return ans;
    }

    private static void cleanCpt(ArrayList<CPT> copyCpt, ArrayList<String[]> evidence) {
        for (String [] tempEvi:evidence) {
            for (CPT cpt:copyCpt) {
                if(cpt.containsVar(tempEvi[0])){
                    int index= cpt.findIndexOfVar(tempEvi[0]);
                    for (int i = 0; i < cpt.cpt.size(); i++) {
                        if (!cpt.cpt.get(i)[index].equals(tempEvi[1])){
                            cpt.cpt.remove(i);
                            i--;
                        }
                    }
                    for (String[] temp:cpt.cpt) {
                        List<String> tempList= new LinkedList<>(Arrays.asList(temp));
                        tempList.remove(index);
                        tempList.toArray(temp);
                    }
                    List<String> tempList= new LinkedList<>(Arrays.asList(cpt.head));
                    tempList.remove(index);
                    cpt.head=Arrays.copyOf(tempList.toArray(cpt.head),tempList.size());
                }
            }

        }
        for (int i = 0; i < copyCpt.size(); i++) {
            if(copyCpt.get(i).head.length==1){
                copyCpt.remove(i);
                i--;
            }
        }

    }

    public static void queries() throws IOException {
        String ans = "";
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("output.txt"), StandardCharsets.UTF_8));

        } catch (IOException ex) {
            // Report
        }
        for (String[] strings : text) {
            if (strings[0].contains("Queries")) {
                for (int j = 1; j < strings.length; j++) {
                    if (strings[j].charAt(0) == 'P') {
                       ans=startVariableElimination(strings[j]);
                        assert writer != null;
                        writer.write(ans);
                    } else {
                       ans=startBayes(strings[j]);
                        assert writer != null;
                        writer.write(ans);
                    }
                }
            }
        }

        try {
            assert writer != null;
            writer.close();} catch (Exception ex) {/*ignore*/}

    }

    private static CPT eliminate(CPT last ,String hidden) {
        CPT temp = new CPT();
        ArrayList<String[]> arrayList = new ArrayList<>();
        temp.head = new String[last.head.length - 1];
        temp.head[temp.head.length - 1] = "p";
        for (int i = 0; i < temp.head.length - 1; i++) {
            temp.head[i] = last.head[i];
            String[] strings = new String[2];
            strings[0] = last.head[i];
            arrayList.add(strings);
        }
        int index = last.findIndexOfVar(hidden);
        ArrayList<Integer> useNumber = new ArrayList<>();
        for (int i = 0; i < last.cpt.size(); i++) {
            if (!useNumber.contains(i)) {
                for (int j = 0; j < arrayList.size(); j++) {
                    arrayList.get(j)[1] = last.cpt.get(i)[j];
                }
                int pIndex = last.findIndexOfVar("p");
                BigDecimal num = new BigDecimal(last.cpt.get(i)[pIndex]);
                boolean rightRow = true;
                for (int j = 1; j < last.cpt.size(); j++) {
                    if (!useNumber.contains(j)&&i!=j) {
                        for (int k = 0; k < last.cpt.get(j).length - 1; k++) {
                            if (k != index) {
                                if (!last.cpt.get(j)[k].equals(arrayList.get(k)[1])) {
                                    rightRow = false;
                                }
                            }
                        }
                        if (rightRow) {
                            useNumber.add(j);
                            BigDecimal num1 = new BigDecimal(last.cpt.get(j)[pIndex]);
                            num=num.add(num1);
                            plusOperation++;
                        }
                        rightRow = true;
                    }
                }
                //create new row
                String[] strings = new String[temp.head.length];
                for (String[] var : arrayList) {
                    int tempIndex = temp.findIndexOfVar(var[0]);
                    strings[tempIndex] = var[1];
                }
                strings[strings.length-1]=num.toString();
                temp.cpt.add(strings);
            }
            useNumber.add(i);
        }
        return temp;
    }


    private static CPT join(CPT one, CPT two,String hidden) {
        CPT temp = new CPT();
        ArrayList<String> commonVar = new ArrayList<>();
        ArrayList<String> diffVar = new ArrayList<>();
        //find the common and different var between Cpt one to Cpt two
        for (int i = 0; i < one.head.length-1; i++) {
            if(two.containsVar(one.head[i])){
                commonVar.add(one.head[i]);
            }else {
                diffVar.add(one.head[i]);
            }
        }
        for (int i = 0; i < two.head.length; i++) {
            if (!one.containsVar(two.head[i])){
                diffVar.add(two.head[i]);
            }
        }
        //create the head to temp cpt
        temp.head = new String[commonVar.size()+diffVar.size()+1];
        temp.head[temp.head.length-1]="p";
        temp.head[temp.head.length-2]=hidden;
        for (int i = 0; i < diffVar.size(); i++) {
            temp.head[i]=diffVar.get(i);
        }
        if(commonVar.size()>1) {
            for (int i = 0; i < commonVar.size(); i++) {
                if (!commonVar.get(i).equals(hidden)) {
                    temp.head[diffVar.size() + i] = commonVar.get(i);
                }
            }
        }
        // find  the index of common var in cpt one and two
        ArrayList<Integer> indexOne = new ArrayList<>();
        for (String s : commonVar) {
            int index = one.findIndexOfVar(s);
            indexOne.add(index);
        }
        ArrayList<Integer> indexTwo = new ArrayList<>();
        for (String s : commonVar) {
            int index = two.findIndexOfVar(s);
            indexTwo.add(index);
        }
        // for each row in cpt one find the row that match in cpt two
        for (String [] stringArrayOne:one.cpt) {
            ArrayList<String []> tempVal = new ArrayList<>();
            for (int num:indexOne) {
                String[] temPair = new String[2];
                temPair[0]= one.head[num];
                temPair[1]=stringArrayOne[num];
                tempVal.add(temPair);
            }
            boolean contains=true; //
            for (String [] stringArrayTwo :two.cpt) {
                for (int i = 0; i < tempVal.size(); i++) {
                    if (!tempVal.get(i)[1].equals(stringArrayTwo[indexTwo.get(i)])) {
                        contains = false;
                        break;
                    }
                }
                // if we find row match
                if(contains){
                    // for each var save the name and the value
                    ArrayList<String[]> arrayList = new ArrayList<>(tempVal);
                    for (int i = 0; i <stringArrayOne.length-1 ; i++) {
                        if (!indexOne.contains(i)&&!one.head[i].equals("p")){
                            String [] strings = new String[2];
                            strings[0] = one.head[i];
                            strings[1]=stringArrayOne[i];
                            arrayList.add(strings);
                        }
                    }
                    for (int i = 0; i < stringArrayTwo.length-1; i++) {
                        if(!indexTwo.contains(i)&&!two.head[i].equals("p")){
                            String [] strings = new String[2];
                            strings[0]=two.head[i];
                            strings[1]=stringArrayTwo[i];
                            arrayList.add(strings);
                        }
                    }
                    // create join row
                    String[] tempRow = new String[commonVar.size()+diffVar.size()+1] ;
                    for (String [] strings:arrayList) {
                        int tempIndex=temp.findIndexOfVar(strings[0]);
                        tempRow[tempIndex] = strings[1];
                    }
                    int numPone= one.findIndexOfVar("p");
                    BigDecimal num = new BigDecimal(stringArrayOne[numPone]);
                    int numPtwo= two.findIndexOfVar("p");
                    BigDecimal num1 = new BigDecimal(stringArrayTwo[numPtwo]);
                    tempRow[tempRow.length-1]=num.multiply(num1).toString();
                    multOperation++;
                    temp.cpt.add(tempRow);

                }
                contains =true;
            }
        }
        return temp;
    }

    /**
     * create deep copy for all CPT
     * @return Array contain the copy CPT
     * @param queries
     * @param evidence
     * @param hiddenVariables
     */
    private static ArrayList<CPT> getCPT(String[] queries, ArrayList<String[]> evidence, ArrayList<String> hiddenVariables) {
        ArrayList<CPT> temp = new ArrayList<>();
        ArrayList<String> factor = findAncestor(queries,evidence);
        for (Node n:graph) {
            if (factor.contains(n.name)) {
                CPT tempCpt = new CPT(n.cpt);
                temp.add(tempCpt);
            }
        }
        for (int i = 0; i <hiddenVariables.size() ; i++) {
            if (!factor.contains(hiddenVariables.get(i))){
                hiddenVariables.remove(i);
                i--;
            }
        }
        return temp;
    }

    private static ArrayList<String> findAncestor(String[] queries, ArrayList<String[]> evidence) {
        ArrayList<String> ancestor = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        strings.add(queries[0]);
        for (String [] str:evidence) {
            strings.add(str[0]);
        }
        Queue<Node> queue = new LinkedList<>();
        for (String s:strings) {
            queue.add(returnNode(s));
            while (!queue.isEmpty()){
                Node node = queue.poll();
                if (!ancestor.contains(node.name)){
                    ancestor.add(node.name);
                }
                for (Node parent:node.parents) {
                    if (!ancestor.contains(parent.name)){
                        queue.add(parent);
                    }
                }
            }
        }
        return ancestor;
    }


}
