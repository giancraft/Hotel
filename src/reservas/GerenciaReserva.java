package reservas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;

import enums.Categoria;
import enums.Cor;
import nodo.Cliente;

public class GerenciaReserva {
    private Cliente raiz;
    private Cliente raizHistorico;
    private List<Quarto> todosQuartos;
    private static final double TAXA_OCUPACAO_LIMITE = 0.9;

    public GerenciaReserva(List<Quarto> quartos) {
        raiz = null;
        raizHistorico = null;
        this.todosQuartos = quartos;
    }

    public void inserir(int cpf, Quarto quarto, String nome, LocalDate checkIn, LocalDate checkOut) {
        if (!verificarDisponibilidade(quarto, checkIn, checkOut)) {
            System.out.println("Conflito de reserva: O quarto já está ocupado no período solicitado.");
            return;
        }
        Cliente novoNodo = new Cliente(cpf, quarto, nome, checkIn, checkOut);
        raiz = inserirNodo(raiz, novoNodo);
        corrigirInserir(novoNodo);
        
     // Verifica a taxa de ocupação após inserir a reserva
        verificarTaxaOcupacao();
    }
    
    public void verificarTaxaOcupacao() {
        // Calcula o número total de quartos ocupados
        long quartosOcupados = todosQuartos.stream()
            .filter(this::quartoEstaOcupado)
            .count();

        // Calcula a taxa de ocupação
        double taxaOcupacao = (double) quartosOcupados / todosQuartos.size();

        // Verifica se a taxa de ocupação excede o limite
        if (taxaOcupacao >= TAXA_OCUPACAO_LIMITE) {
            System.out.println("ALERTA: Taxa de ocupação atingiu " + (taxaOcupacao * 100) + "%.");
        } else {
            System.out.println("Taxa de ocupação atual: " + (taxaOcupacao * 100) + "%.");
        }
    }

    private boolean quartoEstaOcupado(Quarto quarto) {
        return !verificarDisponibilidade(quarto, LocalDate.now(), LocalDate.now().plusDays(1));
    }


    private boolean verificarDisponibilidade(Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        return verificarDisponibilidadeRecursiva(raiz, quarto, checkIn, checkOut);
    }

