; ID: 1757
; Author: Ghost Dancer
; Date: 2006-07-21 11:58:35
; Title: Ogg Converter/Wav Saver
; Description: Load an Ogg file and save it to a Wav

Strict

Local oggFile$ = RequestFile$("Load Ogg...", "Ogg Files:ogg")
Local wavFile$ = RequestFile$("Save as...", "Wav files:wav", True)

If oggToWav(oggFile$, wavFile$)
	Print "Wav file created."
Else
	Print "Error, could not create wav file."
End If

End

Function oggToWav(oggFile$, wavFile$)
	Local sndSample:TAudioSample, sampleSize, sndBank:TBank, fileStream:TStream
	Local channels, bitRate, blockAlign, fileSaved
	
	'load the sound
	sndSample = LoadAudioSample(oggFile$)
	
	'determine mono/stero
	If sndSample.format = SF_MONO8 Or sndSample.format = SF_MONO16LE Or sndSample.format = SF_MONO16BE Then
		channels = 1
	Else
		channels = 2
	End If
		
	'determine bitrate & calculate size
	If sndSample.format = SF_MONO8 Or sndSample.format = SF_STEREO8 Then
		bitRate = 8
		sampleSize = sndSample.length * channels
	Else
		bitRate = 16
		sampleSize = sndSample.length * channels * 2
	End If
	
	blockAlign = channels * bitRate / 8
	
	
	'create a bank from the loaded sound
	sndBank = CreateStaticBank(sndSample.samples, sampleSize)
	
	'create a stream to save data
	fileStream = WriteStream(wavFile$)
	
	If fileStream Then
		'write wav header info
		fileStream.writeString("RIFF")						'"RIFF" file description header (4 bytes)
		fileStream.writeInt(sampleSize + 40)				'file size - 8 (4 bytes)
		fileStream.writeString("WAVE")						'"WAVE" description header (4 bytes)
		fileStream.writeString("fmt ")						'"fmt " description header (4 bytes)
		fileStream.writeInt(16)								'size of WAVE section chunk (4 bytes)
		fileStream.writeShort(1)							'WAVE type format (2 bytes)
		fileStream.writeShort(channels)						'mono/stereo (2 bytes)
		fileStream.writeInt(sndSample.hertz)				'sample rate (4 bytes)
		fileStream.writeInt(sndSample.hertz * blockAlign)	'avg bytes/sec (4 bytes)
		fileStream.writeShort(blockAlign)					'Block alignment (2 bytes)
		fileStream.writeShort(bitRate)						'Bits/sample (2 bytes)
		fileStream.writeString("data")						'"data" description header (4 bytes)
		fileStream.writeInt(sampleSize)						'size of data chunk (4 bytes)
		
		'write wav sound data
		sndBank.Write(fileStream, 0, sampleSize)
		
		'close the stream
		CloseStream fileStream
		
		fileSaved = True
	Else
		fileSaved = False
	End If
	
	'free up mem
	fileStream = Null
	sndBank = Null
	sndSample = Null
	
	Return fileSaved
End Function
