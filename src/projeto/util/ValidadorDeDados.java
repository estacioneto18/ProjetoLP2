package projeto.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projeto.exceptions.dados.DadoInvalidoException;
import projeto.exceptions.dados.DataInvalidaException;
import projeto.exceptions.dados.ObjetoNuloException;
import projeto.exceptions.dados.StringVaziaException;
import projeto.exceptions.logica.OperacaoInvalidaException;
import projeto.hospital.gerencia.farmacia.medicamento.CategoriasValidasMedicamentos;
import projeto.hospital.gerencia.farmacia.medicamento.tipos.TipoMedicamentoValido;
import projeto.hospital.gerencia.funcionario.cargo.Cargo;
import projeto.hospital.gerencia.funcionario.cargo.CargoValido;
import projeto.hospital.gerencia.procedimento.procedimentos.ProcedimentosValidos;
import projeto.hospital.gerencia.prontuario.paciente.TipoSanguineoValido;

/**
 * Classe com validadores de dados
 * 
 * @author Estacio
 * @author Eric
 * @author Thaynan
 */
public abstract class ValidadorDeDados {

	private static final int NOME_TAMANHO_MAXIMO = 50;
	private static final int SENHA_TAMANHO_MINIMO = 8;
	private static final int SENHA_TAMANHO_MAXIMO = 12;

	// STRINGS
	/**
	 * Verifica se a String eh nula ou vazia. O nomeAtributo eh utilizado para
	 * uma melhor criacao de excecao caso a String seja invalida.
	 * 
	 * @param erroAtributo
	 *            Nome do atributo que carrega a String a ser analisada.
	 * @param atributo
	 *            String a ser analisada.
	 * @throws StringVaziaException
	 *             Caso o atributo seja uma String vazia.
	 * @throws ObjetoNuloException
	 *             Caso o atributo seja nulo.
	 */
	public static void validaString(String erroAtributo, String atributo)
			throws StringVaziaException, ObjetoNuloException {
		validaNaoNulo(erroAtributo, atributo);
		if (atributo.trim().isEmpty())
			throw new StringVaziaException(erroAtributo + " nao pode ser vazio.");
	}

	/**
	 * Verifica se o objeto eh nulo. O nomeAtributo eh utilizado para uma melhor
	 * criacao de excecao.
	 * 
	 * @param nomeAtributo
	 *            Nome do atributo que carrega o Objeto a ser analisado.
	 * @param atributo
	 *            Objeto a ser analisado.
	 * @throws ObjetoNuloException
	 *             Caso o atributo seja nulo.
	 */
	public static void validaNaoNulo(String nomeAtributo, Object atributo) throws ObjetoNuloException {
		if (atributo == null)
			throw new ObjetoNuloException(nomeAtributo + " nao pode ser nulo(a)!");
	}

	// OBJETOS
	// ATRIBUTOS
	/**
	 * Verifica se o cargo eh valido de ser cadastrado
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param cargo
	 *            Cargo a ser verificado
	 * @throws DadoInvalidoException
	 *             Caso o cargo nao exista.
	 */
	public static void validaCargo(String erro, String cargo) throws DadoInvalidoException {
		validaString(erro, cargo);
		try {
			// Uso dessa funcao por String.trim() nao funcionar
			CargoValido.valueOf(trim(cargo).toUpperCase());
		} catch (IllegalArgumentException excecao) {
			throw new DadoInvalidoException(MensagensDeErro.CARGO_INVALIDO_FUNCIONARIO);
		}
	}

	/**
	 * Retorna a String sem espacos.
	 * 
	 * @param string
	 *            String inicial.
	 * @return String sem espacos.
	 */
	private static String trim(String string) {
		String separada[] = string.split(" ");
		String retorno = "";
		for (String s : separada) {
			retorno += s;
		}
		return retorno;
	}

	/**
	 * Verifica se o cargo eh valido de ser cadastrado
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param cargo
	 *            Cargo a ser verificado
	 * @throws DadoInvalidoException
	 *             Caso o cargo nao exista.
	 */
	public static void validaCargo(String erro, Cargo cargo) throws DadoInvalidoException {
		validaCargo(erro, cargo.getNome());
	}

