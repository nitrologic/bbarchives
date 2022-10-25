; ID: 1570
; Author: Arowx
; Date: 2005-12-21 19:38:55
; Title: Simple FMOD - TMusic Wrapper
; Description: Provides some basic FMOD features in a single Type

' Music - Include FMod and provide simple object that can:
'	Play List of XM tunes * Done
'   Adjust volume         * Done
'   Pause Music           * Done
'   Cycle through tracks  * Done
'   Play OGG Sound FX     * Done

Import pub.fmod

'rem Test Harness
IncBin "media/sfx/extend.xm"
IncBin "media/sfx/level3.xm"
IncBin "media/sfx/maddy.xm"
IncBin "media/sfx/sad.xm"

IncBin "media/sfx/bidip.ogg"
IncBin "media/sfx/blip1.ogg"
IncBin "media/sfx/boingg.ogg"
IncBin "media/sfx/clicker.ogg"
IncBin "media/sfx/keyclik.ogg"
IncBin "media/sfx/bhump.ogg"

Graphics 640,480,0,0

Global testTracks:String[] = ["media/sfx/extend.xm",
                              "media/sfx/level3.xm",
                              "media/sfx/maddy.xm",
                              "media/sfx/sad.xm"]

Global testSounds:String[] = ["media/sfx/bidip.ogg",                                        
                              "media/sfx/blip1.ogg", 
                              "media/sfx/boingg.ogg", 
                              "media/sfx/clicker.ogg", 
                              "media/sfx/keyclik.ogg", 
                              "media/sfx/bhump.ogg"]

Global musicTest:TMusic = TMusic.Create(testTracks, testSounds)

musicTest.Play()

While not KeyHit(KEY_ESCAPE)
	Cls
		
	If KeyHit(KEY_P)       Then musicTest.pausePlay()
	If KeyHit(KEY_PERIOD)  Then musicTest.nextTrack()
	If KeyHit(KEY_COMMA)   Then musicTest.previousTrack()
	If KeyHit(KEY_MINUS)   Then musicTest.decreaseVolume()
	If KeyHit(KEY_EQUALS)  Then musicTest.increaseVolume()
	
	If KeyHit(KEY_A)  Then musicTest.PlaySfx(0)
	If KeyHit(KEY_B)  Then musicTest.PlaySfx(1)
	If KeyHit(KEY_C)  Then musicTest.PlaySfx(2)
	If KeyHit(KEY_D)  Then musicTest.PlaySfx(3)
	If KeyHit(KEY_E)  Then musicTest.PlaySfx(4)
	If KeyHit(KEY_F)  Then musicTest.PlaySfx(5)
	
	Flip
Wend

musicTest.shutdown()
'End rem

