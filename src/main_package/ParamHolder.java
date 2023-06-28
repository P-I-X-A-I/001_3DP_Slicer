package main_package;

import java.util.ArrayList;

public class ParamHolder {

	// define num
	public static final int A_X = 180;
	public static final int A_Y = 100;
	public static final int A_Z = 100;
	
	public static final int B_X = 310;
	public static final int B_Y = 310;
	public static final int B_Z = 350;
	
	public static final int C_X = 230;
	public static final int C_Y = 150;
	public static final int C_Z = 200;
	
	// selected printer
	public static int PRINTER_ID = 0;
	public static ArrayList<STL_Class> stl_Array = new ArrayList<STL_Class>();
	public static ArrayList<String> stl_Names = new ArrayList<String>();
	
	// selected STL
	public static int SELECTED_STL_ID = -1;
	
	public ParamHolder()
	{
		System.out.println("ParamHolder init");
	}
	
	public static void arrowKeyPressed(int code)
	{
		if( SELECTED_STL_ID != -1)
		{
			
		}
	}
}
