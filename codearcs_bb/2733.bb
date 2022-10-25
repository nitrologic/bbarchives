; ID: 2733
; Author: schilcote
; Date: 2010-06-22 02:19:01
; Title: WAV Examiner
; Description: WAVs are easy to handle!

file$=RequestFile$("Choose a WAV file","wav",False)

Graphics 800,600

fil=OpenFile(file$)

While Not Eof(fil)

	num=ReadInt(fil)
	Print num
	oldy=y
	y=num/10000000+200
	Plot (x,y)
	Line(x-1,oldy,x,y)
	Flip
	
	x=x+1
	If x>800 Then
		Cls
		x=0
	EndIf

Wend
 


WaitKey
End
