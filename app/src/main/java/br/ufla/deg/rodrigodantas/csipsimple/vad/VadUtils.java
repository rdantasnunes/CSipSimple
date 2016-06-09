package br.ufla.deg.rodrigodantas.csipsimple.vad;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rodrigo on 5/27/16.
 */
public class VadUtils {

    public static byte[] converteWavFile2ByteArray(String nomeArquivo){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(nomeArquivo)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read;
        byte[] buff = new byte[1024];
        try {
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public static int durationOfAudioSample(File file, Context context){
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(file.getAbsolutePath()));
        return mp.getDuration()/1000;
    }

    public static File write(String outputPath, byte[] audio) throws Exception {

        File f = new File(outputPath);
        // Write to File
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(f));

        dataOutputStream.write(audio);

        dataOutputStream.close();
        return f;
    }

    public static File vad(String nomeArquivoEntrada) {
        String now = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return retirarSilencios(nomeArquivoEntrada,"newAudioFileWithoutSilences_"+now+"_.wav");
    }

    public static File retirarSilencios(String nomeArquivoEntrada, String nomeArquivoSaida) {
        AutocorrellatedVoiceActivityDetector vad = new AutocorrellatedVoiceActivityDetector();
        File f = new File(nomeArquivoEntrada);

        WavFile wavFile = null;
        try {
            wavFile = WavFile.openWavFile(f); // Open the wav file specified as the first argument

            int numChannels = wavFile.getNumChannels(); // Get the number of audio channels in the wav file
            // Create a buffer of 100 frames
            double[] bufferAux = new double[(int) wavFile.getNumFrames() * numChannels];
            double[] buffer = new double[(int) wavFile.getNumFrames() * numChannels];

            long sampleRate = wavFile.getSampleRate();
            int k = 0;
            int framesRead, lim, m;
            long remaining;

            do {//vai ler de 1000 em 1000 frames
                m = 0;
                remaining = wavFile.getFramesRemaining();
                lim = (remaining > 1000) ? 1000 : (int) remaining;
                // Read frames into bufferAux
                framesRead = wavFile.readFrames(bufferAux, lim);
                while (m < lim) { //vai copiar os bytes lidos acima e salvar no buffer definitivo
                    buffer[k] = bufferAux[m];
                    m += 1;
                    k += 1;
                }
            } while (framesRead != 0);

            double[] signalWithoutSilence = vad.removeSilence(buffer, sampleRate);
            wavFile.close(); // Close the wavFile

            File outputFile = new File(nomeArquivoSaida);
            wavFile = WavFile.newWavFile(outputFile, numChannels, signalWithoutSilence.length, 16, sampleRate);
            wavFile.writeFrames(signalWithoutSilence, signalWithoutSilence.length); // Write the buffer
            wavFile.close(); // Close the wavFile

            return outputFile;

        } catch (IOException | WavFileException ex) {
            //Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return f;
        } finally{
            try {
                wavFile.close();
            } catch (NullPointerException | IOException ex) {
                //ignorar este erro
            }
        }
    }
}
