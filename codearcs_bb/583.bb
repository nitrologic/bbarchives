; ID: 583
; Author: Tri|Ga|De
; Date: 2003-02-10 13:36:45
; Title: Mappy Collision, Animation and shooting demo
; Description: Mappy Exampel

;** Map Stuff **
 Include "mappybb.bb"
 
 ;** Set Up Screen **
 Const SCREEN_WIDTH  = 800
 Const SCREEN_HEIGHT = 600

 ;** Globals **
 Global GameOver = False
 Global Menu, Logo
 Global px = 0
 Global py = 0
 Global StartX = 368
 Global StartY = 145
 Global bx, by, CollX, CollY
 Global Speed1 = 1
 Global Speed2 = 2
 Global px2 = 0
 Global py2 = 0
 Global Tank1, Tank2, bullet1
 Global BSX#, BSY#, stxb#, styb#, stpx#, stpy#, mvx#, mvy#, mv#, nc, stx#, sty#
 Global Direction, DoAnimate
 Global iCanShoot = 0
 ;** Types **


;** Initializing **
 setupScreen()
 InitVariables()
;** Main loop
While Not GameOver = True
 Control()
 Do_render()
 Flip
Wend
MapFreeMem ()
EndGraphics
End
;******************
;*** Functions ****
Function setupScreen()
 Graphics SCREEN_WIDTH, SCREEN_HEIGHT,16
 MapLoad ("maps\level1.FMP")
 MapInitAnims ()
 SetBuffer BackBuffer()
 Menu = LoadImage("graphics\menu.jpg")
 MidHandle Logo

 Tank1 = LoadImage("graphics\tank1.bmp")
 MaskImage Tank1,0,0,0
 MidHandle Tank1
 bullet1 = LoadImage("graphics\bullet.bmp")
 MaskImage bullet1,0,0,0
 MidHandle bullet1

 Tank2 = LoadImage("graphics\tank1.bmp")
 MaskImage Tank2,0,0,0
 MidHandle Tank2
End Function

Function InitVariables()
  CollX = px + StartX
  CollY = py + StartY
  DoAnimate = 0
End Function

Function Control()
 CollX = px + StartX
 CollY = py + StartY
 If KeyHit(1) Then GameOver = True
 ;** Top player
 If KeyDown(205) Then ;** Right
  If Not Collision(CollX+16,CollY)
   If Not Collision(CollX+16,CollY-15)
    If Not Collision(CollX+16,CollY+15)
     px = px + Speed1
     stxb = stxb - speed1
     Direction = 2
    End If
   End If  
  End If 
 End If 
 If KeyDown(203) Then ;** Left
  If Not Collision(CollX-16,CollY)
   If Not Collision(CollX-16,CollY-15)
    If Not Collision(CollX-16,CollY+15)
     px = px - Speed1
     stxb = stxb + speed1
     Direction = 4
    End If
   End If  
  End If 
 End If 
 If KeyDown(200) Then ;** Up
  If Not Collision(CollX,CollY-16)
   If Not Collision(CollX-15,CollY-16)
    If Not Collision(CollX+15,CollY-16)
     py = py - Speed1
     styb = styb + speed1
     Direction = 1
    End If
   End If  
  End If 
 End If 
 If KeyDown(208) Then ;** Down
  If Not Collision(CollX,CollY+16)
   If Not Collision(CollX-15,CollY+16)
    If Not Collision(CollX+15,CollY+16)
     py = py + Speed1
     styb = styb - speed1
     Direction = 3
    End If
   End If  
  End If 
 End If 
 If KeyDown(57) Then
  MapSetBlock(14,22,27)
  MapSetBlock(15,22,1)
  MapSetBlock(16,22,1)
  MapSetBlock(17,22,26)
 End If
 ;** Bottom playerr
 If KeyDown(52) Then px2 = px2 + Speed2
 If KeyDown(51) Then px2 = px2 - Speed2
 If KeyDown(30) Then py2 = py2 - Speed2
 If KeyDown(44) Then py2 = py2 + Speed2

 ;** Top player
 If px < 0 Then px = 0
 If px > 7300  Then px = 7300
 If py < 0 Then py = 0
 If py > 7700  Then py = 7700
 ;** Bottom player
 If px2 < 0 Then px2 = 0
 If px2 > 7300  Then px2 = 7300
 If py2 < 0 Then py2 = 0
 If py2 > 7700  Then py2 = 7700

