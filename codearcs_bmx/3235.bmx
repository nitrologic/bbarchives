; ID: 3235
; Author: coffeedotbean
; Date: 2015-12-29 13:15:05
; Title: Tween/Easing
; Description: 10 different Tweens/Easing's

' ===========================================================================================================================
' Date: 28/12/2015
' ===========================================================================================================================
' Original author\code Skn3 (michael contento)
' Ported from his Monkey-x code <a href="https://github.com/michaelcontento/monkey/blob/master/bananas/skn3/tweening/tween.monkey" target="_blank">https://github.com/michaelcontento/monkey/blob/master/bananas/skn3/tweening/tween.monkey</a>
' All Credit to Skn3, BlitzMax port by Supertino A.K.A coffeedotbean
' 
' Usage - Simply 'Include' this .bmx file into your existing project and use the functions below
' Important - all Equations but Linear must use an EaseIn, EaseOut or EaseInOut Call
' ===========================================================================================================================

' Example
' =======
' local x:Float
' local myTween:Tween
' myTween = CreateTween(Tween.Sine.EaseInOut,0,480,5000)
' StartTween(myTween)
'
' While
'    UpdateTween(myTween)
'    x=TweenValue(myTween)
'    DrawRect x,0,32,32
' Wend

' ###########################################################################################################################
' #                                                           	Required to Create & return a Tween object
' ###########################################################################################################################
Function CreateTween:Tween(equation:TweenEquationCall, startValue:Float, endValue:Float, duration:Int)
	Local a:Tween = New Tween; a.Init(equation, startValue, endValue, duration) ; Return a
End Function

' ###########################################################################################################################
' #   Functions - these just call methods in the Tween Class, class methods can also be called directly for that OOP goodness
' ###########################################################################################################################

'// Return Value
Function TweenValue:Float(a:Tween) ; Return a.Value() ; End Function

'// Start
Function TweenStart:Int(a:Tween) ; a.Start ; End Function

'// Stop (Pause)
Function TweenStop:Int(a:Tween) ; a.Stop ; End Function

'// Resume from Stop
Function TweenResume:Int(a:Tween) ; a.Resume ; End Function

'// Call to update 
Function TweenUpdate:Int(a:Tween) ; a.Update ; End Function

'// Set YoYo (back and forth)
Function TweenSetYoYo:Int(a:Tween, flag:Int = True) ; a.SetYoYo(flag) ; End Function

'// Infinate looping
Function TweenSetLooping:Int(a:Tween, flag:Int = True) ; a.SetLooping(flag) ; End Function

'// Force to start point
Function TweenRewind:Int(a:Tween) ; a.Rewind ; End Function

'// Force to end point
Function TweenFastForward:Int(a:Tween) ; a.FastForward ; End Function

'// Change end value, if 0 duration original duration is used
Function TweenContinueTo:Int(a:Tween, endValue:Float, duration:Int = 0) ; a.ContinueTo(endValue, duration) ; End Function

'// Return if the Tween is running
Function TweenIsActive:Int(a:Tween) ; Return a.isActive ; End Function

'// Set duration time - if changed since creation 
Function TweenSetDuration:Int(a:Tween, duration:Int) ; a.SetDuration(duration) ; End Function

'// Set Equation - if changed since creation
Function TweenSetEquation:Int(a:Tween, equation:TweenEquationCall) ; a.SetEquation(equation:TweenEquationCall) ; End Function

'// Set Value - if changed since creation
Function TweenSetValue:Int(a:Tween, startValue:Float, endValue:Float) ; a.SetValue(startValue:Float, endValue:Float) ; End Function


