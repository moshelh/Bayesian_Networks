import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class readFile {
    public static List<String> read(String path) throws IOException {
        BufferedReader in
                = new BufferedReader(new FileReader(path));

        List<String> allStrings = new ArrayList<String>();
        String str ="";
        while(true)
        {
            String tmp = in.readLine();
            if(tmp==null){
                break;
            }
            if(tmp.isEmpty())
            {
                if(!str.isEmpty())
                {
                    allStrings.add(str);
                }
                str= "";
            }
            else if(tmp==null)
            {
                break;
            }
            else
            {
                if(str.isEmpty())
                {
                    str = tmp;
                }
                else
                {
                    str += "\\n" + tmp;
                }
            }
        }
        allStrings.add(str);
        return allStrings;
    }
    public static ArrayList<String []> split(List<String> text){
        ArrayList<String []> t = new ArrayList<>();
        for (String s:
             text) {
            String tmp []=s.trim().split(Pattern.quote("\\n"));
//            System.out.println(Arrays.toString(tmp));
            t.add(tmp);
        }
        return t;
    }


}
