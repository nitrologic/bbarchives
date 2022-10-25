; ID: 1829
; Author: Arem
; Date: 2006-10-01 16:36:15
; Title: Pi Calculator
; Description: Calculates Pi

n=100

total#=2*Sqr(1.5)

For temp=1 To 99
	x#=-1*Sqr(2)+Sqr(2)*2/n*temp
	
	total=total+2*Sqr(1+(-1/(x^2-4)))
Next

other#=Sqr(2)*2/(2*n)

pivalue#=total*other

Print "Pi="+pivalue
Print ""

asdf=Input$("Hit enter to end.")

End