	/**
	 * Verifica se a data corresponde ao padrao dd/mm/aaaa e se a mesma eh
	 * coerente.
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param data
	 *            Data a ser analisada.
	 * @throws StringVaziaException
	 *             Caso a data seja uma String vazia.
	 * @throws DataInvalidaException
	 *             Caso a data seja incoerente.
	 * @throws ObjetoNuloException
	 *             Caso a data seja nula.
	 */
	public static void validaData(String erro, String data)
			throws StringVaziaException, DataInvalidaException, ObjetoNuloException {
		validaString(erro, data);

		Pattern padrao = Pattern.compile(Constantes.DATA_REGEX);
		Matcher padraoData = padrao.matcher(data);

		// Verificacao da regex
		if (!padraoData.matches())
			throw new DataInvalidaException(Constantes.DATA + " invalida.");

		padraoData.reset();
		// Verificacao no calendario
		if (padraoData.find()) {

			String dia = padraoData.group(1);
			String mes = padraoData.group(2);
			int ano = Integer.parseInt(padraoData.group(3));

			if (dia.equals("31"))
				if (mes.equals("11") || mes.equals("04") || mes.equals("06") || mes.equals("09"))
					throw new DataInvalidaException(Constantes.DATA + " nao eh valida! Mes fornecido nao tem dia 31!");

			if (mes.equals("02")) {
				if (dia.equals("30") || dia.equals("31"))
					throw new DataInvalidaException(
							Constantes.DATA + " nao eh valida! Fevereiro nao tem dias 30 e 31!");

				if ((ano % 4 != 0 || (ano % 4 == 0 && ano % 100 == 0) || ano % 400 != 0) && dia.equals("29"))
					throw new DataInvalidaException(
							Constantes.DATA + " nao eh valida! Ano nao eh bissexto. Fevereiro nao tem dia 29!");
			}
		}
	}

	/**
	 * Verifica se um sexo biologico eh valido
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param sexoBiologico
	 *            sexo biologico
	 * @throws DadoInvalidoException
	 *             Caso o sexo biologico seja invalido.
	 */
	public static void validaSexoBiologico(String erro, String sexoBiologico) throws DadoInvalidoException {
		if (sexoBiologico == null || !sexoBiologico.toLowerCase().equals(Constantes.MASCULINO)
				&& !sexoBiologico.toLowerCase().equals(Constantes.FEMININO))
			throw new DadoInvalidoException(erro);
	}

	/**
	 * Valida padrao da matricula e tambem a validade da String.
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param matricula
	 *            matricula a ser analisada.
	 * @throws DadoInvalidoException
	 *             Caso a matricula nao siga o padrao.
	 */
	public static void validaPadraoMatricula(String erro, String matricula) throws DadoInvalidoException {
		validaString(erro, matricula);

		for (int indice = 0; indice < matricula.length(); indice++) {
			if (!Character.isDigit(matricula.charAt(indice)))
				throw new DadoInvalidoException(MensagensDeErro.PADRAO_MATRICULA);
		}
	}

	/**
	 * Valida um nome.
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param nome
	 *            Nome a ser analisado.
	 * @throws StringVaziaException
	 *             Caso algum dado seja uma String vazia.
	 * @throws ObjetoNuloException
	 *             Caso algum dado seja nulo.
	 */
	public static void validaNome(String erro, String nome) throws StringVaziaException, ObjetoNuloException {
		validaString(erro, nome);
		if (nome.length() >= NOME_TAMANHO_MAXIMO)
			throw new OperacaoInvalidaException(MensagensDeErro.NOME_TAMANHO_INVALIDO);
		for (int indice = 0; indice < nome.length(); indice++) {
			if (Character.isDigit(nome.charAt(indice)))
				throw new OperacaoInvalidaException(MensagensDeErro.NOME_CARACTER_INVALIDO);
		}
	}

	/**
	 * Valida uma nova senha.
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param senha
	 *            Senha a ser analisada.
	 * @throws DadoInvalidoException
	 *             Caso a senha seja vazia, nula ou nao seguir o padrao.
	 */
	public static void validaSenha(String erro, String senha) throws DadoInvalidoException {
		validaString(erro, senha);

		if (senha.length() < SENHA_TAMANHO_MINIMO || senha.length() > SENHA_TAMANHO_MAXIMO)
			throw new DadoInvalidoException("A nova senha deve ter entre 8 - 12 caracteres alfanumericos.");

		for (int indice = 0; indice < senha.length(); indice++) {
			if (!(Character.isAlphabetic(senha.charAt(indice)) || Character.isDigit(senha.charAt(indice))
					|| senha.charAt(indice) == ' ')) {
				throw new DadoInvalidoException("A nova senha deve ter entre 8 - 12 caracteres alfanumericos.");
			}
		}
	}

