; ID: 2068
; Author: Perturbatio
; Date: 2007-07-15 07:53:31
; Title: (BMX) Synthesize a simple sound sample
; Description: It's very basic but it works, includes a primitive waveform drawing function

SuperStrict
Graphics 1024,768,0,0

Global Quit:Int = False
Const SampleSize:Int = 100000
Global sample:TAudioSample = TAudioSample.Create(SampleSize, 44100, SF_STEREO16BE)
Global sampleData:Byte[SampleSize] 

Print sample.length
Print getSampleLength(sample)

Function getSampleLength:Float(sample:TAudioSample)
	Return Float( (sample.length / Float( (sample.hertz * 60) ) ) * 60)
End Function



Function drawWave(sample:TAudioSample)
	Local x:Int, y:Int
	Local xscale:Double = sample.length/GraphicsWidth()
	For x = 0 Until sample.length Step 100
		y = sample.samples[x]
		If (y > 0) Then
			SetColor(255,y,y)
			DrawLine((x/xscale)-1, sample.samples[x-1], x/xscale, y)
			'DrawLine((x/xscale)-1, Abs(sample.samples[x-1]-255)+255, x/xscale, Abs(y-255)+255)
		End If
	Next
End Function

SeedRnd MilliSecs()

For Local m:Int = 0 Until SampleSize
	sample.samples[m] = Sin(m)*128
	If (m Mod 1000) > 900 Then sample.samples[m] = 100
Next

Local sound:TSound = LoadSound(sample)
Local channel:TChannel = PlaySound(sound)

While Not Quit
	Cls
	drawWave(sample)
	If channel.playing() Then
		SetColor(255,0,0)
		DrawText("Playing", 10, 300)
	Else
		SetColor(255,255,255)
		DrawText("Not Playing (Press space)", 10, 300)
	EndIf
	
	Flip
	If KeyDown(KEY_SPACE) And Not channel.playing() Then channel = PlaySound(sound)
	If KeyDown(KEY_ESCAPE) Then quit = True
Wend
End
