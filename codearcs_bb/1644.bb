; ID: 1644
; Author: ANIMAL
; Date: 2006-03-17 05:26:17
; Title: silly patterns
; Description: silly patterns

; auther animal
; silly patterns






Graphics 800,600,16,2
SetBuffer BackBuffer()


q=0
Gosub val
While Not KeyDown(1)

t=t+q
x=ox+r*Sin(t)
y=oy+r*Cos(t)
Color Rnd(1000),Rnd(1000),Rnd(1000)
Line ox,oy,x,y
ox=x:oy=y
r=r+1
If x<0 Or x>800  Then Gosub val

Flip

Wend

x=0:y=0:ox=0:oy=0:End


.val

Cls 
q=30+(300)*10
ox=400:oy=250
x=400:y=250
r=0
Return
