package projeto.hospital.facade;

import projeto.exceptions.logica.AcessoBloqueadoException;
import projeto.hospital.controller.Controller;
import projeto.util.Constantes;

public class Facade {

	private final String CHAVE_DESBLOQUEIO = "c041ebf8";
	private Controller controller;
	private boolean sistemaJaIniciado;

	public Facade() {
		this.controller = new Controller();
		this.sistemaJaIniciado = false;
	}

	public Facade iniciaSistema() {
		return this;
	}

	public String liberaSistema(String chave, String nome, String dataNascimento) {
		if (sistemaJaIniciado) {
			throw new AcessoBloqueadoException("Erro ao liberar o sistema. Sistema liberado anteriormente.");
		} else if (CHAVE_DESBLOQUEIO.equals(chave)) {
			String matricula = this.cadastraFuncionario(nome, Constantes.DIRETOR, dataNascimento);
			sistemaJaIniciado = true;
			return matricula;
		} else {
			throw new AcessoBloqueadoException("Erro ao liberar o sistema. Chave invalida.");
		}
	}

	public void login(String matricula, String senha) {
		this.controller.acessaSistema(matricula, senha);
	}

	public String getInfoFuncionario(String matricula, String atributo) {
		return this.controller.getInfoFuncionario(matricula, atributo);
	}

	public String cadastraFuncionario(String nome, String cargo, String dataNascimento) {
		return this.controller.cadastraFuncionario(nome, cargo, dataNascimento);
	}

	public boolean demiteFuncionario(String senhaDiretor, String matriculaFuncionario) {
		return this.controller.demiteFuncionario(senhaDiretor, matriculaFuncionario);
	}

}
