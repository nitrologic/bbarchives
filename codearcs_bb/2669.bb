; ID: 2669
; Author: Streaksy
; Date: 2010-03-18 11:24:15
; Title: Handy Exotic Maths
; Description: Lots of handy maths essentials.

Global sm$=Chr$(34) ;convenience speech-mark character (quote)

Global floatyrez=10000
Global TriCenterX2D#,TriCenterY2D#

Global RotatedPointX#,RotatedPointY#




; MATHS

;returns value between V1 and V2 with T as tween (0-1)
Function Between#(v1#,v2#,t#):dif#=v2-v1
Return v1+(dif*t)
End Function

;distance between 2 2D points
Function Distance#(x1#,y1#,x2#,y2#)
Return Sqr((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))
End Function

;distance between 2 3D points
Function Distance3D#(x1#,y1#,z1#,x2#,y2#,z2#)
Return Sqr#((x1-x2)^2+(y1-y2)^2+(z1-z2)^2)
End Function

;returns the tween (0-1) of where between L and R the value M is
Function WhereBetween#(l#,r#,m#)
r=r-l:m=m-l
Return m/r
End Function

;returns the absolute difference between two values
Function Difference#(val1#,val2#)
Return Abs(val1-val2)
End Function

;returns whether an integer is an even number
Function Even(v)
If Abs(v)=1 Then Return 0
Return Not (Abs(v) And 1)
End Function

;returns whether an integer is an odd number
Function Odd(x)
Return Abs(x) And 1
End Function

;returns v1 to the power of v2.  (blitz's `^' operation is buggy with high values, and sometimes in general.  this was just as fast in the stress-test anyway)
Function power(v1,v2)
ov1=v1
If v2=0 Then Return 1
If v2=1 Then Return v1
For t=1 To v2-1
v1=v1*ov1
Next
Return v1
End Function

