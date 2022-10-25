; ID: 2786
; Author: jfk EO-11110
; Date: 2010-11-21 00:09:05
; Title: Sprites Substitute
; Description: Get independent from Sprites

; As there has been an increasing amount of issues with sprites, I tried to write a substitute that emulates Sprites, using a simple
; Quad Mesh and an Update Function. The Functions will override the Blitz Functions, by using the same name, so replacing the use of 
; original Sprites in Sourcecode is easy. Don't forget the Init Part and the Update Function Call.
; When you use FreeEntity with such a Quadbased Sprite, you first need to call ReleaseSprite(entity), to remove
; the Sprite from the update list. That's it.

; please note there is still a problem with parents of sprites that will be scaled:
; while original sprites were not affected, these replacement sprites made of quads will be,
; so you should assign them to patents only after the parents were scaled, or do temporary
; unparent the quads while their parents are scaled. 



Graphics3D 800,600,32,2
SetBuffer BackBuffer()

; init sprite substitute ----------------------
Global ps_c=0, ps_max=10000
Dim pseudo_sprites(ps_max),ps_vmode(ps_max)
; end of init sprite substitute --------------



camera=CreateCamera()

;from docs: SpriteViewModes
;1: fixed (sprite always faces camera - Default) 
;2: free (sprite is independent of camera) 
;3: upright1 (sprite always faces camera, but rolls with camera as well, unlike mode no.1) 
;4: upright2 (sprite always remains upright. Gives a 'billboard' effect. Good For trees, spectators etc.) 


s1a=CreateSprite(camera) ; testing default mode (1) parented to camera, aka HUD element
ScaleSprite s1a,0.5,0.1
TranslateEntity s1a,-.45,.5,1
; using default view mode: 1

s1b=CreateSprite() ; testing default mode 1
TranslateEntity s1b,0,0,4 
EntityColor s1b,255,0,0
; using default view mode: 1


s2=CreateSprite() ; testing mode 2 
TranslateEntity s2,-2,1,4
EntityColor s2,0,255,0
SpriteViewMode s2,2


s3=CreateSprite() ; testing mode 3
TranslateEntity s3,2,1,4
EntityColor s3,0,0,255
SpriteViewMode s3,3


s4=CreateSprite() ; testing mode 4
;s4=LoadSprite("test_mask.bmp",4)
TranslateEntity s4,-3,1,3.9
EntityColor s4,255,0,255
SpriteViewMode s4,4



CameraRange camera,0.05,100
TurnEntity camera,-25,0,0

HandleSprite s2, -1,-1

;releasesprite(s3) ; seems to work
;FreeEntity s3

While KeyHit(1)=0
 TurnEntity s2,0,0,0.7
 TurnEntity camera,0,1,0
 UpdateSubstSprites(camera) ; update Rotation of Subst Sprites
 RenderWorld()
 VWait
 Flip 0
Wend
WaitKey()
End





; Sprite Substitute Functions --------------------------------------------------------------------------------------------
Function UpdateSubstSprites(camera)
 Local i
 For i=0 To ps_c-1
   If ps_vmode(i)=1 ; face to camera
    RotateEntity pseudo_sprites(i),EntityPitch(camera,1),EntityYaw(camera,1),EntityRoll(pseudo_sprites(i),1),1
   EndIf
   ;If ps_vmode(i)=2 ; free!
   ;EndIf
   If ps_vmode(i)=3
    RotateEntity pseudo_sprites(i),EntityPitch(camera,1),EntityYaw(camera,1),EntityRoll(pseudo_sprites(i),1),1
   EndIf
   If ps_vmode(i)=4
    RotateEntity pseudo_sprites(i),EntityPitch(pseudo_sprites(i),1),EntityYaw(camera,1),EntityRoll(camera,1),1
   EndIf
 Next
End Function

Function CreateSprite(parent=0)
 Local quad
 quad=CreateQuad4Sprite()
 If parent<>0 Then
  EntityParent quad,parent
  PositionEntity quad,EntityX(parent,1),EntityY(parent,1),EntityZ(parent,1),1
 Else
  PositionEntity quad,0,0,0,1
 EndIf
 EntityColor quad,255,255,255
 EntityFX quad,1 Or 16
 pseudo_sprites(ps_c)= quad
 ps_vmode(ps_c)=1
 If ps_c<ps_max Then ps_c=ps_c+1
 Return quad
End Function


Function LoadSprite(texfil$, flags=0,parent=0)
 Local tex, quad
 If ((flags And 2)=2) And ((flags And 1)=0) Then flags=flags Or 1 ; prevent crash by alpha bug
 tex=LoadTexture(texfil$,flags)
 If tex=0 Then tex=CreateTexture(4,4,0) ; then again, lets be a bit error tolerant :)
 quad=CreateQuad4Sprite()
 EntityTexture quad,tex
 If parent<>0 Then
  EntityParent quad,parent
  PositionEntity quad,EntityX(parent,1),EntityY(parent,1),EntityZ(parent,1),1
 Else
  PositionEntity quad,0,0,0,1
 EndIf
 EntityFX quad,1 Or 16
 pseudo_sprites(ps_c)= quad
 ps_vmode(ps_c)=1
 If ps_c<ps_max Then ps_c=ps_c+1
 Return quad
End Function


Function HandleSprite(ent,x#,y#) ; seems to work now, looks kind of hacked ...
 surf=GetSurface(ent,1)
 x=-x
 y=-y
 VertexCoords surf,0,-1+x#,1+y#,0 
 VertexCoords surf,1,1+x#,1+y#,0 
 VertexCoords surf,2,-1+x#,-1+y#,0 
 VertexCoords surf,3,1+x#,-1+y#,0 
End Function



Function RotateSprite(ent,a#)
 RotateEntity ent,EntityPitch(ent,0), EntityYaw(ent,0),a#,0 ; 
End Function

Function ScaleSprite(ent,w#,h#)
 ScaleEntity ent,w#,h#,0.001 ; for some reason I cannot use zero for the depth, it hides the HUD sprite.
End Function


Function SpriteViewMode(ent, vmode)
 Local i
 For i=0 To ps_c-1
  If pseudo_sprites(i)=ent
   ps_vmode(i)=vmode
  EndIf
 Next
End Function


Function CreateQuad4Sprite()
 Local mesh%,surf%,v1%,v2%,v3%,v4%
 mesh=CreateMesh()
 surf=CreateSurface(mesh)
 v1=AddVertex(surf,-1,1,0,0,0)
 v2=AddVertex(surf,1,1,0,1,0)
 v3=AddVertex(surf,-1,-1,0,0,1)
 v4=AddVertex(surf,1,-1,0,1,1)
 VertexColor surf,v1,255,255,255,1.0
 VertexColor surf,v3,255,255,255,1.0
 VertexColor surf,v2,255,255,255,1.0
 VertexColor surf,v4,255,255,255,1.0
 AddTriangle(surf,0,1,2)
 AddTriangle(surf,3,2,1)
 Return mesh
End Function


Function ReleaseSprite(ent)
 Local i,i2
 For i=0 To ps_c-1
  If pseudo_sprites(i)=ent
   For i2=i To ps_c-1 
    pseudo_sprites(i2)=pseudo_sprites(i2+1)
    ps_vmode(i2)=ps_vmode(i2+1)
   Next
   ps_c=ps_c-1
   Exit
  EndIf
 Next
End Function

; End of Sprite Substitute Functions --------------------------------------------------------------------------------------------
