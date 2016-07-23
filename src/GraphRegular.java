import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class GraphRegular extends JPanel {
	
	double[][] toGraph;
	
	public GraphRegular(double[][] toGraph)
	{
		this.toGraph=toGraph;
	}
	
	Font titles = new Font("Times New Roman", Font.PLAIN, 25);
	Font axes = new Font("Times New Roman", Font.PLAIN, 15);
	Font exponent = new Font("Times New Roman", Font.PLAIN, 7);
	
	public void paintComponent(Graphics g)
    { 
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0,0,1000,700);
        
        g.setColor(Color.black);
        
        //Titles of graphs
        g.setFont(titles);
        g.drawString("Drag, Lift, and Windspeed in Wind Tunnel", 300, 30);
        //g.drawString("Lift", 474, 370);
        
        //Horizontal axes labels 
        g.setFont(axes);
        g.drawString("Force",3,300);
        g.drawString("(mN)", 4, 300+15);
        g.drawString("Wind",950+2+2,300);
        g.drawString("speed",950+2+2,315);
        g.drawString("(ms  )", 950+3, 330+2);
        g.setFont(exponent);
        g.drawString("-1", 967+9, 323+2+2);
        
        g.setFont(axes);
        //Vertical axes labels
        //g.drawString("Distance Along Airfoil (cm)",420,310+13);
        g.drawString("Time(s)",420,650+13);
        
        int origin1y=620, top1y=40;
        int origin1x=75-3, side1x=950-38;
        
        double current=0, current1=0;
        String toPrint, toPrint1;
        
        //Axes lines
        g.drawLine(origin1x, top1y, origin1x, origin1y); //Vertical axis
        g.drawLine(side1x, top1y, side1x, origin1y); //Vertical axis
        g.drawLine(origin1x, origin1y, side1x, origin1y); //Horizontal axis
        
       
        int fontStartx=origin1x-23;
        
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(1);
        
        //Force range
        double min=findMin(toGraph[1]);
        double max=findMax(toGraph[1]);
        if(greaterThan(findMin(toGraph[2]),min)==-1)
        	min=findMin(toGraph[2]);
        if(greaterThan(findMax(toGraph[2]),max)==1)
        	max=findMax(toGraph[2]);
        
        System.out.println(min);
        System.out.println(max);
        
        double intervalForce=(max-min)/25;
        double formatting=Math.round(intervalForce*10);
        double forceScale=formatting/10;
        
        //Wind range
        double minWind=findMin(toGraph[3]);
        double maxWind=findMax(toGraph[3]);
        
        //System.out.println(minWind);
        //System.out.println(maxWind);
        
        double intervalWind=(maxWind-minWind)/23;
        formatting=Math.round(intervalWind*10);
        double windScale=formatting/10;
        
        //Vertical axes ticks
        for(int i=origin1y-23;i>=top1y;i-=23)
        {
        	g.setColor(Color.black);
        	current+=forceScale;
        	toPrint=df.format(current);
        	
        	g.drawString(toPrint,fontStartx-7,i+5);
        	
        	current1+=windScale;
        	toPrint1=df.format(current1);
        	
        	g.drawString(toPrint1, side1x+7, i+5);
        	
        	g.drawLine(origin1x-5, i, origin1x, i);
        	g.drawLine(side1x+5, i, side1x, i);
        	
        	g.setColor(Color.gray);
        	g.drawLine(origin1x, i, side1x, i);
        }
        
        current=0;
        int fontStarty=origin1y+20;
        
        double horzSpacing=(side1x-origin1x)/20;
        int horzSpacingGo=(int)horzSpacing;
        
        int counter=0;
        
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        
        g.setColor(Color.black);
        g.drawString("0", origin1x-7, origin1y+15);
        
        //Horizontal axes ticks
        for(int i=origin1x+horzSpacingGo;i<=side1x;i+=horzSpacingGo)
        {
        	g.setColor(Color.black);
        	//current+=lengthScale;
        	toPrint=df.format(toGraph[0][counter]/1000);
        	
        	g.drawString(toPrint,i-8,fontStarty);
        	
        	g.drawLine(i, origin1y+5, i, origin1y);
        	g.setColor(Color.gray);
        	if(side1x!=i)
        		g.drawLine(i, origin1y, i, top1y);
        	counter++;
        }
        
        g.setColor(Color.white);
        g.fillRect(side1x-90-50,top1y+10,130,110);
        g.setColor(Color.black);
        g.drawRect(side1x-90-50,top1y+10,130,110);
        
        
        Color dragColor=new Color(32,216,255);
        Color liftColor=new Color(255,0,102);
        Color windColor=new Color(204,188,34);
        
        Font legend = new Font("Times New Roman", Font.PLAIN, 20);
        
        
        g.setFont(legend);
        g.drawString("Drag", side1x-60-50, top1y+35);
        g.drawString("Lift", side1x-60-50, top1y+70);
        g.drawString("Wind speed", side1x-60-50, top1y+70+35);
        
        g.setColor(dragColor);
        g.fillRect(side1x-80-50, top1y+22, 12, 12);
        g.setColor(liftColor);
        g.fillRect(side1x-80-50, top1y+57, 12, 12);
        g.setColor(windColor);
        g.fillRect(side1x-80-50, top1y+57+35, 12, 12);
        
        int shiftVertical1=0, shiftVertical2=0;
        //int shiftHorizontal;
        
       	g.setColor(liftColor);
    	shiftVertical2=(int)(toGraph[1][0]/forceScale*23);
    	g.drawLine(origin1x, origin1y, origin1x+horzSpacingGo,  origin1y-shiftVertical2);
    	
    	g.setColor(dragColor);
    	shiftVertical2=(int)(toGraph[2][0]/forceScale*23);
    	g.drawLine(origin1x, origin1y, origin1x+horzSpacingGo,  origin1y-shiftVertical2);
    	
    	g.setColor(windColor);
    	System.out.println(shiftVertical1);
    	shiftVertical2=(int)(toGraph[3][0]/windScale*23);
    	g.drawLine(origin1x, origin1y, origin1x+horzSpacingGo,  origin1y-shiftVertical2);
        
        //Draw the lines
        for(int i=1;i<20;i++)
        {
        	g.setColor(liftColor);
        	shiftVertical1=(int)(toGraph[1][i-1]/forceScale*23);
        	System.out.println(shiftVertical1);
        	shiftVertical2=(int)(toGraph[1][i]/forceScale*23);
        	g.drawLine(origin1x+i*horzSpacingGo, origin1y-shiftVertical1, origin1x+(i+1)*horzSpacingGo,  origin1y-shiftVertical2);
        	
        	g.setColor(dragColor);
        	shiftVertical1=(int)(toGraph[2][i-1]/forceScale*23);
        	System.out.println(shiftVertical1);
        	shiftVertical2=(int)(toGraph[2][i]/forceScale*23);
        	g.drawLine(origin1x+i*horzSpacingGo, origin1y-shiftVertical1, origin1x+(i+1)*horzSpacingGo,  origin1y-shiftVertical2);
        	
        	g.setColor(windColor);
        	shiftVertical1=(int)(toGraph[3][i-1]/windScale*23);
        	System.out.println(shiftVertical1);
        	shiftVertical2=(int)(toGraph[3][i]/windScale*23);
        	g.drawLine(origin1x+i*horzSpacingGo, origin1y-shiftVertical1, origin1x+(i+1)*horzSpacingGo,  origin1y-shiftVertical2);
        }
        
        
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
		double[][] testing=new double[4][20];
		
		for(int i=1;i<=20;i++)
		{
			testing[0][i-1]=1000/20*i;
			testing[1][i-1]=Math.random()*50;
			testing[2][i-1]=Math.random()*50;
			testing[3][i-1]=Math.random()*25;
		}
		
		GraphRegular rectObj = new GraphRegular(testing);
		myWindow.add(rectObj);
		myWindow.setSize(new Dimension(1000,700));
		myWindow.setResizable(false);
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.setVisible(true);
		myWindow.setLocationRelativeTo(null);
		
	}

}
