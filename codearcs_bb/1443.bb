; ID: 1443
; Author: kfprimm
; Date: 2005-08-13 19:55:36
; Title: Group Boxes
; Description: Creates XP style group boxes

Function CreateGroupBox(txt$,x,y,width,height,group)
outline=CreateImage(width,height)
SetBuffer ImageBuffer(outline)
font=LoadFont("Arial",14)
SetFont font
ClsColor 236,233,216
Cls
Color 153,84,10

Text 10,4,txt$,False,True
Color 209,209,192
Line 2,4,6,4
Line 10+StringWidth(txt$)+2,4,width-3,4
Plot 2,5
Plot 1,5
Plot 1,6
Plot width-3,5
Plot width-2,5
Plot width-2,6
Line width-1,6,width-1,height-3
Plot width-2,height-3
Plot width-2,height-2
Plot width-3,height-2
Line width-3,height-1,2,height-1
Plot 2,height-2
Plot 1,height-2
Plot 1,height-3
Line 0,height-3,0,6
SaveImage outline,"outline.bmp"
FreeImage outline
Local can=CreateCanvas(1025,0,1,1,group)
SetBuffer CanvasBuffer(can)
Local outlinepan=CreatePanel(x,y,width,height,group)
SetPanelImage outlinepan,"outline.bmp"
DeleteFile "outline.bmp"
Local pan=CreatePanel(2,9,width-4,height-11,outlinepan)
FreeFont font
Return pan
End Function
