; ID: 1141
; Author: starfox
; Date: 2004-08-21 17:52:59
; Title: Filled Quad
; Description: Fills quads with a certain one color

Function FilledQuad(p1x,p1y,p2x,p2y,p3x,p3y,p4x,p4y,gridprecise=2)
Local farleftx,farlefty ;Find the point farthest to the left
Local farrightx,farrighty ;Find the point farthest to the right
Local mid1x,mid1y ;Find the point second farthest to the left
Local mid2x,mid2y ;Find the point second farthest to the right
Local farleftid,farrightid,mid1id,mid2id

;Find parallel points
par1x = p1x
par1y = p1y
par2x = p4x
par2y = p4y

If Abs(par1x-p1x) < 2
count = count + 1
EndIf
If Abs(par1x-p2x) < 2
count = count + 1
EndIf
If Abs(par1x-p3x) < 2
count = count + 1
EndIf
If Abs(par1x-p4x) < 2
count = count + 1
EndIf
If count >= 3 Then Return ;Can't do anything to that!
count=0
If Abs(par2x-p1x) < 2
count = count + 1
EndIf
If Abs(par2x-p2x) < 2
count = count + 1
EndIf
If Abs(par2x-p3x) < 2
count = count + 1
EndIf
If Abs(par2x-p4x) < 2
count = count + 1
EndIf
If count >= 3 Then Return ;Can't do anything to that!
count=0
If Abs(par1y-p1y) < 2
count = count + 1
EndIf
If Abs(par1y-p2y) < 2
count = count + 1
EndIf
If Abs(par1y-p3y) < 2
count = count + 1
EndIf
If Abs(par1y-p4y) < 2
count = count + 1
EndIf
If count >= 3 Then Return ;Can't do anything to that!
count=0
If Abs(par2y-p1y) < 2
count = count + 1
EndIf
If Abs(par2y-p2y) < 2
count = count + 1
EndIf
If Abs(par2y-p3y) < 2
count = count + 1
EndIf
If Abs(par2y-p4y) < 2
count = count + 1
EndIf
If count >= 3 Then Return ;Can't do anything to that!

;Long unneccesary function to detect locations:p

farleftx = p1x
farlefty = p1y
farleftid=1

If farleftx > p2x
farleftx = p2x
farlefty = p2y
farleftid=2
EndIf

If farleftx > p3x
farleftx = p3x
farlefty = p3y
farleftid=3
EndIf

If farleftx > p4x
farleftx = p4x
farlefty = p4y
farleftid=4
EndIf

farrightx = p1x
farrighty = p1y
farrightid=1

If farrightx < p2x
farrightx = p2x
farrighty = p2y
farrightid=2
EndIf

If farrightx < p3x
farrightx = p3x
farrighty = p3y
farrightid=3
EndIf

If farrightx < p4x
farrightx = p4x
farrighty = p4y
farrightid=4
EndIf

If farleftid <> 1 And farrightid <> 1
mid1x = p1x
mid1y = p1y
mid1id=1
EndIf

If farleftid <> 2 And farrightid <> 2
If mid1id <> 0
mid2x = p2x
mid2y = p2y
mid2id=2
Else
mid1x = p2x
mid1y = p2y
Mid1id = 2
EndIf
EndIf

If farleftid <> 3 And farrightid <> 3
If mid1id <> 0
mid2x = p3x
mid2y = p3y
mid2id=3
Else
mid1x = p3x
mid1y = p3y
Mid1id = 3
EndIf
EndIf

If farleftid <> 4 And farrightid <> 4
If mid1id <> 0
mid2x = p4x
mid2y = p4y
mid2id=4
Else
mid1x = p4x
mid1y = p4y
Mid1id = 4
EndIf
EndIf

col = ray2dintersect(farleftx,farlefty,mid1x,mid1y,mid2x,mid2y,farrightx,farrighty)
If col = 1 Then changdir=1
col = ray2dintersect(farleftx,farlefty,mid2x,mid2y,mid1x,mid1y,farrightx,farrighty)
If col = 1 Then changdir=2

;2D vectors
Local vec1x#,vec1y#,vec1step#
Local vec2x#,vec2y#,vec2step#

;Quad Picture
;   O---------O
;  /         /
; /         /
;O---------O

vec1x = farleftx:vec1y = farlefty
vec2x = farleftx:vec2y = farlefty


dist1# = Abs(mid1x-farleftx)
If dist1 = 0 Then dist1 = 1
dist2# = Abs(mid2x-farleftx)
If dist2 = 0 Then dist2 = 1
alldist = Abs(farrightx-farleftx)

vec1step = (mid1y-farlefty)/(dist1)
vec2step = (mid2y-farlefty)/(dist2)

If mid1x-farleftx = 0
vec1y = vec1y + vec1step
EndIf

If mid2x-farleftx = 0
vec2y = vec2y + vec2step
EndIf

If changdir = 2
dist2# = Abs(farrightx-farleftx)
If dist2 = 0 Then dist2 = 1
vec2step = (farrighty-farlefty)/(dist2)

If farrightx-farleftx = 0
vec2y = vec2y + vec2step
EndIf

ElseIf changdir = 1
dist1# = Abs(farrightx-farleftx)
If dist1 = 0 Then dist1 = 1
vec1step = (farrighty-farlefty)/(dist1)

If farrightx-farleftx = 0
vec1y = vec1y + vec1step
EndIf

EndIf

starttea = farleftx
endtea = farrightx

For tea =  starttea To endtea

If tea Mod gridprecise = 0
If vec1y < vec2y

	If tea >= 0 And tea <= 640
	If vec1y >= 0 And vec1y <= 480
	Rect(tea,vec1y,gridprecise,vec2y-vec1y+1)
	EndIf
	EndIf
Else
	If tea >= 0 And tea <= 640
	If vec2y >= 0 And vec2y <= 480
	Rect(tea,vec2y,gridprecise,vec1y-vec2y+1)
	EndIf
	EndIf
EndIf
EndIf

	If tea >= mid1x And gotomid1=0
	If changdir=0
	dist1# = (farrightx-mid1x)
	If dist1 = 0 Then dist1 = 1
	vec1step = (farrighty-mid1y)/(dist1)
	ElseIf changdir = 2
		dist1# = (mid2x-mid1x)
		If dist1 = 0 Then dist1 = 1
		vec1step = (mid2y-mid1y)/(dist1)
	ElseIf changdir = 1
		dist2# = (farrightx-mid1x)
		If dist2 = 0 Then dist2 = 1
		vec2step = (farrighty-mid1y)/(dist2)
	EndIf
	gotomid1=1
	EndIf
	
	If tea >= mid2x And gotomid2=0
	If changdir=0
	dist2# = (farrightx-mid2x)
	If dist2 = 0 Then dist2 = 1
	vec2step = (farrighty-mid2y)/(dist2)
	ElseIf changdir=2
		dist1# = (farrightx-mid2x)
		If dist1 = 0 Then dist1 = 1
		vec1step = (farrighty-mid2y)/(dist1)
	ElseIf changdir=1
		dist2# = (mid1x-mid2x)
		If dist2 = 0 Then dist2 = 1
		vec2step = (mid1y-mid2y)/(dist2)
	EndIf
	gotomid2=1
	EndIf
	
	vec1y = vec1y + vec1step
	vec2y = vec2y + vec2step
	
Next


End Function
