package reservas;

import java.time.LocalDate;
import java.util.ArrayList;

import enums.Categoria;

public class Main {

	public static void main(String[] args) {
		Quarto quarto = new Quarto(1, Categoria.ECONOMICO);
		Quarto quarto2 = new Quarto(2, Categoria.ECONOMICO);
        ArrayList<Quarto> lista = new ArrayList<>();
        lista.add(quarto);
        lista.add(quarto2);
		LocalDate checkIn = LocalDate.parse("2024-10-23");
		LocalDate checkOut = LocalDate.parse("2024-10-25");
		LocalDate checkIn2 = LocalDate.parse("2024-10-27");
		LocalDate checkOut2 = LocalDate.parse("2024-10-30");
		CadastroReserva cad = new CadastroReserva(lista);
		cad.exibirQuartosDisponiveis(checkIn, checkOut, Categoria.ECONOMICO);
		System.out.println();
		cad.listarReservasPorDataCheckIn();
		System.out.println();
		cad.inserir(1, quarto, "gian", checkIn, checkOut);
		cad.inserir(5, quarto2, "pedro", checkIn2, checkOut2);
		System.out.println();
		cad.exibirQuartosDisponiveis(checkIn, checkOut, Categoria.ECONOMICO);
		System.out.println();
		cad.listarReservasPorDataCheckIn();
		System.out.println();
		cad.cancelarReserva(1);
		System.out.println();
		double taxa = cad.calcularTaxaOcupacao(LocalDate.of(2024, 10, 23), LocalDate.of(2024, 10, 31));
	    System.out.println("Taxa de Ocupação: " + taxa + "%");
		System.out.println();
		cad.quartosMaisEMenosReservados();
		System.out.println();
		System.out.println();
		long cancelamentos = cad.calcularCancelamentos(LocalDate.of(2024, 10, 23), LocalDate.of(2024, 10, 31));
	    System.out.println("Número de Cancelamentos: " + cancelamentos);
		System.out.println();
		cad.exibirQuartosDisponiveis(checkIn, checkOut, Categoria.ECONOMICO);
		System.out.println();
		cad.listarReservasPorDataCheckIn();
		System.out.println();
		cad.inserir(2, quarto, "igor", checkIn, checkOut);
		System.out.println();
		cad.exibirQuartosDisponiveis(checkIn, checkOut, Categoria.ECONOMICO);
		System.out.println();
		cad.listarReservasPorDataCheckIn();
		System.out.println();
		cad.mostrarArvore();
		System.out.println();
		cad.mostrarHistorico();
		System.out.println();
	
		cad.consultarReserva(5);
	}

}