    private boolean verificarDisponibilidadeRecursiva(Cliente nodo, Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        if (nodo == null) {
            return true; // Nenhum conflito encontrado
        }

        // Verifica se é o mesmo quarto e se há sobreposição de datas
        if (nodo.getQuarto() != null && nodo.getCheckIn() != null && nodo.getCheckOut() != null) {
            // Verifica se há conflito com o quarto e o período
            boolean conflito = nodo.getQuarto().getNumQuarto() == quarto.getNumQuarto() &&
                               !(checkOut.isBefore(nodo.getCheckIn()) || checkIn.isAfter(nodo.getCheckOut()));

            if (conflito) {
                return false; // Conflito encontrado
            }
        }

        // Verifica recursivamente os lados esquerdo e direito da árvore
        boolean disponibilidadeEsquerda = true;
        if (nodo.getEsq() != null) {
            disponibilidadeEsquerda = verificarDisponibilidadeRecursiva(nodo.getEsq(), quarto, checkIn, checkOut);
        }

        boolean disponibilidadeDireita = true;
        if (nodo.getDir() != null) {
            disponibilidadeDireita = verificarDisponibilidadeRecursiva(nodo.getDir(), quarto, checkIn, checkOut);
        }

        return disponibilidadeEsquerda && disponibilidadeDireita;
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
        while (nodo != raiz && nodo.getPai() != null && nodo.getPai().getCor() == Cor.VERMELHO) {
            Cliente pai = nodo.getPai();
            Cliente avo = pai != null ? pai.getPai() : null;

            if (avo != null && pai == avo.getEsq()) {
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
            } else if (avo != null) {
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
        if (raiz != null) {
            raiz.setCor(Cor.PRETO);
        }
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
	
	public void cancelarReserva(int cpf) {
	    Cliente clienteParaCancelar = buscar(raiz, cpf);
	    if (clienteParaCancelar == null) {
	        System.out.println("Cliente com CPF " + cpf + " não encontrado.");
	        return;
	    }

	    // Cria uma cópia do cliente para adicionar ao histórico
	    Cliente copiaParaHistorico = new Cliente(
	        clienteParaCancelar.getCpf(),
	        clienteParaCancelar.getQuarto(),
	        clienteParaCancelar.getNome(),
	        clienteParaCancelar.getCheckIn(),
	        clienteParaCancelar.getCheckOut()
	    );
	    
	    // Adiciona a cópia ao histórico
	    raizHistorico = inserirNodo(raizHistorico, copiaParaHistorico);
	    corrigirInserirHistorico(copiaParaHistorico);

	    // Remove a reserva da árvore principal
	    raiz = removerNodo(raiz, clienteParaCancelar);
	    System.out.println("Reserva do cliente com CPF " + cpf + " foi cancelada e movida para o histórico.");
	}


	
	private void corrigirInserirHistorico(Cliente nodo) {
	    corrigirInserir(nodo); // Reaproveitando a lógica da árvore principal
	    
	    // Garantindo que a raiz da árvore de histórico seja preta
	    if (raizHistorico != null) {
	        raizHistorico.setCor(Cor.PRETO);
	    }
	}

	
	public void mostrarHistorico() {
	    if (raizHistorico == null) {
	        System.out.println("O histórico de reservas canceladas está vazio.");
	    } else {
	        System.out.println("Histórico de Reservas Canceladas:");
	        mostrarHistoricoDetalhado(raizHistorico);
	    }
	}

	// Método auxiliar para exibir o histórico detalhado
	private void mostrarHistoricoDetalhado(Cliente nodo) {
	    if (nodo != null) {
	        // Exibe o cliente atual
	        System.out.printf(
	            "CPF: %d, Nome: %s, Quarto: %d, Check-in: %s, Check-out: %s%n",
	            nodo.getCpf(),
	            nodo.getNome(),
	            nodo.getQuarto().getNumQuarto(),
	            nodo.getCheckIn(),
	            nodo.getCheckOut()
	        );
	        
	        // Recursivamente exibe o histórico em ordem
	        mostrarHistoricoDetalhado(nodo.getEsq());
	        mostrarHistoricoDetalhado(nodo.getDir());
	    }
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
	    while (nodo != raiz && (nodo == null || nodo.getCor() == Cor.PRETO)) {
	        Cliente pai = nodo.getPai();
	        if (nodo == pai.getEsq()) {
	            Cliente irmao = pai.getDir();
	            if (irmao != null && irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                rotacaoEsquerda(pai);
	                irmao = pai.getDir();
	            }
	            if (irmao == null || 
	                (irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) &&
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                if (irmao != null) {
	                    irmao.setCor(Cor.VERMELHO);
	                }
	                nodo = pai;
	            } else {
	                if (irmao != null && (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                    if (irmao.getEsq() != null) {
	                        irmao.getEsq().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    rotacaoDireita(irmao);
	                    irmao = pai.getDir();
	                }
	                if (irmao != null) {
	                    irmao.setCor(pai.getCor());
	                    if (irmao.getDir() != null) {
	                        irmao.getDir().setCor(Cor.PRETO);
	                    }
	                }
	                pai.setCor(Cor.PRETO);
	                rotacaoEsquerda(pai);
	                nodo = raiz;
	            }
	        } else {
	            Cliente irmao = pai.getEsq();
	            if (irmao != null && irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                rotacaoDireita(pai);
	                irmao = pai.getEsq();
	            }
	            if (irmao == null || 
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO) &&
	                (irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO)) {
	                if (irmao != null) {
	                    irmao.setCor(Cor.VERMELHO);
	                }
	                nodo = pai;
	            } else {
	                if (irmao != null && (irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO)) {
	                    if (irmao.getDir() != null) {
	                        irmao.getDir().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    rotacaoEsquerda(irmao);
	                    irmao = pai.getEsq();
	                }
	                if (irmao != null) {
	                    irmao.setCor(pai.getCor());
	                    if (irmao.getEsq() != null) {
	                        irmao.getEsq().setCor(Cor.PRETO);
	                    }
	                }
	                pai.setCor(Cor.PRETO);
	                rotacaoDireita(pai);
	                nodo = raiz;
	            }
	        }
	    }
	    if (nodo != null) {
	        nodo.setCor(Cor.PRETO);
	    }
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
	
	public void consultarReserva(int cpf) {
	    Cliente resultado = buscar(raiz, cpf);

	    if (resultado != null) {
	        System.out.println("Reserva encontrada:");
	        System.out.println("CPF: " + resultado.getCpf());
	        System.out.println("Nome: " + resultado.getNome());
	        System.out.println("Quarto: " + resultado.getQuarto().getNumQuarto());
	        System.out.println("Check-in: " + resultado.getCheckIn());
	        System.out.println("Check-out: " + resultado.getCheckOut());
	    } else {
	        System.out.println("Nenhuma reserva encontrada para o CPF: " + cpf);
	    }
	}
	
	// Método para listar quartos disponíveis
    public List<Quarto> listarQuartosDisponiveis(LocalDate checkIn, LocalDate checkOut, Categoria categoria) {
        List<Quarto> disponiveis = new ArrayList<>();

        for (Quarto quarto : todosQuartos) {
            if (quarto.getCategoria() == categoria &&
                verificarDisponibilidade(quarto, checkIn, checkOut)) {
                disponiveis.add(quarto);
            }
        }

        return disponiveis;
    }

    // Exibe os quartos disponíveis
    public void exibirQuartosDisponiveis(LocalDate checkIn, LocalDate checkOut, Categoria categoria) {
        List<Quarto> disponiveis = listarQuartosDisponiveis(checkIn, checkOut, categoria);

        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum quarto disponível para a categoria '" + categoria + "' no período solicitado.");
        } else {
            System.out.println("Quartos disponíveis:");
            for (Quarto quarto : disponiveis) {
                System.out.println("Quarto " + quarto.getNumQuarto() + " - Categoria: " + quarto.getCategoria());
            }
        }
    }
    
    public void listarReservasPorDataCheckIn() {
        if (raiz == null) {
            System.out.println("Não há reservas para exibir.");
            return;
        }

        // Obtem todas as reservas em uma lista
        List<Cliente> reservas = new ArrayList<>();
        coletarReservas(raiz, reservas);

        // Ordena pela data de check-in
        reservas = reservas.stream()
                .sorted(Comparator.comparing(Cliente::getCheckIn))
                .collect(Collectors.toList());

        // Exibe as reservas ordenadas
        System.out.println("Reservas ordenadas por data de check-in:");
        for (Cliente cliente : reservas) {
            System.out.printf(
                "Cliente: %s, CPF: %d, Quarto: %d, Check-in: %s, Check-out: %s%n",
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getQuarto().getNumQuarto(),
                cliente.getCheckIn(),
                cliente.getCheckOut()
            );
        }
    }

    // Método auxiliar para coletar os nodos da árvore
    private void coletarReservas(Cliente nodo, List<Cliente> reservas) {
        if (nodo != null) {
            coletarReservas(nodo.getEsq(), reservas);
            reservas.add(nodo);
            coletarReservas(nodo.getDir(), reservas);
        }
    }
    
    public double calcularTaxaOcupacao(LocalDate inicio, LocalDate fim) {
        long totalQuartos = todosQuartos.size();
        if (totalQuartos == 0) {
            return 0.0; // Sem quartos disponíveis
        }

        long quartosOcupados = todosQuartos.stream()
            .filter(quarto -> quarto != null && !verificarDisponibilidade(quarto, inicio, fim))
            .count();

        return (quartosOcupados / (double) totalQuartos) * 100;
    }

    public void quartosMaisEMenosReservados() {
        List<Quarto> quartosReservados = new ArrayList<>();

        // Adiciona todos os quartos reservados
        adicionarQuartosReservados(raiz, quartosReservados);

        // Calcula a frequência de cada quarto
        var frequencia = quartosReservados.stream()
            .collect(Collectors.groupingBy(Quarto::getNumQuarto, Collectors.counting()));

        // Obtém os quartos mais e menos reservados
        var maisReservado = frequencia.entrySet().stream()
            .max(Comparator.comparingLong(e -> e.getValue()));

        var menosReservado = frequencia.entrySet().stream()
            .min(Comparator.comparingLong(e -> e.getValue()));

        System.out.println("Quarto mais reservado: " +
            (maisReservado.isPresent() ? maisReservado.get().getKey() + " com " + maisReservado.get().getValue() + " reservas" : "Nenhum"));
        System.out.println("Quarto menos reservado: " +
            (menosReservado.isPresent() ? menosReservado.get().getKey() + " com " + menosReservado.get().getValue() + " reservas" : "Nenhum"));
    }

    private void adicionarQuartosReservados(Cliente nodo, List<Quarto> quartosReservados) {
        adicionarQuartosReservadosHelper(nodo, quartosReservados, new HashSet<>());
    }

    private void adicionarQuartosReservadosHelper(Cliente nodo, List<Quarto> quartosReservados, Set<Cliente> visitados) {
        if (nodo == null || visitados.contains(nodo)) {
            return;
        }

        visitados.add(nodo);

        if (nodo.getQuarto() != null) {
            quartosReservados.add(nodo.getQuarto());
        }

        adicionarQuartosReservadosHelper(nodo.getEsq(), quartosReservados, visitados);
        adicionarQuartosReservadosHelper(nodo.getDir(), quartosReservados, visitados);
    }

    public long calcularCancelamentos(LocalDate inicio, LocalDate fim) {
        return contarCancelamentosNoPeriodo(raizHistorico, inicio, fim);
    }

    private long contarCancelamentosNoPeriodo(Cliente nodo, LocalDate inicio, LocalDate fim) {
        if (nodo == null) {
            return 0;
        }

        long cancelamentos = 0;

        if (!nodo.getCheckOut().isBefore(inicio) && !nodo.getCheckIn().isAfter(fim)) {
            cancelamentos++;
        }

        cancelamentos += contarCancelamentosNoPeriodo(nodo.getEsq(), inicio, fim);
        cancelamentos += contarCancelamentosNoPeriodo(nodo.getDir(), inicio, fim);

        return cancelamentos;
    }
}
