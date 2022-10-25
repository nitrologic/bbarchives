; ID: 1625
; Author: John Blackledge
; Date: 2006-02-23 06:01:35
; Title: Model's Texture Names
; Description: Get Texture Filenames from Model

;-----------------------------------
Function CopyTextures(f$,destdir$)
;-----------------------------------
Local m$, z, md$, hfile
Local index, index1, index2, index3, lastindex
Local modelpath$

	If f$<>""
		modelpath$ = FullpathGetPath$(f$) ; <- substitute your own path 

		hfile = ReadFile(f$)
		While Not Eof(hfile)
			If Right$(Upper$(f$),2)=".X"
				m$ = ReadLine$(hfile); implements 0D,0A
			Else
				m$ = ""
				Repeat
					If Eof(hfile) Then Exit
					b = ReadByte(hfile)
					If b = 0 Then Exit
					m$ = m$ + Chr$(b)
				Until b = 0 And m$<>""
				If Eof(hfile) Then Return
			EndIf

		 If m$<>""
			lastindex = 0 : firstindex = 0
			index = 0 : index1 = 0 : index2 = 0 : index3 = 0
			index1 = Instr(Upper$(m$),".BMP") : index = index1
			index2 = Instr(Upper$(m$),".JPG") : index = index2
			index3 = Instr(Upper$(m$),".PNG") : index = index3
			If index1 > 0 Then index = index1
			If index2 > 0 Then index = index2
			If index3 > 0 Then index = index3
			If index1<>0 Or index2<>0 Or index3<>0	;Print m$
				For z = 1 To 4
					md$ = Mid$(m$,index+z,1)
					val = Asc(md$)
					If val<=0 Or val=34 Or val=Asc("\") Or val>127
						lastindex = index+z-1
						z = 4
					EndIf
				Next
				m$ = Left$(m$,lastindex)	;Print m$
				firstindex = 1 ; in case no exit coz its a whole string.
				For z = Len(m$) To 1 Step -1
					md$ = Mid$(m$,z,1)
					val = Asc(md$)
					If val<=0 Or val=34 Or val=Asc("\") Or val>127
						firstindex = z+1
						z = 0
					EndIf
				Next
				m$ = Mid$(m$,firstindex) ; Print m$
				If m$<>""
					sour$ = modelpath$+"\"+m$
					CopyFile sour$,destdir$+"\"+m$
				EndIf
			EndIf
		 EndIf
		
		Wend
		CloseFile(hfile)
	EndIf

End Function