End Function

Function Do_render()
 ;** Top map
 Viewport 0,0,700,300
 MapUpdateAnims ()
 ;Draw BG at map coords px, py and at screen pos 0, 0 at size 640, 480
 MapDrawBG (px, py, 0, 0, 700, 300)
 DrawImage Tank1,StartX,145
 If iCanShoot = 1 Then 
  stxb = stxb + stpx
  styb = styb + stpy
  nc = nc + 1
  DrawImage bullet1, stxb, styb
  If nc > Floor(mv) Then
   iCanShoot = 0
   nc = 0
  End If 
 End If 
 
 ;Draw FG layer 0
 MapDrawFG (px, py, 0, 0, 700, 300, 0)
 
 
 ;** Bottom map
 Viewport 0,301,700,600
 MapUpdateAnims ()
 ;Draw BG at map coords px, py and at screen pos 0, 0 at size 640, 480
 MapDrawBG (px2, py2, 0, 301, 700, 300)
 DrawImage Tank2,StartX,450
 ;Draw FG layer 0
 MapDrawFG (px2, py2, 0, 301, 700, 300, 0)


 ;** Menu
 Viewport 701,0,800,600
 DrawImage Menu,701,0
 Color 0,0,0
 Text 710,10,"px = " + px
 Text 710,20,"py = " + py

 Text 710,30,"px2 = " + px2
 Text 710,40,"py2 = " + py2


 bx = MapGetXOffset(px + StartX, py + StartY)
 by = MapGetYOffset(px + StartX, py + StartY)
 Text 710,50,"bx = " + bx
 Text 710,60,"by = " + by

 Text 710,80, CollType
 
 If iCanShoot = 0 Then
  If IsBlockVisible("99998") = True Then
   Sline(BSX, BSY, (11*32), (4*32))
   stxb = BSX+16
   styb = BSY+16
   DrawImage bullet1, stxb, styb
   iCanShoot = 1
  End If 
 End If

 If iCanShoot = 1 Then 
  Text 710,90, "Kan skyde"
 Else
  Text 710,90, "" 
 End If
 Text 710,100,"stxb = " + stxb
 Text 710,110,"styb = " + styb
 Text 710,120,"BSX = " + bsx
 Text 710,130,"BSY = " + bsy
 Text 710,140,"stpx = " + stpx
 Text 710,150,"stpy = " + stpy
End Function

Function IsBlockVisible(user1)
 For xstep = bx-11 To bx + 10
  For yStep = by-4 To by + 4
   b.BLKSTR = MapGetBlock(xstep,ystep)
   If b\user1 = user1 Then 
    BSX = (xstep*32) - px
    BSY = (yStep*32) - py
    Return 1
   End If 
  Next
 Next 
 Return 0
End Function

Function SLine(stx#,sty#,enx#,eny#)
 mvx# = Stx - enx
 mvy# = sty - eny
 If mvx < 0 Then mvx = -mvx
 If mvy < 0 Then mvy = -mvy
 If mvy > mvx 
  mv#=mvy 
 Else 
  mv#=mvx
 End If 

 stpx# = (mvx/mv)
 If Stx > enx Then stpx = -stpx

 stpy# = (mvy/mv)
 If Sty > eny Then stpy = -stpy
End Function

Function Collision (x, y)
 b.BLKSTR = MapGetBlock (x/mapblockwidth, y/mapblockheight) ;These must be valid map block coords
 If b\tl Then Return 1
 Return 0
End Function

Function BlockDescription (x, y)
 b.BLKSTR = MapGetBlock (x/mapblockwidth, y/mapblockheight) ;These must be valid map block coords
 If b\user1 <> "0" Then Return b\user1
 Return 0
End Function
