; ID: 1172
; Author: CodeD
; Date: 2004-10-05 23:23:49
; Title: Old School Demo
; Description: Retro Amiga/C64 type demo

Global gfxBG, gfxflare
Global screenbk=CreateImage(GraphicsWidth(),GraphicsHeight())
Global underw_a
Graphics3D 640, 480,32, 1
SetBuffer BackBuffer() 
TFormFilter 1
Color 0,0,0
fntcms=LoadFont("Comic Sans MS", 16, False, False, False)
SetFont fntcms
n = 0
grlx = 1
grly = 1
grly2 = 641
grlxdir = 1
grly = 1
timex = 0
speedtimex = 6
run1 = 0
currentsong = 1: song$ = "Tower of Flames": artist$ = "Frederic Hahn"
Type t_bullet
Field x,y
Field speedx,speedy
End Type
Global bullet.t_bullet
gfxBG=LoadImage("coolblue.png")
gfxface = LoadImage("interface4.png")
gfxgirl=LoadImage("amigagirl.png")
gfxflare=LoadImage("ice.png")
chnBackground=PlayMusic("z-tower.xm")
While Not KeyHit(1) 
If KeyDown(203) Then timex = timex - 1
If KeyDown(205) Then timex = timex + 1
;if currentsong = 1 then song$ = "" and artist$  = "" ;etc, etc. simple song chooser
Cls
DrawImage gfxBG, 1, 1
WobbleView()
DrawImage gfxface, 1, facey
Text 100, 365, "NOW PLAYING"
Text 15, 395, "TITLE: "+song$ 
Text 15, 410, "ARTIST: "+artist$
Text 65, 435, "UP/DOWN TO CHANGE SONG"
Text 40, 450, "LEFT/RIGHT TO CHANGE TEXT SPEED"
x=timex/10.0 Mod 7000
bulletchance = Rnd(0,10): If bulletchance = 5 Then bullet.t_bullet = New t_bullet: bullet\x = Rnd(0,200): bullet\y = Rnd(0,10): bullet\speedx = Rnd(1,3): bullet\speedy = Rnd(1,3)
bulletchance2 = Rnd(0,100): If bulletchance = 10 Then bullet.t_bullet = New t_bullet: bullet\x = Rnd(0,200): bullet\y = Rnd(0,10): bullet\speedx = Rnd(1,3): bullet\speedy = Rnd(1,3)
updategame()
rendergame()
If grlxdir = 1 Then grlx = grlx + 1
If grlxdir = 2 Then grlx = grlx - 1
If grlx = 100 Then grlxdir = 2
If grlx = 1 Then grlxdir = 1
SinScroll (" ............... CodeD aka (DJ) Krazy K Presents Amiga Tribute Vol. 1 ... Press up/down to change songs ... Press left/right to change text speed/direction Code: CodeD, Jeppe Neilson (SinScroll), Semar (bullet code), JFK (distort) Gfx: FrogDot (thx for the babe!), CodeD ... Mods for Charity ... your donations help children!!! ... Visit http://sites.gwala.net/tormented/amiga for more info and to order the CD ... greetings to: Ckob, Lenn, Noel Cower, JFK, DJ SeeNSay, LizardKing, FutureCrew, Paradox, Mark Sibly & The Blitz Basic Crew, The Anonymous Modder ... Gotta have mod??  Amiga Tribute Vol. 2 coming soon!!  Cybernoid 3D coming soon!! Quality apps like this and more from CrapSoft and Evolved Dev Studios ............................ ", 100-x, 135, timex/10.0)
SinScroll (" ............... CodeD aka (DJ) Krazy K Presents Amiga Tribute Vol. 1 ... Press up/down to change songs ... Press left/right to change text speed/direction Code: CodeD, Jeppe Neilson (SinScroll), Semar (bullet code), JFK (distort) Gfx: FrogDot (thx for the babe!), CodeD ... Mods for Charity ... your donations help children!!! ... Visit http://sites.gwala.net/tormented/amiga for more info and to order the CD ... greetings to: Ckob, Lenn, Noel Cower, JFK, DJ SeeNSay, LizardKing, FutureCrew, Paradox, Mark Sibly & The Blitz Basic Crew, The Anonymous Modder ... Gotta have mod??  Amiga Tribute Vol. 2 coming soon!!  Cybernoid 3D coming soon!! Quality apps like this and more from CrapSoft and Evolved Dev Studios ............................ ", 100-x-1, 135, timex/10.0)
DrawImage gfxgirl, grlx, grly
DrawImage gfxgirl, grlx, grly2
Flip  
If timex < 0 Then timex = 0
timex = timex + speedtimex
;If KeyDown(200) Then currentsong = currentsong + 1: song$ = "Tower of Flames" And artist$ = "Frederic Hahn": FreeSound chnbackground: chnBackground=PlayMusic("z-tower.xm")
;If KeyDown(208) Then currentsong = currentsong - 1: song$ = "Tower of Flames" And artist$ = "Frederic Hahn": FreeSound chnBackground: chnBackground=PlayMusic("z-tower.xm")
If KeyDown(203) Then speedtimex = speedtimex - 1
If KeyDown(205) Then speedtimex = speedtimex + 1
Wend 
;===================================
Function updategame()
;===================================
For bullet.t_bullet = Each t_bullet
bullet\x = bullet\x + bullet\speedx
bullet\y = bullet\y + bullet\speedy
Next
End Function
;===================================
Function rendergame()
;===================================
For bullet.t_bullet = Each t_bullet
DrawImage gfxflare,bullet\x,bullet\y
Next
End Function
Function SinScroll(txt$,x,y,am,amp=50,per=20,d=10)
For n=1 To Len(txt$)
Text x+xx,y+Sin(am+n*per)*amp,Mid$(txt$,n,1)
xx=xx+d
Next
End Function
Function WobbleView()
 gw#=GraphicsWidth()
 gh#=GraphicsHeight()
 underw_a=(underw_a+4)
 steph#=gh/32
 mu8#=gh/60
 If underw_a>359 Then underw_a=0
  For iif#=0 To gh-4  Step .001
   wsin#=(Sin((underw_a+iif)Mod 360.0)*mu8#)
   CopyRect 0,  iif,         gw,steph+4, 0,iif+wsin#, ImageBuffer(screenbk),BackBuffer()
   iif=iif+steph
  Next
End Function
