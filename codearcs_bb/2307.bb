; ID: 2307
; Author: JoshK
; Date: 2008-09-03 20:01:47
; Title: Better OpenAL
; Description: Lightweight OpenAL command set.

SuperStrict

Framework brl.audiosample
Import pub.OpenAL
Import brl.wavloader
Import brl.oggloader


'===============================================================
'Test
Local sound:TSound
Local source:TSource
sound=TSound.Load("impact_metal07.wav")

TListener.SetMatrix([1.0,0.0,0.0,0.0, 0.0,1.0,0.0,0.0, 0.0,0.0,1.0,0.0, 1.0,0.0,0.0,1.0])

source=TSource.Create(sound,1)
source.Resume()

Delay 1000
source.stop()
source.resume()
source.setvelocity([100.0,0.0,0.0])
Delay 20000

RuntimeError alGetError()

End
'===============================================================


Private

Global ALDevice:Int
Global ALContext:Int

Function StartOpenAL()
	If Not ALContext
		ALDevice=alcOpenDevice(Null) 
		ALContext=alcCreateContext(ALDevice, Null) 
		alcMakeContextCurrent(ALContext)
	EndIf
EndFunction

Function StopOpenAL()
	alcDestroyContext(ALContext)
	alcCloseDevice(ALDevice)
	ALContext=0
	ALDevice=0
EndFunction

OnEnd StopOpenAL

Public


Type TSource
	
	Field id:Int
	Field sound:TSound
	
	Method New()
		StartOpenAL()
		alGenSources 1,Varptr id
		alSourcei id,AL_SOURCE_RELATIVE,False
		alSourcef id,AL_REFERENCE_DISTANCE,0.0
		alSourcef id,AL_ROLLOFF_FACTOR,1.0
	EndMethod
	
	Method Delete()
		Stop()
		If ALContext alDeleteBuffers 1,Varptr id
	EndMethod
	
	'Source controls
	Method Resume()
		alSourcePlay id
	EndMethod
		
	Method Pause()
		alSourcePause id
	EndMethod
	
	Method Stop()
		alSourceStop id
	EndMethod
	
	Method Playing:Int()
		Local state:Int
		alGetSourcei id,AL_SOURCE_STATE,Varptr state
		Return state=AL_PLAYING
	EndMethod
	
	'Source settings
	Method SetVolume(volume:Float)
		alSourcef id,AL_GAIN,volume
	EndMethod
	
	Method SetPitch(pitch:Float)
		alSourcef id,AL_PITCH,pitch
	EndMethod
	
	Method SetPosition(position:Byte Ptr)
		Local positionf:Float[3]
		If position=Null
			alSourcei id,AL_SOURCE_RELATIVE,True
			alSource3f id,AL_POSITION,0,0,0
		Else
			MemCopy positionf,position,12
			alSourcei id,AL_SOURCE_RELATIVE,False
			alSource3f id,AL_POSITION,positionf[0],positionf[1],positionf[2]
		EndIf
	EndMethod
	
	Method SetRange(range:Float)
		alSourcef id,AL_MAX_DISTANCE,range	
	EndMethod
	
	Method SetVelocity(velocity:Byte Ptr)
		Local velocityf:Float[3]
		MemCopy velocityf,velocity,12
		alSource3f id,AL_VELOCITY,velocityf[0],velocityf[1],velocityf[2]
	EndMethod
	
	'Flags
	'1 - looping
	Function Create:TSource(sound:TSound,flags:Int=0)
		If Not sound Return Null
		Local source:TSource
		source=New TSource
		source.sound=sound
		alSourcei source.id,AL_BUFFER,sound.buffer
		If (1 & flags) alSourcei source.id,AL_LOOPING,True
		Return source
	EndFunction
	
EndType


Type TSound
	
	Field buffer:Int
	
	Method New()
		StartOpenAL()
		If ALContext alGenBuffers 1,Varptr buffer
	EndMethod
	
	Method Delete()
		If ALContext alDeleteBuffers 1,Varptr buffer
	EndMethod
	
	Function Load:TSound(url:Object)
		Local sample:TAudioSample
		Local sound:TSound
		Local alfmt:Int
		sample=LoadAudioSample(url)
		If Not sample Return Null
		sound=New TSound		
		Select sample.format
			Case SF_MONO8
				alfmt=AL_FORMAT_MONO8
			Case SF_MONO16LE
				alfmt=AL_FORMAT_MONO16
				?BigEndian
					sample=sample.Convert( SF_MONO16BE )
				?
			Case SF_MONO16BE
				alfmt=AL_FORMAT_MONO16
				?LittleEndian
					sample=sample.Convert( SF_MONO16LE )
				?
			Case SF_STEREO8
				alfmt=AL_FORMAT_STEREO8
			Case SF_STEREO16LE
				alfmt=AL_FORMAT_STEREO16
				?BigEndian
					sample=sample.Convert( SF_STEREO16BE )
				?
			Case SF_STEREO16BE
				alfmt=AL_FORMAT_STEREO16
				?LittleEndian
					sample=sample.Convert( SF_STEREO16LE )
				?
		End Select
		alBufferData sound.buffer,alfmt,sample.samples,sample.length*BytesPerSample[sample.format],sample.hertz
		Return sound
	EndFunction
	
EndType


Type TListener
	
	Function SetMatrix(mat:Byte Ptr)
		StartOpenAL()
		Local matf:Float[16]
		MemCopy matf,mat,64
		alListener3f AL_POSITION,matf[12],matf[13],matf[14]
		alListenerfv AL_ORIENTATION,[matf[4],matf[5],matf[6],matf[8],matf[9],matf[10]]
	EndFunction
	
EndType
