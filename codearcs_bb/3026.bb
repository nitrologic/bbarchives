; ID: 3026
; Author: Andy_A
; Date: 2013-02-04 14:18:14
; Title: Any File to b64 BB data statements
; Description: Post small images with your source

;Any file to base64 data statements by Andy_A
;2013.01.30

AppTitle "Any file to b64 data statements"

;==============================================================================
Const sizeOfLine = 60 ;<-- change number of bytes encoded per line of data here


Const loader = 1 ;<-- change to zero for data output ONLY, else add loader code


BlitzPlus = 1 ;<-- Change to zero if using Blitz Classic or Blitz3D


;Change 'Restore' label for data statements here
dataLabel$ = "imgData" ;<-- ** DO NOT **  include leading period/stop
;==============================================================================

If BlitzPlus Then
	file$ = RequestFile("Load file to convert to data...","*",False)
	If file$ = "" Then RuntimeError "File Load aborted..."
Else
	file$ = "blocks.png" ;<-- blocks from Blitzanoid, change to your file here (B3D)
End If

;Read the file to convert
filein = ReadFile(file$)
;Create and fill bank with file data	
size = FileSize(file$)

;name the output file
If BlitzPlus Then
	datafile$ = RequestFile("Save data as...","bb",True,"dataFile.bb")
	If datafile$ = "" Then RuntimeError "File save aborted."
Else
	datafile$ = "blocks_png.bb" ;<-- change to your output name here (B3D)
End If
fileOut = WriteFile(datafile$)
fullLines = Floor(size/sizeOfLine )
rm = size - (fullLines * sizeOfLine )
q$ = Chr$(34)
char$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
Dim k(63)
For i = 0 To 63
	k(i) = Asc(Mid(char$,i+1,1))	
Next

st = MilliSecs()
If loader = 1 Then
	WriteLine(fileOut,"Restore "+dataLabel$+":Read p$,s,t:o=WriteFile(p):n=Floor(s/t):If(n*t)<s Then n=n+1")
	WriteLine(fileOut,"For i=1To n:Read d$:z$=z+d64(d):For j=1To Len(z)")
	WriteLine(fileOut,"WriteByte(o,Asc(Mid(z,j,1))):Next:z="+q$+q$+":Next:CloseFile o:End")
	WriteLine(fileOut,"Function d64$(d$): s$="+q$+q$+":rm=0:L=Len(d):If Right(d,1)="+q$+"="+q$+"Then rm=2")
	WriteLine(fileOut,"r$="+q$+"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"+q$)
	WriteLine(fileOut,"If Right(d,2)="+q$+"=="+q$+"Then rm=1")
	WriteLine(fileOut,"If rm>0Then m=L-5Else m=L-1")
	WriteLine(fileOut,"For i=1To m Step 4:w=Instr(r,Mid(d,i,1))-1:x=Instr(r,Mid(d,i+1,1))-1")
	WriteLine(fileOut,"y=Instr(r,Mid(d,i+2,1))-1:z=Instr(r,Mid(d,i+3,1))-1")
	WriteLine(fileOut,"a=(w Shl 2)+((x And 48)Shr 4):b=((x And 15)Shl 4)+((y And 60)Shr 2)")
	WriteLine(fileOut,"c=((y And 3)Shl 6)+z:s=s+Chr(a)+Chr(b)+Chr(c):Next:If rm=1Then")
	WriteLine(fileOut,"w=Instr(r,Mid(d,L-3,1))-1:x=Instr(r,Mid(d,L-2,1))-1")
	WriteLine(fileOut,"a=(w Shl 2)+((x And 48)Shr 4):s=s+Chr(a):EndIf:If rm=2Then")
	WriteLine(fileOut,"w=Instr(r,Mid(d,L-3,1))-1:x=Instr(r,Mid(d,L-2,1))-1")
	WriteLine(fileOut,"y=Instr(r,Mid(d,L-1,1))-1:a=(w Shl 2)+((x And 48) Shr 4)")
	WriteLine(fileOut,"b=((x And 15) Shl 4)+((y And 60)Shr 2):s=s+Chr(a)+Chr(b):EndIf")
	WriteLine(fileOut,"Return s:End Function")
End If

WriteLine(fileout,"."+dataLabel$)
WriteLine(fileout,"Data"+q$+file$+q$+","+size+","+sizeOfLine)
For i = 1 To size-rm-1 Step sizeOfLine
	a1$ = ""
	For j = 1 To sizeOfLine
		a1$ = a1$ + Chr(ReadByte(filein))
	Next
	WriteLine(fileOut, "Data" + q$ + e64(a1$) + q$ )
Next
If rm > 0 Then
	a1$ = ""
	For i = 1 To rm
		a1$ = a1$ + Chr(ReadByte(filein))
	Next
	WriteLine(fileOut, "Data" + q$ + e64(a1$) + q$)
End If

CloseFile(filein)
CloseFile(fileout)
et = MilliSecs()-st
Print "ET: "+et

WaitKey()
End

Function e64$(e$)
	f$ = ""
	g = Len(e$)
	rm = g- Floor(g/3)*3
	For i = 1 To g-rm-1 Step 3
		a = Asc(Mid$(e$,i  ,1))
		b = Asc(Mid$(e$,i+1,1))
		c = Asc(Mid$(e$,i+2,1))
		w = (a And 252) Shr 2
		x = ((a And 3) Shl 4) + ( (b And 240) Shr 4)
		y = ((b And 15) Shl 2) + ( (c And 192) Shr 6)
		z = c And 63
		f$ = f$ + Chr$(k(w)) + Chr$(k(x)) +Chr$(k(y)) +Chr$(k(z))
	Next
	If rm = 1 Then
		a = Asc(Right(e$,1))
		w = (a And 252) Shr 2
		x = (a And 3) Shl 4
		f$ = f$ + Chr(k(w)) + Chr(k(x)) + "=="
	End If
	If rm = 2 Then
		a = Asc(Mid(e$,g-1,1))
		b = Asc(Right(e$,1))
		w = (a And 252) Shr 2
		x = (a And 3) Shl 4
		x = x + ( (b And 240) Shr 4)
		y = (b And 15) Shl 2
		f$ = f$ + Chr(k(w))+Chr(k(x))+Chr(k(y)) + "="
	End If
	Return f$
End Function
