; ID: 848
; Author: ChrML
; Date: 2003-12-01 11:42:08
; Title: Draw gradiented surface!
; Description: A function which lets you draw a gradiented 2D surface between any colors!

Function DrawGradientSurface(sred#,sgreen#,sblue#,dred#,dgreen#,dblue#,x%,y%,width#,height#)
  eachxred#=(dred#-sred#)/width#
  eachxgreen#=(dgreen#-sgreen#)/width#
  eachxblue#=(dblue#-sblue#)/width#

  tempwidth%=width#

  For tempx=x% To x%+tempwidth%
    colr = eachxred#*count+sred
    colg = eachxgreen#*count+sgreen
    colb = eachxblue#*count+sblue

    Color colr,colg,colb
    Line tempx,y%,tempx,y%+height#

    count=count+1
  Next
End Function
