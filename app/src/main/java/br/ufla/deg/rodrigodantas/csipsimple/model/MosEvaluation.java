package br.ufla.deg.rodrigodantas.csipsimple.model;

import java.util.Date;

/**
 * Imutable class to store data from file being analized and to calculate the suggested fair price.
 *
 * This class is part of MS Degree work of Rodrigo Dantas Nunes
 * Federal University of Lavras - MG - Brazil
 *
 * @author Rodrigo Dantas Nunes - rdantasnunes(at)posgrad(dot)ufla(dot)br
 * Created by Rodrigo Dantas Nunes on 27/5/16.
 */
public class MosEvaluation {
    //Preço por minuto da ligação em reais. TODO: Criar uma configuração para atribuir este valor
    private final float pricePerMinute = 0.6f;

    //String contendo nome e endereço completo do arquivo avaliado
    private String file;

    //indice MOS atribuido
    private String mos;

    //Armazena a data e a hora que a ligação terminou
    private Date dateHourEndedCall;

    //Duracao chamada em segundos
    private int durationInSeconds;

    //Preço justo sugerido com base no MOS e no Valor por minuto
    private Float fairPriceSuggested;

    public MosEvaluation(String file, String mos, Date dateHourEndedCall, int durationInSeconds){
        super();
        this.file = file;
        this.mos = mos;
        this.dateHourEndedCall = dateHourEndedCall;
        this.durationInSeconds = durationInSeconds;
        //this.pricePerMinute TODO: Ler da configuração e atribuir aqui
        calculePrecoJusto();
    }

    private void calculePrecoJusto(){
        BillingFactor billingFactor = BillingFactor.getPercentualCobranca(this.mos);
        this.fairPriceSuggested = billingFactor.getPercentualCobranca()*
                (this.pricePerMinute /60*this.durationInSeconds);
    }

    public float getFairPriceSuggested(){
        return this.fairPriceSuggested;
    }

    @Override
    public String toString(){
        return "Arquivo: "+this.file+
                "\nDuracao: "+ durationInSeconds +
                "\nPreçoPorSegundo: R$"+(new Float(pricePerMinute /60)).intValue()+
                "\nMOS: "+this.mos+
                "\nValor Justo Sugerido: "+this.fairPriceSuggested;
    }
}
