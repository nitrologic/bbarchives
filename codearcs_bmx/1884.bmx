; ID: 1884
; Author: ninjarat
; Date: 2006-12-22 07:02:57
; Title: High Score Type
; Description: Simple high scores file handler, and an unfinished method for sorting them.

Type scorelist
	Field list:scoreitem[100]
	Global listfile:TStream
	
	Method init()
		For j=0 To 99
			list[j]=New scoreitem
		Next
		open()
	End Method
	
	Method open()
'		Print "openning score file"
		listfile=OpenFile("diamonds.highscores")
		If Not listfile Then
			createhiscoresfile()
			listfile=OpenFile("diamonds.highscores")
		End If
		close()
	End Method
	Method load()
'		Print "loading score data from file stream"
		listfile=OpenFile("diamonds.highscores")
		Rem      This is not neccecary
			If Not listfile Then
				init()
				save()
			End If
		End Rem
		Local strlen:Byte
		For j=0 To 99
			If Not Eof(listfile) Then
				list[j].score=listfile.ReadInt()
				strlen=listfile.ReadByte()
				list[j].name=listfile.ReadString(strlen)
			End If
		Next
		close()
	End Method
	Method save()
'		Print "saving score data to file stream"
		listfile=OpenFile("diamonds.highscores")
		For j=0 To 99
			listfile.WriteInt list[j].score
			listfile.WriteByte Len(list[j].name)
			listfile.WriteString list[j].name
		Next
		close()
	End Method
	
	Method createhiscoresfile()
'		Print "no high scores file found; making new one"
		CreateFile("diamonds.highscores")
		listfile=OpenFile("diamonds.highscores")
		Local examplescores$[]=["BILL","FRANK","JOE","ANN","ROBBIE"]
		For j=0 To 4
			listfile.WriteInt (5-j)*1000
			listfile.WriteByte Len(examplescores[j])
			listfile.WriteString examplescores[j]
		Next
		For j=5 To 99
			listfile.WriteInt list[j].score
			listfile.WriteByte Len(list[j].name)
			listfile.WriteString list[j].name
		Next
		close()
	End Method
	
	Method close()
		listfile.Close
	End Method
	
	Method sort()
	End Method
End Type
