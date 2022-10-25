; ID: 3189
; Author: Andy_A
; Date: 2015-02-17 14:30:57
; Title: IFS Fractal Viewer 2
; Description: Lot of IFS fractals

This the main program, it requires the folder 'IFS Data'
with all of the parameter files inside (see next post).

For your efforts you will be rewarded with over 100 IFS fractals to view at your leisure.

====================================
If you run it with out the data files you get a nice error 
message asking if you want to report the error to Microsoft.
====================================

Yeah, it's a bit of a kludge but, this way I can post the data with the program and not having to worry about some cloud
drive server going belly up. Resulting in a 404 for the data.



; ===============================================
; Iterated Function System (IFS) Fractal Viewer 2
; Andres Amaya Jr.
; Denver, CO
; 2015.02.15
; ===============================================

AppTitle "IFS Fractals II"
Graphics 1024,768,32,2
SetBuffer BackBuffer()
Dim a#(1),b#(1),c#(1),d#(1),e#(1),f#(1)
Dim p#(1)
Dim rr%(24), gg%(24), bb%(24), hxc%(24)
Dim work$(1,1)
Dim hx8$(3)

Global cx#, cy#, band#, coff#, xMult#, yMult#
Global numRows%, xOffset%, yOffset%
cx = 512.0: cy = 384.0: band = 17.0: coff = 1.0

;Dimension these two arrays to
;exceed number of parameter files
Dim picName$(200), si%(200)

fontVerdana14% = LoadFont("Verdana",14,True)
fontVerdana48% = LoadFont("verdana",48,True,True,True)

;there are currently 135 IFS parameter
;files in this distribution
numFiles = getNames()
Dim name$(numFiles), pos%(numFiles,1)

getLocs(numFiles)
SeedRnd MilliSecs()
For i% = 1 To 2000:j%=Rand(1,100):Next

palette()

Repeat
	Cls
	Color 60,91,175
	SetFont fontVerdana48
	Text 50,14,"IFS Fractal Viewer II"
	SetFont fontVerdana14
	instruct()
	showNames(numFiles)
	Flip
	choice = scan(numFiles)
	If choice <> 0 Then
		Cls
		st = MilliSecs()
		readData(choice,numFiles)
		display(choice)
		et = MilliSecs()-st
		Color 80, 158, 238
		Text 5, 5,picName$(choice)
		;Text 5,25,et+"ms" ;<------ un-comment to display render time
		Flip
		WaitMouse()
	End If
	If KeyHit(1) Then
		FreeFont fontVerdana14
		FreeFont fontVerdana48
		End
	End If
Forever

Flip

WaitMouse()
End 

Function getNames()
	Local folder$, file$, fname$
	Local curDir%, ft%, count%

	folder$ = CurrentDir()+"/IFS Data"
	curDir = ReadDir(folder$)
	Repeat
		file$ = NextFile$(curDir)
		If file$ = "" Then Exit
		ft = FileType(folder$+"/"+file$)
		If ft <> 2 And file$<>"." And file$<>".." Then
			count = count + 1
			;strip path from file
			fname$ = justName$(file$)
			;strip the extension from file
			fname$ = justNameEx$(fname$)
			;store the name for later use
			picName$(count) = fname$
			;check for specific fractals to be rendered differently
			If Left(fname$,2)="fd" Then si(count)=1
			If fname$ = "fracFern" Then si(count)=2
		End If
	Forever
	Return count
	ChangeDir( CurrentDir$() )
End Function

Function getLocs(numFiles)
	Local i%, y1%, y2%, y3%, y4%

	For i = 1 To numFiles
		;store the positions of the display boxes for each column
		If i < 36 Then
			pos(i,0) =  20 : pos(i,1) = y1+87
			y1 = y1 + 19
		End If
		If i > 35 And i < 71 Then
			pos(i,0) = 175 : pos(i,1) = y2+87
			y2 = y2 + 19
		End If
		If i > 70 And i < 106 Then
			pos(i,0) = 330 : pos(i,1) = y3+87
			y3 = y3 + 19
		End If
		If i > 105 Then
			pos(i,0) = 485 : pos(i,1) = y4+87
			y4 = y4 + 19
		End If
	Next
End Function

