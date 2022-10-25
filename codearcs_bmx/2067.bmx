; ID: 2067
; Author: Perturbatio
; Date: 2007-07-15 06:45:33
; Title: (BMX) Determine length of an audio sample in seconds
; Description: (BMX) Determine length of an audio sample in seconds (tested with OGG files, but it should be the same for any file)

SuperStrict

Local sample:TAudioSample = LoadAudioSample("battle.ogg")
Local length:Float = getSampleLength(sample)
Print "Length in seconds: " + length
Print "Length: " + Int(Floor(length/60))+"m "+Int(length Mod 60)+"s"

Function getSampleLength:Float(sample:TAudioSample)
	Return Float( (sample.length / Float( (sample.hertz * 60) ) ) * 60)
End Function
