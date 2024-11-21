package nodo;

import enums.Cor;

public class Nodo {
	private Nodo dir, esq, pai;
	private int alturaEsq, alturaDir;
	Cor cor;
	
	public Nodo() {
		dir = esq = pai = null;
		alturaEsq = alturaDir = 0;
		this.cor = Cor.VERMELHO;
	}

	public Nodo getDir() {
		return dir;
	}

	public void setDir(Nodo dir) {
		this.dir = dir;
	}

	public Nodo getEsq() {
		return esq;
	}

	public void setEsq(Nodo esq) {
		this.esq = esq;
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

	public Nodo getPai() {
		return pai;
	}

	public void setPai(Nodo pai) {
		this.pai = pai;
	}

	public Cor getCor() {
		return cor;
	}

	public void setCor(Cor cor) {
		this.cor = cor;
	}
}