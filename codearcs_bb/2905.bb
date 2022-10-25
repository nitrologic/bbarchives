; ID: 2905
; Author: Captain Wicker
; Date: 2011-12-07 15:34:43
; Title: BASIC String$ Example
; Description: An example of Strings being used to simply by using the Print Function

AppTitle("String$ DEMO")

Local X$ = ("Hello")
Local Y$ = (" ")
Local Z$ = ("World")

Print ((X$) + (Y$) + (Z$))

While Not KeyDown( 1 )
Wend
End
