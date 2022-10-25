; ID: 2686
; Author: Streaksy
; Date: 2010-04-01 09:52:40
; Title: Colours By Name
; Description: Change colour by name instead of remembering codes.  It knows 175 colours and they are natural and correct.

Const Colour_Demo=1         ;run this bb with this const set 
as 1 to see the colour chart


Const ColourLibPresent=1

Global BGcolR=-1,BGcolG=-1,BGcolB=-1

Global Colours,MaxColours=200
Dim ColourName$(MaxColours)
Dim ColourLen(MaxColours)
Dim ColourR(MaxColours)
Dim ColourG(MaxColours)
Dim ColourB(MaxColours)
Dim ColourRGB(MaxColours)

Global sat#,lumin#,hue# ;returned by RGB2HSB()

AddColour "Blue"                  ,65,95,255
AddColour "Red"                   ,215,25,25
AddColour "Yellow"                ,255,215,55
AddColour "Green"                 ,75,215,35
AddColour "Orange"                ,245,131,25
AddColour "Brown"                 ,85,45,25
AddColour "Pink"                  ,255,125,165
AddColour "Purple"                ,122,0,100
AddColour "Cyan"                  ,85,195,255
AddColour "Aqua"                  ,85,215,135
AddColour "Indigo"                ,100,38,204
AddColour "Violet"                ,145,65,255
AddColour "Magenta"               ,238,2,128
AddColour "Fuchsia"               ,210,0,90
AddColour "Lilac"                 ,190,150,250
AddColour "Lavender"              ,160,150,250
AddColour "Teal"                  ,0,167,160
AddColour "Turquoise"             ,0,210,170
AddColour "Mauve"                 ,186,126,150
AddColour "Beige"                 ,209,196,174
AddColour "Jade"                  ,53,133,85
AddColour "Cream"                 ,240,225,170
AddColour "Taupe"                 ,174,159,138
AddColour "Buff"                  ,76,86,80
AddColour "Peach"                 ,255,180,80
AddColour "Nutmeg"                ,128,100,70
AddColour "Plum"                  ,70,14,54
AddColour "Salmon"                ,229,107,83
AddColour "Burgundy"              ,65,5,34
AddColour "Tan"                   ,190,130,80
AddColour "Olive"                 ,97,108,53
AddColour "Chestnut"              ,92,32,16
AddColour "Navy Blue"             ,0,38,73
AddColour "Sky Blue"              ,105,135,245
AddColour "Lemon"                 ,255,255,55
AddColour "Lime"                  ,5,205,5
AddColour "Hot Pink"              ,245,0,200
AddColour "Mint"                  ,172,255,172
AddColour "Walnut"                ,163,129,79
AddColour "Coral"                 ,252,125,95
AddColour "Cherry"                ,175,0,30
AddColour "Maroon"                ,88,28,33
AddColour "Azure"                 ,13,119,183
AddColour "Cedar"                 ,163,43,64
AddColour "Safron"                ,255,180,0
AddColour "Crimson"               ,95,0,10
AddColour "Scarlet"               ,135,15,15
AddColour "Royal Purple"          ,70,0,90
AddColour "Slate"                 ,56,59,63
AddColour "Khaki"                 ,154,130,94
AddColour "Oatmeal"               ,215,200,150
AddColour "Raspberry"             ,155,15,65
AddColour "Mahogany"              ,54,22,0
AddColour "Heather"               ,125,85,105
AddColour "Avocado"               ,198,192,116
AddColour "Pea Green"             ,98,143,57
AddColour "Wine"                  ,122,11,56
AddColour "Watermellon"           ,236,91,89
AddColour "Sand"                  ,177,139,88
AddColour "Candy Pink"            ,255,85,195
AddColour "Chocolate"             ,73,50,34
AddColour "Grape"                 ,140,170,100
AddColour "Flamingo"              ,255,62,138
AddColour "Terracotta"            ,225,115,70
AddColour "Auburn"                ,75,23,9
AddColour "Rose"                  ,229,144,150
AddColour "Mustard"               ,200,180,20
AddColour "Caramel"               ,170,110,0
AddColour "Honey"                 ,235,170,46
AddColour "Parchment"             ,225,196,149
AddColour "Kiwi"                  ,112,156,45
AddColour "Cucumber"              ,190,210,99
AddColour "Vanilla"               ,249,241,143
AddColour "Almond"                ,187,167,140
AddColour "Clay"                  ,143,131,114
AddColour "Banana"                ,249,242,80
AddColour "Strawberry"            ,255,95,55
AddColour "Spice"                 ,210,80,0
AddColour "Sorbet"                ,185,85,85
AddColour "Cargo"                 ,100,92,73
AddColour "Milkshake"             ,255,155,175
AddColour "Velvet"                ,171,39,78
AddColour "Amaranth"              ,130,45,90
AddColour "Tomato"                ,203,58,19
AddColour "Amethyst"              ,145,85,165
AddColour "Curry"                 ,165,140,20
AddColour "Butter"                ,254,251,174
AddColour "Toffee"                ,165,85,35
AddColour "Burlap"                ,150,110,60
AddColour "Willow"                ,45,190,100
AddColour "Highlight"             ,225,250,40
AddColour "Menthol"               ,145,255,195
AddColour "Lemon Curd"            ,234,222,116
AddColour "Citrus"                ,255,150,0
AddColour "Buttermilk"            ,255,255,214
AddColour "Moss"                  ,75,90,30
AddColour "Basalt"                ,66,66,55
AddColour "Girly Pink"            ,246,146,217
AddColour "Blush"                 ,255,55,55
AddColour "Morocco"               ,180,40,0
AddColour "Glamour Girl"          ,200,0,140
AddColour "Herbal"                ,100,140,100
AddColour "Soft Blue"             ,55,85,175
AddColour "Tiger Lily"            ,231,60,9
AddColour "Brick"                 ,121,47,36
AddColour "White Chocolate"       ,255,255,193
AddColour "Sour Yellow"           ,225,250,80
AddColour "Soft Orange"           ,255,135,65
AddColour "Denim"                 ,135,165,232
AddColour "Deep Pink"             ,200,80,144
AddColour "Vivid Purple"          ,165,5,255
AddColour "Silver Grey"           ,155,155,177
AddColour "Warm Pink"             ,226,90,110
AddColour "Mud"                   ,125,70,25
AddColour "Desert Camo"           ,190,150,80
AddColour "Cheese"                ,236,185,73
AddColour "Grass"                 ,112,149,38
AddColour "Soft Pink"             ,215,105,125
AddColour "Fern"                  ,40,130,0
AddColour "Gloom"                 ,50,70,60
AddColour "Night Blue"            ,65,65,120
AddColour "Sea Storm"             ,60,100,120
AddColour "Slime"                 ,125,195,35
AddColour "Glow Stick"            ,205,250,100
AddColour "Airy Blue"             ,100,130,150
AddColour "Natural Green"         ,104,125,55
AddColour "Sandstone"             ,115,115,70
AddColour "Bright Blue"           ,185,195,255
AddColour "Bright Green"          ,175,255,135
AddColour "Toxic Yellow"          ,155,155,0
AddColour "Toad"                  ,59,62,40
AddColour "Swamp"                 ,43,48,38
AddColour "Neutral Blue"          ,5,65,85
AddColour "Caucasia"              ,215,170,130
AddColour "Homey"                 ,124,77,59
AddColour "Twilight"              ,28,28,44
AddColour "Sycamore"              ,105,90,40
AddColour "Volcanic"              ,44,28,28
AddColour "Fire"                  ,255,90,14
AddColour "Ice"                   ,125,220,255
AddColour "Troll"                 ,28,44,28
AddColour "Poison"                ,125,250,40
AddColour "Dark Aqua"             ,29,104,100
AddColour "Fog"                   ,139,164,140
AddColour "Metalic"               ,153,172,160
AddColour "Mineral"               ,192,192,160
AddColour "White"                 ,255,255,255
AddColour "Light Grey"            ,165,165,165
AddColour "Grey"                  ,122,122,122
AddColour "Dark Grey"             ,65,65,65
AddColour "Charcoal"              ,24,24,24
AddColour "Opaque Black"          ,2,2,2

