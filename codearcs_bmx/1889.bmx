; ID: 1889
; Author: Jesse
; Date: 2006-12-25 05:54:29
; Title: font to defdata
; Description: converts characters to data

Function bittext(capital$,name$)
	CreateFile(name$)
	Local file:TStream = OpenFile(name$)
	If file = Null End
	For Local i% = 0 To capital.length-1
		Cls
		Local width% = TextWidth(Chr(capital[i]))
		Local Height% = TextHeight(Chr(capital[i]))
		DrawText Chr(capital[i]),0,0
		Local pixmap:TPixmap = GrabPixmap(0,0,width,height)
		WriteString file,"defdata "+String(width)+" ,"+String(height)+"'      "+Chr(capital[i])+"~r~n"
		For Local y% = 0 To height -1
			Local text$="Defdata "
			For Local x% = 0 To width-1
				Local color% =ReadPixel(pixmap,x,y) & $fff
				If x Then text = text+","
				If color Then text=text+"1" Else text = text+"0"
				SetColor 200,100,0
				If color DrawRect x*(5),y*(5),4,4
			Next
			text = text +"~r~n"
			WriteString file,text
		Next
		Flip()
		Delay(50)
	Next
	CloseFile file
	Print "program saved as"+name
End Function

Graphics 1024,768,32
Local letters$= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
Local numsymb$ = "0123456789!@#$%&()-+*/^_="

bittext(letters,"letters.bmx")
bittext(numsymb,"numsymb.bmx")
