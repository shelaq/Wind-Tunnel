import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class Graph extends JPanel {

	//JPanel draw;
	
	//public Graph()
	//{
		//Overall Setup-------------------------------------------------------------------------
		//setPreferredSize(new Dimension(200,500));
		//JButton on=new JButton("sds");
		//add(on);
		//on.addActionListener(new closeHandler());
		
		//draw = new JPanel();
		//draw.setPreferredSize(new Dimension(500, 425));
		//draw.setBorder(BorderFactory.createLineBorder(Color.black));
		//add(draw);
		//JButton blah=new JButton("ssdsdsdsd");
	//}
	
/*	class closeHandler implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			//myWindow.dispose();
		}
	}*/
	
	double[][] toGraph;
	
	public Graph(double[][] toGraph)
	{
		this.toGraph=toGraph;
	}
	
	Font titles = new Font("Times New Roman", Font.PLAIN, 25);
	Font axes = new Font("Times New Roman", Font.PLAIN, 15);
	
	public void paintComponent(Graphics g)
    { 
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0,0,1000,700);
        
        g.setColor(Color.black);
        //Center divider
        //g.setColor(Color.black);
        //g.drawLine(0,333,1000,333);
        //g.drawLine(0,335,1000,335);
        //g.drawLine(0,337,1000,337);
        
        //Titles of graphs
        g.setFont(titles);
        g.drawString("Drag and Lift on Airfoil", 390, 30);
        //g.drawString("Lift", 474, 370);
        
        //Horizontal axes labels 
        g.setFont(axes);
        g.drawString("Force",3,300);
        g.drawString("(N)", 4, 300+15);
        //g.drawString("Force",3,500);
        //g.drawString("(mN)", 4, 500+15);
        //Vertical axes labels
        //g.drawString("Distance Along Airfoil (cm)",420,310+13);
        g.drawString("Distance Along Airfoil (cm)",420,650+13);
        
        int origin1y=620, top1y=40;
        int origin1x=75, side1x=950;
        //double forceScale=0.1;
        
        double current=0;
        String toPrint;
        
        //Axes lines
        g.drawLine(origin1x, top1y, origin1x, origin1y);
        g.drawLine(origin1x, origin1y, side1x, origin1y);
        
       
        int fontStartx=origin1x-33;
        
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);
        
        
        
      //Wind range
        double min=findMin(toGraph[1]);
        double max=findMax(toGraph[1]);
        
        //System.out.println(min);
        //System.out.println(max);
        
        double intervalForce=(max-min)/23;
        double formatting=Math.round(intervalForce*10);
        double forceScale=formatting/10;
        
        //Vertical axes ticks
        for(int i=origin1y-23;i>=top1y;i-=23)
        {
           	g.setColor(Color.black);
        	current+=forceScale;
        	toPrint=df.format(current);
        	
        	g.drawString(toPrint,fontStartx,i+5);
        	
        	g.drawLine(origin1x-5, i, origin1x, i);
        	g.setColor(Color.gray);
        	g.drawLine(origin1x, i, side1x, i);
        }
        
        current=0;
        int fontStarty=origin1y+20;
        
        double horzSpacing=(side1x-origin1x)/14;
        int horzSpacingGo=(int)horzSpacing;
        double lengthScale=1;
        
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        
        //Horizontal axes ticks
        for(int i=origin1x+horzSpacingGo;i<=side1x;i+=horzSpacingGo)
        {
        	g.setColor(Color.black);
        	current+=lengthScale;
        	toPrint=df.format(current);
        	
        	g.drawString(toPrint,i-8,fontStarty);
        	
        	g.drawLine(i, origin1y+5, i, origin1y);
        	g.setColor(Color.gray);
        	g.drawLine(i, origin1y, i, top1y);
        }
        
        g.setColor(Color.white);
        g.fillRect(side1x-90,top1y+10,80,75);
        g.setColor(Color.black);
        g.drawRect(side1x-90,top1y+10,80,75);
        
        Color dragColor=new Color(32,216,255);
        Color liftColor=new Color(255,0,102);
        
        Font legend = new Font("Times New Roman", Font.PLAIN, 20);
        
        
        g.setFont(legend);
        g.drawString("Drag", side1x-60, top1y+35);
        g.drawString("Lift", side1x-60, top1y+70);
        
        g.setColor(dragColor);
        g.fillRect(side1x-80, top1y+22, 12, 12);
        g.setColor(liftColor);
        g.fillRect(side1x-80, top1y+57, 12, 12);
        
        int shiftVertical1, shiftVertical2, shiftHorizontal1, shiftHorizontal2;
        
       	g.setColor(liftColor);
    	shiftVertical2=(int)(toGraph[1][0]/forceScale*23);
    	g.drawLine(origin1x, origin1y, origin1x+horzSpacingGo/2,  origin1y-shiftVertical2);
    	
    	g.setColor(dragColor);
    	shiftVertical2=(int)(toGraph[2][0]/forceScale*23);
    	g.drawLine(origin1x, origin1y, origin1x+horzSpacingGo/2,  origin1y-shiftVertical2);
        
        //Draw the lines
        for(int i=1;i<26;i++)
        {
        	shiftHorizontal1=(int)(toGraph[0][i-1]*horzSpacingGo);
        	shiftHorizontal2=(int)(toGraph[0][i]*horzSpacingGo);
        	
        	g.setColor(liftColor);
        	shiftVertical1=(int)(toGraph[1][i-1]/forceScale*23);
        	//System.out.println(shiftVertical1);
        	shiftVertical2=(int)(toGraph[1][i]/forceScale*23);
        	g.drawLine(origin1x+shiftHorizontal1, origin1y-shiftVertical1, origin1x+shiftHorizontal2,  origin1y-shiftVertical2);
        	
        	g.setColor(dragColor);
        	shiftVertical1=(int)(toGraph[2][i-1]/forceScale*23);
        	//System.out.println(shiftVertical1);
        	shiftVertical2=(int)(toGraph[2][i]/forceScale*23);
        	g.drawLine(origin1x+shiftHorizontal1, origin1y-shiftVertical1, origin1x+shiftHorizontal2,  origin1y-shiftVertical2);
        	
        }
        
