package br.ufla.deg.rodrigodantas.csipsimple.control;

import com.csipsimple.utils.Log;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

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

    public MosEvaluationControl(File sample, float packetLossRate){
        super();
        this.sample = sample;
        this.packetLossRate = packetLossRate;
    }

    private String calculateP563(){
        try{
            P563Executer p563Executer = new P563Executer();
            String resultado = p563Executer.execute(this.sample.getAbsolutePath()).get();
            //TODO: remove line below after development was concluded
            Log.d(this.getClass().getSimpleName(),"NOME DO ARQUIVO "+sample.getName()+"\nResultado MOS:"+resultado);
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
        this.sample = VadUtils.vad(this.sample.getAbsolutePath());
    }

    private float getPacketLossRate(){
        //TODO: Implementar
        return 0f;
    }

    /**
     * Adjustment function implemantation, where n is the packet loss rate in the network.
     * f(n) = alpha*nˆ3 + beta*n^2 + gama*n + D
     *
     * @param mos obtained from audio sample
     * @return MOS ajusted by f(n) above.
     */
    private float executeAdjustmentFunction(float mos){

        double n = getPacketLossRate(); //n is packet loss rate;
        double alpha = -0.00002;
        double beta = 0.001;
        double gama = -0.043;
        double D = 1.059;
        double f_n = alpha*Math.pow(n,3d) + beta*Math.pow(n,2d) + gama*n + D;
        float mosAjustado = new Double(mos*f_n).floatValue();

        Log.d("Metodo executeAdjustmentFunction",String.format("MOS: %06d",mos));
        Log.d("Metodo executeAdjustmentFunction",String.format("f(n): %06d",f_n));
        Log.d("Metodo executeAdjustmentFunction",String.format("MOS ajustado: %06d",mosAjustado));

        return mosAjustado;
    }

    public void calculate() throws Exception {

        executeVAD();

        String mos = calculateP563();
        Float mosF = Utils.parseToFloat(mos);
        if(mosF == -1){
            throw new Exception(mos);
        }

        mosF = executeAdjustmentFunction(mosF);

        this.mosEvaluation = new MosEvaluation(this.sample.getAbsolutePath(),String.format("%06d",mosF),new Date(),
                VadUtils.durationOfAudioSample(this.sample));
    }

    public MosEvaluation getMosEvaluation(){
        return this.mosEvaluation;
    }
}
