; ID: 717
; Author: Oldefoxx
; Date: 2003-06-19 01:29:45
; Title: X-Y 2D Graphs
; Description: Handles 2D graphs

;TITLE:        X-Y 2D Graph
;AUTHOR:       Donald R. Darden, oldefoxx@cox.net
;SUBMITTED:    June 18, 2003
;Restrictions:  If you use this program as part of your own
;work, give credit where due.  You are welcome to adapt the
;included code to other purposes.

;BASIC STRAIGHT LINE EQUATIONS AND RELATIONSHIPS:
;A straight line can be defined a number of different ways:
;  (1) Between two points given by (x1,y1) and (x2,y2)
;  (2) As a point and slope on the x-intercept (where x=0),
;      so that y=Mx+C, where M represents (y2-y1)/(x2-x1)
;      (the tangent) of the angle formed, and C represents
;      a constant offset along the Y-axis
;  (3) A more general equation in the form of Ay+Bx+C=0,
;      which avoids the possibility of a division-by-zero
;      error with (2)
;  (4) A modification of (3) in the form of (y1-y)*(x2-x1)+
;      (y2-y1)*(x-x1)=0, where X and Y are not known, but a
;      formula for them can be derived by replacing the
;      other unknowns with the x- and y- coordinate date
;      for the two points.
;  (5) Using Polar Coordinates


;We are using (4) above to work out the equations for the
;lines, but we don't worry about using the "=0".  We don't
;know what either X or Y is, but we use variables for each
;of these, while we assign the actual known values for x1,
;y1,x2, and y2, and then solve to ger values for A, B, and
;C in the equation Ax+By+C.  Once we have both equations,
;we assume they probably intersect at some point (unless
;they are parallel), and by solving for two simultaneous
;equations, we can get a value for X and for Y which
;defines the point where they intersect, if there is one.

;Computers are better suited to solving complex equations
;if they can be properly expressed, than humans are.  In
;fact, rules such as substitution, elimination, or just
;plotting two lines to visually identify where they might
;intersect, are adaptations that fit human capabilities,
;not those of machines.  With computers, you are generally
;better off working on a point-by-point basis, from tables
;or arrays, from matrixes, or according to some cardinal
;rule.  In our case, there is a relationship called
;Cramer's Rule, which was derived from matrix arithmetic.
;This rule allows the computer to cleanly derive the
;intersection point of two straight lines from their
;equations.  The only pitfall is again, the risk of trying
;to divide-by-zero, so the program has to check for this
;possibility and react accordingly.

;The slope form can be handy for determining the relative
;angles of two straight lines to the vertical axis, and
;hence to each other.  If the slopes are the same, the two
;lines are parallel, and will never meet.  Otherwise, if
;extended, the lines will intersect.  If the product of the
;two slopes equals -1 (that is. M1*M2=-1 as found from (2)
;above), then the two lines are exactly perpendicular to
;each other, and the intersection will form right angles
;where the lines intersect.

;Not only does the property M1*M2=-1 tell us that two lines
;are at right angles to each other, but we can determine
;an equation for any point to any line by first finding the
;inverted slope that the constructed line between the point
;and the line should have:  It would be the reciprocal of
; of M1, or m2=1/M1.  The distance formula between two end
;points is simply D=Sqr((x2-x1)^2+(y2-y1)^2), or for
;better computational speed:
;          xd=x2-x1
;          yd=y2-y1
;          D=Sqr(xd*xd+yd*yd)

;Assuming that two points do not just uniquely identify a
;line that could be extended indefinately, but that they
;identify the end points of that line, then the general
;solution using Cramer's Rule has to be refined further.
;IF there is a solution to Cramer's Rule, then we still
;have to prove that the solved-for intersection point of
(X,Y) lies between the end points of both lines.

;Fortunately, since there is a unique Y for every X in any
;straight line, we only have to establish the the X lies
;between X1 and X2 of the end points (x1,y1,x2,y2) for the
;line, and the same for the second line, or make sure that
;Y fits between the Y end point values for both lines. It
;isn't necessary to check both for an X-fit and a Y-fit,
;which saves some work.

