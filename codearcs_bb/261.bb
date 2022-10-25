; ID: 261
; Author: King Dave
; Date: 2002-03-07 12:42:25
; Title: Streaming sounds
; Description: Did you know you can stream sounds with PlayMusic(file$)?

;Title:		Streaming sounds
;Version:	1.00

;Author:	David Blasdell (aka. King Dave)
;	Email:	admin@netcharger.co.uk
;	Web:	www.netcharger.co.uk or www.freeworldonline.co.uk

;Description:
;	Example source code to demonstrate how to stream sound files using PlayMusic(filename$)
;	(ie. playing the file before the entire file is downloaded)
;	Transfers a file from somewhere on your hard disk to a tempory file, while it is playing
;	Could be used to play music while it downloads off of a website

;Notes:
;	If you enter too lower bytes per second value, the music may stop playing before the whole file is transfered


Graphics 640,480,16,2			;Set graphics to 640x480x16 window mode
SetBuffer BackBuffer()			;Set buffer to back buffer
SetFont LoadFont("Arial",14)	;Use Arial fsize 14 as the font

filename$=Input$("Enter location of the file to be streamed: ")				;Get file to use (most things that can be played by PlayMusic() should work)
bps=Int(Input$("Enter how many bytes to copy per second (eg. 10000): "))/4	;Will transfer this number of bytes to the stream every second

file_in=ReadFile(filename$)													;Read input file
If Not file_in Then RuntimeError "Failed to read from '"+filename$+"'!"		;If failed to read from file, say so
fsize=FileSize(filename$)													;Retrieve total fsize of file
bytes=fsize																	;Used to count down bytes left to read

;Retrieves file extension
For a=Len(filename$) To 1 Step -1
	If Mid$(filename$,a,1)="." Then ext$=Right$(filename$,Len(filename$)-a):Exit
Next

temp_file$=SystemProperty("tempdir")+"stream."+ext$							;Create a tempory filename to use
file_out=WriteFile(temp_file$)												;Write to a tempory file
If Not file_out Then RuntimeError "Failed to write to '"+temp_file$+"'!"	;If failed to write to file, say so

bank=CreateBank(bps*8)				;Create a bank big enough for 2xbps worth of bytes

ReadBytes(bank,file_in,0,bps*8)		;Read bytes from input stream
bytes=bytes-(bps*8)					;Reduce byte count
WriteBytes(bank,file_out,0,bps*8)	;Write bytes to output stream (need a fair amount or bytes ready before starting PlayMusic(), this sends 2 seconds worth)

ResizeBank(bank,bps)				;Reduce bank fsize to 1/4 of the bytes per second speed

music=PlayMusic(temp_file$)			;Start playing the stream

While ChannelPlaying(music)=1				;Main loop, repeats while music is still playing
	If KeyHit(1) Then End										;End the program if escape is hit
	If stream_delay<MilliSecs() Then If Eof(file_in)=0			;Every 250 milliseconds more data will be written to the output file
		If bytes<bps Then a=bytes Else a=bps								;Checks how much can be read	
		ReadBytes(bank,file_in,0,a)											;Reads bytes from the input file
		WriteBytes(bank,file_out,0,a)										;Writes bytes to the output file
		bytes=bytes-a														;Subtracts amount moved from byte counter
		stream_delay=MilliSecs()+250										;Waits for 250 milliseconds
		p#=100-((Float#(bytes)/Float#(fsize))*100)							;Calculates percentage of file sent
		DrawProgress("Streaming... ("+(fsize-bytes)+" of "+fsize+")",p#)	;Update screen
	Else
		If Not done Then DrawProgress("File fully transfered",100):done=1	;Displays once the file has been fully transfered for playing
	EndIf
Wend

StopChannel music		;Stop music if still playing

CloseFile file_in		;Close input file
CloseFile file_out		;Close output file
DeleteFile temp_file$	;Delete the tempory file

;Works out wether the sound was fully played or not
Cls
Color 255,255,255
If bytes=0
	Text 5,5,"Entire file was streamed succesfully"
Else
	Text 5,5,"Only some of the file was streamed, try giving a higher bytes per second value"
EndIf
Flip

FlushKeys()
WaitKey

End

;Draw a progress with description and percentage
Function DrawProgress(t$,p#)
	w=GraphicsWidth()
	Cls
	Color 255,255,255
	Text 2,2,t$
	Color 255,0,0
	Rect 9,20,w-18,16,0
	Color 0,150,0
	Rect 10,21,(p#/100)*(w-20),14,1
	Color 0,0,255
	Text w/2,28,Int(Floor#(p#))+"%",1,1
	Flip 0
End Function
