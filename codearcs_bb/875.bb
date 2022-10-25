; ID: 875
; Author: dangerdave
; Date: 2004-01-04 22:00:17
; Title: Another IsOdd() function.
; Description: This is a simple, quick way to find out if a number is odd.

print IsOdd(97543)

function IsOdd(x)
  return x And 1
end function
