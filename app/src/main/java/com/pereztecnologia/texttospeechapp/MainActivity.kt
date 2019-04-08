package com.pereztecnologia.texttospeechapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

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
            for(s in 0..jsonarr.length()-1){
                var jsonobj = jsonarr.getJSONObject(s)
                word=jsonobj.getString("word")
                positive=jsonobj.getInt("positive")
                negative=jsonobj.getInt("negative")
                //Log.d("APPDEBUG","Word:"+word.toLowerCase()+" Search:"+search.toLowerCase())
                if(word.toLowerCase()==search.toLowerCase()){
                    if (positive==1){
                        globalPositive++
                    }else if (negative==1){
                        globalNegative++
                    }
                }
            }
            Log.d("APPDEBUG","Search:"+search.toLowerCase())
            Log.d("APPDEBUG","Negative:"+globalNegative.toString())
            Log.d("APPDEBUG","Positive:"+globalPositive.toString())
            Log.d("APPDEBUG","=====================================")

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
        var json:String?=null
        val wbyw:List<String>
        val text = editText!!.text.toString()
        if(text==""){
            tts!!.speak("Please insert a phrase in spanish", TextToSpeech.QUEUE_FLUSH, null,"")
            return
        }
        var salida:String=""
        wbyw=text.split(" ")
        globalPositive=0
        globalNegative=0
        for(i in 0..wbyw.size-1){
            readJson(wbyw[i])
        }

        try {
            val inputStream:InputStream=assets.open("food.json")
            json =inputStream.bufferedReader().use{it.readText()}
            var jsonarr=JSONArray(json)
            if(globalPositive>globalNegative){
                salida="your sentiments are positive. I suggest you to eat "+jsonarr.getJSONArray(0).get((0..jsonarr.getJSONArray(0).length()-1).random()).toString()
            }else if(globalNegative>globalPositive){
                salida="your sentiments are negative I suggest you to eat "+jsonarr.getJSONArray(1).get((0..jsonarr.getJSONArray(1).length()-1).random()).toString()
            }else{
                salida="your sentiments are neutral I suggest you to eat whatever you want"
            }
        }catch (e:IOException){

        }



        Toast.makeText(this,salida,Toast.LENGTH_LONG)
        tts!!.speak(salida, TextToSpeech.QUEUE_FLUSH, null,"")
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