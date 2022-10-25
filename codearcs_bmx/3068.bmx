; ID: 3068
; Author: Pineapple
; Date: 2013-08-27 01:39:12
; Title: Word wrap
; Description: Versatile as hell and generally nifty

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.linkedlist
Import brl.max2d

' How different characters are treated can be modified with these.

Global _wrap_space%[]=[32] ' Spaces: good for ending a line on
Global _wrap_hyphen%[]=[45] ' Hyphen: only good for ending a line on if surrounded by non-special characters (i.e. letters, like a normal hyphen rather than a dash.)
Global _wrap_newline%[]=[10,255] ' Newline: newlines
Global _wrap_roughcut%=_wrap_hyphen[0] ' Which character to use to show that a word has been interrupted.

' Returns a list of strings that are single, sequential, word-wrapped lines of the passed string.
' str		- 	the string to be word-wrapped
' maxwidth	-	the maximum number of pixels that a line is allowed to be wide
' charwidth	-	a function that takes an integer ascii value and returns a character width.
'			the default thing should be sufficient if you're using regular old blitzmax
'			drawtext, but if you're using bitmap fonts or anything else funny, you might
'			want write a different one.
' maxcutoff	-	this is the maximum number of characters to scan backward to find a space
'			or hyphen to cleanly end a line on before settling with a mid-word hyphenation.
'			for convenience, set to -1 to default to the global value that can be
'			modified whenever.

Function _wrap_charwidth%(char%) ' See above (charwidth argument)
	Return TextWidth(Chr(char))
End Function

Global _wrap_maxcutoff%=9 ' Also see above (maxcutoff argument)

Function WrapString:TList(str$ Var,maxwidth%,charwidth%(char%)=_wrap_charwidth,maxcutoff%=-1)
	If maxcutoff=-1 Then maxcutoff=_wrap_maxcutoff
	Local pos%=0,linew%=0
	Local basepos%=0
	Local list:TList=CreateList()
	Repeat
		If pos<str.length Then linew:+charwidth(str[pos])
		If _numinarray(str[pos],_wrap_newline)
			Local line$=""
			For Local i%=basepos Until pos
				line:+Chr(str[i])
			Next
			list.addlast line
			basepos=pos+1
			pos=basepos
			linew=0
		ElseIf linew>maxwidth Then
			Local cutoff%=pos-1
			Local goodcut%=False,skipaspace%=False
			While cutoff>basepos And pos-cutoff<maxcutoff
				If _numinarray(str[cutoff],_wrap_space) Or _wrappablehyphen(str,cutoff) Then
					goodcut=True
					Exit
				EndIf
				cutoff:-1				
			Wend
			If Not goodcut Then
				Local budget%=charwidth(_wrap_roughcut)
				Local subbed%=0,subcount%=0
				cutoff=pos-1
				While subbed<budget
					subbed:+charwidth(str[cutoff])
					subcount:+1
					cutoff:-1
				Wend
				If subcount<=1 And cutoff<str.length-1-subcount And _numinarray(str[cutoff+1+subcount],_wrap_space) Then 
					goodcut=True
					skipaspace=True
					cutoff:+subcount
				EndIf
			EndIf
			Local line$=""
			For Local i%=basepos To cutoff
				line:+Chr(str[i])
			Next
			If Not goodcut Then 
				line:+Chr(_wrap_roughcut)
			EndIf
			list.addlast line
			basepos=cutoff+1+skipaspace
			pos=basepos
			linew=0
		Else
			pos:+1
			If pos>=str.length Then
				Local line$=""
				For Local i%=basepos Until str.length
					line:+Chr(str[i])
				Next
				list.addlast line
				Exit
			EndIf
		EndIf
	Forever
	Return list
End Function
' used by WrapString. checks if an int is in an int array.
Function _numinarray%(i%,a%[])
	For Local c%=EachIn a
		If i=c Return True
	Next
	Return False
End Function
' used by WrapString. checks if a hyphen is surrounded by not-special characters, presumably letters.
Function _wrappablehyphen%(str$ Var,pos%)
	If pos<=0 Or pos>=str.length-1 Then Return False
	If Not _numinarray(str[pos],_wrap_hyphen) Then Return False
	Local disqbefore%=_numinarray(str[pos-1],_wrap_hyphen) Or _numinarray(str[pos-1],_wrap_space) Or _numinarray(str[pos-1],_wrap_newline)
	Local disqafter%=_numinarray(str[pos+1],_wrap_hyphen) Or _numinarray(str[pos+1],_wrap_space) Or _numinarray(str[pos+1],_wrap_newline)
	Return Not(disqbefore Or disqafter)
End Function



' Example code

Rem

Graphics 320,320
Local str$="Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " ..
	+ "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " ..
	+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " ..
	+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
	
Local list:TList=wrapstring(str,300)

Repeat
	Cls
	
	Local y%=10
	For Local s$=EachIn list
		DrawText s,10,y
		y:+20
	Next
	
	Flip
	Delay 20
Until KeyDown(27) Or AppTerminate()

EndRem
