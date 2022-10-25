; ID: 2470
; Author: JoshK
; Date: 2009-05-05 16:46:22
; Title: Cipher
; Description: Simple string encryption

SuperStrict

Framework brl.map
Import brl.random

Rem
import brl.standardio
Local cipher:TCipher=TCipher.Create()
Local s:String="HelloHowAreYouToday"
Print s
s=cipher.Encrypt(s)
Print s
s=cipher.UnEncrypt(s)
Print s
EndRem

Type TCipher

	Field map:TMap[2]

	Method New()
		map[0]=New TMap
		map[1]=New TMap
	EndMethod
	
	Method Encrypt:String(s:String)
		Local r:String
		Local n:Int
		For n=0 To s.length-1
			r:+String(map[0].valueforkey(Chr(s[n])))
		Next
		Return r
	EndMethod
	
	Method Decrypt:String(s:String)
		Local r:String
		Local n:Int
		For n=0 To s.length-1
			r:+String(map[1].valueforkey(Chr(s[n])))
		Next
		Return r
	EndMethod
	
	Function Create:TCipher(seed:Int=0)
		Local cipher:TCipher
		Local char:String[62]
		Local n:Int,c:String
		
		SeedRnd(seed)
		cipher=New TCipher
		
		char[0]="0"
		char[1]="1"
		char[2]="2"
		char[3]="3"
		char[4]="4"
		char[5]="5"
		char[6]="6"
		char[7]="7"
		char[8]="8"
		char[9]="9"		
		char[10]="a"
		char[11]="b"
		char[12]="c"
		char[13]="d"
		char[14]="e"
		char[15]="f"
		char[16]="g"
		char[17]="h"
		char[18]="i"
		char[19]="j"
		char[20]="k"
		char[21]="l"
		char[22]="m"
		char[23]="n"
		char[24]="o"
		char[25]="p"
		char[26]="q"
		char[27]="r"
		char[28]="s"
		char[29]="t"
		char[30]="u"
		char[31]="v"
		char[32]="w"
		char[33]="x"
		char[34]="y"
		char[35]="z"
		char[36]="A"
		char[37]="B"
		char[38]="C"
		char[39]="D"
		char[40]="E"
		char[41]="F"
		char[42]="G"
		char[43]="H"
		char[44]="I"
		char[45]="J"
		char[46]="K"
		char[47]="L"
		char[48]="M"
		char[49]="N"
		char[50]="O"
		char[51]="P"
		char[52]="Q"
		char[53]="R"
		char[54]="S"
		char[55]="T"
		char[56]="U"
		char[57]="V"
		char[58]="W"
		char[59]="X"
		char[60]="Y"
		char[61]="Z"		
		
		For n=0 To char.length-1
			Repeat
				c:String=char[Rand(0,61)]
				If Not cipher.map[1].contains(c)
					cipher.map[0].insert char[n],c
					cipher.map[1].insert c,char[n]
					Exit
				EndIf
			Forever
		Next
		
		Return cipher
	EndFunction
	
EndType
