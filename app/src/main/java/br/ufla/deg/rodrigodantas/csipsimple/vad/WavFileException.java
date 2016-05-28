package br.ufla.deg.rodrigodantas.csipsimple.vad;

/**
 * This class is part of MS Degree work of Rodrigo Dantas Nunes
 * Federal University of Lavras - MG - Brazil
 *
 * Wav file Exception class
 *  @author A.Greensted
 *  http://www.labbookpages.co.uk
 *  File format is based on the information from
 *  http://www.sonicspot.com/guide/wavefiles.html
 *  http://www.blitter.com/~russtopia/MIDI/~jglatt/tech/wave.htm
 *  Version 1.0
 *
 * Rodrigo Dantas Nunes - rdantasnunes(at)posgrad(dot)ufla(dot)br
 * Created by Rodrigo on 27/5/16.
 */
public class WavFileException extends Exception {

    private static final long serialVersionUID = 1L;

    public WavFileException() {
        super();
    }

    public WavFileException(String message) {
        super(message);
    }

    public WavFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavFileException(Throwable cause) {
        super(cause);
    }
}