AddColour "Light Blue"            ,115,145,255
AddColour "Light Red"             ,255,35,35
AddColour "Light Yellow"          ,255,225,95
AddColour "Light Green"           ,115,255,85
AddColour "Light Orange"          ,255,171,75
AddColour "Light Brown"           ,125,85,55
AddColour "Light Pink"            ,255,175,205
AddColour "Light Purple"          ,152,70,170

AddColour "Dark Blue"             ,35,30,100
AddColour "Dark Red"              ,115,15,15
AddColour "Dark Yellow"           ,155,115,22
AddColour "Dark Green"            ,39,107,19
AddColour "Dark Orange"           ,145,71,17
AddColour "Dark Brown"            ,42,23,17
AddColour "Dark Pink"             ,165,75,99
AddColour "Dark Purple"           ,62,0,50

AddColour "Black"                 ,0,0,0
AddColour "System Red"            ,255,0,0
AddColour "System Green"          ,0,255,0
AddColour "System Yellow"         ,255,255,0
AddColour "System Blue"           ,0,0,255
AddColour "System Magenta"        ,255,0,255
AddColour "System Cyan"           ,0,255,255


If Colour_Demo Then
ofx=18
ofy=15
sp=10
w=133;230
h=20;33
;Graphics 1280,1024,32,1
Graphics 1024,768,32,2
SetBuffer BackBuffer()
SetFont LoadFont("tahoma",18) ; lucida console 21
ClsColour "khaki"
Repeat
Cls
mmsx=MouseX()
mmsy=MouseY()
mmd2=MouseDown(2)
x=ofx
y=ofy
For c=1 To colours
colour c
If mmd2=0 Then Rect x,y,w,h,1:Colour c,150:Text x+(w/2)-1,y+1+(h/2),colourname(c),1,1:Colour c,160; Color 0,0,0
Rect x,y,w,h,0
Rect x+1,y+1,w-2,h-2,0
If mmd2=0 Then Colour c,390
Text x+(w/2),y+(h/2)-1,colourname(c),1,1

