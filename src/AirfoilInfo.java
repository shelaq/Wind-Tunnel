
public class AirfoilInfo {
	
	private final double lSYMMETRICAL=8.48;
	private final double lCAMBERED=8.45;
	private final double lHIGHCAMBER=8.4;
	private final double lPROPELLOR=13.1;
	
	private final double wSYMMETRICAL = 0.078;
	private final double wCAMBERED = 0.078;
	private final double wHIGHCAMBER = 0.104;
	private final double wPROPELLOR = 0.037;
	
	private final double[] chords={0.037,0.036,0.042,0.028};
	
	public double getChord(int index)
	{
		return chords[index-1];
	}
	
	//Lengths
	public double getLSym()
	{
		return lSYMMETRICAL;
	}
	
	public double getLCam()
	{
		return lCAMBERED;
	}
	
	public double getLHi()
	{
		return lHIGHCAMBER;
	}
	
	public double getLPro()
	{
		return lPROPELLOR;
	}
	
	//Weights
	public double getSym()
	{
		return wSYMMETRICAL;
	}
	
	public double getCam()
	{
		return wCAMBERED;
	}
	
	public double getHi()
	{
		return wHIGHCAMBER;
	}
	
	public double getPro()
	{
		return wPROPELLOR;
	}
}
