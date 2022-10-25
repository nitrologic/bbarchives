; ID: 3129
; Author: skidracer
; Date: 2014-06-10 00:42:25
; Title: freeaudio streaming
; Description: generate dynamic waveforms and stream them directly to a freeaudio device

' freestream.bmx
' mono 8 bit streaming example for 

Strict
Import pub.freeaudio

Const FRAG=4096

Print "freestream is free streaming..."

fa_Init(0)	' brl.freefreeaudio usually does this

Local buffer:Byte[FRAG*8]
Local writepos

Local sound
Local channel

sound=fa_CreateSound( FRAG*8,8,1,44100,buffer,$80000000)
Print "Sound:"+sound

channel=fa_PlaySound( sound, FA_CHANNELSTATUS_STREAMING ,0)
Print "PlaySound:"+channel

Local streaming
Local lfo#
Local osc1#

While True
'	Print "Status:"+fa_ChannelStatus( channel )	
	Local readpos=fa_ChannelPosition( channel )
	Local write=readpos+FRAG*4-writepos
	Local frags=write/FRAG	
	While frags>0	
		Print "Write to "+writepos
		Local pos=writepos Mod (FRAG*8)
		For Local f=0 Until FRAG				
			Local t=writepos+f	
			lfo=Sin(0.001*t)
			osc1=Sin(t*(lfo+2))
			buffer[pos+f]=128+10*lfo*osc1
		Next
		writepos:+FRAG
		frags:-1
	Wend
	
	If Not streaming And writepos>=FRAG*4
		fa_SetChannelPaused( channel, False )
		streaming=True
	EndIf
	
	Print "."	

	Delay 50
Wend
