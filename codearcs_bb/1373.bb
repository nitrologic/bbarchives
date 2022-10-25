; ID: 1373
; Author: Blaine
; Date: 2005-05-13 16:22:54
; Title: Hyperlink label
; Description: Add Hyperlink labels to your BlitzPlus programs!

Type hyperlink
Field canvas
Field href$
End Type


Function CreateHyperlink(txt$,x,y,w,h,link$,parent,style=0)
canvas=CreateCanvas(x,y,w,h,parent)
SetBuffer CanvasBuffer(canvas)
font=LoadFont("tahoma",13,0,0,1)
ClsColor getsyscolorr(15),getsyscolorg(15),getsyscolorb(15)
Cls
Color 0,0,255
SetFont font
If StringWidth(txt)>w
txt2$=txt
Repeat
txt2=Left(txt2,Len(txt2)-1)
Until StringWidth(txt2)<w
Text 0,0,txt2
Text 0,13,Right$(txt,Len(txt2)-3)
Else
Text 0,0,txt
EndIf
FlipCanvas canvas
l.hyperlink=New hyperlink
l\canvas=canvas
l\href=link
Return l\canvas
End Function


Function UpdateHyperlinks()
For l.hyperlink=Each hyperlink
If EventSource()=l\canvas
If EventID()=$203
SetCursor helpcursor
Else If EventID()=$201
ExecFile(l\href)
EndIf
EndIf
FlipCanvas l\canvas
Next
End Function
