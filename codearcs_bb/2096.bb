; ID: 2096
; Author: xtremegamr
; Date: 2007-08-23 00:17:00
; Title: circle wars
; Description: A little game I made to test out an idea of mine...

;circle wars
;programmed by xtremegamr

AppTitle "circle wars"
SetGFX("Have fun shooting other dots","over the internet!","Controls:","WASD-Move","Mouse-Aim","Left Click- Shoot")

;globals
Global CIRCLESIZE=5,XHAIRSIZE=5
Global GAMESTATUS

;user type
Type user
	Field id ;id of player
	Field r,g,b ;color of player
	Field x,y ;coordinates
	Field xx,xy ;crosshair coordinates
	Field acrex,acrey ;acre coordinates
	Field spd ;player speed
	Field damage ;damage done by gun
	Field health ;health left
End Type

;;start game
GAMESTATUS=StartNetGame()

Select GAMESTATUS
	Case 0
		RuntimeError "The game could not be started."
	Case 1
		Print "You joined a game!"
	Case 2
		Print "You hosted a game!"
	Default
		RuntimeError "Something went VERY wrong!"
End Select

;;create player
Global p1.user=New user

p1\x=Rand(0,800)
p1\y=Rand(0,600)
p1\xx=p1\x
p1\xy=p1\y
p1\acrex=Rand(-5,5)
p1\acrey=Rand(-5,5)
p1\spd=Rand(5,7)
p1\damage=Rand(8,12)
p1\health=Rand(100,110)
p1\r=Rand(0,255)
p1\g=Rand(0,255)
p1\b=Rand(0,255)
name$=Input("What is your name? ")
p1\id=CreateNetPlayer(name$)

;main loop
While Not KeyHit(1)

SetBuffer BackBuffer()
Cls

updategame()

Flip
Wend ;end of main loop

End

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;functions
;SetGFX()
;what do you think it does?
Function SetGFX(imagefile$,info1$,info2$,info3$,info4$,info5$,info6$)

Graphics 800,600,0,2

;get selected mode
modes=CountGfxModes()
selectedmode=0
banner=LoadImage(imagefile$)
MidHandle banner
y=ImageHeight(banner)

Repeat

SetBuffer BackBuffer()
Cls

;draw banner
DrawImage banner,400,ImageHeight(banner)/2

;show gfx modes
For x=0 To modes

If x=selectedmode Then

Select x
	Case 0
		Text 400,y+12,"Windowed (default)",True,True
	Case 1
		Text 400,y+12,GfxModeWidth(selectedmode)+ "," +GfxModeHeight(selectedmode)+ "," +GfxModeDepth(selectedmode)+ " ->",True,True
	Case modes
		Text 400,y+12,"<- " +GfxModeWidth(selectedmode)+ "," +GfxModeHeight(selectedmode)+ "," +GfxModeDepth(selectedmode),True,True
	Default
		Text 400,y+12,"<- " +GfxModeWidth(selectedmode)+ "," +GfxModeHeight(selectedmode)+ "," +GfxModeDepth(selectedmode)+ " ->",True,True
End Select

Exit
End If

Next

;show text
Text 400,y+36,info1$,True,True
Text 400,y+48,info2$,True,True
Text 400,y+60,info3$,True,True
Text 400,y+72,info4$,True,True
Text 400,y+84,info5$,True,True
Text 400,y+96,info6$,True,True

;controls
If KeyHit(28) Then Exit ;enter
If KeyHit(203) Then selectedmode=selectedmode-1
If KeyHit(205) Then selectedmode=selectedmode+1

;constraints
If selectedmode<0 Then selectedmode=0
If selectedmode>modes Then selectedmode=modes

Flip
Forever

;do selected mode
If selectedmode=0 Then Graphics 800,600,0,2
If selectedmode>=1 Then Graphics GfxModeWidth(selectedmode),GfxModeHeight(selectedmode),GfxModeDepth(selectedmode),1

End Function

;updategame()
;updates the game...duh!
Function updategame()

draw() ;draw everything
travel() ;travelling between acres
controls() ;player controls
updateusers() ;updates all users
sendinfo() ;sends your user type variables to other players

End Function

;draw()
;draws the users
Function draw()

For p.user=Each user

