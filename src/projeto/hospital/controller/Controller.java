package projeto.hospital.controller;

import java.io.Serializable;

import projeto.exceptions.dados.DadoInvalidoException;
import projeto.exceptions.logica.AcessoBloqueadoException;
import projeto.exceptions.logica.OperacaoInvalidaException;
import projeto.hospital.gerencia.bancodeorgaos.BancoDeOrgaos;
import projeto.hospital.gerencia.bancodeorgaos.Orgao;
import projeto.hospital.gerencia.farmacia.Farmacia;
import projeto.hospital.gerencia.funcionario.Funcionario;
import projeto.hospital.gerencia.funcionario.GerenciadorDeFuncionarios;
import projeto.hospital.gerencia.funcionario.cargo.Permissao;
import projeto.hospital.gerencia.procedimento.GerenciadorProcedimento;
import projeto.hospital.gerencia.prontuario.GerenciadorDePacienteProntuario;
import projeto.hospital.gerencia.prontuario.Prontuario;
import projeto.util.Constantes;
import projeto.util.MensagensDeErro;
import projeto.util.Util;
import projeto.util.ValidadorDeDados;
import projeto.util.ValidadorDeLogica;

/**
 * Classe controladora. Gerencia a logica de negocio do sistema delegando
 * responsabilidades e conectando a facade com a logica do sistema (Model).
 * 
 * @author Estacio Pereira
 * @author Eric
 * @author Thaynan
 */
public class Controller implements Serializable {

	/**
	 * Serial gerado automaticamente.
	 */
	private static final long serialVersionUID = -1215543804197231123L;

	private Funcionario funcionarioLogado;
	private GerenciadorDeFuncionarios gerenciadorFuncionarios;
	private GerenciadorDePacienteProntuario gerenciadorDePaciente;
	private Farmacia farmacia;
	private BancoDeOrgaos bancoDeOrgaos;
	private GerenciadorProcedimento gerenciadorProcedimento;

	private final String CHAVE_DESBLOQUEIO = "c041ebf8";
	private boolean sistemaJaLiberado;

	/**
	 * Construtor
	 */
	public Controller() {
		this.gerenciadorDePaciente = new GerenciadorDePacienteProntuario();
		this.farmacia = new Farmacia();
		this.gerenciadorFuncionarios = new GerenciadorDeFuncionarios();
		this.bancoDeOrgaos = new BancoDeOrgaos();
		this.gerenciadorProcedimento = new GerenciadorProcedimento();
		this.funcionarioLogado = null;
		this.sistemaJaLiberado = false;
	}

	// OPERACOES DO SISTEMA

	/**
	 * Inicia o sistema, tentando carregar um arquivo ja salvo
	 */
	public void iniciaSistema() {
		try {
			Controller controllerSalvo = (Controller) Util.getObjetoArquivo(Constantes.ARQUIVO_CONTROLLER);

			this.bancoDeOrgaos = controllerSalvo.bancoDeOrgaos;
			this.farmacia = controllerSalvo.farmacia;
			this.gerenciadorDePaciente = controllerSalvo.gerenciadorDePaciente;
			this.gerenciadorFuncionarios = controllerSalvo.gerenciadorFuncionarios;
			this.gerenciadorProcedimento = controllerSalvo.gerenciadorProcedimento;
		} catch (Exception excecao) {
			Util.criaArquivo(Constantes.ARQUIVO_CONTROLLER);
		}
	}

	/**
	 * Libera o sistema pela primeira vez.
	 * 
	 * @param chave
	 *            Chave do sistema.
	 * @param nome
	 *            Nome do usuario.
	 * @param dataNascimento
	 *            Data de nascimento do usuario.
	 * @return Matricula gerada.
	 */
	public String liberaSistema(String chave, String nome, String dataNascimento) {
		if (sistemaJaLiberado) {
			throw new AcessoBloqueadoException("Erro ao liberar o sistema. Sistema liberado anteriormente.");
		} else if (CHAVE_DESBLOQUEIO.equals(chave)) {
			String matricula = this.cadastraFuncionario(nome, Constantes.DIRETOR_GERAL, dataNascimento);
			sistemaJaLiberado = true;
			return matricula;
		} else {
			throw new AcessoBloqueadoException("Erro ao liberar o sistema. Chave invalida.");
		}
	}