Global xmin=-30            ;lowest value for x to be graphed
Global xmax=30             ;highest value for x to be graphed
Global scale#=5            ;scale the resulting plot by some factor
Global wmax=4620           ;rightmost viewable window pixel reference
Global hmax=4620           ;bottommost viewable window pixel reference   
Global wmin=0              ;leftmost viewable window pixel reference
Global hmin=0              ;topmost viewable window pixel reference 
Global wmid=(wmin+wmax)/2  ;center of viewable area horizontally
Global hmid=(hmin+hmax)/2  ;center of viewable area vertically
Global zoom=10             ;magnifier for windows viewpoint
Global grid=1              ;distance of displayed grid marks
Global width0=0
Global height0=0
Global width=800           ;number of pixels horizontally
Global height=600          ;number of pixels vertically
Global width2=width/2
Global height2=height/2
Global woffset=width2     ;viewpoint width offset, all factors included
Global hoffset=height2    ;viewpoint height offset, all factors included
Global showscale=0         ;toggles bottom and right scale display
Global debug=0        ;real handy if you set debug=1, and
                      ;write "if debug then print ..."
                      ;statements when trying to isolate
                      ;program errors.

Global q$=Chr$(34)
Dim oxpos(20)    ;saves old x position of text writes
Dim oypos(20)    ;saves old y position of text writes
Dim osome$(20)   ;saves old text write messages
Dim pn$(3)       ;saves prime numbers for later reduction
Dim pr(3)        ;saves numbers for later prime reduction
Dim ea$(3)       ;saves equations being reduced
Dim gp#(2,7)     ;Line (x1,y1,x2,y2),a,b,c of expression Ax+by=C
Dim eqn$(2)
Global ap        ;index for gp(ap,n) points 
Global pr7$=""   ;collects prime values in number
Global pr4$=""   ;collects prime values in number
Global x1#,y1#,x2#,y2#,x3#,y3#,x4#,y4#,e1$,e2$,e3$,xn#,yn#
Graphics width,height,16,2 ;pick your windows size and colors
SetBuffer BackBuffer()     ;do plotting out of sight
SeedRnd MilliSecs()
owoffset=woffset
ohoffset=hoffset
ozoom=zoom
Flip
Cls
if debug then     ;an example of using debug
  print "Debug mode is currently enabled."
  print "Press any key to continue."
  waitkey()
endif
Goto startdraw     ;jump in and calculate initial values
While Not KeyHit(1)     ;loop until Esc key pressed
  If KeyDown(30) Or KeyDown(78) Then ;"A" or "+"   zoom in
    If zoom<1000 Then
      zoom=zoom+1+zoom/50
      Goto redraw
    EndIf
  EndIf
  If KeyDown(44) Or KeyDown(74) Then  ;"Z" or "-"  zoom out
    If zoom>1 Then
      zoom=zoom-1-zoom/50
      Goto redraw
    EndIf
  EndIf
  If KeyDown(75) Or KeyDown(203) Or KeyDown(219) Then  ;left arrow (offset right)
    woffset=woffset+10   
    Goto redraw
  EndIf
  If KeyHit(31) Then    ;"S"    show/hide scales at bottom and right side
    showscale=Not showscale
    Goto redraw
  EndIf
  If KeyDown(71) Then           ;left and up (offset down & right)
    woffset=woffset+10
    hoffset=hoffset+10
    Goto redraw
  EndIf
  If KeyDown(72) Or KeyDown(200) Then  ;up arrow (offset down)
    hoffset=hoffset+10        
    Goto redraw
  EndIf
  If KeyDown(73) Then            ;up and right (offset down and left)
    woffset=woffset-10
    hoffset=hoffset+10
    Goto redraw
  EndIf
  If KeyDown(77) Or KeyDown(205) Or KeyDown(220) Then ;right
    woffset=woffset-10
    Goto redraw
  EndIf
  If KeyDown(79) Then            ;down and left (offset up and right)
    woffset=woffset+10
    hoffset=hoffset-10
    Goto redraw
  EndIf
  If KeyDown(81) Then      ;down and right (offset up and left)
    woffset=woffset-10
    hoffset=hoffset-10
    Goto redraw
  EndIf
  If KeyDown(80) Or KeyDown(208) Then  ;down (offset up)
    hoffset=hoffset-10
    Goto redraw
  EndIf
  If KeyHit(57) Then
