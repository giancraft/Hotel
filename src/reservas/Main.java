package reservas;

import java.time.LocalDate;

import enums.Categoria;

public class Main {

	public static void main(String[] args) {
		Quarto quarto = new Quarto(1, Categoria.ECONOMICO);
		Quarto quarto2 = new Quarto(2, Categoria.ECONOMICO);
		LocalDate checkIn = LocalDate.parse("2024-10-23");
		LocalDate checkOut = LocalDate.parse("2024-10-25");
		CadastroReserva cad = new CadastroReserva();
		cad.inserir(1, quarto, "gian", checkIn, checkOut);
		cad.inserir(5, quarto2, "pedro", checkIn, checkOut);
		cad.remover(1);
		cad.inserir(2, quarto, "igor", checkIn, checkOut);
		cad.mostrarArvore();

	}

}
