package reservas;

import enums.Categoria;
import nodo.Cliente;

import java.util.ArrayList;
import java.util.List;

public class Quarto {
    private int numQuarto;
    private Categoria categoria;
    private List<Cliente> reservas;

    public Quarto(int numQuarto, Categoria categoria) {
        this.numQuarto = numQuarto;
        this.categoria = categoria;
        this.reservas = new ArrayList<>();
    }

    public int getNumQuarto() {
        return numQuarto;
    }

    public void setNumQuarto(int numQuarto) {
        this.numQuarto = numQuarto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Cliente> getReservas() {
        return reservas;
    }

    public void adicionarReserva(Cliente cliente) {
        this.reservas.add(cliente);
    }
}
