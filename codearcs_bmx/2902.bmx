; ID: 2902
; Author: Armitage 1982
; Date: 2011-11-10 12:26:50
; Title: Easing Equations
; Description: Flash-like easing equations

' TERMS OF USE - EASING EQUATIONS
'
' Open source under the BSD License. 
' Ported to BlitzMax in 2011 by Armitage1982 (Michaël Lievens) : http://www.arm42.com/
' Based on the work of Robert Penner available at : http://www.robertpenner.com/easing/
'
' © 2003 Robert Penner, all rights reserved. 
' Redistribution and use in source and binary forms, with or without modification, are permitted
' provided that the following conditions are met:
'
' 	- 	Redistributions of source code must retain the above copyright notice, this list of conditions
'	-	and the following disclaimer.
' 	- 	Redistributions in binary form must reproduce the above copyright notice, this list of conditions
' 		and the following disclaimer in the documentation and/or other materials provided with the distribution.
' 	-	Neither the name of the author nor the names of contributors may be used to endorse or promote
'		products derived from this software without specific prior written permission.
'
' THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
' OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
' AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
' CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
' DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF 
' USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
' WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
' WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

SuperStrict

Import brl.StandardIO
Import brl.Graphics
Import brl.max2d

Const M_PI:Float = 3.14159265358979323846
Const PI_RAD:Float = 57.295779513082323				' Simply 180.0 / Pi

SetGraphicsDriver GLMax2DDriver()
Graphics(640, 480)

glPointSize 2.0




Function LinearTweening:Float(time:Float, begin:Float, finish:Float, duration:Float)
		Return finish * (time / duration) + begin
End Function




Function BounceTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
		time:/duration
		If (time < (1.0 / 2.75))
			Return finish * (7.5625 * time * time) + begin
		Else If (time < (2.0 / 2.75))
			time:-(1.5 / 2.75)
			Return finish * (7.5625 * time * time + 0.75) + begin
		Else If (time < (2.5 / 2.75))
			time:-(2.25 / 2.75)
			Return finish * (7.5625 * time * time + 0.9375) + begin
		Else
			time:-(2.625 / 2.75)
			Return finish * (7.5625 * time * time + 0.984375) + begin
		EndIf
End Function

Function BounceTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)

		Return finish - BounceTweeningEaseOut(duration - time, 0.0, finish, duration) + begin
		
End Function

Function BounceTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)

	If (time < duration / 2.0)
		Return BounceTweeningEaseIn(time * 2.0, 0.0, finish, duration) * 0.5 + begin
	Else
		Return BounceTweeningEaseOut(time * 2.0 - duration, 0.0, finish, duration) * 0.5 + finish * 0.5 + begin
	EndIf
		
End Function




Function ElasticTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float, a:Float = 0.0, p:Float = 0.0)

	Local s:Float

	If time = 0.0 Then Return begin
	time:/duration
	If time = 1.0 Then Return begin + finish
	If Not p Then p = duration * 0.3

	If Not a Or a < Abs(finish)
		a = finish
		s = p / 4.0
	Else
		s = p / (2.0 * M_PI) * ASin((finish / a) * PI_RAD)
	EndIf
	
	time:-1.0
	Return - (a * (2.0 ^ (10.0 * time))) * Sin(((time * duration - s) * (2.0 * M_PI) / p) * PI_RAD) + begin
	
End Function

Function ElasticTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float, a:Float = 0.0, p:Float = 0.0)

	Local s:Float

	If time = 0.0 Then Return begin
	time:/duration
	If time = 1.0 Then Return begin + finish
	If Not p Then p = duration * 0.3
	
	If Not a Or a < Abs(finish)
		a = finish
		s = p / 4.0
	Else
		s = p / (2.0 * M_PI) * ASin((finish / a) * PI_RAD)
	EndIf
	
	Return (a * (2.0 ^ (-10.0 * time))) * Sin(((time * duration - s) * (2.0 * M_PI) / p) * PI_RAD) + finish + begin
	
End Function

