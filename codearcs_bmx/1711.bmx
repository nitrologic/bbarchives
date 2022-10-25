; ID: 1711
; Author: RepeatUntil
; Date: 2006-05-13 02:08:28
; Title: RC4 encryption
; Description: Very easy, fast and secure encryption and decryption using RC4 algorithm

Local pass$ = "mySecretKey"
Local msg$ = "Blitz Research Ltd is a software development company dedicated to bringing you the ultimate in game creation tools and utilities.Our flagship product is Blitz3D, the hit 3D games programming language used by thousands of programmers around the world.Our latest product is BlitzMax, a cross platform programming language based on BASIC, but with many weird And wonderful additions."

Print ""
Print "Message before encryption: " + msg

Print ""

Local crypt$ = RC4(msg$, pass$)

Print "Message after encryption: " + crypt

Print ""
Local decrypt$ = RC4(crypt$,pass$)
Print "Message after decryption: " + decrypt




' This function encrypts and decrypts a string with the use of a key
Function RC4$(inp$, key$)
	Local S[256] ' 255 byte Arrays
	Local K[256]

	Local i,j,t,temp,y
	Local Output$

	For i = 0 To 255
		S[i] = i
	Next

	j = 1
	For i = 0 To 255
		If j > key.length
 			j = 1
		EndIf
		K[i] = Asc(Mid(key,j,1))
		j:+ 1
	Next

	j = 0
	For i = 0 To 255 '
		j = ( j + S[i] + K[i] ) & 255
		temp = S[i]
		S[i] = S[j]
		S[j] = temp
	Next

	i = 0
	j = 0
	For Local x = 1 To Len(inp)
		i = (i + 1) & 255
		j = (j + S[i]) & 255
		temp = S[i]
		S[i] = S[j]
		S[j] = temp
		t = (  S[i] + ( S[j] & 255 )  ) & 255
		y = S[t]
		Output:+ Chr(Asc(Mid(inp,x,1)) ~ Y)
	Next
	
	Return Output$
EndFunction