.startdraw
    x1=Rnd(xmin,xmax)
    y1=Rnd(xmin,xmax)
    x2=Rnd(xmin,xmax)
    y2=Rnd(xmin,xmax)
    x3=Rnd(xmin,xmax)
    y3=Rnd(xmin,xmax)
    x4=Rnd(xmin,xmax)
    y4=Rnd(xmin,xmax)
    Goto redraw
  EndIf
  If KeyHit(199) Then       ;home key (toggle with previous home setting)
    h=woffset
    woffset=owoffset
    owoffset=h
    h=hoffset
    hoffset=ohoffset
    ohoffset=h
    h=zoom
    zoom=ozoom
    ozoom=h
    Goto redraw
  EndIf
  If KeyDown(207) Then       ;end key (set previous home setting to current)
    owoffset=woffset
    ohoffset=hoffset
    ozoom=zoom
  EndIf
  mxs=MouseXSpeed()           ;get the mouse x position
  mys=MouseYSpeed()           ;get the mouse y position
  If MouseDown(1)             ;check for mouse left button down
    woffset=woffset+mxs       ;if mouse, update width offset
    hoffset=hoffset+mys       ;if mouse, update height offset
.redraw                       ;perform screen redraws when necessary
    Cls
    drawgraph()   ;first, we redraw the grid view in the window

;-------MODIFY THE FOLLOWING SECTION FOR WHATEVER EQUATIONS(S) YOU WANT TO PLOT-------
    ;TEST DATA FOR CREATING EQUATION DERIVITIVE PROCESS:
    ;(y-y1)*(x2-x1)-(x-x1)*(y2-y1)   ;the expression for line 1
    ;(y-y3)*(x3-x3)-(x-x3)*(y4-y3)   ;the expression for line 2

;first set of test values:
    ap=1
