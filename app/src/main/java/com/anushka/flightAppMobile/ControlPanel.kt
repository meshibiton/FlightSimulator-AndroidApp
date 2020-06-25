package com.anushka.flightAppMobile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anushka.flightAppMobile.connect.MainActivity
import com.anushka.flightAppMobile.connect.ServerViewModel
import com.anushka.flightAppMobile.models.Command
import com.anushka.flightAppMobile.services.Api
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_control_panel.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.round


class ControlPanel  : AppCompatActivity(), JoystickView.JoystickListener {

    var urlToConnect = ""
    var shouldStopLoop = false
    //last parms we send to the server
    private var lastJoystickX = 0f
    private var lastJoystickY = 0f
    private var lastSliderX = 0f
    private var lastSliderY = 0f
    private var server111: ServerViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_panel)
        val joystick = JoystickView(this)
        setContentView(R.layout.activity_control_panel)
        val arguments = requireNotNull(intent?.extras){"There should be parameters or your more meaningful message."}
        with(arguments){
            var url = getString("url")
            urlToConnect = url.toString()
        }
//        if(urlToConnect == "null"){
//            var intent = Intent(this, MainActivity::class.java)
//            this.startActivity(intent)
//        }
        seekBarX.setMax(1000);
        seekBarX.progress = 500
        seekBarY.setMax(500);
        seekBarY.progress = 0
        val min = 0
        val max = 100
        val current = 50
        //is we touch slider
        seekBarX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            //slider value changed
            override fun onProgressChanged(seekBarX: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressFloatTemp: Double = progress.toDouble()
                val progressFloat: Double
                progressFloat = if (progressFloatTemp > 500) {
                    ((progressFloatTemp - 500) / 500)
                } else {
                    ((progressFloatTemp - 500) / 500)
                }
                var ySlider  = textViewY.text.toString().toFloat()
                var xJoystick  = textViewXJ.text.toString().toFloat()
                var yJoystick  = textViewYJ.text.toString().toFloat()
                sendIfChangeIsBig(xJoystick, yJoystick , progressFloat.toFloat(), ySlider )
                textViewX.text = progressFloat.toString();
            }

            override fun onStartTrackingTouch(seekBarX: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBarX: SeekBar?) {

            }

        })
        seekBarY.setProgress(max - min);
        seekBarY.setProgress(current - min);
        seekBarY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBarX: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressFloatTemp: Double = progress.toDouble()

                val progressFloat: Double = (progressFloatTemp / 500)
                var xSlider  = textViewX.text.toString().toFloat()
                var xJoystick  = textViewXJ.text.toString().toFloat()
                var yJoystick  = textViewYJ.text.toString().toFloat()
                sendIfChangeIsBig(xJoystick, yJoystick , xSlider , progressFloat.toFloat())
                textViewY.text = progressFloat.toString();
            }

            override fun onStartTrackingTouch(seekBarX: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBarX: SeekBar?) {

            }

        })
//        button_reset.setOnClickListener { v ->
//            seekBarX.progress = 500
//            seekBarY.progress = 0
//            textViewXJ.text = "0.0"
//            textViewYJ.text = "0.0"
//            lastJoystickX = 0f
//            lastJoystickY = 0f
//            lastSliderX = 0f
//            lastSliderY = 0f
//            sendIfChangeIsBig(0f,0f,0f, 0f)
//        }


//        val mHandler = Handler()
//
//        val runnable: Runnable = object : Runnable {
//            override fun run() {
//                getUserData()
//                if (!shouldStopLoop) {
//                    mHandler.postDelayed(this, 4000)
//                }
//            }
//        }
//        mHandler.post(runnable);
//        buttonPost.setOnClickListener {
//            postCommand()
//        }
//        buttonLogin.setOnClickListener{
//            var intent = Intent(this, Activity2::class.java)
//            this.startActivity(intent)
//
//        }

    }

    //when the app is unseeing it should stop request the server
    override fun onStop() {
        shouldStopLoop=true;
        super.onStop()

    }

//    override fun onPause() {
//        shouldStopLoop=true;
//        super.onPause()
//
//    }

    override fun onRestart() {
        //   shouldStopLoop=false;
        super.onRestart()
    }

    //if we get to start we will start sending the server request
    override fun onStart() {
        super.onStart()
        shouldStopLoop=false;
        val mHandler = Handler()
        val context = this
        val runnable: Runnable = object : Runnable {
            override fun run() {
                // getUserData()
                if (!shouldStopLoop) {
                    getUserData()
                    mHandler.postDelayed(this, 4000)
                }
//                else{
//                    Toast.makeText(context, "dont do anyting", Toast.LENGTH_SHORT).show()
//                }
            }
        }
        mHandler.post(runnable);
//        buttonPost.setOnClickListener {
//            postCommand()
//        }
        buttonLogin.setOnClickListener{
            // shouldStopLoop=true;
            var intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)

        }

    }

    fun getUserData() {
        //try tutuorial
        // Create Retrofit Builder

        //---
    //  var x = server111!!.getUrl()
        val logger:HttpLoggingInterceptor=HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        //create Okhttp Client
        val okHttp=OkHttpClient.Builder().callTimeout(10,TimeUnit.SECONDS).addInterceptor(logger)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(urlToConnect)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
            .build()

        //---
        val api = retrofit.create(Api::class.java)
        val context = this
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.code()==200){
                    // finish() // Move back to DestinationListActivity
                    val I = response?.body()?.byteStream()
                    val B = BitmapFactory.decodeStream(I)
                    runOnUiThread { X.setImageBitmap(B) }
                    //Toast.makeText(context, "Successfully Added", Toast.LENGTH_SHORT).show()
                }
                else if(response.code()==500){
                    Toast.makeText(context, "Connection Problem in server/simulator,go back to LOGIN", Toast.LENGTH_SHORT).show()

                }
                else if(response.code()==400){
                    Toast.makeText(context, "ERROR in format,go back to LOGIN", Toast.LENGTH_SHORT).show()
                }
