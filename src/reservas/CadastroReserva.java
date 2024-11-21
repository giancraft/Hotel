package reservas;

import java.time.LocalDate;
import enums.Cor;
import nodo.Cliente;

public class CadastroReserva {
    private Cliente raiz;

    public CadastroReserva() {
        raiz = null;
    }

    public void inserir(int cpf, Quarto quarto, String nome, LocalDate checkIn, LocalDate checkOut) {
        if (!verificarDisponibilidade(quarto, checkIn, checkOut)) {
            System.out.println("Conflito de reserva: O quarto já está ocupado no período solicitado.");
            return;
        }
        Cliente novoNodo = new Cliente(cpf, quarto, nome, checkIn, checkOut);
        raiz = inserirNodo(raiz, novoNodo);
        corrigirInserir(novoNodo);
    }


    private boolean verificarDisponibilidade(Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        return verificarDisponibilidadeRecursiva(raiz, quarto, checkIn, checkOut);
    }

    private boolean verificarDisponibilidadeRecursiva(Cliente nodo, Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        if (nodo == null) {
            return true; // Nenhum conflito encontrado
        }

        // Verifica se é o mesmo quarto e se há sobreposição de datas
        if (nodo.getQuarto().getNumQuarto() == quarto.getNumQuarto() &&
            !(checkOut.isBefore(nodo.getCheckIn()) || checkIn.isAfter(nodo.getCheckOut()))) {
            return false; // Conflito encontrado
        }

        // Continua a busca em ambos os lados da árvore
        return verificarDisponibilidadeRecursiva(nodo.getEsq(), quarto, checkIn, checkOut) &&
               verificarDisponibilidadeRecursiva(nodo.getDir(), quarto, checkIn, checkOut);
    }


    private Cliente inserirNodo(Cliente atual, Cliente novoNodo) {
        if (atual == null) {
            return novoNodo;
        }

        if (novoNodo.getCpf() < atual.getCpf()) {
            atual.setEsq(inserirNodo(atual.getEsq(), novoNodo));
            atual.getEsq().setPai(atual);
        } else if (novoNodo.getCpf() > atual.getCpf()) {
            atual.setDir(inserirNodo(atual.getDir(), novoNodo));
            atual.getDir().setPai(atual);
        }
        return atual;
    }

    private void corrigirInserir(Cliente nodo) {
        Cliente pai, avo;

        while (nodo != raiz && nodo.getPai().getCor() == Cor.VERMELHO) {
            pai = nodo.getPai();
            avo = pai.getPai();

            if (pai == avo.getEsq()) {
                Cliente tio = avo.getDir();

                if (tio != null && tio.getCor() == Cor.VERMELHO) {
                    pai.setCor(Cor.PRETO);
                    tio.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    nodo = avo;
                } else {
                    if (nodo == pai.getDir()) {
                        nodo = pai;
                        rotacaoEsquerda(nodo);
                    }

                    pai.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    rotacaoDireita(avo);
                }
            } else {
                Cliente tio = avo.getEsq();

                if (tio != null && tio.getCor() == Cor.VERMELHO) {
                    pai.setCor(Cor.PRETO);
                    tio.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    nodo = avo;
                } else {
                    if (nodo == pai.getEsq()) {
                        nodo = pai;
                        rotacaoDireita(nodo);
                    }

                    pai.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    rotacaoEsquerda(avo);
                }
            }
        }
        raiz.setCor(Cor.PRETO);
    }

    private void rotacaoEsquerda(Cliente nodo) {
        Cliente novoNodo = nodo.getDir();
        nodo.setDir(novoNodo.getEsq());

        if (novoNodo.getEsq() != null) {
            novoNodo.getEsq().setPai(nodo);
        }

        novoNodo.setPai(nodo.getPai());

        if (nodo.getPai() == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.getPai().getEsq()) {
            nodo.getPai().setEsq(novoNodo);
        } else {
            nodo.getPai().setDir(novoNodo);
        }

        novoNodo.setEsq(nodo);
        nodo.setPai(novoNodo);
    }

    private void rotacaoDireita(Cliente nodo) {
        Cliente novoNodo = nodo.getEsq();
        nodo.setEsq(novoNodo.getDir());

        if (novoNodo.getDir() != null) {
            novoNodo.getDir().setPai(nodo);
        }

        novoNodo.setPai(nodo.getPai());

        if (nodo.getPai() == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.getPai().getDir()) {
            nodo.getPai().setDir(novoNodo);
        } else {
            nodo.getPai().setEsq(novoNodo);
        }

        novoNodo.setDir(nodo);
        nodo.setPai(novoNodo);
    }
    
    public void mostrarArvore() {
		if (raiz == null) {
			System.out.println("A árvore está vazia");
		} else {
			mostrarArvoreRecursiva(raiz, "", true);
		}
	}
	
