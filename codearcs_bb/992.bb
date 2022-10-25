; ID: 992
; Author: Rob Farley
; Date: 2004-04-08 09:10:41
; Title: Key Generator
; Description: Create keys for your software

; KeyGen
; 2004 MentalIllusion.co.uk
; web: http://www.mentalillusion.co.uk
; email: rob@mentalillusion.co.uk

; This is a key generator for any blitzy thing you want
; include it in your main programs to unlock bits of code.

; The usage is pretty straight forward, key$ = keygen("Name or whatever")
; So you'll need to keep a version for yourself for sending keys to people!



name$ = "rob@mentalillusion.co.uk"

key$ = keygen(name)
Print name
Print key
WaitKey


Function keygen$(name$)

	; change v$ to as many or few random or unrandom letters, numbers, characters whatever
	; this is what the key is going to be made out of
	; You can have duplicates all over the place if you want, it's up to you!
	; This is one part that will make your keys unique to other people using this program
	; e.g. v$="I1D9U0AJ5PFWIN1TR3EKLWZID42HU7KL8S6LTBN9VMCXOF6T46GY3JHIE9T7VTLFDEQ3Y38P"

	v$ = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

	tname$ = name$

	; make name longer if necessary
	; again adjust this to make your keys unique

	namel = 20
	Repeat
		If Len(tname) <= namel
			temp$ = ""
			For n = 1 To Len(tname)
				temp = temp + Chr(Asc(Mid(tname, n, 1)) + 1)
			Next
			tname = tname + temp
		EndIf
	Until Len(tname) > namel

	; this bit makes sure that you don't get any obvious repetitions over the 20 character key
	For n = 5 To 100 Step 5
		If Len(tname) = n Then tname = tname + "~"
	Next

	; create encrypt string
	encrypt$ = ""
	For n = 0 To 19
		encrypt = encrypt + Chr(1)
	Next
	ee = 1

	; over load encrypt 30 times
	; change this to make your keys unique further

	For l = 1 To 30

		For n = 1 To Len(tname)
			a = Asc(Mid(tname, n, 1))
			a = a - 32

			temp$ = ""

			For nn = 1 To 20
				tl = Asc(Mid(encrypt, nn, 1))
				If nn = ee Then temp = temp + Chr(tl + a Mod 256)Else temp = temp + Chr(tl)
			Next
			encrypt = temp
			ee = ee + 1
			If ee = 20 Then ee = 1
		Next

	Next

	; suck out the key
	encrypted$ = ""
	For ee = 1 To 20
		e = Asc(Mid(encrypt, ee, 1))Mod Len(v)
		If e = 0 Then e = 1
		encrypted = encrypted + Mid(v, e, 1)
	Next

	; format the key with -'s
	encrypted = Mid(encrypted, 1, 5) + "-" + Mid(encrypted, 6, 5) + "-" + Mid(encrypted, 11, 5) + "-" + Mid(encrypted, 16, 5)

	; return the key
	Return encrypted

End Function