Type TMusic
	Const Playing = 1
	Const PAUSED  = 0
	Const STOPPED = -1
	Const VOLUME_OFFSET = 8
	
	Field _trackNames:String[]
	Field _soundNames:String[]
	Field _tracks:Int[]
	Field _sounds:Int[]
	Field _volume:Int
	Field _currentTrack:Int
	Field _mode:Int
	
	Function Create:TMusic(tracks:String[], sounds:String[])
		newMusic:TMusic = New TMusic
		newMusic.setup(tracks,sounds)
		Return newMusic
	End Function
	
	Method setup(tracks:String[], sounds:String[])
		If FSOUND_Init(22000,256,0) =  True
			Print "Sound initialized."
		Else
			Print "Sound failed to initialize."
		EndIf
		
		FSOUND_DSP_SetActive(FSOUND_DSP_GetFFTUnit(),True)
		_mode = STOPPED
		_volume = 128
		_currentTrack = 0
		_tracks = New Int[0]
		_sounds = New Int[0]
		_tracks = _tracks[0..tracks.length]
		_sounds = _sounds[0..sounds.length]
		_trackNames = tracks[0..]
		_soundNames = sounds[0..]
		
		SetVolume(_volume)
		
		For Local trackIndex:Int = 0 Until tracks.length
			loadTrack(trackIndex)
		Next
		
		For Local soundIndex:Int = 0 Until sounds.length
			loadSfx(soundIndex)
		Next
		
	End Method               
	
	Method SetVolume(newVolume:Int)
		If FMUSIC_SetMasterVolume(_tracks[_currentTrack],newVolume) Then
			_volume = newVolume
			'SetChannelVolume( channel:TChannel,volume# )
			DebugLog "Volume changed to "+newVolume
		Else
			_volume = FMUSIC_GetMasterVolume(_tracks[_currentTrack])
			Print "Volume not changed to "+newVolume
			PrintError()
		EndIf
		
		If FSOUND_SetSFXMasterVolume(newVolume) Then
			DebugLog "SFX Volume changed to "+newVolume
		Else
			Print "Volume not changed to "+newVolume
			PrintError()
		End If
	End Method
	
	Method increaseVolume()
		_volume:+VOLUME_OFFSET
		If _volume > 255 Then _volume = 255
		
		SetVolume(_volume)
	End Method
	
	
	Method decreaseVolume()
		_volume:-VOLUME_OFFSET
		If _volume < 0 Then _volume = 0
		
		SetVolume(_volume)
	End Method
	
	Method loadTrack(trackIndex:Int)
		Local xmPtr:Byte Ptr = IncBinPtr(_trackNames[trackIndex])
		Local xmLen:Int = IncBinLen(_trackNames[trackIndex])
		
		Assert xmPtr <> Null Else "Track ["+_trackNames[trackIndex]+"] not found in IncBin "
		Assert xmLen <> 0 Else "Track ["+_trackNames[trackIndex]+"] has zero length "
		
		_tracks[trackIndex]=FMUSIC_LoadSongEx(xmPtr, 0, xmLen, FSOUND_LOADMEMORY, Null, 0)
		
		If _tracks[trackIndex] > 0 Then
			Print "Track Loaded "+_trackNames[trackIndex]
		Else 
			Print "Track failed to load."
			PrintError()
		EndIf
	End Method
	
	Method loadSfx(sfxIndex:Int)
		Local sfxPtr:Byte Ptr = IncBinPtr(_soundNames[sfxIndex])
		Local sfxLen:Int = IncBinLen(_soundNames[sfxIndex])
		
		Assert sfxPtr <> Null Else "Sound ["+_soundNames[sfxIndex]+"] not found in IncBin "
		Assert sfxLen <> 0 Else "Sound ["+_soundNames[sfxIndex]+"] has zero length "
		
		_sounds[sfxIndex]=FSOUND_Sample_Load(FSOUND_FREE, sfxPtr,FSOUND_LOADMEMORY, 0 , sfxLen )
		
		If _sounds[sfxIndex] > 0 Then
			Print "Sound Loaded "+_soundNames[sfxIndex]
		Else 
			Print "Sound failed to load."
			PrintError()
		EndIf		
	End Method
	
	Method shutdown()
		FSOUND_DSP_SetActive(FSOUND_DSP_GetFFTUnit(),False)
		FSOUND_Close()
	End Method
	
	Method Play()
		If FMUSIC_PlaySong(_tracks[_currentTrack]) = True
			_mode = Playing
			Print "Music playing "+_trackNames[_currentTrack]
			SetVolume(_volume)
		Else
			_mode = STOPPED
			Print "Music track failed to play "+_trackNames[_currentTrack]
			PrintError()
		EndIf
		
	End Method
	
	Method PlaySfx(index:Int)
		 FSOUND_PlaySound(FSOUND_FREE, _sounds[index]);
	End Method
	
	
	Method Stop()
		If _mode = Playing Then
			If FMUSIC_StopSong(_tracks[_currentTrack]) Then
				_mode = STOPPED
				Print "Music track stopped "+_trackNames[_currentTrack]
			Else
				Print "Music track failed to stop playing "+_trackNames[_currentTrack]
				PrintError()
			EndIf
		EndIf
	End Method
	
	Method pausePlay()
		If _mode = PAUSED
			If FMUSIC_SetPaused(_tracks[_currentTrack], False) Then
				_mode = Playing
			Else
				Print "Music track failed to pause "+_trackNames[_currentTrack]
				PrintError()
			EndIf
		Else If _mode = Playing
			FMUSIC_SetPaused(_tracks[_currentTrack], True)
			_mode = PAUSED	
		EndIf
	End Method
	
	Method nextTrack()
		Stop()
		_currentTrack:+1
		_currentTrack:Mod _tracks.length
		Play()
	End Method
	
	Method previousTrack()
		Stop()
		_currentTrack:-1
		
		If _currentTrack < 0 Then 
			_currentTrack = _tracks.length-1
		EndIf
		
		Play()
	End Method
	
	Method PrintError()
		Print "FMOD Error status : "+FMOD_GetError()
	End Method
	
End Type
