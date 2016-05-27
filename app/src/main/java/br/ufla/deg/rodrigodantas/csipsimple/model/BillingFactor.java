package br.ufla.deg.rodrigodantas.csipsimple.model;

/**
 * This enum is part of MS Degree work of Rodrigo Dantas Nunes
 * Federal University of Lavras - MG - Brazil
 *
 * @author Rodrigo Dantas Nunes - rdantasnunes(at)posgrad(dot)ufla(dot)br
 * Created by Rodrigo on 27/5/16.
 *
 */
public enum BillingFactor {
    /*
      Q1 From 4.1 to 4.5
      Q2 From 3.6 to 4.0
      Q3 From 3.1 to 3.5
      Q4 From 2.1 to 3.0
      Q5 From 1.0 to 2.0
     */
    Q1(1.0f),Q2(0.9f),Q3(0.6f),Q4(0.1f),Q5(0f);

    BillingFactor(float perc){
        this.percentualCobranca = perc;
    }

    private float percentualCobranca;

    public float getPercentualCobranca(){
        return this.percentualCobranca;
    }

    public static BillingFactor getPercentualCobranca(String mos){
        try{
            float mosF = Float.parseFloat(mos);
            if (mosF <= 2f){
                return Q5;
            }else if (mosF <= 3f){
                return Q4;
            }else if (mosF <= 3.5f){
                return Q3;
            }else if (mosF <= 4f){
                return Q2;
            }else {
                return Q1;
            }
        }catch (NumberFormatException n){
            return Q1;
        }
    }
}