Function instruct()
	Local t$
	wrapText(870,89, "Instructions",135, 0,0,0, 255,224,200,1)
	
	t = "Click on one of the selections to display a particular fractal. /n/ "
	t=t+"After the fractal has been rendered, click anywhere on the fractal screen to get back to this "
	t=t+"screen. /n/ Click the [Exit] button to terminate.
	wrapText(640,114, t,365, 0,0,0, 255,224,200)
	
	wrapText(870,250, "Attributions:",135, 0,0,0, 208,208,208, 1)
	
	t = "Fractals beginning with 'fd' are from 'F-Design v3.08' (DOS) by Doug Nelson. "
	t=t+"These fractals are unique in that he centered the fractal on the screen using the parameters. "
	t=t+"This means that ONLY scaling is applied to fit perfectly on a 1024x768 window. /n/ "

	t=t+"Fractals begining with 'frac' are from Windows version of FractInt: 'WinFract_20-04-p09'. /n/ "
	t=t+"Fractals Bmc0884x, DC022b6c, Sq0002d, Sq441, and Td081 are in the same WinFract_20-04-p09 distribution "
	t=t+"by Anthony Hanmer (frac205.ifs) /n/ "

	t=t+"Fractals beginning with 'rs'are from an old QB listing 'RS-IFS-Programm von Robert Seidel (7/1995)' "
	t=t+"Most of these are unique, but some were 'from FractInt' /n/ "

	t=t+ "Fractals beginning with 'tcl' are from wiki.tcl.tk. Many of these are repeats but, the unique thing about "
	t=t+"these fractals are that the probabilities are always equal. This also has the effect of not being as "
	t=t+"detailed versus using weighted probabilities as do the other sources. /n/ "

	t=t+ "Fractals fractIntDragon, Levy Dragon, and Heighway Dragon are Python recipes from www.activestate.com /n/ "

	t=t+ "'wikipediaFern' is from Wikipedia.com /n/ 'ifsMaple' is from www.hiddendimension.com"

	wrapText(640,275, t,365, 0,0,0, 208,208,208)

End Function

Function showNames(numFiles%)
	Local i%

	For i = 1 To numFiles
		drawbox(picName$(i),pos(i,0),pos(i,1),135,17, 40,61,139, 255,255,208)
	Next
	drawbox("Exit",986,5,33,19, 255,0,0, 255,255,255)
End Function

Function scan(numFiles%)
	Local mx%, my%, i%, choice%

	WaitMouse()

	If MouseHit(1) Then
		mx = MouseX()
		my = MouseY()
		For i = 1 To numFiles
			If pnr(mx,my,pos(i,0),pos(i,1),135,17) Then
				hilite(pos(i,0),pos(i,1),135,17,255,160,32)
				Flip
				Delay 200
				FlushMouse()
				choice = i
				Exit
			End If
		Next
		If pnr(mx,my,986,5,33,19) Then
			hilite(986,5,33,19,255,255,255)
			Flip
			Delay 300
			FreeFont fontVerdana14
			FreeFont fontVerdana48
			End
		End If
		Return choice
	End If
	If KeyHit(1) Then
		FreeFont fontVerdana14
		FreeFont fontVerdana48
		End
	End If
End Function

Function readData(choice%, numFiles%)
	Local fileName$, in$
	Local fin%, i%

	If choice <1 Or choice>numFiles Then Return

	fileName$ = "IFS Data\"+picName$(choice)+".ifs"
	fin = ReadFile(fileName$)
	in$ = ReadLine(fin)
	numRows = Word$(in$,1)
	Dim a(numRows), b(numRows), c(numRows)
	Dim d(numRows), e(numRows), f(numRows)
	Dim p(numRows)
	xMult = Word$(in$,2)
	yMult = Word$(in$,3)
	xOffset = Word$(in$,4)
	yOffset = Word$(in$,5)
	For i = 1 To numRows
		in$ = ReadLine(fin)
		a(i) = Word$(in$,1)
		b(i) = Word$(in$,2)
		c(i) = Word$(in$,3)
		d(i) = Word$(in$,4)
		e(i) = Word$(in$,5)
		f(i) = Word$(in$,6)
		p(i) = Word$(in$,7)
		p(i) = p(i) + p(i-1)
	Next
	CloseFile(fin)
End Function

Function display(choice%)
	Local k%, j%, i%, xp%, yp%, clr%
	Local xalt#, yalt#, x#, y#, pct#

	xalt=0.5 : yalt=0.5
	LockBuffer()
	For k = 1 To 10000
		For j = 1 To 5
			pct=Rnd(0.0, 1.0)
			For i = 1 To numRows
				If pct <= p(i) Then Exit
			Next
			If i > numRows Then i = numRows
			x = a(i) * xalt + b(i) * yalt + e(i)
			y = c(i) * xalt + d(i) * yalt + f(i)
			xp = Int(x*xMult) + xOffset 

			If si(choice)=1 Then
				;image doesn't require inversion
				yp=Int(y*yMult)+yOffset
			Else
				;yup, invert the image
				yp = (768-Int(y*yMult)) + yOffset
			End If
			If si(choice)=2 Then
				clr = Int(yp/32)
			Else
				clr = Int(Sqr((cx-xp)*(cx-xp) + (cy-yp)*(cy-yp))/band + coff) Mod 24
			End If
			If xp>=0 And xp<1024 And yp>=0 And yp<768 Then
				WritePixelFast(xp,yp,hxc(clr))
			End If
	        xalt = x : yalt = y
    	Next
	Next
	UnlockBuffer()
