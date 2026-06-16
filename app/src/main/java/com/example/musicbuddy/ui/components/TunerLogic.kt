package com.example.musicbuddy.ui.components

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.pitch.PitchDetectionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.math.sqrt

//class TunerLogic (private val onUpdate: (pitch: Float, decibels: Double) -> Unit) {
class TunerLogic : ViewModel() {
    /**
     * Avvia il thread di TarsosDSP per il pitch detection
     */
    /*suspend fun startPitchDetection(onPitchDetected: (Float, Float) -> Unit) {
        withContext(Dispatchers.IO) {
            val sampleRate = 44100
            val bufferSize = 512
            val overlap = 0

            val dispatcher: AudioDispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap)

            val pdh = PitchDetectionHandler { result, audioEvent ->
                val pitch =
                    result.pitch // Frequenza in Hertz (-1 se non viene rilevato un tono chiaro)
                val dbSPL = audioEvent.getdBSPL() // Intensità in Decibel
                if (pitch != -1f && dbSPL <= 0f) {
                    // Ritorna al thread principale per aggiornare la UI di Compose
                    onPitchDetected(pitch, dbSPL.toFloat())
                }
            }

            /*val pdh = PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                sampleRate.toFloat(),
                bufferSize
            ) { result, _ ->
                val pitch = result.pitch
                if (pitch != -1f) {
                    // Ritorna al thread principale per aggiornare la UI di Compose
                    onPitchDetected(pitch)
                }
            }*/

