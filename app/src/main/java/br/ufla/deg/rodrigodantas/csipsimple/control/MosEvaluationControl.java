package br.ufla.deg.rodrigodantas.csipsimple.control;

import android.content.Context;

import com.csipsimple.utils.Log;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.SimpleFormatter;

import br.ufla.deg.rodrigodantas.csipsimple.model.MosEvaluation;
import br.ufla.deg.rodrigodantas.csipsimple.p563.P563Executer;
import br.ufla.deg.rodrigodantas.csipsimple.util.Utils;
import br.ufla.deg.rodrigodantas.csipsimple.vad.VadUtils;

/**
 * Imutable class to process the audio sample and to calculate the MOS and suggested fair price.
 *
 * This class is part of MS Degree work of Rodrigo Dantas Nunes
 * Federal University of Lavras - MG - Brazil
 *
 * @author Rodrigo Dantas Nunes - rdantasnunes(at)posgrad(dot)ufla(dot)br
 * Created by Rodrigo on 27/5/16.
 */
public class MosEvaluationControl {

    private float packetLossRate;

    private File sample;

    private MosEvaluation mosEvaluation;

    private Context context;

    private NumberFormat f = NumberFormat.getInstance(new Locale("en","US"));

    public MosEvaluationControl(File sample, float packetLossRate, Context context){
        super();
        this.sample = sample;
        this.packetLossRate = packetLossRate;
        this.context = context;
        f.setMaximumFractionDigits(6);
    }

    public MosEvaluationControl(File sample, float packetLossRate){
        super();
        this.sample = sample;
        this.packetLossRate = packetLossRate;
        //this.context = context;
        f.setMaximumFractionDigits(6);
    }

    private String calculateP563(){
        try{
            P563Executer p563Executer = new P563Executer();
            String resultado = p563Executer.execute(this.sample.getAbsolutePath()).get();
            return resultado;
        }catch (ExecutionException x){
            Log.e(this.getClass().getSimpleName(),"File Path "+sample.getName()+" Exc. Message: "+x.getMessage(),x);
            this.mosEvaluation = null;
            return "An Execution Exception occurred, and was not possible compute the MOS of audio sample." +
                    "\nMessage: "+x.getMessage();
        }catch (InterruptedException e){
            Log.e(this.getClass().getSimpleName(),"File Path "+sample.getName()+" Exc. Message: "+e.getMessage(),e);
            this.mosEvaluation = null;
            return "An Interrupted Exception occurred, and was not possible compute the MOS of audio sample." +
                    "\nMessage: "+e.getMessage();
        }
    }

    private void executeVAD(){
        this.sample = VadUtils.vad(this.sample.getAbsolutePath(), getPacketLossRate());
    }

    private float getPacketLossRate(){
        return this.packetLossRate;
    }

    /**
     * Adjustment function implemantation, where n is the packet loss rate in the network.
     * f(n) = alpha*nˆ3 + beta*n^2 + gama*n + D
     *Valores de n abaixo de aprox 1.41742 resultam em um valor positivo aumentando assim, o MOS resultante da funcao de n
     * @param mos obtained from audio sample
     * @return MOS ajusted by f(n) above.
     */
    private float executeAdjustmentFunction(float mos){

        double n = getPacketLossRate(); //n is packet loss rate;
        if(n <= 1.5d)//se a taxa de perda for menor ou igual 1.5, então usa o MOS original
            return mos;

        double alpha = -0.00002;
        double beta = 0.001;
        double gama = -0.043;
        double D = 1.059;
        double f_n = alpha * Math.pow(n,3d) + beta * Math.pow(n,2d) + gama * n + D;
        float mosAjustado = new Double(mos*f_n).floatValue();

        /*System.out.println("Metodo executeAdjustmentFunction MOS: "+f.format(mos));
        System.out.println("Metodo executeAdjustmentFunction n: "+f.format(n));
        System.out.println("Metodo executeAdjustmentFunction f(n): "+f.format(f_n));
        System.out.println("Metodo executeAdjustmentFunction MOS ajustado: "+f.format(mosAjustado));*/

        return mosAjustado;
    }

    public void calculate() throws Exception {

        executeVAD();

        String mos = calculateP563();
        //System.out.println("MOS do P563 "+mos);

        Float mosF = Utils.parseToFloat(mos);
        if(mosF == -1){
            throw new Exception(mos);
        }

        mosF = executeAdjustmentFunction(mosF);
        ///System.out.println("MOS do f(n) "+mosF);

        this.mosEvaluation = new MosEvaluation(this.sample.getAbsolutePath(),
                f.format(mosF.doubleValue()),new Date(),
                VadUtils.durationOfAudioSample(this.sample));
        this.mosEvaluation.setPlr(this.packetLossRate);

    }

    public MosEvaluation getMosEvaluation(){
        return this.mosEvaluation;
    }
}
