package inutile;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by ShilleR on 22/03/14.
 */
public class Test {
    public static void main(String[] args) {
        try{
            String s = "{\"ciao\":\"mamma\"}";

            JSONParser p = new JSONParser();

            JSONObject obj = (JSONObject) p.parse(s);
            obj.put("altro","parametro");
            System.out.println("obj.toJSONString() = " + obj.toJSONString());

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
