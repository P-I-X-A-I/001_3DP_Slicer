package main_package;

import java.nio.FloatBuffer;

public class matrixClass {

	// variables
	float[] MATRIX = new float[16];
	float[] INVERSE = new float[16];
	float[] Lm = new float[16];
	float[] Rm = new float[16];
	float[] tempM = new float[16];
	float[] xVec = new float[3];
	float[] yVec = new float[3];
	float[] zVec = new float[3];
	FloatBuffer matBuffer = FloatBuffer.wrap(MATRIX);
	
	// constructor
	public matrixClass()
	{
		this.initMatrix();
	}
	
	// matrix initialize
	void initMatrix()
	{
		MATRIX[0] = 1.0f;	MATRIX[4] = 0.0f;	MATRIX[8] = 0.0f;	MATRIX[12] = 0.0f;
		MATRIX[1] = 0.0f;	MATRIX[5] = 1.0f;	MATRIX[9] = 0.0f;	MATRIX[13] = 0.0f;
		MATRIX[2] = 0.0f;	MATRIX[6] = 0.0f;	MATRIX[10] = 1.0f;	MATRIX[14] = 0.0f;
		MATRIX[3] = 0.0f;	MATRIX[7] = 0.0f;	MATRIX[11] = 0.0f;	MATRIX[15] = 1.0f;

		INVERSE[0] = 1.0f;	INVERSE[4] = 0.0f;	INVERSE[8] = 0.0f;	INVERSE[12] = 0.0f;
		INVERSE[1] = 0.0f;	INVERSE[5] = 1.0f;	INVERSE[9] = 0.0f;	INVERSE[13] = 0.0f;
		INVERSE[2] = 0.0f;	INVERSE[6] = 0.0f;	INVERSE[10] = 1.0f;	INVERSE[14] = 0.0f;
		INVERSE[3] = 0.0f;	INVERSE[7] = 0.0f;	INVERSE[11] = 0.0f;	INVERSE[15] = 1.0f;
	}

	// matrix return
	float[] getMatrix()
	{
		return MATRIX;
	}
	
	// matrix float buffer return
	FloatBuffer getMatrixAsFloatBuffer()
	{
		return matBuffer;
	}
	
	// later *********************************
	// getInverseMatrix()
	// getInverse_of_currentMatrix()
	// ***************************************
	
	void calculateMatrix(float[] inMat)
	{		
		// copy matrix
		for(int i = 0 ; i < 16 ; i++ )
		{
			Lm[i] = inMat[i];
			Rm[i] = MATRIX[i];
		}
		
		MATRIX[0] = Lm[0] * Rm[0] + Lm[4] * Rm[1] + Lm[8] * Rm[2] + Lm[12] * Rm[3];
		MATRIX[1] = Lm[1] * Rm[0] + Lm[5] * Rm[1] + Lm[9] * Rm[2] + Lm[13] * Rm[3];
		MATRIX[2] = Lm[2] * Rm[0] + Lm[6] * Rm[1] + Lm[10] * Rm[2] + Lm[14] * Rm[3];
		MATRIX[3] = Lm[3] * Rm[0] + Lm[7] * Rm[1] + Lm[11] * Rm[2] + Lm[15] * Rm[3];

		MATRIX[4] = Lm[0] * Rm[4] + Lm[4] * Rm[5] + Lm[8] * Rm[6] + Lm[12] * Rm[7];
		MATRIX[5] = Lm[1] * Rm[4] + Lm[5] * Rm[5] + Lm[9] * Rm[6] + Lm[13] * Rm[7];
		MATRIX[6] = Lm[2] * Rm[4] + Lm[6] * Rm[5] + Lm[10] * Rm[6] + Lm[14] * Rm[7];
		MATRIX[7] = Lm[3] * Rm[4] + Lm[7] * Rm[5] + Lm[11] * Rm[6] + Lm[15] * Rm[7];

		MATRIX[8] = Lm[0] * Rm[8] + Lm[4] * Rm[9] + Lm[8] * Rm[10] + Lm[12] * Rm[11];
		MATRIX[9] = Lm[1] * Rm[8] + Lm[5] * Rm[9] + Lm[9] * Rm[10] + Lm[13] * Rm[11];
		MATRIX[10] = Lm[2] * Rm[8] + Lm[6] * Rm[9] + Lm[10] * Rm[10] + Lm[14] * Rm[11];
		MATRIX[11] = Lm[3] * Rm[8] + Lm[7] * Rm[9] + Lm[11] * Rm[10] + Lm[15] * Rm[11];

		MATRIX[12] = Lm[0] * Rm[12] + Lm[4] * Rm[13] + Lm[8] * Rm[14] + Lm[12] * Rm[15];
		MATRIX[13] = Lm[1] * Rm[12] + Lm[5] * Rm[13] + Lm[9] * Rm[14] + Lm[13] * Rm[15];
		MATRIX[14] = Lm[2] * Rm[12] + Lm[6] * Rm[13] + Lm[10] * Rm[14] + Lm[14] * Rm[15];
		MATRIX[15] = Lm[3] * Rm[12] + Lm[7] * Rm[13] + Lm[11] * Rm[14] + Lm[15] * Rm[15];
	} // calculate matrix
	
	
	void calculateVec4(float[] vec4)
	{
		// use tempM[0]~tempM[3] to stock result;
		tempM[0] = MATRIX[0]*vec4[0] + MATRIX[4]*vec4[1] + MATRIX[8]*vec4[2] + MATRIX[12]*vec4[3];
		tempM[1] = MATRIX[1]*vec4[0] + MATRIX[5]*vec4[1] + MATRIX[9]*vec4[2] + MATRIX[13]*vec4[3];
		tempM[2] = MATRIX[2]*vec4[0] + MATRIX[6]*vec4[1] + MATRIX[10]*vec4[2] + MATRIX[14]*vec4[3];
		tempM[3] = MATRIX[3]*vec4[0] + MATRIX[7]*vec4[1] + MATRIX[11]*vec4[2] + MATRIX[15]*vec4[3];
	
		// write back result
		vec4[0] = tempM[0];
		vec4[1] = tempM[1];
		vec4[2] = tempM[2];
		vec4[3] = tempM[3];
	}
	
