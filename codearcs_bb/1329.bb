; ID: 1329
; Author: jfk EO-11110
; Date: 2005-03-16 00:54:31
; Title: MciSendString
; Description: Can be used to play Avis and Media

; MCI Wrapper - Written by Shawn C. Swift
; To use this system to display an AVI, you will need to call mciOpen, then mciWindow, then mciPlay.

; -------------------------------------------------------------------------------------------------------------------------------------
; This function opens a new mci device.
;
; DeviceName$ will be the name which you use to give the device commands later.
;
; Device should be one of the following:  
;
; 	0 = "cdaudio" 		- For playing tracks on a CD.
;	1 = "AVIVideo" 		- For playing AVI and other video files. 
; 	2 = "sequencer"     - For playing MIDI files.
;	3 = "waveaudio"		- For playing WAV files.
;
; Filename$ is an optional parameter to specify the file you wish to be loaded when the device is created.
; I don't think the mciLoad command can load audio later though, so for WAV files, you might have to use filename here.
; -------------------------------------------------------------------------------------------------------------------------------------


; partially edited by jfk of csp
;...

; you need these userlib decls in "winmm.decls" :
; ***********************************************

;.lib "winmm.dll" ; this is a standard windows file
;mciExecute%(Text$)
;mciSendString%(Command$,ReturnString*,ReturnLength%,Callback):"mciSendStringA"


; for more infos about MciSendString Commands goto msdn.com and search for "MciSendString"


Global ScreenWidth=1024
Global ScreenHeight=768

Graphics ScreenWidth,ScreenHeight,32,1
SetBuffer FrontBuffer()

Global generic_bank=CreateBank(4096) ; used for Mci answers





device_name$="AVIVideo" +MilliSecs()+Rand(100) ; create a unique handle to allow multiple movies
myHWND=SystemProperty$("AppHWND")

mciOpen(device_name$,"csp_intro_2005mp4.avi")
mciWindow(device_name$,myHWND) ; hook movie on blitz frontbuffer (requires fullscreen)


mciSize(device_name$, 10,10,320,240) ; scale and position as desired
mciSetAudioVolume(device_name$,500) ;0 to 1000... don't works! (only alters the main Volume, instead of movie volume!)

mciSet(device_name$,"seek exactly on") ; turn on "seek exactly"
mciSet(device_name$,"time format frames")

movie_length=mciStatus(device_name$,"length")







mciPlay(device_name$,"repeat") ; start playing, with loop option
t1=MilliSecs()+100000
While (KeyDown(1)=0) And (MilliSecs()<t1)
 Delay 10
 cuf=mciStatus(device_name$,"position") ; get current frame number
 Color 0,0,0
 Rect 0,400,300,30,1
 Color 0,255,0
 Locate 0,400
 Print cuf+" of "+movie_length+" Frames"
 ; you may capture frames using copyrect on the frontbuffer in fullscreen mode...
Wend

mciStop(device_name$)
mciClose(device_name$) ; never forget this

WaitKey()
End










; -------------------------------------------------------------------------------------------------------------------------------------
Function mciUpdate(DeviceName$,dc)
	mciSendString("update " + DeviceName$ + " hdc "+dc,generic_bank,0,0)
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
Function mciOpen(DeviceName$, FileName$="")
	
	Local hWND
	Local DeviceType$
	
	mciSendString("OPEN " + FileName$ + " TYPE AVIVIDEO ALIAS "+DeviceName$+" STYLE POPUP",generic_bank,0,0)

End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function sets the window which an mci device should display its media in and the display properties for that window.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciWindow(DeviceName$, Parent)
	
	Local hWND
	
	hWND = Parent ;QueryObject(Parent, 1)
	mciSendString("window " + DeviceName$ + " handle " + Str$(hWND),generic_bank,0,0)
		
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function loads a file into an mci digital-video or video-overlay device.
; I don't think it will load waveaudio, which is why I included the optional filename parameter on the mciOpen command.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciLoad(DeviceName$, FileName$)
	mciSendString("load " + DeviceName$ + " " + FileName$,generic_bank,0,0)
End Function	
	

