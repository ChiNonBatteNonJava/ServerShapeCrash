package OfficialServer;

import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class PhysicCarStatus {
	public float steering=0;
	public Vector3 position;
	public Vector3 linearVelocity;
	public Vector3 angularVelocity;
	
	public Vector3 linearVelocityChaiss;
	public Vector3 angularVelocityChaiss;
	
	
	public Quaternion orientation;
	public float[] wheel;
	public PhysicCarStatus(){
		position = new Vector3();
		linearVelocity = new Vector3();
		angularVelocity = new Vector3();
		
		linearVelocityChaiss = new Vector3();
		angularVelocityChaiss = new Vector3();
		
		
		orientation = new Quaternion();
		wheel=new float[3];
	}
	
	public JSONObject toJson(){
		JSONObject json = new JSONObject();
		JSONObject positionJson = new JSONObject();
		JSONObject linearJson = new JSONObject();
		JSONObject angularJson = new JSONObject();
		
		//JSONObject linearJsonChaiss = new JSONObject();
		//JSONObject angularJsonChaiss = new JSONObject();
		
		JSONObject orientationJson = new JSONObject();
		//JSONObject wheelJson = new JSONObject();
		
		positionJson.put("x",position.x);	
		positionJson.put("y",position.y);
		positionJson.put("z",position.z);
		
		linearJson.put("x",linearVelocity.x);
		linearJson.put("y",linearVelocity.y);
		linearJson.put("z",linearVelocity.z);
		
		angularJson.put("x",angularVelocity.x);
		angularJson.put("y",angularVelocity.y);
		angularJson.put("z",angularVelocity.z);
		
		/*
		linearJsonChaiss.put("x",linearVelocityChaiss.x);
		linearJsonChaiss.put("y",linearVelocityChaiss.y);
		linearJsonChaiss.put("z",linearVelocityChaiss.z);
		
		angularJsonChaiss.put("x",angularVelocityChaiss.x);
		angularJsonChaiss.put("y",angularVelocityChaiss.y);
		angularJsonChaiss.put("z",angularVelocityChaiss.z);
		*/
		orientationJson.put("x",orientation.x);
		orientationJson.put("y",orientation.y);
		orientationJson.put("z",orientation.z);
		orientationJson.put("w",orientation.w);
		/*
		wheelJson.put("0", wheel[0]);
		wheelJson.put("1", wheel[1]);
		wheelJson.put("2", wheel[2]);
		*/
		json.put("steering", steering);
		json.put("position", positionJson);
		json.put("linear", linearJson);
		json.put("angular", angularJson);
		json.put("orientation", orientationJson);
		/*
		json.put("wheel", wheelJson);
		json.put("linearC", linearJsonChaiss);
		json.put("angularC", angularJsonChaiss);
		*/
		return json;
	}
	
	public void fromJSON(JSONObject json){
		steering = ((Double) json.get("steering")).floatValue();
		JSONObject positionJson = (JSONObject) json.get("position");
		JSONObject linearJson =  (JSONObject) json.get("linear");
		JSONObject angularJson =  (JSONObject) json.get("angular");
		JSONObject orientationJson = (JSONObject) json.get("orientation");
		//JSONObject wheelJson = (JSONObject) json.get("wheel");
		
		//JSONObject linearJsonC =  (JSONObject) json.get("linearC");
		//JSONObject angularJsonC =  (JSONObject) json.get("angularC");
		
		position.x = ((Double) positionJson.get("x")).floatValue();
		position.y = ((Double) positionJson.get("y")).floatValue();
		position.z = ((Double) positionJson.get("z")).floatValue();
		
		linearVelocity.x = ((Double) linearJson.get("x")).floatValue();
		linearVelocity.y = ((Double) linearJson.get("y")).floatValue();
		linearVelocity.z = ((Double) linearJson.get("z")).floatValue();
		
		angularVelocity.x = ((Double) angularJson.get("x")).floatValue();
		angularVelocity.y = ((Double) angularJson.get("y")).floatValue();
		angularVelocity.z = ((Double) angularJson.get("z")).floatValue();
		
		/*
		linearVelocityChaiss.x = ((Double) linearJsonC.get("x")).floatValue();
		linearVelocityChaiss.y = ((Double) linearJsonC.get("y")).floatValue();
		linearVelocityChaiss.z = ((Double) linearJsonC.get("z")).floatValue();
		
		angularVelocityChaiss.x = ((Double) angularJsonC.get("x")).floatValue();
		angularVelocityChaiss.y = ((Double) angularJsonC.get("y")).floatValue();
		angularVelocityChaiss.z = ((Double) angularJsonC.get("z")).floatValue();
		*/
		orientation.x = ((Double) orientationJson.get("x")).floatValue();
		orientation.y = ((Double) orientationJson.get("y")).floatValue();
		orientation.z = ((Double) orientationJson.get("z")).floatValue();
		orientation.w = ((Double) orientationJson.get("w")).floatValue();
		/*
		wheel[0]=((Double) wheelJson.get("0")).floatValue();
		wheel[1]=((Double) wheelJson.get("1")).floatValue();
		wheel[2]=((Double) wheelJson.get("2")).floatValue();
		*/
		
	}
	
}