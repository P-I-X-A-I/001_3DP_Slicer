package main_package;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;

public class GUI_Manager implements ActionListener{

	JFrame paramWindow;
	
	// parameter GUI
	public JSlider SLI_layer_height;
	public JSlider SLI_perimeters;
	public JSlider SLI_infill;
	public JSlider SLI_loops;
	public JSlider SLI_adaptiveSlice;
	public JSlider SLI_volumetric;

	
	public JRadioButton RD_x;
	public JRadioButton RD_y;
	public JRadioButton RD_z;
	public ButtonGroup RD_axis_group;
	
	public JRadioButton RD_grid;
	public JRadioButton RD_honeycomb;
	public JRadioButton RD_gyroid;
	
	public JCheckBox CHK_raft;
	public JCheckBox CHK_support;
	public JCheckBox CHK_advanced;
	public JCheckBox CHK_howManyLoops;
	public JCheckBox CHK_adaptiveSlice;
	public JCheckBox CHK_fastFirstLayer;
	public JCheckBox CHK_hasCHTnozzle;
	
	public JSlider SLI_temperature;
	
	JButton BT_conv;
	
	JLabel LB_log;
	
	// STL list
	public DefaultListModel<String> ListModel;
	public JList<String> ListView_STL;
	//public ArrayList<STL_Class> stl_Array = new ArrayList<STL_Class>();
	//public ArrayList<String> stl_Names = new ArrayList<String>();
	
	
	public GUI_Manager()
	{
		
		System.out.println("GUI Manager init");
		
		paramWindow = new JFrame("parameters");
		paramWindow.setBounds(100, 100, 420, 600);
		paramWindow.setResizable(false);
		paramWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		paramWindow.setLayout(null);
		paramWindow.setVisible(true);
		
		// setup GUI
		JLabel LB_selectPrinter = new JLabel("1.Select Printer");
		paramWindow.getContentPane().add(LB_selectPrinter);
		LB_selectPrinter.setBounds(10, 10, 150, 20);
		
		// printer combobox
		int PNUM = ParamHolder.PRNT_NUM;
		
		String[] printers = new String[PNUM];
		for( int i = 0 ; i < PNUM ; i++ )
		{
			printers[i] = ParamHolder.PRNT_NAME[i];
		}
		
		JComboBox<String> combo_obj = new JComboBox<>(printers);
		combo_obj.setFocusable(false);
		paramWindow.getContentPane().add(combo_obj);
		combo_obj.setBounds(10, 40, 170, 20);
		combo_obj.addActionListener(e->{
			
			int ID = combo_obj.getSelectedIndex();
			ParamHolder.PRINTER_ID = ID;
			System.out.println("PRINTER_ID:"+ID);
		});
		
		// load & delete STL
		JLabel LB_loadSTL = new JLabel("2.Load STL files");
		paramWindow.getContentPane().add(LB_loadSTL);
		LB_loadSTL.setBounds(10, 85, 150, 20);
		
		JButton BT_load = new JButton("Load");
		JButton BT_delete = new JButton("Delete");
		BT_load.setFocusable(false);
		BT_delete.setFocusable(false);
		paramWindow.getContentPane().add(BT_load);
		paramWindow.getContentPane().add(BT_delete);
		BT_load.setBounds(10, 110, 80, 20);
		BT_delete.setBounds(100, 110, 80, 20);
		BT_load.setActionCommand("BT_LOAD");
		BT_delete.setActionCommand("BT_DELETE");
		BT_load.addActionListener(this);
		BT_delete.addActionListener(this);
		
		// list view
		
		ListModel = new DefaultListModel<>();
		/*
		for( int i = 0 ; i < 10 ; i++ )
		{
			ListModel.addElement(new String("model"+i));
		}
		*/
		ListView_STL = new JList<>(ListModel);
		ListView_STL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListView_STL.setFocusable(false);
		
		JScrollPane scroll_obj = new JScrollPane(ListView_STL);
		scroll_obj.setFocusable(false);
		paramWindow.getContentPane().add(scroll_obj);
		scroll_obj.setBounds(10, 140, 170, 100);
		
		// add ListSelectionListener
		ListSelectionListener listListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if( e.getValueIsAdjusting() == false) // mouse released
				{
					// count list elements
					int itemCount = ListView_STL.getModel().getSize();
					
					int sel = ListView_STL.getSelectedIndex();
					System.out.println(itemCount + "Selected STL "+sel);
					
					ParamHolder.SELECTED_STL_ID = sel;
				}
			}
		};
		
		ListView_STL.addListSelectionListener(listListener);
		
		
		// rotation button
		JLabel LB_rotate = new JLabel("3.rotate object");
		paramWindow.getContentPane().add(LB_rotate);
		LB_rotate.setBounds(10, 260, 170, 20);
		
		JButton BT_plus45 = new JButton("+45");
		JButton BT_minus45 = new JButton("-45");
		BT_plus45.setFocusable(false);
		BT_minus45.setFocusable(false);
		paramWindow.getContentPane().add(BT_plus45);
		paramWindow.getContentPane().add(BT_minus45);
		BT_plus45.setBounds(10, 285, 80, 20);
		BT_minus45.setBounds(100, 285, 80, 20);
		BT_plus45.setActionCommand("rotate_plus");
		BT_minus45.setActionCommand("rotate_minus");
		BT_plus45.addActionListener(this);
		BT_minus45.addActionListener(this);
		
		// radio button
		RD_x = new JRadioButton("x");
		RD_y = new JRadioButton("y");
		RD_z = new JRadioButton("z");
		RD_axis_group = new ButtonGroup();
		RD_x.setFocusable(false);
		RD_y.setFocusable(false);
		RD_z.setFocusable(false);
		RD_axis_group.add(RD_x);
		RD_axis_group.add(RD_y);
		RD_axis_group.add(RD_z);
		RD_x.setSelected(true);
		
		
		paramWindow.getContentPane().add(RD_x);
		paramWindow.getContentPane().add(RD_y);
		paramWindow.getContentPane().add(RD_z);
		
		RD_x.setActionCommand("radio_x");
		RD_y.setActionCommand("radio_y");
		RD_z.setActionCommand("radio_z");
		
		RD_x.addActionListener(this);
		RD_y.addActionListener(this);
		RD_z.addActionListener(this);
		
		RD_x.setBounds(10, 310, 50, 20);
		RD_y.setBounds(60, 310, 50, 20);
		RD_z.setBounds(120, 310, 50, 20);
		
		// **********************************************************
		// ******** Parameter setting GUI ***************************
		// **********************************************************
		JSeparator seps = new JSeparator(JSeparator.VERTICAL);
		paramWindow.getContentPane().add(seps);
		seps.setBounds(200, 20, 10, 330);
		
		JLabel LB_layer_height = new JLabel("Layer height : 0.30mm");
		paramWindow.getContentPane().add(LB_layer_height);
		LB_layer_height.setBounds(220, 10, 200, 20);
		
		// layer height
		SLI_layer_height = new JSlider(0, 11, 5);
		SLI_layer_height.setFocusable(false);
		SLI_layer_height.setPaintTicks(true);
		SLI_layer_height.setSnapToTicks(true);
		SLI_layer_height.setMajorTickSpacing(1);
		SLI_layer_height.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_layer_height);
		SLI_layer_height.setBounds(210, 40, 180, 20);
		
		SLI_layer_height.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int SLI = SLI_layer_height.getValue();
				String mmStr = String.format("Layer height : %1.2fmm", 0.15 + SLI*0.03);
				LB_layer_height.setText(mmStr);
				
				// set paramholder
				ParamHolder.LAYER_HEIGHT = (0.15 + SLI*0.03);
			}
		});
		
		
		// perimeter
		JLabel LB_perimeter = new JLabel("Perimeters : 2");
		paramWindow.getContentPane().add(LB_perimeter);
		LB_perimeter.setBounds(220, 70, 200, 20);
		
		SLI_perimeters = new JSlider(0, 4, 1);
		SLI_perimeters.setFocusable(false);
		SLI_perimeters.setPaintTicks(true);
		SLI_perimeters.setSnapToTicks(true);
		SLI_perimeters.setMajorTickSpacing(1);
		SLI_perimeters.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_perimeters);
		SLI_perimeters.setBounds(210, 100, 180, 20);
		SLI_perimeters.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int SLI = SLI_perimeters.getValue();
				String periStr = String.format("Perimeters : %d", SLI+1);
				LB_perimeter.setText(periStr);
				
				// set paramholder
				ParamHolder.PERIMETERS = (SLI+1);
			}
			
		});
		
		
		// fill density
		JLabel LB_fill = new JLabel("Infill : 10%");
		paramWindow.getContentPane().add(LB_fill);
		LB_fill.setBounds(220, 130, 200, 20);
		
		SLI_infill = new JSlider(0, 20, 5);
		SLI_infill.setFocusable(false);
		SLI_infill.setPaintTicks(true);
		SLI_infill.setSnapToTicks(true);
		SLI_infill.setMajorTickSpacing(1);
		SLI_infill.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_infill);
		SLI_infill.setBounds(210, 160, 180, 20);
		SLI_infill.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int SLI = SLI_infill.getValue();
				String infillStr = String.format("Infill : %d%%", SLI*2);
				LB_fill.setText(infillStr);
				
				// set param holder
				ParamHolder.INFILL = (SLI*2);
			}
			
		});
		
		// infill pattern
		JLabel LB_pattern = new JLabel("Infill Pattern, support, raft");
		paramWindow.getContentPane().add(LB_pattern);
		LB_pattern.setBounds(220, 200, 200, 20);
		
		RD_grid = new JRadioButton("Grid");
		RD_honeycomb = new JRadioButton("honey");
		RD_gyroid = new JRadioButton("Gyroid");
		ButtonGroup pattern_group = new ButtonGroup();
		
		RD_grid.setFocusable(false);
		RD_honeycomb.setFocusable(false);
		RD_gyroid.setFocusable(false);
		pattern_group.add(RD_grid);
		pattern_group.add(RD_honeycomb);
		pattern_group.add(RD_gyroid);
		RD_grid.setSelected(true);
		
		paramWindow.getContentPane().add(RD_grid);
		paramWindow.getContentPane().add(RD_honeycomb);
		paramWindow.getContentPane().add(RD_gyroid);
		RD_grid.setBounds(210, 230, 50, 20);
		RD_honeycomb.setBounds(260, 230, 70, 20);
		RD_gyroid.setBounds(330, 230, 70, 20);
		
		
		// raft, support
		CHK_support = new JCheckBox("support");
		CHK_support.setFocusable(false);
		CHK_support.setSelected(false);
		paramWindow.getContentPane().add(CHK_support);
		CHK_support.setBounds(210, 260, 80, 20);
		
		CHK_raft = new JCheckBox("raft");
		CHK_raft.setFocusable(false);
		CHK_raft.setSelected(false);
		paramWindow.getContentPane().add(CHK_raft);
		CHK_raft.setBounds(300, 260, 80, 20);
		
		
		// temperature
		JLabel LB_temp = new JLabel("Temperature : 200");
		paramWindow.getContentPane().add(LB_temp);
		LB_temp.setBounds(220, 290, 200, 20);
		
		SLI_temperature = new JSlider(0, 12, 4);
		SLI_temperature.setFocusable(false);
		SLI_temperature.setPaintTicks(true);
		SLI_temperature.setSnapToTicks(true);
		SLI_temperature.setMajorTickSpacing(1);
		SLI_temperature.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_temperature);
		SLI_temperature.setBounds(210, 320, 180, 20);
		SLI_temperature.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int SLI = SLI_temperature.getValue();
				String tempStr = String.format("Temperature : %d", 180 + SLI*5);
				LB_temp.setText(tempStr);
			}
			
		});
		
		
		// convert button
		BT_conv = new JButton("convert");
		BT_conv.setFocusable(false);
		paramWindow.getContentPane().add(BT_conv);
		BT_conv.setBounds(125, 375, 150, 20);
		BT_conv.setActionCommand("convert");
		BT_conv.addActionListener(this);
		
		//******************************************************
		// advanced settings ***********************************
		//******************************************************
		
		CHK_howManyLoops = new JCheckBox("print loops(if possible) :");;
		CHK_howManyLoops.setFocusable(false);
		CHK_howManyLoops.setSelected(false);
		paramWindow.getContentPane().add(CHK_howManyLoops);
		CHK_howManyLoops.setBounds(10, 450, 200, 20);
		CHK_howManyLoops.setEnabled(false);
		
		SLI_loops = new JSlider(2, 9, 2);
		SLI_loops.setFocusable(false);
		SLI_loops.setPaintTicks(true);
		SLI_loops.setSnapToTicks(true);
		SLI_loops.setMajorTickSpacing(1);
		SLI_loops.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_loops);
		SLI_loops.setBounds(10, 480, 180, 20);
		SLI_loops.setEnabled(false);
		SLI_loops.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int LOP = SLI_loops.getValue();
				String tempStr = String.format("print loops(if possible) : %d", LOP);
				CHK_howManyLoops.setText(tempStr);
				
				ParamHolder.NUM_LOOP = LOP;
			}
			
		});
		
		CHK_fastFirstLayer = new JCheckBox("Fast First Layer(raft ignored)");
		CHK_fastFirstLayer.setFocusable(false);
		CHK_fastFirstLayer.setSelected(false);
		paramWindow.getContentPane().add(CHK_fastFirstLayer);
		CHK_fastFirstLayer.setBounds(10, 510, 200, 20);
		CHK_fastFirstLayer.setEnabled(false);
		
		CHK_adaptiveSlice = new JCheckBox("Adaptive slice : ");
		CHK_adaptiveSlice.setFocusable(false);
		CHK_adaptiveSlice.setSelected(false);
		paramWindow.getContentPane().add(CHK_adaptiveSlice);
		CHK_adaptiveSlice.setBounds(210, 450, 200, 20);
		CHK_adaptiveSlice.setEnabled(false);
		
		SLI_adaptiveSlice = new JSlider(1, 10, 5);
		SLI_adaptiveSlice.setFocusable(false);
		SLI_adaptiveSlice.setPaintTicks(true);
		SLI_adaptiveSlice.setSnapToTicks(true);
		SLI_adaptiveSlice.setMajorTickSpacing(1);
		SLI_adaptiveSlice.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_adaptiveSlice);
		SLI_adaptiveSlice.setBounds(210, 480, 180, 20);
		SLI_adaptiveSlice.setEnabled(false);
		SLI_adaptiveSlice.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int QOL = SLI_adaptiveSlice.getValue();
				String tempStr = String.format("Adaptive slice : %d%%", QOL*10);
				CHK_adaptiveSlice.setText(tempStr);
				
				ParamHolder.ADAPTIVE_QUALITY = QOL*10;
			}
			
		});
		
		CHK_hasCHTnozzle = new JCheckBox("has CHT nozzle");
		CHK_hasCHTnozzle.setFocusable(false);
		CHK_hasCHTnozzle.setSelected(false);
		paramWindow.getContentPane().add(CHK_hasCHTnozzle);
		CHK_hasCHTnozzle.setBounds(210, 510, 200, 20);
		CHK_hasCHTnozzle.setEnabled(false);
		
		SLI_volumetric = new JSlider(10, 24, 15);
		SLI_volumetric.setFocusable(false);
		SLI_volumetric.setPaintTicks(true);
		SLI_volumetric.setSnapToTicks(true);
		SLI_volumetric.setMajorTickSpacing(1);
		SLI_volumetric.setMinorTickSpacing(1);
		paramWindow.getContentPane().add(SLI_volumetric);
		SLI_volumetric.setBounds(210, 540, 180, 20);
		SLI_volumetric.setEnabled(false);
		SLI_volumetric.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int volspeed = SLI_volumetric.getValue();
				String tempStr = String.format("has CHT nzl : %d mm^3/s", volspeed);
				CHK_hasCHTnozzle.setText(tempStr);
				
				ParamHolder.VOLUMETRIC_SPEED = volspeed;
			}
		});
		
				
		CHK_advanced = new JCheckBox("advanced settings");
		CHK_advanced.setFocusable(false);
		CHK_advanced.setSelected(false);
		paramWindow.getContentPane().add(CHK_advanced);
		CHK_advanced.setBounds(10, 420, 150, 20);
		CHK_advanced.addItemListener(e->{
			
			if(e.getStateChange() == 1)// checked
			{
				CHK_howManyLoops.setEnabled(true);
				CHK_fastFirstLayer.setEnabled(true);
				CHK_adaptiveSlice.setEnabled(true);
				CHK_hasCHTnozzle.setEnabled(true);
				SLI_loops.setEnabled(true);
				SLI_adaptiveSlice.setEnabled(true);
				SLI_volumetric.setEnabled(true);
			}
			else
			{
				CHK_howManyLoops.setEnabled(false);
				CHK_fastFirstLayer.setEnabled(false);
				CHK_adaptiveSlice.setEnabled(false);
				CHK_hasCHTnozzle.setEnabled(false);
				SLI_loops.setEnabled(false);
				SLI_adaptiveSlice.setEnabled(false);
				SLI_volumetric.setEnabled(false);
				
				// turn of checkboxes;
				CHK_howManyLoops.setSelected(false);
				CHK_fastFirstLayer.setSelected(false);
				CHK_adaptiveSlice.setSelected(false);
				CHK_hasCHTnozzle.setSelected(false);
			}
		});
		

		
		// process log label
		LB_log = new JLabel("");
		paramWindow.getContentPane().add(LB_log);
		LB_log.setBounds(300, 375, 150, 20);
		
		//****************************************************************
		// set Key Event Listener ****************************************
		KeyListener keyL = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				//System.out.println("key typed " + e.getKeyCode());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				int code = e.getKeyCode();
				int mods = e.getModifiersEx();
				if(mods == 64) { mods = 100; }
				else { mods = 0; }
				System.out.println("GUI key:"+code+"/"+mods );
				// 37 - left
				// 38 - up
				// 39 - right
				// 40 - down
				// with shift, +100
				ParamHolder.arrowKeyPressed(code - 37 + mods);
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		};
		
		paramWindow.addKeyListener(keyL);
	} // constructor

	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String actionCommand = e.getActionCommand();
		
		if( actionCommand.equals("BT_LOAD")) 
		{
			this.load_STL_file();
			// autoselect listview
			// select last STL object
			int itemCount = ListView_STL.getModel().getSize();
			ListView_STL.setSelectedIndex(itemCount - 1);
		}
		else if(actionCommand.equals("BT_DELETE"))
		{
			this.delete_STL_file();
			// autoselect listview
			// select last STL object if exist
			int itemCount = ListView_STL.getModel().getSize();
			if(itemCount != 0)
			{
				ListView_STL.setSelectedIndex(itemCount -1);
			}
		}
		else if(actionCommand.equals("rotate_plus"))
		{
			int SEL_ID = ParamHolder.SELECTED_STL_ID;
			
			if(SEL_ID != -1 )
			{
				STL_Class tempSTL = ParamHolder.stl_Array.get(SEL_ID);
				
				// selected axis
				if(RD_x.isSelected())
				{
					tempSTL.rotateXYZ(45.0f, 0.0f, 0.0f);
				}
				else if(RD_y.isSelected())
				{
					tempSTL.rotateXYZ(0.0f, 45.0f, 0.0f);
				}
				else if(RD_z.isSelected())
				{
					tempSTL.rotateXYZ(0.0f, 0.0f, 45.0f);
				}
			}
		}
		else if(actionCommand.equals("rotate_minus"))
		{
			int SEL_ID = ParamHolder.SELECTED_STL_ID;
			
			if( SEL_ID != -1)
			{
				STL_Class tempSTL = ParamHolder.stl_Array.get(SEL_ID);
				
				// selected axis
				if(RD_x.isSelected())
				{
					tempSTL.rotateXYZ(-45.0f, 0.0f, 0.0f);
				}
				else if(RD_y.isSelected())
				{
					tempSTL.rotateXYZ(0.0f, -45.0f, 0.0f);
				}
				else if(RD_z.isSelected())
				{
					tempSTL.rotateXYZ(0.0f, 0.0f, -45.0f);
				}
			}
		}
		
		else if(actionCommand.equals("radio_x"))
		{
			//System.out.println("radio x");
		}
		else if(actionCommand.equals("radio_y"))
		{
			//System.out.println("radio y");
		}
		else if(actionCommand.equals("radio_z"))
		{
			//System.out.println("radio z");
		}
		else if(actionCommand.equals("convert"))
		{
			int numSTL = ParamHolder.stl_Array.size();

			if( numSTL >= 1)
			{
				
				// open save dialog
				String desktop_path = System.getProperty("user.home") + File.separator + "Desktop";
				JFileChooser saveDialog = new JFileChooser(desktop_path);
				int ret = saveDialog.showSaveDialog(paramWindow);

				
				if(ret == JFileChooser.APPROVE_OPTION)
				{
					String savePath = saveDialog.getSelectedFile().getAbsolutePath();
					String finalPath;
					
					// check .gco extension
					boolean isExt = savePath.endsWith(".gco");
					if(isExt == false)
					{
						finalPath = savePath + ".gco";
					}
					else
					{
						finalPath = savePath;
					}
					
					ParamHolder.save_gcode_path = finalPath;
				}
				else if(ret == JFileChooser.CANCEL_OPTION)
				{
					return; // finish this process
				}
				
				
				// get slice parameter fron GUI
				// SLIDER value are set when slider changed.
				
				// fill pattern
				int pattern = -1;
				if( RD_grid.isSelected() ) { pattern = 0; }
				else if(RD_honeycomb.isSelected() ) { pattern = 1; }
				else if(RD_gyroid.isSelected() ) { pattern = 2; }
				ParamHolder.INFILL_PATTERN = pattern;
				System.out.println("Infill pattern : " + ParamHolder.INFILL_PATTERN );
				
				
				// checkbox
				ParamHolder.IS_SUPPORT = CHK_support.isSelected();
				System.out.println("support : " + ParamHolder.IS_SUPPORT);
				
				ParamHolder.IS_RAFT = CHK_raft.isSelected();
				System.out.println("raft : " + ParamHolder.IS_RAFT);
				
				ParamHolder.TEMPERATURE = SLI_temperature.getValue()*5 + 180;
				System.out.println("temperature : " + ParamHolder.TEMPERATURE);
				
				ParamHolder.IS_ADVANCED = CHK_advanced.isSelected();
				System.out.println("advanced settings : " + ParamHolder.IS_ADVANCED);
				
				ParamHolder.IS_LOOP = CHK_howManyLoops.isSelected();
				System.out.println("loop printing : " + ParamHolder.IS_LOOP);
				
				ParamHolder.IS_ADAPTIVE_SLICE = CHK_adaptiveSlice.isSelected();
				System.out.println("adaptive slicing : " + ParamHolder.IS_ADAPTIVE_SLICE);
				
				ParamHolder.IS_FFL = CHK_fastFirstLayer.isSelected();
				System.out.println("fast first layer : " + ParamHolder.IS_FFL);
				
				ParamHolder.IS_CHT_NOZZLE = CHK_hasCHTnozzle.isSelected();
				System.out.println("has CHT nozzle : " + ParamHolder.IS_CHT_NOZZLE);
				
				
				// disable convert button
				BT_conv.setEnabled(false);
				
				// stop rendering
				ParamHolder.isRender = false;
				
				//****************************************************
				//****************************************************
				
				
				// re-layout & merge STL file
				try {
					this.merge_stl_files();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// slice *****************************************
				this.run_slicer_repair();
				
				// create ini file *******************************
				ParamHolder.create_ini_file();
				
				// run slice & wait for finish*********************
				ParamHolder.create_arg_and_run_slice();
				
				// restore render
				ParamHolder.isRender = true;
				
				// restore convert button
				BT_conv.setEnabled(true);
				LB_log.setText("");
				
			} // if stl >= 1	
		} // if convert button hit
		
		
	}// action performed
	
	
	void load_STL_file()
	{
		// open file chooser
		String desktop_path = System.getProperty("user.home") + File.separator + "Desktop";
		JFileChooser fc = new JFileChooser(desktop_path);
		FileNameExtensionFilter filt = new FileNameExtensionFilter("STL file", "stl");
		fc.setFileFilter(filt);
		
		// open dialog
		int result = fc.showOpenDialog(paramWindow);
		
		// OK, or cancel
		if(result == fc.APPROVE_OPTION)
		{
			// get file path
			String filePath = fc.getSelectedFile().getAbsolutePath();
			
			// open STL file
			try {
				
				// create STL
				STL_Class tempSTL = new STL_Class(filePath);
				// add to Array
				ParamHolder.stl_Array.add(tempSTL);
				ParamHolder.stl_Names.add(new String(tempSTL.file_name));
				// add name to list model
				ListModel.addElement(tempSTL.file_name);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			// canceled.
		}
	} // load STL
	
	
	void delete_STL_file()
	{
		// check selected STL index
		int selID = ListView_STL.getSelectedIndex();
		
		// remove items
		if(selID != -1)
		{
			ParamHolder.stl_Array.remove(selID);
			ParamHolder.stl_Names.remove(selID);
			ListModel.remove(selID);
		}
	}
	
	
	//*************************************************
	
	private void merge_stl_files() throws IOException
	{
		//*****************************************
		LB_log.setText("merge stl files.");
		LB_log.revalidate();
		LB_log.repaint();
		//*****************************************
		
		int numSTL = ParamHolder.stl_Array.size();
		
		// get total num triangles
		int numT = 0;
		
		for( int i = 0 ; i < numSTL ; i++ )
		{
			numT += ParamHolder.stl_Array.get(i).num_of_triangles;
		}
		
		System.out.println("num total triangles : " + numT);
		
		////////////////////////////////////////////////////
		// get current directory
		String curDir = System.getProperty("user.dir");
		
		//Path curPath = Paths.get("");
		//String absolutePath = curPath.toAbsolutePath().toString();
		
		// create merged STL path
		String merge_STL_path = new String(curDir + "/merged.stl");
		ParamHolder.merged_STL_path = merge_STL_path;
		
		//*********************************************************
		// merge STLs *********************************************
		//*********************************************************
		
		try {
			
			// must write data in LittleEndian format ( java use BigEndian )
			FileOutputStream fos = new FileOutputStream(merge_STL_path);
			DataOutputStream dos = new DataOutputStream(fos);
			ByteBuffer wb = ByteBuffer.allocate(4);
			ByteBuffer wVert = ByteBuffer.allocate(4*9); // 3vertex(xyz)
			ByteBuffer wNorm = ByteBuffer.allocate(4*3);
			wb.order(ByteOrder.LITTLE_ENDIAN);
			wVert.order(ByteOrder.LITTLE_ENDIAN);
			wNorm.order(ByteOrder.LITTLE_ENDIAN);
			
			// write header [80]bytes
			byte tempChar = 65;
			for( int i = 0 ; i < 80 ; i++ )
			{
				dos.write((int)tempChar); // write 1 byte in INT, upper 3bytes are ignored.
			}
			
			// write num triangles [4]bytes
			wb.putInt(numT);
			wb.flip();
			dos.write(wb.array());
			
			// write stl triangle data
			for( int s = 0 ; s < numSTL ; s++ )
			{
				// vertex must be shift
				STL_Class targetSTL = ParamHolder.stl_Array.get(s);
				//000001CA

				//1010 0100 \ 0001 0000
				// 0001 0000 1010 1100
				// this num triangles
				int nt = targetSTL.num_of_triangles;
				float[] tempV = targetSTL.vert;
				float[] tempN = targetSTL.norm;
				float tX = targetSTL.shift_x;
				float tY = targetSTL.shift_y;
				float tZ = targetSTL.bound_min_z;

				for( int n = 0 ; n < nt ; n++ )
				{
					int ID = n*9;
					wNorm.putFloat(tempN[ID]);
					wNorm.putFloat(tempN[ID+1]);
					wNorm.putFloat(tempN[ID+2]);
					wNorm.flip();
					dos.write(wNorm.array()); // norm z
					
					wVert.putFloat(tempV[ID] + tX); // x1
					wVert.putFloat(tempV[ID+1] + tY); // y1
					wVert.putFloat(tempV[ID+2] - tZ); // z1
					wVert.putFloat(tempV[ID+3] + tX); // x2
					wVert.putFloat(tempV[ID+4] + tY); // y2
					wVert.putFloat(tempV[ID+5] - tZ); // z2
					wVert.putFloat(tempV[ID+6] + tX); // x3
					wVert.putFloat(tempV[ID+7] + tY); // y3
					wVert.putFloat(tempV[ID+8] - tZ); // z3
					wVert.flip();
					dos.write(wVert.array());
					
					// skip 2 byte
					dos.writeByte((int)0);
					dos.writeByte((int)0);
				} // for n
				
			}// for numSTL
			
			// close stream
			dos.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} // merge stl files()
	
	//**********************************************************
	//**********************************************************
	
	void run_slicer_repair()
	{
		//
		LB_log.setText("repair STL");
		
		// get current directory
		String curDir = System.getProperty("user.dir");
		String slicer_path = new String(curDir + "/Slic3r/Slic3r-console.exe");
		String repair_arg = new String("--repair");
		String mergeSTL_path = new String(curDir + "/merged.stl");
		
		ProcessBuilder slicer_process = new ProcessBuilder(slicer_path, repair_arg, mergeSTL_path);
		
		try {
			Process process = slicer_process.start();
			try {
				// wait for repair process
				int exitCode = process.waitFor();
				System.out.println("repair process exit code : " + exitCode );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	} // run slicer repair
	

}
