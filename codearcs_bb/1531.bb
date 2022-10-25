; ID: 1531
; Author: Andy
; Date: 2005-11-12 12:44:06
; Title: Threading
; Description: Emulate threading so that you can make animated loading screens etc.

Global thread_branch=0
Global thread_count=0
Global thread_percent=0
Global filecount
Global file_in$="image.img"
Global file_out$="image.img"
Global filein
Global fileout
Global x=0
Global y=0

; Create the file you are loading later
savefile(file_out$)

Graphics 800,600 

; Setup imagebuffer
Global img=CreateImage(640,480)

; Setup mousepointer
Global mouseimg=CreateImage(10,10)
SetBuffer ImageBuffer(mouseimg)
Color 250,250,250 
 For q= 0 To 9
 Plot q,q
 Plot 0,q
 Next
 Color 5,5,5 
 For q= 1 To 9
 Plot q+1,q
 Plot 1,q
 Next

; setup doublebuffering
SetBuffer BackBuffer() 

; Starting values
thread_branch=1

; main loop
While Not KeyHit(1) 

; branch to file loading
	If thread_branch=1 Then
	 loadfile(file_in$)
    EndIf

; draw on backbuffer
SetBuffer BackBuffer()
Cls
Color 255,255,255
Text 360,230,"Loading..."

; draw the slider
	If thread_branch=1 Then
     thread_percent=((filecount*100)/FileSize(file_in$))
     Color 255,255,255
     Rect 150,250,500,25,0
     Rect 150,250,thread_percent*5,25,1
    EndIf

; Draw the image when loaded
	If thread_branch=2 Then
	 DrawBlock img, 250,200
     Text 360,450,"Esc to quit"
    EndIf

; draw mouse pointer
DrawImage mouseimg, MouseX(), MouseY() 
   
Flip False
Wend 

End



; saving file 
Function savefile(file_out$)
fileout = WriteFile(file_out$) 
For x=0 To 319
	For y=0 To 199
	WriteByte( fileout, Rnd(0,255)) 
	WriteByte( fileout, Rnd(0,255)) 
	WriteByte( fileout, Rnd(0,255)) 
	Next
Next
CloseFile( fileout )
End Function 

; loading file in logical steps
Function loadfile(file_in$)

; step one - open file for reading
If thread_count=0 Then
	filein = ReadFile(file_in$) 
	thread_count=1
    filecount=0
    x=0
    y=0
EndIf

; step two - read data and write to imagebuffer
If thread_count =1 Then	
	y=y+1

	If y=200 Then
		x=x+1
		y=0
	EndIf
	Read1 = ReadByte( filein )
	Read2 = ReadByte( filein )
    Read3 = ReadByte( filein )
        filecount=filecount+3

; draw to imagebuffer
    SetBuffer ImageBuffer(img)
	Color read1,read2,read3 
	Plot x,y
; check for end of file	
	If Eof(filein) Then
		thread_count=2
	EndIf
EndIf

; step three - close file
If thread_count=2 Then
	CloseFile( filein )
	thread_branch=2
EndIf
End Function