If p\acrex=p1\acrex And p\acrey=p1\acrey Then
	Oval p\x,p\y,CIRCLESIZE,CIRCLESIZE,True ;circle
	drawxhair(p\x,p\y) ;xhair
End If

Next

End Function

;drawxhair()
;draws the xhair
Function drawxhair(x,y)

Line x-XHAIRSIZE,y,x+XHAIRSIZE,y ;left-right
Line x,y-XHAIRSIZE,x,y+XHAIRSIZE ;up-down

End Function

;travel()
;takes care of travelling between acres
Function travel()

If p1\x<0 Then p1\x=GraphicsWidth() : p1\acrex=p1\acrex-1
If p1\x>GraphicsWidth() Then p1\x=0 : p1\acrex=p1\acrex+1

If p1\y<0 Then p1\x=GraphicsHeight() : p1\acrey=p1\acrey-1
If p1\y>GraphicsHeight() Then p1\x=0 : p1\acrey=p1\acrey-1

End Function

;controls()
;player controls
Function controls()

;move
If KeyDown(17) Then p1\y=p1\y-p1\spd
If KeyDown(31) Then p1\y=p1\y+p1\spd

If KeyDown(30) Then p1\x=p1\x-p1\spd
If KeyDown(32) Then p1\x=p1\x+p1\spd

;shoot
If MouseHit(1) Then

For p.user=Each user

If circleshot(p\x,p\y,p1\xx,p1\xy) And (p\acrex=p1\acrex And p\acrey=p1\acrey) Then
	p\health=p\health-p1\damage
End If

Next

End If

End Function

;circleshot()
;returns true is the circle was shot
Function circleshot(tx,ty,xx,xy) ;target x, target y, xhair x, xhair y

If (xx>tx And xx<tx+CIRCLESIZE) And (xy>ty And xy<ty+CIRCLESIZE) Then
	Return True
End If

Return False

End Function

;updateusers()
;takes all of the messages recieved from other players and updates their user types
Function updateusers()

If RecvNetMsg() Then

For p.user=Each user

If p\id=NetMsgFrom() Then

Select NetMsgType()
	Case 1 ;r,g,b
		d$=NetMsgData()
		
		comma1pos=Instr(d$,",")
		comma2pos=Instr(d$,",",comma1pos+1)
		
		glen=(comma1pos+1)-comma2pos
		
		p\r=Mid(d$,1,comma1pos-1)
		p\g=Mid(d$,comma1pos+1,glen)
		p\b=Mid(d$,comma2pos+1)
	Case 2 ;x,y
		d$=NetMsgData()
		
		commapos=Instr(d$,",")
		
		p\x=Mid(d$,1,commapos-1)
		p\y=Mid(d$,commapos+1)
	Case 3 ;xx,xy
		d$=NetMsgData()
		
		commapos=Instr(d$,",")
		
		p\xx=Mid(d$,1,commapos-1)
		p\xy=Mid(d$,commapos+1)
	Case 4 ;acrex,acrey
		d$=NetMsgData()
		
		commapos=Instr(d$,",")
		
		p\acrex=Mid(d$,1,commapos-1)
		p\acrey=Mid(d$,commapos+1)
	Case 5 ;speed,damage,health
		d$=NetMsgData()
		
		comma1pos=Instr(d$,",")
		comma2pos=Instr(d$,",",comma1pos+1)
		
		glen=(comma1pos+1)-comma2pos
		
		p\spd=Mid(d$,1,comma1pos-1)
		p\damage=Mid(d$,comma1pos+1,glen)
		p\health=Mid(d$,comma2pos+1)
End Select

playerfound=1
Exit

End If

Next

If playerfound=0 Then p.user=New user : p\id=NetMsgFrom()

End If

End Function

;sendinfo()
;sends your info
Function sendinfo()

SendNetMsg(1,p1\r+ "," +p1\g+ "," +p1\b,p1\id)
SendNetMsg(2,p1\x+ "," +p1\y,p1\id)
SendNetMsg(3,p1\xx+ "," +p1\xy,p1\id)
SendNetMsg(4,p1\crex+ "," +p1\acrey,p1\id)
SendNetMsg(5,p1\spd+ "," +p1\damage+ "," +p1\health,p1\id)

End Function
