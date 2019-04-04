package com.pereztecnologia.texttospeechapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    var globalPositive:Int=0
    var globalNegative:Int=0
    private var tts: TextToSpeech? = null
    private var buttonSpeak: Button? = null
    private var editText: EditText? = null
    fun readJson(search:String){
        var json: String? = null
        var word:String? = null
        var positive:Int=0
        var negative:Int=0
        try {
            val inputStream:InputStream=assets.open("dictionary.json")
            json =inputStream.bufferedReader().use{it.readText()}
            var jsonarr=JSONArray(json)
            for(s in 0..jsonarr.length()){
                var jsonobj = jsonarr.getJSONObject(1)
                word=jsonobj.getString("word")
                positive=jsonobj.getInt("positive")
                negative=jsonobj.getInt("negative")
                if(word.toLowerCase()==search.toLowerCase()){
                    if (positive==1){
                        globalPositive++
                    }else if (negative==1){
                        globalNegative++
                    }
                }
            }
        }catch (e:IOException){

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSpeak = this.button_speak
        editText = this.edittext_input

        buttonSpeak!!.isEnabled = false;
        tts = TextToSpeech(this, this)

        buttonSpeak!!.setOnClickListener { speakOut() }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","El lenguaje que seleccionaste no es valido!")
            } else {
                buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Inicio Fallido!")
        }

    }

    private fun speakOut() {
        val text = editText!!.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }



}