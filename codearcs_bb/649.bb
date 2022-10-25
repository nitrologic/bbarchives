; ID: 649
; Author: elias_t
; Date: 2003-04-13 08:54:56
; Title: Lens Flares
; Description: Create Lens Flares

;*************************************************************************
;
; lens flare creation lib [kinda]
;
; by elias_t
;
;*************************************************************************


;the radius of the flare
rad=128;radius

Graphics rad*2,rad*2,32,2


;set the buffer to write to
;[the functions work on the locked buffer]
buffer=FrontBuffer()
SetBuffer buffer






;examples-----------------------------------------------------------------



;rad,disk effect,r,g,b
sun1(rad,0,buffer,255,255,255)

SaveBuffer (buffer,"1.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;radius,size[1-100],brightness# [0.01 to 1.0],r,g,b
sun2(rad,100 ,0.99 ,buffer ,255,255,255)

SaveBuffer (buffer,"2.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;rad , smooth outline [.95 - .99],r,g,b
ring1(rad,.97,buffer ,255,255,255)

SaveBuffer (buffer,"3.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;radius,inner dark radius[1-10] , smooth outline [.95 - .99],r,g,b
ring2(rad ,10,.98,buffer ,255,255,255)

SaveBuffer (buffer,"4.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;rad , smooth outline [.95 - .99],r,g,b
ring3(rad,.98,buffer ,255,255,255)

SaveBuffer (buffer,"5.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;rad , size of ring [.01 - .9] (optional[0/1]: for sizes<.9 th#=thickness=1-size# ,r,g,b)
ring4(rad,.9, False ,buffer ,255,255,255)
SaveBuffer (buffer,"6.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;rad, size [=length of strikes]
;,no, [0-1.0]=strike start from center , [0-256]=brigtness [,r1,g1,b1,r2,g2,b2,r3,g3,b3] )
strikes(rad, 5,50 ,1.0 ,5 ,buffer ,255,255,255 ,245,245,245 ,240,240,240)

SaveBuffer (buffer,"7.bmp")
WaitKey():Delay 100:Cls:FlushKeys()



;rad,size [=length of strikes]
;,type=1,2,3,4,5 [0-1.0]=strike start from center , [0-256]=brigtness [,r1,g1,b1,r2,g2,b2,r3,g3,b3] )
;type 1=cross [4 strikes]
;type 2=cross rotated 45 degrees
;type 3=above 2 together
;type 4=20 strikes
;type 5=20 strikes random

strikes2(rad, 5,3, 1.0 ,30 ,buffer ,255,255,255  ,255,255,255 ,255,255,255)

SaveBuffer (buffer,"8.bmp")

WaitKey()


End





;*************************************************************************

Function sun1(r#, d#=0.0 ,buffer ,r1=255,g1=255,b1=255 )

LockBuffer buffer

d#=Abs(d#)

If d#>1.0 Then d#=1.0

For x#=0 To r#*2

	For y#=0 To r#*2

	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx#  + dy#*dy# )/r#

	c# = 1.0-ri#;
	c# = c#*c#;
	
	
	If (ri#>1.0) Then c#=0.0;

If ri#>d#
	rgb = c#*b1 Or (c#*g1 Shl 8) Or (c#*r1 Shl 16)
EndIf

	WritePixelFast(x, y, rgb ,buffer)

	Next

Next

UnlockBuffer buffer

End Function



;*************************************************************************



;*************************************************************************

Function sun2(r#,f=100 ,b#=1.0 ,buffer ,r1=255,g1=255,b1=255)

LockBuffer buffer

;============================
f=Abs(f)
b#=Abs(b#)

If f>100 Then f=100
If f<2 Then f=2

f=101-f


If b#>1.0 Then b#=1.0
If b#<.02 Then b#=.02

b#=1.01-b#


;===========================


For x#=0 To r#*2

	For y#=0 To r#*2

	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx#  + dy#*dy# )/r#

ri#=ri# - b#
ri#=Abs(ri#)
If ri#<b# Then ri# = b#

	c# = 1.0-ri#-b#;
	c# = c#^f;
	If (ri#>1.0-b#) Then c#=0.0;

If c#<>0.0 Then c#=c#+c#*b#


c#=c#+(c#*f )

If c#>1.0 Then c#=1.0

	rgb = c#*b1 Or (c#*g1 Shl 8) Or (c#*r1 Shl 16)

	WritePixelFast(x, y, rgb ,buffer)

	Next

Next


UnlockBuffer buffer

End Function



;*************************************************************************

Function ring1(r#,s#=.95 ,buffer ,r1=255,g1=255,b1=255)

LockBuffer buffer

s#=Abs(s#)
If s#<.95 Then s#=.95
If s#>.99 Then s#=.99

For x#=0 To r#*2

	For y#=0 To r#*2
	
	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx# + dy#*dy# )/r# 

	
	If ri#>s# And ri#<1.0
		ri# = (ri#-s#) / (1.0-s#)
		j#=(ri#*ri#) * (3.0-2.0*ri#)
		c# = 1.0-j#
	Else
		c# = ri#
	EndIf	

If (ri#>0.99999) Then c#=0.0


	rgb = c#*b1 Or (c#*g1  Shl 8) Or (c#*r1 Shl 16)

	WritePixelFast(x, y, rgb ,buffer)

	Next

Next

UnlockBuffer buffer

End Function

;*************************************************************************


Function ring2(r#,f=2,s# ,buffer ,r1=255,g1=255,b1=255)

LockBuffer buffer

f=Abs(f)
If f<1 Then f=1
If f>10 Then f=10

For x#=0 To r#*2

	For y#=0 To r#*2

	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx# + dy#*dy# )/r# 

	If ri#>s# And ri#<1.0
		ri# = (ri#-s#) / (1.0-s#)
		j#=(ri#*ri#) * (3.0-2.0*ri#)
		c# = 1.0-j#
	Else
		c# = ri#
	EndIf	

If (ri#>0.99999) Then c#=0.0
	
	c#=c#^f



	rgb = c#*b1 Or (c#*g1  Shl 8) Or (c#*r1 Shl 16)
	
	WritePixelFast(x, y, rgb ,buffer)

	Next

Next

UnlockBuffer buffer

End Function



;*************************************************************************

Function ring3(r# ,s# ,buffer ,r1=255,g1=255,b1=255)

LockBuffer buffer

For x#=0 To r#*2

	For y#=0 To r#*2

	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx# + dy#*dy# )/r# 


	If ri#>s# And ri#<1.0
		ri# = (ri#-s#) / (1.0-s#)
		j#=(ri#*ri#) * (3.0-2.0*ri#)
		c# = 1.0-j#
	Else
		c# = ri#*ri#;
		c# = c#*c#;
		c# = c#*c#*c#; 
	EndIf	




If (ri#>0.99999) Then c#=0.0


	rgb = c#*b1 Or (c#*g1  Shl 8) Or (c#*r1 Shl 16)
	
	WritePixelFast(x, y, rgb ,buffer)

	Next

Next

UnlockBuffer buffer

End Function



;*************************************************************************

Function ring4(r#,siz#=.9,th# ,buffer ,r1=255,g1=255,b1=255)

LockBuffer buffer

siz#=Abs(siz#)
If siz#<.01 Then siz#=.01
If siz#>.9 Then siz#=.9


If th#=True
th#=(1.0-siz#)
Else
th#=.1
EndIf


For x#=0 To r#*2

	For y#=0 To r#*2

	dx# =r#-x#
	dy# =r#-y#

	ri# = Sqr( dx#*dx# + dy#*dy# )/r# 

c# = 1-Abs(ri#-siz#)/th#;
 If (c# < 0) c# = 0;
 c# = c#*c#;
 c# = c#*c#; 

	If (ri#>1.0) Then c#=0.0;

	rgb = c#*b1 Or (c#*g1  Shl 8) Or (c#*r1 Shl 16)
	
	WritePixelFast(x, y, rgb ,buffer)

	Next

Next

UnlockBuffer buffer

End Function

;*************************************************************************





;*************************************************************************

Function strikes(r#, siz,no , f#=1.0 , of#=20 ,buffer ,r1=255,g1=255,b1=255 ,r2=255,g2=255,b2=255 ,r3=255,g3=255,b3=255)

LockBuffer buffer

f#=Abs(f#)
If f#>1.0 Then f#=1.0

of#=Abs(of#)
If of#>r# Then of#=r#

For k=1 To no


SeedRnd MilliSecs()*Rand(k)

	angle# = Rand(-180.0 , 180.0)
    dx# = Cos(angle#)
    dy# = Sin(angle#)

    fx# = r#
    fy# = r#


c#=1.0
d#=1.0
e#=1.0

count=of#

    For y# = -siz To siz
        For x# =-siz To siz

count=count-1

If count<0
c#=c#-.009
d#=d#-.01
e#=e#-.015
EndIf 
			
If c#<0.0 Then c#=0.0
If d#<0.0 Then d#=0.0
If e#<0.0 Then e#=0.0
			

If fx#<r#*2 And fx#>0 And fy#<r#*2 And fy#>0

If c#<=f# And count<0 Then rgb = c#*b1 Or (c#*g1 Shl 8) Or (c#*r1 Shl 16);
If count>0 Then rgb = c#*255 Or (c#*255 Shl 8) Or (c#*255 Shl 16);
If c#>f# Then rgb=0;

	WritePixelFast(fx#, fy#, rgb ,buffer)


If d#<=f# Then rgb = d#*b2 Or (d#*g2 Shl 8) Or (d#*r2 Shl 16);
If d#>f#Then rgb = 0;
	
t=ReadPixelFast(fx#, fy#-1)
If t=-16777216 Or t=0 Then WritePixelFast(fx#, fy#-1, rgb ,buffer)
t=ReadPixelFast(fx#-1, fy#)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-1, fy#, rgb ,buffer)
	
If e#<=f# Then rgb = e#*b3 Or (e#*g3 Shl 8) Or (e#*r3 Shl 16);
If e#>f# Then rgb = 0;

t=ReadPixelFast(fx#-1, fy#-2)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-1, fy#-2, rgb ,buffer)
t=ReadPixelFast(fx#-2, fy#-1)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-2, fy#-1, rgb ,buffer)



		
EndIf

        fx# = fx#+dx#
        fy# = fy#+dy#

Next
Next

Next

UnlockBuffer buffer

End Function

;*************************************************************************





;*************************************************************************

Function strikes2(r#, siz,t, f#=1.0 , of#=20 ,buffer ,r1=255,g1=255,b1=255 ,r2=255,g2=255,b2=255 ,r3=255,g3=255,b3=255)

LockBuffer buffer

f#=Abs(f#)
If f#>1.0 Then f#=1.0

of#=Abs(of#)
If of#>r# Then of#=r#

t=Abs(t):If t>5 Then t=5

If t=1
no=4:an=90:o=0
EndIf

If t=2
no=4:an=90:o=45
EndIf

If t=3
no=8:an=45:o=0
EndIf

If t=4
no=20:an=18:o=0
EndIf

If t=5
no=20:an=18:o=0
EndIf

For k=1 To no


SeedRnd MilliSecs()*Rand(k)

If t=5 Then o=Rand(5,15)
	
	angle# = Float(k*an+o);Rand(-180.0 , 180.0)
	
    dx# = Cos(angle#)
    dy# = Sin(angle#)

    fx# = r#
    fy# = r#


c#=1.0
d#=1.0
e#=1.0

count=of#

    For y# = -siz To siz
        For x# =-siz To siz

count=count-1

If count<0
c#=c#-.009
d#=d#-.01
e#=e#-.015
EndIf 
			
If c#<0.0 Then c#=0.0
If d#<0.0 Then d#=0.0
If e#<0.0 Then e#=0.0
			

If fx#<r#*2 And fx#>0 And fy#<r#*2 And fy#>0

If c#<=f# And count<0 Then rgb = c#*b1 Or (c#*g1 Shl 8) Or (c#*r1 Shl 16);
If count>0 Then rgb = c#*255 Or (c#*255 Shl 8) Or (c#*255 Shl 16);
If c#>f# Then rgb=0;

	WritePixelFast(fx#, fy#, rgb ,buffer)


If d#<=f# Then rgb = d#*b2 Or (d#*g2 Shl 8) Or (d#*r2 Shl 16);
If d#>f#Then rgb = 0;
	
t=ReadPixelFast(fx#, fy#-1)
If t=-16777216 Or t=0 Then WritePixelFast(fx#, fy#-1, rgb ,buffer)
t=ReadPixelFast(fx#-1, fy#)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-1, fy#, rgb ,buffer)
	
If e#<=f# Then rgb = e#*b3 Or (e#*g3 Shl 8) Or (e#*r3 Shl 16);
If e#>f# Then rgb = 0;

t=ReadPixelFast(fx#-1, fy#-2)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-1, fy#-2, rgb ,buffer)
t=ReadPixelFast(fx#-2, fy#-1)
If t=-16777216 Or t=0 Then WritePixelFast(fx#-2, fy#-1, rgb ,buffer)



		
EndIf

        fx# = fx#+dx#
        fy# = fy#+dy#

Next
Next

Next

UnlockBuffer buffer

End Function

;*************************************************************************
