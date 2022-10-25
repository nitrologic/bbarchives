; ID: 2112
; Author: Petron
; Date: 2007-09-26 18:44:30
; Title: Convert Images to Code
; Description: This program can be used to convert any image to code, including animated images.

location$ = Input$("Enter file location: ")
name$ = Input$("Enter file name: ")
maskred$ = Input$("Redmask: ")
maskgreen$ = Input$("Greenmask: ")
maskblue$ = Input$("Bluemask: ")
frames = Input$("Frames: ")
GlobalYesOrNo = Input$("Global 1 for yes, 0 for no: ")
frame = 0
xspot = 0 
image = LoadImage (location$)
width = ImageWidth (image)
height = ImageHeight (image)
fwidth = width/frames
x = 0
y = 0
draw = CreateImage(width,height)
SetBuffer ImageBuffer(draw)
DrawImage image,0,0
save=WriteFile(name$+".bb") 
If GlobalYesOrNo = "1" Then WriteLine save,"Global "+name$+" = "+"CreateImage("+fwidth+","+height+","+frames+")"
If GlobalYesOrNo = "0" Then WriteLine save,name$+" = "+"CreateImage("+fwidth+","+height+","+frames+")"
WriteLine save,"SetBuffer ImageBuffer("+name$+","+frame+")"
Repeat 
GetColor x,y
WriteLine save,"Color "+ColorRed()+","+ColorGreen()+","+ColorBlue()
WriteLine save,"Plot "+xspot+","+y
y = y + 1
If y > height   
y = 0  
xspot = xspot + 1
x = x + 1
If xspot > fwidth - 1
frame = frame + 1
If frame = frames 
x = width
y = height
EndIf  
xspot = 0 
If Not x = width And y = height
WriteLine save,"SetBuffer ImageBuffer("+name$+","+frame+")"
EndIf 
EndIf 
EndIf 
Until x => width And y => height
.exitloop
WriteLine save,"MaskImage "+name$+","+maskred$+","+maskgreen$+","+maskblue$ 
CloseFile save 
End