;rounds a float to the closest integer
Function Round(val#) 
dec#=val-Floor(val):If dec<.5 Return Floor(val) Else Return Ceil(val)
End Function


;curve a value based on its range and a curve type and amplitude (see my Curve() upload in the code archives for better example)
Function Curve#(val#,min#,max#,typ=3,amp#=1)
val=val-min
max=max-min
If amp<>1 Then olval#=val
tween#=((val/max)*90)
If typ=<1 Then cos1#=Cos(tween-90):val=cos1*max					;smooth out
If typ=2 Then cos1#=1-Cos((tween)):val=cos1*max					;smooth in
If typ=3 Then cos1#=Cos(tween-90)*Sin(tween):val=cos1*max		;smooth in and out
If amp<>1 Then dif#=olval-val:val#=olval-(dif*amp) ;amplify
Return val+min
End Function

;returns the difference between two angles in degrees
Function AngleDifference#(angle1#,angle2#)
Return ((angle2 - angle1) Mod 360 + 540) Mod 360 - 180
End Function

;returns an angle after turning it to another angle a given amount (not as straight-forward as it sounds)
Function TurnTo#(ang#,angto#,turnamt#)
a1#=ang
a2#=ang
For t=1 To 180
a1#=a1#-1
a2#=a2#+1
If a1<0 Then a1=a1+360
If a2=>360 Then a2=a2-360
If Int(a1)=Int(angto) Then
ang=ang-turnamt
EndIf
If Int(a2)=Int(angto) Then
ang=ang+turnamt
EndIf
Next
Return ang
End Function

;returns the X vector(?) of an angle
Function AngleX#(aa#)
Return Cos(aa-90)
End Function

;returns the Y vector(?) of an angle
Function AngleY#(aa#)
Return Sin(aa-90)
End Function

;returns the direction that point B is from point A (I think I borrowed this from someone else... sincere apologies!!! but info should be spread)
Function Direction#(EnX#,EnY#,OtX#,OtY#)
If OtX#>EnX# And EnY#>=OtY# Then Return ATan((OtX#-EnX#)/(EnY#-OtY#))
If OtX#>=EnX# And OtY#>EnY# Then Return 90+ATan((OtY#-EnY#)/(OtX#-EnX#))
If EnX#>OtX# And OtY#>=EnY# Then Return 180+ATan((EnX#-OtX#)/(OtY#-EnY#))
If EnX#>=OtX# And EnY#>OtY# Then Return 270+ATan((EnY#-OtY#)/(EnX#-OtX#))
End Function

;limit a value to a minimum and maximum
Function Clamp#(V#,l#,u#)
Return Lim(v,l,u)
End Function

;limit a value to a minimum and maximum
Function Lim#(vl#,lw#,up#) ; A.K.A - Clamp
If vl<lw Then Return lw
If vl>up Then Return up
Return vl:End Function

;return a lower resolution float (reduce it to 2 decimal places)
Function Dec2#(v#)
Return Float(Int(v*10))/10
End Function

;return a lower resolution float (reduce it to 3 decimal places)
Function Dec3#(v#)
Return Float(Int(v*100))/100
End Function

;return a lower resolution float (reduce it to 4 decimal places)
Function Dec4#(v#)
Return Float(Int(v*1000))/1000
End Function

;returns a string of an integer with the given number of digits (places any zero on the front that it needs to)
Function PadNum$(val,digits=2)
v$=val:If Len(v$)<digits Then Repeat:v$="0"+v$:Until Len(v$)=digits
Return v$:End Function

;wrap a value between a minimum and a maximum (i love this.. never seen it done before :P )
Function Wrap(val,min,max)
If val=>min And val=<max Then Return val
If val>max Then val=val-min:max=max-min:max=max+1:Return min+(val Mod max)
If val<min Then min2=-min:max2=-max:max=min2:min=max2:val=-val:val=val-min:max=max-min:max=max+1:Return 0-(min+(val Mod max))
End Function

;wrap a float value... slower and clunkier but nessecities must...
Function WrapFloat#(val#,min#,max#)
span#=Abs(max-min)
.gain
cosh=cosh+1:If cosh>100000 Then RuntimeError "Wrap trap!"
If val>max Then val=val-span:Goto gain
If val<min Then val=val+span:Goto gain
Return val
End Function




;turn an integer into a string, using however many chracters (bytes) it takes
Function Word$(val)
Repeat
res$=res$+Chr(val Mod 255)
val=Int(val/255)
Until val=0
Return res$
End Function

;undo the affects of Word$(), turning a string back into an integer
Function UnWord(w$)
power=1
For t=1 To Len(w)
val=val+(Asc(Mid(w,t,1))*power)
power=power*255
Next
Return val
End Function





;turn a float into a string, using however many chracters (bytes) it takes.  The resolution (decimal places) is set at the top of the code
Function Floaty$(v#)
vv=Floor(v)
v2#=v-vv
v2s$=Str(v2):v2s=Right(v2s,Len(v2s)-2):If Len(v2s)>floatyrez Then v2s=Left(v2s,floatyrez)
vs$=Str(v)
w1$=xword(vv)
If v2<>0 Then w2$=xword(Int(v2s))
Return Chr(Len(w1))+w1+w2
End Function

;undo the affects of Floaty$(), turning a string back into a float
Function Unfloaty#(v$)
w=Asc(Left(Str(v),1))
v=Right(v,Len(v)-1)
w1$=xunword(Left(v,w))
If w<Len(v) Then w1=w1+"."+xunword(Right(v,Len(v)-w))
vv#=Float(w1)
Return vv#
End Function



Function xWord$(val) ;for floaty()
Repeat
res$=res$+Chr(val Mod 254)
val=Int(val/254)
Until val=0
res=Replace(res," ",Chr(255))
Return res$
End Function

Function xUnWord(w$) ;for unfloaty()
w=Replace(w,Chr(255)," ")
power=1
For t=1 To Len(w)
val=val+(Asc(Mid(w,t,1))*power)
power=power*254
Next
Return val
End Function


Function RectsOverlap%(x1, y1, w1, h1, x2, y2, w2, h2)
If x1>(x2+w2) Or (x1+w1)<x2 Then Return False
If y1>(y2+h2) Or (y1+h1)<y2 Then Return False
Return True
End Function





;I love this too.  also never seen it done.
;It returns a random value, but a float!  You can specify the resolution... so if resolution is .1 then it will return to 1 decimal place
Function Randm#(min=0,max,res#=1) ;res is resolution.  so random(0,1,.1) will be 0,.1,.2,.3,.4,.5,.6,.7,.8 or .9... random(5,20,5) will be 5,10,15,20
nums=(max-min)/res
Return (Rand(0,nums)*res)+min
End Function


;returns the angle after it has bounced off a "wall".  entrot# is the angle the "ball" is travelling, and Wallface# is the angle the wall is facing
Function Bounce2D#(entrot#,wallface#)
Return (wallface*2)-entrot
End Function


;get the center (really just a point inside) of a triangle (a bit rough but seems solid enough.  could use some improvement)
;it doesnt really return the centre (my trig is terrible.. someone help :P ) but it returns a point inside the triangle
;Resulting values are recorded in the global variables: TriCenterX2D# and TriCenterY2D#
Function GetTriangleCenter2D(x1#,y1#,   x2#,y2#,   x3#,y3#)
cx#=(x2+x3)/2
cy#=(y2+y3)/2
dir#=direction(x1,y1,cx,cy)
dis#=distance(x1,y1,cx,cy)
TriCenterX2D#=x1+  (      (   anglex(dir) * (dis/2)   )     )
TriCenterY2D#=y1+  (      (   angley(dir) * (dis/2)   )     )
End Function


;another one im rather proud of.  it returns the distance from a point to an infinite line.
Function DistanceFromLine#(x1#,y1#,x2#,y2#,   x#,y#)	;THE LINE IS EXTENDED FOREVER!  THIS JUST RETURNS A PARALLEL Y DIFFERENCE !
dir#=direction(x1,y1,x2,y2)+90
cx#=between(x1,x2,.5):cy#=between(y1,y2,.5)
pointdir#=direction(cx,cy,x,y)
pointdis#=distance(cx,cy,x,y)
newang#=pointdir-dir+180
Return (angley(newang)*pointdis)
End Function


;another favourite.  this rotates the new position of point A after it has been rotated around point B with the given angle.
;Resulting values are recorded in the global variables: RotatedPointX# and RotatedPointY#
Function RotatePoint(x#,y#,  ang#,  pivx#=0,pivy#=0)
dis#=distance(pivx,pivy,x,y)
dir#=direction(pivx,pivy,x,y)+ang
RotatedPointX#=pivx+(anglex(dir)*dis)
RotatedPointY#=pivy+(angley(dir)*dis)
End Function


;returns whether two lines cross.  this is one of the functions i found in the code archives.
;apologies to author for not noting who you are when i took this
Function LinesCross(x0#,y0#, x1#,y1#, x2#,y2#, x3#,y3# ,parralelcheck=1,pointtouch=0)   ;parralel means the lines dont cross but "overlap"
If pointtouch Then
If Abs(x0-x2)<.001 And Abs(y0-y2)<.001 Then Return True
If Abs(x1-x3)<.001 And Abs(y1-y3)<.001 Then Return True
EndIf
n# = (y0#-y2#)*(x3#-x2#) - (x0#-x2#)*(y3#-y2#)
d# = (x1#-x0#)*(y3#-y2#) - (y1#-y0#)*(x3#-x2#)
If Abs(d#) < 0.0001 
; Lines are parallel!
	If parralelcheck Then
	dir#=-direction(x0,y0,x1,y1)-90
	cx#=between(x0,x1,.5)
	cy#=between(y0,y1,.5)
	RotatePoint(x0,y0,dir,cx,cy)
	xx1#=RotatedPointX
	yy1#=RotatedPointY
	RotatePoint(x1,y1,dir,cx,cy)
	xx2#=RotatedPointX
	yy2#=RotatedPointY
	RotatePoint(x2,y2,dir,cx,cy)
	xx3#=RotatedPointX
	yy3#=RotatedPointY
	RotatePoint(x3,y3,dir,cx,cy)
	xx4#=RotatedPointX
	yy4#=RotatedPointY
	If xx1>xx2 Then xxxx=xx1:xx1=xx2:xx2=xxxx
	If xx3>xx4 Then xxxx=xx3:xx3=xx4:xx4=xxxx
	xx2=xx2-10
	xx3=xx3+10
	If xx2>xx3 And xx1<xx3 Then Return True
	If xx1<xx4 And xx2>xx4 Then Return True
Else
Return False
EndIf
Else
; Lines might cross!
Sn# = (y0#-y2#)*(x1#-x0#) - (x0#-x2#)*(y1#-y0#)
AB# = n# / d#
If AB#>0.0 And AB#<1.0
CD# = Sn# / d#
If CD#>0.0 And CD#<1.0
; Intersection Point
X# = x0# + AB#*(x1#-x0#)
Y# = y0# + AB#*(y1#-y0#)
Return True
End If
End If
; Lines didn't cross, because the intersection was beyond the end points of the lines
EndIf
; Lines do not cross!
Return False
End Function


;returns whether a point is within a given triangle (looks like i borrowed this too but i needed it for more functions below :D )
;wish i recorded the author of these functions so I could credit them...  so sorry... if anyone knows...
Function InTriangle(x0#,y0#   ,x1#,y1#,x2#,y2#,x3#,y3#)
b0# =  (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
b1# = ((x2 - x0) * (y3 - y0) - (x3 - x0) * (y2 - y0)) / b0 
b2# = ((x3 - x0) * (y1 - y0) - (x1 - x0) * (y3 - y0)) / b0
b3# = ((x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0)) / b0 
If b1>0 And b2>0 And b3>0 Then Return True Else Return False
End Function

;return whether a line enters a triangle.  its probably not 100% perfect but it worked out great for a certain project
Function LineInTriangle(lx1#,ly1#,lx2#,ly2#,   tx1#,ty1#,tx2#,ty2#,tx3#,ty3#)
If intriangle(lx1,ly1,  tx1,ty1,tx2,ty2,tx3,ty3) Then Return True
If intriangle(lx2,ly2,  tx1,ty1,tx2,ty2,tx3,ty3) Then Return True
If linescross(lx1,ly1,lx2,ly2,   tx1,ty1,tx2,ty2,0,0) Then Return True
If linescross(lx1,ly1,lx2,ly2,   tx2,ty2,tx3,ty3,0,0) Then Return True
If linescross(lx1,ly1,lx2,ly2,   tx1,ty1,tx3,ty3,0,0) Then Return True
dis#=distance(lx1,ly1,lx2,ly2)
steps=5
For t#=0 To steps-1 Step 1
tw#=t/Float(steps-1)
x#=between(lx1,lx2,tw)
y#=between(ly1,ly2,tw)
If Abs(distancefromline(tx1,ty1,tx2,ty2,   x,y))>.00001 Then	;make sure its not too close to a triangle edge!
If Abs(distancefromline(tx2,ty2,tx3,ty3,   x,y))>.00001 Then
If Abs(distancefromline(tx1,ty1,tx3,ty3,   x,y))>.00001 Then
If intriangle(x,y,  tx1#,ty1#,tx2#,ty2#,tx3#,ty3#) Then Return True
EndIf
EndIf
EndIf
Next
End Function

;same as above but a bit less sophisticated.  I cant remember why I kept this or even what the difference is
Function LineInTriangleSimple(lx1#,ly1#,lx2#,ly2#,   tx1#,ty1#,tx2#,ty2#,tx3#,ty3#)
If intriangle(lx1,ly1,  tx1,ty1,tx2,ty2,tx3,ty3) Then Return True
If intriangle(lx2,ly2,  tx1,ty1,tx2,ty2,tx3,ty3) Then Return True
If linescross(lx1,ly1,lx2,ly2,  tx1,ty1,tx2,ty2,0,0) Then Return True
If linescross(lx1,ly1,lx2,ly2,  tx2,ty2,tx3,ty3,0,0) Then Return True
If linescross(lx1,ly1,lx2,ly2,  tx1,ty1,tx3,ty3,0,0) Then Return True
dis#=distance(lx1,ly1,lx2,ly2)
For t#=dis*.1 To dis*.9 Step .1
tw#=t/dis
x#=between(lx1,lx2,tw)
y#=between(ly1,ly2,tw)
If intriangle(x,y,tx1#,ty1#,tx2#,ty2#,tx3#,ty3#) Then Return True
Next
End Function

;i guess this is a more accurate version of LineInTriangle.  I made these a while ago so I forget :P
Function LineInTriangleHiDef(lx1#,ly1#,lx2#,ly2#,   tx1#,ty1#,tx2#,ty2#,tx3#,ty3#)
dis#=distance(lx1,ly1,lx2,ly2)
;dir#=direction(lx1,ly1,lx2,ly2)
If intriangle(lx2,ly2,  tx1#,ty1#,tx2#,ty2#,tx3#,ty3#) Then Return True
For t#=0 To dis Step .1
tw#=t/dis
x#=between(lx1,lx2,tw)
y#=between(ly1,ly2,tw)
If Abs(distancefromline(tx1,ty1,tx2,ty2,   x,y))>.00001 Then	;make sure its not too close to a triangle edge!
If Abs(distancefromline(tx2,ty2,tx3,ty3,   x,y))>.00001 Then
If Abs(distancefromline(tx1,ty1,tx3,ty3,   x,y))>.00001 Then
If intriangle(x,y,  tx1#,ty1#,tx2#,ty2#,tx3#,ty3#) Then Return True
EndIf
EndIf
EndIf
Next
End Function