	private void mostrarArvoreRecursiva(Cliente nodo, String prefixo, boolean isFilhoDireito) {
		if (nodo != null) {
			System.out.println(prefixo + (isFilhoDireito ? "|---" : "|---") + nodo.getCpf() + " (" + nodo.getCor() + ")");
			String novoPrefixo = prefixo + (isFilhoDireito ? " " : "|");
			mostrarArvoreRecursiva(nodo.getDir(), novoPrefixo, true);
			mostrarArvoreRecursiva(nodo.getEsq(), novoPrefixo, false);
		}
	}
	
	public void remover(int cpf) {
	    Cliente clienteParaRemover = buscar(raiz, cpf);
	    if (clienteParaRemover == null) {
	        System.out.println("Cliente com CPF " + cpf + " não encontrado.");
	        return;
	    }

	    // Remove o cliente da árvore
	    raiz = removerNodo(raiz, clienteParaRemover);
	    System.out.println("Reserva do cliente com CPF " + cpf + " foi cancelada.");
	}

	private Cliente removerNodo(Cliente raiz, Cliente nodo) {
	    if (raiz == null) {
	        return null;
	    }

	    if (nodo.getCpf() < raiz.getCpf()) {
	        raiz.setEsq(removerNodo(raiz.getEsq(), nodo));
	    } else if (nodo.getCpf() > raiz.getCpf()) {
	        raiz.setDir(removerNodo(raiz.getDir(), nodo));
	    } else {
	        // Caso 1: Nodo com no máximo um filho
	        if (raiz.getEsq() == null || raiz.getDir() == null) {
	            Cliente filho = (raiz.getEsq() != null) ? raiz.getEsq() : raiz.getDir();

	            // Caso 1.1: Nodo sem filhos
	            if (filho == null) {
	                filho = raiz;
	                raiz = null;
	            } else {
	                // Caso 1.2: Nodo com um filho
	                raiz = filho;
	            }
	        } else {
	            // Caso 2: Nodo com dois filhos
	            Cliente substituto = obterMinimo(raiz.getDir());
	            raiz.setCpf(substituto.getCpf());
	            raiz.setQuarto(substituto.getQuarto());
	            raiz.setNome(substituto.getNome());
	            raiz.setCheckIn(substituto.getCheckIn());
	            raiz.setCheckOut(substituto.getCheckOut());

	            raiz.setDir(removerNodo(raiz.getDir(), substituto));
	        }
	    }

	    // Caso em que a árvore se torna vazia
	    if (raiz == null) {
	        return null;
	    }

	    // Corrigir a árvore após a remoção
	    return corrigirRemocao(raiz);
	}

	private Cliente corrigirRemocao(Cliente nodo) {
	    if (nodo == null) {
	        return null;
	    }

	    while (nodo != raiz && nodo.getCor() == Cor.PRETO) {
	        Cliente pai = nodo.getPai();

	        if (nodo == pai.getEsq()) {
	            Cliente irmao = pai.getDir();

	            if (irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                rotacaoEsquerda(pai);
	                irmao = pai.getDir();
	            }

	            if ((irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) &&
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                irmao.setCor(Cor.VERMELHO);
	                nodo = pai;
	            } else {
	                if (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO) {
	                    if (irmao.getEsq() != null) {
	                        irmao.getEsq().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    rotacaoDireita(irmao);
	                    irmao = pai.getDir();
	                }

	                irmao.setCor(pai.getCor());
	                pai.setCor(Cor.PRETO);
	                if (irmao.getDir() != null) {
	                    irmao.getDir().setCor(Cor.PRETO);
	                }
	                rotacaoEsquerda(pai);
	                nodo = raiz;
	            }
	        } else {
	            Cliente irmao = pai.getEsq();

	            if (irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                rotacaoDireita(pai);
	                irmao = pai.getEsq();
	            }

	            if ((irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) &&
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                irmao.setCor(Cor.VERMELHO);
	                nodo = pai;
	            } else {
	                if (irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) {
	                    if (irmao.getDir() != null) {
	                        irmao.getDir().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    rotacaoEsquerda(irmao);
	                    irmao = pai.getEsq();
	                }

	                irmao.setCor(pai.getCor());
	                pai.setCor(Cor.PRETO);
	                if (irmao.getEsq() != null) {
	                    irmao.getEsq().setCor(Cor.PRETO);
	                }
	                rotacaoDireita(pai);
	                nodo = raiz;
	            }
	        }
	    }

	    nodo.setCor(Cor.PRETO);
	    return nodo;
	}

	private Cliente obterMinimo(Cliente nodo) {
	    while (nodo.getEsq() != null) {
	        nodo = nodo.getEsq();
	    }
	    return nodo;
	}

	private Cliente buscar(Cliente nodo, int cpf) {
	    if (nodo == null || nodo.getCpf() == cpf) {
	        return nodo;
	    }
	    if (cpf < nodo.getCpf()) {
	        return buscar(nodo.getEsq(), cpf);
	    }
	    return buscar(nodo.getDir(), cpf);
	}

}
