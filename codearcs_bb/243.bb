; ID: 243
; Author: skn3[ac]
; Date: 2002-02-15 07:36:28
; Title: TEXT input Lib
; Description: Fucntion that returns String of keypresses

Repeat
a$=get_keyinput$()
	If a$="ENTER" Then 
		Print
	Else If a$<>"" Then
		Write a$
	End If
Until KeyDown(1)=True


Function get_keyinput$()
	;rest values
	func2=False
	key$=""
	;Get 2nd function key presses
	If KeyDown(42)=True Or KeyDown(54)=True Then
		func2=True
	Else
		func2=False
	End If
	;Get key hits
	If KeyHit(2)=True Then
		If func2=False Then
			key$="1"
		Else
			key$="!"
		End If
	Else If KeyHit(3)=True
		If func2=False Then
			key$="2"
		Else
			key$=Chr(34)
		End If
	Else If KeyHit(4)=True
		If func2=False Then
			key$="3"
		Else
			key$="£"
		End If	
	Else If KeyHit(5)=True
		If func2=False Then
			key$="4"
		Else
			key$="$"
		End If
	Else If KeyHit(6)=True
		If func2=False Then
			key$="5"
		Else
			key$="%"
		End If
	Else If KeyHit(7)=True
		If func2=False Then
			key$="6"
		Else
			key$="^"
		End If
	Else If KeyHit(8)=True
		If func2=False Then
			key$="7"
		Else
			key$="&"
		End If
	Else If KeyHit(9)=True
		If func2=False Then
			key$="8"
		Else
			key$="*"
		End If
	Else If KeyHit(10)=True
		If func2=False Then
			key$="9"
		Else
			key$="("
		End If
	Else If KeyHit(11)=True
		If func2=False Then
			key$="0"
		Else
			key$=")"
		End If
	Else If KeyHit(12)=True
		If func2=False Then
			key$="-"
		Else
			key$="_"
		End If
	Else If KeyHit(13)=True
		If func2=False Then
			key$="="
		Else
			key$="+"
		End If
	Else If KeyHit(14)=True
		key$="BACKSPACE"
	Else If KeyHit(15)=True
		key$="TAB"
	Else If KeyHit(16)=True
		If func2=False Then
			key$="q"
		Else
			key$="Q"
		End If
	Else If KeyHit(17)=True
		If func2=False Then
			key$="w"
		Else
			key$="W"
		End If
	Else If KeyHit(18)=True
		If func2=False Then
			key$="e"
		Else
			key$="E"
		End If
	Else If KeyHit(19)=True
		If func2=False Then
			key$="r"
		Else
			key$="R"
		End If
	Else If KeyHit(20)=True
		If func2=False Then
			key$="t"
		Else
			key$="T"
		End If
	Else If KeyHit(21)=True
		If func2=False Then
			key$="y"
		Else
			key$="Y"
		End If
	Else If KeyHit(22)=True
		If func2=False Then
			key$="u"
		Else
			key$="U"
		End If
	Else If KeyHit(23)=True
		If func2=False Then
			key$="i"
		Else
			key$="I"
		End If
	Else If KeyHit(24)=True
		If func2=False Then
			key$="o"
		Else
			key$="O"
		End If
	Else If KeyHit(25)=True
		If func2=False Then
			key$="p"
		Else
			key$="P"
		End If
	Else If KeyHit(26)=True
		If func2=False Then
			key$="["
		Else
			key$="{"
		End If
	Else If KeyHit(27)=True
		If func2=False Then
			key$="]"
		Else
			key$="}"
		End If
	Else If KeyHit(28)=True
		key$="ENTER"
	Else If KeyHit(30)=True
		If func2=False Then
			key$="a"
		Else
			key$="A"
		End If
	Else If KeyHit(31)=True
		If func2=False Then
			key$="s"
		Else
			key$="S"
		End If
	Else If KeyHit(32)=True
		If func2=False Then
			key$="d"
		Else
			key$="D"
		End If
	Else If KeyHit(33)=True
		If func2=False Then
			key$="f"
		Else
			key$="F"
		End If
	Else If KeyHit(34)=True
		If func2=False Then
			key$="g"
		Else
			key$="G"
		End If
	Else If KeyHit(35)=True
		If func2=False Then
			key$="h"
		Else
			key$="H"
		End If
	Else If KeyHit(36)=True
		If func2=False Then
			key$="j"
		Else
			key$="J"
		End If
	Else If KeyHit(37)=True
		If func2=False Then
			key$="k"
		Else
			key$="K"
		End If
	Else If KeyHit(38)=True
		If func2=False Then
			key$="l"
		Else
			key$="L"
		End If
	Else If KeyHit(39)=True
		If func2=False Then
			key$=";"
		Else
			key$=":"
		End If
	Else If KeyHit(40)=True
		If func2=False Then
			key$="'"
		Else
			key$="@"
		End If
	Else If KeyHit(41)=True
		If func2=False Then
			key$="`"
		Else
			key$="¬"
		End If
	Else If KeyHit(43)=True
		If func2=False Then
			key$="#"
		Else
			key$="~"
		End If
	Else If KeyHit(44)=True
		If func2=False Then
			key$="z"
		Else
			key$="Z"
		End If
	Else If KeyHit(45)=True
		If func2=False Then
			key$="x"
		Else
			key$="X"
		End If
	Else If KeyHit(46)=True
		If func2=False Then
			key$="c"
		Else
			key$="C"
		End If
	Else If KeyHit(47)=True
		If func2=False Then
			key$="v"
		Else
			key$="V"
		End If
	Else If KeyHit(48)=True
		If func2=False Then
			key$="b"
		Else
			key$="B"
		End If
	Else If KeyHit(49)=True
		If func2=False Then
			key$="n"
		Else
			key$="N"
		End If
	Else If KeyHit(50)=True
		If func2=False Then
			key$="m"
		Else
			key$="M"
		End If
	Else If KeyHit(51)=True
		If func2=False Then
			key$=","
		Else
			key$="<"
		End If
	Else If KeyHit(52)=True
		If func2=False Then
			key$="."
		Else
			key$=">"
		End If
	Else If KeyHit(53)=True
		If func2=False Then
			key$="/"
		Else
			key$="?"
		End If
	Else If KeyHit(55)=True
		key$="*"
	Else If KeyHit(57)=True
		key$=" "
	Else If KeyHit(71)=True
		key$="7"
	Else If KeyHit(72)=True
		key$="8"
	Else If KeyHit(73)=True
		key$="9"
	Else If KeyHit(74)=True
		key$="-"
	Else If KeyHit(75)=True
		key$="4"
	Else If KeyHit(76)=True
		key$="5"
	Else If KeyHit(77)=True
		key$="6"
	Else If KeyHit(78)=True
		key$="+"
	Else If KeyHit(79)=True
		key$="1"
	Else If KeyHit(80)=True
		key$="2"
	Else If KeyHit(81)=True
		key$="3"
	Else If KeyHit(82)=True
		key$="0"
	Else If KeyHit(83)=True
		key$="."
	Else If KeyHit(156)=True
		key$="ENTER"
	Else If KeyHit(181)=True
		key$="/"
	Else If KeyHit(199)=True
		If func2=False Then
			key$="HOME"
		Else
			key$="HOMESELECT"
		End If
	Else If KeyHit(200)=True
		key$="UP"
	Else If KeyHit(203)=True
		key$="LEFT"
	Else If KeyHit(205)=True
		key$="RIGHT"
	Else If KeyHit(207)=True
		If func2=False Then
			key$="END"
		Else
			key$="ENDSELECT"
		End If
	Else If KeyHit(208)=True
		key$="DOWN
	Else If KeyHit(211)=True
		key$="DELETE"
	End If
	Return key$
End Function
