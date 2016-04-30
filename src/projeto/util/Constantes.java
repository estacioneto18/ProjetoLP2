package projeto.util;

import org.junit.internal.runners.ErrorReportingRunner;

public class Constantes {

	public static final String STRING_VAZIA = "";
	public static final String BARRA = "/";
	public static final String FORMATO_TRES_NUMEROS = "000";
	
	public static final String DIRETOR = "Diretor Geral";
	public static final String MEDICO = "Medico";
	public static final String TECNICO = "Tecnico Administrativo";
	public static final String FUNCIONARIO = "Funcionario";
	public final static String CODIGO_DIRETOR = "1";
	public final static String CODIGO_MEDICO = "2";
	public final static String CODIGO_TECNICO = "3";
	public final static String PRIMEIRO_CADASTRO = "001";
	
	public static final String NOME = "Nome";
	public static final String PRECO = "Preco";
	public static final String CARGO = "Cargo";
	public static final String ATRIBUTO = "Atributo";
	public static final String DATA_NASCIMENTO = "Data";
	
	public static final String SENHA = "Senha";
	public static final String MATRICULA = "Matricula";
	
	public static final String DATA_REGEX = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d{2})";
	
	public static final String ERRO_CADASTRO_FUNCIONARIO = "Erro no cadastro de funcionario. ";
	public static final String ERRO_NOME_FUNCIONARIO = ERRO_CADASTRO_FUNCIONARIO + "Nome do funcionario ";
	public static final String ERRO_MATRICULA_FUNCIONARIO = ERRO_CADASTRO_FUNCIONARIO + "Matricula do funcionario ";
	public static final String ERRO_DATA_NASCIMENTO_FUNCIONARIO = ERRO_CADASTRO_FUNCIONARIO + "Data ";
	
	public static final int ZERO = 0;
	public static final int INDICE_DIA = ZERO;
	public static final int INDICE_MES = 1;
	public static final int INDICE_ANO = 2;
	public static final int QUATRO = 4;
	
}
