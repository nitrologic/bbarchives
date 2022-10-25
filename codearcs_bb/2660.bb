; ID: 2660
; Author: Streaksy
; Date: 2010-03-05 13:32:30
; Title: XGetKey()
; Description: Better alternative to the native GetKey()

;XGETKEY'S STUFF
Const keydelay=300,keydelay2=40
Dim okdn(255)
Dim keymap(255)
Global lastkey,keytime,xlastkey
keymap(2)=49
keymap(3)=50
keymap(4)=51
keymap(5)=52
keymap(6)=53
keymap(7)=54
keymap(8)=55
keymap(9)=56
keymap(10)=57
keymap(11)=48
keymap(12)=45
keymap(13)=61
keymap(14)=8
keymap(15)=9
keymap(16)=113
keymap(17)=119
keymap(18)=101
keymap(19)=114
keymap(20)=116
keymap(21)=121
keymap(22)=117
keymap(23)=105
keymap(24)=111
keymap(25)=112
keymap(26)=91
keymap(27)=93
keymap(28)=13
keymap(30)=97
keymap(31)=115
keymap(32)=100
keymap(33)=102
keymap(34)=103
keymap(35)=104
keymap(36)=106
keymap(37)=107
keymap(38)=108
keymap(39)=59
keymap(40)=39
keymap(41)=96
keymap(43)=35
keymap(44)=122
keymap(45)=120
keymap(46)=99
keymap(47)=118
keymap(48)=98
keymap(49)=110
keymap(50)=109
keymap(51)=44
keymap(52)=46
keymap(53)=47
keymap(57)=32
keymap(86)=92
keymap(201)=5
keymap(207)=2
keymap(209)=6
keymap(210)=3
keymap(211)=4
keymap(199)=1

keymap(200)=28 ;up
keymap(208)=29 ;down
keymap(203)=31 ;left
keymap(205)=30 ;right





; THE FUNCTION
Function XGetKey()
ms=MilliSecs()
For t=1 To 255
If keymap(t)>0 Then
If KeyDown(t) Then
	kp=1
	If okdn(t)=0 Then
	keytime=Ms
	force=1
	lastkey=keymap(t)
	okdn(t)=1
	EndIf
Else
	okdn(t)=0
EndIf
EndIf
Next
If kp=0 Then lastkey=0:keytime=0
xlastkey=lastkey
If force=0 Then If keytime>0 Then If (ms-keytime) < keydelay Then Return
If keytime>0 Then If ms-keytime =>keydelay Then keytime=0
If lastkey>0 And keytime=0 Then keytime=Ms-(keydelay)+keydelay2
ooo=lastkey
If ooo>0 Then
If KeyDown(42) Or KeyDown(54) Then shift=1 Else shift=0
If shift Then
If Chr(ooo)="1" Then ooo=Asc("!")
If Chr(ooo)="2" Then ooo=34
If Chr(ooo)="3" Then ooo=Asc("£")
If Chr(ooo)="4" Then ooo=Asc("$")
If Chr(ooo)="5" Then ooo=Asc("%")
If Chr(ooo)="6" Then ooo=Asc("^")
If Chr(ooo)="7" Then ooo=Asc("&")
If Chr(ooo)="8" Then ooo=Asc("*")
If Chr(ooo)="9" Then ooo=Asc("(")
If Chr(ooo)="0" Then ooo=Asc(")")
If Chr(ooo)="`" Then ooo=Asc("¬")
If Chr(ooo)="-" Then ooo=Asc("_")
If Chr(ooo)="=" Then ooo=Asc("+")
If Chr(ooo)="[" Then ooo=Asc("{")
If Chr(ooo)="]" Then ooo=Asc("}")
If Chr(ooo)=";" Then ooo=Asc(":")
If Chr(ooo)="'" Then ooo=Asc("@")
If Chr(ooo)="#" Then ooo=Asc("~")
If Chr(ooo)="," Then ooo=Asc("<")
If Chr(ooo)="." Then ooo=Asc(">")
If Chr(ooo)="/" Then ooo=Asc("?")
If Chr(ooo)="\" Then ooo=Asc("|")
ooo=Asc(Upper(Chr(ooo)))
EndIf
EndIf
Return ooo
End Function
