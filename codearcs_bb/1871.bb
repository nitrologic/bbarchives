; ID: 1871
; Author: Nathaniel
; Date: 2006-12-03 13:31:58
; Title: Prime Finder
; Description: A program that calculates if a given number is prime.

;Prime Finder
;Created by: Bubble Boy

Graphics 800,600,16,2
SetBuffer BackBuffer()

AppTitle "Prime Finder"

number#=1
divisor#=1
quotient#=0
rounded=0
prime=0
go=0


Color 250,250,86
Print "PRIME FINDER"

number#=Input$("What number do you want me to start with? ")


While Not KeyHit(1)

Color 250,250,86
Print number#
Flip

While go = 0

   quotient# = number# / divisor#
   rounded=Int( quotient# )

   Color 50,255,50
   Print quotient# + " = " + number# + " / " + divisor#


   If divisor# > 1
   If quotient# = rounded
      prime = 0
      go = 1
   Else 
      prime = 1

   End If   
   End If


Delay 20

If KeyDown(1)
   End
End If

If divisor# = number#
   prime = 1
   go = 1
End If

If number# = 1
   prime = 0
   go = 1
End If

divisor# = divisor# + 1




Wend

If prime = 1
   Color 250,250,86
   Print number#
   Color 255,50,50
   Print "PRIME"

   Flip()


Else If prime = 0
   Color 250,250,86
   Print number#
   Color 50,50,255
   Print "NOT PRIME"
   Flip()
End If
Print " "

Delay 2000

go=0
prime=0
number#=number# + 1
divisor#=1
quotient#=0

Wend

End
