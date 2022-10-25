; ID: 1401
; Author: SillyPutty
; Date: 2005-06-15 23:31:10
; Title: Bullets with trails
; Description: shooting bullets with trails, ala Crimsonland

Strict

Framework brl.glmax2d
Import brl.graphics
Import brl.linkedlist
SetGraphicsDriver GLMax2DDriver()
AppTitle = "Bullet Test - by Erick 'Deux' Grove"

Graphics 800,600,0



AutoMidHandle True

Global bulletimage:TImage = CreateImage(16,16,DynamicImage|MaskedImage)
Global bulletList:TList

Type bullet
Field x#
Field y#
Field origin_x#
Field origin_y#
Field scale_factor#
Field alpha_factor#

Field life#
Method draw()
SetColor 200,125,125
DrawRect x,y+(scale_factor*5),3,5
SetScale 1,scale_factor
SetColor 100,100,100
SetAlpha alpha_factor
DrawRect origin_x,origin_y,3,5
SetScale 1,1
SetAlpha 1
End Method

Method update()
y:-1
life:-1
scale_factor:-3
alpha_factor:-0.03

If(alpha_factor <0) ListRemove bulletList,Self
End Method

End Type

bulletList = CreateList()

Cls
SetColor 200,200,200
DrawRect 0,0,3,10
GrabImage bulletimage,0,0

SetBlend(alphablend)

Function UpdateBullets()

For Local b:bullet = EachIn bulletList
b.Draw()
b.Update()

Next 

End Function

While Not KeyDown(KEY_ESCAPE)

SetScale 1,1
SetAlpha 1
If KeyHit(KEY_SPACE)
Local mybullet:bullet = New bullet
mybullet.life = 100
mybullet.alpha_factor=1
mybullet.x = MouseX()
mybullet.y = MouseY()
myBullet.origin_x = mybullet.x
myBullet.origin_y = mybullet.y

bulletList.addLast(mybullet)
End If

UpdateBullets()

Flip
Cls
Wend

FlushMem
End
