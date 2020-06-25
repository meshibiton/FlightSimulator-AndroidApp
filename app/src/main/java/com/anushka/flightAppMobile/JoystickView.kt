package com.anushka.flightAppMobile


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat


/**
 * Joystick activity
 */
class JoystickView : SurfaceView, SurfaceHolder.Callback, OnTouchListener {
    //private variables
    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var baseRadius2 = 0f
    private var hatRadius = 0f
    private var joystickCallback: JoystickListener? = null

    //The smaller, the more shading will occur
    private val ratio = 5
    private fun setupDimensions() {
        centerX = width / 2.toFloat()
        centerY = height / 2.toFloat()
        baseRadius = Math.min(width, height) / 4.2.toFloat()
        baseRadius2 =
            Math.min(width, height) / 4.2.toFloat() + Math.min(width, height) / 7.toFloat()
        hatRadius = Math.min(width, height) / 7.toFloat()
    }
    //constructors for the activity
    constructor(context: Context?) : super(context) {
        holder.addCallback(this)
        setOnTouchListener(this)
        //set the context
        if (context is JoystickListener) joystickCallback = context
    }

    constructor(
        context: Context?,
        attributes: AttributeSet?,
        style: Int
    ) : super(context, attributes, style) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) joystickCallback = context
    }

    constructor(context: Context?, attributes: AttributeSet?) : super(
        context,
        attributes
    ) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) joystickCallback =
            context as JoystickListener?
    }

    fun drawJoystick(newX: Float, newY: Float) {
        if (holder.surface.isValid) {
            //Stuff to draw
            val myCanvas = this.holder.lockCanvas()
            val colors = Paint()
            myCanvas.drawColor(
                Color.TRANSPARENT,
                PorterDuff.Mode.CLEAR
            )
            // Clear the BG
            myCanvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorAccent1));
            //First determine the sin and cos of the angle that the touched point is at
            // relative to the center of the joystick
            val hypotenuse = Math.sqrt(
                Math.pow(
                    newX - centerX.toDouble(),
                    2.0
                ) + Math.pow(newY - centerY.toDouble(), 2.0)
            ).toFloat()
            //sin = o/h
            val sin = (newY - centerY) / hypotenuse
            //cos = a/h
            val cos = (newX - centerX) / hypotenuse
            //Draw the base first before shading
            colors.setARGB(180, 100, 100, 100)
            myCanvas.drawCircle(centerX, centerY, baseRadius2, colors)
            //Draw the base first before shading
            colors.setARGB(255, 100, 100, 100)
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors)
            DrawBaseCircul(colors, myCanvas, newX, newY, cos, sin, hypotenuse)
//            for (i in 1..(baseRadius / ratio).toInt()) {
//                colors.setARGB(
//                    150 / i,
//                    255,
//                    0,
//                    0
//                ) //Gradually decrease the shade of black drawn to create a nice shading effect
//                myCanvas.drawCircle(
//                    newX - cos * hypotenuse * (ratio / baseRadius) * i,
//                    newY - sin * hypotenuse * (ratio / baseRadius) * i,
//                    i * (hatRadius * ratio / baseRadius),
//                    colors
//                ) //Gradually increase the size of the shading effect
//            }

            //Drawing the joystick hat
            DrawingJoystickHat(colors, myCanvas, newX, newY)