End Function

Function justName$(fileName$)
;=====================================================================
; Strip full path info from fileName$ - only name of file+ext is
; returned. If fileName$ not found - return null string
;=====================================================================
	Local nPos%, i%, lastPos%

	If fileName$ <> "" Then
		nPos = 1
		For i = 1 To 99
			nPos = Instr(fileName$,"\",nPos)
			If nPos <> 0 Then
				lastPos = nPos + 1
				nPos = lastPos
			Else
				;No more back whacks EXIT the For...Next loop
				Exit
			End If
		Next
		Return Right(fileName$, Len(fileName$)-lastPos+1) 
	Else
		Return ""
	End If
End Function 

Function justNameEx$(filename$)
;=====================================================================
; Strips file extension from fileName$ - only name of file without file
; extension is returned. If filename not found - return null string
;=====================================================================
	Local nPos%, i%, lastPos%

	;PART ONE: Remove any path info from filename$
	If fileName$ <> "" Then
		nPos = 1
		For i = 1 To 99
			nPos = Instr(fileName$,"\",nPos)
			If nPos <> 0 Then
				lastPos = nPos + 1
				nPos = lastPos
			Else
				;No more back whacks. EXIT the For...Next loop
				Exit
			End If
		Next
		fileSansPath$ = Right(filename$,Len(filename$)-lastPos+1)
	Else
		fileSansPath$ = ""
		Return ""
	End If
	;PART TWO: If fileSansPath$ is NOT null,
	;the extract file name w/o file extension
	If fileSansPath$ = "" Then Return
	dpos% = Instr(filename$,".",1)
	strLen% = Len(filename$)
	If dpos Then
		Return Left(filename$, strLen-(strLen-dpos) -1 )
	Else
		Return filename$
	End If
End Function

Function Word$(string2Chk$, n, delimiter$=",")
	Local count%, findDelimiter%, position%, current$

	count = 0
	findDelimiter = 0
	position = 1
	If n > 0 Then
		string2Chk$  = Trim(string2Chk$)
		Repeat
			findDelimiter% = Instr(string2Chk$,delimiter$,position)
			If findDelimiter <> 0 Then
				current$ = Mid$(string2Chk$,position,findDelimiter-position)
				count = count + 1
				position = findDelimiter + 1
				If count = n Then findDelimiter = 0
			End If
		Until findDelimiter = 0
		If (count < n) And (position <= Len(string2Chk$)) Then
			current$ = Mid$(string2Chk$,position, Len(string2Chk$) - position+1)
			count = count + 1
			If count < n Then current$ = ""
		End If
	End If
	Return current$
End Function

Function wrapText%(x%, y%, msg$, wide%, fr%, fg%, fb%, br%, bg%, bb%, center% = 0)
;====================================================================================
; This function wraps text inside of a rounded rectangle with the option to center
; the wrapped text within the rounded rectangle.
;====================================================================================
; x,y = upperleft coordinates of the rounded rectangle
; msg$ = text string that you wish to place in rounded rectangle
; wide = maximum width of button in pixels
; fr, fg, fb = text color (RGB triplet)
; br, bg, bb = button color (RGB triplet)
; center = when 'center' = 1 then each line of text is centered
;		the default setting is zero. (no centering)
;====================================================================================
;NB: placing /n/ in your text will insert a new line at that position.
;	If at the beginning of message string must be in form of "/n/{space}blah blah"
;	If in middle of message string must be in form of "blah{space}/n/{space} blah"
;	If at the end of message string must in form of "blah blah{space}/n/"
;====================================================================================
	Local rad%, initX%, initY%, txtHigh%, count%, nxtWord$, flag%, txtCenter%
	Local tLine$, temp$, lineCount%, boxWide%, boxHigh%, pad%, tLen%

	Dim work$(100,1)
	pad = 10
	rad = 7  ;radius of rounded corner - values 2 To 26 recommended
	diam = rad + rad ;diameter of circles used to make rounded corners
	initX = x : initY = y
	txtWide = wide - pad*2
	txtHigh = StringHeight(msg$) ;height of current font in pixels
	x = x : y = y + pad
	Repeat
		count = count + 1
		nxtWord$ = Word$(msg$, count, " ")
		If Lower(Trim(nxtWord$)) = "/n/" Then newLine = 1
		tLine$ = temp$
		If Word$(msg$,count+1," ") = "" Then
			temp$ = temp$ + nxtWord$
		Else
			temp$ = temp$ + nxtWord$ + " "
		End If
		If (StringWidth(temp$) >= txtWide) Or (newLine = 1) Then
			tLine$ = Trim(tLine$)
			lineCount = lineCount+1
			work$(lineCount,0) = Str(x)+" "+Str(y)
			work$(lineCount,1) = tLine$
			If newLine = 1 Then
				newLine = 0
				count = count + 1
				lineCount = lineCount + 1
				y = y + txtHigh
			End If
			y = y + txtHigh
			temp$ = ""
			count = count-1
		End If
	Until nxtWord$ = ""
	tLine$ = Trim(tLine$)
	If work$(lineCount,1) <> tLine$ Then
		lineCount = lineCount + 1
		work$(lineCount,0) = Str(x)+" "+Str(y)
		work$(lineCount,1) = tLine$
	End If
	boxWide = wide
	boxHigh = lineCount * txtHigh + pad*2
	Color br, bg, bb
	Oval initX, initY, diam, diam, True
	Oval initX, initY+boxHigh-diam-1, diam, diam, True
	Oval initX+boxWide-diam-1, initY, diam, diam, True
	Oval initX+boxWide-diam-1, initY+boxHigh-diam-1, diam, diam, True
	Rect initX, initY+rad, boxWide-1, boxHigh-diam-1, True
	Rect initX+rad, initY, boxWide-diam-1, boxHigh-1, True
	Color fr, fg, fb
	For i = 1 To lineCount
		x = Word$(work$(i,0),1," ")
		y = Word$(work$(i,0),2," ")
		If center = 1 Then
			tLen = StringWidth(work$(i,1))
			txtCenter = (boxWide - tLen) Shr 1
			x = x + txtCenter
		Else 
			x = x + pad
		End If
		Text x, y, work$(i,1)
	Next
	Dim work$(1,1)
End Function

Function drawBox (msg$,x%,y%,w%=168,h%=17,rbox%=255,gbox%=255,bbox%=255, rtext%=0,gtext%=0,btext%=0)
	Color rbox,gbox,bbox
	Rect x,y,w,h,True
	Color rtext,gtext,btext
	Text x+3,y+2,msg$
End Function

Function hilite(x,y,w,h,r,g,b)
	Color r,g,b
	Rect x-2,y-2,w+4,h+4,False
	Rect x-1,y-1,w+2,h+2,False
End Function

Function pnr(px, py, rx, ry, rw, rh)
;====================================================================================
;   Function "Point In Rectangle"
;====================================================================================
; This function checks to see if the point (px,py) is within the specified rectangle.
;
; If the point is inside the rectangle a value of 1 is returned.
;
; If the point is NOT inside the rectangle a value of 0 (zero) is returned.
;====================================================================================
; px = the X coord of the point in question
; py = the Y coord of the point in question
; rx = Upper  Left X coord of rectangle
; ry = Upper  Left Y coord of rectangle
; rw =  width of rectangle
; rh = height of rectangle
;====================================================================================
    Return ((px>=rx) And (px<=(rx+rw-1)) And (py>=ry) And (py<=(ry+rh-1)))
End Function

Function h2d$(hx$)
	Local hi%, lo%
	
	hx$ = Upper(hx$)
	hi = Asc( Left(hx$,1))-48
	lo = Asc(Right(hx$,1))-48
	If hi>10 Then hi=hi-7
	If lo>10 Then lo=lo-7
	Return hi Shl 4 Or lo
End Function

Function palette()
	Local i%, j%, count%
	Local triplet$
	hx8$(1)="0000FF4000FF7D00FFBE00FFFF00FFFF00BEFF007DFF0040"
	hx8$(2)="FF0000FF4000FF7D00FFBE00FFFF00BEFF007DFF0040FF00"
	hx8$(3)="00FF0000FF4000FF7D00FFBE00FFFF00BEFF007DFF0040FF"
	count = 0
	For i = 1 To 3
		For j = 1 To 43 Step 6
			triplet$ = Mid(hx8$(i),j,6)
			rr(count) = h2d$(Mid(triplet$,1,2))
			gg(count) = h2d$(Mid(triplet$,3,2))
			bb(count) = h2d$(Mid(triplet$,5,2))
			hxc(count) = rr(count) Shl 16 Or gg(count) Shl 8 Or bb(count)
			count = count + 1
		Next
	Next
End Function
