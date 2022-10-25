; ID: 32
; Author: DJWoodgate
; Date: 2001-09-01 11:57:44
; Title: Like Function
; Description: Search strings using wildcards

; Like Function - pattern matching routine.
;
; ?   Any single character.
; *   Zero Or more characters.
; #   Any single digit (0-9).
; [charlist]  Any single character in charlist.
; [!charlist] Any single character Not in charlist.
;
; A group of one Or more characters (charlist) enclosed in brackets ([ ])
; can be used To match any single character in String And can Include almost
; any character code, including digits.
;
; Note:   To match the special characters Left bracket ([), question
; mark (?), number sign (#), And asterisk (*), enclose them in brackets.
; The Right bracket (]) can't be used within a group To match itself, but
; it can be used outside a group as an individual character.
;
; By using a hyphen (-) To separate the Upper And Lower bounds of the range,
; charlist can specify a range of characters. For example, [A-Z] results in
; a match If the corresponding character position in String contains any
; uppercase letters in the range A-Z. Multiple ranges are included within
; the brackets without delimiters.
Function Like(Parse$,Pattern$)
INVERT$="!"
While Pattern <> ""
	Select Mid$(Pattern, 1, 1)
		Case "?"
			If Parse = "" Then Return 0
		Case "#"
			If IsDigit(Mid$(Parse, 1, 1)) = 0 Then Return 0
		Case "*"
			Repeat
				Pattern = Mid$(Pattern, 2, Len(Pattern) - 1)
			Until Mid$(Pattern, 1, 1) <> "*"
			If Pattern = "" Then Return 1
			While Parse <> ""
				If Like(Parse, Pattern) Then Return 1
				If Parse <> "" Then Parse = Mid$(Parse,2,Len(Parse)-1)
			Wend
			Return 0
		Case "["
			reverse = (Mid$(Pattern,2,1) = INVERT)
			If reverse Then Pattern = Mid$(Pattern,2,Len(Pattern)-1)
			prev = -1 : esc=1: matched = 0
			Repeat
				Pattern = Mid$(Pattern,2,Len(Pattern)-1)
				If (Pattern <> "") And (Mid$(Pattern,1,1) <> "]") Then
					If Mid$(Pattern, 1, 1) = "-" Then
						Pattern = Mid$(Pattern,2,Len(Pattern)-1)
						If Pattern = "" Then Return 0
						matched = matched Or (Mid$(Parse,1,1) <= Mid$(Pattern,1,1) And Asc(Mid$(Parse,1,1)) >= prev)
					Else
						matched = matched Or (Mid$(Parse,1,1) = Mid$(Pattern,1,1))
					End If
				prev = Asc(Mid$(Pattern,1,1))
				Else
            		Exit 
				End If
			Forever
			If (prev = -1 Or Mid$(Pattern,1,1) <> "]" Or Abs(matched) = Abs(reverse)) Then Return 0
		Default
			If Mid$(Parse,1,1) <> Mid$(Pattern,1,1) Then Return 0
	End Select
	Parse = Mid$(Parse,2,Len(Parse)-1)
    Pattern = Mid$(Pattern,2,Len(Pattern)-1)
Wend
Return Abs(Len(Parse) = 0)
End Function

Function IsDigit(S$)
  If S >= "0" And S <= "9" Then Return 1 Else Return 0
End Function