/*        
        
        origin1y=620;
        top1y=380;
        origin1x=75;
        side1x=950;
        forceScale=0.1;

        current=0;

        //Axes lines
        g.setColor(Color.black);
        g.drawLine(origin1x, top1y, origin1x, origin1y);
        g.drawLine(origin1x, origin1y, side1x, origin1y);


        //fontStartx=49;
        
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);

        //Horizontal axes ticks
        for(int i=origin1y-20;i>=top1y;i-=20)
        {
        	g.setColor(Color.black);
        	current+=forceScale;
        	toPrint=df.format(current);
        	
        	g.drawString(toPrint,fontStartx,i+5);
        	
        	g.drawLine(origin1x-5, i, origin1x, i);
        	g.setColor(Color.gray);
        	g.drawLine(origin1x, i, side1x, i);
        }
        
        current=0;
        fontStarty=origin1y+20;
        
        horzSpacing=(side1x-origin1x)/20;
        horzSpacingGo=(int)horzSpacing;
        lengthScale=0.7;
        
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        
        //Vertical axes ticks
        for(int i=origin1x+horzSpacingGo;i<=side1x;i+=horzSpacingGo)
        {
        	g.setColor(Color.black);
        	current+=lengthScale;
        	toPrint=df.format(current);
        	
        	g.drawString(toPrint,i-8,fontStarty);
        	
        	g.drawLine(i, origin1y+5, i, origin1y);
        	g.setColor(Color.gray);
        	g.drawLine(i, origin1y, i, top1y);
        }*/
    }
	
	
	double findMax(double[] values)
	{
		double max=values[0];
		for(int i=0;i<values.length;i++)
		{
			if(greaterThan(values[i],max)==1)
				max=values[i];
		}
		return max;
	}
	
	double findMin(double[] values)
	{
		double min=values[0];
		
		for(int i=0;i<values.length;i++)
		{
			if(greaterThan(values[i],min)==-1)
				min=values[i];
		}
		return min;
	}
	
	//Check if num1 is greater than num2
	//Return 1 if greater, -1 if smaller, 0 if equal
	public static int greaterThan (double num1, double num2)
	{
		// compares two Double objects numerically
	     Double obj1 = new Double(num1);
	     Double obj2 = new Double(num2);
	     int retval =  obj1.compareTo(obj2);
	    
	     if(retval > 0) {
	        return 1;
	     }
	     else if(retval < 0) {
	        return -1;
	     }
	     else {
	        return 0;
	     }
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame myWindow=new JFrame("Graphing Interface");
		double[][] testing=new double[3][26];
		
		for(int i=1;i<=20;i++)
		{
			testing[0][i-1]=1000/20*i;
			testing[1][i-1]=Math.random()*10;
			testing[2][i-1]=Math.random()*10;
		}
		
		Graph rectObj = new Graph(testing);
		myWindow.add(rectObj);
		myWindow.setSize(new Dimension(1000,700));
		myWindow.setResizable(false);
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.setVisible(true);
		myWindow.setLocationRelativeTo(null);
		
	}

}
