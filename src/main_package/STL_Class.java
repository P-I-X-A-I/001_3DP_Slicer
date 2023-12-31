package main_package;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.swing.SwingUtilities;


public class STL_Class {
	
	
	// variables
	String file_path_str;
	String file_name;
	long dataLength = 0;
	boolean isValid = false;
	
	int num_of_triangles = 0;
	float obj_width = 0.0f;
	float obj_height = 0.0f;
	float obj_depth = 0.0f;
	
	
	public volatile float[] vert;// alloc later
	public volatile float[] norm;// alloc later
	public volatile float[] vert_cp;
	public volatile float[] norm_cp;
	public boolean isAccesible = true;
	

	
	public float bound_min_x = 10000.0f;
	public float bound_min_y = 10000.0f;
	public float bound_min_z = 10000.0f;
	public float bound_max_x = -10000.0f;
	public float bound_max_y = -10000.0f;
	public float bound_max_z = -10000.0f;
	public float shift_x = 0.0f;
	public float shift_y = 0.0f;
	public float shift_z = 0.0f; // = -(bound_min_z)
	
	volatile float[] matX = new float[9];
	volatile float[] matY = new float[9];
	volatile float[] matZ = new float[9];
	volatile float[] tmpM = new float[9];
	volatile float[] matN = new float[9];
	
	
	// constructor
	public STL_Class(String filePath) throws IOException
	{
		// create file path string
		file_path_str = new String(filePath);
		
		// open file
		try {
			
			// get file size
			File fl = new File(file_path_str);
			dataLength = fl.length();

			// create file name
			file_name = new String(fl.getName());
			
			// access to data
			FileInputStream fp = new FileInputStream(file_path_str);
			
			// read whole data
			byte[] wholeData = new byte[(int) dataLength];
			int ret = fp.read(wholeData);
			System.out.println(file_path_str + ":" + ret + "byte");
			
			// create byte buffer
			ByteBuffer wholeBuffer = ByteBuffer.wrap(wholeData);
			wholeBuffer.order(ByteOrder.LITTLE_ENDIAN);
			//**************************************************
			// read header ( 80 byte )
			byte hc;
			for(int i = 0 ; i < 80 ; i++ )
			{
				hc = wholeBuffer.get();
				char tempChar = (char)hc;
				System.out.print(tempChar);
			}
			
			//**********************************************
			// read num of triangles (4byte)
			num_of_triangles = wholeBuffer.getInt();
			System.out.println("T : " + num_of_triangles);
			
			// alloc memory
			vert = new float[num_of_triangles * 3 * 3]; // xyz * 3v
			norm = new float[num_of_triangles * 3 * 3];
			vert_cp = new float[num_of_triangles * 3 * 3];
			norm_cp = new float[num_of_triangles * 3 * 3];
			
			//************************************************
			// read data from wholeBuffer
			for( int i = 0 ; i < num_of_triangles ; i++ )
			{
				// set normal
				float nX = wholeBuffer.getFloat();
				float nY = wholeBuffer.getFloat();
				float nZ = wholeBuffer.getFloat();
				
				int nID = i*9;
				norm[nID+0] = nX;	norm[nID+1] = nY;	norm[nID+2] = nZ;
				norm[nID+3] = nX;	norm[nID+4] = nY;	norm[nID+5] = nZ;
				norm[nID+6] = nX;	norm[nID+7] = nY;	norm[nID+8] = nZ;
			
				// set vertex 1
				int vID = i*9;
				float vX = wholeBuffer.getFloat();
				float vY = wholeBuffer.getFloat();
				float vZ = wholeBuffer.getFloat();
				vert[vID+0] = vX;	vert[vID+1] = vY;	vert[vID+2] = vZ;
				
				// set vertex 2
				vX = wholeBuffer.getFloat();
				vY = wholeBuffer.getFloat();
				vZ = wholeBuffer.getFloat();
				vert[vID+3] = vX;	vert[vID+4] = vY;	vert[vID+5] = vZ;

				// set vertex 3
				vX = wholeBuffer.getFloat();
				vY = wholeBuffer.getFloat();
				vZ = wholeBuffer.getFloat();
				vert[vID+6] = vX;	vert[vID+7] = vY;	vert[vID+8] = vZ;

				// skip 2 byte
				int pos = wholeBuffer.position();
				pos += 2;
				wholeBuffer.position(pos);
			}// for
			
			// check boundings
			this.check_boundings_loop();
			
			// close stream
			fp.close();
			
			// set valid flag
			isValid = true;
			
			
			// decide centerpoint
			this.move_center_to_origin(); // must be set
			this.copy_vert_norm(); // must be set
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			isValid = false;
			e.printStackTrace();
		}
		
		
		
	} // constructor
	
	
	
