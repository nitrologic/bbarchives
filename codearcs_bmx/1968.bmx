; ID: 1968
; Author: ninjarat
; Date: 2007-03-15 13:19:14
; Title: Stream from Ram, File, or Net in FMod!
; Description: what teh title says!

Import Pub.FMod

Const FMOD_SAMPLE=1
Const FMOD_STREAM=2

Const TAS_PLAYMODE_ALL=True
Const TAS_PLAYMODE_ONE=False
Const TAS_LOOPMODE_ON =True
Const TAS_LOOPMODE_OFF=False

Type TAudioRamStreamer
	Field msp:Byte Ptr[]
	Field msl[]
	Field aChan,aStream,aTrack,aTrackCount
	Field playMode,loopMode,isPlaying
	
	Function create:TAudioRamStreamer(sampleimgs:Byte Ptr[],samplelengths[])
		Local tracker:TAudioRamStreamer=New TAudioRamStreamer
		tracker.aChan=channel
		tracker.aTrack=0
		
		tracker.playMode=TAS_PLAYMODE_ALL
		tracker.loopMode=TAS_LOOPMODE_ON
		
		If Not tracker.Init(sampleimgs,samplelengths) Then
			Print "Failed to get audio samples for streaming!"
			tracker=Null
		End If
		
		Return tracker
	End Function
	
	Method Init(sampleimgs:Byte Ptr[],samplelengths[])
		If sampleimgs.length<>samplelengths.length Then
			If sampleimgs.length<samplelengths.length Then
				Print "Audio sample image array smaller than sample length array!"
			Else
				Print "Sample length array smaller than audio sample image array!"
			End If
			Return False
		End If
		aTrackCount=sampleimgs.length
		
		msp=sampleimgs
		msl=samplelengths
		
		Return True
	End Method
	
	Method Play()
		If isPlaying Then Close
		isPlaying=True
		aStream=..
		 fsound_stream_open(msp[aTrack],FSOUND_HW2D|FSOUND_LOADMEMORY,..
		  0,msl[aTrack])
		aChan=fsound_stream_play(FSOUND_FREE,aStream)
	End Method
	
	Method Update()
		If isPlaying Then
			Select loopMode
			Case TAS_LOOPMODE_ON
				Select playMode
				Case TAS_PLAYMODE_ALL
					If Not fsound_isplaying(aChan) Then
						If aTrack=aTrackCount-1 Then SetTrack(0) Else SkipFwd()
					End If
				Case TAS_PLAYMODE_ONE
					If Not fsound_isplaying(aChan) Then
						Play
					End If
				End Select
			Case TAS_LOOPMODE_OFF
				Select playMode
				Case TAS_PLAYMODE_ALL
					If Not fsound_isplaying(aChan) Then
						If aTrack=aTrackCount-1 Then Close() Else SkipFwd()
					End If
				Case TAS_PLAYMODE_ONE
					If Not fsound_isplaying(aChan) Then
						Close
					End If
				End Select
			End Select
		End If
	End Method
	
	Method Stop()
		isPlaying=False
		Return fsound_stream_stop(aStream)
	End Method
	
	Method Close()
		Stop
		Return fsound_stream_close(aStream)
	End Method
	
	Method SkipFwd()
		aTrack:+1
		If aTrack>aTrackCount-1 Then aTrack=0
		If aTrack<0 Then aTrack=aTrackCount-1
		Play
	End Method
	
	Method SkipBack()
		If aTrack>aTrackCount-1 Then aTrack=0
		If aTrack<0 Then aTrack=aTrackCount-1
		Play
	End Method
	
	Method SetTrack(track)
		If aTrack<>track Then
			aTrack=track
			If aTrack>aTrackCount-1 Then aTrack=aTrackCount-1
			If aTrack<0 Then aTrack=0
			Play
		End If
	End Method
	
	Method GetTrack()
		Return aTrack
	End Method
	
	Method GetCurrTrackLengthInSize()
		Return fsound_stream_getlength(aStream)
	End Method
	
	Method GetCurrTrackPosInSize()
		Return fsound_stream_getposition(aStream)
	End Method
	
	Method GetCurrTrackLengthInTime()
		Return fsound_stream_getlengthms(aStream)
	End Method
	
	Method GetCurrTrackPosInTime()
		Return fsound_stream_gettime(aStream)
	End Method
	
	Method GetTrackID3Tag$()
		Return FModGetSongName(aStream,FMOD_STREAM)
	End Method
	
	Method Free()
		Close
		msp=Null;msl=Null
		aChan=Null
		aStream=Null
		aTrack=Null
		aTrackCount=Null
		playMode=Null
		loopMode=Null
	End Method
