; ID: 2662
; Author: Streaksy
; Date: 2010-03-08 15:58:22
; Title: Encode$() &amp; Decode() - Like Hex$() but use any character set
; Description: Super fast value refolding  - Convert any unsigned integer to a string using the key.  Can do hex, binary, trinary, anything you want.  And convert it back, of course.

;ENCODE / DECODE / SETCODEKEY / RANDOMIZECODEKEY
Dim nkeypos(256)
Dim ekey(256)
Dim keycache(64) ;32 is max really (max code length possible)
Global lastkey$,lastekey$,CodeKey$="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
Dim jum(256)












;EXAMPLE
Global FPS_fpstime ;FPS()
Graphics 800,600,32,2
API_ShowWindow(SystemProperty("AppHWND"),5)
SetBuffer BackBuffer()
SetFont LoadFont("lucida console",20)
keyword$="farmlegs"
k$=keyword:kd$="Keyword"
Repeat
Cls
msx=MouseX()
Color 150,0,0
Line msx,0,msx,1000
msxx=msx*2651611
If KeyDown(2) Then randomizecodekey:k=codekey:kd$="Full ASCII Set, Jumbled (Maximum Key Length)"
If KeyHit(3) Then k="0123456789 abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ":kd$="Bialphanumerical"
If KeyHit(4) Then k="0123456789ABCDEF":kd$="Hexidecimal"
If KeyHit(5) Then k=keyword:kd$="Keyword"
If KeyHit(6) Then k="abc":kd$="Trinary"
If KeyHit(7) Then k="01":kd$="Binary (Minimum key length)"
del=1
If MouseDown(1) Then del=5000
If MouseDown(2) Then del=del+10000:If del=10001 Then del=10000
om=MilliSecs()
For t=1 To del
e$=Encode(msxx,k)
de=decode(e,k)
Next
Dely=MilliSecs()-om
Color 255,150,90
Text 10,180,"KEYS:"
Text 10,200,"1 - Full ASCII Set, Jumbled (Maximum key length)"
Text 10,220,"2 - Bialphanumerical"
Text 10,240,"3 - Hexidecimal"
Text 10,260,"4 - Keyword"
Text 10,280,"5 - Trinary"
Text 10,300,"6 - Binary (Minimum key length)"
Text 10,360,"MOUSE-LEFT  -  +5000 Processes"
Text 10,380,"MOUSE-RIGHT - +10000 Processes"
Color 255,255,255
If msxx=de Then m$="MATCH" Else m="MISMATCH"
Text 10,30, "Integer: "+msxx
Text 10,50, "Encoded: ["+e+"]"
Text 10,70, "Decoded: "+de
Text 10,90, m
Color 0,255,0
Text 400,80,"PROCESSES PER FRAME: "+del
Text 400,100,"FPS: "+FPS()
s#=(Float(dely))/1000
Text 400,150,"DELAY: "+s+" secs"
Color 150,150,0
Text 0,500,"KEY: ["+k+"]"
Text 0,520,"KEY TYPE: "+kd
Text 0,540,"(KEY LENGTH: "+Len(k)+")"
Flip
Until KeyHit(1)
End
Function FPS()
oldtime=FPS_fpstime
FPS_fpstime=MilliSecs()
elapsed=FPS_fpstime-oldtime
If Not elapsed elapsed=1
FPS_fps=1000/elapsed
Return FPS_FPS
End Function

















;FUNCTIONS

Function encode$(v,key$="",minlen=0)
		If key<>"" And lastkey<>key Then ;check user keys for recurring characters
		For t=1 To Len(key)-1
		c$=Mid(key,t,1)
		For tt=t+1 To Len(key)
		c2$=Mid(key,tt,1):If c=c2 Then RuntimeError "SetCodeKey passed a key with one or more recurring characters."
		Next
		Next
		EndIf
If key="" Then key=codekey
L=Len(key)
If l<2 Or l>256 Then RuntimeError "Encode key is a bad size."
sol$=""
Repeat
i=v Mod l
v=(v-i)/L
sol=Mid(key,i+1,1)+sol
Until v=0
	If minlen>0 Then
	If Len(sol)<minlen Then
	Repeat
	sol=Mid(key,1,1)+sol
	ll=Len(sol)
	Until ll=>minlen
	EndIf
	EndIf
Return sol
End Function







Function decode(v$,key$="")
		If key<>"" And lastkey<>key Then ;check user keys for recurring characters
		For t=1 To Len(key)-1
		c$=Mid(key,t,1)
		For tt=t+1 To Len(key)
		c2$=Mid(key,tt,1):If c=c2 Then RuntimeError "SetCodeKey passed a key with one or more recurring characters."
		Next
		Next
		EndIf
If key="" Then key=codekey
If lastkey<>key Then newkey=1:lastkey=key
l=Len(key)
If l<2 Or l>256 Then RuntimeError "Encode key is a bad size."
sol=0
	If newkey Then
	For t=1 To l
	c$=Mid(key,t,1)
	nkeypos(Asc(c))=WhereInStringEncode(key,c)-1
	Next
	EndIf
		lv=Len(v)
		For t=1 To lv
		vl=nkeypos(Asc(Mid(v,t,1)))
		If vl>0 Then sol=sol+(vl*powerEncode(l,lv-t))
		Next
Return sol
End Function











Function powerEncode(v1,v2) ;for decode()  the same as ^ but blitz's ^ is sometimes defective with high integers
ov1=v1
If v2=0 Then Return 1
If v2=1 Then Return v1
For t=1 To v2-1
v1=v1*ov1
Next
Return v1
End Function





Function WhereInStringEncode(s$,t$) ;for decode()
lt=Len(t$):ls=Len(s$)
If lt>ls Then Return 0
For tt=1 To ls-lt+1
If Mid(s$,tt,lt)=t$ Then Return tt
Next
End Function



Function RandomizeCodeKey(seed=0,siz=256)
If seed=0 Then SeedRnd MilliSecs() Else SeedRnd seed
If siz<2 Or siz>256 Then RuntimeError "RandomizeCodeKey was passed a bad key size. ("+siz+")"
For t=1 To 256:jum(t)=t-1:Next
For t=1 To 500
p1=Rand(1,256)
p2=Rand(1,256)
If p1<>p2 Then
c1=jum(p1)
c2=jum(p2)
jum(p2)=c1
jum(p1)=c2
EndIf
Next
k$=""
For t=1 To siz
k=k+Chr(jum(t))
Next
codekey=k
End Function



Function SetCodeKey(k$)
If k=codekey Then Return
For t=1 To Len(k)-1
c$=Mid(k,t,1)
	For tt=t+1 To Len(k)
	c2$=Mid(k,tt,1):If c=c2 Then RuntimeError "SetCodeKey passed a key with one or more recurring characters."
	Next
Next
CodeKey$=k$
End Function




Function Hexify$(v,sz=8)
Return encode(v,"0123456789ABCDEF",sz)
End Function

Function UnHexify(v$)
Return decode(v,"0123456789ABCDEF")
End Function
