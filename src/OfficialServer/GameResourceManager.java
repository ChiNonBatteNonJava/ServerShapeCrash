package OfficialServer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameResourceManager {
    private HashMap<String, Game3DModel> models;
    private static GameResourceManager instance;

    /*
	// class TAG for Log
	private static final String TAG = "GameResourceManager";
	
	// constants
	private static final String shadersPath = "shaders";
	private static final String modelsPath = "models";
	private static final String shaderProgramsFilename = "programs";
	
	// singleton instance

	
	// locals
	private Resources androidRes;
	private HashMap<String, GameShader> shaders;
	private HashMap<String, GameShaderProgram> programs;
	private HashMap<String, Game3DModel> models;
	private HashMap<String,Integer> textures;
	private float aspectRatio=1;
	
	
	*/

	private GameResourceManager()
	{
		models = new HashMap<String, Game3DModel>();
	}

	public static GameResourceManager getInstance()
	{
		if (instance == null)
			instance = new GameResourceManager();
		return instance;
	}

	/*
	public static String getShadersPath()
	{
		return shadersPath;
	}
	
	public static String getModelsPath()
	{
		return modelsPath;
	}
	
	public void bindAndroidResources(Resources res)
	{
		androidRes = res;
	}
	
	public Resources getAndroidResources()
	{
		return androidRes;
	}
	
	public GameShader getShaderByName(String shaderName)
	{
		return shaders.get(shaderName);
	}
	
	public GameShaderProgram getShaderProgramByName(String programName)
	{
		return programs.get(programName);
	}
	*/

	public Game3DModel get3DModelByName(String modelName)
	{
		return models.get(modelName);
	}

	/*
	public void loadShaders()
	{
		if (androidRes != null)
		{
			try {
				for (String shaderFileName : androidRes.getAssets().list(shadersPath))
				{
					// check shader type
					int type = 0;
					if (shaderFileName.endsWith(".ps"))
						type = GLES20.GL_FRAGMENT_SHADER;
					else if (shaderFileName.endsWith(".vs"))
						type = GLES20.GL_VERTEX_SHADER;
					
					if (type != 0) // valid shader type
					{
						// read file
						BufferedReader reader = new BufferedReader(new InputStreamReader(androidRes.getAssets().open(GameResourceManager.getShadersPath() + File.separator + shaderFileName)));
						
						String shaderCode = "";
						
						String line = null;
						while ((line = reader.readLine()) != null) {
						    shaderCode = shaderCode + line + " \n ";
						}
						
						// initialize GameShader object
						GameShader s = new GameShader(type);
						
						s.setShaderName(shaderFileName);
						
						s.setShaderCode(shaderCode);
						
						// add to collection
						shaders.put(s.getShaderName(), s);
						
						Log.d(TAG, "Shader code loaded: " + s.getShaderName());
					}
				}
			} catch (IOException e) {
	            e.printStackTrace();
			}
		}
		else
		{
			Log.e(TAG, "loadShadersCode() called before Android Resources binding!");
		}
	}
	
	public void compileShaders()
	{
		for (HashMap.Entry<String, GameShader> entry : shaders.entrySet())
		{
			entry.getValue().compileShader();
		}
	}
	
	public void releaseShaders()
	{
		for (HashMap.Entry<String, GameShader> entry : shaders.entrySet())
		{
			entry.getValue().releaseShader();
		}
		
		shaders = null;
	}
	
	public void loadShaderPrograms()
	{
		if (androidRes != null)
		{
			try {
				// read file
				BufferedReader reader = new BufferedReader(new InputStreamReader(androidRes.getAssets().open(GameResourceManager.getShadersPath() + File.separator + shaderProgramsFilename)));
				
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] programLine = line.split("=");
					String[] programShaders = programLine[1].split(",");

					if (programLine.length != 2 || programShaders.length != 2)
					{
						Log.e(TAG, "Error parsing programs: " + line);
						continue;
					}
					
					String programName = programLine[0].trim();
					String vsName = programShaders[0].trim();
					String psName = programShaders[1].trim();
					
					// initialize GameShaderProgam object
					GameShaderProgram sp = new GameShaderProgram(getShaderByName(vsName), getShaderByName(psName));
					
					// add to collection
					programs.put(programName, sp);
				}
			} catch (IOException e) {
	            e.printStackTrace();
			}
		}
		else
		{
			Log.e(TAG, "loadShaderPrograms() called before Android Resources binding!");
		}
	}

	*/

	public void load3DObjModel(String fileName){
		if (GameResourceManager.getInstance().get3DModelByName(fileName) == null)
		{
			try {
				class IndexHelper{
					public int vertex;
					public int normal;
					public int uvs;
					public IndexHelper(String v){
						String[] l=v.split("/");
						vertex=Integer.parseInt(l[0])-1;
						normal=Integer.parseInt(l[2])-1;
						uvs=Integer.parseInt(l[1])-1;	
					}
					
				}
				// read file
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				List<Vector3> vertices=new ArrayList<Vector3>();
				List<Vector3> normal=new ArrayList<Vector3>();
				List<Vector2> uvs=new ArrayList<Vector2>();
				List<IndexHelper> index=new ArrayList<IndexHelper>();
				
				String line = null;
				while ((line = reader.readLine()) != null) {
					line=line.trim();
					//uvs
					if(line.startsWith("vt")){
						line=line.replace("vt", "");
						line=line.trim();
						String[] v=line.split(" ");
						uvs.add(new Vector2(Float.parseFloat(v[0]),Float.parseFloat(v[1])));
					}else if(line.startsWith("vn")){
						
						line=line.replace("vn", "");
						line=line.trim();
						String[] v=line.split(" ");
						normal.add(new Vector3(Float.parseFloat(v[0]),Float.parseFloat(v[1]),Float.parseFloat(v[2])));
						
					}else if(line.startsWith("v")){
						
						line=line.replace("v", "");
						line=line.trim();
						String[] v=line.split(" ");
						vertices.add(new Vector3(Float.parseFloat(v[0]),Float.parseFloat(v[1]),Float.parseFloat(v[2])));
						
					}else if(line.startsWith("f")){
						line=line.replace("f", "");
						line=line.trim();
						String[] v=line.split(" ");
						index.add(new IndexHelper(v[0]));
						index.add(new IndexHelper(v[1]));
						index.add(new IndexHelper(v[2]));
						
					}	
				}
				float[] verticesFloat = new float[index.size()*8];
				int a=0;
				for (IndexHelper i:index){
					verticesFloat[a]=vertices.get(i.vertex).x; a++;
					verticesFloat[a]=vertices.get(i.vertex).y; a++;
					verticesFloat[a]=vertices.get(i.vertex).z; a++;
					
					verticesFloat[a]=uvs.get(i.uvs).x; a++;
					verticesFloat[a]=uvs.get(i.uvs).y; a++;
					
					verticesFloat[a]=normal.get(i.normal).x; a++;
					verticesFloat[a]=normal.get(i.normal).y; a++;
					verticesFloat[a]=normal.get(i.normal).z; a++;

				}
				// initialize Game3DModel
				Game3DModel m = new Game3DModel(verticesFloat,8);
				
				// add to collection
				models.put(fileName, m);
				
			} catch (IOException e) {
	            e.printStackTrace();
			}
		}
		else
		{
			Log.log("load3DModels() called before Android Resources binding!");
		}
	}

	/*
	public void load3DModel(String modelFileName)
	{
		if (androidRes != null && GameResourceManager.getInstance().get3DModelByName(modelFileName) == null)
		{
			try {
				// read file
				BufferedReader reader = new BufferedReader(new InputStreamReader(androidRes.getAssets().open(GameResourceManager.getModelsPath() + File.separator + modelFileName)));
				
				String modelString = "";
				
				String line = null;
				while ((line = reader.readLine()) != null) {
					modelString = modelString + line + " ";
				}
				
				String[] verticesString = modelString.split(",");
				float[] vertices = new float[verticesString.length];
				
				Log.d(TAG, modelString);
				
				for (int i = 0; i < vertices.length; i++)
				{
					vertices[i] = Float.parseFloat(verticesString[i]);
				}
				
				// initialize Game3DModel
				Game3DModel m = new Game3DModel(vertices);
				
				// add to collection
				models.put(modelFileName, m);
				
				Log.d(TAG, "Model loaded: " + modelFileName);
			} catch (IOException e) {
	            e.printStackTrace();
			}
		}
		else
		{
			Log.e(TAG, "load3DModels() called before Android Resources binding!");
		}
	}


	public void loadTexture(String fileName){
		int mBrickDataHandle;
		try {
			mBrickDataHandle = loadTextureDo(fileName);
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBrickDataHandle);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			textures.put(fileName, mBrickDataHandle);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public int  getTexture(String name){
		return textures.get(name);
	}
	
	
	private int loadTextureDo(final String name)
			throws IOException {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling

			// Read in the resource
			AssetManager am = androidRes.getAssets();
			InputStream is = am.open("Textures"+ File.separator +name);
			final Bitmap bitmap = BitmapFactory.decodeStream(is);

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public float getAspectRatio(){
		return aspectRatio;
	}
	public void setAspectRatio(float ratio){
		aspectRatio=ratio;
		
	}
	*/
}
