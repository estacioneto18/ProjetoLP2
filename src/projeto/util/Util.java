package projeto.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projeto.exceptions.dados.DadoInvalidoException;
import projeto.exceptions.dados.DataInvalidaException;
import projeto.exceptions.dados.ObjetoNuloException;
import projeto.exceptions.dados.StringVaziaException;

/**
 * Se encarrega de varias validacoes de dados e de metodos mais genericos
 * utilizados em varias classes.
 * 
 * @author Estacio Pereira
 *
 */
public class Util {

	/**
	 * Verifica se a String eh nula ou vazia.
	 * 
	 * @param atributo
	 *            String a ser analisada.
	 */
	public static void validaString(String atributo) {
		validaNaoNulo(atributo);
		if (atributo.trim().length() == Constantes.ZERO)
			throw new StringVaziaException();
	}

	/**
	 * Verifica se a String eh nula ou vazia. O nomeAtributo eh utilizado para
	 * uma melhor criacao de excecao caso a String seja invalida.
	 * 
	 * @param erroAtributo
	 *            Nome do atributo que carrega a String a ser analisada.
	 * @param atributo
	 *            String a ser analisada.
	 */
	public static void validaString(String erroAtributo, String atributo) {
		validaNaoNulo(erroAtributo, atributo);
		if (atributo.trim().length() == Constantes.ZERO)
			throw new StringVaziaException(erroAtributo
					+ "nao pode ser vazio.");
	}

	/**
	 * Verifica se um objeto eh nulo.
	 * 
	 * @param atributo
	 *            Objeto a ser analisado.
	 */
	public static void validaNaoNulo(Object atributo) {
		if (atributo == null)
			throw new ObjetoNuloException("Objeto nao pode ser nulo!");
	}

	/**
	 * Verifica se o objeto eh nulo. O nomeAtributo eh utilizado para uma melhor
	 * criacao de excecao.
	 * 
	 * @param nomeAtributo
	 *            Nome do atributo que carrega o Objeto a ser analisado.
	 * @param atributo
	 *            Objeto a ser analisado.
	 */
	public static void validaNaoNulo(String nomeAtributo, Object atributo) {
		if (atributo == null)
			throw new ObjetoNuloException(nomeAtributo
					+ " nao pode ser nulo(a)!");
	}

	/**
	 * Verifica se a data corresponde ao padrao dd/mm/aaaa e se a mesma eh
	 * coerente.
	 * 
	 * @param nomeData
	 *            Nome do atributo que carrega a data a ser analisada.
	 * @param data
	 *            Data a ser analisada.
	 */
	public static void validaData(String nomeData, String data) {
		validaString(nomeData, data);

		Pattern padrao = Pattern.compile(Constantes.DATA_REGEX);
		Matcher validadorDePadrao = padrao.matcher(data);

		// Verificacao da regex
		if (!validadorDePadrao.matches())
			throw new DataInvalidaException(nomeData + " nao eh valida!");

		validadorDePadrao.reset();
		// Verificacao no calendario
		if (validadorDePadrao.find()) {

			String dia = validadorDePadrao.group(1);
			String mes = validadorDePadrao.group(2);
			int ano = Integer.parseInt(validadorDePadrao.group(3));

			if (dia.equals("31"))
				if (mes.equals("11") || mes.equals("04") || mes.equals("06")
						|| mes.equals("09"))
					throw new DataInvalidaException(nomeData
							+ " nao eh valida! Mes fornecido nao tem dia 31!");

			if (mes.equals("02")) {
				if (dia.equals("30") || dia.equals("31"))
					throw new DataInvalidaException(nomeData
							+ " nao eh valida! Fevereiro nao tem dias 30 e 31!");

				if (ano % 4 != 0 && dia.equals("29"))
					throw new DataInvalidaException(
							nomeData
									+ " nao eh valida! Ano nao eh bissexto. Fevereiro nao tem dia 29!");
			}
		}
	}

	/**
	 * Dado o nome do cargo, este metodo retorna o codigo do mesmo.
	 * 
	 * @param cargo
	 *            Nome do cargo.
	 * @return Codigo do cargo.
	 */
	public static String getCodigoPorCargo(String cargo) {
		// Validacao do cargo ja deve ter sido feita antes de chamar esse metodo

		if (cargo.equals(Constantes.DIRETOR))
			return Constantes.CODIGO_DIRETOR;
		if (cargo.equals(Constantes.MEDICO))
			return Constantes.CODIGO_MEDICO;
		if (cargo.equals(Constantes.TECNICO))
			return Constantes.CODIGO_TECNICO;
		throw new DadoInvalidoException("Cargo inexistente!");
	}

	/**
	 * Dada uma determinada data, o metodo retorna o ano da mesma.
	 * 
	 * @param data
	 *            Data
	 * @return Ano da data.
	 */
	public static String getAnoPorData(String data) {
		// Validacao da data ja deve ter sido feita antes de chamar esse metodo
		return data.split(Constantes.BARRA)[Constantes.INDICE_ANO];
	}

	/**
	 * Dado um numero em String, retorna os quatro primeiros digitos em String.
	 * 
	 * @param numero
	 *            Numero base.
	 * @return Quatro digitos iniciais do numero.
	 */
	public static String quatroDigitosIniciais(String numero) {
		return numero.substring(Constantes.ZERO, Constantes.QUATRO);
	}

	/**
	 * Dado um numero em inteiro, retorna os quatro primeiros digitos em String.
	 * 
	 * @param numero
	 *            Numero base.
	 * @return Quatro digitos iniciais do numero.
	 */
	public static String quatroDigitosIniciais(int numero) {
		return quatroDigitosIniciais(Integer.toString(numero));
	}

	/**
	 * Dada uma matricula, retorna o codigo do cargo da funcionario dono da
	 * mesma.
	 * 
	 * @param matricula
	 *            Matricula do funcionario.
	 * @return Codigo do cargo do funcionario.
	 */
	public static String getCodigoPorMatricula(String matricula) {
		return Character.toString(matricula.charAt(Constantes.ZERO));
	}
	
	public static String transformaFormatoData(String dataSistema){
		String[] dataQuebrada = dataSistema.split("/");
		String formatoSaida = String.join("-", dataQuebrada[2], dataQuebrada[1], dataQuebrada[0]);
		return formatoSaida;
	}

}
