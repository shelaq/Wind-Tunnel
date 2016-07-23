/**
Copyright 2013 Luciano Zu project Ardulink http://www.ardulink.org/

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author Luciano Zu
*/
//package org.zu.ardulink.tutorial;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zu.ardulink.Link;
import org.zu.ardulink.connection.usb.DigisparkUSBConnection;
import org.zu.ardulink.protocol.IProtocol;
import org.zu.ardulink.protocol.ProtocolHandler;
import org.zu.ardulink.protocol.SimpleBinaryProtocol;
import org.zu.ardulink.event.ConnectionEvent;
import org.zu.ardulink.event.ConnectionListener;
import org.zu.ardulink.event.DigitalReadChangeEvent;
import org.zu.ardulink.event.DigitalReadChangeListener;
import org.zu.ardulink.event.AnalogReadChangeEvent;
import org.zu.ardulink.event.AnalogReadChangeListener;
import org.zu.ardulink.event.DisconnectionEvent;

public class ArduinoControl{
	static Link link;
	
/*	private int A1=2;
	private int B1=1;
	private int C1=2;
	private int D1=3;
	
	private int negFirst=20;
	
	private int constantB=5;
	private int constantC=9;
	private int constantE=12;*/
	
	//private int high=IProtocol.HIGH;
	//private int low=IProtocol.LOW;
	
	
	private int[][] segmentPins = 
		{
			{38,39,40,41},
			{34,35,36,37},
			{30,31,32,33},
			{26,27,28,29},
			{22,23,24,25},
		};
	
	
	private int[][] binary = 
		{
			{IProtocol.LOW,IProtocol.LOW,IProtocol.LOW,IProtocol.LOW},		//0
			{IProtocol.HIGH,IProtocol.LOW,IProtocol.LOW,IProtocol.LOW},		//1
			{IProtocol.LOW,IProtocol.HIGH,IProtocol.LOW,IProtocol.LOW},		//2
			{IProtocol.HIGH,IProtocol.HIGH,IProtocol.LOW,IProtocol.LOW},	//3
			{IProtocol.LOW,IProtocol.LOW,IProtocol.HIGH,IProtocol.LOW},		//4
			{IProtocol.HIGH,IProtocol.LOW,IProtocol.HIGH,IProtocol.LOW},	//5
			{IProtocol.LOW,IProtocol.HIGH,IProtocol.HIGH,IProtocol.LOW},	//6
			{IProtocol.HIGH,IProtocol.HIGH,IProtocol.HIGH,IProtocol.LOW},	//7
			{IProtocol.LOW,IProtocol.LOW,IProtocol.LOW,IProtocol.HIGH},		//8
			{IProtocol.HIGH,IProtocol.LOW,IProtocol.LOW,IProtocol.HIGH},	//9
			{IProtocol.HIGH,IProtocol.LOW,IProtocol.HIGH,IProtocol.HIGH}	//-
		};

	
	final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	
	private NumManip manip=new NumManip();
	
//	public ArduinoControl(Link link)
//	{
//		this.link=link;
//	}
	