	void calculateVec3(float[] vec3)
	{
		// use tempM[0] ~ tempM[2] to stock result
		tempM[0] = MATRIX[0]*vec3[0] + MATRIX[4]*vec3[1] + MATRIX[8]*vec3[2] + MATRIX[12]*1.0f;
		tempM[1] = MATRIX[1]*vec3[0] + MATRIX[5]*vec3[1] + MATRIX[9]*vec3[2] + MATRIX[13]*1.0f;
		tempM[2] = MATRIX[2]*vec3[0] + MATRIX[6]*vec3[1] + MATRIX[10]*vec3[2] + MATRIX[14]*1.0f;
	
		// write back result
		vec3[0] = tempM[0];
		vec3[1] = tempM[1];
		vec3[2] = tempM[2];
	}
	
	
	
	// rotation
	void rotate_Xdeg(float degree)
	{
		float radian = degree * 0.0174532925f;
		float cosVal = (float)Math.cos(radian);
		float sinVal = (float)Math.sin(radian);
		
		tempM[0] = 1.0f;	tempM[4] = 0.0f;	tempM[8] = 0.0f;		tempM[12] = 0.0f;
		tempM[1] = 0.0f;	tempM[5] = cosVal;	tempM[9] = -sinVal;		tempM[13] = 0.0f;
		tempM[2] = 0.0f;	tempM[6] = sinVal;	tempM[10] = cosVal;		tempM[14] = 0.0f;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;		tempM[15] = 1.0f;
	
		this.calculateMatrix(tempM);
	}
	void rotate_Ydeg(float degree)
	{
		float radian = degree * 0.0174532925f;
		float cosVal = (float)Math.cos(radian);
		float sinVal = (float)Math.sin(radian);
		
		tempM[0] = cosVal;	tempM[4] = 0.0f;	tempM[8] = sinVal;	tempM[12] = 0.0f;
		tempM[1] = 0.0f;	tempM[5] = 1.0f;	tempM[9] = 0.0f;	tempM[13] = 0.0f;
		tempM[2] = -sinVal;	tempM[6] = 0.0f;	tempM[10] = cosVal;	tempM[14] = 0.0f;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 0.0f;
	
		this.calculateMatrix(tempM);
	}
	void rotate_Zdeg(float degree)
	{
		float radian = degree * 0.0174532925f;
		float cosVal = (float)Math.cos(radian);
		float sinVal = (float)Math.sin(radian);
		
		tempM[0] = cosVal;	tempM[4] = -sinVal;	tempM[8] = 0.0f;	tempM[12] = 0.0f;
		tempM[1] = sinVal;	tempM[5] = cosVal;	tempM[9] = 0.0f;	tempM[13] = 0.0f;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = 1.0f;	tempM[14] = 0.0f;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
		
		this.calculateMatrix(tempM);
	}
	
	// translate
	void translate_XYZ(float tx, float ty, float tz)
	{
		tempM[0] = 1.0f;	tempM[4] = 0.0f;	tempM[8] = 0.0f;	tempM[12] = tx;
		tempM[1] = 0.0f;	tempM[5] = 1.0f;	tempM[9] = 0.0f;	tempM[13] = ty;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = 1.0f;	tempM[14] = tz;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
	
		this.calculateMatrix(tempM);
	}
	
