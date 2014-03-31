package inutile;

import com.sun.tools.internal.xjc.generator.util.ExistingBlockReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by ShilleR on 22/03/14.
 */
public class Test {
    public static void main(String[] args) {
        try{






            String e = "{\"ciao\":[1,2,3,4,5]}#";
            String[] s = e.split("#");
            System.out.println(s.length);
            JSONParser parser = new JSONParser();
            JSONObject j = (JSONObject) parser.parse(s[0]);
            JSONArray arr = (JSONArray)j.get("ciao");
            for(int i=0; i<arr.size(); i++){
                System.out.println(arr.get(i));
            }

            arr = new JSONArray();
            arr.add(new Integer(3));
            arr.add(new Integer(4));
            arr.add(new Integer(6));
            arr.add(new Integer(8));
            System.out.println(arr.toJSONString());
            
        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