Function ElasticTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float, a:Float = 0.0, p:Float = 0.0)

	Local s:Float

	If time = 0.0 Then Return begin
	time:/duration / 2.0
	If time = 2.0 Then Return begin + finish
	If Not p Then p = duration * (0.3 * 1.5)
	
	If Not a Or a < Abs(finish)
		a = finish
		s = p / 4.0	
	Else
		s = p / (2.0 * M_PI) * ASin((finish / a) * PI_RAD)
	EndIf
	
	If time < 1.0
		time:-1.0
		Return - 0.5 * (a * (2.0 ^ (10.0 * time)) * Sin(((time * duration - s) * (2.0 * M_PI) / p) * PI_RAD)) + begin
	EndIf
	
	time:-1.0
	Return a * (2.0 ^ (-10.0 * time)) * Sin(((time * duration - s) * (2.0 * M_PI) / p) * PI_RAD) * 0.5 + finish + begin
	
End Function




Function BackTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float, s:Float = -1.0)
		If s = -1.0 Then s = 1.70158
		time:/duration
		Return finish * time * time * ((s + 1.0) * time - s) + begin
End Function

Function BackTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float, s:Float = -1.0)
		If s = -1.0 Then s = 1.70158
		time = time / duration - 1.0
		Return finish * (time * time * ((s + 1.0) * time + s) + 1.0) + begin
End Function

Function BackTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float, s:Float = -1.0)
		If s = -1.0 Then s = 1.70158
		time:/duration / 2.0
		s:*1.525
		If time < 1 Then Return finish / 2.0 * (time * time * ((s + 1.0) * time - s)) + begin
		time:-2.0
		Return finish / 2.0 * (time * time * ((s + 1.0) * time + s) + 2.0) + begin
End Function




Function RegularTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return finish * time * time + begin
End Function

Function RegularTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return - finish * time * (time - 2.0) + begin
End Function

Function RegularTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration / 2.0
	If time < 1.0 Then Return finish / 2.0 * time * time + begin
	time:-1.0
	Return - finish / 2.0 * (time * (time - 2.0) - 1.0) + begin
End Function




Function StrongTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return finish * time * time * time * time * time + begin
End Function

Function StrongTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time = time / duration - 1.0
	Return finish * (time * time * time * time * time + 1.0) + begin
End Function

Function StrongTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration / 2.0
	If time < 1.0 Then Return finish / 2.0 * time * time * time * time * time + begin
	time:-2.0
	Return finish / 2.0 * (time * time * time * time * time + 2.0) + begin
End Function




Function CircTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return - finish * (Sqr(1.0 - time * time) - 1.0) + begin
End Function

Function CircTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time = time / duration - 1.0
	Return finish * Sqr(1.0 - time * time) + begin
End Function

Function CircTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration / 2.0
	If time < 1.0 Then Return - finish / 2.0 * (Sqr(1.0 - time * time) - 1.0) + begin
	time:-2.0
	Return finish / 2.0 * (Sqr(1.0 - time * time) + 1.0) + begin
End Function



Function CubicTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return finish * time * time * time + begin
End Function

Function CubicTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time = time / duration - 1.0
	Return finish * (time * time * time + 1.0) + begin
End Function

Function CubicTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration / 2.0
	If time < 1 Then Return finish / 2.0 * time * time * time + begin
	time:-2.0
	Return finish / 2.0 * (time * time * time + 2.0) + begin
End Function




Function ExpoTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	If time = 0.0
		Return begin
	Else
		Return finish * (2.0 ^ (10.0 * (time / duration - 1.0))) + begin
	End If
End Function

Function ExpoTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	If time = duration
		Return begin + finish
	Else
		Return finish * (-(2.0 ^ (-10.0 * time / duration)) + 1.0) + begin
	End If
End Function

Function ExpoTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
		If time = 0.0 Then Return begin
		If time = duration Then Return begin + finish
		time:/duration / 2.0
		If (time < 1.0) Then Return finish / 2.0 * (2.0 ^ (10.0 * (time - 1.0))) + begin
		Return finish / 2.0 * (-(2.0 ^ (-10.0 * (time - 1.0))) + 2.0) + begin
End Function




Function QuartTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration
	Return finish * time * time * time * time + begin
End Function

Function QuartTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time = time / duration - 1.0
	Return - finish * (time * time * time * time - 1.0) + begin
End Function

Function QuartTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
	time:/duration / 2.0
	If (time < 1.0) Then Return finish / 2.0 * time * time * time * time + begin
	time:-2.0
	Return - finish / 2.0 * (time * time * time * time - 2.0) + begin
End Function




