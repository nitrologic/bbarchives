; ID: 2278
; Author: _33
; Date: 2008-06-23 21:42:18
; Title: Word Maker
; Description: Creates unknown words

Dim a$(8)
Restore letters
For i = 1 To 8
   Read a$(i)
Next

.letters
;1
Data "aeiouy"
Data "bcdfghjklmnpqrstvwxz"
;2
Data "abacadaeafagahaiajakalamanaoapaqarasatauavawaxayaz"
Data "eaebecedefegeheiejekelemeneoepeqereseteuevewexeyez"
Data "iaibicidieifigihijikiliminioipiqirisitiuiviwixiyiz"
Data "oaobocodoeofogohoiojokolomonopoqorosotouovowoxoyoz"
Data "uaubucudueufuguhuiujukulumunuoupuqurusutuvuwuxuyuz"
Data "yaybycydyeyfygyhyiyjykylymynyoypyqyrysytyuyvywyxyz"

Function word_maker$(ln%, c% = False, s% = False)
   word$ = ""
   If s% <> False Then
	q% = s%
   Else
      q% = Rand(1,2)
   EndIf
   For l% = 1 To ln%

      If q = 1 Then ; vowel
         x% = Rand(1,6)
         If Rand(1,2) = 1 Or l = ln Then ; 1 letter
            word$ = word$ + Mid$(a$(1),x,1)
            q% = 2
         Else ; two letters
            y% = Rand(1,25)
            word$ = word$ + Mid$(a$(x+2),y*2,2)
            l = l + 1
            q% = Rand(1,2)
         EndIf
      Else
         z% = Rand(1,20)
         word$ = word$ + Mid$(a$(2),z,1)
         q% = 1
      EndIf

   Next
   If c = True Then
      word$ = Upper$(Left$(word,1)) + Mid$(word$, 2, ln - 1)	
   EndIf
	
   Return word$
End Function



SeedRnd (MilliSecs())
For i = 1 To 20
	Print word_maker$(Rand(2,8))
Next
WaitKey()
End
