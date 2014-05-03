package AndroidServerTest;

/**
 * Created by ShilleR on 3/5/14.
 */

import com.badlogic.gdx.math.Vector3;
import org.json.simple.JSONObject;

public class PhysicCarStatus {
    public float steering=0;
    public Vector3 position;
    public Vector3 linearVelocity;
    public Vector3 angularVelocity;
    public long time;


    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        JSONObject positionJson = new JSONObject();
        JSONObject linearJson = new JSONObject();
        JSONObject angularJson = new JSONObject();

        positionJson.put("x",position.x);
        positionJson.put("y",position.y);
        positionJson.put("z",position.z);

        linearJson.put("x",linearVelocity.x);
        linearJson.put("y",linearVelocity.y);
        linearJson.put("z",linearVelocity.z);

        angularJson.put("x",angularVelocity.x);
        angularJson.put("y",angularVelocity.y);
        angularJson.put("z",angularVelocity.z);

        json.put("time", time);
        json.put("steering", steering);
        json.put("position", positionJson);
        json.put("linear", linearJson);
        json.put("angular", angularJson);

        return json;
    }

    public void fromJSON(JSONObject json){
        steering = (Float) json.get("steering");
        JSONObject positionJson = (JSONObject) json.get("position");
        JSONObject linearJson =  (JSONObject) json.get("linear");
        JSONObject angularJson =  (JSONObject) json.get("angular");

        time = (Long) json.get("time");

        position.x = (Float) positionJson.get("x");
        position.y = (Float) positionJson.get("y");
        position.z = (Float) positionJson.get("z");

        linearVelocity.x = (Float) linearJson.get("x");
        linearVelocity.y = (Float) linearJson.get("y");
        linearVelocity.z = (Float) linearJson.get("z");

        angularVelocity.x = (Float) angularJson.get("x");
        angularVelocity.y = (Float) angularJson.get("y");
        angularVelocity.z = (Float) angularJson.get("z");
    }

}