' ###########################################################################################################################
' ###########################################################################################################################
' ###########################################################################################################################
' #                                            Tween module contents - all credit to Skn3 for orginal code, 99% original code
' ###########################################################################################################################
Type Tween
    
	Global Linear:TweenEquationCall = New TweenEquationCallLinear
	Global Sine:TweenEquation = New TweenEquationSine
    Global Quint:TweenEquation = New TweenEquationQuint
	Global Quart:TweenEquation = New TweenEquationQuart
	Global Quad:TweenEquation = New TweenEquationQuad
	Global Back:TweenEquation = New TweenEquationBack
    Global Bounce:TweenEquation = New TweenEquationBounce
    Global Circ:TweenEquation = New TweenEquationCirc
    Global Cubic:TweenEquation = New TweenEquationCubic
    Global Elastic:TweenEquation = New TweenEquationElastic
    Global Expo:TweenEquation = New TweenEquationExpo
    
	Field equation:TweenEquationCall

    Field startt:Float
    Field change:Float
    Field current:Float

    Field isActive:Int = False
    Field isLooping:Int = False
    Field isYoYo:Int = False

    Field duration:Int
    Field loopCount:Int
    Field timeStart:Int
    Field timeCurrent:Int
    Field timePrevious:Int
	Field timePaused:Int

    ' --- constructors ---
    Method Init(equation:TweenEquationCall, startValue:Float, endValue:Float, duration:Int)
        SetEquation(equation)
        SetDuration(duration)
        SetValue(startValue,endValue)
    End Method

    ' --- private methods ---
    Method UpdateValue:Int()
        Current = equation.Call(timeCurrent, startt, change, duration)
    End Method

    ' --- value methods ---
    Method Value:Float()
        Return current
    End Method

    ' --- setup methods methods ---
    Method SetEquation:Int(equation:TweenEquationCall)
        Self.equation = equation
    End Method

    Method SetDuration:Int(duration:Int)
        Self.duration = duration
    End Method

    Method SetValue:Int(startValue:Float, endValue:Float)
        startt = startValue
        change = endValue - startValue
    End Method

    Method SetYoYo:Int(flag:Int)
        isYoYo = flag
    End Method

    Method SetLooping:Int(flag:Int)
        isLooping = flag
   	End Method

    ' --- control methods ---
    Method Start:Int()
        ' --- this will re-start the tween ---
        Rewind()
        isActive = True
        loopCount = 0
    End Method

    Method Stop:Int()
        ' --- stop the tween from updating ---
		If isActive
        	isActive = False
			timePaused = Millisecs()
		EndIf
    End Method

	Method Resume:Int()
		' --- resume the tween from a paused state ---
		If isActive = False
			isActive = True
	
			'update the start timer!
			'get difference
			Local difference:Int = timePaused - timeStart
			
			'update timestamps
			timeCurrent = Millisecs()
			timeStart = timeCurrent - difference
			timePrevious = timeCurrent
			timePaused = 0
			
			Print "difference = " + difference
			Print "timeStart = " + timeStart
		EndIf
	End Method

    Method Rewind:Int()
        ' --- put the tween at the start of its tween ---
        timeCurrent = 0
		timePaused = 0
        timeStart = Millisecs()
        UpdateValue()
    End Method

    Method FastForward:Int()
        ' --- set the tween at the end ---
        timeCurrent = duration
        UpdateValue()
        Stop()
    End Method

    Method ContinueTo:Int(endValue:Float, duration:Int = 0)
        ' --- change the end target for teh value
        startt = Current
        SetValue(startt, endValue)

        If isActive
            'isActive so need to continue operation
            If duration = 0
                'no duration specified so use previous duration!
                Self.duration = duration - timeCurrent
            Else
                'override duration
                Self.duration = duration
            Endif

            timeStart = Millisecs()
            timeCurrent = 0

            If duration <= 0
                duration = 0
                Stop()
            Endif
        Else
            If duration > 0 SetDuration(duration)
            Start()
        EndIf		
    End Method

    Method YoYo:Int()
        ContinueTo(startt)
    End Method

    ' --- class methods ---
    Method Update:Int()
        If isActive
            'update the current time!
            timePrevious = timeCurrent
            Local time:Int = MilliSecs() - timeStart

            If time > duration
                'time is beyond length of tween
                If isLooping Or isYoYo
                    'increase the loop count
                    loopCount:+1

                    'look at yoyoing the values
                    If isYoYo
                        timeCurrent = duration
                        Current = startt + change

                        If isLooping Or loopCount <= 1
                            ContinueTo(startt, duration)
                        Else
                            Stop()
                        Endif
                    Else
                        'wrap around the tween to the start
                        timeCurrent = 0'
                        timeStart = Millisecs()
                    Endif
                Else
                    'set the tween to the end
                    timeCurrent = duration
                    Current = startt + change
                    Stop()
                Endif
            Else If time < 0
                'time is before the start of the tween
                timeCurrent = 0
                timeStart = Millisecs()
                UpdateValue()
            Else
                'time is within the tween
                timeCurrent = time
                UpdateValue()
            Endif
        Endif
    End Method
