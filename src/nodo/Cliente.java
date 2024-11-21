package nodo;

import java.time.LocalDate;
import enums.Cor;
import reservas.Quarto;

public class Cliente {
	private int cpf;
	private Quarto quarto;
	private String nome;
	private LocalDate checkIn;
	private LocalDate checkOut;
	private Cliente dir, esq, pai;
	private int alturaEsq, alturaDir;
	Cor cor;

	public Cliente(int cpf, Quarto quarto, String nome, LocalDate checkIn, LocalDate checkOut) {
		this.cpf = cpf;
		this.quarto = quarto;
		this.nome = nome;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		dir = esq = pai = null;
		alturaEsq = alturaDir = 0;
		this.cor = Cor.VERMELHO;
	}

	public int getCpf() {
		return cpf;
	}

	public void setCpf(int cpf) {
		this.cpf = cpf;
	}

	public Quarto getQuarto() {
		return quarto;
	}

	public void setQuarto(Quarto quarto) {
		this.quarto = quarto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDate getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(LocalDate checkIn) {
		this.checkIn = checkIn;
	}

	public LocalDate getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(LocalDate checkOut) {
		this.checkOut = checkOut;
	}

	public Cliente getDir() {
		return dir;
	}

	public void setDir(Cliente dir) {
		this.dir = dir;
	}

	public Cliente getEsq() {
		return esq;
	}

	public void setEsq(Cliente esq) {
		this.esq = esq;
	}

	public Cliente getPai() {
		return pai;
	}

	public void setPai(Cliente pai) {
		this.pai = pai;
	}

	public int getAlturaEsq() {
		return alturaEsq;
	}

	public void setAlturaEsq(int alturaEsq) {
		this.alturaEsq = alturaEsq;
	}

	public int getAlturaDir() {
		return alturaDir;
	}

	public void setAlturaDir(int alturaDir) {
		this.alturaDir = alturaDir;
	}

	public Cor getCor() {
		return cor;
	}

	public void setCor(Cor cor) {
		this.cor = cor;
	}
}
