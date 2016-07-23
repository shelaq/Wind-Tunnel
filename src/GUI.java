import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.awt.geom.AffineTransform;

import java.util.List;

import org.zu.ardulink.Link;
import org.zu.ardulink.protocol.IProtocol;
import org.zu.ardulink.event.AnalogReadChangeEvent;
import org.zu.ardulink.event.AnalogReadChangeListener;
import org.zu.ardulink.event.DigitalReadChangeEvent;
import org.zu.ardulink.event.DigitalReadChangeListener;


public class GUI extends JPanel
{
	//Panel size
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 700;

	//GUI components
	private JPanel graphic, info, titles, buttons; //JPanels for each section of the GUI
	//Graphic
	private final int FONTSIZE=35, FONTSIZE2=25;
	private ImageIcon foilPic= new ImageIcon("Symmetrical.jpg");
	//Information Panel
	private JPanel col1,col2,col3,col4,col5,col6,col7,col8,col9,col10,col11; //Layout
	private JPanel col12,col13,col14,col15,col16, col17; //Layout
	private JPanel filler, filler1; //Layout
	private JTextField length, weight, lift, drag, measured, equiv; //Values to be displayed
	private JTextField exp1, exp2, exp3, exp4, exp5; //Exponents to be displayed
	private JLabel le, we, li, li2, dr, dr2, me, me2, eq, eq2; //Labels for value display
	private JLabel cm, t1, t2, t3, t4, t5, n1, n2, n3, k1, k2; //More labels
	//Title panel
	private JLabel chooseFoil, graphFoil, powerSetting; //Labels for the buttons
	//Buttons panel
	private JRadioButton foil1,foil2,foil3,foil4; //Radio buttons to choose foils
	private JButton on, off, graph; //Buttons at the bottom of the screen
	
	//Controlling the fan etc
	private boolean itsOn=true;

	//Drawing arrows in the graphic panel
	private final int ARR_SIZE = 10; //For arrow drawing method
	private Color verticalArrow = new Color (37, 120, 178); //Colour of vertical arrow
	private Color horizontalArrow = new Color (128,204,42); //Colour of horizontal arrow
	private int vertStretch=69,horzStretch=40; //Initial length of arrows
	
	//Initiating classes for needed methods
	AirfoilInfo airfoil=new AirfoilInfo();
	NumManip manip=new NumManip();
	ArduinoControl arduino=new ArduinoControl();

	//Link to connect to Arduino
	static Link link = Link.getDefaultInstance();
	private int forcePin1=0, forcePin2=1, forcePin3=2, forcePin4=3, windPin=4, positionPin=5;
	private int fanSwitch=7, windType=6, fanPot=53;

	//Display number control
	int counter=0, windCounter=0; //Counter - used to show every 5 values read in to prevent fluctuations
	boolean showMeasured=true; 
	boolean regularFoil=true; //Determine whether it's on original program or variable program
	boolean fanOn=true;
	
	//Values read from the strain gauges
	double[] strainReadings=new double[4]; //Store the current value being read from the 4 strain gauges
	double[] baseForceReadings=new double[4];
	
	//Calculation values
	int strainGaugeBase=200; //Discard any values below this number, because they don't work
	double currentLift=0,currentDrag=0,currentWind=0,currentEquivWind=0; //Current values to be displayed
	
