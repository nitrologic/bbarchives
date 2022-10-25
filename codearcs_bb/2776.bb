; ID: 2776
; Author: Andy_A
; Date: 2010-10-11 11:09:19
; Title: Wrapped text in rounded rectangles
; Description: Rounded Rects & wrapped text

AppTitle "Wrapped and Centered Text in Rounded Rects"
Graphics 800, 600, 32, 2
SetBuffer BackBuffer()
Dim work$(1,1)

fntArial% = LoadFont("Arial", 20, True, False, False)
fntTimes% = LoadFont("Times New Roman", 20, True, False, False)

;text excerpted from Blitz help on 'Types'
msg1$=msg1$+"Not a database guru? Need another example? Okay. Let's say you are setting "
msg1$=msg1$+"up an auditorium for a speech or event and you are putting up hundreds of chairs for "
msg1$=msg1$+"the spectators. The chairs have to be in a certain place on the floor, and some will "
msg1$=msg1$+"need to be raised up a bit higher than others (visiting dignitaries, the mayor is "
msg1$=msg1$+"coming, etc.). So being the computer genius you are, you start figuring out how you "
msg1$=msg1$+"can layout the chairs with the least amount of effort. You realize that the floor "
msg1$=msg1$+"is checkered, so its really a huge grid! This will make it easy! You just need to "
msg1$=msg1$+"number the floor on a piece of graph paper and put into the grid how high each "
msg1$=msg1$+"chair should be, based on where the boss told you the important people are to sit. "
msg1$=msg1$+"So, for each chair, you will have a row and column on the graph paper (x and y location) "
msg1$=msg1$+"and a level to adjust the chair to (height). Good, we are organized. Now, even "
msg1$=msg1$+"though we have it all on paper, we still have to do the work of placing all the "
msg1$=msg1$+"chairs. After you are done, let's say your boss walks up to you and says 'they aren't "
msg1$=msg1$+"centered right .. move'em all over 1 square'. Oh no! You have them all perfect, and "
msg1$=msg1$+"even though it is a simple thing to move a chair one square to the right (After all, "
msg1$=msg1$+"their order and height won't change) - you still have to move each and every chair! "
msg1$=msg1$+"Sure would be nice if you could just wave your hand and say 'For Each chair in the "
msg1$=msg1$+"room, add 1 square to its x location' and have it just magically happen. Alas, in "
msg1$=msg1$+"the real world, get busy - you've got a lot of chairs to move!"


msg2$ = "In Blitz, you could have set up a TYPE called CHAIR, set the TYPE's FIELDS "
msg2$=msg2$+"as X, Y, and HEIGHT. You would then create as many chairs as you need with the NEW "
msg2$=msg2$+"command (each time you call NEW, it makes a new chair, with its OWN X, Y, and HEIGHT "
msg2$=msg2$+"variables) and assign them the X, Y, and HEIGHT values you decide upon. In our "
msg2$=msg2$+"example above, when the boss told you to move the chairs over 1 box, you probably "
msg2$=msg2$+"groaned inside. That's a lot of work! In Blitz, we could use four lines of code to "
msg2$=msg2$+"adjust all our CHAIR objects to the new position (using FOR ... EACH commands). "



SetFont fntTimes
wrapText(  10,  10, msg1$, 780, 0,0,0, 248,255,192, 1)

SetFont fntArial
wrapText(  10, 380, msg2$, 780, 255,255,255, 32,80,192, 1)

wrapText( 250, 545,"Click to view side-by-side layout",300, 255,255,255, 255,0,0, 1)
Flip
WaitMouse()

Cls
SetFont fntArial	;switch from Times to Arial
wrapText( 10,   5, msg1$, 470, 0,0,0, 248,255,192, 0)

SetFont fntTimes 	;switch from Arial to Times
wrapText( 490, 5, msg2$, 300, 255,255,255, 32,80,192, 0)

wrapText( 540, 465,"Waiting for left mouse click to exit.",200, 255,255,255, 255,0,0,1)

Flip 



