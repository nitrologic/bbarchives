; ID: 1146
; Author: Jan_
; Date: 2004-08-25 01:40:44
; Title: 3 Blur effekts
; Description: Blur, Blur Blend and Dream

;Blur_in_use.bb 
;Include the Libs 
   Include "Blur.bb" 
   Include "Dreamblur.bb" 
   Include "Dreamview.bb" 
   Include "FPS.bb" 
   Include "Skybox\Loadskybox.bb" 

;Init Graph 
   Graphics3D 1024,768,32,1 
   SetBuffer BackBuffer() 

;init Cam 
   Cam=CreateCamera() 
   CameraRange cam,0.1,1000 


;Medias 4 the Motion Blur 
   cube1=CreateCube() 
   MoveEntity cube1,0,0,3 
   cube2=CreateCube() 
   MoveEntity cube2,0,1.5,4 
   EntityColor cube2,0,0,255 

;Init Blur 
   CreateBlur(Cam) 

Repeat 

   ;----------------------------------------------------- 
   ;Kontroll 
   ;----------------------------------------------------- 
   If KeyDown(200) Then TurnEntity cam,1,0,0 
   If KeyDown(208) Then TurnEntity cam,-1,0,0 
   If KeyDown(203) Then TurnEntity cam,0,1,0 
   If KeyDown(205) Then TurnEntity cam,0,-1,0 
   If KeyHit(2) Then blur# = 0.1 
   If KeyHit(3) Then blur# = 0.2 
   If KeyHit(4) Then blur# = 0.3 
   If KeyHit(5) Then blur# = 0.4 
   If KeyHit(6) Then blur# = 0.5 
   If KeyHit(7) Then blur# = 0.6 
   If KeyHit(8) Then blur# = 0.7 
   If KeyHit(9) Then blur# = 0.8 
   If KeyHit(10) Then blur# = 0.9 
   If KeyHit(11) Then blur# = 1.0 
   ;----------------------------------------------------- 
    
   ;----------------------------------------------------- 
   ;The Updates 
   ;----------------------------------------------------- 
   RenderWorld 
   UpdateBlur(blur) 
   ;----------------------------------------------------- 

   ;----------------------------------------------------- 
   ;Calculate the Frames per second 
   ;-----------------------------------------------------    
   update_FPS(FPS) 
   ;----------------------------------------------------- 

   ;----------------------------------------------------- 
   ;Some infos 
   ;----------------------------------------------------- 
   Text 10,60,"FPS: " +Int(FPS#) 
    
   Text 0,0,"Motionblur by Jan Kuhnert." 
   Text 0,20,"weiterentwicklung der Arkblur.bb. Funktioniert nun mit Farbänderung auf jeder auflösung und in jedem Bildschirmverhältniss" 
   Text 0,160,"Use the keys 1,2,3,4,5,6,7,8,9,0 to change the blur ''power''." 
   Text 0,40,"Use the cursor keys to turn the camera" 
    
   Text GraphicsWidth()/2,GraphicsHeight()/2,"Visit www.FX-Visions.de",1,1 
    

       
   ;gibt die Gerenderten Polygon aus, muss später entfernt werden! 
   Text 10,80,"Polygon: "+TrisRendered() 
       
   ;Gibt die Aktivierten Texturen zurück 
   Text 10,100,"Textures used: "+ActiveTextures () 
          
   ;Gibt den verwendeten VRAM aus 
   Text 10,120,"VRAM used: "+(TotalVidMem()-AvailVidMem())/(1024.0^2) 

   ;----------------------------------------------------- 

   ;----------------------------------------------------- 
   ;Show Pic 
   ;----------------------------------------------------- 
   Flip 0 
   ;----------------------------------------------------- 

Until KeyHit(1) 

;Free the Motionblur 
   freeblur() 
    
;Free Medias 
   FreeEntity cube1 
   FreeEntity cube2 
    
;load medias for Example number2 
   PositionEntity cam,5,5,7 
   mesh=LoadMesh("example.b3d") 
    
;Create Skybox    
   skybox=Loadskybox("Skybox\Sky",cam) 

;Create Dream Blur 
   Createdreamblur(Cam) 
    

;Variable Setzten 
   g#=0.001 
Repeat 

   If KeyDown(200) Then TurnEntity cam,1,0,0 
   If KeyDown(208) Then TurnEntity cam,-1,0,0 
   If KeyDown(203) Then TurnEntity cam,0,1,0 
   If KeyDown(205) Then TurnEntity cam,0,-1,0 
   dream#=dream#+g# 
   If dream> 1 Then g#=-0.001 
   If dream <0.1 Then g# = 0.001 
    
    
   RenderWorld 
   Updatedreamblur(dream) 
    
   update_FPS(10) 

   Text 10,60,"FPS: " +Int(FPS#) 
    
   Text 0,0,"Dreamblurfilter by Jan Kuhnert." 

   Text 0,40,"Use the cursor keys to turn the camera" 
    
   Text GraphicsWidth()/2,GraphicsHeight()/2,"Visit www.FX-Visions.de",1,1 
    

   ;gibt die Gerenderten Polygon aus, muss später entfernt werden! 
   Text 10,80,"Polygon: "+TrisRendered() 
          
   ;Gibt die Aktivierten Texturen zurück 
   Text 10,100,"Textures used: "+ActiveTextures () 
          
   ;Gibt den verwendeten VRAM aus 
   Text 10,120,"VRAM used: "+(TotalVidMem()-AvailVidMem())/(1024.0^2) 

   Flip 0 
Until KeyHit(1) 

;free dream Blur 
   freedreamblur() 

;Load Dreamview 
   Createdreamview(Cam,0,255,255,0) 


   g#=0.001 
Repeat 

   If KeyDown(200) Then TurnEntity cam,1,0,0 
   If KeyDown(208) Then TurnEntity cam,-1,0,0 
   If KeyDown(203) Then TurnEntity cam,0,1,0 
   If KeyDown(205) Then TurnEntity cam,0,-1,0 
   dream#=dream#+g# 
   If dream> 1.2 Then g#=-0.001 
   If dream <0.4 Then g# = 0.001 
    
    
   RenderWorld 
   Updatedreamview(dream) 
    
   update_FPS(10) 

   Text 10,60,"FPS: " +Int(FPS#) 
    
   Text 0,0,"Dreamshiningfilter by Jan Kuhnert." 

   Text 0,40,"Use the cursor keys to turn the camera" 
    
   Text GraphicsWidth()/2,GraphicsHeight()/2,"Visit www.FX-Visions.de",1,1 
    

       
   ;gibt die Gerenderten Polygon aus, muss später entfernt werden! 
   Text 10,80,"Polygon: "+TrisRendered() 
          
   ;Gibt die Aktivierten Texturen zurück 
   Text 10,100,"Textures used: "+ActiveTextures () 
          
   ;Gibt den verwendeten VRAM aus 
   Text 10,120,"VRAM used: "+(TotalVidMem()-AvailVidMem())/(1024.0^2) 
    
   Flip 0 
Until KeyHit(1) 

;Free dreamview 
   freedreamview() 

End ; Me.close 


;Blur.bb 
Global blur_image, blur_texture, blur_gw, blur_gh,Blur_W,Blur_H,Blur_R,Blur_g,Blur_B 

Function CreateBlur(BlurCam,r=255,g=255,b=255) 
   Local sf,spr,w#,h#,BildschirmVerhaeltnis# 
    
   Blur_r=r 
   Blur_g=g 
   Blur_b=b 
    
   blur_gw = GraphicsWidth() 
   blur_gh = GraphicsHeight() 
    
   spr = CreateMesh(BlurCam) 
   sf = CreateSurface(spr) 
   AddVertex sf, -1, 1, 0, 0, 0 
   AddVertex sf, 1, 1, 0, 1, 0 
   AddVertex sf, -1, -1, 0, 0, 1 
   AddVertex sf, 1, -1, 0, 1, 1 
   AddTriangle sf, 0, 1, 2 
   AddTriangle sf, 3, 2, 1 
    
   EntityFX spr, 1+8 
   PositionEntity spr,0,0,4.0 
    
   EntityOrder spr, -(2^30) 
   EntityBlend spr, 1 
   blur_image = spr 
    
   blur_texture = CreateTexture(blur_gw, blur_gh, 1+256) 
   Blur_W=TextureWidth(blur_texture) 
   Blur_H=TextureHeight(blur_texture) 
    
   w#=Blur_W - blur_gw 
   h#=Blur_h - blur_gh 
    
   BildschirmVerhaeltnis#= Float(blur_gh)/Float(blur_gw)*4.0 
    
   ScaleEntity Spr,4.0 + (4.0*w#/Float(blur_gw)),BildschirmVerhaeltnis#+ (BildschirmVerhaeltnis#*h#/Float(blur_gh)),1.0 
    
   Blur_W=(w#/2.0)-0.75 
   Blur_h=(h#/2.0)-0.5 
    
   EntityTexture spr, blur_texture 
End Function 

Function UpdateBlur(power#) 
   If power >0.94 Then power = 0.94 
   EntityAlpha blur_image, power# 
   EntityColor Blur_image,Blur_R,Blur_g,Blur_b 
   CopyRect  0, 0, Blur_GW, Blur_gh, Blur_W, Blur_h, BackBuffer(), TextureBuffer(blur_texture)    
End Function 

Function FreeBlur() 
   FreeEntity blur_image 
   FreeTexture blur_texture 
End Function 


;Dreamblur.bb 
Global Dream_image, Dream_texture, Dream_gw, Dream_gh,Dream_W,Dream_H,DREAM_r,DREAM_g,DREAM_b 

Function CreateDreamBlur(DreamCam,r=255,g=255,b=255) 
   Local sf,spr,w#,h#,BildschirmVerhaeltnis# 
    
   DREAM_r=R 
   DREAM_g=g 
   DREAM_b=b 
    
   Dream_gw = GraphicsWidth() 
   Dream_gh = GraphicsHeight() 
    
   spr = CreateMesh(DreamCam) 
   sf = CreateSurface(spr) 
   AddVertex sf, -1, 1, 0, 0, 0 
   AddVertex sf, 1, 1, 0, 1, 0 
   AddVertex sf, -1, -1, 0, 0, 1 
   AddVertex sf, 1, -1, 0, 1, 1 
   AddTriangle sf, 0, 1, 2 
   AddTriangle sf, 3, 2, 1 
    
   EntityFX spr, 1+8 
   PositionEntity spr,0,0,4.0 
    
   EntityOrder spr, -(2^30)-1 
   EntityBlend spr, 3 
   Dream_image = spr 
    
   Dream_texture = CreateTexture(Dream_gw, Dream_gh, 1+256) 
   Dream_W=TextureWidth(Dream_texture) 
   Dream_H=TextureHeight(Dream_texture) 
    
   w#=Dream_W - Dream_gw 
   h#=Dream_h - Dream_gh 
    
   BildschirmVerhaeltnis#= Float(Dream_gh)/Float(Dream_gw)*4.0 
    
   ScaleEntity Spr,4.0 + (4.0*w#/Float(Dream_gw)),BildschirmVerhaeltnis#+ (BildschirmVerhaeltnis#*h#/Float(Dream_gh)),1.0 
    
   Dream_W=(w#/2.0)-0.75 
   Dream_h=(h#/2.0)-0.5 
    
   EntityTexture spr, Dream_texture 
End Function 

Function UpdateDreamBlur(power#) 
   If power >0.98 Then power = 0.98 
   EntityAlpha Dream_image, power# 
   EntityColor Dream_image,DREAM_r*power#,DREAM_g*power#,DREAM_b*power# 
   CopyRect  0, 0, Dream_GW-1, Dream_gh-1, Dream_W, Dream_h, BackBuffer(), TextureBuffer(Dream_texture)    
End Function 

Function FreeDreamBlur() 
   FreeEntity Dream_image 
   FreeTexture Dream_texture 
End Function 


;Dreamview.bb 
Global dreamview_image, dreamview_texture, dreamview_gw, dreamview_gh,dreamview_W,dreamview_H,dreamview_Cam 
Global dreamview_R,dreamview_G,dreamview_B 

Function Createdreamview(dreamviewCam,R=255,G=255,B=255,Extreme=0) 
   Local sf,spr,w#,h#,BildschirmVerhaeltnis# 
    
   dreamview_R=R 
   dreamview_G=G 
   dreamview_B=B 
    
   dreamview_Cam=dreamviewCam 
    
   dreamview_gw = GraphicsWidth() 
   dreamview_gh = GraphicsHeight() 
    
   spr = CreateMesh(dreamviewCam) 
   sf = CreateSurface(spr) 
   AddVertex sf, -1, 1, 0, 0, 0 
   AddVertex sf, 1, 1, 0, 1, 0 
   AddVertex sf, -1, -1, 0, 0, 1 
   AddVertex sf, 1, -1, 0, 1, 1 
   AddTriangle sf, 0, 1, 2 
   AddTriangle sf, 3, 2, 1 
    
   EntityFX spr, 1+8 

    
   EntityOrder spr, -(2^30)-2 
   EntityBlend spr, 1 
   If extreme Then EntityBlend spr, 3 
   dreamview_image = spr 
    
   dreamview_texture = CreateTexture(dreamview_gw, dreamview_gh, 1+256) 
   dreamview_W=TextureWidth(dreamview_texture) 
   dreamview_H=TextureHeight(dreamview_texture) 
    
   w#=dreamview_W - dreamview_gw 
   h#=dreamview_h - dreamview_gh 
    
   BildschirmVerhaeltnis#= Float(dreamview_gh)/Float(dreamview_gw)*4.0 
    
   ScaleEntity Spr,4.0 + (4.0*w#/Float(dreamview_gw)),BildschirmVerhaeltnis#+ (BildschirmVerhaeltnis#*h#/Float(dreamview_gh)),1.0 
    
   dreamview_W=(w#/2.0)-0.75 
   dreamview_h=(h#/2.0)-0.5 
    
   EntityTexture spr, dreamview_texture 
End Function 

Function Updatedreamview(power#) 
   Local opower# 
   opower#=power# 
   If opower#>1Then opower#=1 
   power#=power#*1.3 
   If power >1.3 Then power = 1.3 
   PositionEntity dreamview_image,0,0,4.0-power#/2,0 
   EntityAlpha dreamview_image, (power#/3.0)*2.0 
   EntityColor dreamview_image,dreamview_R*opower#,dreamview_G*opower#,dreamview_B*opower# 
   ScaleEntity dreamview_Cam,1+Power#,1+power,1 
   CopyRect  0, 0, dreamview_GW-1, dreamview_gh-1, dreamview_W, dreamview_h, BackBuffer(), TextureBuffer(dreamview_texture)    
End Function 

Function Freedreamview() 
   FreeEntity dreamview_image 
   FreeTexture dreamview_texture 
   ScaleEntity dreamview_cam,1,1,1 
End Function 


;FPS.bb 
Global FPS_zahler,FPS_Start_Time,FPS# 

Function update_FPS(checktime%=10) 
    
   Local Time,Righttime 
    
   FPS_Zahler = FPS_Zahler + 1 
    
   Time=MilliSecs() 
    
   If Time > FPS_START_TIME + checktime% 
    
      Righttime      = Time - FPS_START_TIME 
      FPS_START_TIME   = Time 
      FPS#         = (Float(FPS_Zahler)/Float(Righttime))*1000.0 
      FPS_Zahler      = 0 
                
   EndIf 

End Function