//            for (i in 0..(hatRadius / ratio).toInt()) {
//                colors.setARGB(
//                    255,
//                    (i * (255 * ratio / hatRadius)).toInt(),
//                    (i * (255 * ratio / hatRadius)).toInt(),
//                    255
//                ) //Change the joystick color for shading purposes
//                myCanvas.drawCircle(
//                    newX,
//                    newY,
//                    hatRadius - i.toFloat() * ratio / 2,
//                    colors
//                ) //Draw the shading for the hat
//            }
            holder.unlockCanvasAndPost(myCanvas) //Write the new drawing to the SurfaceView
        }
    }
    private fun DrawBaseCircul(colors: Paint, myCanvas: Canvas, newX: Float, newY: Float,
        cos: Float, sin: Float, hypotenuse: Float) {
        for (i in 1..(baseRadius / ratio).toInt()) {
            colors.setARGB(
                150 / i,
                255,
                0,
                0
            ) //Gradually decrease the shade of black drawn to create a nice shading effect
            myCanvas.drawCircle(
                newX - cos * hypotenuse * (ratio / baseRadius) * i,
                newY - sin * hypotenuse * (ratio / baseRadius) * i,
                i * (hatRadius * ratio / baseRadius),
                colors
            ) //Gradually increase the size of the shading effect
        }
    }
    private fun DrawingJoystickHat(
        colors: Paint, myCanvas: Canvas, newX: Float, newY: Float) {
        for (i in 0..(hatRadius / ratio).toInt()) {
            colors.setARGB(
                255,
                (i * (255 * ratio / hatRadius)).toInt(),
                (i * (255 * ratio / hatRadius)).toInt(),
                255
            ) //Change the joystick color for shading purposes
            myCanvas.drawCircle(
                newX,
                newY,
                hatRadius - i.toFloat() * ratio / 2,
                colors
            ) //Draw the shading for the hat
        }
    }
    //override the surface - create
    override fun surfaceCreated(holder: SurfaceHolder) {
        setupDimensions()
        drawJoystick(centerX, centerY)
    }
    //override - changed the surface
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (v == this) {
            if (e.action != MotionEvent.ACTION_UP) {
                ifMotionEvent(e)
//                val displacement = Math.sqrt(
//                    Math.pow(
//                        e.x - centerX.toDouble(),
//                        2.0
//                    ) + Math.pow(e.y - centerY.toDouble(), 2.0)
//                ).toFloat()
//                if (displacement < baseRadius) {
//                    drawJoystick(e.x, e.y)
//                    joystickCallback!!.onJoystickMoved(
//                        (e.x - centerX) / baseRadius,
//                        (e.y - centerY) / baseRadius,
//                        id
//                    )
//
//                } else {
//                    val ratio = baseRadius / displacement
//                    val constrainedX = centerX + (e.x - centerX) * ratio
//                    val constrainedY = centerY + (e.y - centerY) * ratio
//                    drawJoystick(constrainedX, constrainedY)
//                    joystickCallback!!.onJoystickMoved(
//                        (constrainedX - centerX) / baseRadius,
//                        (constrainedY - centerY) / baseRadius,
//                        id
//                    )
//
//                }
            } else {
                //animation joystick  part

                var oldX: Float = joystickCallback!!.getValusX().toFloat()
                var oldY: Float = joystickCallback!!.getValusY().toFloat() * -1
                //check where the hat joystick placed
                if (oldX > oldY*-1 && oldX> 0&& oldY*-1 > 0 ) {
                    val x: Float = oldY / oldX
                    var degree = Math.atan(x.toDouble())

                    while (oldX > 0.01) {
                        oldX -= 0.1.toFloat()
                        oldY = (oldX * (Math.tan(degree))).toFloat()
                        tryLoop(oldX, oldY )
                    }

                } else if (oldX < oldY*-1 && oldX>0 && oldY*-1 > 0 ) {
                    val x: Float = oldY / oldX
                    var degree = Math.atan(x.toDouble())

                    while (oldY < -0.01) {

                        oldY += 0.1.toFloat()
                        oldX = (oldY / (Math.tan(degree))).toFloat()
                        tryLoop(oldX, oldY )

                    }
                } else  if (oldY > 0) {
                    val x: Float = oldY / oldX
                    var degree = Math.atan(x.toDouble())
                    if(oldX*-1> oldY){
                        while (oldY > 0.01) {
                            oldX += 0.1.toFloat()
                            oldY =(oldX * (Math.tan(degree))).toFloat()
                            tryLoop(oldX, oldY )

                        }
                    }else {
                        if(oldX> oldY){
                            while (oldX > 0.01) {
                                oldX -= 0.1.toFloat()
                                oldY =(oldX * (Math.tan(degree))).toFloat()
                                tryLoop(oldX, oldY)

                            }

                        }else{
                            while (oldY > 0.01) {
                                oldY -= 0.1.toFloat()
                                oldX = (oldY / (Math.tan(degree))).toFloat()
                                tryLoop(oldX, oldY)

                            }
                        }

                    }
                } else if (oldY < 0) {
                    val x: Float = oldY / oldX
                    var degree = Math.atan(x.toDouble())
                    if(oldX< oldY){
                        while (oldX < -0.01) {
                            oldX += 0.1.toFloat()
                            oldY =(oldX * (Math.tan(degree))).toFloat()
                            tryLoop(oldX, oldY )
                        }
                    }else{
                        while (oldX < -0.01) {
                            oldY += 0.1.toFloat()
                            oldX = (oldY / (Math.tan(degree))).toFloat()
                            tryLoop(oldX, oldY )
                        }
                    }

                }

                drawJoystick(centerX, centerY)
                joystickCallback!!.onJoystickMoved(0f, 0f, id)
            }


        }
        return true
    }

    private fun ifMotionEvent(e: MotionEvent) {
        val displacement = Math.sqrt(
            Math.pow(
                e.x - centerX.toDouble(),
                2.0
            ) + Math.pow(e.y - centerY.toDouble(), 2.0)
        ).toFloat()
        if (displacement < baseRadius) {
            drawJoystick(e.x, e.y)
            joystickCallback!!.onJoystickMoved(
                (e.x - centerX) / baseRadius,
                (e.y - centerY) / baseRadius,
                id
            )

        } else {
            val ratio = baseRadius / displacement
            val constrainedX = centerX + (e.x - centerX) * ratio
            val constrainedY = centerY + (e.y - centerY) * ratio
            drawJoystick(constrainedX, constrainedY)
            joystickCallback!!.onJoystickMoved(
                (constrainedX - centerX) / baseRadius,
                (constrainedY - centerY) / baseRadius,
                id
            )
        }
    }


    //check and calculate where  to placed the hat joystick
    fun tryLoop(oldX: Float, oldY: Float ) {
        var realPositionX: Float
        var realPositionY: Float
        try {
            Thread.sleep(1)
            realPositionX = (oldX * baseRadius) + centerX
            realPositionY = (oldY * baseRadius) + centerY
            val displacement = Math.sqrt(
                Math.pow(
                    realPositionX - centerX.toDouble(),
                    2.0
                ) + Math.pow(realPositionY - centerY.toDouble(), 2.0)
            ).toFloat()
            if (displacement < baseRadius) {
                drawJoystick(realPositionX, realPositionY)

            } else {
                val ratio = baseRadius / displacement
                val constrainedX = centerX + (realPositionX - centerX) * ratio
                val constrainedY = centerY + (realPositionY - centerY) * ratio
                drawJoystick(constrainedX, constrainedY)
            }
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }

    }

    interface JoystickListener {
        fun onJoystickMoved(
            xPercent: Float,
            yPercent: Float,
            id: Int
        )

        fun getValusX(): String
        fun getValusY(): String
        fun sendIfChangeIsBig(xJoystick: Float, yJoystick: Float, xSlider: Float, ySlider: Float)
        fun checkIfBigChange(xJoystick: Float, yJoystick: Float, xSlider: Float, ySlider: Float): Boolean
        fun sendValueToServer(xJoystick: Float, yJoystick: Float, xSlider: Float, ySlider: Float)
    }

}
