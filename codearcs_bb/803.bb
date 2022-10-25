; ID: 803
; Author: Andy_A
; Date: 2003-10-02 04:26:24
; Title: Fat Functions
; Description: Useful graphics with line thickness

;This simple 2D demo shows examples of using lines of varying thickness to create graphic primitives.
;There are other methods to achieve the same results but other methods are usually plagued by 'banding'
; (artifacts caused by not filling in the thick line completely). These functions are free of 'banding' artifacts.
;The functions are:
;     fatLine
;     fatBox
;     fatOval
;     fatStar
;     fatCircle
;     fatSuperEllipse
; 
;The fatBox and fatOval functions also allow full rotation through 360 degrees.
;Have commented the functions fairly well and each function has a description of the required parameters.
;
;I've tried to optimize all of the functions for speed, but some can be
;tweaked for better results by removing parameter checking and using LUT's.
;
;All of the functions except fatCircle are dependent on fatLine for line thickness, so if you want
;to use any of the other functions,don't forget to copy the fatLine function to
;your application as well. If you don't need the line thickness in the other functions,
;just replace all references to fatLine with Blitz's Line() function.
;
;fatLine written in Blitz+ by Andy Amaya 2003.06.23
;fatFunctions compiled 2003.10.02
;Update - fatSuperEllipse() function added 2003.10.06
;
;Feel free to use these routines as you see fit. Enjoy 8)






;          ------> Press any key to cycle through the different examples



Const sw% = 800
Const sh% = 600
Const cx% = sw%/2
Const cy% = sh%/2

Graphics sw, sh, 16
SetBuffer BackBuffer()
SeedRnd MilliSecs()

fntArial=LoadFont("Arial",24,True,False,False)
SetFont fntArial


Cls

Gosub DrawFatLines
Gosub TrapKey

Gosub DrawFatBoxes
Gosub TrapKey

Gosub DrawFatCircles
Gosub TrapKey

Gosub DrawFatStars

Gosub DrawGlobe
Gosub TrapKey

Gosub DrawSuperEllipses
Gosub TrapKey

Gosub Swirly

End

.TrapKey
     WaitKey
Return

.DrawFatLines
     Cls
     For i% = 1 To 360 Step 5
          j% = Cos(i%) * cx% + cx%
          k% = Sin(i%) * cy% + cy%
          Color Rand(64,255),Rand(64,255),Rand(64,255)
          ;fatLine(x1%, y1%, x2%, y2%,   penSize%)   <-- shows syntax of function call
          fatLine( cx%, cy%,  j%,  k%, Rand(1,25))
     Next
     Color 255,255,255
     Text 0,0,"Randomly thick lines"
     Flip
Return

.DrawFatBoxes
     Cls
     For i% = 0 To 14
          Color Rand(64,255),Rand(64,255),Rand(64,255)
          tcX% = Cos(i%*24) * 30 + cx%
          tcY% = Sin(i%*24) * 30 + cy%
          ;fatBox( x%,   y%, wide%, high%, penSize%, rotAngle%)   <-- shows syntax of function call
          fatBox(tcX%, tcY%,   160,    60,        3,     i%*24)
          fatBox(tcX%, tcY%,   170,    50,        3,     i%*24)
          fatBox(tcX%, tcY%,   180,    40,        3,     i%*24)
          fatBox(tcX%, tcY%,   190,    30,        3,     i%*24)
          fatBox(tcX%, tcY%,   200,    20,        3,     i%*24)
          fatBox(tcX%, tcY%,   210,    10,        3,     i%*24)
     Next
     Color 255,255,255
     Text 0,0,"Rotated rectangles"
     Flip
Return

.DrawFatCircles
     Cls
     r% = 255: g% = 0 : b% = 0
     y% = 5
     pen% = 1
     rad% = 5
     While y% <= 620
          Color r%, g%, b%
          x% = 0
          While x% <= 850
               ;fatCircle(centerX%, centerY%, rad%, penSize%)   <-- shows syntax of function call
               fatCircle(       x%,       y%, rad%,     pen%)
               x% = x% + rad% * 2 + pen%
          Wend
          pen% = pen% + 1
          rad% = rad% + 2
          y% = y% + rad% * 2 + pen
          r% = r% - 18
          g% = g% + 18
     Wend
     Color 255,255,255
     Text 0,0,"Circles of varying thickness"
     Flip
