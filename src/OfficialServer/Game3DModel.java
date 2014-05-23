package OfficialServer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.gdx.math.Vector3;

public class Game3DModel {
	// class TAGz for Log
	private static final String TAG = "Game3DModel";

	public int COORDS_PER_VERTEX = 3;

	private float[] vertices;
	private FloatBuffer vertexBuffer;
	private int vertexCount;
	private float[] verticesOnly = null;
	private Vector3[] verticesVector = null;
	
	public Game3DModel()
	{
		this.vertexCount = 0;
	}

	public Game3DModel(float[] vertices) {
		this.vertices = vertices;
		this.vertexCount = vertices.length / COORDS_PER_VERTEX;
	}

	public Game3DModel(float[] vertices, int cordXvertex) {
		this.vertices = vertices;
		this.COORDS_PER_VERTEX = cordXvertex;
		this.vertexCount = vertices.length / COORDS_PER_VERTEX;
	}

	public float[] getVertices() {
		if (verticesOnly == null) {
			verticesOnly = new float[(vertices.length / COORDS_PER_VERTEX) * 3];
			int a = 0;

			for (int i = 0; i < vertices.length; i += COORDS_PER_VERTEX) {
				verticesOnly[a] = vertices[i];
				a++;
				verticesOnly[a] = vertices[i + 1];
				a++;
				verticesOnly[a] = vertices[i + 2];
				a++;

			}
		}
		return verticesOnly;
	}

	public Vector3[] getVerticesVector3() {
		if (verticesVector == null) {
			verticesVector = new Vector3[(vertices.length / COORDS_PER_VERTEX)];
			int a = 0;
			for (int i = 0; i < vertices.length; i += COORDS_PER_VERTEX) {

				verticesVector[a] = new Vector3(vertices[i], vertices[i + 1],
						vertices[i + 2]);
				a++;

			}
		}
		return verticesVector;
	}

	public FloatBuffer getVertexBuffer() {
		if (vertices == null) {
			return null;
		}

		if (vertexBuffer == null) {
			ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
			bb.order(ByteOrder.nativeOrder());

			vertexBuffer = bb.asFloatBuffer();

			vertexBuffer.put(vertices);

			vertexBuffer.position(0);
		}

		return vertexBuffer;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
