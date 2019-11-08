package myPack;

public class NeuronFire
{
	// pesi dei parametri utilizzati come input per il neurone
	double weight_1, weight_2, weight_3;
	// learning rate è settato alto per rendere il robot dinamico
	double learningRate;
	// parametri in input al neurone
	double input_1, input_2, input_3;
	// percentuali
	double percOld, percNew;
	// soglia di spike
	double threshold;
	// output del neurone
	boolean result;

	public NeuronFire() {
		this.percOld = 0.0; this.percNew = 0.0;
		this.input_1 = 0.0; this.input_2 = 0.0; this.input_3 = 0.0;
		this.weight_1 = 0.2; this.weight_2 = 0.6; this.weight_3 = 0.2;
		this.learningRate = 0.9;
		this.threshold = 0.6;
		this.result = true;
	}

	public double getWeight_1() {
		return this.weight_1;
	}

	public double getWeight_2() {
		return this.weight_2;
	}

	public double getWeight_3() {
		return this.weight_3;
	}

	public void updateWeight_1( double percN, double percO ) {
		this.weight_1 = this.weight_1 + ( this.learningRate*this.input_1*(percN-percO) );
	}

	public void updateWeight_2( double percN, double percO ) {
		weight_2 = this.weight_2 + ( this.learningRate*this.input_2*(percN-percO) );
	}

	public  void updateWeight_3( double percN, double percO ) {
		weight_3 = this.weight_3 + ( this.learningRate*this.input_3*(percN-percO) );
	}

	public boolean calcolaOut( double power, double distance, double vel, double percNew ) {
		input_1 = power;
		input_2 = distance;
		input_3 = vel;
		updateWeight_1( percNew, percOld );
		updateWeight_2( percNew, percOld );
		updateWeight_3( percNew, percOld );
		result = weight_1*input_1 + weight_2*input_2 + weight_3*input_3 > threshold;
		//System.out.println( "la potenza è " + input_1 + " il weight_1 " + weight_1 );
		//System.out.println( "la distanza è " + input_2 + " il weight_2 " + weight_2 );		
		//System.out.println( "la velocità è " + input_3 + " il weight_3 " + weight_3 );
		//System.out.println( "la percentuale nuova è " + percNew );
		//System.out.println( "la percentuale vecchia è " + percOld );
		//System.out.println( "il result del neurone è " + result );
		//System.out.println( "somma pesata =  " + ( weight_1*input_1 + weight_2*input_2 + weight_3*input_3 ) );
		percOld = percNew;
		return result;
	}
}