Return

.DrawFatStars
     pen% = 1
     While GetKey() = 0
          For j = 30 To 70 Step 2
               Cls
               For i% = 1 To 5
                    outy% = i * j
                    inny% = outy%/2.6
                    If i% And 1 Then Color 255,96,0 Else Color 255,255,0
                                ;fatStar(centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%)   <-- shows syntax of function call
                    fatStar(     cx%,      cy%,        outy%,        inny%,      12,     pen%,     0)
               Next
               Color 255,255,255
               Text 0,0,"Variable sized stars"
               Flip
          Next 
          For j = 70 To 30 Step -2
               Cls
               For i% = 1 To 5
                    outy% = i * j
                    inny% = outy%/2.6
                    If i% And 1 Then Color 255,96,0 Else Color 255,255,0
                                ;fatStar(centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%)   <-- shows syntax of function call
                    fatStar(     cx%,      cy%,        outy%,        inny%,      12,     pen%,     0)
               Next
               Color 255,255,255
               Text 0,0,"Variable sized stars"
               Flip
          Next
          For j = 30 To 70
               Cls
               For i% = 1 To 5
                    outy% = i * j
                    inny% = outy%/2.6
                    If i% And 1 Then Color 255,96,0 Else Color 255,255,0
                                ;fatStar(centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%)   <-- shows syntax of function call
                    fatStar(     cx%,      cy%,        outy%,        inny%,       5,     pen%,     0)
               Next
               Color 255,255,255
               Text 0,0,"Variable sized stars"
               Flip
          Next 
          For j = 70 To 30 Step -1
               Cls
               For i% = 1 To 5
                    outy% = i * j
                    inny% = outy%/2.6
                    If i% And 1 Then Color 255,96,0 Else Color 255,255,0
                                ;fatStar(centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%)   <-- shows syntax of function call
                    fatStar(     cx%,      cy%,        outy%,        inny%,       5,     pen%,     0)
               Next
               Color 255,255,255
               Text 0,0,"Variable sized stars"
               Flip
          Next
     Wend
Return

.DrawGlobe
     pen% = 2
     globe = 225
     globeStep = globe/15
     counter = 0
     toggle = 0
     Cls
     Flip
     Cls
     For rotate = 0 To 360 Step 15 
             t = 0
          While t <= globe
               penColor% = (penColor% + 1) Mod 3
               Select penColor
                    Case 0: Color 0, 0, 255
                    Case 1: Color 0, 255, 0
                    Case 2: Color 255, 0, 0
               End Select
                      u = globe-t
               ;fatOval(centerX%, centerY%, wide%, high%, penSize%, rotAngle%,interval%)   <-- shows syntax of function call
                fatOval(      cx,       cy,     u,  globe,     pen%,    rotate,        40)
                  counter = counter + 2
               t = t + globeStep
          Wend
          Flip
     Next
     Color 255,255,255
     Text 0,0,"Sphere made from rotated ovals"
     Flip
Return

.Swirly
     pen% = 2
     toggle = 0
     ClsColor 4,4,80
     Cls
     Repeat
          ;jiggle% = Rand(0,5)           ;Jiggles center of whirlpool while spinning
          For j% = 360 To 15 Step -15
               Cls
               iwx% = Rand(cx% - jiggle%, cx% + jiggle%)
               iwy% = Rand(cy% - jiggle%, cy% + jiggle%)
               For i% = 15 To 270 Step 15
                    toggle% = 1 - toggle%
                    If toggle Color 0,0,255 Else Color 16,16,192
                    ;fatOval(centerX%, centerY%,    wide%,    high%, penSize%, rotAngle%,interval%)   <-- shows syntax of function call
                    fatOval(     iwx%,     iwy%, i% Shl 1,       i%,     pen%,     i%+j%,       24)
                    If GetKey() <> 0 Then flag% = 1: Exit
               Next
                  Color 255 , 255 , 255
               Text 0,0,"Whirlpool (rotating ovals)"
               Flip
               If flag% Then Exit
          Next
          If flag% Then Exit
     Forever
Return

.DrawSuperEllipses
	ClsColor 160,192,255
	Cls
	Color 96,96,96
	For i% = 16 To 799 Step 16
		Line(i%, 48, i%, 576)
	Next
	For i% = 48 To 576 Step 16
		Line(0, i%, 799, i%)
	Next
	r% = 0: g% = 0: b% = 255
	fpen# = 2
	k% = 0
    For j% = 32 To 795 Step 60
        Color r%, g%, b%
