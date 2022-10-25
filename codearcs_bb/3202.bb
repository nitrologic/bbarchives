; ID: 3202
; Author: Dan
; Date: 2015-04-27 16:43:15
; Title: Simple Test Game Vertical Shooter
; Description: Asteroid Shooter

;Testshooter by Dan (found at www.blitzbasic.com)

ScrW=320
ScrH=240
frameTimer=CreateTimer(60) 
;AppTitle "Testshooter", "Quit Game:" 
Graphics ScrW*4,ScrH*4,0,2	;<-- This sets the window to be 640,480
Graphics ScrW,ScrH,0,3	;<-- The 3 tells it to only change the resolution, not the window size.
SeedRnd MilliSecs
SetBuffer BackBuffer()

Viewport 0,0,320,240

Title=LoadImage("Title.bmp")
DrawImage Title,0,0

Text 80,122,"Game Data is loading"
Text 80,132,"Please Wait"

Tile0=LoadImage("Tile.bmp")
Tile1=LoadImage("Tile1.bmp")
Tile2=LoadImage("Tile2.bmp")
Tile3=LoadImage("Tile3.bmp")
Tile4=LoadImage("Tile4.bmp")
Tile5=LoadImage("Tile5.bmp")
Tile6=LoadImage("Tile6.bmp")
Tile7=LoadImage("Tile7.bmp")
Tile8=LoadImage("Tile8.bmp")

Tile=Tile0

w=0
Wall1=LoadImage("Wall.bmp")
Wall2=LoadImage("Wall1.bmp")

Wall=Wall1

Ship=LoadAnimImage("MyShip.bmp",16,16,0,3)
Laser=LoadImage("MyShipLaser.bmp")
Enemy=LoadImage("Enemy.bmp")

Delay 1000
Flip
DrawImage Title,0,0

Text 80,122,"Game is Ready"
Text 80,132,"Press Fire"

Flip

Repeat
Until KeyDown(157)

Flush()

Type Bull
    Field x,y
End Type

Type Eship
    Field x
    Field y
    Field x1
    Field y1
    Field state
End Type

DiffTime=300    ;Bullet time to shoot
offset=-24
offwall1=-16
offwall2=-16
offwall3=-16
offwall4=-16

.start
an=1
Score=0
XPos=152
Bullet=1
BullSy=220
time=MilliSecs()
BullTime=MilliSecs()
Etime=MilliSecs()
WallChange=MilliSecs()
Ediff=1500
Enmy=0
dead=0
wave=0
MaxBull=5

Repeat
    Update=MilliSecs()           
    Gosub Drawtile
    Gosub DrawWall
    Gosub DrawShip
    
    If KeyDown(157) And ((MilliSecs()-BullTime)>DiffTime) Then
        If (Bullet<=MaxBull) Then
            Bullet=Bullet+1
            Missile.Bull=New Bull
            Missile\x=Xpos
            missile\y=BullSy
            BullTime=MilliSecs()
        EndIf
    EndIf
    Gosub Shoot
    Gosub DrawEnemy    
    esc=KeyDown(1)
    Text 1,1,"Score:"+Score
    Repeat
    Until MilliSecs() > Update+12
    WaitTimer(frameTimer) 
    Flip True
Until esc Or dead=1

Flush()

Repeat
    Update=MilliSecs()
    Gosub Drawtile
    Gosub DrawWall
    WaitTimer(frameTimer) 
    Flip 1
    Repeat
    Until MilliSecs() > Update+8
Until (Not KeyDown(1)) And (Not KeyDown(157))

Flush()

DHelp=MilliSecs()
DTrack=0
Help$=""
Repeat
    Update=MilliSecs()
    Gosub Drawtile
    Gosub DrawWall
    Gosub DrawHelp
    Text 1,1,"Score:"+Score
    Text 120,122,"Game Over"
    Text 160-((Len(Help$)/2)*FontWidth()),122+FontHeight(),Help$
    esc=KeyDown(1)
    fire=KeyDown(157)
    Repeat
    Until MilliSecs() > Update+8
WaitTimer(frameTimer) 
    Flip 1
    
Until esc Or fire

Flush()

If fire Then Delete Each Eship : Delete Each Bull :Goto start

End

.DrawHelp

If MilliSecs()-Dhelp>1500
DHelp=MilliSecs()
DTrack=DTrack+1
  Select DTrack
    Case 4  
      Help$="Press fire (ctrl) to start"
    Case 6 
      Help$="Press 1-9"
    Case 8 
      Help$="To Change Background"
    Case 10 
      Help$="Press 0"
    Case 12 
      Help$="To Change the Wall"
    Case 15
      Help$=""
      Dtrack=0
  End Select
End If 

Return 

.Drawtile

If KeyDown(2) Then Tile=Tile0
If KeyDown(3) Then Tile=Tile1
If KeyDown(4) Then Tile=Tile2
If KeyDown(5) Then Tile=Tile3
If KeyDown(6) Then Tile=Tile4
If KeyDown(7) Then Tile=Tile5
If KeyDown(8) Then Tile=Tile6
If KeyDown(9) Then Tile=Tile7
If KeyDown(10) Then Tile=Tile8

