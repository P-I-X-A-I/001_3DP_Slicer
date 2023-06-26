package main_package;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;



public class GUI_Manager implements ActionListener{

	JFrame paramWindow;
	
	// parameter GUI
	public JSlider SLI_layer_height;
	public JSlider SLI_perimeters;
	public JSlider SLI_infill;
	
	public JRadioButton RD_grid;
	public JRadioButton RD_honeycomb;
	public JRadioButton RD_gyroid;
	
	public JCheckBox CHK_raft;
	public JCheckBox CHK_support;
	
	public JSlider SLI_temperature;
	
	
	// STL list
	public DefaultListModel<String> ListModel;
	public JList<String> ListView_STL;
	//public ArrayList<STL_Class> stl_Array = new ArrayList<STL_Class>();
	//public ArrayList<String> stl_Names = new ArrayList<String>();
	
	
	public GUI_Manager()
	{
		
		System.out.println("GUI Manager init");
		
		paramWindow = new JFrame("parameters");
		paramWindow.setBounds(100, 100, 420, 450);
		paramWindow.setResizable(false);
		paramWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		paramWindow.setLayout(null);
		paramWindow.setVisible(true);
		
		// setup GUI
		JLabel LB_selectPrinter = new JLabel("1.Select Printer");
		paramWindow.getContentPane().add(LB_selectPrinter);
		LB_selectPrinter.setBounds(10, 10, 150, 20);
		
		// printer combobox
		String[] printers = {"printer-1", "printer-2", "printer-3"};
		JComboBox<String> combo_obj = new JComboBox<>(printers);
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
		JScrollPane scroll_obj = new JScrollPane(ListView_STL);
		paramWindow.getContentPane().add(scroll_obj);
		scroll_obj.setBounds(10, 140, 170, 100);
		
		// add ListSelectionListener
		ListSelectionListener listListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if( e.getValueIsAdjusting() == false) // mouse released
				{
					int sel = ListView_STL.getSelectedIndex();
					System.out.println("Listview listener "+sel);
					if(sel != -1)
					{
						// *******************
						// set selected STL
					}
					else
					{
						// no selection
					}
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
		paramWindow.getContentPane().add(BT_plus45);
		paramWindow.getContentPane().add(BT_minus45);
		BT_plus45.setBounds(10, 285, 80, 20);
		BT_minus45.setBounds(100, 285, 80, 20);
		BT_plus45.setActionCommand("rotate_plus");
		BT_minus45.setActionCommand("rotate_minus");
		BT_plus45.addActionListener(this);
		BT_minus45.addActionListener(this);
		
		// radio button
		JRadioButton RD_x = new JRadioButton("x");
		JRadioButton RD_y = new JRadioButton("y");
		JRadioButton RD_z = new JRadioButton("z");
		ButtonGroup RD_group = new ButtonGroup();
		RD_group.add(RD_x);
		RD_group.add(RD_y);
		RD_group.add(RD_z);
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
		SLI_layer_height = new JSlider(0, 5, 2);
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
				String mmStr = String.format("Layer height : %1.2fmm", 0.2 + SLI*0.05);
				LB_layer_height.setText(mmStr);
			}
		});
		
		
		// perimeter
		JLabel LB_perimeter = new JLabel("Perimeters : 2");
		paramWindow.getContentPane().add(LB_perimeter);
		LB_perimeter.setBounds(220, 70, 200, 20);
		
		SLI_perimeters = new JSlider(0, 4, 1);
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
			}
			
		});
		
		
		// fill density
		JLabel LB_fill = new JLabel("Infill : 15%");
		paramWindow.getContentPane().add(LB_fill);
		LB_fill.setBounds(220, 130, 200, 20);
		
		SLI_infill = new JSlider(0, 10, 3);
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
				String infillStr = String.format("Infill : %d%%", SLI*5);
				LB_fill.setText(infillStr);
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
		CHK_support.setSelected(false);
		paramWindow.getContentPane().add(CHK_support);
		CHK_support.setBounds(210, 260, 80, 20);
		
		CHK_raft = new JCheckBox("raft");
		CHK_raft.setSelected(false);
		paramWindow.getContentPane().add(CHK_raft);
		CHK_raft.setBounds(300, 260, 80, 20);
		
		
		// temperature
		JLabel LB_temp = new JLabel("Temperature : 210");
		paramWindow.getContentPane().add(LB_temp);
		LB_temp.setBounds(220, 290, 200, 20);
		
		SLI_temperature = new JSlider(0, 10, 4);
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
				String tempStr = String.format("Temperature : %d", 190 + SLI*5);
				LB_temp.setText(tempStr);
			}
			
		});
		
		
		// convert button
		JButton BT_conv = new JButton("convert");
		paramWindow.getContentPane().add(BT_conv);
		BT_conv.setBounds(125, 375, 150, 20);
		BT_conv.setActionCommand("convert");
		BT_conv.addActionListener(this);
		
		//****************************************************************
		// set Key Event Listener ****************************************
		KeyListener keyL = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println("GUI key:"+e.getKeyCode());
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
		}
		else if(actionCommand.equals("BT_DELETE"))
		{
			this.delete_STL_file();
		}
		else if(actionCommand.equals("rotate_plus"))
		{
			System.out.println("rotate plus");
		}
		else if(actionCommand.equals("rotate_minus"))
		{
			System.out.println("rotate minus");
		}
		
		else if(actionCommand.equals("radio_x"))
		{
			System.out.println("radio x");
		}
		else if(actionCommand.equals("radio_y"))
		{
			System.out.println("radio y");
		}
		else if(actionCommand.equals("radio_z"))
		{
			System.out.println("radio z");
		}
		else if(actionCommand.equals("convert"))
		{
			System.out.println("convert button");
			
		}
	}// action performed
	
	
	void load_STL_file()
	{
		// open file chooser
		JFileChooser fc = new JFileChooser();
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
	
}