;    x1#=1:y1#=1:x2#=5:y2#=-1            ;this is test data for 1st line
    e1$=line_exp(x1#,y1#,x2#,y2#)        ;should give us 2y+x-3  
    ;place first line segment on the screen...
    Color 255,255,0
    n$="Line ("+sout(x1,2)+","+sout(y1,2)+")...("+sout(x2,2)+","+sout(y2,2)+")"
    Text width2+8,1,n$
    Line x1*zoom+woffset,hoffset-y1*zoom,x2*zoom+woffset,hoffset-y2*zoom 
    ;generate first equation and print on screen:
    Text width2-Len(e1)*8-8,1,e1             ;write to screen
 
;second set of test values:
    ap=2
;    x3#=2:y3#=1:x4#=3:y4#=-3             ;this is test data for 2nd line
    e2$=line_exp(x3#,y3#,x4#,y4#)        ;should give us y+4x-9
    ;place second line segment on the screen...
    Color 0,255,255
    n$="Line ("+sout(x3,2)+","+sout(y3,2)+")...("+sout(x4,2)+","+sout(y4,2)+")"
    Text width2+8,15,n$
    Line x3*zoom+woffset,hoffset-y3*zoom,x4*zoom+woffset,hoffset-y4*zoom
    ;generate second equation and print on screen:
    Text width2-Len(e2)*8-8,15,e2             ;write to screen

    ;relate the two Line segments To Each other...
     e3$=cramer$(1,2)
     Text width2+8,29,"Intercept at ("+e3$+")"
;-----------------------END OF SECTION FOR EQUATION PLOTTING--------------------------

    Flip
    Goto redraw1                                    ;finish redrawing text
  EndIf
  xpos=(MouseX()-woffset)/zoom
  ypos=(hoffset-MouseY())/zoom
.redraw1                                          ;redraw text when necessary
  n$="("+xpos+","+ypos+")  "                      ;put mouse coordinates into n$
  If n$<>n1$ Then
    n1$=n$
    Color 255,255,255
    show width0,height0,n$
  EndIf
Wend

Function show(sxpos,sypos,some$)
For os=1 To 20
  If oxpos(os)=sxpos And oypos(os)=sypos Then
    Viewport sxpos,sypos,sxpos+Len(osome$(os))*7,sypos+13
    Cls
    Goto skipout
  EndIf
Next
For os=1 To 19
  oxpos(os)=oxpos(os+1)
  oypos(os)=oypos(os+1)
  osome$(os)=osome$(os+1)
Next
.skipout
Viewport sxpos,sypos,sxpos+Len(some$)*7,sypos+13
Cls
Viewport width0,height0,width,height
Locate sxpos,sypos
Write some$
oxpos(os)=sxpos
oypos(os)=sypos
osome$(os)=some$
End Function

Function swap (u1#,u2#)
u3#=u1#
u1#=u2#
u2#=u3#
End Function

Function sout$(value#,pos)
sout_a=10^pos
sout_b$=Int(Abs(value#*sout_a)+.5)
If Len(sout_b)<=pos Then sout_b=Right$(String$("0",pos)+sout_b,pos+1)
sout_b=Left$(sout_b,Len(sout_b)-pos)+"."+Right$(sout_b,pos)
If value#<0 Then sout_b="-"+sout_b
Return sout_b
End Function

Function drawgraph()
sizing=grid*zoom                    ;set the side of the area to be drawn
Color 50,50,50                      ;make the gridlines a low level grey color
x=woffset Mod sizing                ;determine the offset for the first grid line
While x<=wmax                       ;create all vertical grid lines
  Line x,hmin,x,hmax
  x=x+sizing
Wend
y=hoffset Mod sizing                ;determine the offset for the first grid line
While y<=hmax                       ;create all horizontal grid lines
  Line wmin,y,wmax,y
  y=y+sizing
Wend
If showscale Then                   ;determine if the scales are to be drawn
  j#=sizing*5                       ;scale lines are every fifth grid line
  Color 255,255,255
  Line width0,height-35,width,height-35  ;the horizontal scale line along the bottom
  k#=woffset                        ;work from the grid center backwards
  i=0
  While k#>=width0                  ;continue while display area remains 
    If h#<=width Then               ;don;t display if still out of view area
      Line k#,height-45,k#,height-35  ;make a tick mark on the horizontal scale line
      Locate k#-5,height-35           ;position to write the tick mark value
      Write Str$(i)                   ;write the tick mark value
    EndIf
    k#=k#-j#                          ;decrement the amount to the next tick mark
    i=i-10                            ;decrement the tick count by ten
  Wend
  k#=woffset+j#                       ;start at the tick position right of center
  i=10                                ;start with a tick count of ten
  While k#<=width                     ;continue across the viewable area
    If k#>=width0 Then                ;don;t bother with line if out of viewable area
      Line k#,height-45,k#,height-35  ;draw the tick mark indicated
      Locate k#-5,height-35           ;position to write the tick mark value
      Write Str$(i)                   ;write the tick mark value
    EndIf
    k#=k#+j#                          ;increment the tick mark offset position
    i=i+10                            ;increment the tick mark count by 10
  Wend
  Line width-20,height0+20,width-20,height-35  ;draw the right side vertical scale line
  k#=hoffset                          ;begin at the center crosshairs
  i=0                                 ;set the tick count to zero
  While k#>=height0+30                ;don;t mess up the equation text area
    If k#<height Then                 ;don;t draw line if not in viewable area
      Line width-35,k#,width-20,k#    ;draw a tick mark against the vertical scale line
      n$=Str$(i)                      ;create the text for the tick count value
      Locate width-Len(n$)*8+5*(i<0),k#-5   ;position to write the tick count value
      Write n$                        ;write the tick count value
    EndIf
    k#=k#-j#                          ;decrement the tick mark offset position
    i=i-10                            ;decrement the tick mark count by 10
  Wend
  k#=hoffset+j#                       ;start at one tick mark below crosshairs
  i=10                                ;start with a count of ten
  While k#<=height-30                 ;continue until out of viewable area
    If k#>=height0 Then               ;delay line if not yet in viewable area
      Line width-35,k#,width-20,k#    ;draw tick mark up to vertical scale line
      n$=Str$(i)                      ;get tick mark count
      Locate width-Len(n$)*8+5,k#-5   ;position to write tick mark count
      Write Str$(i)                   ;write tick mark count
    EndIf
    k#=k#+j#                          ;increment tick mark position
    i=i+10                            ;increment tick mark count by 10
  Wend
EndIf
xlen=1000*zoom                        ;we arbitrarily make the cross hairs 1000 long
Color 128,255,128                     ;make the crosshairs the color of green
Line woffset-xlen,hoffset,woffset+xlen,hoffset  ;paint the horizontal crosshair first
Line woffset,hoffset-xlen,woffset,hoffset+xlen  ;paint the vertical crosshair second
Color 255,255,255                     ;set the current color to bright white
End Function

Function reduce$(expression$)
equation$=expression
ai=1
bi=0
While ai<=Len(equation)
  Select Mid$(equation,ai,1)
  Case " "
    equation=Left$(equation,ai-1)+Mid$(equation,ai+1)
  Case Chr$(9)
    equation=Left$(equation,ai-1)+Mid$(equation,ai+1)
  Case "+"
    If bi Then equation=Left$(equation,ai-1)+"-"+Mid$(equation,ai+1)
    ai=ai+1
  Case "-"
    If bi Then equation=Left$(equation,ai-1)+"+"+Mid$(equation,ai+1)
    ai=ai+1
  Case "="
    bi=1
    equation=Left$(equation,ai-1)+"-"+Mid$(equation,ai+1)
    ai=ai+1
  Default
    ai=ai+1
  End Select
Wend
While ai
  ai=Instr(equation,"++")
  If ai=0 Then ai=Instr(equation,"--")
  If ai Then
    equation=Left$(equation,ai-1)+"+"+Mid$(equation,ai+2)
  Else
    ai=Instr(equation,"+-")
    If ai=0 Then ai=Instr(equation,"-+")
    If ai Then
      equation=Left$(equation,ai-1)+"-"+Mid$(equation,ai+2)
    EndIf
  EndIf
Wend
ai=Instr(equation,"y")
bi=Instr(equation,"x")
ci=Instr(equation,"c")
If ci=0 Then ci=Len(equation)+1
di=1
While di
  If ai>bi Then
    di=ai
    ai=bi
    bi=di
  ElseIf ai>ci Then
    di=ai
    ai=ci
    ci=di
  ElseIf bi>ci Then
    di=bi
    bi=ci
    ci=bi
  Else
    di=0
  EndIf
Wend
If ai Then
  aj$=Left$(equation,ai)
Else
  aj=""
EndIf
placeit(aj)
If bi Then
  aj=Mid$(equation,ai+1,bi-ai)
Else
  aj=""
EndIf
placeit(aj)
If ci Then
  aj=Mid$(equation,bi+1,ci-bi+1)
Else
  aj=""
EndIf
placeit(aj)
For ai=1 To 3
  aj=val(ea(ai))
  If Instr(aj,".") Then aj=rational(aj,2)
  pn$(ai)=prime$(aj)
  aj=ea(ai)
  bi=Instr(aj,"/")
  If bi Then
    aj=Mid$(aj,bi)
  Else
    aj=Right$(aj,1)
    Select aj
    Case "x"
    Case "y"
    Default
      aj=""
    End Select
  EndIf
  ea(ai)=aj
Next
For ai=1 To 2
  aj=pn(ai)
  If aj="*0*" Then Goto chk_next2
  bi=1
  While bi
    ci=Instr(aj,"*",bi+1)
    If ci Then
      bj$=Mid$(aj,bi,ci-bi+1)
      For di=ai+1 To 3
        cj$=pn(di)
        ei=Instr(cj,bj)
        If ei=0 And cj<>"*0*" Then Goto chk_next1
      Next
      For di=1 To 3
        cj$=pn(di)
        ci=Instr(cj,bj)
        If ci Then
          cj=Left$(cj,ci)+Mid$(cj,ci+Len(bj))
          If cj="*" Then cj="*1*"
          pn(di)=cj
        EndIf
      Next
      ci=bi
    EndIf
.chk_next1
    bi=ci
  Wend
.chk_next2
Next
For ai=1 To 3
  aj=pn(ai)
  bi=1
  ci=1
  While ci
    di=Instr(aj,"*",ci+1)
    If di Then bi=bi*val(Mid$(aj,ci+1))
    ci=di
  Wend
  pr(ai)=bi
  cj=ea(ai)
  If ai>1 Then
    If bi>0 Then
       bj=bj+"+"
    ElseIf bi=0 And cj>"" Then
       bj=bj+"+"
    EndIf
    bj=bj+bi+cj
  Else
    bj=bi+cj
  EndIf
Next
Return bj
End Function

Function placeit(eax$)
Select Right$(eax,1)
Case "y"
  eay=1
Case "x"
  eay=2
Default
  eay=3
End Select
ea$(eay)=eax
Return eay
End Function

Function line_exp$(xa#,ya#,xb#,yb#)
u1#=ya#-yb#
u2#=xb#-xa#
u3#=u2*ya+u1*xa
If Abs(u1)>1 Then
  le$=prime$(u1)
ElseIf Abs(u2)>1 Then
  le$=prime$(u2)
Else
  le$=prime$(u3)
EndIf
s1=1
While s1
  s2=Instr(le,"*",s1+1)
  If s2 Then
    s3=val(Mid$(le,s1+1))
    If s3>1 Then
      If (u1 Mod s3)=0 And (u2 Mod s3)=0 And (u3 Mod s3)=0 Then
        u1=u1/s3
        u2=u2/s3
        u3=u3/s3
      EndIf
    EndIf
  EndIf
  s1=s2
Wend
gp(ap,1)=xa
gp(ap,2)=ya
gp(ap,3)=xb
gp(ap,4)=yb
gp(ap,5)=u1
gp(ap,6)=u2
gp(ap,7)=u3
le$=rational(u1,2)+"x+"+rational(u2,2)+"y+"+rational(-u3,2)
s1=1
While s1
  s1=Instr(le,"-+")
  If s1=0 Then s1=Instr(le,"+-")
  If s1 Then le=Left$(le,s1-1)+"-"+Mid$(le,s1+2)
Wend
s1=1
While s1
  s1=Instr(le,"--")
  If s1=0 Then s1=Instr(le,"++")
  If s1 Then le=Left$(le,s1-1)+"+"+Mid$(le,s1+2)
Wend
eqn(ap)=le
s1=1
While s1
  s1=Instr(le,"+0")
  If s1=0 Then s1=Instr(le,"-0")
  If s1 Then
    Select Mid$(le,s1+2,1)
    Case "y", "x"
     le=Left$(le,s1-1)+Mid$(le,s1+3)
    Default
      le=Left$(le,s1-1)+Mid$(le,s1+2)
    End Select
  EndIf
Wend
Return le
End Function

Function rational$(value#,pos)
places = 10^pos
r4# = -1
r1#=value-Int(value)
If Int(r1*places+.5)=0 Then
  pp$=value
  Goto not_rational
EndIf
r1# = 1/value
r5# = 1
r6# = 1
r0 = 8
Repeat  
  r0=r0-1
  If r0=0 Then Goto abort
  r5 = r1*r5
  r2# = 1/r1
  r1 = r2-Int(r2)
  If r1 = 0 Then r1 = 1
  r3# = r2/r1
  r5 = r5*r3
  r4 = Int(r5)
  r7# = Int(r4*value)
  pr7$=prime$(r7)
  an=1
  While an  ;eliminate matching primes from numerator and denominator
    bn=Instr(pr7,"*",an+1)
    If bn Then
      s1=val(Mid$(pr7,an+1))
      If s1 Then
        If r4 Mod s1=0 Then
          r7=r7/s1
          r4=r4/s1
        EndIf
      EndIf
    EndIf
    an=bn
  Wend
  r6=r7/r4-value
Until Int(r6#*places)=0
.abort
If r4<0 Then
  r7=-r7
  r4=-r4
EndIf
If r4>1 Then
  pp=r7
  s1=Instr(pp,".")
  If s1 Then pp=Left$(pp,s1-1)
  pp=pp+"/"+r4
Else
  pp=r7
EndIf
.not_rational
s1=Instr(pp,".")
If s1 Then pp=Left$(pp,s1-1)
Return pp
End Function

Function val#(some$)
ab#=some$
Return ab
End Function

Function prime$(number#)
ad=Int(Abs(number))
at$="*"
bd=1
cd=Sqr(ad)
dd=2
While dd<=cd
  If (ad Mod dd)=0 Then
    ad=ad/dd
    at=at+dd+"*"
  Else
    dd=dd+bd
    bd=2
  EndIf
Wend
If number#<0 Then at="*-1"+at
If ad>1 Then at=at+ad+"*"
Return at
End Function

Function Cramer$(n1,n2)
div#=gp(n1,5)*gp(n2,6)-gp(n2,5)*gp(n1,6)
If div Then
  div=1/div
Else
  div=10*20
EndIf
xn#=(gp(n1,7)*gp(n2,6)-gp(n2,7)*gp(n1,6))*div
yn#=(gp(n2,7)*gp(n1,5)-gp(n1,7)*gp(n2,5))*div
;The following code restricts intersections to areas between points
If xn>=gp(n1,1) And xn<=gp(n1,3) Then
  ;possible intersection along first line
ElseIf xn<=gp(n1,1) And xn>=gp(n1,3) Then
  ;possible intersection along first line
Else
  Goto nointersection
EndIf
If xn>=gp(n2,1) And xn<=gp(n2,3) Then
  ;possible intersection along second line
ElseIf xn<=gp(n2,1) And xn>=gp(n2,3) Then
  ;possible intersection along second line
Else
.nointersection
  Return "---,---"
EndIf
;End of code for restriction interesctions to areas between points
le$=rational(xn,2)
p1=le$
s1=Instr(le,"/")
If s1 Then
  q1=val(Mid$(le,s1+1))
  r1=Int(p1/q1)
  p1=P1-r1*q1
Else
  r1=0
  q1=0
EndIf
le=rational(yn,2)
p2=le$
s1=Instr(le,"/")
If s1 Then
  q2=val(Mid$(le,s1+1))
  r2=Int(p2/q2)
  p2=p2-r2*q2
Else
  r2=0
  q2=0
End If
If r1 Then 
  le=r1
  s1=Instr(le,".")
  If s1 Then le=Left$(le,s1-1)
  le=le+" "
Else
  le=""
EndIf
le=le+p1
s1=Instr(le,".")
If s1 Then le=Left$(le,s1-1)
If q1>1 Then
  le=le+"/"+q1
  s1=Instr(le,".")
  If s1 Then le=Left$(le,s1-1)
EndIf
le=le+","
If r2 Then 
  le=le+r2
  s1=Instr(le,".")
  If s1 Then le=Left$(le,s1-1)
  le=le+" "
EndIf
le=le+p2
s1=Instr(le,".")
If s1 Then le=Left$(le,s1-1)
If q2>1 Then
  le=le+"/"+q2
  s1=Instr(le,".")
  If s1 Then le=Left$(le,s1-1)
EndIf
Return le
End Function
