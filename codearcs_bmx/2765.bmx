; ID: 2765
; Author: MacSven
; Date: 2010-09-11 12:31:45
; Title: Space Writer
; Description: Little Demo for a Space Writer

' You need the odd.AsciiFont module

Import odd.Asciifont

Graphics 800,600

Global text$[21]
Global lines
Global background:TPixmap=CreatePixmap(800,600,PF_RGBA8888)

Local font:TAsciiFont=TAsciiFont.Load("asciifont.png",8,8,ASCII_PADDED|ASCII_EXTENDED)
Global ende

Delay 2000

Repeat
	SetScale 2,2
	SetColor 64,64,255
'	text$="THIS IS A SPACE WRITER WRITTEN in"+Chr$(13)+"BLITZMAX IN DREAMS OF OLD SCHOOLDEMOS"
	text$[0]="THIS IS A SPACE WRITER WRITTEN IN"
	text$[1]="BLITZMAX IN DREAMS OF OLD SCHOOLDEMOS"
	text$[2]="AND IT LOOKS VERY COOL"
	text$[3]="AND IT WORKS!"
	text$[4]=""
	text$[5]=""
	text$[6]="THANX TO ALL THE BLITZMAX DEVELOPER"
	text$[7]="FOR THIS GREAT PROGRAMMING LANGUAGE"
	text$[8]=""
	text$[9]="THIS IS FREEWARE, USE IT OR NOT"
	text$[10]="BYE"
	text$[11]=""
	text$[12]=""
	text$[13]=""
	text$[14]=""
	text$[15]=""
	text$[16]=""
	text$[17]=""
	text$[18]=""
	text$[19]=""
	text$[20]=""
	If ende=0 Then
		For lines=0 To 20
			For test=0 To Len(text$[lines])+1
				Cls
				DrawPixmap (background,0,0)

				If test=Len(text$[lines])+1
					If KeyHit(key_escape) Then End
					font.draw Mid$(text$[lines],1,test)+Chr$(32),0,100+lines*16
					Flip
					Delay 50
				Else
					font.draw Mid$(text$[lines],1,test)+Chr$(128),0,100+lines*16
					If KeyHit(key_escape) Then End
				EndIf
				Flip
				Delay 50
'				Flip

			Next
			background=GrabPixmap(0,0,800,600)
		Next
	EndIf
	Cls
	background=GrabPixmap(0,0,800,600)
Until KeyDown(KEY_ESCAPE) Or AppTerminate()