	// scale
	void scale_XYZ( float sx, float sy, float sz)
	{
		tempM[0] = sx;		tempM[4] = 0.0f;	tempM[8] = 0.0f;	tempM[12] = 0.0f;
		tempM[1] = 0.0f;	tempM[5] = sy;		tempM[9] = 0.0f;	tempM[13] = 0.0f;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = sz;		tempM[14] = 0.0f;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
		
		this.calculateMatrix(tempM);
	}
	
	
	// view transformation
	void lookAt(float eyeX, float eyeY, float eyeZ,
				float viewX, float viewY, float viewZ,
				float headX, float headY, float headZ)
	{
		float normWeight;
		
		// Z axis is view-to-eye vector
		zVec[0] = eyeX - viewX;
		zVec[1] = eyeY - viewY;
		zVec[2] = eyeZ - viewZ;
		normWeight = (float) (1.0 / Math.sqrt(zVec[0]*zVec[0] + zVec[1]*zVec[1] + zVec[2]*zVec[2]));
	
		zVec[0] *= normWeight;
		zVec[1] *= normWeight;
		zVec[2] *= normWeight;
		
		// X axis is cross product of [zVec & headVec]
		xVec[0] = headY*zVec[2] - headZ*zVec[1];
		xVec[1] = headZ*zVec[0] - headX*zVec[2];
		xVec[2] = headX*zVec[1] - headY*zVec[0];
		normWeight = (float) (1.0/Math.sqrt(xVec[0]*xVec[0]+xVec[1]*xVec[1]+xVec[2]*xVec[2]));
	
		xVec[0] *= normWeight;
		xVec[1] *= normWeight;
		xVec[2] *= normWeight;
		
		// Y axis is cross product of [xVec & zVec]
		yVec[0] = zVec[1]*xVec[2] - zVec[2]*xVec[1];
		yVec[1] = zVec[2]*xVec[0] - zVec[0]*xVec[2];
		yVec[2] = zVec[0]*xVec[1] - zVec[1]*xVec[0];
		normWeight= (float) (1.0/Math.sqrt(yVec[0]*yVec[0] + yVec[1]*yVec[1] + yVec[2]*yVec[2]));
	
		yVec[0] *= normWeight;
		yVec[1] *= normWeight;
		yVec[2] *= normWeight;
		
		// create lookAt matrix
		tempM[0] = 1.0f;	tempM[4] = 0.0f;	tempM[8] = 0.0f;	tempM[12] = -eyeX;
		tempM[1] = 0.0f;	tempM[5] = 1.0f;	tempM[9] = 0.0f;	tempM[13] = -eyeY;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = 1.0f;	tempM[14] = -eyeZ;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
		this.calculateMatrix(tempM);
		
		tempM[0] = xVec[0];	tempM[4] = xVec[1];	tempM[8] = xVec[2];	tempM[12] = 0.0f;
		tempM[1] = yVec[0];	tempM[5] = yVec[1];	tempM[9] = yVec[2];	tempM[13] = 0.0f;
		tempM[2] = zVec[0];	tempM[6] = zVec[1];	tempM[10] = zVec[2];	tempM[14] = 0.0f;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
		this.calculateMatrix(tempM);
	}
	
	
	
	// perspective
	void perspective(float fovy, float aspectRatio, float near, float far)
	{
		float radian = fovy * 0.0174532825f;
		float COEF = (float) (1.0 / Math.tan(radian*0.5));
		float A = (near + far) / (near - far);
		float B = (2.0f * near * far) / (near - far);
		float C = COEF / aspectRatio;
		
		tempM[0] = C;		tempM[4] = 0.0f;	tempM[8] = 0.0f;	tempM[12] = 0.0f;
		tempM[1] = 0.0f;	tempM[5] = COEF;	tempM[9] = 0.0f;	tempM[13] = 0.0f;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = A;		tempM[14] = B;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = -1.0f;	tempM[15] = 0.0f;
	
		this.calculateMatrix(tempM);
	}
	
	// orthogonal
	void orthogonal(float leftVal, float rightVal, 
					float bottomVal, float topVal,
					float nearVal, float farVal)
	{
		float X = 2.0f / (rightVal - leftVal);
		float Y = 2.0f / (topVal - bottomVal);
		float Z = -2.0f / (farVal - nearVal);
		float O = -(rightVal + leftVal) / (rightVal - leftVal);
		float P = -(topVal + bottomVal) / (topVal - bottomVal);
		float Q = -(farVal + nearVal) / (farVal - nearVal);
		
		tempM[0] = X;		tempM[4] = 0.0f;	tempM[8] = 0.0f;	tempM[12] = O;
		tempM[1] = 0.0f;	tempM[5] = Y;		tempM[9] = 0.0f;	tempM[13] = P;
		tempM[2] = 0.0f;	tempM[6] = 0.0f;	tempM[10] = Z;		tempM[14] = Q;
		tempM[3] = 0.0f;	tempM[7] = 0.0f;	tempM[11] = 0.0f;	tempM[15] = 1.0f;
		this.calculateMatrix(tempM);
	}
	
} // class