	/**
	 * Valida uma categoria de medicamento.
	 * 
	 * @param mensagemDeErro
	 *            Mensagem de Erro, caso aconteca uma excessao.
	 * @param categoria
	 *            Categorias do medicamento
	 * @throws DadoInvalidoException
	 *             Caso a categotia nao exista ou ela seja uma String invalida.
	 */
	public static void validaCategoriaMedicamento(String mensagemDeErro, String categoria)
			throws DadoInvalidoException {
		String[] arrayCategorias = categoria.split(",");
		for (int i = 0; i < arrayCategorias.length; i++) {
			try {
				String categoriaAtual = arrayCategorias[i];
				CategoriasValidasMedicamentos.valueOf(categoriaAtual.toUpperCase());
			} catch (IllegalArgumentException excecao) {
				throw new DadoInvalidoException(mensagemDeErro);
			}
		}
	}

	/**
	 * Metodo responsavel por validar um tipo sanguineo.
	 * 
	 * @param erro
	 *            Mensagem de erro que sera lancada
	 * @param tipo
	 *            Tipo sanguineo
	 * @throws DadoInvalidoException
	 *             Caso o tipo sanguineo seja invalido.
	 */
	public static void validaTipoSanguineo(String erro, String tipo) throws DadoInvalidoException {
		try {
			TipoSanguineoValido.valueOf(tipo.substring(Constantes.ZERO, tipo.length() - Constantes.UM));
			if (tipo.charAt(tipo.length() - Constantes.UM) != '+'
					&& tipo.charAt(tipo.length() - Constantes.UM) != '-') {
				throw new DadoInvalidoException(erro);
			}
		} catch (IllegalArgumentException excecao) {
			throw new DadoInvalidoException(erro);
		}
	}

	/**
	 * Metodo responsavel por validar um tipo de medicamento.
	 * 
	 * @param atributo
	 *            Mensagem de erro que sera lancada
	 * @param tipo
	 *            Tipo de medicamento
	 * @throws DadoInvalidoException
	 *             Caso o tipo seja invalido.
	 */
	public static void validaTipoMedicamento(String atributo, String tipo) throws DadoInvalidoException {
		try {
			TipoMedicamentoValido.valueOf(tipo.toUpperCase());
		} catch (IllegalArgumentException excecao) {
			throw new DadoInvalidoException(atributo + " invalido.");
		}
	}

	// ATRIBUTOS
	// NUMEROS
	/**
	 * Valida se um valor double eh positivo
	 * 
	 * @param atributo
	 *            Nome do atributo
	 * @param valor
	 *            valor
	 * @throws DadoInvalidoException
	 *             Caso o valor seja negativo.
	 */
	public static void validaPositivo(String atributo, Double valor) throws DadoInvalidoException {
		if (valor < 0)
			throw new DadoInvalidoException(atributo + MensagensDeErro.NAO_PODE_SER_NEGATIVO);
	}

	/**
	 * Valida se um valor long eh positivo
	 * 
	 * @param atributo
	 *            Nome do atributo
	 * @param valor
	 *            valor
	 * @throws DadoInvalidoException
	 *             Caso o valor seja negativo.
	 */
	public static void validaPositivo(String atributo, Long valor) throws DadoInvalidoException {
		if (valor < 0)
			throw new DadoInvalidoException(atributo + MensagensDeErro.NAO_PODE_SER_NEGATIVO);
	}

	/**
	 * Valida se um valor inteiro eh positivo
	 * 
	 * @param atributo
	 *            Nome do atributo
	 * @param valor
	 *            Valor a ser avaliado
	 * @throws DadoInvalidoException
	 *             Caso o valor seja negativo.
	 */
	public static void validaPositivo(String atributo, Integer valor) throws DadoInvalidoException {
		if (valor < 0)
			throw new DadoInvalidoException(atributo + MensagensDeErro.NAO_PODE_SER_NEGATIVO);
	}
	// NUMEROS

	// PROCEDIMENTOS
	public static void validaProcedimento(String procedimento) throws DadoInvalidoException {
		try{
			ProcedimentosValidos.valueOf(trim(procedimento).toUpperCase());
		}catch(IllegalArgumentException excecao){
			throw new DadoInvalidoException(MensagensDeErro.PROCEDIMENTO_INVALIDO);
		}
	}
	// PROCEDIMENTOS
}
