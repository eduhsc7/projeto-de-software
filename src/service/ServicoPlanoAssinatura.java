package service;

import entity.Plano;
import java.util.ArrayList;
import java.util.List;

public class ServicoPlanoAssinatura {
    public List<Plano> obterPlanos() {
        List<Plano> planos = new ArrayList<>();
        planos.add(new Plano(1, "Plano Básico", 49.90));
        planos.add(new Plano(2, "Plano Família", 89.90));
        planos.add(new Plano(3, "Plano Premium", 129.90));
        return planos;
    }
}
