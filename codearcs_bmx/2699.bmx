; ID: 2699
; Author: John Galt
; Date: 2010-04-14 17:58:06
; Title: Realtime sound generation with fmod
; Description: Realtime sound generation with bah.fmod

SuperStrict

Framework BaH.FMOD
Import BRL.GLMax2d

Local system:TFMODSystem = New TFMODSystem.Create()

system.Init(32)

'Method CreateSound:TFMODSound(Mode:Int, exInfo:TFMODCreateSoundExInfo = Null)

Local channel:TFMODChannel=Null
Local result:Int=0
Local mode:Int=FMOD_2D|FMOD_OPENUSER|FMOD_LOOP_NORMAL|FMOD_HARDWARE

Local createsoundexinfo:TFMODCreateSoundExInfo=New TFMODCreateSoundExInfo
Local channels:Int=2

createsoundexinfo.SetDecodeBufferSize(44100)				'Chunk size of stream update in samples.This will be the amount of data passed To the user callback.
createsoundexinfo.SetLength(44100*channels*4*5)			'Length of PCM data in bytes of whole song (For Sound::getLength)
'4=length of short
createsoundexinfo.SetNumChannels(channels)                  'Number of channels in the sound.
createsoundexinfo.SetDefaultFrequency(44100)                'Default playback rate of sound.    
createsoundexinfo.SetFormat(FMOD_SOUND_FORMAT_PCM16)	     'Data format of sound.
createsoundexinfo.SetPCMReadCallback  (pcmreadcallback)     'User callback For reading.
createsoundexinfo.SetPCMSetPosCallback(pcmsetposcallback)   'User callback For seeking.

Local sound:TFMODSound=system.CreateSound(mode,createsoundexinfo)

If sound=Null
	DebugLog "sound is null"
EndIf

Local key:Int=0
Local version:Int=0

'Play the sound 

channel=system.PlaySound(FMOD_CHANNEL_FREE, sound, 0, Null)

Graphics 800, 600, 32

While Not KeyDown(KEY_ESCAPE)
	Cls
	DrawText "hello",100,100
	system.Update()
	Delay 10
	Flip
Wend

system.Close()
system.SystemRelease()

End

Function pcmreadcallback:Int(sound:TFMODSound, data:Byte Ptr, dataLen:Int)
	Local f1:Float=1600.0
	Local f2:Float=2400.0
	Local count:Int
	Global f1phase:Float=0.0
	Global f2phase:Float=0.0
    	
    	Local stereo16bitbuffer:Short Ptr = Short Ptr (data);

    	For count=0 Until datalen Shr 2 ' shr2 = 16bit stereo (4 bytes per sample)
   		stereo16bitbuffer[count*2] = makeSignedShort( (Sin(f1phase) * 32767.0 ) )    'Left channel
        	stereo16bitbuffer[count*2+1] = makeSignedShort( (Sin(f2phase) * 32767.0 ) )  'Right channel
		count:+1;
		
        	f1phase :+ 360.0*f1*(1.0/44100.0);
		While f1phase>=360.0
        		f1phase:-360.0
		Wend
		
		f2phase :+ 360.0*f2*(1.0/44100.0);
		While f2phase>=360.0
        		f2phase:-360.0
		Wend
	Next
	
    Return FMOD_OK;
End Function

Function pcmsetposcallback:Int(sound:TFMODSound, subsound:Int, position:Int, posType:Int)
    'This is useful If the user calls FMOD_Sound_SetPosition And you want To seek your data accordingly.
    Return FMOD_OK;
End Function

Function makeSignedShort:Short(floatval:Float)
	If (floatVal>0.0)
		Return Short(floatVal) 
	Else
		Return ( ~Short(Abs(floatval)) ) +Short(1)
	EndIf
End Function
