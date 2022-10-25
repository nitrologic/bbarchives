; ID: 3253
; Author: Casaber
; Date: 2016-02-04 17:17:26
; Title: Play Audio using MCI
; Description: Playing Audio usinv MCI with BMax

rem
   _/\/\/\/\/\____/\/\___________________________
    _/\/\____/\/\__/\/\____/\/\/\______/\/\__/\/\_ 
   _/\/\/\/\/\____/\/\________/\/\____/\/\__/\/\_  
  _/\/\__________/\/\____/\/\/\/\______/\/\/\/\_   
 _/\/\__________/\/\/\__/\/\/\/\/\________/\/\_    
___________________________________/\/\/\/\___  

 MID, OGG, MP3, WMA of any length

 Get the ability to REPEAT, READ position, SET position, get LENGTH etc.

 For Windows only (Bmax)

 ' This is a simple and working Bmax example of MCI usage'

endrem
' ---------------------------------------------------------------------

Extern "win32"
	Function mciSendStringA(cmd$z,resultbuffer:Byte Ptr=Null,buffersize=0,hwndcallback=0)
End Extern

' Init
Graphics 640,480
sound$ = "birds.mp3" ' Any kind of media goes here.
sound$=chr$(34)+sound$+chr$(34)

' Example
mci "open " + sound$ + " type mpegvideo alias sploit" ' Notice mpegvideo is forced as type
mci "play sploit from 0 repeat"

waitkey

'Quick reference of some practical examples of MCI commands :

'mci "set sploit time format ms" ' Use milliseconds instead of PPQN.
'Print "Song length is " + mci("status sploit length") 
'Print "position is " + mci("status sploit position")
'mci "seek sploit to 20" ' Remember a SEEK is needed after a PLAY.
'mci "play sploit"
'mci "pause sploit"
'mci "resume sploit"
'mci "stop sploit"
'mci "close sploit"
’mci "setaudio sploit volume to 50”

len = mci("status sploit length")
pos = mci("status sploit position")

Function mci:String(msg$)
	Local answer$
	Local retmsg:Byte Ptr = MemAlloc(128)
	Local a = mcisendstringa(msg$,retmsg,128)
	answer$ = String.FromCString(retmsg)
	MemFree(retmsg)
	Return answer$
EndFunction

’ An example hoe to pack them inside the exe:
’ incbin ”birds.mp3"
’ CopyFile ”incbin::birds.mp3”,”birds.mp3”
