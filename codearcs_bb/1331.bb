; ID: 1331
; Author: indiepath
; Date: 2005-03-17 08:32:03
; Title: Sound Engine
; Description: Stop Sounds overlapping, chain sound events.

; ****************************************************************************
; MOD_SoundEngine

DebugLog "MOD_SoundEngine.bb"


; AUDIO CHANNEL CONSTANTS

Const CH_ANY = - 1
Const CH1 = 1
Const CH2 = 2
Const CH3 = 3
Const CH4 = 4
Const CH5 = 5
Const CH6 = 6
Const CH7 = 7
Const CH8 = 8
Const CH9 = 9
Const CH10 = 10

; ****************************************************************************

Type ActiveChannel
	Field SampleID
    Field Address  ; Channel handle
    Field FadeTime#
    Field FadeStart#
    Field Volume#  
	Field VolumeS# ; Start Volume
	Field Queue[4]
	Field QueueCount
End Type

; ****************************************************************************

Function PlaySample ( SampleAddress% , SampleID% = -1, Volume# = 1, Queue% = False )
    Local Channel.ActiveChannel
    Local Simultaneous% = False
    Local Playing% = False

    If SampleID < 0 Then Simultaneous = True
    For Channel = Each ActiveChannel

      	If Channel\SampleID = SampleID 

			If Queue = True
				Channel\QueueCount = Channel\QueueCount + 1
				Channel\Queue[Channel\QueueCount] = SampleAddress
				Playing = True
			Else
				Playing = True
			EndIf
			
		EndIf
		
      	If Not ChannelPlaying ( Channel\Address ) Then Delete Channel

    Next
    If Not ( Simultaneous = False And Playing = True )
      	Channel = New ActiveChannel
      	Channel\SampleID = SampleID
      	Channel\Address = PlaySound ( SampleAddress )
		ChannelVolume Channel\Address,Volume
		Channel\Volume# = Volume
		Channel\Volumes# = Volume
		channel\FadeTime = 0
    EndIf
End Function

; ****************************************************************************


Function UpdateChannels ()
	Local Channel.ActiveChannel

    For Channel = Each ActiveChannel
    	If Not ChannelPlaying ( Channel\Address ) Then 

			If Channel\QueueCount = 0 Then
				Delete Channel
				Return
			Else
				Channel\Address = PlaySound(Channel\Queue[1])
				For a = 1 To Channel\QueueCount - 1
					Channel\Queue[a] = Channel\Queue[a+1]
				Next
				Channel\QueueCount = Channel\QueueCount - 1
			EndIf
		EndIf
        If Channel\FadeTime
        	If MilliSecs () - Channel\FadeStart >= Channel\FadeTime
            	StopChannel Channel\Address
                Delete Channel
            Else
                Channel\Volume = Channel\VolumeS - Float ( MilliSecs () - Channel\FadeStart ) / Channel\FadeTime
                ChannelVolume Channel\Address , Channel\Volume
            End If
        End If
    Next
End Function

; ****************************************************************************

Function SoundOff ()  
  	Local Channel.ActiveChannel  
  	For Channel = Each ActiveChannel  
    	StopChannel Channel\Address  
    	Delete Channel  
  	Next
End Function

; ****************************************************************************

Function ReleaseSample ( SampleID , TimeOut# = 0 )
	Local Channel.ActiveChannel
    For Channel = Each ActiveChannel
	    If Not Channel\FadeTime
    	     If Channel\SampleID = SampleID
        	     If TimeOut
            	     Channel\FadeTime = TimeOut
                     Channel\FadeStart = MilliSecs ()
                 Else
                     StopChannel Channel\Address
                     Delete Channel
                 End If
             End If
        End If
    Next
End Function