; -------------------------------------------------------------------------------------------------------------------------------------
; This function changes the settings of an mci digital-video device, if said device supports them.
; (Mine does not.)
;
; Here are the settings you can use for Option$:
;
; 	"brightness" - 0..1 
;	"color"		 - 0..1 
; 	"contrast"	 - 0..1 
;	"tint"		 - 0.0 = Blue, 0.25 = Green, 0.5 = Normal, 0.75 = Red, 1.0 = Blue 
;	"sharpness"	 - 0..1 
;	"gamma" 	 - 1.0 = No gamma correction, 2.2 = Windows gamma
;	
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciSetVideo(DeviceName$, Option$, Value#=0)
	;mciSendString("setvideo " + DeviceName$ + " " + Option$ + " to " + Int(Value#*1000.0))
	mciSendString("setvideo " + DeviceName$ + " brightness to 100",generic_bank,0,0)	
End Function	


; -------------------------------------------------------------------------------------------------------------------------------------
; This function changes certain settings of the specified mci device.
;
; The following options may be specified.  Multiple options can be seperated with a space.
;
; 	"audio all off"      - 	Turns off audio.
;	"audio all on" 	     - 	Turns on audio.
;
;	"door open"		     - 	Opens the cd door.
;	"door closed" 	     - 	Closes the cd door.
;
;	"seek exactly on"    - 	Enables seeking to the exact frame specified.
;	"seek exactly off"   - 	When seeking, seeks only to the nearest keyframe. (Presumed to be faster.)
;
;	"speed FACTOR"	     - 	Sets the speed of playback, where FACTOR is the desired multiple of the normal speed multiplied by 1000.
;						   	Ie, 1.0, the normal speed, becomes 1000.  2.0, twice normal speed becomes 2000.  And 0.5 becomes 500.
;						   	FACTOR must be an integer.  A speed of 0 plays back the video as fast as possible without dropping
; 							frames, but without audio.
;
;	"time format FORMAT" - 	Sets the format for time used by the seek command. 
;
; 							Where FORMAT is the following:
;						   	
;							For digital-video: "frames" or "milliseconds"
;
;						  	For CD-audio: "msf" or "tmsf" which are in the format:
; 						  	"minutes:seconds:frames" or "tracks:minutes:seconds:frames" where the max values for each are:
;							"99:59:74" and "99:99:59:74"
;							
;							For wavaudio: "bytes", "milliseconds", or "samples"
;						
;							For sequencer: "milliseconds"
;
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciSet(DeviceName$, Option$)
	mciSendString("set " + DeviceName$ + " " + Option$,generic_bank,0,0)
End Function

; added by jfk:
Function mciSetAudioVolume(DeviceName$, volume,tp$="",nr=0)
    PokeInt generic_bank,0,0
    If volume<  0 Then volume=0
    If volume>1000 Then volume=1000
	mciSendString("setaudio " + DeviceName$ + " volume to " + volume,generic_bank,4,0)
    Return PeekInt(generic_bank,0)
End Function


; added by jfk:
Function mciSize(DeviceName$, x,y,w,h)
	mciSendString("put " + DeviceName$ + " destination at " +x+" "+y+" "+w+" "+h,generic_bank,0,0)
End Function


Function mciCapability(DeviceName$, feature$)
    PokeInt generic_bank,0,0
	mciSendString("capability " + DeviceName$ + " "+feature$,generic_bank,4,0)
    Return PeekInt(generic_bank,0)
End Function


Function mciGetDeviceType$(DeviceName$)
    For i=0 To 1001
     PokeByte generic_bank,i,0
    Next
	mciSendString("capability " + DeviceName$ + " device type",generic_bank,100,0)
	ret$=""
	Repeat
 	 pp=PeekByte(generic_bank,c)
     If pp<>0 Then
      ret$=ret$+Chr$(pp)
     EndIf
	 c=c+1
	Until (pp=0) Or (c>1000)
    Return ret$
End Function

Function mciStatus$(DeviceName$,what$)
    For i=0 To 1001
     PokeByte generic_bank,i,0
    Next
	mciSendString("status " + DeviceName$ + " "+what$,generic_bank,100,0)
	ret$=""
	Repeat
 	 pp=PeekByte(generic_bank,c)
     If pp<>0 Then
      ret$=ret$+Chr$(pp)
     EndIf
	 c=c+1
	Until (pp=0) Or (c>1000)
    Return ret$
End Function




; -------------------------------------------------------------------------------------------------------------------------------------
; This function plays the specified mci device.
; 
; Option may be set to the following:
;
;	"fullscreen" - Sets the video to full screen.  Uncompressed video will not play in full screen.
;	"window"	 - Sets the video the play back in it's parent window.
;	"repeat"	 - Restarts playback from the beginning once it reaches the end.
;	"reverse"	 - Specifies that the video plays back backwards.
;
;   "from POSITION"					- Specifies that the video plays back starting at the position specified.  
;	"from POSITION1 to POSITION2"   - Specifies that the video plays from point A to point B.  (Cannot be used with reverse.)
;
; You may specify more than one option at a time if you seperate each with a space.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciPlay(DeviceName$, Option$="")

	If Option$ <> "" 
		mciSendString("play " + DeviceName$ + " " + Option$,generic_bank,0,0)		
	Else
		mciSendString("play " + DeviceName$,generic_bank,0,0)
	EndIf	
	
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function pauses the specified mci device.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciPause(DeviceName$)
	mciSendString("pause " + DeviceName$,generic_bank,0,0)
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function unpauses the specified mci device.
; (May not work with all devices?)
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciResume(DeviceName$)
	mciSendString("resume " + DeviceName$,generic_bank,0,0)		
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function causes the specified mci device to seek to a position and stop.
;
; Position$ =
;	"start"    - Seek to the start of the media.
; 	"end"	   - Seek to the end of the media.
; 	"POSITION" - Seek to a specified position in the media, where POSITION is frames, milliseconds, or whatever the 
; 				 currently selected time format is.  Ie: "635"
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciSeek(DeviceName$, Position$)
	mciSendString("seek " + DeviceName$ + " to " + Position$,generic_bank,0,0)
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function causes the specified mci digital-video device to step a specified number of frames forward or back.
; Frames can be a positive or negative value.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciStep(DeviceName$, Frames)
	mciSendString("step " + DeviceName$ + " by " + Str$(Frames),generic_bank,0,0)
End Function


; -------------------------------------------------------------------------------------------------------------------------------------
; This function stops a device.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciStop(DeviceName$)
	mciSendString("stop " + DeviceName$,generic_bank,0,0)
End Function


; added by jfk
Function mciClose(DeviceName$)
	mciSendString("close " + DeviceName$,generic_bank,0,0)
End Function

Function mciCapture(DeviceName$,savepath$) ; didn't work for some reason
    If savepath$<>""
	 mciSendString("capture " + DeviceName$+" as "+savepath$,generic_bank,4,0)
	EndIf
End Function



; -------------------------------------------------------------------------------------------------------------------------------------
; This function opens a configuration window for the specified mci device.
; -------------------------------------------------------------------------------------------------------------------------------------
Function mciConfigure(DeviceName$)
	mciSendString("configure " + DeviceName$,generic_bank,0,0)
End Function
