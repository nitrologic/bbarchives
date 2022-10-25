; ID: 3287
; Author: Flanker
; Date: 2016-09-12 17:21:41
; Title: .wav samples value
; Description: Load a .wav and access any sample value in function of its time from the begining

Graphics 800,600,32,2
SetBuffer BackBuffer()

; variables
Global wavBank
Global wavChannels
Global wavFrequency
Global wavBytePerSec
Global wavBytePerBloc
Global wavBits
Global wavDataSize

Print "Loading .wav...":Flip
Global wavSound = WaveLoad("test16bits.wav") ; ONLY WORKS WITH MONO AND STEREO WAV 8 OR 16 BITS

PlaySound wavSound
wavStartTime = MilliSecs()

;------------------------------------------------------------------------------------;
While Not KeyHit(1)

	Cls
	
	; waveform
	wavCurrentTime = MilliSecs()-wavStartTime
	For channel = 0 To wavChannels-1
		For i = 0 To 800
			Plot i,75+channel*150+WaveSample(wavCurrentTime+i,channel)/500
		Next
	Next
	
	Flip 0

Wend
;------------------------------------------------------------------------------------;

FreeSound wavSound
FreeBank wavBank

End

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function WaveLoad(wavPath$)

	If FileType(wavPath) = 1
		wavBank = CreateBank(FileSize(wavPath))
		wavFile = OpenFile(wavPath)
		ReadBytes wavBank,wavFile,0,FileSize(wavPath)
		CloseFile wavFile
	Else
		Return 0
	EndIf

	If Chr(PeekByte(wavBank,0)) + Chr(PeekByte(wavBank,1)) + Chr(PeekByte(wavBank,2)) + Chr(PeekByte(wavBank,3)) = "RIFF"
	
		If PeekInt(wavBank,4)+8 = FileSize(wavPath) ; file size
		
			If Chr(PeekByte(wavBank,8)) + Chr(PeekByte(wavBank,9)) + Chr(PeekByte(wavBank,10)) + Chr(PeekByte(wavBank,11)) = "WAVE"
			
				If Chr(PeekByte(wavBank,12)) + Chr(PeekByte(wavBank,13)) + Chr(PeekByte(wavBank,14)) + Chr(PeekByte(wavBank,15)) = "fmt "
	
					If PeekInt(wavBank,16) = 16 ; bloc Size
					
						If PeekShort(wavBank,20) = 1 ; PCM
													
							If Chr(PeekByte(wavBank,36)) + Chr(PeekByte(wavBank,37)) + Chr(PeekByte(wavBank,38)) + Chr(PeekByte(wavBank,39)) = "data"
							
								wavChannels = PeekShort(wavBank,22)
								wavFrequency = PeekInt(wavBank,24)
								wavBytePerSec = PeekInt(wavBank,28)
								wavBytePerBloc = PeekShort(wavBank,32)
								wavBits = PeekShort(wavBank,34)
																
								wavDataSize = PeekInt(wavBank,40)
								
								If wavBits <= 16 And wavChannels <= 2							
									wavSound = LoadSound(wavPath)
									Return wavSound
								Else
									Return 0
								EndIf
								
							EndIf
							
						EndIf
					
					EndIf				
				
				EndIf
			
			EndIf
		
		EndIf
		
	EndIf

End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function WaveSample(time,channel=0)
	
	offset = time*Float(wavBytePerSec)/1000/wavChannels/(wavBits/8)
	If offset > wavDataSize/wavChannels/(wavBits/8) Or offset < 0 Then Return 0
	
	Select wavBits
		Case 8
			sample = (PeekByte(wavBank,offset*wavChannels+channel)-128)*128
		Case 16
			sample = PeekShort(wavBank,offset*wavChannels*2+channel*2)
			If sample > 32768 Then sample = sample - 65535
	End Select

	Return sample

End Function
