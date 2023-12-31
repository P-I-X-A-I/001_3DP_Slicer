package main_package;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL11;

public class GL_Manager implements Runnable{
	
	public long h_mainWnd;
	
	// class
	matrixClass mat_obj = new matrixClass();
	matrixClass lookMat_obj = new matrixClass();
	
	
	// shader
	int VS_LINE;
	int FS_LINE;
	int PRG_LINE;
	int UNF_single_mvpMatrix;
	int UNF_singleColor;
	
	int VS_STL;
	int FS_STL;
	int PRG_STL;
	int UNF_STL_mvpMatrix;
	int UNF_STL_singleColor;
	int UNF_STL_rotateMatrix;
	int UNF_STL_shiftPos;
	
	// VAO VBO
	int VAO_LINE;
	int VBO_LINE;
	int VAO_STL;
	int VBO_STL_VERT;
	int VBO_STL_NORM;
	
	// bed grid
	//int num_grid_line;
	//float[] bed_A;
	//float[] bed_B;
	//float[] bed_C;
	//float[] draw_bed_v;
	float[] bed_v = new float[5000];// enough large array
	int num_bed_line;
	float bed_x_lim = 0.0f;
	float bed_y_lim = 0.0f;
	float bed_z_lim = 0.0f;
	
	// view point
	float[] eyeVec = new float[3];
	float[] targetVec = new float[3];
	float[] headVec = new float[3];
	float rot_X = -45.0f;
	float rot_Z = 0.0f;
	float acc_rot_X = 0.0f;
	float acc_rot_Z = 0.0f;
	float eyeLength = 300.0f;
	float acc_eyeLength = 0.0f;
	float prev_mouse_X = 0.0f;
	float prev_mouse_Y = 0.0f;
	boolean isMouseDown = false;
	boolean isFirstMouse = false;
	
