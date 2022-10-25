; ID: 2652
; Author: tesuji
; Date: 2010-01-31 07:31:12
; Title: TWaveform, TCompositeWaveform
; Description: Simple Waveform example

' ------------------------------------------
' TWaveform, TCompositeWaveform example
' Waveforms can be used wherever there's a need for oscillating data - eg platform height
' Complex waveforms can be formed by blending multiple simple waveforms together aka analog waveform synthesis
' x axis range 0..1 y axis range -1..+1
' Tesuji 2010
' ------------------------------------------

SuperStrict

Graphics 1024,768,32

Local blobImage:TImage = CreateBlobImage(32)
MidHandleImage blobImage

' setup waveform examples

' basic sine wave
Local wave:TCompositeWaveform = TCompositeWaveform.Create( [TWaveform.Create(TWaveform.SINE)] )
Local blob1:TTestBlob = TTestBlob.Create(0, wave, blobImage, 0,128,255)

wave = TCompositeWaveform.Create( .. 
	[ ..
		TWaveform.Create(TWaveform.SINE, 16), ..
		TWaveform.Create(TWaveform.TRI, 3) .. 
    ] .. 
)
Local blob2:TTestBlob = TTestBlob.Create(1, wave, blobImage, 255,128,0)

wave = TCompositeWaveform.Create( .. 
	[ ..
		TWaveform.Create(TWaveform.SINE), ..
		TWaveform.Create(TWaveform.TRI, 4), .. 
		TWaveform.Create(TWaveform.SQUARE, 2) .. 
    ] .. 
)
Local blob3:TTestBlob = TTestBlob.Create(2, wave, blobImage, 64,255,64)

wave = TCompositeWaveform.Create( [TWaveform.Create(TWaveform.SINE, 3), TWaveform.Create(TWaveform.SINE, 4)] )
Local blob4:TTestBlob = TTestBlob.Create(3, wave, blobImage, 196,196,0)

Local x:Int = 0

While Not KeyHit(KEY_ESCAPE)

	' motion blur cls
	SetBlend ALPHABLEND
	SetColor 0,0,0
	SetAlpha .075
	If KeyDown(KEY_SPACE) Then SetAlpha .02
	DrawRect 0,0,GraphicsWidth(),GraphicsHeight()

	SetColor 0,0,0
	SetBlend ALPHABLEND
	SetAlpha .5
	DrawRect 0,0,GraphicsWidth(),64

	blob1.render(x Mod GraphicsWidth())
	blob2.render((x+(GraphicsWidth()*.25)) Mod GraphicsWidth())
	blob3.render((x+(GraphicsWidth()*.5)) Mod GraphicsWidth())
	blob4.render((x+(GraphicsWidth()*.75)) Mod GraphicsWidth())

	x :+ 2
	
	Flip

Wend

End

Function createBlobImage:TImage(size:Int=48)
	Cls
	Local cm:Float = 256/Float(size)
	For Local r:Int = 0 To size-1
		SetColor r*cm,r*cm,r*cm
		DrawOval r/2,r/2,size-r,size-r
	Next
	Local pixmap:TPixmap = GrabPixmap(0,0,size,size)
	Return LoadImage(pixmap) 	
End Function

' ------------------------------------------------------
Type TTestBlob

	Field number:Int = 0
	Field osc:TCompositeWaveform
	Field img:TImage
	Field red:Int,green:Int,blue:Int
	
	Function Create:TTestBlob(number:Int=0, osc:TCompositeWaveform, img:TImage, red:Int, green:Int, blue:Int)
		Local o:TTestBlob = New TTestBlob
		o.number = number
		o.osc = osc
		o.img = img
		o.red = red
		o.green = green
		o.blue = blue
		Return o
	End Function

	Method render(position:Float)

		Local v:Float = osc.value(position*(1.0/GraphicsWidth()))
		SetAlpha .75
		SetBlend LIGHTBLEND	
		SetColor red,green,blue
		DrawImage img, position, (GraphicsHeight()*.5)+(v*(GraphicsHeight()*.5)*.5)

		SetAlpha .5
		DrawText "oscillator "+number+" value: "+v, 0,number*16 
		DrawText osc.toString(), 300,number*16

	End Method
	

End Type

' ---------------------------------------------------------------------------------------------------------

' -------------------------------------------------------------------------
' Represents a simple waveform
' Sine, Triangle or Square wave
' frequency dictates the number of wave cycles in the time period provided
' -------------------------------------------------------------------------
Type TWaveform

	Const SINE:Int = 0
	Const TRI:Int = 1
	Const SQUARE:Int = 2
	
	Field labels:String[] = ["Sine","Triangle","Square"]
	
	Field waveformType:Int
	Field amplitude:Float = 1.0
	Field frequency:Float = 1.0
	Field offset:Float = 0.0

	Function Create:TWaveform(waveformType:Int, frequency:Float=1.0, amplitude:Float=1.0)
		Local w:TWaveform = New TWaveform
		w.waveformType = waveFormType
		w.frequency = frequency
		w.amplitude = amplitude
		Return w
	End Function

	' provide the waveform y position at the given x axis position
	' time should be in the range 0.0 to 1.0
	' output is given in the range of -ve amplitude to +ve amplitude
	
	Method value:Float(time:Float)

		Local v:Float = 1.0
		If amplitude > 0.0
			Select waveformType 
				Case SINE v = (Sin(frequency*(time+offset)*360))*amplitude
				Case SQUARE v = (((Ceil((time+offset) * frequency * 2) Mod 2.0)*2)-1) * amplitude
				Case TRI
					v = (((((time+offset+.25) * frequency * 2) Mod 1.0)*2)-1)
					If (Ceil((time+offset+.25) * frequency * 2) Mod 2.0) = 0.0 Then v = -v
			End Select
		End If

		Return v
		
	End Method
	
	Method toString:String()
		Return labels[waveformType] +" "+Left(frequency,3)
	End Method

End Type

' ---------------------------------------------------
' represents a blending of multiple simple waveforms
' ---------------------------------------------------
Type TCompositeWaveform

	Field waveforms:TWaveform[]
	Field amplitude:Float = 1.0
	
	Function Create:TCompositeWaveform(waveforms:TWaveform[], amplitude:Float=1.0)
		Local mw:TCompositeWaveform = New TCompositeWaveform
		mw.waveforms = waveforms
		mw.amplitude = amplitude
		Return mw
	End Function
	
	' provide the blended amplitude of all waveforms for the given x axis position
	' time should be in the range 0.0 to 1.0
	
	Method value:Float(time:Float)
	
		Local v:Float = 1.0
		For Local w:TWaveform = EachIn waveforms
			v :* w.value(time)
		Next
		
		Return v*amplitude
	
	End Method

	Method toString:String()
		Local s:String
		For Local w:TWaveform = EachIn waveforms
			s :+ "[" + w.toString() + "] "
		Next
		Return s
	End Method

End Type
