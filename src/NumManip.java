//Class for converting number formats

import java.util.Scanner;
import java.lang.*;
import java.util.*;

public class NumManip {
	
	//Find the power of 10 in scientific notation for a given double
	public int exp (double num)
	{
		if(num==0)
			return 0;
		
		//Counter for number of times number is divided by 10
		int result=0;

		//If |number| greater than 1, keep dividing by 10 until proper scientific
		//notation is reached (1 non-decimal place)
		if(greaterThan(Math.abs(num),1)==1)
		{
			while(greaterThan(Math.abs(num),1)==1)
			{
				num/=10;
				result++;
			}
			result--;
		}
		//If |number| less than 1, keep multiplying by 10 until proper scientific
		//notation is reached (1 non-decimal place)
		else if(greaterThan(Math.abs(num),1)==-1)
		{
			while(greaterThan(Math.abs(num),1)==-1)
			{
				result--;
				num*=10;
			}
		}
		return result;
	}
	
	//Get number to proper decimal place
	public double properFormat(double num)
	{
		return num*=Math.pow(10, -exp(num));
	}
	
	//Round numbers to 3 significant digits
	public double threeSig (double num)
	{
		//To get to 3 significant digits, need to multiply by 10 to the negative power
		//of the proper exponent (method above^)
		int power=-exp(num);
		num*=Math.pow(10, power);
		
		//Multiply by 100 then cut off the rest of the decimals by rounding, then 
		//converting to integer
		num*=100;
		int a = (int) Math.round(num);
		
		//Divide by 100 to return to the scientific notation, and convert to double
		double result=(double)a/100;
		
		return result;
	}
	
	//Return the first, second, and third digits of the rounded 3 significant digit number
	public int digit1(double num)
	{
		//Convert to integer - cut off the decimals for the first digit
		double working=threeSig(num);
		return (int)working;
	}
	public int digit2(double num)
	{
		//Mod the first digit to get rid of it, and cut off decimals for the middle digit
		double working=threeSig(num);
		working*=10;
		working=working%(digit1(num)*10);
		return (int)working;
	}
	public int digit3(double num)
	{
		//Multiply by 100 then mod 10 to get the last digit
		double working=threeSig(num);
		working*=100;
		working=working%10;
		return (int)working;
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
	
	public static void main(String[] arguments) {
		Scanner user_input = new Scanner( System.in );

		double n= user_input.nextDouble();
		//double n1= user_input.nextDouble();
		
		//System.out.println(greaterThan(n,n1));
		
		//System.out.println(threeSig(n));
		//System.out.println(exp(n));
		//System.out.println(properFormat(n));
		//System.out.println(digit1(n));
		//System.out.println(digit2(n));
		//System.out.println(digit3(n));
	}

}
