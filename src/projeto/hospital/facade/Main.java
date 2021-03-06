package projeto.hospital.facade;

import projeto.util.Constantes;
import projeto.util.Util;
import easyaccept.EasyAccept;

public class Main {
	public static void main(String[] args) {
		// Aqui ele vai resetar todos os dados guardados, e o sistema inicia do zero sempre
		Util.limpaDados(Constantes.ARQUIVO_DADOS);
		Util.limpaDados(Constantes.PACIENTES_DADOS);
		args = new String[] { "projeto.hospital.facade.Facade", "acceptTests/usecase_1.txt",
				"acceptTests/usecase_2.txt", "acceptTests/usecase_3.txt", "acceptTests/usecase_4.txt",
				"acceptTests/usecase_5.txt", "acceptTests/usecase_6.txt", "acceptTests/usecase_7.txt" };
		EasyAccept.main(args);
	}
}