            val pitchProcessor = PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                sampleRate.toFloat(),
                bufferSize,
                pdh
            )

            dispatcher.addAudioProcessor(pitchProcessor)
            dispatcher.run() // Esegue il loop di ascolto
        }
    }*/

    //VERSIONE ALTERNATIVA
    private var dispatcher: AudioDispatcher? = null

    var pitch = mutableFloatStateOf(0f)
    var decibels = mutableFloatStateOf(-160f) // Silenzio di default

    fun startListening() {
        val sampleRate = 44100
        val bufferSize = 4096
        val overlap = 0

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap)

        // 2. Handler per il Pitch
        val pdh = PitchDetectionHandler { result, _ ->
            val pitchInHz = result.pitch
            // Aggiorna lo stato sul thread principale per Compose
            pitch.floatValue = pitchInHz
        }

        // 3. Processore per il Pitch
        val pitchProcessor = PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            sampleRate.toFloat(),
            bufferSize,
            pdh
        )
        dispatcher?.addAudioProcessor(pitchProcessor)

        // 4. Processore per i Decibel (intensità)
        dispatcher?.addAudioProcessor(object : AudioProcessor {
            override fun process(audioEvent: AudioEvent): Boolean {
                val rms = audioEvent.rms
                /*val buffer = audioEvent.floatBuffer
                var sum = 0f
                for (sample in buffer) {
                    sum += sample * sample
                }
                val rms = sqrt((sum / buffer.size).toDouble())*/
                val db = 20 * log10(rms)

                // Aggiorna lo stato dei decibel
                decibels.floatValue = db.toFloat()
                return true
            }

            override fun processingFinished() {}
        })

        // Avvia il thread audio
        Thread(dispatcher, "Audio Thread").start()
    }

    fun stopListening() {
        dispatcher?.stop()
    }

    //Matrix to map sound pitches to musical notes. The rows are the notes, the columns are the octaves
    //8 octaves are included in the analysis
    fun createNotesMatrix(): Array<Array<Float>> {
        val notesMatrix = Array(12) { Array<Float>(8) { 0f } }
        //C
        notesMatrix[0][0] = 32.70f
        notesMatrix[0][1] = 65.41f
        notesMatrix[0][2] = 130.81f
        notesMatrix[0][3] = 261.63f
        notesMatrix[0][4] = 523.25f
        notesMatrix[0][5] = 1046.50f
        notesMatrix[0][6] = 2093.0f
        notesMatrix[0][7] = 4186.01f
        //C#/Db
        notesMatrix[1][0] = 34.65f
        notesMatrix[1][1] = 69.30f
        notesMatrix[1][2] = 138.59f
        notesMatrix[1][3] = 277.18f
        notesMatrix[1][4] = 554.37f
        notesMatrix[1][5] = 1108.73f
        notesMatrix[1][6] = 2217.46f
        notesMatrix[1][7] = 4434.92f
        //D
        notesMatrix[2][0] = 36.71f
        notesMatrix[2][1] = 73.42f
        notesMatrix[2][2] = 146.83f
        notesMatrix[2][3] = 293.66f
        notesMatrix[2][4] = 587.33f
        notesMatrix[2][5] = 1174.66f
        notesMatrix[2][6] = 2349.32f
        notesMatrix[2][7] = 4698.63f
        //D#/Eb
        notesMatrix[3][0] = 38.89f
        notesMatrix[3][1] = 77.78f
        notesMatrix[3][2] = 155.56f
        notesMatrix[3][3] = 311.13f
        notesMatrix[3][4] = 622.25f
        notesMatrix[3][5] = 1244.51f
        notesMatrix[3][6] = 2489.02f
        notesMatrix[3][7] = 4978.03f
        //E
        notesMatrix[4][0] = 41.20f
        notesMatrix[4][1] = 82.41f
        notesMatrix[4][2] = 164.81f
        notesMatrix[4][3] = 329.63f
        notesMatrix[4][4] = 659.25f
        notesMatrix[4][5] = 1318.51f
        notesMatrix[4][6] = 2637.02f
        notesMatrix[4][7] = 5274.04f
        //F
        notesMatrix[5][0] = 43.65f
        notesMatrix[5][1] = 87.31f
        notesMatrix[5][2] = 174.61f
        notesMatrix[5][3] = 349.23f
        notesMatrix[5][4] = 698.46f
        notesMatrix[5][5] = 1396.91f
        notesMatrix[5][6] = 2793.83f
        notesMatrix[5][7] = 5587.65f
        //F#/Gb
        notesMatrix[6][0] = 46.25f
        notesMatrix[6][1] = 92.50f
        notesMatrix[6][2] = 185.0f
        notesMatrix[6][3] = 369.99f
        notesMatrix[6][4] = 739.99f
        notesMatrix[6][5] = 1479.98f
        notesMatrix[6][6] = 2959.96f
        notesMatrix[6][7] = 5919.91f
        //G
        notesMatrix[7][0] = 49.0f
        notesMatrix[7][1] = 98.0f
        notesMatrix[7][2] = 196.0f
        notesMatrix[7][3] = 392.0f
        notesMatrix[7][4] = 783.99f
        notesMatrix[7][5] = 1567.98f
        notesMatrix[7][6] = 3135.96f
        notesMatrix[7][7] = 6271.93f
        //G#/Ab
        notesMatrix[8][0] = 51.91f
        notesMatrix[8][1] = 103.83f
        notesMatrix[8][2] = 207.65f
        notesMatrix[8][3] = 415.30f
        notesMatrix[8][4] = 830.61f
        notesMatrix[8][5] = 1661.22f
        notesMatrix[8][6] = 3322.44f
        notesMatrix[8][7] = 6644.88f
        //A
        notesMatrix[9][0] = 55f
        notesMatrix[9][1] = 110f
        notesMatrix[9][2] = 220f
        notesMatrix[9][3] = 440f
        notesMatrix[9][4] = 880f
        notesMatrix[9][5] = 1760f
        notesMatrix[9][6] = 3520f
        notesMatrix[9][7] = 7040f
        //A#/Bb
        notesMatrix[10][0] = 58.27f
        notesMatrix[10][1] = 116.54f
        notesMatrix[10][2] = 233.08f
        notesMatrix[10][3] = 466.16f
        notesMatrix[10][4] = 932.33f
        notesMatrix[10][5] = 1864.66f
        notesMatrix[10][6] = 3729.31f
        notesMatrix[10][7] = 7458.62f
        //B
        notesMatrix[11][0] = 61.74f
        notesMatrix[11][1] = 123.47f
        notesMatrix[11][2] = 246.94f
        notesMatrix[11][3] = 493.88f
        notesMatrix[11][4] = 987.77f
        notesMatrix[11][5] = 1975.53f
        notesMatrix[11][6] = 3951.07f
        notesMatrix[11][7] = 7902.13f

        return notesMatrix
    }

    /**
     * Converte la frequenza in Hz nel nome della nota (C, C#, D...)
     * Basato sulla formula: n = 12 * log2(f / 440) + 69
     */
}