	public GL_Manager() // constructor
	{
		System.out.println("GL Manager init");
		/*
		// setup bed lines;
		int iterX = ParamHolder.A_X / 10;
		int iterY = ParamHolder.A_Y / 10;
		int numVert = (iterX+1)*2 + (iterY+1)*2 + 2;
		float endX = (float)(ParamHolder.A_X)/2.0f;
		float endY = (float)(ParamHolder.A_Y)/2.0f;
		int ID = 0;
		// alloc array
		bed_A = new float[numVert*3];
		
		for( int x = 0 ; x < iterX+1 ; x++ )
		{
			float pX = (x*10.0f) - endX;
			System.out.println(pX);
			bed_A[ID] = pX; 	ID++; // x1
			bed_A[ID] = endY;	ID++; // y1
			bed_A[ID] = 0.0f;	ID++; // z1
			
			bed_A[ID] = pX;		ID++; // x2
			bed_A[ID] = -endY;	ID++; // y2
			bed_A[ID] = 0.0f;	ID++; // z2
		}
		for( int y = 0 ; y < iterY+1 ; y++ )
		{
			float pY = (y*10.0f) - endY;
			bed_A[ID] = endX;	ID++; // x1
			bed_A[ID] = pY;		ID++; // y1
			bed_A[ID] = 0.0f;	ID++; // z1
			
			bed_A[ID] = -endX;	ID++; // x2
			bed_A[ID] = pY;		ID++; // y2
			bed_A[ID] = 0.0f;	ID++; // z2
		}
		
		// add origin pole
		bed_A[ID] = -endX;	ID++;
		bed_A[ID] = -endY;	ID++;
		bed_A[ID] = 0.0f;	ID++;
		
		bed_A[ID] = -endX;	ID++;
		bed_A[ID] = -endY;	ID++;
		bed_A[ID] = (float)ParamHolder.A_Z;	ID++;
		
		
		/// B
		iterX = ParamHolder.B_X / 10;
		iterY = ParamHolder.B_Y / 10;
		numVert = (iterX+1)*2 + (iterY+1)*2 + 2;
		endX = (float)(ParamHolder.B_X)/2.0f;
		endY = (float)(ParamHolder.B_Y)/2.0f;
		ID = 0;
		
		// alloc memory
		bed_B = new float[numVert*3];
		
		for( int x = 0 ; x < iterX+1 ; x++ )
		{
			float pX = (x*10.0f) - endX;
			System.out.println(pX);
			bed_B[ID] = pX; 	ID++; // x1
			bed_B[ID] = endY;	ID++; // y1
			bed_B[ID] = 0.0f;	ID++; // z1
			
			bed_B[ID] = pX;		ID++; // x2
			bed_B[ID] = -endY;	ID++; // y2
			bed_B[ID] = 0.0f;	ID++; // z2
		}
		for( int y = 0 ; y < iterY+1 ; y++ )
		{
			float pY = (y*10.0f) - endY;
			bed_B[ID] = endX;	ID++; // x1
			bed_B[ID] = pY;		ID++; // y1
			bed_B[ID] = 0.0f;	ID++; // z1
			
			bed_B[ID] = -endX;	ID++; // x2
			bed_B[ID] = pY;		ID++; // y2
			bed_B[ID] = 0.0f;	ID++; // z2
		}
		
		// add origin pole
		bed_B[ID] = -endX;	ID++;
		bed_B[ID] = -endY;	ID++;
		bed_B[ID] = 0.0f;	ID++;
		
		bed_B[ID] = -endX;	ID++;
		bed_B[ID] = -endY;	ID++;
		bed_B[ID] = (float)ParamHolder.B_Z;	ID++;
		
		/// C
		iterX = ParamHolder.C_X / 10;
		iterY = ParamHolder.C_Y / 10;
		numVert = (iterX+1)*2 + (iterY+1)*2 + 2;
		endX = (float)(ParamHolder.C_X)/2.0f;
		endY = (float)(ParamHolder.C_Y)/2.0f;
		ID = 0;
		
		// alloc memory
		bed_C = new float[numVert*3];
		
		for( int x = 0 ; x < iterX+1 ; x++ )
		{
			float pX = (x*10.0f) - endX;
			System.out.println(pX);
			bed_C[ID] = pX; 	ID++; // x1
			bed_C[ID] = endY;	ID++; // y1
			bed_C[ID] = 0.0f;	ID++; // z1
			
			bed_C[ID] = pX;		ID++; // x2
			bed_C[ID] = -endY;	ID++; // y2
			bed_C[ID] = 0.0f;	ID++; // z2
		}
		for( int y = 0 ; y < iterY+1 ; y++ )
		{
			float pY = (y*10.0f) - endY;
			bed_C[ID] = endX;	ID++; // x1
			bed_C[ID] = pY;		ID++; // y1
			bed_C[ID] = 0.0f;	ID++; // z1
			
			bed_C[ID] = -endX;	ID++; // x2
			bed_C[ID] = pY;		ID++; // y2
			bed_C[ID] = 0.0f;	ID++; // z2
		}
		// add origin pole
		bed_C[ID] = -endX;	ID++;
		bed_C[ID] = -endY;	ID++;
		bed_C[ID] = 0.0f;	ID++;
		
		bed_C[ID] = -endX;	ID++;
		bed_C[ID] = -endY;	ID++;
		bed_C[ID] = (float)ParamHolder.C_Z;	ID++;
		*/
	}
	
	
	private void loop()
	{
		// get time
		long prevTime = System.currentTimeMillis();
		
		while(!(GLFW.glfwWindowShouldClose(h_mainWnd)))
		{
			// set fps
			long curTime = System.currentTimeMillis();
			long diff = curTime - prevTime; // if over 33ms, do something
			
			//**************************************************
			//**************************************************
			
			// set viewpoint & bed v pointer
			this.CHECK_PRINTER();

			if( diff > 33 && ParamHolder.isRender )
			{
				// clear buffer ******************************
				GL46.glViewport(0, 0, (int)ParamHolder.GL_WIN_WIDTH, (int)ParamHolder.GL_WIN_HEIGHT);
				GL46.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
				GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
			
				// init matrix ( view point ) ****************
				this.SET_VIEW_POINT();

				// perspective matrix setup ******************
				mat_obj.initMatrix();
				mat_obj.lookAt(eyeVec[0], eyeVec[1], eyeVec[2], 
								0.0f, 0.0f, targetVec[2], 
								headVec[0], headVec[1], headVec[2]);
				mat_obj.perspective(75.0f,
									ParamHolder.GL_WIN_RATIO, 0.01f, 1000.0f);
				
				// change shader *****************************
				this.DRAW_GRID_LINE();

				
				// draw STL file *****************************
				GL46.glUseProgram(PRG_STL);
				GL46.glUniformMatrix4fv(UNF_STL_mvpMatrix, false, mat_obj.getMatrix());
				GL46.glUniformMatrix4fv(UNF_STL_rotateMatrix, false, lookMat_obj.getMatrix());
				
				this.draw_STLs();
				
	
				
				// finish command & swap buffer
				GL46.glFlush();
				GLFW.glfwSwapBuffers(h_mainWnd);

				// set time
				prevTime = curTime;
				
			} // is render
			
			//*****************************************************
			//*****************************************************
			
			GLFW.glfwPollEvents();

		}//while
	}

	
	// each process **************************************************
	void CHECK_PRINTER()
	{
		// get printer ID
		int PRINTER_ID = ParamHolder.PRINTER_ID;
		
		// get bed size
		int BED_X = ParamHolder.PRNT_X[PRINTER_ID];
		int BED_Y = ParamHolder.PRNT_Y[PRINTER_ID];
		int BED_Z = ParamHolder.PRNT_Z[PRINTER_ID];
		
		int NUM_X_LINE = (BED_X/10) + 1;
		int NUM_Y_LINE = (BED_Y/10) + 1;
		num_bed_line = NUM_X_LINE + NUM_Y_LINE + 1;
		
		// set grid vertex *******************************
		// x direction
		int ID = 0;
		float sX = (float)(BED_X/2);
		float sY = (float)(BED_Y/2);
		// x line
		for( int lx = 0 ; lx < NUM_X_LINE; lx++ )
		{
			bed_v[ID] = sX - (10.0f*lx); 	ID++; // x0
			bed_v[ID] = sY;					ID++; // y0
			bed_v[ID] = 0.0f;				ID++; // z0
			
			bed_v[ID] = sX - (10.0f*lx);	ID++; // x1
			bed_v[ID] = -sY;				ID++; // y1
			bed_v[ID] = 0.0f;				ID++; // z1
		}
		// y line
		for(int ly = 0 ; ly < NUM_Y_LINE ; ly++ )
		{
			bed_v[ID] = sX;					ID++; // x0
			bed_v[ID] = sY - (10.0f*ly);	ID++; // y0
			bed_v[ID] = 0.0f;				ID++; // z0
			
			bed_v[ID] = -sX;				ID++;
			bed_v[ID] = sY - (10.0f*ly);	ID++;
			bed_v[ID] = 0.0f;				ID++;
		}
		// origin line
		bed_v[ID] = -sX;	ID++;
		bed_v[ID] = -sY;	ID++;
		bed_v[ID] = 0.0f;	ID++;
		
		bed_v[ID] = -sX;	ID++;
		bed_v[ID] = -sY;	ID++;
		bed_v[ID] = (float)BED_Z; ID++;
		//*************************************************
		
		// setup eye target vec ***************************
		targetVec[0] = 0.0f;
		targetVec[1] = 0.0f;
		targetVec[2] = (float)(BED_Z/4);
		//*************************************************
		
		// set bed limitation *****************************
		bed_x_lim = BED_X / 2.0f;
		bed_y_lim = BED_Y / 2.0f;
		bed_z_lim = (float)BED_Z;
		//*************************************************
		
		/*
		switch(PRINTER_ID)
		{
		case 0:
			targetVec[0] = 0.0f;
			targetVec[1] = 0.0f;
			targetVec[2] = (float)(ParamHolder.A_Z/4);
			//draw_bed_v = bed_A;
			//bed_x = ParamHolder.A_X / 2.0f;
			//bed_y = ParamHolder.A_Y / 2.0f;
			//bed_z = (float)ParamHolder.A_Z;
			break;
		case 1:
			targetVec[0] = 0.0f;
			targetVec[1] = 0.0f;
			targetVec[2] = (float)(ParamHolder.B_Z/4);
			//draw_bed_v = bed_B;
			//bed_x = ParamHolder.B_X / 2.0f;
			//bed_y = ParamHolder.B_Y / 2.0f;
			//bed_z = (float)ParamHolder.B_Z;
			break;
		case 2:
			targetVec[0] = 0.0f;
			targetVec[1] = 0.0f;
			targetVec[2] = (float)(ParamHolder.C_Z/4);
			//draw_bed_v = bed_C;
			//bed_x = ParamHolder.C_X / 2.0f;
			//bed_y = ParamHolder.C_Y / 2.0f;
			//bed_z = (float)ParamHolder.C_Z;
			break;
		default:
			
			break;
		}
		*/
	}
	