End Type

Type TweenEquation
    ' --- really just a grouping class for user access ---
    Field EaseIn:TweenEquationCall
    Field EaseOut:TweenEquationCall
    Field EaseInOut:TweenEquationCall

End Type

Type TweenEquationCall
    ' --- base class to provide a call function pointer work around ---
    Method Call:Float(t:Float, b:Float, c:Float, d:Float) Abstract
	
	Function Pow:Float(a:Float, b:Float)
		Return a ^ b
	End Function
	
End Type

' ###########################################################################################################################
' #                                                                                                                    Linear
' ###########################################################################################################################
Type TweenEquationCallLinear Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        Return c*t/d + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                      Sine
' ###########################################################################################################################
Type TweenEquationSine Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallSineEaseIn
        EaseOut = New TweenEquationCallSineEaseOut
        EaseInOut = New TweenEquationCallSineEaseInOut
    End Method
End Type

Type TweenEquationCallSineEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        Return - c * Cos((t / d * (Pi / 2)) * 57.2957795) + c + b
    End Method
End Type

Type TweenEquationCallSineEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        Return c * Sin((t/d * (PI/2)) * 57.2957795) + b
    End Method
End Type

Type TweenEquationCallSineEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        Return -c/2 * (Cos((PI*t/d) * 57.2957795) - 1) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                     Quint
' ###########################################################################################################################
Type TweenEquationQuint Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallQuintEaseIn
        EaseOut = New TweenEquationCallQuintEaseOut
        EaseInOut = New TweenEquationCallQuintEaseInOut
    End Method
End Type

Type TweenEquationCallQuintEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return c * t * t * t * t * t + b
    End Method
End Type

Type TweenEquationCallQuintEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t = t / d - 1
        Return c * (t * t * t * t * t + 1) + b
    End Method
End Type

Type TweenEquationCallQuintEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		t:/d / 2
		If t < 1 Return c / 2 * t * t * t * t * t + b
        t:-2
		Return c / 2 * (t * t * t * t * t + 2) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                     Quart
' ###########################################################################################################################
Type TweenEquationQuart Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallQuartEaseIn
        EaseOut = New TweenEquationCallQuartEaseOut
        EaseInOut = New TweenEquationCallQuartEaseInOut
    End Method
End Type

Type TweenEquationCallQuartEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return c * t * t * t * t + b
    End Method
End Type

Type TweenEquationCallQuartEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t = t / d - 1
        Return -c * (t * t * t * t - 1) + b
    End Method
End Type

Type TweenEquationCallQuartEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d / 2
		If t < 1 Return c / 2 * t * t * t * t + b
        t:-2
		Return -c / 2 * (t * t * t * t - 2) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                      Quad
' ###########################################################################################################################
Type TweenEquationQuad Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallQuadEaseIn
        EaseOut = New TweenEquationCallQuadEaseOut
        EaseInOut = New TweenEquationCallQuadEaseInOut
    End Method
End Type

Type TweenEquationCallQuadEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return c * t * t + b
    End Method
End Type

Type TweenEquationCallQuadEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return -c * t * (t - 2) + b
    End Method
End Type

Type TweenEquationCallQuadEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d / 2
		If t < 1 Return c / 2 * t * t + b
		t:-1
		Return -c / 2 * (t * (t - 2) - 1) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                   Explode
