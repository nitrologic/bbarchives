; ID: 2076
; Author: jfk EO-11110
; Date: 2007-07-24 19:12:26
; Title: RND substitute
; Description: Generates random floats without to use RND()

Graphics 640,480,32,2
SetBuffer FrontBuffer()

Global rnd_inc


Repeat
 Plot  my_RND#(100,540), my_RND#(100,380)
Until KeyDown(1)

End




Function my_RND#(v1#,v2#)
 rnd_inc=(rnd_inc)+(ScanLine()*MilliSecs()) Mod 3500000000
 rnd_st$=Abs(rnd_inc)
 rnd_st$=Right$("000000"+Abs(rnd_inc),6)
 rnd_st$="0."+rnd_st$
 n#=v1+(Float(rnd_st$)*(v2-v1))
 Return n
End Function
