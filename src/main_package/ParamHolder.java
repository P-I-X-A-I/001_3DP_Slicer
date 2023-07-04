package main_package;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ParamHolder {

	// define num
	public static final float GL_WIN_WIDTH = 1024.0f;
	public static final float GL_WIN_HEIGHT = 768.0f;
	public static final float GL_WIN_RATIO = 1024.0f / 768.0f;
	public static final int A_X = 180;
	public static final int A_Y = 100;
	public static final int A_Z = 100;
	
	public static final int B_X = 310;
	public static final int B_Y = 310;
	public static final int B_Z = 350;
	
	public static final int C_X = 220;
	public static final int C_Y = 160;
	public static final int C_Z = 200;
	
	// selected printer
	public static int PRINTER_ID = 0;
	public static ArrayList<STL_Class> stl_Array = new ArrayList<STL_Class>();
	public static ArrayList<String> stl_Names = new ArrayList<String>();
	
	// selected STL
	public static int SELECTED_STL_ID = -1;
	
	// slice parameters
	public static double LAYER_HEIGHT = 0.3;
	public static int PERIMETERS = 2;
	public static int INFILL = 15;
	public static int INFILL_PATTERN = 0;
	public static boolean IS_SUPPORT = false;
	public static boolean IS_RAFT = false;
	public static int TEMPERATURE = 210;
	public static ArrayList<String> INFILL_STRING = new ArrayList<>(Arrays.asList("rectilinear", "3dhoneycomb", "gyroid"));
	
	//
	public static boolean isRender = true;
	
	// save path
	public static String save_gcode_path;
	
	public ParamHolder()
	{
		System.out.println("ParamHolder init");
	}
	
	public static void arrowKeyPressed(int code)
	{
		if( SELECTED_STL_ID != -1)
		{
			STL_Class targetSTL = stl_Array.get(SELECTED_STL_ID);
			
			switch( code )
			{
			case 0: // left
				targetSTL.shift_x -= 2.0;
				break;
			case 1: // up
				targetSTL.shift_y += 2.0;
				break;
			case 2: // right
				targetSTL.shift_x += 2.0;
				break;
			case 3:
				targetSTL.shift_y -= 2.0;
				break;
			case 100:
				targetSTL.shift_x -= 20.0;
				break;
			case 101:
				targetSTL.shift_y += 20.0;
				break;
			case 102:
				targetSTL.shift_x += 20.0;
				break;
			case 103:
				targetSTL.shift_y -= 20.0;
				break;
			}
		}
	}
	
	public static void create_ini_file()
	{
		StringBuilder ini_string = new StringBuilder();
		
		// add memo
		ini_string.append("#print setting \n");
		
		// layer height
		ini_string.append( String.format("layer_height = %.2f\n", LAYER_HEIGHT) );
		
		// common settings
		ini_string.append("first_layer_height = 0.3\n");
		ini_string.append("external_perimeter_speed = 55\n");
		ini_string.append("support_material_speed = 55\n");
		ini_string.append("support_material_interface_speed = 45\n");
		ini_string.append("gap_fill_speed = 30\n");
		ini_string.append("travel_speed = 120\n");
		
		ini_string.append("adaptive_slicing = 0\n");
		ini_string.append("adaptive_slicing_quality = 75%\n");
		ini_string.append("match_horizontal_surfaces = 0\n");
		ini_string.append(String.format("perimeters = %d\n", PERIMETERS));
		ini_string.append("spiral_vase = 0\n");
		ini_string.append("top_solid_layers = 3\n");
		ini_string.append("bottom_solid_layers = 3\n");
		ini_string.append("extra_perimeters = 1\n");
		ini_string.append("avoid_crossing_perimeters = 0\n");
		ini_string.append("thin_walls = 1\n");
		ini_string.append("seam_position = aligned\n");
		ini_string.append("external_perimeters_first = 0\n");
		
		ini_string.append(String.format("fill_density = %d%%\n", INFILL));
		ini_string.append(String.format("fill_pattern = %s\n", INFILL_STRING.get(INFILL_PATTERN)));
		ini_string.append("bottom_infill_pattern = rectilinear\n");
		ini_string.append("external_fill_pattern = rectilinear\n");
		ini_string.append("infill_every_layers = 1\n");
		ini_string.append("infill_only_where_needed = 0\n");
		ini_string.append("solid_infill_every_layers = 0\n");
		ini_string.append("fill_gaps = 1\n");
		ini_string.append("fill_angle = 135\n");
		ini_string.append("solid_infill_below_area = 5\n");
		ini_string.append("only_retract_when_crossing_perimeters = 1\n");
		ini_string.append("infill_first = 0\n");
		
		ini_string.append("skirts = 0\n");
		ini_string.append("skirt_distance = 5\n");
		ini_string.append("skirt_height = 1\n");
		ini_string.append("min_skirt_length = 0\n");
		ini_string.append("brim_width = 0\n");
		ini_string.append("brim_connections_width = 0\n");
		ini_string.append("interior_brim_width = 0\n");
		
		ini_string.append(String.format("support_material = %d\n", IS_SUPPORT));
		ini_string.append("support_material_threshold = 40\n");
		ini_string.append("overhangs = 1\n");
		ini_string.append("support_material_enforce_layers = 0\n");
		
		int num_raft = 0;
		int num_interface = 0;
		if( IS_RAFT )
		{
			num_raft = 7;
			num_interface = 4;
		}
		ini_string.append(String.format("raft_layers = %d\n", num_raft));
		ini_string.append("support_material_contact_distance = 0.2\n");
		ini_string.append("support_material_pattern = pillars\n"); // or "rectilinear"
		ini_string.append("support_material_spacing = 1.25\n");
		ini_string.append("support_material_angle = 135\n");
		ini_string.append(String.format("support_material_interface_layers = %d\n", num_interface));
		ini_string.append("support_material_interface_spacing = 0.8\n");
		ini_string.append("dont_support_bridges = 1\n");
		ini_string.append("support_material_buildplate_only = 0\n");
		ini_string.append("support_material_interface_extrusion_width = 0\n");
		
		ini_string.append("perimeter_acceleration = 0\n");
		ini_string.append("infill_acceleration = 0\n");
		ini_string.append("bridge_acceleration = 0\n");
		ini_string.append("first_layer_acceleration = 0\n");
		ini_string.append("default_acceleration = 0\n");
		ini_string.append("max_print_speed = 80\n");
		ini_string.append("max_volumetric_speed = 0\n");
		
		ini_string.append("perimeter_extruder = 1\n");
		ini_string.append("infill_extruder = 1\n");
		ini_string.append("solid_infill_extruder = 1\n");
		ini_string.append("support_material_extruder = 1\n");
		
		
		// printer id
		switch(PRINTER_ID)
		{
		case 1: // BIG 
			ini_string.append("perimeter_speed = 55\n");
			ini_string.append("small_perimeter_speed = 35\n");
			ini_string.append("infill_speed = 55\n");
			ini_string.append("solid_infill_speed = 50\n");
			ini_string.append("top_solid_infill_speed = 50\n");
			ini_string.append("bridge_speed = 55\n");
			ini_string.append("first_layer_speed = 40%\n");
			break;
		default: // other
			ini_string.append("perimeter_speed = 60\n");
			ini_string.append("small_perimeter_speed = 20\n");
			ini_string.append("infill_speed = 60\n");
			ini_string.append("solid_infill_speed = 60\n");
			ini_string.append("top_solid_infill_speed = 60\n");
			ini_string.append("bridge_speed = 65\n");
			ini_string.append("first_layer_speed = 30%\n");
			break;
		}
		
		//***************************************************************
		//****** filament setting ***************************************
		//***************************************************************

		
		//***************************************************************
		//****** machine setting ***************************************
		//***************************************************************
		
		
		// save inifile as tempolary file
		Path curPath = Paths.get("");
		String absolutePath = curPath.toAbsolutePath().toString();
		String ini_path = new String(absolutePath + "/temp.ini");
		
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(ini_path))) {
	            writer.write(ini_string.toString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
}
