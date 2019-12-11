import java.util.ArrayList;

public class CPT implements Comparable<CPT> {

    public ArrayList<String []> cpt;
    public String[] head;

    CPT( ){
        cpt=new ArrayList<>();
    }
    CPT(CPT cpt){
        this.cpt = new ArrayList<>();
        head = new String[cpt.head.length];
        System.arraycopy(cpt.head, 0, this.head, 0, cpt.head.length);
        for (String [] strings:cpt.cpt) {
            String [] s= new String[strings.length];
            System.arraycopy(strings,0,s,0,strings.length);
            this.cpt.add(s);
        }

    }

    public int findIndexOfVar(String temp){
        int index=0;
        for (int i = 0; i <head.length ; i++) {
            if (head[i].equals(temp)){
                index=i;
                break;
            }
        }
        return index;
    }
    public boolean containsVar(String temp){
        for (String s : head) {
            if (s.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(CPT o) {
        int num = Integer.compare(this.head.length, o.head.length);
        if (num ==0){
            int sumThis = 0;
            for (String s : head) {
                if (!s.equals("p")) {
                    sumThis += s.charAt(0);
                }
            }
            int sumOther = 0;
            for (int i = 0; i < o.head.length; i++) {
                if (!o.head[i].equals("p")) {
                    sumThis += o.head[i].charAt(0);
                }
            }
            return Integer.compare(sumThis, sumOther);
        }
        return num;
    }
}