	private void check_boundings(float x, float y, float z)
	{
		// update max
		if( bound_max_x < x ) { bound_max_x = x;}
		if( bound_max_y < y ) { bound_max_y = y;}
		if( bound_max_z < z ) { bound_max_z = z;}
		
		// update min
		if( bound_min_x > x ) { bound_min_x = x;}
		if( bound_min_y > y ) { bound_min_y = y;}
		if( bound_min_z > z ) { bound_min_z = z;}
	}
	
	private void check_boundings_loop()
	{
		// reset bounding box
		bound_max_x = -10000.0f;	bound_min_x = 10000.0f;
		bound_max_y = -10000.0f;	bound_min_y = 10000.0f;
		bound_max_z = -10000.0f;	bound_min_z = 10000.0f;
		
		int iter = num_of_triangles*3;
		
		for( int i = 0 ; i < iter ; i++ )
		{
			int ID = i*3;
			float X = vert[ID];
			float Y = vert[ID+1];
			float Z = vert[ID+2];
			
			if( bound_max_x < X) { bound_max_x = X; }
			if( bound_max_y < Y) { bound_max_y = Y; }
			if( bound_max_z < Z) { bound_max_z = Z; }
			
			if( bound_min_x > X) { bound_min_x = X; }
			if( bound_min_y > Y) { bound_min_y = Y; }
			if( bound_min_z > Z) { bound_min_z = Z; }
		}
	}
	
	private void move_center_to_origin()
	{
		float tempX = (bound_max_x + bound_min_x)/2.0f;
		float tempY = (bound_max_y + bound_min_y)/2.0f;
		float tempZ = (bound_max_z + bound_min_z)/2.0f;
		
		// shift all vertex
		
		
		int num_vert = num_of_triangles * 3;
		
		for( int i = 0 ; i < num_vert ; i++ )
		{
			int ID = i*3;
			vert[ID+0] -= tempX;
			vert[ID+1] -= tempY;
			vert[ID+2] -= tempZ;
		}
		
		// shift bounding box
		bound_max_x -= tempX;	bound_min_x -= tempX;
		bound_max_y -= tempY;	bound_min_y -= tempY;
		bound_max_z -= tempZ;	bound_min_z -= tempZ;
		
		// shift-z
		shift_z = -bound_min_z;
	}

	
	private void copy_vert_norm()
	{
		int iter = num_of_triangles *3 * 3;
		for( int i = 0 ; i < iter ; i++ )
		{
			vert_cp[i] = vert[i];
			norm_cp[i] = norm[i];
		}
	}
	
	///////////////////////////////////////////
	//////// Transformation ///////////////////
	///////////////////////////////////////////
	
