//import java.awt.FlowLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

public class Dialog extends JDialog{

	JLabel instruction=new JLabel("Graphing windspeed and force over time");
	JLabel instruction2=new JLabel("How long would you like to take measurements for?");
	//JTextField seconds=new JTextField(5);
	JLabel s=new JLabel("second(s)");
	
	boolean cancelled=false;
	
	String[] choices = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	JComboBox seconds = new JComboBox(choices);
	
	JButton next=new JButton("Continue"),cancel=new JButton("Cancel");
	
	public Dialog()
	{
		setSize(350,160);
		
		JTextField filler1=new JTextField(41);
		filler1.setEditable(false);
		filler1.setFont(new Font("Times New Roman",Font.PLAIN, 10));
		add(filler1);
		
		setLayout(new FlowLayout());
		instruction.setFont(new Font("Calibri",Font.BOLD, 17));
		add(instruction);
		add(instruction2);
		
		JPanel input = new JPanel();
		input.setLayout(new BoxLayout(input,BoxLayout.LINE_AXIS));
		input.add(Box.createRigidArea(new Dimension(50,0)));
		input.add(seconds);
		input.add(Box.createRigidArea(new Dimension(5,0)));
		input.add(s);
		input.add(Box.createRigidArea(new Dimension(50,0)));
		add(input);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.LINE_AXIS));
		buttons.add(Box.createRigidArea(new Dimension(50,0)));
		buttons.add(next);
		next.addActionListener(new closer());
		buttons.add(Box.createRigidArea(new Dimension(5,0)));
		buttons.add(cancel);
		cancel.addActionListener(new closer());
		buttons.add(Box.createRigidArea(new Dimension(50,0)));
		add(buttons);
		
		JTextField filler2=new JTextField(41);
		filler2.setEditable(false);
		filler2.setFont(new Font("Times New Roman",Font.PLAIN, 10));
		add(filler2);
		
		setModal(true);
		setUndecorated(true);
		setLocationRelativeTo(null);
		
		
	}
	
	int showDialog()
	{
		setVisible(true);
		if(cancelled)
			return -1;
		else
			return seconds.getSelectedIndex()+1;
	}
	
	
	
	//Button listeners
		private class closer implements ActionListener
		{
			public void actionPerformed (ActionEvent e)
			{	
				
				String button = e.getActionCommand();

	            if(button.equals("Continue"))
	            	cancelled=false;
	            else if(button.equals("Cancel"))
	            	cancelled=true;
				
				setVisible(false);
				dispose();
			}
		}
	
	public static void main(String[] arguments) {
		Dialog dialog=new Dialog();
	}
}
