package com.example.musicbuddy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.musicbuddy.ui.components.TunerLogic
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

@Preview
@Composable
fun TunerScreen(tunerLogic: TunerLogic = viewModel()) {
    /*var frequency by remember { mutableFloatStateOf(0f) }
    var decibel by remember { mutableFloatStateOf(0f) }
    var noteName by remember { mutableStateOf("--") }*/

    //CHIEDO PERMESSI PER USARE IL MICROFONO
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher per gestire la risposta dell'utente
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // Chiede il permesso appena lo screen viene caricato
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (hasPermission) {
        // Gestione ciclo di vita: avvia all'ingresso, stoppa all'uscita
        DisposableEffect(Unit) {
            tunerLogic.startListening()
            onDispose {
                tunerLogic.stopListening()
            }
        }

        var noteName by remember { mutableStateOf("--") }
        val notesMatrix = tunerLogic.createNotesMatrix()
        //noteName = if (tunerLogic.pitch.floatValue > 0) getNoteName(tunerLogic.pitch.floatValue) else "--"

        /*
        // Avvia il rilevamento all'avvio dello screen
        LaunchedEffect(Unit) {
            TunerLogic.startPitchDetection { pitch, intensity ->
                frequency = pitch
                decibel = intensity
                noteName = if (pitch > 0) TunerLogic.getNoteName(pitch) else "--"
            }
        }
        */

        // 1. Stato che controlla l'angolo obiettivo (inizialmente a 0)
        var targetAngle by remember { mutableFloatStateOf(0f) }
        var handState by remember { mutableIntStateOf(0) }  //0 means note is under tuned, 1 it is in tune, 2 it is over tuned
        var isVisible by remember { mutableStateOf(false) }

        isVisible = if (tunerLogic.decibels.floatValue < -50f) false else true

        val closestNote = getClosestNotePitch(tunerLogic.pitch.floatValue, notesMatrix)
        //We consider the note out of tune if it is above or below 0,7% of the closest note
        if (tunerLogic.pitch.floatValue < (closestNote - 0.007 * closestNote)) {    //Under tuned
            if (handState == 0) {}//targetAngle += 0f
            else if (handState == 1) targetAngle -= 55f
            else targetAngle -= 100f
            handState = 0
        } else if (tunerLogic.pitch.floatValue > (closestNote + 0.007 * closestNote)) {      //Over tuned
            if (handState == 0) targetAngle += 100f
            else if (handState == 1) targetAngle += 55f
            else {}//targetAngle += 0f
            handState = 2
        } else {              //In tune
            if (handState == 0) targetAngle += 55f
            else if (handState == 1) targetAngle = 0f
            else targetAngle -= 55f
            handState = 1
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Perceived note", fontSize = 30.sp, color = darkText)
            Text(
                text = if (tunerLogic.decibels.floatValue >= -50f) getNoteName(tunerLogic.pitch.floatValue) else "--",
                fontSize = 64.sp,
                style = MaterialTheme.typography.headlineLarge,
                color = darkText
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (tunerLogic.decibels.floatValue < -50f) "Sound too low" else "",
                fontSize = 20.sp,
                color = darkText
            )

            Spacer(modifier = Modifier.height(16.dp))

            //TUNER HAND SECTION
            // L'animazione osserva 'targetAngle'. Se cambia, parte la transizione.
            val animatedAngle by animateFloatAsState(
                targetValue = targetAngle,
                animationSpec = tween(durationMillis = 100, easing = LinearEasing),
                label = "HandAnimation"
            )
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(10)
            )


            Canvas(modifier = Modifier.size(200.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                rotate(degrees = animatedAngle, pivot = center) {
                    drawLine(
                        color = green,
                        start = center,
                        end = Offset(center.x, center.y - 80.dp.toPx()),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round,
                        alpha = animatedAlpha
                    )
                }
                drawCircle(
                    color = green,
                    radius = 10.dp.toPx(),
                    center = center,
                    alpha = animatedAlpha
                )
            }


            Spacer(modifier = Modifier.height(24.dp))
            //COLORED LIGHT SECTION
            // Cambia colore rapidamente quando la condizione muta
            val circleColor by animateColorAsState(
                targetValue = if (handState == 1) Color(0xFF4CBB17) else Color.Red,
                animationSpec = tween(10),
                label = "ColorAnimation"
            )

            /*Box(
            modifier = Modifier
                .size(16.dp)
                .background(circleColor, shape = CircleShape)
        )*/
            //Spia simile al vetro
            Box(
                modifier = Modifier
                    .size(24.dp)
                    // Bordo metallico/plastico esterno
                    .border(2.dp, Color.DarkGray, CircleShape)
                    .padding(2.dp)
                    // Sfondo scuro per simulare la cavità
                    .background(Color(0xFF1A1A1A), CircleShape)
                    .drawWithCache {
                        onDrawWithContent {
                            // 1. Il corpo illuminato (con gradiente radiale per il volume)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(circleColor, circleColor.copy(alpha = 0.6f)),
                                    center = center,
                                    radius = size.minDimension / 2
                                )
                            )

                            // 2. Riflesso del vetro (la "macchia" bianca in alto)
                            drawOval(
                                color = Color.White.copy(alpha = 0.4f),
                                topLeft = Offset(size.width * 0.25f, size.height * 0.1f),
                                size = Size(size.width * 0.5f, size.height * 0.3f)
                            )
                        }
                    }
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = if (tunerLogic.pitch.floatValue > 20f && tunerLogic.decibels.floatValue >= -50f) "%.2f Hz".format(tunerLogic.pitch.floatValue) else "Listening...",
                fontSize = 20.sp,
                color = darkText
            )
        }
    }
    else {
        Button(onClick = { launcher.launch(Manifest.permission.RECORD_AUDIO) }) {
            Text("Give permission to use the microphone?")
        }
    }
}

private fun getNoteName(frequency: Float): String {
    if (frequency > 0f) {
        val notes =
            arrayOf("C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B")
        val n =
            (12 * log2(frequency / 440.0) + 69).roundToInt()    //formula per ottenere nome della nota più vicina dal pitch (arrotondato)
        val noteIndex = n % 12
        return notes[if (noteIndex < 0) noteIndex + 12 else noteIndex]
    }
    else return "--"
}

private fun getClosestNotePitch(currentPitch: Float, notesMatrix: Array<Array<Float>>): Float {
    var minDifference: Float = 10000f   //value greater than every matrix element
    var targetValue: Float = 0f
    //var targetIndexI: Int = 0
    //var targetIndexJ: Int = 0

    for (i in notesMatrix.indices) {
        for (j in notesMatrix[i].indices) {
            var currDifference = abs(currentPitch - notesMatrix[i][j])
            if (currDifference <= minDifference) {
                minDifference = currDifference
                targetValue = notesMatrix[i][j]
                //targetIndexI = i
                //targetIndexJ = j
            }
        }
    }

    return targetValue
}