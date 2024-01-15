package main_package;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ParamHolder {

	// define num
	public static final float GL_WIN_WIDTH = 1024.0f;
	public static final float GL_WIN_HEIGHT = 768.0f;
	public static final float GL_WIN_RATIO = 1024.0f / 768.0f;
	
	/*
	public static final int A_X = 180;
	public static final int A_Y = 110;
	public static final int A_Z = 100;
	
	public static final int B_X = 310;
	public static final int B_Y = 310;
	public static final int B_Z = 350;
	
	public static final int C_X = 220;
	public static final int C_Y = 160;
	public static final int C_Z = 200;
	*/
	
	public static int PRNT_NUM = 1;
	public static String[] PRNT_NAME = new String[128];
	public static int[] PRNT_X = new int[128];
	public static int[] PRNT_Y = new int[128];
	public static int[] PRNT_Z = new int[128];
	public static float[] PRNT_NZL = new float[128];
	public static boolean[] PRNT_LOOP = new boolean[128];
	
	// selected printer
	public static int PRINTER_ID = 0;
	public static ArrayList<STL_Class> stl_Array = new ArrayList<STL_Class>();
	public static ArrayList<String> stl_Names = new ArrayList<String>();
	
	// selected STL
	public static int SELECTED_STL_ID = -1;
	
	// slice parameters
	public static double LAYER_HEIGHT = 0.3;
	public static int PERIMETERS = 2;
	public static int INFILL = 10;
	public static int INFILL_PATTERN = 0;
	public static boolean IS_SUPPORT = false;
	public static boolean IS_RAFT = false;
	public static int TEMPERATURE = 210;
	public static ArrayList<String> INFILL_STRING = new ArrayList<>(Arrays.asList("rectilinear", "3dhoneycomb", "gyroid"));
	
	public static boolean IS_ADVANCED = false;
	public static boolean IS_LOOP = false;
	public static int NUM_LOOP = 1;
	public static boolean IS_ADAPTIVE_SLICE = false;
	public static int ADAPTIVE_QUALITY = 50;
	public static boolean IS_FFL = false;
	public static boolean IS_CHT_NOZZLE = false;
	public static int VOLUMETRIC_SPEED = 10;
	
	//
	public static boolean isRender = true;
	
	// save path
	public static String save_gcode_path;
	public static String merged_STL_path;
	public static String ini_file_path;
	
	public ParamHolder()
	{
		System.out.println("ParamHolder init");
	}
	
	public static void load_setting_file()
	{
		System.out.println("ParamHolder:load_setting_file");
		
		// get current path
		Path curPath = Paths.get("");
		String absolutePath = curPath.toAbsolutePath().toString();
		String printer_ini_path = new String(absolutePath + "/printers.ini");
		
		System.out.println(printer_ini_path);
		
		// get contents of "printers.ini"
		String p_settings = new String();;
		try {
			p_settings = Files.readString(Paths.get(printer_ini_path));
		}
		catch(Exception e) { e.printStackTrace();}
		
		// parse printer settings
		parse_printer_settings(p_settings);
		
		
	}//**************************************************************
	
	
	public static void parse_printer_settings(String stngs)
	{
		try {
			String[] params = stngs.split(";");
			int numElem = params.length;
			
			// the first one is must be num of printer
			String[] numPrnt = params[0].split(":");
			PRNT_NUM = Integer.parseInt(numPrnt[1]);

			// counter
			int INDEX = -1;
			
			for( int i = 1 ; i < numElem ; i++)
			{
				// "\n" is included in this array
				String[] eachElem = params[i].split(":");
				
				if(eachElem.length == 1) {} // this is \n
				else
				{
					//eachElem[0] is dummy :
					//eachElem[1] is "param name"
					//eachElem[2] is value
					if(eachElem[1].equals("name"))
					{
						INDEX++;
						String pName = eachElem[2];
						PRNT_NAME[INDEX] = pName;
					}
					else if(eachElem[1].equals("bx"))
					{
						int bedX = Integer.parseInt(eachElem[2]);
						PRNT_X[INDEX] = bedX;
					}
					else if(eachElem[1].equals("by"))
					{
						int bedY = Integer.parseInt(eachElem[2]);
						PRNT_Y[INDEX] = bedY;
					}
					else if(eachElem[1].equals("bz"))
					{
						int bedZ = Integer.parseInt(eachElem[2]);
						PRNT_Z[INDEX] = bedZ;
					}
					else if(eachElem[1].equals("nozzle"))
					{
						float nozDir = Float.parseFloat(eachElem[2]);
						PRNT_NZL[INDEX] = nozDir;
					}
					else if(eachElem[1].equals("loop"))
					{
						int yn = Integer.parseInt(eachElem[2]);
						if(yn == 0) { PRNT_LOOP[INDEX] = false;}
						else if(yn == 1) {PRNT_LOOP[INDEX] = true;}
					}
				}
			}// for
			
			for( int i = 0 ; i < PRNT_NUM ; i++ )
			{
				System.out.println(PRNT_NAME[i]);
				System.out.println(PRNT_X[i]);
				System.out.println(PRNT_Y[i]);
				System.out.println(PRNT_Z[i]);
				System.out.println(PRNT_NZL[i]);
			}
		}//try
		catch(Exception e) {e.printStackTrace();}
		
		
	}//*******************************************************
	
	
	
	
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
		// CONDITIONAL ::::::::::::::::::::::::::::::::::::
		// Thicker layer can adhere to bed strongly
		float FFL_height = PRNT_NZL[PRINTER_ID]*0.4f;
		float first_height_start_gcode = 0.0f;
		if(IS_ADVANCED && IS_FFL)
		{
			// depend on nozzle dir
			ini_string.append(String.format("first_layer_height = %.2f\n", FFL_height));
			ini_string.append("first_layer_speed = 22\n");
			first_height_start_gcode = FFL_height; // use later
		}
		else
		{
			ini_string.append("first_layer_height = 0.3\n");
			ini_string.append("first_layer_speed = 22\n");
			first_height_start_gcode = 0.3f; // use later
		}
		//-------------------------------------------------
		

		
		// CONDITIONAL :::::::::::::::::::::::::::::::::::::
		if(IS_ADVANCED && IS_ADAPTIVE_SLICE)
		{
			ini_string.append("adaptive_slicing = 1\n");
			ini_string.append(String.format("adaptive_slicing_quality = %d%%\n", ADAPTIVE_QUALITY));
		}
		else
		{
			ini_string.append("adaptive_slicing = 0\n");
			ini_string.append("adaptive_slicing_quality = 0%\n");
		}
		//--------------------------------------------------
		ini_string.append("match_horizontal_surfaces = 0\n");
		
		ini_string.append(String.format("perimeters = %d\n", PERIMETERS));
		ini_string.append("spiral_vase = 0\n");
		// CONDITIONAL :::::::::::::::::::::::::::::::::::::
		ini_string.append("top_solid_layers = 3\n");
		ini_string.append("bottom_solid_layers = 3\n");
		//--------------------------------------------------
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
		ini_string.append("solid_infill_below_area = 1\n");
		ini_string.append("only_retract_when_crossing_perimeters = 1\n");
		ini_string.append("infill_first = 0\n");
		
		ini_string.append("skirts = 0\n");
		ini_string.append("skirt_distance = 5\n");
		ini_string.append("skirt_height = 1\n");
		ini_string.append("min_skirt_length = 0\n");
		ini_string.append("brim_width = 0\n");
		ini_string.append("brim_connections_width = 0\n");
		ini_string.append("interior_brim_width = 0\n");
		
		if(IS_SUPPORT)
		{
			ini_string.append("support_material = 1\n");
		}
		else
		{
			ini_string.append("support_material = 0\n");
		}

		ini_string.append("support_material_threshold = 40\n");
		ini_string.append("overhangs = 1\n");
		ini_string.append("support_material_enforce_layers = 0\n");
		
		
		// CONDITIONAL :::::::::::::::::::::
		int num_raft = 0;
		int num_interface = 0;
		if( IS_RAFT )
		{
			num_raft = 7;
			num_interface = 4;
		}
		else
		{
			if(IS_ADVANCED && IS_FFL)
			{
				num_raft = 1;
				num_interface = 1;
			}
		}
		//-----------------------------------
		
		ini_string.append(String.format("raft_layers = %d\n", num_raft));
		ini_string.append("support_material_contact_distance = 0\n");
		ini_string.append("support_material_pattern = pillars\n"); // or "rectilinear"
		ini_string.append("support_material_spacing = 2.75\n");
		ini_string.append("support_material_angle = 120\n");
		ini_string.append(String.format("support_material_interface_layers = %d\n", num_interface));
		ini_string.append("support_material_interface_spacing = 1.2\n");
		ini_string.append("dont_support_bridges = 1\n");
		ini_string.append("support_material_buildplate_only = 0\n");
		///////
		///////
		///////
		ini_string.append("perimeter_acceleration = 0\n");
		ini_string.append("infill_acceleration = 0\n");
		ini_string.append("bridge_acceleration = 0\n");
		ini_string.append("first_layer_acceleration = 0\n");
		ini_string.append("default_acceleration = 0\n");
		ini_string.append("max_print_speed = 80\n");
		ini_string.append("max_volumetric_speed = 0\n");
		
		//# multiple extruder settings----------------------
		ini_string.append("perimeter_extruder = 1\n");
		ini_string.append("infill_extruder = 1\n");
		ini_string.append("solid_infill_extruder = 1\n");
		ini_string.append("support_material_extruder = 1\n");
		ini_string.append("support_material_interface_extruder = 1\n");
		ini_string.append("ooze_prevention = 0\n");
		ini_string.append("standby_temperature_delta = -5\n");
		ini_string.append("interface_shells = 0\n");
		
		// WIDTH SETTINGS ****************************************
		//********************************************************
		//# advanced settings-------------------------------
		ini_string.append("extrusion_width = 0\n");
		
		// CONDITIONAL :::::::::::::::::::::::::::::::::::::::::::
		float nzl_value = PRNT_NZL[PRINTER_ID];
		//float f_layer_coef = 1.0f; // currently, first layer is fixed in 0.3mm.
		//double normal_layer_coef = LAYER_HEIGHT / 0.3; // 0.2mm -> 0.66, 0.36mm -> 1.2, 
		//float finalWidth = nzl_value * f_layer_coef;
		
		double first_width, infill_width, perimeter_width, solid_width, top_width; // used to calculate speed
		// set val ***********
		first_width = nzl_value * 1.5;
		infill_width = nzl_value * 1.35;
		solid_width = nzl_value * 1.35;
		top_width = nzl_value * 1.35;
		perimeter_width = nzl_value * 1.25;
		
		System.out.println("first_width : " + first_width + "mm");
		System.out.println("infill_width : " + infill_width + "mm");
		System.out.println("solid_width : " + solid_width + "mm");
		System.out.println("top_width : " + top_width + "mm");
		
		if( num_raft == 0 ) 
		{
			// this value controll bottom cap layer ( not raft )
			// by default, this value is 200% of first layer height -> ex. 0.3 * 2.0 = 0.6
			// 175% value seems to be better, = 0.525 for 0.3mm layer height
			ini_string.append(String.format("first_layer_extrusion_width = %.2f\n", first_width));
			
			// infill
			ini_string.append(String.format("infill_extrusion_width = %.2f\n", infill_width));

			// if no raft, "solid infill" controls solid layer excluding top/bottom cap
			ini_string.append(String.format("solid_infill_extrusion_width = %.2f\n", solid_width));
			
			// top cap layer
			ini_string.append(String.format("top_infill_extrusion_width = %.2f\n", top_width));
		
			// actually no meanings, in no support printing.
			ini_string.append("support_material_interface_extrusion_width = 0\n");
			
		}
		else // WITH RAFT,(including FFL)
		{
			ini_string.append(String.format("infill_extrusion_width = %.2f\n", infill_width));

			// if FLL, "solid_infill" affect bottom cap and normal solid infill
			ini_string.append(String.format("solid_infill_extrusion_width = %.2f\n", solid_width));
			
			// top cap layer
			ini_string.append(String.format("top_infill_extrusion_width = %.2f\n", top_width));

			if(IS_FFL)
			{
				// actually, no layer modified by this value
				ini_string.append("first_layer_extrusion_width = 175%\n");
				
				// affect on FFL first layer
				float FFL_width = PRNT_NZL[PRINTER_ID] * 2.5f;
				ini_string.append(String.format("support_material_interface_extrusion_width = %.2f\n", FFL_width));
			}
			else // normal raft
			{
				// affect on first raft layer
				ini_string.append(String.format("first_layer_extrusion_width = %.2f\n", PRNT_NZL[PRINTER_ID]*2.0f));
				// normal interface layer
				ini_string.append("support_material_interface_extrusion_width = 0\n");
			}

		}
		//--------------------------------------------------------
		
		
		// CONDITIONAL (by nozzle size)::::::::::::::::::::::::::::
		ini_string.append(String.format("perimeter_extrusion_width = %.2f\n", perimeter_width));
		ini_string.append(String.format("external_perimeter_extrusion_width = %.2f\n", perimeter_width));
		ini_string.append("support_material_extrusion_width = 0\n"); 
		//---------------------------------------------------------
		
		
		// #speed settings ---------------------------------
		// first, infill, solid, top, perimeter
		double infill_plane, solid_plane, top_plane, perimeter_plane;
		// area size
		infill_plane = infill_width * LAYER_HEIGHT;
		solid_plane = solid_width * LAYER_HEIGHT;
		top_plane = top_width * LAYER_HEIGHT;
		perimeter_plane = perimeter_width * LAYER_HEIGHT;
		
		System.out.println("infill_plane : " + infill_plane + "mm^2");
		System.out.println("solid_plane" + solid_plane + "mm^2");
		System.out.println("top_plane" + top_plane + "mm^2");
		System.out.println("perimeter_plane" + perimeter_plane + "mm^2");
		
		int infill_speed, solid_speed, top_speed, perimeter_speed;
		double volumetric = 0.0;
		//double print_plane = PRNT_NZL[PRINTER_ID] * LAYER_HEIGHT; // mm^2
		//double base_speed = 0.0;
		//int base_speed_int = 0;
		
		if(IS_CHT_NOZZLE)
		{
			volumetric = VOLUMETRIC_SPEED; //mm^3/s
		}
		else
		{
			volumetric = 10.0; //mm^3/s
		}
		
		// print faster (limit to 100mm/s)
		infill_speed = Math.min((int)(volumetric / infill_plane), 150);
		solid_speed = Math.min((int)(volumetric / solid_plane), 150);
		top_speed = Math.min((int)(volumetric / top_plane), 150);
		perimeter_speed = Math.min((int)(volumetric / perimeter_plane), 150);
		
		System.out.println("infill_speed : " + infill_speed + "mm/s");
		System.out.println("solid_speed : " + solid_speed + "mm/s");
		System.out.println("top_speed : " + top_speed + "mm/s");
		System.out.println("perimeter_speed : " + perimeter_speed + "mm/s");
		
		
		//ini_string.append("perimeter_speed = 52\n");
		ini_string.append(String.format("perimeter_speed = %d\n", perimeter_speed));
		
		//ini_string.append("small_perimeter_speed = 20\n");
		ini_string.append(String.format("small_perimeter_speed = %d\n", 22));
		
		//ini_string.append("external_perimeter_speed = 50\n");
		ini_string.append(String.format("external_perimeter_speed = %d\n", (int)(perimeter_speed * 0.9)));
		
		//ini_string.append("infill_speed = 53\n");
		ini_string.append(String.format("infill_speed = %d\n", infill_speed));
		
		//ini_string.append("solid_infill_speed = 53\n");
		ini_string.append(String.format("solid_infill_speed = %d\n", solid_speed));
		
		//ini_string.append("top_solid_infill_speed = 50\n");
		ini_string.append(String.format("top_solid_infill_speed = %d\n", top_speed));
		
		ini_string.append("gap_fill_speed = 30\n"); // small area infilling
		
		//ini_string.append("bridge_speed = 60\n");
		ini_string.append(String.format("bridge_speed = %d\n", perimeter_speed));
		
		//ini_string.append("support_material_speed = 50\n");
		ini_string.append(String.format("support_material_speed = %d\n", infill_speed));
		
		//ini_string.append("support_material_interface_speed = 48\n");
		ini_string.append(String.format("support_material_interface_speed = %d\n", infill_speed));
		
		ini_string.append("travel_speed = 180\n");
		
		
		//***********************************************************
		//***********************************************************
		
		
		ini_string.append("infill_overlap = 85%\n");
		ini_string.append("bridge_flow_ratio = 1\n");
		ini_string.append("xy_size_compensation = 0\n");
		ini_string.append("threads = 8\n");
		ini_string.append("resolution = 0\n");
		
		ini_string.append("complete_objects = 0\n");
		ini_string.append("extruder_clearance_height = 20\n");
		ini_string.append("extruder_clearance_radius = 20\n");
		ini_string.append("output_filename_format = [input_filename_base].gco\n");
		ini_string.append("post_process =\n");
		ini_string.append("notes =\n");
		ini_string.append("gcode_comments= 0\n");
		

		
		
		//***************************************************************
		//****** filament setting ***************************************
		//***************************************************************
			
			double extrusion_coef = 0.0;
			
			if(LAYER_HEIGHT < 0.3 )
			{
				extrusion_coef = (1.0 - (LAYER_HEIGHT / 0.3)) * 0.1; // 0.2mm = 0.0333, 0.1mm = 0.0666
			}
			
			
			ini_string.append("# filament setting\n");
			ini_string.append("filament_colour = #FFFFFF\n");
			ini_string.append("filament_diameter = 1.75\n");
			//ini_string.append("extrusion_multiplier = 1.0\n");
			ini_string.append(String.format("extrusion_multiplier = %.4f\n", 1.002 + extrusion_coef));
			System.out.println("extrusion multiplier : " + (1.002 + extrusion_coef));
			
			ini_string.append(String.format("first_layer_temperature = %d\n", TEMPERATURE+3));
			ini_string.append(String.format("temperature = %d\n", TEMPERATURE));
			ini_string.append("first_layer_bed_temperature = 0\n");
			//CONDITIONAL:::::::::::::::::::::::::::::::::
			ini_string.append("bed_temperature = 0\n");
			//--------------------------------------------
			
			ini_string.append("fan_always_on = 1\n");
			ini_string.append("cooling = 1\n");
			ini_string.append("min_fan_speed = 35\n");
			ini_string.append("max_fan_speed = 100\n");
			ini_string.append("bridge_fan_speed = 100\n");
			ini_string.append("disable_fan_first_layers = 0\n");
			ini_string.append("fan_below_layer_time = 60\n");
			ini_string.append("slowdown_below_layer_time = 10\n");
			ini_string.append("min_print_speed = 15\n");
			
		//***************************************************************
		//****** machine setting ***************************************
		//***************************************************************
			ini_string.append("#machine setting \n");

			int BedX = PRNT_X[PRINTER_ID];
			int BedY = PRNT_Y[PRINTER_ID];
			
			ini_string.append(String.format("nozzle_diameter = %.1f\n", PRNT_NZL[PRINTER_ID]));
			ini_string.append(String.format("bed_shape = 0x0,%dx0,%dx%d,0x%d\n", BedX, BedX, BedY, BedY));
			// START GCODE ********************************
			ini_string.append("start_gcode = G28 \\nM106 S255 \\nM109 S[first_layer_temperature] \\nG92 E0 ");
			ini_string.append(String.format("\\nG1 X1 Y3 Z%.2f F1000 E0.5 \\nG1 X100 Y3 E18 \\nG92 E0 \\n\n", first_height_start_gcode));
			//ini_string.append("start_gcode = G28 \\nM106 S255 \\nM109 S[first_layer_temperature] \\nG92 E0 \\nG1 F300 E0.5 \\nG1 E40 \\nG92 E0 \\n\n");
			ini_string.append("end_gcode = M104 S0 \\nG28 X0 \\nM84 \\n\n"); // "//n" = yen n
			ini_string.append("extruder_offset = 0x0 \n");
			ini_string.append("retract_length = 1.0 \n");
			ini_string.append("retract_lift = 0 \n");
			ini_string.append("retract_speed = 80 \n");
			ini_string.append("retract_restart_extra = 0 \n");
			ini_string.append("retract_before_travel = 2 \n");
			ini_string.append("retract_layer_change = 1 \n");
			ini_string.append("wipe = 0 \n");
			ini_string.append("retract_length_toolchange = 0 \n");
			ini_string.append("retract_restart_extra_toolchange = 0 \n");
			
			ini_string.append("has_heatbed = 1\n");
			ini_string.append("max_layer_height = 0.8\n");
			ini_string.append("min_layer_height = 0.05\n");
			ini_string.append("z_offset = 0\n");
			ini_string.append("octoprint_host =\n");
			ini_string.append("octoprint_apikey =\n");
			ini_string.append("gcode_flavor = reprap\n");
			ini_string.append("use_relative_e_distances = 0\n");
			ini_string.append("use_firmware_retraction = 0\n");
			ini_string.append("use_volumetric_e = 0\n");
			ini_string.append("pressure_advance = 0\n");
			ini_string.append("vibration_limit = 0\n");
			ini_string.append("before_layer_gcode = \n");
			ini_string.append("layer_gcode = \n");
			ini_string.append("toolchange_gcode = \n");
			
			
		// save inifile as tempolary file
		Path curPath = Paths.get("");
		String absolutePath = curPath.toAbsolutePath().toString();
		String ini_path = new String(absolutePath + "/temp.ini");
		ini_file_path = ini_path;
		
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(ini_path))) {
	            writer.write(ini_string.toString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	} //
	
	//////////////////////////////////////////////////////
	///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	
	public static void create_arg_and_run_slice()
	{
		
		 // create argument string for slicer
		int Center_X = PRNT_X[PRINTER_ID] / 2;
		int Center_Y = PRNT_Y[PRINTER_ID] / 2;
		
		float ave_x = 0.0f;
		float ave_y = 0.0f;
		int numS = stl_Array.size();
		
		for( int s = 0 ; s < numS ; s++ )
		{
			STL_Class tempSTL = stl_Array.get(s);
			ave_x += tempSTL.shift_x;
			ave_y += tempSTL.shift_y;
		}
		
		ave_x /= (float)numS;
		ave_y /= (float)numS;
		
		Center_X += ave_x;
		Center_Y += ave_y;
		
		
		// get current directory
		String curDir = System.getProperty("user.dir");
		String fixed_obj_path = new String(curDir + "/merged_fixed.obj");
		String load_string = new String("--load");
		String printCenter_string = new String("--print-center");
		String center_string = String.format("%d,%d", Center_X, Center_Y);
		String o_string = new String("-o");
		 
		 // run slicer
		 String slicer_path = new String(curDir + "/Slic3r/Slic3r-console.exe");
		 
		 
		 ProcessBuilder slicer_process = new ProcessBuilder(slicer_path, fixed_obj_path, load_string, ini_file_path, printCenter_string, center_string, o_string, save_gcode_path);
		 
		 try {
		
			 Process process = slicer_process.start();
		
			 try {
				int exitCode = process.waitFor();
				System.out.println("Slice process exit code : " + exitCode);
				
				// if Loop enabled, repeat gcode.
				//******************************
				
				//******************************
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	} // create arg and run slice
	
	
}