	//*************************************************************
	
	void SET_VIEW_POINT()
	{
		acc_eyeLength *= 0.5;
		eyeLength += acc_eyeLength; // 100 ~ 500
		if( eyeLength < 100.0f) { eyeLength = 100.0f; }
		else if( eyeLength > 500.0f) { eyeLength = 500.0f; }
		
		eyeVec[0] = 0.0f;	eyeVec[1] = -eyeLength;		eyeVec[2] = 0.0f;
		headVec[0] = 0.0f;	headVec[1] = 0.0f;	headVec[2] = 1.0f;
		
		acc_rot_X *= 0.6f;
		acc_rot_Z *= 0.6f;
		rot_X += acc_rot_X;
		rot_Z += acc_rot_Z;
		
		lookMat_obj.initMatrix();
		lookMat_obj.rotate_Xdeg(rot_X);
		lookMat_obj.rotate_Zdeg(rot_Z);
		lookMat_obj.calculateVec3(eyeVec);
		lookMat_obj.calculateVec3(headVec);
	}
	
	//******************************************************
	
	void DRAW_GRID_LINE()
	{
		GL46.glUseProgram(PRG_LINE);
		GL46.glUniform4f(UNF_singleColor, 0.0f, 0.0f, 0.0f, 1.0f);
		GL46.glUniformMatrix4fv(UNF_single_mvpMatrix, false, mat_obj.getMatrix());
		
		// update buffer ( LINE )
		GL46.glBindVertexArray(VAO_LINE);
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO_LINE);
		//GL46.glBufferData(GL46.GL_ARRAY_BUFFER, draw_bed_v, GL46.GL_DYNAMIC_DRAW);
		GL46.glBufferData(GL46.GL_ARRAY_BUFFER, bed_v, GL46.GL_DYNAMIC_DRAW);
		GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);
		
		//GL46.glDrawArrays(GL46.GL_LINES, 0, draw_bed_v.length/3);
		GL46.glDrawArrays(GL46.GL_LINES, 0, num_bed_line*2);
	}
	
	//*********************************************************
	
	private void draw_STLs()
	{
		// get num STL
		int numSTL = ParamHolder.stl_Array.size();
		for( int s = 0 ; s < numSTL ; s++ )
		{
			STL_Class targetSTL = ParamHolder.stl_Array.get(s);
			
			if(targetSTL.isAccesible)
			{
			
			// check if in range ************************************
			boolean isInRange = true;
			
			// x range
			if( bed_x_lim <= (targetSTL.bound_max_x + targetSTL.shift_x))
			{ isInRange = false; }
			else if( (-bed_x_lim) > (targetSTL.bound_min_x + targetSTL.shift_x))
			{ isInRange = false;}
			
			// y range
			if( bed_y_lim <= (targetSTL.bound_max_y + targetSTL.shift_y))
			{ isInRange = false; }
			else if((-bed_y_lim) > (targetSTL.bound_min_y + targetSTL.shift_y))
			{ isInRange = false; }
			
			// z range
			if(bed_z_lim <= (targetSTL.bound_max_z - targetSTL.bound_min_z))
			{ isInRange = false;}
			
			//*******************************************************
			
			// set color
			if( s == ParamHolder.SELECTED_STL_ID)
			{
				GL46.glUniform4f(UNF_STL_singleColor, 1.0f, 0.2f, 1.0f, 1.0f);
			}
			else
			{
				GL46.glUniform4f(UNF_STL_singleColor, 0.2f, 1.0f, 1.0f, 1.0f);
			}
			
			if(isInRange == false)
			{
				GL46.glUniform4f(UNF_STL_singleColor, 0.2f, 0.2f, 0.2f, 1.0f);
			}

			
			
			// set shift z

			GL46.glUniform3f(UNF_STL_shiftPos,
								targetSTL.shift_x,
								targetSTL.shift_y,
								targetSTL.shift_z);
			
			
			GL46.glBindVertexArray(VAO_STL);
			// update vert

			GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO_STL_VERT);
			GL46.glBufferData(GL46.GL_ARRAY_BUFFER, targetSTL.vert_cp, GL46.GL_DYNAMIC_DRAW);
			GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);
			// update norm
			GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO_STL_NORM);
			GL46.glBufferData(GL46.GL_ARRAY_BUFFER, targetSTL.norm_cp, GL46.GL_DYNAMIC_DRAW);
			GL46.glVertexAttribPointer(1, 3, GL46.GL_FLOAT, false, 0, 0);
			
			GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, targetSTL.num_of_triangles * 3);
			
			} // is accesible ( as fence )
		} // for s
	}
	
	
	//*********************************************************
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("GL_Manager run");
		System.out.println("LWJGL version:"+Version.getVersion());
		
		// init GLFW
		if(GLFW.glfwInit())
		{
			System.out.println("GLFW init SUCCESS");
		}
		else
		{
			System.out.println("GLFW init fail... return");
			return;
		}
		
		// configure window hint
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
		
		// create window
		h_mainWnd = GLFW.glfwCreateWindow((int)ParamHolder.GL_WIN_WIDTH,
											(int)ParamHolder.GL_WIN_HEIGHT,
											"3DP-Slicer", 0, 0);
		
		// set key callback********************************************
		GLFW.glfwSetKeyCallback(h_mainWnd, (window, key, scancode, action, mods)->{
			
			// define key event
			System.out.println("GL key"+key+"/"+mods + "/" + action);
			// mods == 1, shift pushed
			// left = 263
			// up = 265
			// right = 262
			// down = 264
			int shiftMods = 0;
			if( mods == 1 )
			{
				shiftMods = 100;
			}
			
			if( action == 1 ) // when pushed
			{
				switch(key)
				{
				case 263:
					ParamHolder.arrowKeyPressed(0 + shiftMods);
					break;
				case 265:
					ParamHolder.arrowKeyPressed(1 + shiftMods);
					break;
				case 262:
					ParamHolder.arrowKeyPressed(2 + shiftMods);
					break;
				case 264:
					ParamHolder.arrowKeyPressed(3 + shiftMods);
					break;
				}
			}
			
			if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
			{
				GLFW.glfwSetWindowShouldClose(h_mainWnd, true);
			}
		}); 
		
		// set close callback *******************************************
		GLFW.glfwSetWindowCloseCallback(h_mainWnd, (window)->{
			
			GLFW.glfwDestroyWindow(h_mainWnd);
			GLFW.glfwTerminate();
			System.exit(0);
		});
		
		// set mouse button callback ***********************************
		GLFW.glfwSetMouseButtonCallback(h_mainWnd, (window, button, action, mods)->{
			
			//System.out.println(button +"/"+ action +"/"+ mods);
			// action 1 = mouse down, action 0 = mouse up
			if( action == 1 )
			{
				isMouseDown = true;
				isFirstMouse = true;
			}
			else if(action == 0)
			{
				isMouseDown = false;
			}
		});
		
		// set cursor position callback *******************************
		GLFW.glfwSetCursorPosCallback(h_mainWnd, (window, xpos, ypos)->{
			
			if( isMouseDown && isFirstMouse )
			{
				// set prevPoint
				prev_mouse_X = (float)xpos;
				prev_mouse_Y = (float)ypos;
				isFirstMouse = false;
			}
			else if( isMouseDown && !isFirstMouse)
			{
				// calcurate delta
				float deltaX = prev_mouse_X - (float)xpos;
				float deltaY = prev_mouse_Y - (float)ypos;
				//System.out.println(deltaX + "/" + deltaY);
				
				// rotate view axis
				acc_rot_X += deltaY * 0.15f;
				acc_rot_Z += deltaX * 0.2f;
				
				// reset prev
				prev_mouse_X = (float)xpos;
				prev_mouse_Y = (float)ypos;
				
				//System.out.println(rot_X + "/" + rot_Z);
			}
			
		});
		
		// set cursor enter callback ***********************************
		GLFW.glfwSetCursorEnterCallback(h_mainWnd, (window, entered)->{
			//System.out.println("Entered/"+entered);
		});
		
		// set scroll callback
		GLFW.glfwSetScrollCallback(h_mainWnd, (window, xoffset, yoffset)->{
			
			//System.out.println("x"+xoffset+"Y"+yoffset);
			acc_eyeLength -= (float)yoffset*10.0f;
		});
		
		//GLFW.glfwSetWindowRefreshCallback(h_mainWnd, (window)->{});
		//GLFW.glfwSetFramebufferSizeCallback(window, cbfun)
		
		// set window position
		GLFW.glfwSetWindowPos(h_mainWnd, 700, 200);
		// make context current
		GLFW.glfwMakeContextCurrent(h_mainWnd);
		// enable v-sync
		GLFW.glfwSwapInterval(1);
		// make window visible
		GLFW.glfwShowWindow(h_mainWnd);
		
		// *******************************************
		// critically iportant to use openGL function
		GL.createCapabilities();
		// *******************************************
	
		//**********************
		this.check_opengl_status();
		this.setup_status();
		this.setup_sampler();
		this.setup_shader();
		this.setup_VAO_VBO();
		//**********************
		
		// run loop
		this.loop();
	}
	
	
	void check_opengl_status()
	{
		System.out.println("VERSION:"+GL46.glGetString(GL46.GL_VERSION));
		System.out.println("RENDERER:"+GL46.glGetString(GL46.GL_RENDERER));
		System.out.println("GLSL:"+GL46.glGetString(GL46.GL_SHADING_LANGUAGE_VERSION));
		
		int val;
		val = GL46.glGetInteger(GL46.GL_MAX_VERTEX_ATTRIBS);
		System.out.println("max_vertex_attribs:"+val);
		
		val = GL46.glGetInteger(GL46.GL_MAX_VARYING_VECTORS);
		System.out.println("max_varying_vectors:"+val);
	}
	
	
	void setup_status()
	{
		// multisample
		GL46.glEnable(GL46.GL_MULTISAMPLE);
		GL46.glEnable(GL46.GL_SAMPLE_ALPHA_TO_COVERAGE);
		
		// blend
		GL46.glEnable(GL46.GL_BLEND);
		GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
		
		// cullface
		GL46.glEnable(GL46.GL_CULL_FACE);
		GL46.glCullFace(GL46.GL_BACK);
		
		// depth test
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		GL46.glDepthFunc(GL46.GL_LESS);
		
		// antialiasing
		GL46.glEnable(GL46.GL_POLYGON_SMOOTH);
		GL46.glEnable(GL46.GL_LINE_SMOOTH);
		
		// point size
		GL46.glEnable(GL46.GL_PROGRAM_POINT_SIZE);
	}
	
	
	void setup_sampler()
	{
		int sampID = GL46.glGenSamplers();
		GL46.glSamplerParameteri(sampID, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
		GL46.glSamplerParameteri(sampID, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
		GL46.glSamplerParameteri(sampID, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
		GL46.glSamplerParameteri(sampID, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR);
		
		int numTex = GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
		System.out.println("num texture unit : " + numTex);
		
		for(int i = 0 ; i < numTex ; i++ )
		{
			GL46.glBindSampler(i,  sampID);
		}
	}
	
	
	void setup_shader()
	{
		VS_LINE = ShaderClass.loadShaderSource_And_Compile("SHADER/VS_SINGLE.txt", 0);
		FS_LINE = ShaderClass.loadShaderSource_And_Compile("SHADER/FS_SINGLE.txt", 2);
		PRG_LINE = ShaderClass.createProgram_And_AttachShader(VS_LINE, -1, FS_LINE);
		UNF_single_mvpMatrix = ShaderClass.getUniformLocation(PRG_LINE, "mvpMatrix");
		UNF_singleColor = ShaderClass.getUniformLocation(PRG_LINE, "singleColor");
	
		VS_STL = ShaderClass.loadShaderSource_And_Compile("SHADER/VS_STL.txt", 0);
		FS_STL = ShaderClass.loadShaderSource_And_Compile("SHADER/FS_STL.txt", 2);
		PRG_STL = ShaderClass.createProgram_And_AttachShader(VS_STL, -1, FS_STL);
		UNF_STL_mvpMatrix = ShaderClass.getUniformLocation(PRG_STL, "mvpMatrix");
		UNF_STL_singleColor = ShaderClass.getUniformLocation(PRG_STL, "singleColor");
		UNF_STL_rotateMatrix = ShaderClass.getUniformLocation(PRG_STL, "rotateMatrix");
		UNF_STL_shiftPos = ShaderClass.getUniformLocation(PRG_STL, "shiftPos");
	}
	
	
	void setup_VAO_VBO()
	{
		// for bed grid line
		VAO_LINE = GL46.glGenVertexArrays();
		VBO_LINE = GL46.glGenBuffers();
		
		GL46.glBindVertexArray(VAO_LINE);
		GL46.glEnableVertexAttribArray(0); // vertex
		
		// for STL objects
		VAO_STL = GL46.glGenVertexArrays();
		VBO_STL_VERT = GL46.glGenBuffers();
		VBO_STL_NORM = GL46.glGenBuffers();
		
		GL46.glBindVertexArray(VAO_STL);
		GL46.glEnableVertexAttribArray(0);
		GL46.glEnableVertexAttribArray(1);
		
		GL46.glBindVertexArray(0);
	}
	
}// class