	/**
	 * Fecha o sistema se nenhum usuario estiver mais logado
	 */
	public void fechaSistema() {
		if (estaLogado())
			throw new OperacaoInvalidaException("Nao foi possivel fechar o sistema. Um funcionario ainda esta logado: "
					+ funcionarioLogado.getNome() + ".");
		Util.guardaObjetoArquivo(Constantes.ARQUIVO_CONTROLLER, this);
	}

	/**
	 * Faz o login no sistema
	 * 
	 * @param matricula
	 *            Matricula
	 * @param senha
	 *            Senha
	 */
	public void loginSistema(String matricula, String senha) {
		if (estaLogado())
			throw new OperacaoInvalidaException("Nao foi possivel realizar o login. Um funcionario ainda esta logado: "
					+ funcionarioLogado.getNome() + ".");
		this.funcionarioLogado = this.gerenciadorFuncionarios.acessaSistema(matricula, senha);
	}

	/**
	 * Desloga do sistema
	 */
	public void logout() {
		if (!estaLogado())
			throw new OperacaoInvalidaException("Nao foi possivel realizar o logout. Nao ha um funcionario logado.");
		funcionarioLogado = null;
	}

	/**
	 * Verifica se ha funcionario logado.
	 * 
	 * @return true se um funcionario esta logado.
	 */
	private boolean estaLogado() {
		return this.funcionarioLogado != null;
	}

	// OPERACOES DO SISTEMA
	// OPERACOES DE FUNCIONARIO
	/**
	 * Realiza o cadastro de um funcionario.
	 * 
	 * @param nome
	 *            Nome do usuario a ser cadastrado
	 * @param cargo
	 *            Cargo do novo usuario
	 * @param dataNascimento
	 *            Data de nascimento do novo usuario
	 * @return Matricula do novo cadastro
	 */
	public String cadastraFuncionario(String nome, String cargo, String dataNascimento) {
		// Se o usuario esta logado vai ser verificado se ele pode realizar a
		// operacao porque o diretor eh cadastrado quando nao existe ninguem
		// logado, por isso precisa disso
		if (gerenciadorFuncionarios.temFuncionariosCadastrados()) {
			verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		}
		return this.gerenciadorFuncionarios.cadastraFuncionario(nome, cargo, dataNascimento, funcionarioLogado);
	}