	public void sevenSegment(Link link, double num)
	{
		//num=3.34567;
		
		int digit=manip.digit1(num);
		for(int i=0;i<4;i++)
			link.sendPowerPinSwitch(segmentPins[0][i],binary[digit][i]);
		
		digit=manip.digit2(num);
		for(int i=0;i<4;i++)
			link.sendPowerPinSwitch(segmentPins[1][i],binary[digit][i]);
		
		digit=manip.digit3(num);
		for(int i=0;i<4;i++)
			link.sendPowerPinSwitch(segmentPins[2][i],binary[digit][i]);
		
		
		digit=manip.exp(num);
		if(digit<0)
			for(int i=0;i<4;i++)
				link.sendPowerPinSwitch(segmentPins[3][i], binary[10][i]);
		else
			for(int i=0;i<4;i++)
				link.sendPowerPinSwitch(segmentPins[3][i], IProtocol.HIGH);
		
		digit=Math.abs(digit);
		for(int i=0;i<4;i++)
			link.sendPowerPinSwitch(segmentPins[4][i], binary[digit][i]);

	}
	
	
/*		public void sevenSegment(Link link, int digit, int place)
	{
		int A,B,C,D;
		
		if(place==0)
		{
			
		}
		if(place==2)
			pinAdd=constantB;
		else if (place==3)
			pinAdd=constantC;
		else if(place==5)
			pinAdd=constantE;
	
		if(digit<0)
		{
			for(int i=0;i<4;i++)
				link.sendPowerPinSwitch(negFirst+i+pinAdd, binary[11][i]);
		}
		
		digit=Math.abs(digit);
		
		for(int i=0;i<4;i++)
		{
			link.sendPowerPinSwitch(A1+i+pinAdd, binary[digit][i]);
		}

	}*/

	
/*	public void segmentZero(Link link, int digit)
	{		
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.LOW);
	}
	
	public void segmentOne(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.LOW);
	}
	
	public void segmentTwo(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.LOW);
	}
	
	public void segmentThree(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.LOW);
	}
	
	//DONE UP TO HERE
	public void segmentFour(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}
	
	public void segmentFive(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}
	
	public void segmentSix(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}
	
	public void segmentSeven(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}
	
	public void segmentEight(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}
	
	public void segmentNine(Link link, int digit)
	{
		int pinAdd=0;
		
		if(digit==2)
			pinAdd=constantB;
		else if (digit==3)
			pinAdd=constantC;
		
		link.sendPowerPinSwitch(A1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(B1+pinAdd, IProtocol.LOW);
		link.sendPowerPinSwitch(C1+pinAdd, IProtocol.HIGH);
		link.sendPowerPinSwitch(D1+pinAdd, IProtocol.HIGH);
	}*/
	
	public static void plzDoSomeShit()
	{
		try {
			//Link link = Link.getDefaultInstance();
			
			//link = getDigisparkConnection(); // Comment this row if you use just the default connection

			List<String> portList = link.getPortList();
			if(portList != null && portList.size() > 0) {
				String port = portList.get(0);
				System.out.println("Connecting on port: " + port);
				boolean connected = link.connect(port);
				System.out.println("Connected:" + connected);
				Thread.sleep(1000);
				int power = IProtocol.HIGH;
/*				while(true) {
					System.out.println("Send power:" + power);
					link.sendPowerPinSwitch(13, power);
					if(power == IProtocol.HIGH) {
						power = IProtocol.LOW;
					} else {
						power = IProtocol.HIGH;
					}
					Thread.sleep(2000);
				}*/
				link.startListenAnalogPin(1);
				link.startListenAnalogPin(0);
				link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
					
					@Override
					public void stateChanged(AnalogReadChangeEvent e) {
						System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
						System.out.println(e.getIncomingMessage());
					}
					
					@Override
					public int getPinListening() {
						return 0;
					}
				});
					link.addAnalogReadChangeListener(new AnalogReadChangeListener() {
					
					@Override
					public void stateChanged(AnalogReadChangeEvent e) {
						System.out.println("PIN: " + e.getPin() + " STATE: " + e.getValue());
						System.out.println(e.getIncomingMessage());
					}
					
					@Override
					public int getPinListening() {
						return 1;
					}
				});
/*				while(true){
				for(int i=0;i<256;i++)
				{
					link.sendPowerPinIntensity(2, i);
					Thread.sleep(20);
				}
				for(int i=255;i>=0;i--)
				{
					link.sendPowerPinIntensity(2, i);
					Thread.sleep(20);
				}
				}*/
				/*Thread.sleep(2000);
				link.sendPowerPinSwitch(2, IProtocol.LOW);
				Thread.sleep(2000);
				link.sendPowerPinSwitch(2, IProtocol.HIGH);
				Thread.sleep(2000);
				link.sendPowerPinSwitch(2, IProtocol.LOW);
				Thread.sleep(2000);
				link.sendPowerPinSwitch(2, IProtocol.HIGH);
				Thread.sleep(2000);
				link.sendPowerPinSwitch(2, IProtocol.LOW);
				Thread.sleep(2000);*/
				//link.sendCustomMessage("oled");
				
				
			} else {
				System.out.println("No port found!");
			}
						
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		plzDoSomeShit();
	}
	
}
