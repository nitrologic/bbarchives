; ID: 317
; Author: Rob Farley
; Date: 2002-05-06 18:02:26
; Title: Analogue Clock Function
; Description: Shows the time as an Analogue Clock

; analogue clock - http://www.mentalillusion.co.uk
Function clock(x,y,size#,r,g,b)
Color r,g,b
hour#=Left$(CurrentTime$(),2)
minute#=Mid$(CurrentTime$(),4,2)
second#=Right$(CurrentTime$(),2)
If hour#>12 Then hour#=hour#-12
Line x,y,x+(Sin(second#*6)*size#),y-(Cos(second#*6)*size#)
Line x,y,x+(Sin(minute#*6)*size#),y-(Cos(minute#*6)*size#)
Line x,y,x+(Sin((hour#*30)+(minute#*.2))*(size#*.7)),y-(Cos((hour#*30)+(minute#*.2))*(size#*.7))
For n=0 To 11
Line x+(Sin(n*30)*(size#*.9)),y+(Cos(n*30)*(size#*.9)),x+(Sin(n*30)*size#),y+(Cos(n*30)*size#)
Next
For n=0 To 59
Plot x+(Sin(n*6)*size#),y+(Cos(n*6)*size#)
Next
End Function
