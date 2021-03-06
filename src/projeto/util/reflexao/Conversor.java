package projeto.util.reflexao;

import projeto.hospital.gerencia.funcionario.cargo.Cargo;

/**
 * Classe de conversao de dados. Contem algumas conversoes uteis para validacao
 * de dados.
 * 
 * @author Estacio Pereira
 */
public abstract class Conversor {

	/**
	 * Transforma uma dada String para double.
	 * 
	 * @param numero
	 *            Numero.
	 * @return Numero em double.
	 */
	public static double stringParaDouble(String numero) {
		return Double.parseDouble(numero);
	}

	/**
	 * Transforma uma dada String para inteiro.
	 * 
	 * @param numero
	 *            Numero.
	 * @return Numero em inteiro.
	 */
	public static int stringParaInteiro(String numero) {
		return Integer.parseInt(numero);
	}

	/**
	 * Transforma um dado Double para String
	 * 
	 * @param numero
	 *            Numero.
	 * @return Numero em String.
	 */
	public static String doubleParaString(Double numero) {
		return Double.toString(numero);
	}

	/**
	 * Transforma um dado inteiro para String
	 * 
	 * @param numero
	 *            Numero.
	 * @return Numero em String.
	 */
	public static String inteiroParaString(Integer numero) {
		return Integer.toString(numero);
	}

	/**
	 * Converte o cargo para uma String
	 * 
	 * @param cargo
	 *            Cargo.
	 * @return Nome do cargo.
	 */
	public static String cargoParaString(Cargo cargo) {
		return cargo.getNome();
	}
}