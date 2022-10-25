; ID: 286
; Author: Rob Farley
; Date: 2002-04-07 04:12:26
; Title: Working Bar
; Description: Custom working bar function

; Working Bar - By Rob Farley 2002
; web: http://www.mentalillusion.co.uk
; email: rob@mentalillusion.co.uk
; working_bar([x position],[y position],[width],[height],[current position],
; [maximum position],[red],[green],[blue],[background red],[background green],[background blue])

Function working_bar(x,y,width#,height,pos#,top#,r,g,b,bgr,bgg,bgb)
Color bgr,bgg,bgb
Rect x,y,width,height
real_width#=((width#-4)/top#)*pos#
Color r,g,b
Rect x+2,y+2,real_width#,height-4
End Function