	int[]windReadings=new int[10];
	
	
	public GUI()
	{
		//Overall Setup-------------------------------------------------------------------------
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		//Set the layout type
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		//Setting up 3 panels for picture of airfoil, information, and buttons
		setupBeginning();
		//Information panel
		setupMiddle();
		//Buttons at bottom
		setupBottom();
		
		//Connection to the Arduino
				try {
					//Connect to the link
					List<String> portList = link.getPortList();
					if(portList != null && portList.size() > 0) {
						port = portList.get(0);
						System.out.println("Connecting on port: " + port);
						boolean connected = link.connect(port);
						System.out.println("Connected:" + connected);
						Thread.sleep(1000);

						link.sendPowerPinSwitch(fanPot, IProtocol.HIGH);
						
						//Start monitoring all the pins
						link.startListenAnalogPin(forcePin1);
						link.startListenAnalogPin(forcePin2);
						link.startListenAnalogPin(forcePin3);
						link.startListenAnalogPin(forcePin4);
						link.startListenAnalogPin(windPin);
						link.startListenAnalogPin(positionPin);
						link.startListenAnalogPin(fanSwitch);
						link.startListenAnalogPin(windType);
						//Reading strain gauges---------------------------------------------------------------
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								if(fanOn)
								{
									strainReadings[0]=e.getValue(); //Store value in force array
									counter++; //Increment counter
									if(counter%10==0) //Only calculate force every 5 readings
									{
										calculateForce(); //Calculate force and display it
										counter=0; //Reset counter
									}
								}
								else
									baseForceReadings[0]=e.getValue();
							}
							@Override
							public int getPinListening() {
								return forcePin1;
							}
						});
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {

							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								if(fanOn)
									strainReadings[1]=e.getValue(); //Store value in force array
								else
									baseForceReadings[1]=e.getValue();
							}
							@Override
							public int getPinListening() {
								return forcePin2;
							}
						});
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {

							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								if(fanOn)
								strainReadings[2]=e.getValue(); //Store value in force array
								else
									baseForceReadings[2]=e.getValue();
							}
							@Override
							public int getPinListening() {
								return forcePin3;
							}
						});
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {

							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								if(fanOn)
								strainReadings[3]=e.getValue(); //Store value in force array
								else
									baseForceReadings[3]=e.getValue();
							}
							@Override
							public int getPinListening() {
								return forcePin4;
							}
						});
						//Readings for wind control-------------------------------------------------------------
						//Read whether fan should be on or off
						
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
							int sum=0,counter=0,average=0;
							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								
//								sum+=e.getValue();
//								counter++;
//								if(counter%20==0)
//									average=sum/20;
//								
//								
//								if(average<1000)
//								{
//									link.sendPowerPinSwitch(fanPot, IProtocol.LOW);
//									System.out.println("should be off");
//									fanOn=false;
//								}
//								else if(fanOn)
//								{
//									
//								}
//								else if(!fanOn&&average>=1000)
//								{
//									link.sendPowerPinSwitch(fanPot, IProtocol.HIGH);
//									System.out.println("Should be on");
//									fanOn=true;
//								}
								
								
								//System.out.println(e.getValue());
							}
							
							@Override
							public int getPinListening() {
								return fanSwitch;
							}
						});
						//Read whether it should be displaying measured or equivalent wind speed
//						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
//							@Override
//							public void stateChanged(AnalogReadChangeEvent e) {
//								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
//								//System.out.println(e.getIncomingMessage());
//								//if(e.getValue()>1000)
//									//System.out.println("on");
//									//showMeasured=true;
//									//arduino.sevenSegment(link,currentWind);
//								//else if(e.getValue()<1000)
//									//System.out.println("off");
//									//showMeasured=false;
//									//arduino.sevenSegment(link,currentEquivWind);
//								
//								//System.out.println(e.getValue());
//							}
//
//							@Override
//							public int getPinListening() {
//								return windType;
//							}
//						});
						//Read from differential pressure sensor and display wind speeds
						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {

							@Override
							public void stateChanged(AnalogReadChangeEvent e) {
								//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
								//System.out.println(e.getIncomingMessage());
								windReadings[windCounter]=e.getValue();
								windCounter++;
								if(windCounter%10==0)
								{
									calculateWind(windReadings);
								
								//calculateWind(e.getValue());
									windCounter=0;
								}
							}

							@Override
							public int getPinListening() {
								return windPin;
							}
						});
						//Determine where to show forces on propellor (not regular foil)-----------------------
//						link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
//						@Override
//						public void stateChanged(AnalogReadChangeEvent e) {
//							//System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
//							//System.out.println(e.getIncomingMessage());
//							if(!regularFoil)
//								calculatePosition(e.getValue());
//						}
//
//						@Override
//						public int getPinListening() {
//							return positionPin;
//						}
//					});
						
						//Testing
//						for(int i=0;i<10;i++)
//						{
//							arduino.sevenSegment(link,i);
//							Thread.sleep(500);
//						}
						//arduino.sevenSegment(link,0,1);
						//link.sendCustomMessage("alp://ss01");
						//link.sendPowerPinSwitch(2, IProtocol.HIGH);
						//link.sendPowerPinSwitch(3, IProtocol.LOW);
						//link.sendPowerPinSwitch(4, IProtocol.LOW);
						//link.sendPowerPinSwitch(5, IProtocol.HIGH);
						//link.sendPowerPinSwitch(9,1);
						//link.sendPowerPinSwitch(12,0);
						//link.sendPowerPinSwitch(11,0);
						//link.sendPowerPinSwitch(10,0);
						//arduino.sevenSegment(link, 4444);
