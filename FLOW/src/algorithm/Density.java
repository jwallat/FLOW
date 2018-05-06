package algorithm;

import java.text.DecimalFormat;

import model.Network;

/**
 * Klasse die genutzt wird, die Dichte eines Netzwerkes zu berechnen.
 *
 * @author jwall
 *
 */
public class Density {

	private Network network;
	private DecimalFormat df = new DecimalFormat("#0.00");

	public Density(Network network) {
		this.network = network;
	}

	/**
	 * Methode, welche die Density des Netzwerks berechnet. Density ist dabei das
	 * Verhltnis der Zahl vorhandener Kanten zu der Zahl moeglicher Kanten.
	 */
	public String getDensity() {
		int numVertices = network.getVertices().size();
		int numEdges = network.getEdges().size();

		double density = (double) (2 * (numEdges - numVertices + 1)) / (double) (numVertices * (numVertices - 3) + 2);
		return df.format(density);
	}
}