Function SineTweeningEaseIn:Float(time:Float, begin:Float, finish:Float, duration:Float)
		Return - finish * Cos((time / duration * (M_PI / 2.0)) * PI_RAD) + finish + begin
End Function

Function SineTweeningEaseOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
		Return finish * Sin((time / duration * (M_PI / 2.0)) * PI_RAD) + begin
End Function

Function SineTweeningEaseInOut:Float(time:Float, begin:Float, finish:Float, duration:Float)
		Return - finish / 2.0 * (Cos((M_PI * time / duration) * PI_RAD) - 1.0) + begin
End Function




While Not KeyHit(KEY_ESCAPE)
	
	drawBounceTweening()
	
	drawElasticTweening()

	drawBackTweening()

	drawRegularTweening()

	drawStrongTweening()

	drawCircTweening()

	drawCubicTweening()

	drawExpoTweening()

	drawQuartTweening()

	drawSineTweening()
	
Wend

Function drawBounceTweening()

	Local Y:Double, X:Double
	
	cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("BOUNCE TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = BounceTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = BounceTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = BounceTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()

	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawElasticTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("ELASTIC TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()	
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = ElasticTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = ElasticTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = ElasticTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawBackTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("BACK TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()	
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = BackTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = BackTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = BackTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawRegularTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("REGULAR TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = RegularTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = RegularTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = RegularTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawStrongTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("STRONG TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = StrongTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = StrongTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = StrongTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawCircTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("CIRC TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)
	
	TStopWatch.StartTimer()
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = CircTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = CircTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = CircTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawCubicTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("CUBIC TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = CubicTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = CubicTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = CubicTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawExpoTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("EXPO TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()	
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = ExpoTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = ExpoTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = ExpoTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawQuartTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("QUART TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()	
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = QuartTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = QuartTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = QuartTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Function drawSineTweening()

	Local Y:Double, X:Double
	
	Cls()
	SetColor 40, 40, 40
	DrawRect(0, 0, 640, 150)
	DrawRect(0, 330, 640, 150)

	SetColor(255, 80, 255)
	DrawText("SINE TWEENING", 260, 60)

	SetColor(255, 255, 255)
	DrawText("Linear Tweening", 260, 130)
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_ESCAPE) Then End
		If KeyHit(KEY_SPACE) Then Return
		Y = LinearTweening(X, 0.0, 150.0, 1000.0)
		Plot(X / 5 + 10, Y + 165)
		Plot(X / 5 + 220, Y + 165)
		Plot(X / 5 + 430, Y + 165)
		If X Mod 50 = 0 Then Flip
	Next

	SetColor(255, 80, 80)
	DrawText("EaseIn", 80, 340)
	SetColor(80, 255, 80)
	DrawText("EaseOut", 290, 340)
	SetColor(80, 80, 255)
	DrawText("EaseInOut", 500, 340)

	TStopWatch.StartTimer()	
	For X = 0.0 To 1000.0 Step 5.0
		If KeyHit(KEY_SPACE) Then Return
		If KeyHit(KEY_ESCAPE) Then End
		Y = SineTweeningEaseIn(X, 0.0, 150.0, 1000.0)
		SetColor(255, 80, 80)
		Plot(X / 5 + 10, Y + 165)

		Y = SineTweeningEaseOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 255, 80)
		Plot(X / 5 + 220, Y + 165)

		Y = SineTweeningEaseInOut(X, 0.0, 150.0, 1000.0)
		SetColor(80, 80, 255)
		Plot(X / 5 + 430, Y + 165)

		If X Mod 20 = 0 Then Flip
	Next
	TStopWatch.EndTimer()
	
	If WaitKey() = KEY_ESCAPE Then End

End Function

Type TStopWatch

	Global _watch:TStopWatch = New TStopWatch
	Field ms:Int
	Field time:Int
	
	Method New() 
		
	End Method
	
	Method Start() 
		ms = MilliSecs() 
	End Method
	
	Method Stop() 
		time = MilliSecs() - ms
		If time > 0 Then
			SetColor(200, 200, 200)
			DrawText("600 Points processed in " + time + " MilliSecs", 10, 400)
			Flip()
		End If
	End Method
	
	Function StartTimer()
		_watch.Start()
	End Function
	
	Function EndTimer()
		_watch.Stop()
	End Function
End Type