' ###########################################################################################################################
Type TweenEquationExpo Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallExpoEaseIn
        EaseOut = New TweenEquationCallExpoEaseOut
        EaseInOut = New TweenEquationCallExpoEaseInOut
    End Method
End Type

Type TweenEquationCallExpoEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        If t = 0 Return b
        Return c * Pow(2, 10 * (t / d - 1)) + b
    End Method
End Type

Type TweenEquationCallExpoEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        If t = d Return b + c
        Return c * (-Pow(2, -10 * t / d) + 1) + b
    End Method
End Type

Type TweenEquationCallExpoEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		If t =0 Return b
		If t = d Return b + c
		t:/d / 2
		If t < 1 Return c / 2 * Pow(2, 10 * (t - 1)) + b
		t:-1
		Return c / 2 * (-Pow(2, -10 * t) + 2) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                     Cubic
' ###########################################################################################################################
Type TweenEquationCubic Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallCubicEaseIn
        EaseOut = New TweenEquationCallCubicEaseOut
        EaseInOut = New TweenEquationCallCubicEaseInOut
    End Method
End Type

Type TweenEquationCallCubicEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return c * t * t * t + b
    End Method
End Type

Type TweenEquationCallCubicEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t = t / d - 1
        Return c * (t * t * t + 1) + b
    End Method
End Type

Type TweenEquationCallCubicEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d / 2
		If t < 1 Return c / 2 * t * t * t + b
		t:-2
		Return c / 2 *(t * t * t + 2) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                      Circ
' ###########################################################################################################################
Type TweenEquationCirc Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallCircEaseIn
        EaseOut = New TweenEquationCallCircEaseOut
        EaseInOut = New TweenEquationCallCircEaseInOut
    End Method
End Type

Type TweenEquationCallCircEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
        Return - c * (Sqr(1 - t * t) - 1) + b
    End Method
End Type

Type TweenEquationCallCircEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t = t / d - 1
        Return c * Sqr(1 - t * t) + b
    End Method
End Type

Type TweenEquationCallCircEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d / 2
		If t < 1 Return - c / 2 * (Sqr(1 - t * t) - 1) + b
		t:-2
		Return c / 2 * (Sqr(1 - t * t) + 1) + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                    Bounce
' ###########################################################################################################################
Type TweenEquationBounce Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallBounceEaseIn
        EaseOut = New TweenEquationCallBounceEaseOut
        EaseInOut = New TweenEquationCallBounceEaseInOut
    End Method
End Type

Type TweenEquationCallBounceEaseIn Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t = (d-t) / d
		If t < 0.3636363
			Return c - (c * (7.5625 * t * t)) + b
		Else If t < 0.7272727
            t:-0.5454545
			Return c - (c * (7.5625 * t * t + 0.75)) + b
		Else If t < 0.9090909
            t:-0.8181818
			Return c - (c * (7.5625 * t * t + 0.9375)) + b
		Else
            t:-0.9636363
			Return c - (c * (7.5625 * t * t + 0.984375)) + b
        Endif
    End Method
End Type

Type TweenEquationCallBounceEaseOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
		If t < 0.3636363
			Return c *(7.5625 * t * t) + b
		Else If t < 0.7272727
            t:-0.5454545
			Return c * (7.5625 * t * t + 0.75) + b
		Else If t < 0.9090909
            t:-0.8181818
			Return c * (7.5625 * t * t + 0.9375) + b
		Else
            t:-0.9636363
			Return c * (7.5625 * t * t + 0.984375) + b
        Endif
    End Method
End Type