If mmsx=>x And mmsy=>y And mmsx=<x+w And mmsy=<y+h Then Rect x,y,w,h,0:If MouseDown(1) Then ClsColor colourr(c),colourg(c),colourb(c)
y=y+h+sp:If y+h>GraphicsHeight() Then y=ofy:x=x+w+sp
Next
Flip
Until KeyHit(1)
End
EndIf



Global maxremcols=100,remcolat=0
Dim remcolr(maxremcols)
Dim remcolg(maxremcols)
Dim remcolb(maxremcols)
Global cremred,cremgreen,cremblue


Dim alut(600,3)		;MIXCOLOURS()
Local r					
For r=1 To 600
c=r
If c>255 c=255
alut(r,1) = (c And 255) Shl 16 
alut(r,2) = (c And 255) Shl 8 
alut(r,3) = (c And 255) 
Next




Function RememberColour()
cremred=ColorRed()
cremgreen=ColorGreen()
cremblue=ColorBlue()
If remcolat=maxremcols Then Return
remcolat=remcolat+1
remcolr(remcolat)=cremred
remcolg(remcolat)=cremgreen
remcolb(remcolat)=cremblue
End Function

Function RecallColour()
If remcolat=0 Then Color cremred,cremgreen,cremblue:Return
Color remcolr(remcolat),remcolg(remcolat),remcolb(remcolat)
remcolat=remcolat-1
End Function



Function Alpha(hue)
Return (hue And $FF000000) Shr 24
End Function

Function Red(hue)
Return (hue And $00FF0000) Shr 16
End Function

Function Green(hue)
Return (hue And $0000FF00) Shr 8
End Function

Function Blue(hue)
Return (hue And $000000FF)
End Function

Function ARGB(A,R,G,B)
Return RGBA(r,g,b,a)
End Function

Function RGBA(R,G,B,A=255)
If a<0 Then Return (R*256*256)+(g*256)+b
Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B; Shl 0
End Function

