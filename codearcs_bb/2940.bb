; ID: 2940
; Author: _PJ_
; Date: 2012-03-22 20:06:07
; Title: Simple Channel Fading
; Description: Fade one channel to another

; THESE ARE REEQUIRED!
Global CHANNEL_PLAYING
Global CHANNEL_HIDDEN

Global PLAYING_VOLUME#=1.00
Global HIDDEN_VOLUME#=0.00

Global nb_Fading=False
Global FADE_START_TIME=0


;EXAMPLE:

Graphics 800,600,32,2
SetBuffer BackBuffer()

Const OVERLAP#=5850.0

Const A_PATH$="[INSERT FILEPATH HERE]"
Const B_PATH$="[INSERT FILEPATH HERE]"

Global SOUND_A=LoadSound(A_PATH)
Global SOUND_B=LoadSound(B_PATH)

InitialiseChannels(SOUND_A,SOUND_B)

While (Not KeyDown(1))
	Cls
	
	If (nb_Fading)
		
		UpdateFade
		
	Else
		Text 0,0,"Press Space Bar to Begin Fade In/Out"
		
		If (KeyHit(57))
			
			BeginFade
			
		End If
		
	End If	
	
	Flip
	
Wend

	
	
Function InitialiseChannels(SOUNDHandle_A,SOUNDHandle_B)	
	CHANNEL_PLAYING=PlaySound(SOUNDHandle_A)
	CHANNEL_HIDDEN=PlaySound(SOUNDHandle_B)
	
	ChannelVolume CHANNEL_PLAYING,PLAYING_VOLUME#
	ChannelVolume CHANNEL_HIDDEN,HIDDEN_VOLUME#
	
	PauseChannel CHANNEL_HIDDEN
	
	ResumeChannel CHANNEL_PLAYING
End Function

Function BeginFade()
	FADE_START_TIME=MilliSecs()
	nb_Fading=True
	ResumeChannel CHANNEL_HIDDEN
End Function

Function UpdateFade()
	
	Local Elapsed=(MilliSecs()-FADE_START_TIME)
	
	Text 0,20,"Fading..."
	
	If (Elapsed>OVERLAP)
			; It's been too long = swap over channels
		
		Local spare=CHANNEL_PLAYING
		CHANNEL_PLAYING=CHANNEL_HIDDEN
		CHANNEL_HIDDEN=spare
		
		PLAYING_VOLUME#=1.0
		HIDDEN_VOLUME#=0.0
		
			;we're donw with the faded-out channel
		PauseChannel CHANNEL_HIDDEN
		
			; No longer fading because we've all faded out ;)
		nb_Fading=False
		
	Else
		
		Local f_VolumeDrop#=Float(Elapsed/OVERLAP)
		
		PLAYING_VOLUME#=1.0-(f_VolumeDrop#)
		HIDDEN_VOLUME#=(f_VolumeDrop#)
	End If
	
		;Update Volume on channels
	
	ChannelVolume CHANNEL_PLAYING,PLAYING_VOLUME
	ChannelVolume CHANNEL_HIDDEN,HIDDEN_VOLUME
	
	
	Text 0,40,Str(PLAYING_VOLUME)
End Function
