package projeto.hospital.gerencia.prontuario.paciente.fidelidade;

import java.io.Serializable;

import projeto.util.Constantes;

/**
 * Fidelidade padrao.
 * 
 * @author Estacio Pereira
 * @author Thaynan Andrey
 */
public class FidelidadePadrao implements Fidelidade, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2425017466808695066L;

	@Override
	public double getDescontoServico() {
		Double zero = 0.0; 
		return zero;
	}

	@Override
	public double getCreditoBonus() {
		return Constantes.ZERO;
	}

}