;                                  (show fatSuperEllipse() function syntax)
;       fatSuperEllipse(centerX%, centerY%, wide%, high%, exponent#, levelOfDetail#, penSize%, rotAngle%)
        fatSuperEllipse       j%,       77,    24,    18,        .4,           20.0,    fpen#,        k%
        fatSuperEllipse       j%,      134,    24,    18,       .75,           20.0,    fpen#,        k%
        fatSuperEllipse       j%,      191,    24,    18,         1,           90.0,    fpen#,        k%
        fatSuperEllipse       j%,      248,    24,    24,         2,             72,    fpen#,        k%
        fatSuperEllipse       j%,      305,    24,    24,         2,      51.428571,    fpen#,        k%
        fatSuperEllipse       j%,      362,    24,    18,         2,           20.0,    fpen#,        k%
        fatSuperEllipse       j%,      420,    24,    18,         3,           20.0,    fpen#,        k%
        fatSuperEllipse       j%,      477,    24,    18,         5,           20.0,    fpen#,        k%
        fatSuperEllipse       j%,      534,    24,    18,        10,           15.0,    fpen#,        k%
        k% = k% + 30
        r% = r% + 21.25
        b% = b% - 21.25
        fpen# = fpen# + .3
    Next
	Color 0,0,128
    For i% = 0 To 12
        angle$ = Str$(i% * 30) + "°"
        wid% = StringWidth(angle$)
        Text (i * 61 - wid% + 42), 576,angle$
    Next
	msg$ = "All of these shapes created using just one function:         fatSuperEllipse!"
	wid% = StringWidth(msg$)
	Text (800-wid%)/2,10,msg$
	Flip
Return


 

;**********************************   fatLine   ******************************************
;** Parameters are:                                                                     **
;** x1%, y1%, x2%, y2%, penSize%                                                        **
;**                                                                                     **
;** x1%, y1% are the coords of the line origin                                          **
;** x2%, y2% are the coords of the line end point                                       **
;** penSize% is the thickness of line to be drawn                                       **
;**                                                                                     **
;** BRESLINE.PAS - A general line drawing procedure.                                    **
;** By Mark Feldman                                                                     **
;**                                                                                     **
;** Source: http://www.gamedev.net/reference/articles/article767.asp                    **
;**                                                                                     **
;** Adapted for Blitz+ fatLine function by Andy Amaya                                   **
;**                                                                                     **
;*****************************************************************************************

Function fatLine(x1%, y1%, x2%, y2%, penSize%)
If penSize% < 1 Then Return False
If penSize% = 1 Then Line(x1%, y1%, x2%, y2%): Return
;  penSize% is thickness to draw line
offset% = penSize% / 2          ;offset needed to place Oval correctly

; Calculate deltax and deltay for initialization
  deltax% = Abs(x2% - x1%)
  deltay% = Abs(y2% - y1%)
 
; Initialize all vars based on which is the independent variable
  If deltax% >= deltay% Then
;     x is the independent variable
      numovals% = deltax% + 1
      d% = (2 * deltay%) - deltax%
      dinc1% = deltay% Shl 1
      dinc2% = (deltay% - deltax%) Shl 1
      xinc1% = 1
      xinc2% = 1
      yinc1% = 0
      yinc2% = 1
  Else 
;     y is the independent variable
      numovals% = deltay% + 1
      d% = (2 * deltax%) - deltay%
      dinc1% = deltax% Shl 1
      dinc2% = (deltax% - deltay%) Shl 1
      xinc1% = 0
      xinc2% = 1
      yinc1% = 1
      yinc2% = 1
  End If
; Make sure x and y move in the right directions
  If x1% > x2% Then
      xinc1% = - xinc1%
      xinc2% = - xinc2%
  End If
  If y1% > y2% Then 
      yinc1% = - yinc1%
      yinc2% = - yinc2%
  End If
; Start drawing at x%, y%
  x% = x1% - offset%
  y% = y1% - offset%
; Draw the filled ovals
  For i% = 1 To numovals%
       Oval x%, y%, penSize%, penSize%, 1
      If d% < 0 Then 
          d% = d% + dinc1%
          x% = x% + xinc1%
          y% = y% + yinc1%
      Else
          d% = d% + dinc2%
          x% = x% + xinc2%
          y% = y% + yinc2%
      End If
  Next
End Function


;**********************************    fatBox   ******************************************
;** Parameters are:                                                                     **
;** x%, y%, wide%, high%, penSize%, rotAngle%                                           **
;**                                                                                     **
;** x%, y% are the  coords of the upper left corner of the box to be plotted            **
;**        this function rotates box around the upper left corner (not center of box)   **
;** wide%  is the width of the box                                                      **
;** high%  is the height of the box                                                     **
;** penSize% is the thickness of lines used to plot box                                 **
;** rotAngle% is the rotation angle of the box to be plotted (0 to 360 typically)       **
;**                                                                                     **
;*****************************************************************************************

Function fatBox(x%, y%, wide%, high%, penSize%, rotAngle%)
     If rotAngle > 360 Then rotAngle = rotAngle Mod 360
     ;No rotation involved, so just draw a box
     If rotAngle% = 0 Or rotAngle% = 360 Then
          fatLine(x%        , y%        , x% + wide%, y%        , penSize%)
          fatLine(x% + wide%, y%        , x% + wide%, y% + high%, penSize%)
          fatLine(x%        , y% + high%, x% + wide%, y% + high%, penSize%)
          fatLine(x%        , y%        , x%,         y% + high%, penSize%)     
     Else
          ;Make rotation calculations and draw rotated box
          vtx% = rotAngle% + 90
          cs% = Cos(vtx%) * high%
          sn% = Sin(vtx%) * high%
          x1% = Cos(rotAngle%)* wide% + x%
          y1% = Sin(rotAngle%)* wide% + y%
          fatLine(x%,   y%, x1%, y1%, penSize%)
          x2% = cs% + x1%
          y2% = sn% + y1%
          fatLine(x1%, y1%, x2%, y2%, penSize%)
          x3% = cs% + x%
          y3% = sn% + y%
          fatLine(x%,   y%, x3%, y3%, penSize%)
          fatLine(x2%, y2%, x3%, y3%, penSize%)
     End If
End Function


;**********************************   fatOval   ******************************************
;** Parameters are:                                                                     **
;** centerX%, centerY%, wide%, high%, penSize%, rotAngle%, sides%                       **
;**                                                                                     **
;** centerX%, centerY% coords locate center of oval to be plotted                       **
;** wide%  is the x-radius of the oval                                                  **
;** high%  is the y-radius of the oval                                                  **
;** penSize% is the thickness of lines used to plot oval                                **
;** rotAngle% is the rotation angle of the oval to plot  (0 to 359)                     **
;** sides% is the number of connected points on plotted oval                            **
;**                                                                                     **
;*****************************************************************************************

Function fatOval(centerX%, centerY%, wide%, high%, penSize%, rotAngle%, sides%)
    ;360 sides generates the smoothest oval, 3 sides  generates a triangle
    ;interval# is the number of degrees between plotted points
	interval# = Float(360 / sides%)
    If interval# <= 0.0 Or interval# > 180.0 Then Return False
	;Keep rotAngle between 0 and 359
	rotAngle% = rotAngle% Mod 360
	If penSize% < 1 Then Return False
    ;If rotation angle is 0 don't do extra calculations
    If rotAngle% = 0 Then
        x1% = wide% + centerX%
        y1% = centerY%
        ;This While..Wend is same as For i# = interval# To 360.0 Step interval#)
		i# = interval#
		While i# <= 360.0
            x2% = Cos(i#) * wide% + centerX%
            y2% = Sin(i#) * high% + centerY%
            fatLine(x1%, y1%, x2%, y2%, penSize%)
            x1% = x2%
            y1% = y2%
 	    	i# = i# + interval#	;(increment loop index, else you end up with a Repeat..Forever loop)
		Wend
    Else
        ;Make rotation calculations and draw oval
        cs# = Cos(rotAngle%)
        sn# = Sin(rotAngle%)
        x1% = cs# * wide% + centerX%
        y1% = sn# * wide% + centerY%

        ;This While..Wend is same as For i# = interval# To 360.0 Step interval#)
		i# = interval#
		While i# <= 360.0
            rotX# = Cos(i#) * wide%
            rotY# = Sin(i#) * high%
            x2% = cs# * rotX# - sn# * rotY# + centerX%
            y2% = sn# * rotX# + cs# * rotY# + centerY%
            fatLine(x1%, y1%, x2%, y2%, penSize%)
            x1% = x2%
			y1% = y2%
			i# = i# + interval#	;(increment loop index, else you end up with a Repeat..Forever loop)
        Wend
    End If
End Function


;**********************************   fatStar   ******************************************
;** Parameters are:                                                                     **
;** centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%            **
;**                                                                                     **
;** centerX%, centerY% coords locate center of star to be plotted                       **
;** majorRadius% is the size of 'convex' star points                                    **
;** minorRadius% is the size of 'concave' star points                                   **
;** points% is the number of ;convex; star points: 3 - 180 are valid number of points   **
;** penSize% sets the thickness for lines that draw the star                            **
;**                                                                                     **
;*****************************************************************************************

Function fatStar(centerX%, centerY%, majorRadius%, minorRadius%, points%, penSize%, mode%)
    ;tell function to draw a star with 3 to 180 'convex' points, otherwise can't do - Return False
    If points% < 3 Or points% > 180 Then Return False

    ;inc is number of star points times 2
    ;For example: a five point star has 5 ;convex; points + 5 ;concave; points
    inc# = 360.0/(points * 2)

    ;initialize variables
    ;toggle keeps track of 'concave' And 'convex' radii
    toggle% = 0

    ;calculate First x,y coords to start due north of center point
    oldX% = centerX                    ;same as Cos(270)*majorRadius + centerX
    oldY% = -majorRadius + centerY     ;same as Sin(270)*majorRadius + centerY

    ;remember First x,y coords To close polyStar
    firstX% = oldX%: firstY% = oldY%

    ;loop that draws the star
    ;next 3 lines are equivalent to:  For i# = inc# To 360 - (inc#-1) Step inc
    i# = 0
    While i# <= 360 - (inc#-1)
     i# = i# + inc#

        ;start first star point due north of center point
        angle% = i# + 270

        ;scale back degrees to between 0 And 360
        angle = angle Mod 360

        ;make toggle either 0 Or 1
        toggle% = 1 - toggle%

        ;If toggle is 1 Then calculate a 'concave' point x,y pair
        If toggle Then
            ;valid minorRadius values for stars are integers:  0 < minorRadius < majorRadius
            newX% = Cos(angle) * minorRadius + centerX
            newY% = Sin(angle) * minorRadius + centerY

            ;draw a line from last 'convex' point to new 'concave' point
         fatLine(oldX, oldY, newX, newY, penSize)

            ;If mode = 1 Then draw lines from center of star to a 'concave' point
            If mode% Then
          fatLine(newX, newY, centerX, centerY, penSize)
         End If               

        Else
            ;valid majorRadius values for stars are integers: 0 < minorRadius <= majorRadius
            newX = Cos(angle) * majorRadius+ centerX
            newY = Sin(angle) * majorRadius+ centerY

            ;draw a line from last 'concave' point to new 'convex' point
         fatLine(oldX, oldY, newX, newY, penSize)
        End If

        ;remember last x,y pair to use as start for the next line segment
        oldX = newX
        oldY = newY

     Wend
    ;close the polyStar by drawing line from last 'concave' point to 1st 'convex' point
     fatLine(oldX, oldY, firstX, firstY, penSize)
End Function


;**********************************  fatCircle  ******************************************
;** Parameters are:                                                                     **
;** centerX%, centerY%, rad%,  penSize%                                                 **
;**                                                                                     **
;** centerX%, centerY% coords locate center of circle to be plotted                     **
;** rad% is the radius of the circle                                                    **
;** penSize% is the line thickness used to draw circle                                  **
;**                                                                                     **
;** Note: Bresenham's circle algorithm is smoother but is about 10 times slower         **
;**                                                                                     **
;*****************************************************************************************

Function fatCircle(centerX%, centerY%, rad%, penSize%)
     If penSize < 1 Or rad% < 1 Then Return False
     If penSize% = 1 Then Oval centerX%-rad%, centerY%-rad%, rad% Shl 1, rad% Shl 1, 0: Return
     offset% = penSize% Shr 1
     For i% = 0 To 360
          a% = Cos(i%) * rad% + centerX%
          b% = Sin(i%) * rad% + centerY%
          Oval a% - offset%, b% - offset%, penSize%, penSize%, 1
     Next
End Function

;******************************   fatSuperEllipse    *************************************
;** Parameters are:                                                                     **
;** centerX%, centerY%, wide%, high%, exponent#, levelOfDetail, penSize%, rotAngle%     **
;**                                                                                     **
;** centerX%, centerY% coords locate center of SuperEllipse to be plotted               **
;** wide%  is the x-radius of the SuperEllipse                                          **
;** high%  is the y-radius of the SuperEllipse                                          **
;** exponent# determines the overall shape of the SuperEllipse      (0.1 to 99 typical) **
;** levelOfDetail# - determines number of points plotted & connected with lines         **
;** penSize% is the thickness of lines used to plot SuperEllipse    ( 1 to 200 typical) **
;** rotAngle% is the rotation angle of the SuperEllipse plotted     ( 0 to 360 typical) **
;**                                                                                     **
;** Sources:                                                                            **
;**     http://www.cs.colorado.edu/lizb/graphics.html                                   **
;**     http://astronomy.swin.edu.au/pbourke/surfaces/superellipse/                     **
;**     http://www.wikipedia.org/wiki/Super_ellipse                                     **
;**                                                                                     **
;** Ported to Blitz+ by Andy Amaya 2003.10.06                                           **
;*****************************************************************************************

Function fatSuperEllipse (centerX%, centerY%, wide%, high%, exponent#, levelOfDetail#, penSize%, rotAngle%)
     rotAngle% = rotAngle% Mod 360
     ;Make sure we don't get any divide by zero errors     
     If exponent# = 2.0 Or Exponent = 0.0 Then
          power# = 0.0
     Else
          power# = 2.0/exponent#-1
     End If
     ;If rotation angle is zero then don't do the extra calculations      
     If rotAngle% = 0 Then
          theta# = 0.0
          ;determine the first x,y coords
          x1% = wide% * 1.0^power# + centerX%
          y1% = centerY%
          ;initialize theta (current angle) and loop variable
          theta# = levelOfDetail#
          limit# = 360.0 + levelOfDetail#
          While theta# <= limit#
               cosTheta# = Cos(theta#)
               sinTheta# = Sin(theta#)
               ;calculate x,y coords using Super Ellipse formula
               x2% = wide% * cosTheta# * Abs(cosTheta#)^power# + centerX%
               y2% = high% * sinTheta# * Abs(sinTheta#)^power# + centerY%
               ;connect the 2 sets of coords calculated with a line
               fatLine (x1%, y1%, x2%, y2%, penSize%)
               ;last coords are now first coords of line
               x1% = x2%
               y1% = y2%
               ;increment loop variable by amount indicated by "levelOfDetail"
               theta# = theta# + levelOfDetail#
          Wend
     Else
          ;need to rotate SuperEllipse, so do the extra math
          theta# = 0.0
          cosTheta# = 1.0
          sinTheta# = 0.0
          ;pre-calc Cos() and Sin() of rotation angle
          cs# = Cos(rotAngle%)
          sn# = Sin(rotAngle%)
          ;calculate the first rotated x,y coords
          rotX# = wide% * 1.0^power#
          rotY# = 0.0
          x1% = cs# * rotX# - sn# * rotY# + centerX%
          y1% = sn# * rotX# + cs# * rotY# + centerY%
          ;initialize theta (current angle) and loop variable
          theta# = levelOfDetail#
          limit# = 360.0 + levelOfDetail#
          While theta# <= limit#
               cosTheta# = Cos(theta#)
               sinTheta# = Sin(theta#)
               ;calculate the next pair of rotated x,y coords
               rotX# = cosTheta# * wide% * Abs(cosTheta#)^power#
               rotY# = sinTheta# * high% * Abs(sinTheta#)^power#
               x2% = cs# * rotX# - sn# * rotY# + centerX%
               y2% = sn# * rotX# + cs# * rotY# + centerY%
               ;connect the 2 sets of coords calculated with a line
               fatLine(x1%, y1%, x2%, y2%, penSize)
               ;last coords are now first coords of line
               x1% = x2%
               y1% = y2%
               ;increment loop variable by amount indicated by "levelOfDetail"
               theta# = theta# + levelOfDetail#
          Wend          
     End If
End Function