Function MixColours(col1,col2,a#)
Local na#,rr1,gg1,bb1,rr2,gg2,bb2
na#=a
a=1-a
rr1=((col1 Shr 16) And 255) *a
gg1=((col1 Shr 8) And 255) *a
bb1=(col1 And 255) *a
rr2=((col2 Shr 16) And 255) *na
gg2=((col2 Shr 8) And 255) *na 
bb2=(col2 And 255) *na
Return $ff000000 Or aLut(rr1+rr2,1) Or aLut(gg1+gg2,2) Or aLut(bb1+bb2,3)
End Function



Function AddColour(N$,R,G,B)
If colours=maxcolours Then RuntimeError "Too many colours."
Colours=Colours+1
ColourName$(Colours)=N$
Colourlen(Colours)=Len(N$)
colourr(colours)=r
colourg(colours)=g
colourb(colours)=b
colourRGB(colours)=rgba(r,g,b,255)
End Function

Function FindColour(n$)
n=Lower(n):l=Len(n)
For t=1 To colours
If colourlen(t)+l Then If n=Lower(colourname(t)) Then Return t
Next
End Function

Function Colour(n$,bri=255) ;bri goes from 0 to 512 ... 255 is normal!
t=Int(n)
If t=0 Then t=findcolour(n)
If bri=255 Then Color colourr(t),colourg(t),colourb(t):Return t
If bri<255 Then Color (colourr(t)*bri)/255,(colourg(t)*bri)/255,(colourb(t)*bri)/255:Return t
If bri>255 Then tw#=bri-255:tw=tw/255:Color cbetween(colourr(t),255,tw#),cbetween(colourg(t),255,tw#),cbetween(colourb(t),255,tw#):Return t
Return t
End Function
Function cBetween#(v1#,v2#,t#):dif#=v2-v1
Return v1+(dif*t)
End Function



Function CLSColour(n$,bri=255) ;bri goes from 0 to 512 ... 255 is normal!
t=Int(n)
If t=0 Then t=FindColour(n)
If bri=255 Then ClsColor colourr(t),colourg(t),colourb(t):Return t
If bri<255 Then ClsColor (colourr(t)*bri)/255,(colourg(t)*bri)/255,(colourb(t)*bri)/255:Return t
If bri>255 Then tw#=bri-255:tw=tw/255:ClsColor cbetween(colourr(t),255,tw#),cbetween(colourg(t),255,tw#),cbetween(colourb(t),255,tw#):Return t
Return t
End Function



Function SetBGColour(col$):bgcolour col:End Function

Function BGColour(col$)
If Lower(c)="transparent" Then bgcolr=-1:bgcolb=-1:bgcolg=-1
c=FindColour(col)
BGcolR=colourr(c)
BGcolG=colourg(c)
BGcolB=colourb(c)
End Function


Function CStringWidth(tx$)
getting=1
For t=1 To Len(tx)
c$=Mid(tx,t,1)
If c="{" Then getting=0
If c="}" Then getting=1:c=""
If getting Then tx2$=tx2+c
Next
Return StringWidth(tx2)
End Function









Function CText(x,y,tx$,cx=0,cy=0)
Local cod$,cod1$,cod2$
rd=ColorRed():gr=ColorGreen():bl=ColorBlue()
rd3=bgcolr:gr3=bgcolg:bl3=bgcolb
If cx Then x=x-(CStringWidth(tx$)/2)
If cy Then y=y-(FontHeight()/2)
getting=1
For t=1 To Len(tx)
c$=Mid(tx,t,1)
If c="{" Then getting=0:c=""
	If c="}" Then
	getting=1
	c="":cod1$="":cod2$=""
	If Len(cod)>4 Then cod1$=Upper(Left(cod,4)):cod2=Right(cod,Len(cod)-4)
	If cod1="COL:" Then Colour cod2
	If cod1="BGC:" Then BGColour cod2
	cod=""
	EndIf
If getting Then
If bgcolr>-1 Or bgcolg>-1 Or bgcolb>-1 Then rd2=ColorRed():gr2=ColorGreen():bl2=ColorBlue():Color bgcolr,bgcolg,bgcolb:Rect x,y,StringWidth(c),FontHeight(),1:Color rd2,gr2,bl2
Text x,y,c:x=x+StringWidth(c)
EndIf
If getting=0 Then cod$=cod+c$
Next
bgcolr=rd3:bgcolg=gr3:bgcolb=bl3
Color rd,gr,bl
End Function
