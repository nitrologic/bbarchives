; ID: 556
; Author: Zenith
; Date: 2003-01-21 23:21:32
; Title: RLE compression
; Description: Basic  Run Length Encoding compression for files.

Function RLE_compress(in_file$,out_file$)
	Local temp$
	ifile = ReadFile(in_file)
	ofile = WriteFile(out_file)
	oldbyte = -1
	While Not Eof(ifile)
	
		newbyte = ReadByte(ifile)
		
		If newbyte = oldbyte				; another byte! Lets add it to the list :)
			If rcount = 255					; we have TOO many in the list :)
				WriteByte(ofile,rcount)
				WriteByte(ofile,newbyte)
				rcount = 0
			ElseIf rcount = 0 And Len(temp)>0
				WriteByte(ofile,0)
				WriteByte(ofile,Len(temp))
				For i=1 To Len(temp)
					WriteByte(ofile,Asc(Mid(temp,i,1)))
				Next
				temp=""
				rcount = rcount + 1
			Else
				rcount = rcount + 1
			EndIf
		Else								; new byte type! Lets write off the old byte list	
			If oldbyte>-1
				If rcount>0
					WriteByte(ofile,rcount)
					WriteByte(ofile,oldbyte)
				Else
					If Len(temp)=255
						WriteByte(ofile,0)
						WriteByte(ofile,Len(temp))
						For i=1 To Len(temp)
							WriteByte(ofile,Asc(Mid(temp,i,1)))
						Next
						temp = Chr(oldbyte)
					Else
						temp = temp + Chr(oldbyte)
					EndIf
				EndIf
			EndIf
			oldbyte = newbyte
			rcount = 0
		EndIf
		
	Wend
	
	If rcount>0
		WriteByte(ofile,rcount)
		WriteByte(ofile,oldbyte)
	EndIf
	
	CloseFile(ifile)
	CloseFile(ofile)
End Function

Function RLE_uncompress(in_file$,out_file$)
	ifile = ReadFile(in_file)
	ofile = WriteFile(out_file)
	While Not Eof(ifile)
		rcount = ReadByte(ifile)
		If rcount = 0
			length = ReadByte(ifile)
			For i=1 To length
				WriteByte ofile,ReadByte(ifile)
			Next
		Else
			arg	   = ReadByte(ifile)
			For i=0 To rcount
				WriteByte ofile,arg
			Next
		EndIf
	Wend
	CloseFile(ifile)
	CloseFile(ofile)
End Function
