package projeto.hospital.gerencia.funcionario;

import java.io.Serializable;

import projeto.exceptions.dados.DadoInvalidoException;
import projeto.exceptions.logica.OperacaoInvalidaException;
import projeto.hospital.gerencia.funcionario.cargo.Cargo;
import projeto.hospital.gerencia.funcionario.cargo.Permissao;
import projeto.util.Constantes;
import projeto.util.MensagensDeErro;
import projeto.util.Util;
import projeto.util.reflexao.ConstantesReflection;
import projeto.util.reflexao.Conversao;
import projeto.util.reflexao.MetodoAssociado;
import projeto.util.reflexao.Reflection;
import projeto.util.reflexao.Validacao;

/**
 * Entidade Funcionario. Trata-se da entidade generica do sistema que tera todos
 * os dados de um funcionario do SOOS.
 * 
 * @author Estacio Pereira
 */
public class Funcionario implements Serializable {
	/**
	 * Serial gerado automaticamente.
	 */
	private static final long serialVersionUID = 1948219698630791794L;

	@Validacao(metodo = ConstantesReflection.VALIDA_NOME, erro = Constantes.NOME
			+ Constantes.DO_FUNCIONARIO)
	@MetodoAssociado(get = ConstantesReflection.GET_NOME, set = ConstantesReflection.SET_NOME)
	private String nome;

	@Conversao(formato = Cargo.class, conversor = ConstantesReflection.CARGO_STRING)
	@Validacao(metodo = ConstantesReflection.VALIDA_CARGO, erro = Constantes.NOME
			+ Constantes.DO_CARGO)
	@MetodoAssociado(get = ConstantesReflection.GET_CARGO)
	private Cargo cargo;

	@Validacao(metodo = ConstantesReflection.VALIDA_DATA, erro = Constantes.DATA)
	@MetodoAssociado(get = ConstantesReflection.GET_DATA_NASCIMENTO, set = ConstantesReflection.SET_DATA)
	private String data;

	@Validacao(metodo = ConstantesReflection.VALIDA_MATRICULA, erro = Constantes.MATRICULA
			+ Constantes.DO_FUNCIONARIO, cadastro = false)
	@MetodoAssociado(get = ConstantesReflection.GET_MATRICULA)
	private String matricula;

	@Validacao(metodo = ConstantesReflection.VALIDA_SENHA, erro = Constantes.SENHA, cadastro = false)
	@MetodoAssociado(get = ConstantesReflection.GET_SENHA_PROTEGIDA, set = ConstantesReflection.SET_SENHA)
	private String senha;

	/**
	 * Construtor padrao.
	 * 
	 * @param nome
	 *            Nome do funcionario.
	 * @param cargo
	 *            Cargo do funcionario.
	 * @param dataNascimento
	 *            Data de nascimento do funcionario.
	 */
	public Funcionario(String nome, String cargo, String dataNascimento) {
		this(nome, cargo, dataNascimento, GeradorDeDadosDeSeguranca
				.getInstancia().geraMatricula(cargo, Util.getAnoAtual()));
	}

	/**
	 * Construtor com matricula.
	 * 
	 * @param nome
	 *            Nome do funcionario.
	 * @param cargo
	 *            Cargo do funcionario.
	 * @param dataNascimento
	 *            Data de nascimento do funcionario.
	 * @param matricula
	 *            Matricula do funcionario.
	 */
	public Funcionario(String nome, String cargo, String dataNascimento,
			String matricula) {
		// Nao eh necessario validar porque ja eh feito isso
		// la no gerenciador de funcionarios
		this.nome = nome;
		this.setCargo(cargo);
		this.matricula = matricula;
		this.senha = GeradorDeDadosDeSeguranca.getInstancia().geraSenha(
				matricula, Util.getAnoPorData(dataNascimento));
		this.data = dataNascimento;
	}

	// VERIFICACAO DE PERMISSAO
	/**
	 * Verifica se o funcionario possui determinada permissao.
	 * 
	 * @param permissao
	 *            Permissao a ser verificada.
	 * @return true se o funcionario possui a permissao.
	 */
	public boolean temPermissao(Permissao permissao) {
		return this.cargo.temPermissao(permissao);
	}

	// VERIFICACAO DE PERMISSAO
	// GETTERS E SETTERS
	/**
	 * Retorna o nome do funcionario.
	 * 
	 * @return Nome do funcionario.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Muda o nome do funcionario.
	 * 
	 * @param nome
	 *            Nome novo do funcionario.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Retorna o nome do cargo do funcionario.
	 * 
	 * @return Nome do cargo do funcionario.
	 */
	public String getCargoNome() {
		return cargo.getNome();
	}

	/**
	 * Retorna o cargo do funcionario.
	 * 
	 * @return Cargo do funcionario.
	 */
	public Cargo getCargo() {
		return cargo;
	}

	/**
	 * Muda o cargo do funcionario.
	 * 
	 * @param cargo
	 *            Cargo novo do funcionario.
	 */
	public void setCargo(String cargo) {
		try {
			this.cargo = (Cargo) Reflection.godFactory(Util.getNomeClasse(Cargo.class, cargo), MensagensDeErro.CARGO_INVALIDO_FUNCIONARIO, cargo);
		} catch (DadoInvalidoException excecao) {
			throw new OperacaoInvalidaException(excecao.getMessage());
		}
	}

	/**
	 * Retorna a matricula do funcionario.
	 * 
	 * @return Matricula do funcionario.
	 */
	public String getMatricula() {
		return matricula;
	}

	/**
	 * Retorna a senha do funcionario.
	 * 
	 * @return Senha do funcionario.
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * Metodo utilizado para consulta de senha.
	 */
	public void getSenhaProtegida() {
		throw new OperacaoInvalidaException("A senha do funcionario eh protegida.");
	}

	/**
	 * Muda a senha do funcionario.
	 * 
	 * @param senha
	 *            Senha nova do funcionario.
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * Retorna a data de nascimento do funcionario.
	 * 
	 * @return Data de nascimento do funcionario.
	 */
	public String getDataNascimento() {
		return Util.transformaFormatoData(this.data);
	}

	/**
	 * Muda a data do nascimento do funcionario.
	 * 
	 * @param dataNascimento
	 *            Data de nascimento do funcionario.
	 * @throws DadoInvalidoException
	 *             Caso a data seja invalida.
	 */
	public void setData(String dataNascimento) throws DadoInvalidoException {
		this.data = dataNascimento;
	}
	// GETTERS E SETTERS

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
		return result;
	}

	/**
	 * Equals de funcionario, tendo em vista que a matricula identifica cada um.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Funcionario))
			return false;
		Funcionario outro = (Funcionario) obj;

		return this.matricula.equals(outro.matricula);
	}
}
