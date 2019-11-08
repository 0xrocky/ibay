package myPack;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;
import java.awt.Color;
import java.util.Arrays;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class JudgeDredd extends AdvancedRobot
{
	// Costanti
	static final int GUESS_FACTORS = 25;
	static final int MIDDLE_FACTOR = (GUESS_FACTORS - 1) / 2;
	static final double MAXIMUM_ESCAPE_ANGLE = 0.72727272727272727272727272727273;
	static final double FACTOR_ANGLE = MAXIMUM_ESCAPE_ANGLE / MIDDLE_FACTOR;

	// Variabili globali
	static double averageLateralVelocity;
	static double direction = 1;
	static double enemyBulletSpeed;
	static double enemyEnergy;
	static double enemyHeading;
	static double enemyVelocity;
	static double shots; // colpi sparati
	static double hits; // colpi ricevuti
	static int absoluteEnemyLateralVelocity;
	static int movementMode;
	static int timeSinceVelocityChange;
	double fieldDimension;
	NeuronFire neuron = new NeuronFire();
	
	// Predizione della imminente posizione del nemico, per targeting intelligente
	static int[][][][][][] guessFactors = new int[3][4][5][2][5][GUESS_FACTORS];
	
	//En garde!
	public void run()
	{
		// Imposta il radar e il cannone per ruotare in modo indipendente
		setAdjustRadarForGunTurn( true );
		setAdjustGunForRobotTurn( true );
		setColors(Color.red, Color.yellow, Color.green, Color.pink, Color.orange);
		fieldDimension = Math.sqrt( Math.pow(getBattleFieldHeight(), 2) + Math.pow(getBattleFieldWidth(), 2) );
	}

	public void spara( double perc, double distance, double power, double vel )
	{
		if( ( neuron.calcolaOut( power, distance, vel, perc ) ) )
			setFire( power );
		else
			setFire( Math.min( power, 0.5 ) );
	}

	public void onStatus(StatusEvent e)
	{
		// Gira il radar ad ogni colpo andato a segno
    	setTurnRadarRightRadians( 1 );
	}

	public void onScannedRobot(ScannedRobotEvent e)
	{
		// Variabili locali
		int antiRam;
		double enemyDistance;
		double absoluteBearing;
		double enemyDirection;
		double lateralVelocity;
		double offset;
		double theta;
		int acceleration;

		Wave wave;
		addCustomEvent( wave = new Wave() );
		
		/*********************************************
		 *---------------MOVEMENT CODE---------------*
		 *********************************************/
		// Ritirata veloce quando il nemico è in ramming (speronamento)
		offset = 2 + ( antiRam = (int)( 100 / ( wave.enemyDistance = enemyDistance = e.getDistance() ) ) );
		
		// Codice di Wall smoothing (lisciatura delle pareti, cioè evitare collisioni con muri senza bisogno di invertire la direzione)
		// Si sottraggono le coordinate correnti al confine del muro inferiore
		Rectangle2D.Double fieldRectangle;
		while( !( fieldRectangle = new Rectangle2D.Double(18 - getX(), 18 - getY(), 764, 564) ).
			contains(160 * Math.sin(theta = (wave.absoluteBearing = absoluteBearing = 
			(e.getBearingRadians() + getHeadingRadians())) + direction * (offset -= .02)), 160 * Math.cos(theta)));
		setTurnRightRadians(Math.tan(theta -= getHeadingRadians()));
			
		// Movimento Stop&Go: muoviti sotto fuoco nemico, o con movimenti random o sotto ramming nemico
		double energyDelta;
		if( ( energyDelta = ( enemyEnergy - ( enemyEnergy = e.getEnergy() ) ) ) > movementMode - antiRam )
		{			
			// Calcola la lunghezza del movimento Stop&Go in base alla potenza del fuoco nemico
			setAhead( ((3 + (int)(energyDelta / 0.5000001)) << 3) * Math.signum( Math.cos(theta) ) );
		}
		
		//Movimento random: non muoverti mai random se il nemico sta facendo ramming, o se il robot è in Stop&Go
		// Direzione inversa nel caso in cui il bot sia troppo vicino al muro
		if (Math.random() + antiRam < (-0.6 * Math.sqrt(enemyBulletSpeed / enemyDistance) + 0.04) * movementMode || offset < Math.PI/3.5)
			direction = -direction;

		/********************************************
		 *--------------TARGETING CODE--------------*
		 ********************************************/
		// Determina la velocità laterale del nemico e la direzione del suo movimento
		// Uso una semplice media aggiornata per memorizzare la direzione laterale precedente
		wave.enemyDirection = enemyDirection = (averageLateralVelocity = ((averageLateralVelocity * .01) +
			(lateralVelocity = ((enemyVelocity = e.getVelocity()) * Math.sin((enemyHeading = e.getHeadingRadians()) - absoluteBearing))))) < 0 ? -FACTOR_ANGLE : FACTOR_ANGLE;
		
		// Determino se il robot avversario sta rallantando o no
		if( (acceleration = (int)Math.signum(absoluteEnemyLateralVelocity - (absoluteEnemyLateralVelocity = (int)Math.abs(lateralVelocity)))) != 0 )
		timeSinceVelocityChange = 0;
					
		// Determino la situazione corrente
		double angle;
		int[] guessFactorsLocal = wave.guessFactors = guessFactors
			[1 + acceleration] // accelerazione
			[Math.min(3, (int)(Math.pow(280 * timeSinceVelocityChange++ / enemyDistance, .7)))] // numero di colpi nemici senza che la velocità sia variata
			[absoluteEnemyLateralVelocity / 2] // velocità laterale assoluta del nemico
			[(int)Math.signum(fieldRectangle.outcode(Math.sin(angle = (absoluteBearing +
				enemyDirection * MIDDLE_FACTOR)) * enemyDistance, Math.cos(angle) * enemyDistance))] // vicinanza al muro
			[(int)enemyDistance / 200]; // distanza

		// Cerco il valore migliore per la situazione attuale
		int mostVisited = MIDDLE_FACTOR;
		int i = 0;
		try
		{
			while( true )
			{
				if( guessFactorsLocal[i] > guessFactorsLocal[mostVisited] )
				{
					mostVisited = i;
				}
				i++;
			}			
		}
		catch(Exception ex)
		{}
		// Giro il cannone in verso la posizione calcolata (il leggero offset aiuta a penetrare l'armatura)
		setTurnGunRightRadians(0.0005 + Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()
			+ (enemyDirection * (mostVisited - MIDDLE_FACTOR))));
		
		// Spara solo se il tempo di ricarica è passato
		// Preferisci colpi di media potenza la maggior parte del tempo, utilizzando massima potenza solo a distanza ravvicinata
		// Se il nemico è debole, impiega la potenza di fuoco minima necessaria per distruggerlo
		if( getGunHeat() == 0 ) {
			spara( hits/(shots+1), e.getDistance()/fieldDimension, (Math.min(2+antiRam, enemyEnergy/4)), Math.abs( e.getVelocity() ) );
		}

		// Aggiustamento del radar nello scontro 1 vs 1
		setTurnRadarRightRadians( 2 * Utils.normalRelativeAngle( absoluteBearing - getRadarHeadingRadians() ) );
	}

	public void onBulletHit(BulletHitEvent e)
	{
		// Aggiorna l'energia variabile del nemico in negativo quando il robot lo colpisce
		enemyEnergy -= 10;
		shots++;
	}

	public void onHitByBullet(HitByBulletEvent e)
	{
		// Aggiorna l'energia variabile del nemico in positivo quando il mio robot viene colpito
		// Registra la velocità dei proiettili del nemico
		enemyEnergy += 20 - ( enemyBulletSpeed = e.getVelocity() );	
		// Se il bot riceve troppi danni in modalità Stop&Go, passa al movimento random
		if( ( hits += (4.25 / enemyBulletSpeed) ) > getRoundNum() + 2 )
			movementMode = -1;
    }

	// Se mi stanno venendo addosso cerco di sparare più forte che posso
	 public void onHitRobot(HitRobotEvent e) {
		double turnGunAmt = normalRelativeAngleDegrees( e.getBearing() + getHeading() - getGunHeading() );
		turnRight(turnGunAmt);
		if( getGunHeat() == 0 && Math.abs(getGunTurnRemaining() ) < 10) {
			setFire(3);
			shots++;
		}
	}

	// WaveSurfing: serve per schivare efficacemente i proiettili nemici, usando movimenti ondulatori circolari per predire come il nemico sta puntanto il mio bot
	static class Wave extends Condition
	{
		// Variabili globali
		double absoluteBearing;
		double bearingOffset;
		double enemyDirection;
		double enemyDistance;		
		double waveDistanceTraveled;
		int[] guessFactors;
		
		public boolean test()
		{
			bearingOffset += (enemyVelocity * Math.sin(enemyHeading - absoluteBearing)) / (enemyDistance += (enemyVelocity * Math.cos(enemyHeading - absoluteBearing)));
			
			// Controlla se l'onda ha superato la posizione attuale del nemico
			if(Math.abs( ( waveDistanceTraveled += 14 ) - enemyDistance ) <= 7)
			{
				// Calcola il "fattore indovinante" (guess factor) che avrebbe colpito il nemico, ed incrementalo
				guessFactors[(int)Math.round((bearingOffset / enemyDirection) + MIDDLE_FACTOR)]++;
				System.out.println(Arrays.toString(guessFactors));
			}
			return false;
		}
	}
}																																												