	void rotateXYZ(float xDeg, float yDeg, float zDeg)
	{
		// as fence
		isAccesible = false;
		
		
		// convert to radian
		float radX = xDeg * 0.0174532925f;
		float radY = yDeg * 0.0174532925f;
		float radZ = zDeg * 0.0174532925f;
		
		
		// create matrix
		float cosX = (float)Math.cos(radX);
		float sinX = (float)Math.sin(radX);
		matX[0] = 1.0f;	matX[3] = 0.0f;	matX[6] = 0.0f;
		matX[1] = 0.0f;	matX[4] = cosX;	matX[7] = -sinX;
		matX[2] = 0.0f;	matX[5] = sinX;	matX[8] = cosX;
		
		float cosY = (float)Math.cos(radY);
		float sinY = (float)Math.sin(radY);
		matY[0] = cosY;		matY[3] = 0.0f;		matY[6] = sinY;
		matY[1] = 0.0f;		matY[4] = 1.0f;		matY[7] = 0.0f;
		matY[2] = -sinY;	matY[5] = 0.0f;		matY[8] = cosY;
		
		float cosZ = (float)Math.cos(radZ);
		float sinZ = (float)Math.sin(radZ);
		matZ[0] = cosZ;		matZ[3] = -sinZ;	matZ[6] = 0.0f;
		matZ[1] = sinZ;		matZ[4] = cosZ;		matZ[7] = 0.0f;
		matZ[2] = 0.0f;		matZ[5] = 0.0f;		matZ[8] = 1.0f;
		
		// matX * matY = tmpM
		tmpM[0] = matX[0]*matY[0] + matX[1]*matY[3] + matX[2]*matY[6];
		tmpM[1] = matX[0]*matY[1] + matX[1]*matY[4] + matX[2]*matY[7];
		tmpM[2] = matX[0]*matY[2] + matX[1]*matY[5] + matX[2]*matY[8];
		
		tmpM[3] = matX[3]*matY[0] + matX[4]*matY[3] + matX[5]*matY[6];
		tmpM[4] = matX[3]*matY[1] + matX[4]*matY[4] + matX[5]*matY[7];
		tmpM[5] = matX[3]*matY[2] + matX[4]*matY[5] + matX[5]*matY[8];
		
		tmpM[6] = matX[6]*matY[0] + matX[7]*matY[3] + matX[8]*matY[6];
		tmpM[7] = matX[6]*matY[1] + matX[7]*matY[4] + matX[8]*matY[7];
		tmpM[8] = matX[6]*matY[2] + matX[7]*matY[5] + matX[8]*matY[8];
		
		// tmpM * matZ = matN
		matN[0] = tmpM[0]*matZ[0] + tmpM[1]*matZ[3] + tmpM[2]*matZ[6];
		matN[1] = tmpM[0]*matZ[1] + tmpM[1]*matZ[4] + tmpM[2]*matZ[7];
		matN[2] = tmpM[0]*matZ[2] + tmpM[1]*matZ[5] + tmpM[2]*matZ[8];
		
		matN[3] = tmpM[3]*matZ[0] + tmpM[4]*matZ[3] + tmpM[5]*matZ[6];
		matN[4] = tmpM[3]*matZ[1] + tmpM[4]*matZ[4] + tmpM[5]*matZ[7];
		matN[5] = tmpM[3]*matZ[2] + tmpM[4]*matZ[5] + tmpM[5]*matZ[8];

		matN[6] = tmpM[6]*matZ[0] + tmpM[7]*matZ[3] + tmpM[8]*matZ[6];
		matN[7] = tmpM[6]*matZ[1] + tmpM[7]*matZ[4] + tmpM[8]*matZ[7];
		matN[8] = tmpM[6]*matZ[2] + tmpM[7]*matZ[5] + tmpM[8]*matZ[8];

		
		// rotate vertex & normal
		int iter = num_of_triangles * 3;

		
		for( int i = 0 ; i < iter ; i++ )
		{
			int ID = i*3;
			
			vert[ID+0] = matN[0]*vert_cp[ID+0] + matN[1]*vert_cp[ID+1] + matN[2]*vert_cp[ID+2];
			vert[ID+1] = matN[3]*vert_cp[ID+0] + matN[4]*vert_cp[ID+1] + matN[5]*vert_cp[ID+2];
			vert[ID+2] = matN[6]*vert_cp[ID+0] + matN[7]*vert_cp[ID+1] + matN[8]*vert_cp[ID+2];
			
			norm[ID+0] = matN[0]*norm_cp[ID+0] + matN[1]*norm_cp[ID+1] + matN[2]*norm_cp[ID+2];
			norm[ID+1] = matN[3]*norm_cp[ID+0] + matN[4]*norm_cp[ID+1] + matN[5]*norm_cp[ID+2];
			norm[ID+2] = matN[6]*norm_cp[ID+0] + matN[7]*norm_cp[ID+1] + matN[8]*norm_cp[ID+2];
		}
		this.check_boundings_loop();
		
		this.move_center_to_origin();  // must be set
		this.copy_vert_norm(); // must be set
		
		// like fence
		isAccesible = true;
		
	}
	
	
	//*********************************************
	
	void save_STL(String savePath) throws IOException // export STL before Slicer process
	{
		try {
			FileOutputStream fpo = new FileOutputStream(savePath);
			DataOutputStream dos = new DataOutputStream(fpo);
			
			// write data
			// header (80byte)
			byte tempChar = 32;
			for( int i = 0 ; i < 80 ; i++ )
			{
				dos.write((int)tempChar); // write 1 byte. upper 3byte is ignored
			}
			
			// num of triangles
			dos.writeInt(num_of_triangles);
	
			// write normal & vertex
			// shift vertex to fit ground
			
			
			for( int i = 0 ; i < num_of_triangles ; i++ )
			{
				int ID = i*9;
				dos.writeFloat(norm[ID]);
				dos.writeFloat(norm[ID+1]);
				dos.writeFloat(norm[ID+2]);
				
				dos.writeFloat(vert[ID]);
				dos.writeFloat(vert[ID+1]);
				dos.writeFloat(vert[ID+2]);
				dos.writeFloat(vert[ID+3]);
				dos.writeFloat(vert[ID+4]);
				dos.writeFloat(vert[ID+5]);
				dos.writeFloat(vert[ID+6]);
				dos.writeFloat(vert[ID+7]);
				dos.writeFloat(vert[ID+8]);
				
				// skip 2 bytes
				dos.writeByte((int)0);
				dos.writeByte((int)0);
			}
			
			dos.close();
			fpo.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
