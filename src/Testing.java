import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import java.util.List;

import org.zu.ardulink.Link;
import org.zu.ardulink.protocol.IProtocol;
import org.zu.ardulink.event.AnalogReadChangeEvent;
import org.zu.ardulink.event.AnalogReadChangeListener;
import org.zu.ardulink.event.DigitalReadChangeEvent;
import org.zu.ardulink.event.DigitalReadChangeListener;

public class Testing {
	static int testingPin=0;
	static int digTestingPin=0;
	
	public static void main(String[] args) {
		 Link link = Link.getDefaultInstance();
		 
		 try {
				//Connect to the link
				List<String> portList = link.getPortList();
				if(portList != null && portList.size() > 0) {
					String port = portList.get(0);
					System.out.println("Connecting on port: " + port);
					boolean connected = link.connect(port);
					System.out.println("Connected:" + connected);
					Thread.sleep(1000);

					//link.sendPowerPinSwitch(53, IProtocol.HIGH);
					
					//Start monitoring all the pins
					link.startListenAnalogPin(testingPin);
					link.startListenDigitalPin(digTestingPin);
//					//Reading strain gauges---------------------------------------------------------------
//					link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
//						@Override
//						public void stateChanged(AnalogReadChangeEvent e) {
//							System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
//							System.out.println(e.getIncomingMessage());
//						}
//						@Override
//						public int getPinListening() {
//							return testingPin;
//						}
//					});
//					//Readings for wind control-------------------------------------------------------------
//					//Read whether fan should be on or off
//					link.addDigitalReadChangeListener(new DigitalReadChangeListener() {
//						
//						@Override
//						public void stateChanged(DigitalReadChangeEvent e) {
//							System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
//							System.out.println(e.getIncomingMessage());
//						}
//						
//						@Override
//						public int getPinListening() {
//							return digTestingPin;
//						}
//					});
					
					ArduinoControl arduino=new ArduinoControl();
					//Testing
//					for(int i=0;i<=10;i++)
//					{
//						arduino.sevenSegment(link,i);
//						Thread.sleep(500);
//					}
					
					link.sendPowerPinSwitch(34, IProtocol.LOW);
					link.sendPowerPinSwitch(35, IProtocol.LOW);
					link.sendPowerPinSwitch(36, IProtocol.LOW);
					link.sendPowerPinSwitch(37, IProtocol.HIGH);
					
				} else {
					//No connection
					System.out.println("No port found");
				}			
			}
			catch(Exception e) {
				e.printStackTrace();
			}

	}
}