WaitMouse()

FreeFont fntArial
FreeFont fntTimes
End

Function wrapText%(x%, y%, msg$, wide%, fr%, fg%, fb%, br%, bg%, bb%, center% = 0)
;=======================================================================
; This function wraps text inside of a rounded rectangle with an option to
; center the text.
;=======================================================================
; x,y = upperleft coordinates of the rounded rectangle
;
; msg$ = text string that you wish to place in rounded rectangle
;
; wide = maximum width of button in pixels
;
; fr, fg, fb = text color (RGB triplet)
;
; br, bg, bb = button color (RGB triplet)
;
; center = when 'center' = 1 then each line of text is centered
;		   the default setting is zero. (no centering)
;=======================================================================
	Local rad%, initX%, initY%, txtHigh%, count%, nxtWord$, flag%, txtCenter%
	Local tLine$, temp$, lineCount%, boxWide%, boxHigh%, pad%, tLen%
	
	Dim work$(50,1)
	pad = 10 ;the padding can be adjusted to suit your particular needs
	rad = 9  ;radius of rounded corner - values 2 To 26 recommended
	diam = rad + rad ;diameter of circles
	initX = x
	initY = y
	txtWide = wide - pad*2
	txtHigh = StringHeight(msg$) ;height of current font in pixels
	y = y + pad
	Repeat
		count = count + 1
		nxtWord$ = Word$(msg$, count, " ")
		tLine$ = temp$
		temp$ = temp$ + nxtWord$ + " "
		If StringWidth(temp$) >= txtWide Then
			tLine$ = Trim(tLine$)
			lineCount = lineCount+1
			work$(lineCount,0) = Str(x)+" "+Str(y)
			work$(lineCount,1) = tLine$
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
	;draw the filled circles To make rounded corners
	Oval initX, initY, diam, diam, True
	Oval initX, initY+boxHigh-diam-1, diam, diam, True
	Oval initX+boxWide-diam-1, initY, diam, diam, True
	Oval initX+boxWide-diam-1, initY+boxHigh-diam-1, diam, diam, True
	;fill in the space between circles using filled boxes
	Rect initX, initY+rad, boxWide-1, boxHigh-diam-1, True
	Rect initX+rad, initY, boxWide-diam-1, boxHigh-1, True
	Color fr, fg, fb
	For i = 1 To lineCount
		x = Word$(work$(i,0),1)
		y = Word$(work$(i,0),2)
		If center = 1 Then
			tLen = StringWidth(work$(i,1))
			txtCenter = (boxWide - tLen) Shr 1
			x = x + txtCenter
		Else 
			x = x + pad
		End If
		Text x, y, work$(i,1)
	Next
End Function

Function Word$(string2Chk$, n, delimiter$=" ")
	;initialize local variables
	Local count%, findDelimiter%, position%, current$
		
	count = 0
	findDelimiter = 0
	position = 1
	;current$ = ""

	;'n' must be greater than zero
	;otherwise exit function and return null string
	If n > 0 Then
		;strip leading and trailing spaces
		string2Chk$  = Trim(string2Chk$)
		;find the word(s)
		Repeat
			;first check if the delimiter occurs in string2Chk$
			findDelimiter% = Instr(string2Chk$,delimiter$,position)
			If findDelimiter <> 0 Then
				;extract current word in string2Chk$
				current$ = Mid$(string2Chk$,position,findDelimiter-position)
				;word extracted; increment counter
				count = count + 1
				;update the start position of the next pass
				position = findDelimiter + 1
				;if counter is same as n then exit loop
				If count = n Then findDelimiter = 0
			End If
		Until findDelimiter = 0
		;Special Case: only one word and no delimiter(s) or last word in string2Chk$
		If (count < n) And (position <= Len(string2Chk$)) Then
			current$ = Mid$(string2Chk$,position, Len(string2Chk$) - position+1)
			count = count + 1
			;looking for word that is beyond length of string2Chk$
			If count < n Then current$ = ""
		End If
	End If
	Return current$
End Function