//                else{
//                    Toast.makeText(context, "ERROR in format,go back to LOGIN", Toast.LENGTH_SHORT).show()
//
//                }
//                if (response.isSuccessful) {
//                    // finish() // Move back to DestinationListActivity
//                    val I = response?.body()?.byteStream()
//                    val B = BitmapFactory.decodeStream(I)
//                    runOnUiThread { X.setImageBitmap(B) }
//                    Toast.makeText(context, "Successfully Added", Toast.LENGTH_SHORT).show()
//                } else {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Timeout image! Server not responding!,go back to LOGIN", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun postCommand(xJoystick: Float,yJoystick: Float,xSlider: Float,ySlider: Float): Boolean {
        val context = this
        val newCommand = Command()
        var succed=false
        var check=0;
        //we will take that from the joystick
        newCommand.Aileron = xSlider
        newCommand.Throttle =ySlider
        newCommand.Elevator = yJoystick
        newCommand.Rudder = xJoystick
//        newCommand.Aileron = 0.5
//        newCommand.Throttle = 0.6
//        newCommand.Elevator = 0.2
//        newCommand.Rudder = 0.4
        //create Logger
        val logger:HttpLoggingInterceptor=HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        //create Okhttp Client
        val okHttp=OkHttpClient.Builder().callTimeout(10,TimeUnit.SECONDS).addInterceptor(logger)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(urlToConnect)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.addCommand(newCommand).enqueue(object : Callback<Command> {

            override fun onResponse(call: Call<Command>, response: Response<Command>) {
                if (response.code()==200) {
                    //   finish() // Move back to DestinationListActivity
                    // var newlyCreatedDestination = response.body() // Use it or ignore it
                    //   Toast.makeText(context, "Successfully post!! Added", Toast.LENGTH_SHORT).show()
                    succed=true
                    check=0;

                }
                else if(response.code()==500){
                    Toast.makeText(context, "Connection Problem in server/simulator,go back to LOGIN", Toast.LENGTH_SHORT).show()

                }
                else if(response.code()==400){
                    Toast.makeText(context, "ERROR in format,go back to LOGIN", Toast.LENGTH_SHORT).show()
                }
//                else {
//                    Toast.makeText(context, "ERROR in format ,go back to LOGIN", Toast.LENGTH_SHORT).show()
//                    succed=false
//                    check=1;
//                }
            }

            override fun onFailure(call: Call<Command>, t: Throwable) {
                Toast.makeText(context, "Timeout server! Simulator/server is not responding,go back to LOGIN", Toast.LENGTH_SHORT).show()
                //Toast.makeText(context,t.message , Toast.LENGTH_SHORT).show()
                succed=false
                check=2;
            }
        })
        return succed
    }

    //updat the screen display
    override fun onJoystickMoved(xPercent: Float, yPercent: Float, id: Int) {
        var xSlider  = textViewX.text.toString().toFloat()
        var ySlider  = textViewY.text.toString().toFloat()
        sendIfChangeIsBig(xPercent, yPercent *-1, xSlider , ySlider )

        //update display
        textViewXJ.text = (round(xPercent * 100) / 100).toString()
        if (yPercent != 0f) {
            textViewYJ.text = (round(yPercent * 100 * -1) / 100).toString()
        } else {
            textViewYJ.text = (round(yPercent * 100) / 100).toString()

        }


    }

    override fun getValusX(): String {
        // do what you need to get R out of T

        return textViewXJ.text.toString()
    }

    override fun getValusY(): String {
        // do what you need to get R out of T

        return textViewYJ.text.toString()
    }

    override fun sendIfChangeIsBig(
        xJoystick: Float,
        yJoystick: Float,
        xSlider: Float,
        ySlider: Float
    ) {
        //if it is true call send value to server and we are in seen mode
        if ((checkIfBigChange(
                xJoystick,
                yJoystick,
                xSlider,
                ySlider
            ))
        ) {
            sendValueToServer(
                xJoystick,
                yJoystick,
                xSlider,
                ySlider
            )
        }
    }


    override fun checkIfBigChange(
        xJoystick: Float,
        yJoystick: Float,
        xSlider: Float,
        ySlider: Float
    ): Boolean {
        // all parameter range sre between -1 - 1 then percent is 0.02 and
        // for ySLider is range 0-1 then percent is 0.01
        if (Math.abs(xJoystick - lastJoystickX) > 0.02 ||
            Math.abs(yJoystick - lastJoystickY) > 0.02 || Math.abs(xSlider - lastSliderX) > 0.02
            || Math.abs(ySlider - lastSliderY) > 0.01
        ) {

            return true
        }
        return false
    }


    override fun sendValueToServer(
        xJoystick: Float,
        yJoystick: Float,
        xSlider: Float,
        ySlider: Float
    ) {

        val arrayUpdate: FloatArray
        arrayUpdate = FloatArray(4)
        arrayUpdate[0] = xJoystick
        arrayUpdate[1] = yJoystick
        arrayUpdate[2] = xSlider
        arrayUpdate[3] = ySlider
        //need to send to server and get flag back
        var ifSucced = false;
        ifSucced= postCommand(xJoystick,yJoystick,xSlider,ySlider);
        //send to function
        if(ifSucced){
            lastJoystickX= xJoystick;
            lastJoystickY =yJoystick
            lastSliderX =xSlider
            lastSliderY = ySlider
            Toast.makeText(this, "post been send", Toast.LENGTH_SHORT).show()
        }
    }
    interface urlInterface{

        fun getUrl (): String

    }

}
//}

