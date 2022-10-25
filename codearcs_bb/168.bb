; ID: 168
; Author: Jeppe Nielsen
; Date: 2002-02-22 15:00:08
; Title: Cannonball physics
; Description: Code which shows how to move objects as if they were shot like a cannonball ( 2D )

;***********************************************
;*             Cannonball physics              *
;*                                             *
;*                     By                      *
;*                                             *
;*                Jeppe Nielsen                *
;*                                             *
;*            www.dahlsgaards.dk/blitz         *
;*                                             *
;*           nielsen_jeppe@hotmail.com         *
;*                                             *
;***********************************************

Type obj

Field mass#,speed#
Field x#,y#,sx#,sy#

End Type



Graphics 640,480,16,2


SetBuffer BackBuffer()


Global mass#=1,speed#=0,trust#=10,gravity#=0.05,windspeed#=0.04,windangle#=90

Global stx=320,sty=240,angle#,lastobj.obj

Global mx,my

Repeat
Cls
mx=MouseX()
my=MouseY()
trust#=Float(Sqr((stx-mx)^2+(sty-my)^2))/10
angle#=ATan2(my-sty,mx-stx)


Plot stx,sty
Line stx,sty,stx+30*Cos(angle),sty+30*Sin(angle)

Plot mx,my

Line 550,80,550+30*Cos(windangle),80+30*Sin(windangle)


Text 10,10,"Power :"+trust#
Text 10,20,"Angle :"+Int(angle#)
Text 10,30,"Obj mass :"+mass#
Text 450,10,"Wind speed :"+windspeed#
Text 450,20,"Wind angle :"+Int(windangle#)

If KeyDown(200) Then mass#=mass#+0.5
If KeyDown(208) Then mass#=mass#-0.5:If mass#<1 Then mass#=1

If lastobj<>Null Then Text 10,40,"Last obj speed :"+Str(Sqr(lastobj\sx#^2+lastobj\sy#^2))
updateall()
drawall()

If MouseDown(1) Then lastobj=createobj(mass#,stx,sty,trust#,angle)


dir=Int(Rnd(0,99))

If dir<5
windangle#=windangle#+1 
If windangle#>359 Then windangle#=0
Else 
windangle#=windangle#-1
If windangle#<0 Then windangle#=359
EndIf



Flip
Until KeyDown(1)

End


Function createobj.obj(mass#,x,y,inittrust#,angle)

o.obj=New obj

o\mass#=mass#
o\x=x
o\y=y
o\speed#=inittrust#/mass#
o\sx#=Cos(angle)*o\speed#
o\sy#=Sin(angle)*o\speed#

Return o

End Function


Function updateall()

windx#=windspeed#*Cos(windangle#)
windy#=windspeed#*Sin(windangle#)

For o.obj=Each obj
o\sx#=o\sx#+(windx#/o\mass#)
o\sy#=o\sy#+gravity#+(windy#/o\mass#)

o\x#=o\x#+o\sx#
o\y#=o\y#+o\sy#

If o\x<0 Or o\x>640 Or o\y<0 Or o\y>480 Then Delete o

Next


End Function


Function drawall()

For o.obj=Each obj
Oval o\x,o\y,o\mass,o\mass,0
Next

End Function