//					for(int i=0;i<1000;i++)
//						{
//							double num=(double)i/1000;
//							arduino.sevenSegment(link, num);
//							Thread.sleep(200);
//						}

					} else {
						//No connection
						System.out.println("No port found");
					}			
				}
				catch(Exception e) {
					e.printStackTrace();
				}

	}
	String port;
	void setUpLink()
	{
		//Connection to the Arduino
		try {
			//Connect to the link
			//List<String> portList = link.getPortList();
			//if(portList != null && portList.size() > 0) {
				//port = portList.get(0);
				System.out.println("Connecting on port: " + port);
				boolean connected = link.connect(port);
				System.out.println("Connected:" + connected);
				Thread.sleep(1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Average strain gauge values to get one number
	int averageStrain(double[] array)
	{
		//Add and divide by 4
		return (int)(array[0]+array[1]+array[2]+array[3])/4;
	}
	
	//Calculate forces over the propellor and store them in the array
	void calculatePropellor()
	{
		double sum=strainReadings[0]+strainReadings[3]-baseForceReadings[0]-baseForceReadings[3];
		double value=sum*0.0003*Math.sin(65*Math.PI/180)*2;
		value+=selectedWeight;
		
		sum=strainReadings[1]+strainReadings[1]-baseForceReadings[1]-baseForceReadings[1];
		double value1=sum*0.0003*Math.sin(65*Math.PI/180)*2;
		value1+=selectedWeight;
		
		currentLift=(double)positionOnSlider/13*value+(13-positionOnSlider)/13*value1;
		
		
		sum=strainReadings[0]-strainReadings[3]-baseForceReadings[0]+baseForceReadings[3];
		value=sum*0.0003*Math.cos(65*Math.PI/180)*2;
		
		sum=strainReadings[1]-strainReadings[1]-baseForceReadings[1]+baseForceReadings[1];
		value1=sum*0.0003*Math.cos(65*Math.PI/180)*2;
		
		currentDrag=(double)positionOnSlider/13*value+(13-positionOnSlider)/13*value1;
		
		
		for(int i=0;i<13;i++)
		{
			//positionOnSlider=i;
			
			//Fill in propellor array with calculations
			propellorForce[0][i]=i;
			propellorForce[1][i]=Math.random()*26;
			propellorForce[2][i]=Math.random()*26;
		}
//		propellorForce[0][26]=13.1;
//		propellorForce[1][26]=Math.random()*26;
//		propellorForce[2][26]=Math.random()*26;
	}
	
	
	JSlider slider;
	JDialog sliderDialog;
	
	void addSlider()
	{
		sliderDialog =new JDialog();
		sliderDialog.setSize(400,100);
		
		JSlider positionSlider = new JSlider(JSlider.HORIZONTAL,
		                                      0, 13, 7);
		positionSlider.addChangeListener(new SliderListener());

		//Turn on labels at major tick marks.
		positionSlider.setMajorTickSpacing(1);
		//framesPerSecond.setMinorTickSpacing(1);
		positionSlider.setPaintTicks(true);
		positionSlider.setPaintLabels(true);
		
		sliderDialog.add(positionSlider);
		sliderDialog.setLocationRelativeTo(null);
		sliderDialog.setVisible(true);
	}
	
	void sliderPosition(int value)
	{
		double num=(double)value/13;
		int shift=(int)(num*800);
		arrowCx=arrowOx-400+shift;
		repaint();
	}
	
	int upperBound=500,lowerBound=100;
	
	void calculatePosition(int value)
	{
		double num=(value-lowerBound)/(upperBound-lowerBound);
		int shift=(int)(num*800);
		arrowCx=arrowOx-400+shift;
		repaint();
	}
	
	void disposeSlider()
	{
		try
		{
		sliderDialog.dispose();
		}
		catch(Exception e)
		{
			
		}
	}
	
	int positionOnSlider;
	
	class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	       // if (!source.getValueIsAdjusting()) {
	            positionOnSlider = (int)source.getValue();
	            sliderPosition(positionOnSlider);
	        //}    
	    }
	}
	
	//CALCULATE LIFT AND DISPLAY ON GUI
	void calculateForce()
	{
		for(int i=0;i<4;i++)
		{
			if(strainReadings[i]<baseForceReadings[i])
				strainReadings[i]=0;
		}
		double sum=0;
		for(int i=0;i<4;i++)
		{
			sum+=strainReadings[i];
			sum-=baseForceReadings[i];
		}
		
		currentLift=sum*0.0003*Math.sin(65*Math.PI/180);
		currentLift+=selectedWeight;
		
		sum=strainReadings[0]+strainReadings[1]-strainReadings[2]-strainReadings[3];
		sum+=-baseForceReadings[0]-baseForceReadings[1]+baseForceReadings[2]+baseForceReadings[3];
		
		currentDrag=Math.abs(sum*0.0003*Math.cos(65*Math.PI/180));

		displayForce();
	}
	
	void displayForce()
	{
		//Lift
		double num=manip.threeSig(currentLift);
		int exp=manip.exp(currentLift);
		lift.setText(Double.toString(num));
		exp2.setText(Integer.toString(exp));
		
		vertStretch=(int)(currentLift*200);
		repaint();
		
		//Drag
		num=manip.threeSig(currentDrag);
		exp=manip.exp(currentDrag);
		drag.setText(Double.toString(num));
		exp3.setText(Integer.toString(exp));
		
		horzStretch=(int)(currentDrag*300);
		repaint();
	}

	int windBase=523; 
	
	//CALCULATE WIND SPEED AND DISPLAY ON GUI
	void calculateWind(int[] array)
	{
		double average;
		
		
		
		int sum=0;
		for(int i=0;i<array.length;i++)
		{
			sum+=array[i];
		}
		average=(double)sum/(double)array.length;
		
		System.out.println(average);
		
		double pascal=Math.abs(windBase-average)*4.618;
		currentWind=Math.sqrt(2*pascal/1.225);
		currentEquivWind=currentWind*airfoil.getChord(selectedFoil)*2*1000/3;
		//System.out.println(currentWind);
		//currentWind=value/20;
		//currentEquivWind=value/10;
		//currentWind=10000;
		//currentEquivWind=4345345;

		//Measured wind speed
		double num=manip.threeSig(currentWind);
		int exp=manip.exp(currentWind);
		measured.setText(Double.toString(num));
		exp4.setText(Integer.toString(exp));

		//Equivalent wind speed
		double num2=manip.threeSig(currentEquivWind);
		int exp2=manip.exp(currentEquivWind);
		equiv.setText(Double.toString(num2));
		exp5.setText(Integer.toString(exp2));

		
		
		if(showMeasured)
			arduino.sevenSegment(link,currentWind);
		else
			arduino.sevenSegment(link,currentEquivWind);
		//arduino.sevenSegment(link,manip.digit2(result1),2);
		//arduino.sevenSegment(link,manip.digit3(result1),3);
		//arduino.sevenSegment(link,manip.exp(result1),5);
	}

	
	
	//Method to draw arrow
	void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) g1.create();

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx*dx + dy*dy);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		g.drawLine(0, 0, len, 0);
		g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
				new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}


	

	    

	
	//Button listeners
	private class OnOffHandler implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			if(itsOn)
			{
				buttons.remove(off);
				buttons.add(on);
				itsOn=false;
				//disconnectReset();
				
				showMeasured=false;
			}
			else
			{
				buttons.remove(on);
				buttons.add(off);
				itsOn=true;
				//setUpLink();
				
				showMeasured=true;
			}
			buttons.revalidate();
			buttons.repaint();
		}
	}
	
	void disconnectReset()
	{
		boolean connected = link.disconnect();
	}

	double[][]toGraph=new double[4][20];
	double[][] propellorForce=new double[3][27];
	//int arrayCounter=0;
	int interval=0;
	
	//Button listeners
	private class graphHandler implements ActionListener
	{
		//counter for reading values
		int collectCounter = 0;
		//timer for reading values
		Timer timer;
		public void actionPerformed (ActionEvent e)
		{
			if(regularFoil)
			{
				Dialog getSeconds=new Dialog();
				int time= getSeconds.showDialog();

				if(time!=-1)
				{
					interval=time*1000/20;
					//arrayCounter=0;

					timer = new Timer(interval, new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent ex){
							add();
						}
					});
					timer.start();
	
					/*for(int i=0;i<20;i++)
					{
						fillArray(i,interval*(i+1));
						delay(interval);
					}*/

					//Fill in the values of wind speed and force in a 2D array, over time
					//Graph the array across the proper axis
					//Add link into constructor in ArduinoController
/*
					JFrame myWindow1=new JFrame("Graphing Interface");
					GraphRegular rectObj = new GraphRegular(toGraph);
					myWindow1.add(rectObj);
					myWindow1.setSize(new Dimension(1000,700));
					myWindow1.setResizable(false);
					myWindow1.setVisible(true);
					myWindow1.setLocationRelativeTo(null);*/
				}
			}
			else
			{
				calculatePropellor();
				
				JFrame myWindow1=new JFrame("Graphing Interface");
				Graph rectObj = new Graph(propellorForce);
				myWindow1.add(rectObj);
				myWindow1.setSize(new Dimension(1000,700));
				myWindow1.setResizable(false);
				myWindow1.setVisible(true);
				myWindow1.setLocationRelativeTo(null);
			}
				
		}
		public void add(){
			fillArray(collectCounter,interval*(collectCounter+1));
			collectCounter++;
			if(collectCounter == 19){
				timer.stop();
				JFrame myWindow1=new JFrame("Graphing Interface");
				GraphRegular rectObj = new GraphRegular(toGraph);
				myWindow1.add(rectObj);
				myWindow1.setSize(new Dimension(1000,700));
				myWindow1.setResizable(false);
				myWindow1.setVisible(true);
				myWindow1.setLocationRelativeTo(null);
			}
		}
	}
	
	
	void fillArray(int i, int currentTime)
	{
		toGraph[0][i]=currentTime;
		//Lift (CHANGE TO MILLINEWTONS)
		toGraph[1][i]=currentLift*1000;
		//toGraph[1][i]=Math.random()*6;
		//Drag (CHANGE TO MILLINEWTONS)
		toGraph[2][i]=currentDrag*1000;
		//toGraph[2][i]=Math.random()*5;
		//Wind speed
		toGraph[3][i]=currentWind;
		//toGraph[3][i]=Math.random()*4;
	}

	//Delay thread
	void delay(int milliseconds)
	{
		try {
		    Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}
	
	int selectedFoil=1;
	
	double selectedWeight=0;
	
	//Radio buttons
	private class RadioListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			JRadioButton button = (JRadioButton) e.getSource();

			// Set enabled based on button text (you can use whatever text you prefer)
			if (button.getText().equals("Symmetrical"))
			{
				foilPic = new ImageIcon("symmetrical.jpg");
				//Length
				length.setText(Double.toString(airfoil.getLSym()));
				//Weight
				selectedWeight=airfoil.getSym();
				double num=manip.threeSig(airfoil.getSym());
				int exp=manip.exp(airfoil.getSym());
				weight.setText(Double.toString(num));
				exp1.setText(Integer.toString(exp));
				
				regularFoil=true;
				selectedFoil=1;
				disposeSlider();
				arrowCx=arrowOx;
			}
			else if (button.getText().equals("Cambered"))
			{
				foilPic = new ImageIcon("cambered.jpg");
				//Length
				length.setText(Double.toString(airfoil.getLCam()));
				//Weight
				selectedWeight=airfoil.getCam();
				double num=manip.threeSig(airfoil.getCam());
				int exp=manip.exp(airfoil.getCam());
				weight.setText(Double.toString(num));
				exp1.setText(Integer.toString(exp));
				
				regularFoil=true;
				selectedFoil=2;
				disposeSlider();
				arrowCx=arrowOx;
			}
			else if (button.getText().equals("High camber"))
			{
				foilPic = new ImageIcon("highCamber.jpg");
				//Length
				length.setText(Double.toString(airfoil.getLHi()));
				//Weight
				selectedWeight=airfoil.getHi();
				double num=manip.threeSig(airfoil.getHi());
				int exp=manip.exp(airfoil.getHi());
				weight.setText(Double.toString(num));
				exp1.setText(Integer.toString(exp));
				
				regularFoil=true;
				selectedFoil=3;
				disposeSlider();
				arrowCx=arrowOx;
			}
			else if (button.getText().equals("Propellor"))
			{
				foilPic = new ImageIcon("propellor.jpg");
				//Length
				length.setText(Double.toString(airfoil.getLPro()));
				//Weight
				selectedWeight=airfoil.getPro();
				double num=manip.threeSig(airfoil.getPro());
				int exp=manip.exp(airfoil.getPro());
				weight.setText(Double.toString(num));
				exp1.setText(Integer.toString(exp));
				
				regularFoil=false;
				selectedFoil=4;
				addSlider();
			}

			repaint();
		}
	}
	
	//Main
	public static void main(String[] args)
	{
		JFrame myWindow=new JFrame("Wind Tunnel Analyzer");
		GUI rectObj = new GUI();
		myWindow.add(rectObj);
		myWindow.setSize(new Dimension(1000,700));
		myWindow.setResizable(false);
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.setVisible(true);
		myWindow.setLocationRelativeTo(null);
		
		//System.out.println(System.getProperty("java.library.path"));
	}

	int arrowOx=500,arrowOy=169;
	int arrowCx=500,arrowCy=169;

	//GUI Setup---------------------------------------------------------------------------------
	public void setupBeginning()
	{
		//Images
		graphic = new JPanel()
		{
			protected void paintComponent (Graphics g)
			{
				super.paintComponent (g);
				foilPic.paintIcon (this, g, 17, 35);
				g.setColor(verticalArrow);
				drawArrow(g, arrowCx, arrowCy, arrowCx, arrowCy-vertStretch);
				g.setColor(horizontalArrow);
				drawArrow(g, arrowCx, arrowCy, arrowCx-horzStretch, arrowCy);
			}
		};
		info = new JPanel();
		titles = new JPanel();
		buttons = new JPanel();
		graphic.setPreferredSize(new Dimension(1000, 425));
		info.setPreferredSize(new Dimension(1000, 200));
		titles.setPreferredSize(new Dimension(1000, 25));
		buttons.setPreferredSize(new Dimension(1000, 50));
		graphic.setBorder(BorderFactory.createLineBorder(Color.black));
		titles.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		add(graphic);
		add(info);
		add(titles);
		add(buttons);
	}

	//Information panel
	public void setupMiddle()
	{
		//Changeable text fields
		length = new JTextField(3);
		length.setText("sdsd");
		weight = new JTextField(3);
		lift = new JTextField(3);
		drag = new JTextField(3);
		measured = new JTextField(3);
		equiv = new JTextField(3);
		//Set font sizes
		length.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		weight.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		lift.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		drag.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		measured.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		equiv.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		//Exponents
		exp1 = new JTextField(3);
		exp2 = new JTextField(3);
		exp3 = new JTextField(3);
		exp4 = new JTextField(3);
		exp5 = new JTextField(3);
		//Titles
		le = new JLabel("x");
		we = new JLabel("W");
		li = new JLabel("F");
		li.setForeground(verticalArrow);
		li2 = new JLabel("L");
		li2.setForeground(verticalArrow);
		dr = new JLabel("F");
		dr.setForeground(horizontalArrow);
		dr2 = new JLabel("D");
		dr2.setForeground(horizontalArrow);
		me = new JLabel("V");
		me2 = new JLabel("M");
		eq = new JLabel("V");
		eq2 = new JLabel("E");
		//Set font sizes
		le.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		we.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		li.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		li2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE2));
		dr.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		dr2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE2));
		me.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		me2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE2));
		eq.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		eq2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE2));
		//Units
		cm = new JLabel("cm");
		n1 = new JLabel("N");
		n2 = new JLabel("N");
		n3 = new JLabel("N");
		k1 = new JLabel("ms^-1");
		k2 = new JLabel("kmh^-1");
		//Set font sizes
		cm.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		n1.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		n2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		n3.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		k1.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		k2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		//Times 10
		t1 = new JLabel("x10");
		t2 = new JLabel("x10");
		t3 = new JLabel("x10");
		t4 = new JLabel("x10");
		t5 = new JLabel("x10");
		//Set font sizes
		t1.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		t2.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		t3.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		t4.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));
		t5.setFont(new Font("Calibri", Font.PLAIN, FONTSIZE));		

		//Add everything in
		//Column 1
		col1=new JPanel();
		col1.setLayout(new BoxLayout(col1,BoxLayout.PAGE_AXIS));
		col1.add(Box.createRigidArea(new Dimension(0,35)));
		col1.add(le);
		col1.add(Box.createRigidArea(new Dimension(0,35)));
		col1.add(we);
		col1.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col1);
		//Column 2
		col2=new JPanel();
		col2.setLayout(new BoxLayout(col2,BoxLayout.PAGE_AXIS));
		col2.add(Box.createRigidArea(new Dimension(0,35)));
		col2.add(length);
		col2.add(Box.createRigidArea(new Dimension(0,35)));
		col2.add(weight);
		col2.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col2);
		//Column 3
		col3=new JPanel();
		col3.setLayout(new BoxLayout(col3,BoxLayout.PAGE_AXIS));
		col3.add(Box.createRigidArea(new Dimension(0,35)));
		col3.add(cm);
		col3.add(Box.createRigidArea(new Dimension(0,35)));
		col3.add(t1);
		col3.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col3);
		//Column 4
		col4=new JPanel();
		col4.setLayout(new BoxLayout(col4,BoxLayout.PAGE_AXIS));
		col4.add(Box.createRigidArea(new Dimension(0,70)));
		col4.add(exp1);
		col4.add(Box.createRigidArea(new Dimension(0,20)));
		info.add(col4);
		//Column 5
		col5=new JPanel();
		col5.setLayout(new BoxLayout(col5,BoxLayout.PAGE_AXIS));
		col5.add(Box.createRigidArea(new Dimension(0,100)));
		col5.add(n1);
		col5.add(Box.createRigidArea(new Dimension(0,20)));
		info.add(col5);
		//Second set=======
		filler = new JPanel();
		filler.setPreferredSize(new Dimension(30, 200));
		info.add(filler);
		//Column 6
		col6=new JPanel();
		col6.setLayout(new BoxLayout(col6,BoxLayout.PAGE_AXIS));
		col6.add(Box.createRigidArea(new Dimension(0,35)));
		col6.add(li);
		col6.add(Box.createRigidArea(new Dimension(0,35)));
		col6.add(dr);
		col6.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col6);
		//Column 7
		col7=new JPanel();
		col7.setLayout(new BoxLayout(col7,BoxLayout.PAGE_AXIS));
		col7.add(Box.createRigidArea(new Dimension(0,55)));
		col7.add(li2);
		col7.add(Box.createRigidArea(new Dimension(0,45)));
		col7.add(dr2);
		col7.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col7);
		//Column 8
		col8=new JPanel();
		col8.setLayout(new BoxLayout(col8,BoxLayout.PAGE_AXIS));
		col8.add(Box.createRigidArea(new Dimension(0,35)));
		col8.add(lift);
		col8.add(Box.createRigidArea(new Dimension(0,35)));
		col8.add(drag);
		col8.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col8);
		//Column 9
		col9=new JPanel();
		col9.setLayout(new BoxLayout(col9,BoxLayout.PAGE_AXIS));
		col9.add(Box.createRigidArea(new Dimension(0,35)));
		col9.add(t2);
		col9.add(Box.createRigidArea(new Dimension(0,35)));
		col9.add(t3);
		col9.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col9);
		//Column 10
		col10=new JPanel();
		col10.setLayout(new BoxLayout(col10,BoxLayout.PAGE_AXIS));
		col10.add(Box.createRigidArea(new Dimension(0,30)));
		col10.add(exp2);
		col10.add(Box.createRigidArea(new Dimension(0,70)));
		col10.add(exp3);
		col10.add(Box.createRigidArea(new Dimension(0,60)));
		info.add(col10);
		//Column 11
		col11=new JPanel();
		col11.setLayout(new BoxLayout(col11,BoxLayout.PAGE_AXIS));
		col11.add(Box.createRigidArea(new Dimension(0,30)));
		col11.add(n2);
		col11.add(Box.createRigidArea(new Dimension(0,40)));
		col11.add(n3);
		col11.add(Box.createRigidArea(new Dimension(0,20)));
		info.add(col11);
		//Third set=======
		filler1 = new JPanel();
		filler1.setPreferredSize(new Dimension(30, 200));
		info.add(filler1);
		//Column 12
		col12=new JPanel();
		col12.setLayout(new BoxLayout(col12,BoxLayout.PAGE_AXIS));
		col12.add(Box.createRigidArea(new Dimension(0,35)));
		col12.add(me);
		col12.add(Box.createRigidArea(new Dimension(0,35)));
		col12.add(eq);
		col12.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col12);
		//Column 13
		col13=new JPanel();
		col13.setLayout(new BoxLayout(col13,BoxLayout.PAGE_AXIS));
		col13.add(Box.createRigidArea(new Dimension(0,55)));
		col13.add(me2);
		col13.add(Box.createRigidArea(new Dimension(0,45)));
		col13.add(eq2);
		col13.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col13);
		//Column 14
		col14=new JPanel();
		col14.setLayout(new BoxLayout(col14,BoxLayout.PAGE_AXIS));
		col14.add(Box.createRigidArea(new Dimension(0,35)));
		col14.add(measured);
		col14.add(Box.createRigidArea(new Dimension(0,35)));
		col14.add(equiv);
		col14.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col14);
		//Column 15
		col15=new JPanel();
		col15.setLayout(new BoxLayout(col15,BoxLayout.PAGE_AXIS));
		col15.add(Box.createRigidArea(new Dimension(0,35)));
		col15.add(t4);
		col15.add(Box.createRigidArea(new Dimension(0,35)));
		col15.add(t5);
		col15.add(Box.createRigidArea(new Dimension(0,35)));
		info.add(col15);
		//Column 16
		col16=new JPanel();
		col16.setLayout(new BoxLayout(col16,BoxLayout.PAGE_AXIS));
		col16.add(Box.createRigidArea(new Dimension(0,30)));
		col16.add(exp4);
		col16.add(Box.createRigidArea(new Dimension(0,70)));
		col16.add(exp5);
		col16.add(Box.createRigidArea(new Dimension(0,60)));
		info.add(col16);
		//Column 17
		col17=new JPanel();
		col17.setLayout(new BoxLayout(col17,BoxLayout.PAGE_AXIS));
		col17.add(Box.createRigidArea(new Dimension(0,20)));
		col17.add(k1);
		col17.add(Box.createRigidArea(new Dimension(0,50)));
		col17.add(k2);
		col17.add(Box.createRigidArea(new Dimension(0,20)));
		info.add(col17);

		//Length
		length.setText(Double.toString(airfoil.getLSym()));

		//Set default text to symmetrical
		double num=manip.threeSig(airfoil.getSym());
		int exp=manip.exp(airfoil.getSym());
		weight.setText(Double.toString(num));
		exp1.setText(Integer.toString(exp));
	}

	//Buttons at bottom
	public void setupBottom()
	{
		//Titles panel
		titles.setLayout(new BoxLayout(titles,BoxLayout.LINE_AXIS));
		//Instantiate text fields
		chooseFoil=new JLabel("Current airfoil in test section");
		graphFoil=new JLabel("Generate a graph of force and lift");
		powerSetting=new JLabel("Power");
		titles.add(Box.createRigidArea(new Dimension(30,0)));
		titles.add(chooseFoil);
		titles.add(Box.createRigidArea(new Dimension(290,0)));
		titles.add(graphFoil);
		titles.add(Box.createRigidArea(new Dimension(210,0)));
		titles.add(powerSetting);
		titles.add(Box.createRigidArea(new Dimension(40,0)));
		//Buttons panel
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.LINE_AXIS));
		//Instantiate elements
		foil1=new JRadioButton("Symmetrical");
		foil2=new JRadioButton("Cambered");
		foil3=new JRadioButton("High camber"); //Change to "arbitrary" for general testing with new foils
		foil4=new JRadioButton("Propellor");
		graph=new JButton("Graph");
		graph.addActionListener(new graphHandler());
		on=new JButton("Meas");
		on.addActionListener(new OnOffHandler());
		off=new JButton("Equiv");
		off.addActionListener(new OnOffHandler());
		//Button Group
		ButtonGroup group=new ButtonGroup();
		group.add(foil1);
		group.add(foil2);
		group.add(foil3);
		group.add(foil4);
		//Add listener for buttons
		RadioListener radio = new RadioListener();
		foil1.setSelected(true);
		foil1.addActionListener(radio);
		foil2.addActionListener(radio);
		foil3.addActionListener(radio);
		foil4.addActionListener(radio);
		//Add into panel
		//buttons.add(Box.createRigidArea(new Dimension(55,0)));
		buttons.add(foil1);
		buttons.add(foil2);
		buttons.add(foil3);
		buttons.add(foil4);
		buttons.add(Box.createRigidArea(new Dimension(100,0)));
		buttons.add(graph);
		buttons.add(Box.createRigidArea(new Dimension(330,0)));
		buttons.add(off);
	}

}




