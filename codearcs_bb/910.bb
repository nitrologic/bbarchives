; ID: 910
; Author: Techlord
; Date: 2004-02-04 10:01:19
; Title: Sin/Cos Lookup Tables
; Description: Sin/Cos Lookup Tables

;SIN/COS Lookup Table
Dim Sin2#(360)
Dim Cos2#(360)
For loop=0 To 359
	Sin2#(loop)=Sin(loop)
	Cos2#(loop)=Cos(loop)
Next