Type TweenEquationCallBounceEaseInOut Extends TweenEquationCall
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		If t < d/2
            t = (d - t * 2) / d
    		If t < 0.3636363
    			Return (c - (c * (7.5625 * t * t))) * 0.5 + b
    		Else If t < 0.7272727
                t:-0.5454545
    			Return (c - (c * (7.5625 * t * t + 0.75))) * 0.5 + b
    		Else If t < 0.9090909
                t:-0.8181818
    			Return (c - (c * (7.5625 * t * t + 0.9375))) * 0.5 + b
    		Else
                t:-0.9636363
    			Return (c - (c * (7.5625 * t * t + 0.984375))) * 0.5 + b
            Endif
        Else
            t = (t * 2 - d) / d
    		If t < 0.3636363
    			Return (c *(7.5625 * t * t)) * 0.5 + c * 0.5 + b
    		Else If t < 0.7272727
                t:-0.5454545
    			Return (c * (7.5625 * t * t + 0.75)) * 0.5 + c * 0.5 + b
    		Else If t < 0.9090909
                t:-0.8181818
    			Return (c * (7.5625 * t * t + 0.9375)) * 0.5 + c * 0.5 + b
    		Else
                t:-0.9636363
    			Return (c * (7.5625 * t * t + 0.984375)) * 0.5 + c * 0.5 + b
            Endif
        Endif
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                   Elastic
' ###########################################################################################################################
Type TweenEquationElastic Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallElasticEaseIn
        EaseOut = New TweenEquationCallElasticEaseOut
        EaseInOut = New TweenEquationCallElasticEaseInOut
    End Method
End Type

Type TweenEquationCallElasticEaseIn Extends TweenEquationCall
    Field p:Float
    Field a:Float
    Field s:Float

    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		If t = 0 Return b
		t:/d
        If t = 1 Return b+c
        p = d *  0.3
		a = c
        s = p / 4
        t:-1
		Return -(a * Pow(2,10 * (t)) * Sin(((t * d - s) * (2 * PI) / p) * 57.2957795)) + b
    End Method
End Type

Type TweenEquationCallElasticEaseOut Extends TweenEquationCall
    Field p:Float
    Field a:Float
    Field s:Float

    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		If t = 0 Return b
		t:/d
        If t = 1 Return b+c
        p = d * 0.3
		a = c
        s = p / 4
		Return (a * Pow(2,-10 * t) * Sin(((t * d - s) * (2 * PI) / p) * 57.2957795) + c + b)
    End Method
End Type

Type TweenEquationCallElasticEaseInOut Extends TweenEquationCall
    Field p:Float
    Field a:Float
    Field s:Float

    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
		If t = 0 Return b
		t:/d / 2
        If t = 2 Return b+c
        p = d * (0.3 * 1.5)
		a = c
        s = p / 4
		If t < 1
            t:-1
            Return -0.5 * (a * Pow(2,10 * t) * Sin(((t * d - s) * (2 * PI) / p) * 57.2957795)) + b
        Endif
        t:-1
		Return a * Pow(2,-10 * t) * Sin(((t * d - s) * (2 * PI) / p) * 57.2957795) * 0.5 + c + b
    End Method
End Type

' ###########################################################################################################################
' #                                                                                                                      Back
' ###########################################################################################################################
Type TweenEquationBack Extends TweenEquation
    Method New()
        EaseIn = New TweenEquationCallBackEaseIn
        EaseOut = New TweenEquationCallBackEaseOut
        EaseInOut = New TweenEquationCallBackEaseInOut
    End Method
End Type

Type TweenEquationCallBackEaseIn Extends TweenEquationCall
    Field s:Float = 1.70158
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d
		Return c * t * t * ((s + 1) * t - s) + b
    End Method
End Type

Type TweenEquationCallBackEaseOut Extends TweenEquationCall
    Field s:Float = 1.70158
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        t:/d - 1
		Return c * (t * t * ((s + 1) * t + s) + 1) + b
    End Method
End Type

Type TweenEquationCallBackEaseInOut Extends TweenEquationCall
    Field s:Float = 1.70158
    Field s2:Float
    Method Call:Float(t:Float,b:Float,c:Float,d:Float)
        s2 = s
        t:/d / 2
        s2:*1.525
		If t < 1
            Return c / 2 * (t * t *((s2+1) * t - s2)) + b
        Endif
		t:-2
		Return c / 2 * (t * t * ((s2 + 1) * t + s2) + 2) + b
    End Method
End Type