If KeyDown(11) And (MilliSecs() - WallChange)>1000
  w=w+1 : If w=2 Then w=0
  Select w
    Case 0: Wall=Wall1
    Case 1: Wall=Wall2
  End Select
  WallChange=MilliSecs()
EndIf

offset=offset+1: If offset =0 Then offset=-128
For x=64 To 255 Step 128
    For y=1 To 264 Step 128
        DrawBlock Tile,x,offset+y
    Next
Next
;Color 0,0,0
;Rect 256,0,64,240,1
;Color 255,255,255
Return

.Shoot

If Bullet>0 Then
    For missile.bull = Each Bull
        missile\y=missile\y-3
        If missile\y<-16 Then
            Delete missile.bull
            bullet=bullet-1
            Else
            DrawImage Laser,missile\x,missile\y
        EndIf
    Next
EndIf
Return

.DrawWall

Offwall1=Offwall1+5:If offwall1=>0 Then offwall1=-16
Offwall2=Offwall2+4:If offwall2=>0 Then offwall2=-16
Offwall3=Offwall3+3:If offwall3=>0 Then offwall3=-16
Offwall4=Offwall4+2:If offwall4=>0 Then offwall4=-16
For x=0 To 3
    For y=0 To 16
        Select x
            Case 0
                DrawBlock Wall,x*16,Offwall1+(y*16)
                DrawBlock Wall,304-(x*16),Offwall1+(y*16)
            Case 1
                DrawBlock Wall,x*16,Offwall2+(y*16)
                DrawBlock Wall,304-(x*16),Offwall2+(y*16)
            Case 2
                DrawBlock Wall,x*16,Offwall3+(y*16)
                DrawBlock Wall,304-(x*16),Offwall3+(y*16)
            Case 3
                DrawBlock Wall,x*16,Offwall4+(y*16)
                DrawBlock Wall,304-(x*16),Offwall4+(y*16)
        End Select
    Next
Next
Return

.DrawShip
  If KeyDown(203) Then Xpos=Xpos - 5
  If KeyDown(205) Then Xpos=Xpos + 5
  If Xpos<=64 Then Xpos=64
  If Xpos=>241 Then Xpos=241
  If MilliSecs()-time>70 Then time=MilliSecs(): an=an+1:If an=3 Then an=0
  DrawImage Ship,XPos,220,2-an
Return

.DrawEnemy
;x,y,x1,y1,state
If (MilliSecs()-Etime)>Ediff Then
    ;  wave=0 - Start or the enemies are hit or out of screen
    If wave=0 Then
        wave=Rnd(1,5)
        ShipClass=Rnd(1,2)
        If ShipClass=1
         FreeImage Enemy
         Enemy=LoadImage("Enemy.bmp")
        Else
          FreeImage Enemy
          Enemy=LoadImage("Enemy1.bmp")
        EndIf
        Select wave
            Case 1
                Restore wav1
                Read Enr,ESTx,ESTy,ESPx,ESPy
            Case 2
                Restore wav2
                Read Enr,ESTx,ESTy,ESPx,ESPy
            Case 3
                Restore wav3
                Read Enr,ESTx,ESTy,ESPx,ESPy
            Case 4
                Restore wav4
                Read Enr,ESTx,ESTy,ESPx,ESPy
            Case 5
                Enr =Rnd(1,15)
                ESTx=Rnd(70,218)
                ESTy=-20
                ESPx=0
                ESPy=Rnd(2,4)
        End Select
        
    Else
        ; Move enemies
        If Enmy<Enr Then
            Enmy=Enmy+1
            hip.Eship = New Eship
            hip\State=1
            hip\x=ESTx
            hip\y=ESTy
            hip\X1=ESPx
            hip\Y1=ESPy
        EndIf
    EndIf
    Ediff=150
    Etime=MilliSecs()
EndIf

For hip.eship = Each Eship
    hip\X=hip\x+hip\x1
    hip\Y=hip\Y+hip\y1
    If hip\X<=64 Then hip\x=65   : hip\x1=hip\x1*(-1)
    If hip\X=>241 Then hip\x=240 : hip\x1=hip\x1*(-1)
    
    If (hip\Y>280 And b<199) Then
        Delete hip.eship
        b=b+1 : If b=>enr Then b=200
        Else
        DrawImage Enemy,hip\x,hip\y
    EndIf

    If hip.eship <> Null Then
        If ImagesCollide(Enemy,hip\x,hip\y,0,Ship,xpos,220,2-an) Then
            Dead=1
        EndIf
    EndIf

    For missile.bull=Each Bull
        If hip.eship<>Null And Missile.bull<>Null Then
            If ImagesOverlap(Laser,missile\x,missile\y,Enemy,hip\x,hip\y) Then
                Delete hip.eship
                Delete missile.bull
                b=b+1 : If b=>enr Then b=200
                bullet=bullet-1
                Score=Score+1
            EndIf
        EndIf
    Next
Next

If b=>200 Then
    b=0
    Enmy=0
    Ediff=2000
    Etime=MilliSecs()
    wave=0
EndIf
Return

.wav1
Data 3,100,-16,1,2
.wav2
Data 5,240,-16,2,1
.wav3
Data 7,200,-16,-3,1
.wav4
Data 10,69,-16,1,1

Function Flush()
FlushKeys
FlushJoy
FlushMouse
End Function
