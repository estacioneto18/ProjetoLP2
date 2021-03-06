package projeto.hospital.gerencia.farmacia.medicamento.tipos;

/**
 * Classe que representa o tipo medicamento generico, seus comportaemntos e
 * estados.
 * 
 * @author Thaynan
 */
public class Generico implements TipoMedicamento {
	/**
	 * Serial gerado automaticamente.
	 */
	private static final long serialVersionUID = 1556492773719602719L;

	public static final double DESCONTO_GENERICO = 60;
	public static final double DESCONTO_GENERICO_PORCENTAGEM = 100;
	public static final String TIPO = "Generico";

	/**
	 * @return Tipo do medicamento.
	 */
	public String getTipo() {
		return TIPO;
	}

	/**
	 * @return Preco do medicamento final.
	 */
	@Override
	public Double calculaPreco(Double preco) {
		return (preco * DESCONTO_GENERICO
				/ DESCONTO_GENERICO_PORCENTAGEM);
	}

	/**
	 * @return caracteristicas do medicamento.
	 */
	@Override
	public String toString() {
		StringBuilder saida = new StringBuilder();
		saida.append("Medicamento " + TIPO + ":");
		return saida.toString();
	}
}