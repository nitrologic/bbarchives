; ID: 1574
; Author: 5t@nKy
; Date: 2005-12-23 14:43:10
; Title: mathematical algorithms
; Description: some mathematical algorithms

Here are some of my collections:

this is the 'heron-algorithm' with which you can get the square root from 'n'

Function Sqr#(n#)
   x0#=1
   repeat
      check#=0.5*(x0#+n#/x0#)
      x0#=x1#
   until check#=x0#
   return x0#
end function

this function is to get the greatest common divisor:

Function gcd(A,B)
   While B > 0
      R=A mod B
      A=B
      B=R
   Wend
   Return A
End Function

this function is to get the least common denominator and based on the gcd function

Function lcd(A,B)
   C=(A*B)/gcd(A,B)
   Return C
End Function

to get the factorial of a number you can use this

Function fac(n)
   check=1
   For i=1 To n
      check=check*1
   Next
   Return check
Emd Function

this function is to get the binomial coefficient, this is to get the number of Pascal's triangle (n choose k)

Function nCk(n,k)
   Return Fac(n)/(Fac(k)*Fac(n-k))
End Function

so that's it!

I hope it's useful and sorry for my bad english^^
