package main_package;

public class EntryClass {

	static GUI_Manager GUI_obj;
	static GL_Manager gl_obj;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Entry Class main Func");
		
		// parameter window setup
		GUI_obj = new GUI_Manager();
		
		// GL setup ( GL loop run on another thread )
		gl_obj = new GL_Manager();
		Thread th = new Thread(gl_obj);
		th.start();
		
		System.out.println("Entry main end");
	}

}
