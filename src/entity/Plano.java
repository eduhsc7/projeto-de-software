package entity;

public class Plano {
    private int id;
    private String nome;
    private double valorMensal;

    public Plano(int id, String nome, double valorMensal) {
        this.id = id;
        this.nome = nome;
        this.valorMensal = valorMensal;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getValorMensal() {
        return valorMensal;
    }
}
