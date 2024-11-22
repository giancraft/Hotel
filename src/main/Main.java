package main;

import enums.Categoria;
import reservas.GerenciaReserva;
import reservas.Quarto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Inicializa os quartos disponíveis no sistema
        List<Quarto> quartos = new ArrayList<>();
        quartos.add(new Quarto(101, Categoria.ECONOMICO));
        quartos.add(new Quarto(102, Categoria.ECONOMICO));
        quartos.add(new Quarto(201, Categoria.LUXO));
        quartos.add(new Quarto(202, Categoria.LUXO));
        GerenciaReserva cadastroReserva = new GerenciaReserva(quartos);

        while (true) {
            System.out.println("\n--- Menu de Sistema de Reservas ---");
            System.out.println("1. Cadastrar Nova Reserva");
            System.out.println("2. Cancelar Reserva");
            System.out.println("3. Consultar Disponibilidade de Quartos");
            System.out.println("4. Listar Reservas por Data de Check-in");
            System.out.println("5. Relatórios Gerenciais");
            System.out.println("6. Mostrar Histórico de Cancelamentos");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir o '\n' restante

            switch (opcao) {
                case 1:
                    System.out.print("CPF do cliente: ");
                    int cpf = scanner.nextInt();
                    scanner.nextLine(); // Consumir o '\n'

                    System.out.print("Nome do cliente: ");
                    String nome = scanner.nextLine();

                    System.out.print("Número do quarto: ");
                    int numQuarto = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Data de check-in (dd/MM/yyyy): ");
                    LocalDate checkIn = LocalDate.parse(scanner.nextLine(), formatter);

                    System.out.print("Data de check-out (dd/MM/yyyy): ");
                    LocalDate checkOut = LocalDate.parse(scanner.nextLine(), formatter);

                    Quarto quartoEscolhido = quartos.stream()
                            .filter(q -> q.getNumQuarto() == numQuarto)
                            .findFirst()
                            .orElse(null);

                    if (quartoEscolhido != null) {
                        cadastroReserva.inserir(cpf, quartoEscolhido, nome, checkIn, checkOut);
                    } else {
                        System.out.println("Número do quarto inválido!");
                    }
                    break;

                case 2:
                    System.out.print("CPF do cliente para cancelar a reserva: ");
                    int cpfCancelamento = scanner.nextInt();
                    cadastroReserva.cancelarReserva(cpfCancelamento);
                    break;

                case 3:
                    System.out.print("Categoria do quarto (ECONOMICO/LUXO): ");
                    Categoria categoria = Categoria.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Data de check-in (dd/MM/yyyy): ");
                    LocalDate checkInDisp = LocalDate.parse(scanner.nextLine(), formatter);

                    System.out.print("Data de check-out (dd/MM/yyyy): ");
                    LocalDate checkOutDisp = LocalDate.parse(scanner.nextLine(), formatter);

                    cadastroReserva.exibirQuartosDisponiveis(checkInDisp, checkOutDisp, categoria);
                    break;

                case 4:
                    cadastroReserva.listarReservasPorDataCheckIn();
                    break;

                case 5:
                    System.out.println("\n--- Relatórios Gerenciais ---");
                    System.out.print("1. Taxa de ocupação\n2. Quartos mais e menos reservados\n3. Cancelamentos por período\nEscolha uma opção: ");
                    int relatorioOpcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (relatorioOpcao) {
                        case 1:
                            System.out.print("Data de início (dd/MM/yyyy): ");
                            LocalDate inicioOcupacao = LocalDate.parse(scanner.nextLine(), formatter);

                            System.out.print("Data de fim (dd/MM/yyyy): ");
                            LocalDate fimOcupacao = LocalDate.parse(scanner.nextLine(), formatter);

                            double taxaOcupacao = cadastroReserva.calcularTaxaOcupacao(inicioOcupacao, fimOcupacao);
                            System.out.printf("Taxa de ocupação: %.2f%%\n", taxaOcupacao);
                            break;

                        case 2:
                            cadastroReserva.quartosMaisEMenosReservados();
                            break;

                        case 3:
                            System.out.print("Data de início (dd/MM/yyyy): ");
                            LocalDate inicioCancelamento = LocalDate.parse(scanner.nextLine(), formatter);

                            System.out.print("Data de fim (dd/MM/yyyy): ");
                            LocalDate fimCancelamento = LocalDate.parse(scanner.nextLine(), formatter);

                            long totalCancelamentos = cadastroReserva.calcularCancelamentos(inicioCancelamento, fimCancelamento);
                            System.out.println("Número de cancelamentos: " + totalCancelamentos);
                            break;

                        default:
                            System.out.println("Opção inválida!");
                    }
                    break;

                case 6:
                    cadastroReserva.mostrarHistorico();
                    break;

                case 0:
                    System.out.println("Saindo do sistema...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }
}