End Type

Type TAudioFileStreamer
	Field msp:Byte Ptr[]
	Field aChan,aStream,aTrack,aTrackCount
	Field playMode,loopMode,isPlaying
	
	Function create:TAudioFileStreamer(names$[])
		Local tracker:TAudioFileStreamer=New TAudioFileStreamer
		tracker.aChan=channel
		tracker.aTrack=0
		
		tracker.playMode=TAS_PLAYMODE_ALL
		tracker.loopMode=TAS_LOOPMODE_ON
		
		If Not tracker.Init(names) Then
			Print "Failed to get audio samples for streaming!"
			tracker=Null
		End If
		
		Return tracker
	End Function
	
	Method Init(names$[])
		aTrackCount=names.length
		For tk=0 To aTrackCount-1
			msp[tk]=names[tk].ToCString()
		Next
		
		Return True
	End Method
	
	Method Play()
		If isPlaying Then Close
		isPlaying=True
		aStream=..
		 fsound_stream_open(msp[aTrack],FSOUND_HW2D,..
		  0,0)
		aChan=fsound_stream_play(FSOUND_FREE,aStream)
	End Method
	
	Method Update()
		If isPlaying Then
			Select loopMode
			Case TAS_LOOPMODE_ON
				Select playMode
				Case TAS_PLAYMODE_ALL
					If Not fsound_isplaying(aChan) Then
						If aTrack=aTrackCount-1 Then SetTrack(0) Else SkipFwd()
					End If
				Case TAS_PLAYMODE_ONE
					If Not fsound_isplaying(aChan) Then
						Play
					End If
				End Select
			Case TAS_LOOPMODE_OFF
				Select playMode
				Case TAS_PLAYMODE_ALL
					If Not fsound_isplaying(aChan) Then
						If aTrack=aTrackCount-1 Then Close() Else SkipFwd()
					End If
				Case TAS_PLAYMODE_ONE
					If Not fsound_isplaying(aChan) Then
						Close
					End If
				End Select
			End Select
		End If
	End Method
	
	Method Stop()
		isPlaying=False
		Return fsound_stream_stop(aStream)
	End Method
	
	Method Close()
		Stop
		Return fsound_stream_close(aStream)
	End Method
	
	Method SkipFwd()
		aTrack:+1
		If aTrack>aTrackCount-1 Then aTrack=0
		If aTrack<0 Then aTrack=aTrackCount-1
		Play
	End Method
	
	Method SkipBack()
		If aTrack>aTrackCount-1 Then aTrack=0
		If aTrack<0 Then aTrack=aTrackCount-1
		Play
	End Method
	
	Method SetTrack(track)
		If aTrack<>track Then
			aTrack=track
			If aTrack>aTrackCount-1 Then aTrack=aTrackCount-1
			If aTrack<0 Then aTrack=0
			Play
		End If
	End Method
	
	Method GetTrack()
		Return aTrack
	End Method
	
	Method GetCurrTrackLengthInSize()
		Return fsound_stream_getlength(aStream)
	End Method
	
	Method GetCurrTrackPosInSize()
		Return fsound_stream_getposition(aStream)
	End Method
	
	Method GetCurrTrackLengthInTime()
		Return fsound_stream_getlengthms(aStream)
	End Method
	
	Method GetCurrTrackPosInTime()
		Return fsound_stream_gettime(aStream)
	End Method
	
	Method GetTrackID3Tag$()
		Return FModGetSongName(aStream,FMOD_STREAM)
	End Method
	
	Method Free()
		Close
		msp=Null;msl=Null
		aChan=Null
		aStream=Null
		aTrack=Null
		aTrackCount=Null
		playMode=Null
		loopMode=Null
	End Method
End Type

Function FModGetSongName$(stream_or_sptr,mode=FMOD_SAMPLE)
	If mode=FMOD_STREAM Then
		sptr=fsound_stream_getsample(stream_or_sptr)
	Else If mode=FMOD_SAMPLE
		sptr=stream_or_sptr
	Else
		Return ""
	End If
	Return FmCStr(fsound_sample_getname(sptr))
End Function