	/**
	 * Exclui um funcionario do sistema
	 * 
	 * @param senhaDiretor
	 *            Senha do diretor
	 * @param matriculaFuncionario
	 *            Matricula do usuario a ser excluido
	 */
	public void excluiFuncionario(String senhaDiretor, String matriculaFuncionario) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.gerenciadorFuncionarios.excluiFuncionario(this.funcionarioLogado.getMatricula(), senhaDiretor,
				matriculaFuncionario);
	}

	/**
	 * Atualiza as informacoes de um outro usuario
	 * 
	 * @param matricula
	 *            Matricula do usuario
	 * @param atributo
	 *            Atributo a ser atualizado
	 * @param novoValor
	 *            Novo valor do atributo
	 */
	public void atualizaInfoFuncionario(String matricula, String atributo, String novoValor) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.gerenciadorFuncionarios.atualizaInfoFuncionario(this.funcionarioLogado, matricula, atributo, novoValor);
	}

	/**
	 * Atualiza o proprio usuario.
	 * 
	 * @param atributo
	 *            Atributo a ser atualizado
	 * @param novoValor
	 *            Novo valor do atributo
	 */
	public void atualizaInfoFuncionario(String atributo, String novoValor) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.gerenciadorFuncionarios.atualizaInfoFuncionario(funcionarioLogado, this.funcionarioLogado.getMatricula(),
				atributo, novoValor);
	}

	/**
	 * Atualiza senha do usuario
	 * 
	 * @param senhaAntiga
	 *            Senha antiga
	 * @param novaSenha
	 *            Nova senha
	 */
	public void atualizaSenha(String senhaAntiga, String novaSenha) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.gerenciadorFuncionarios.atualizaSenha(this.funcionarioLogado, senhaAntiga, novaSenha);
	}

	/**
	 * Verifica se o usuario esta logado para realizar uma acao
	 */
	private void verificaLogado(String msgErro) {
		if (!estaLogado())
			throw new OperacaoInvalidaException(msgErro);
	}

	// CONSULTA DE FUNCIONARIO
	/**
	 * Pega informacao de um funcionario
	 * 
	 * @param matricula
	 *            Matricula do usuario
	 * @param atributo
	 *            Atributo a ser requisitado
	 * @return Atributo requisitado
	 */
	public Object getInfoFuncionario(String matricula, String atributo) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		return this.gerenciadorFuncionarios.getInfoFuncionario(matricula, atributo);
	}

	// CONSULTA DE FUNCIONARIO
	// OPERACOES DE FUNCIONARIO
	// OPERACOES DE PACIENTE/PRONTUARIO

	/**
	 * Realiza o cadastro de um paciente
	 * 
	 * @param nome
	 *            Nome do paciente
	 * @param data
	 *            Data de nascimento do paciente
	 * @param peso
	 *            Peso do paciente
	 * @param sexo
	 *            Sexo biologico do paciente
	 * @param genero
	 *            Genero do paciente
	 * @param tipoSanguineo
	 *            Tipo sanguineo do paciente
	 * @return Id do paciente cadastrado
	 */
	public String cadastraPaciente(String nome, String data, double peso, String sexo, String genero,
			String tipoSanguineo) {
		verificaLogado("Voce precisa estar logado no sistema para realizar cadastros de pacientes.");
		return this.gerenciadorDePaciente.cadastraPaciente(nome, data, peso, sexo, genero, tipoSanguineo,
				funcionarioLogado);
	}

	// CONSULTA DE PACIENTE/PRONTUARIO
	/**
	 * Acessa uma informacao especifica sobre um paciente
	 * 
	 * @param idPaciente
	 *            Id do paciente
	 * @param atributo
	 *            Informacao a ser requisitada
	 * @return Informacao requisitada
	 */
	public Object getInfoPaciente(String idPaciente, String atributo) {
		verificaLogado("Voce precisa estar logado no sistema para realizar cadastros.");
		return this.gerenciadorDePaciente.getInfoPaciente(idPaciente, atributo);
	}

	/**
	 * Pega o id de um prontuario
	 * 
	 * @param posicao
	 *            Posicao do prontuario
	 * @return Id
	 */
	public String getProntuario(int posicao) {
		return this.gerenciadorDePaciente.getIdProntuarioPosicao(posicao);
	}

	/**
	 * Informa pontos de fidelidade de um paciente.
	 * 
	 * @param id
	 *            Id do paciente.
	 * @return Quantidade de pontos de fidelidade.
	 */
	public Integer getPontosFidelidade(String id) {
		return this.gerenciadorDePaciente.getPontosFidelidade(id);
	}

	/**
	 * Metodo responsavel por retornar os gastos do paciente.
	 * 
	 * @param id
	 *            Id do paciente
	 * @return String Gastos do paciente
	 */
	public String getGastosPaciente(String id) {
		return this.gerenciadorDePaciente.getGastosPaciente(id);
	}

	/**
	 * Pega o id de um paciente de acordo com seu nome
	 * 
	 * @param nome
	 *            Nome do paciente
	 * @return Id do paciente
	 */
	public String getPacienteId(String nome) {
		return this.gerenciadorDePaciente.getIdPaciente(nome);
	}

	/**
	 * Gera uma ficha de um paciente e guarda
	 * 
	 * @param idPaciente
	 *            id do paciente
	 */
	public void exportaFichaPaciente(String idPaciente) {
		this.gerenciadorDePaciente.exportaFichaPaciente(idPaciente);
	}

	// CONSULTA DE PACIENTE/PRONTUARIO
	// OPERACOES DE PACIENTE/PRONTUARIO
	// OPERACOES DE MEDICAMENTO/FARMACIA
	/**
	 * Metodo que cadastra um medicamento.
	 * 
	 * @param nome
	 *            Nome do medicamento.
	 * @param tipo
	 *            Tipo do medicamento.
	 * @param preco
	 *            Preco do medicamento.
	 * @param quantidade
	 *            Quantidade do medicamento.
	 * @param categorias
	 *            do medicamento.
	 * @return Nome do medicamento.
	 */
	public String cadastraMedicamento(String nome, String tipo, Double preco, int quantidade, String categorias) {
		verificaLogado("Voce precisa estar logado no sistema para realizar cadastros.");
		ValidadorDeLogica.validaOperacao(MensagensDeErro.ERRO_PERMISSAO_CADASTRO_MEDICAMENTO,
				Permissao.CADASTRAR_MEDICAMENTO, funcionarioLogado);

		return farmacia.addMedicamento(nome, preco, quantidade, tipo, categorias);
	}

	/**
	 * Metodo que atualiza um atributo de um medicamento.
	 * 
	 * @param nome
	 *            Nome do medicamento.
	 * @param atributo
	 *            Atributo a ser atualizado.
	 * @param novo
	 *            Novo valor do atributo
	 */
	public void atualizaMedicamento(String nome, String atributo, String novo) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.farmacia.atualizaMedicamento(nome, atributo, novo);
	}

	// CONSULTA DE MEDICAMENTO/FARMACIA
	/**
	 * Metodo que retorna um determinado atributo de um medicamento, passado o
	 * seu nome.
	 * 
	 * @param atributo
	 *            Atributo do medicamento.
	 * @param nome
	 *            Nome do medicamento.
	 * @return atributo do medicamento.
	 */
	public Object getInfoMedicamento(String atributo, String nome) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		return farmacia.getInfoMedicamento(atributo, nome);
	}

	/**
	 * Metodo que retorna uma lista em String de todos os medicamentos com
	 * determinada categoria.
	 * 
	 * @param categoria
	 *            Categoria do medicamento desejada.
	 * @return lista em String dos medicamentos.
	 */
	public String consultaMedCategoria(String categoria) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		return farmacia.consultaMedicamentoPorCategoria(categoria);
	}

	/**
	 * Metodo que retorna as caracteristicas de um medicamento.
	 * 
	 * @param nome
	 *            Nome do medicamento.
	 * @return Caracteristicas do medicamento.
	 */
	public String consultaMedicamentoNome(String nome) {
		return farmacia.pegaMedicamento(MensagensDeErro.ERRO_CONSULTA_MEDICAMENTO, nome).toString();
	}

	/**
	 * Metodo que retorna uma lista em String dos medicamentos a partir de uma
	 * ordem definida.
	 * 
	 * @param ordenacao
	 *            Ordenacao desejada.
	 * @return lista ordenada em String dos medicamentos.
	 */
	public String getEstoqueFarmacia(String ordenacao) {
		return farmacia.getEstoqueFarmacia(ordenacao);
	}

	// CONSULTA DE MEDICAMENTO/FARMACIA
	// OPERACOES DE MEDICAMENTO/FARMACIA
	// OPERACOES DE ORGAO

	/**
	 * Cadastra um orgao no banco de orgaos.
	 * 
	 * @param nome
	 *            Nome do orgao.
	 * @param tipoSanguineo
	 *            Tipo sanguineo do orgao.
	 */
	public void cadastraOrgao(String nome, String tipoSanguineo) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.bancoDeOrgaos.cadastraOrgao(nome, tipoSanguineo);
	}

	/**
	 * Retira orgao do banco de orgaos.
	 * 
	 * @param nome
	 *            Nome do orgao.
	 * @param tipoSanguineo
	 *            Tipo sanguineo do orgao.
	 */
	public void retiraOrgao(String nome, String tipoSanguineo) {
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		this.bancoDeOrgaos.retiraOrgao(nome, tipoSanguineo);
	}

	/**
	 * Busca os orgaos com o nome solicitado.
	 * 
	 * @param nome
	 *            Nome do orgao.
	 * @return String com tipos sanguineos dos orgaos de nome solicitado,
	 *         separados por virgula.
	 */
	public String buscaOrgPorNome(String nome) {
		return this.bancoDeOrgaos.buscaOrgPorNome(nome);
	}

	/**
	 * Busca os orgaos com o tipo sanguineo solicitado.
	 * 
	 * @param tipoSanguineo
	 *            tipo sanguineo do orgao.
	 * @return String com nomes dos orgaos de tipo sanguineo solicitado,
	 *         separados por virgula.
	 */
	public String buscaOrgPorSangue(String tipoSanguineo) {
		return this.bancoDeOrgaos.buscaOrgPorSangue(tipoSanguineo);
	}

	/**
	 * Verifica a existencia de um determinado orgao.
	 * 
	 * @param nome
	 *            Nome do orgao.
	 * @param tipoSanguineo
	 *            Tipo sanguineo do orgao.
	 * @return {@code true} se o banco tiver o orgao.
	 */
	public boolean buscaOrgao(String nome, String tipoSanguineo) {
		return this.bancoDeOrgaos.buscaOrgao(nome, tipoSanguineo);
	}

	/**
	 * Retorna a quantidade de orgaos com o nome solicitado.
	 * 
	 * @param nome
	 *            Nome do orgao.
	 * @return Quantidade de orgaos com o nome solicitado.
	 */
	public int qtdOrgaos(String nome) {
		return this.bancoDeOrgaos.qtdOrgaos(nome);
	}

	/**
	 * Retorna a quantidade total de orgaos no banco.
	 * 
	 * @return Quantidade de orgaos no banco de orgaos.
	 */
	public int totalOrgaosDisponiveis() {
		return this.bancoDeOrgaos.totalOrgaosDisponiveis();
	}

	/**
	 * Recupera orgao pelo nome do mesmo e pelo tipo sanguineo do paciente.
	 * 
	 * @param orgao
	 *            Nome do orgao.
	 * @param sanguePaciente
	 *            Tipo sanguineo do paciente.
	 * @return Orgao recuperado.
	 * @throws DadoInvalidoException
	 *             Caso nao possua orgao com o nome desejado.
	 */
	private Orgao recuperaOrgao(String orgao, String sanguePaciente) throws DadoInvalidoException {
		// Validacao de orgao de nome vazio
		verificaLogado(MensagensDeErro.LOGIN_NECESSARIO);
		ValidadorDeDados.validaString("Nome do orgao", orgao);
		try {
			return this.bancoDeOrgaos.getOrgao(orgao, sanguePaciente);
		} catch (OperacaoInvalidaException e) {
			// Excecao diferente no controller
			throw new DadoInvalidoException("Banco nao possui o orgao especificado.");
		}
	}

	// OPERACOES DE ORGAO
	// OPERACOES DE PROCEDIMENTO

	/**
	 * Realiza procedimento.
	 * 
	 * @param procedimento
	 *            Nome do procedimento.
	 * @param idPaciente
	 *            Id do paciente.
	 */
	public void realizaProcedimento(String procedimento, String idPaciente) {
		try {
			// Validacoes necessarias na ordem
			ValidadorDeDados.validaProcedimento(procedimento);
			ValidadorDeLogica.validaOperacao(MensagensDeErro.FUNCIONARIO_PROIBIDO_REALIZAR_PROCEDIMENTO,
					Permissao.REALIZA_PROCEDIMENTO, funcionarioLogado);

			Prontuario prontuario = this.gerenciadorDePaciente.getProntuarioPaciente(idPaciente);

			Double valorMedicamentos = 0.0;
			this.gerenciadorProcedimento.realizaProcedimento(procedimento, prontuario, this.funcionarioLogado.getNome(),
					valorMedicamentos);

		} catch (DadoInvalidoException | OperacaoInvalidaException e) {
			throw new OperacaoInvalidaException(MensagensDeErro.ERRO_REALIZAR_PROCEDIMENTO + e.getMessage());
		}
	}

	/**
	 * Realiza procedimento com medicamentos associados.
	 * 
	 * @param procedimento
	 *            Nome do procedimento.
	 * @param idPaciente
	 *            Id do paciente.
	 * @param medicamentos
	 *            Medicamentos necessarios.
	 */
	public void realizaProcedimento(String procedimento, String idPaciente, String medicamentos) {
		try {
			// Validacoes necessarias na ordem
			ValidadorDeDados.validaProcedimento(procedimento);
			ValidadorDeLogica.validaOperacao(MensagensDeErro.FUNCIONARIO_PROIBIDO_REALIZAR_PROCEDIMENTO,
					Permissao.REALIZA_PROCEDIMENTO, funcionarioLogado);

			Prontuario prontuario = this.gerenciadorDePaciente.getProntuarioPaciente(idPaciente);

			Double valorMedicamentos = this.farmacia.getValorMedicamentos(medicamentos);
			this.gerenciadorProcedimento.realizaProcedimento(procedimento, prontuario, this.funcionarioLogado.getNome(),
					valorMedicamentos);
		} catch (DadoInvalidoException | OperacaoInvalidaException e) {
			throw new OperacaoInvalidaException(MensagensDeErro.ERRO_REALIZAR_PROCEDIMENTO + e.getMessage());
		}
	}

	/**
	 * Realiza procedimento com medicamentos e orgao associado.
	 * 
	 * @param procedimento
	 *            Nome do procedimento.
	 * @param idPaciente
	 *            Id do paciente.
	 * @param medicamentos
	 *            Medicamentos necessarios.
	 * @param orgao
	 *            Orgao necessario.
	 */
	public void realizaProcedimento(String procedimento, String idPaciente, String medicamentos, String orgao) {
		try {
			// Validacoes necessarias na ordem
			ValidadorDeDados.validaProcedimento(procedimento);
			ValidadorDeLogica.validaOperacao(MensagensDeErro.FUNCIONARIO_PROIBIDO_REALIZAR_PROCEDIMENTO,
					Permissao.REALIZA_PROCEDIMENTO, funcionarioLogado);

			Prontuario prontuario = this.gerenciadorDePaciente.getProntuarioPaciente(idPaciente);

			Double valorMedicamentos = this.farmacia.getValorMedicamentos(medicamentos);
			String sanguePaciente = prontuario.getPaciente().getTipoSanguineo();
			Orgao orgaoRecuperado = recuperaOrgao(orgao, sanguePaciente);

			// Chamada do metodo com orgao
			this.gerenciadorProcedimento.realizaProcedimento(procedimento, prontuario, this.funcionarioLogado.getNome(),
					valorMedicamentos, orgaoRecuperado);
		} catch (DadoInvalidoException excecao) {
			throw new OperacaoInvalidaException(MensagensDeErro.ERRO_REALIZAR_PROCEDIMENTO + excecao.getMessage());
		}
	}

	/**
	 * Pega a quantidade de procedimentos realizados pelo paciente
	 * 
	 * @param idPaciente
	 *            Id do paciente
	 * @return Quantidade de procedimentos realizados
	 */
	public int getTotalProcedimento(String idPaciente) {
		return this.gerenciadorDePaciente.getTotalProcedimentos(idPaciente);
	}
	// OPERACOES DE PROCEDIMENTO
}
