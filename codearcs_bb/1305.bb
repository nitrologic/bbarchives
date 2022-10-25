; ID: 1305
; Author: jfk EO-11110
; Date: 2005-02-28 23:16:35
; Title: Fast Image Resize
; Description: Utilizes 3D Renderer to resample 2D Images. 108 times faster than ResizeImage

; yet another QUICK SCALE
; An example on how to use 3D Hardware to speed up scaling of 2D images.
; written by jfk of csp

; results: scaling a 1024*768 image to half the size was 
; 109 times faster than ResizeImage() with TFormFiter on
; 36 times faster than ResizeImage() with TFormFiter off

; There are some limits with this method:
; images may not be bigger than 1024*1024 prior scaling.
; images may not be bigger than the current Graphics Resolution after scaling.
; Resampling looks best when in 1024*768 screen resolution



Graphics3D 1024,768,32,2
;Graphics3D 800,600,32,2
;Graphics3D 640,480,32,2
SetBuffer BackBuffer()

; init myResizeImage(img,w,h)
Global myResizeCamera=CreateCamera() 
CameraProjMode myResizeCamera,0
Global myResizeQuad=CreateQuad()
Global myResizeTex=CreateTexture(1024,1024,256 Or 16 Or 32)
EntityTexture myResizeQuad,myResizeTex
EntityFX myResizeQuad,1
CameraRange myResizeCamera,.001,10
TranslateEntity myResizeCamera,(1.0/1024.0),-(1.0/1024.0),-1.0
EntityParent myResizeQuad,myResizeCamera,1
PositionEntity myResizeCamera,32000,16000,16000
; end of init myResizeImage(img,w,h)




;-------------------------- Demo:-------------------------------

TFormFilter 1
;test speed of blitzs built in scaling
img=LoadImage("testbmp.bmp") ; a test image, max 1024 * 1024 !

t1=MilliSecs()
ResizeImage img,ImageWidth(img)/2,ImageHeight(img)/2
t2=MilliSecs()
Cls:DrawBlock img,0,0
Text 0,580,"TFormFiler on, Blitz Scaling: "+(t2-t1)+" ms"
Flip()
WaitKey()
FreeImage img

;---------------------------------------------------------------

TFormFilter 0
;test speed of blitzs built in scaling (now with Filter off)
img=LoadImage("testbmp.bmp") 

t1=MilliSecs()
ResizeImage img,ImageWidth(img)/2,ImageHeight(img)/2
t2=MilliSecs()
Cls:DrawBlock img,0,0
Text 0,580,"TFormFiler off, Blitz Scaling: "+(t2-t1)+" ms"
Flip()
WaitKey()
FreeImage img

;---------------------------------------------------------------

; now test speed of faster scaling utilizing 3D Hardware
img=LoadImage("testbmp.bmp") 

t1=MilliSecs()
img=myResizeImage(img,ImageWidth(img)/2,ImageHeight(img)/2)
t2=MilliSecs()
Cls:DrawBlock img,0,0
Text 0,580,"3D Image Scaling: "+(t2-t1)+" ms"
Flip
WaitKey()




End





Function myResizeImage(img,w#,h#,cam0=0)
; img-handle, desired width and height, optional camera handle that may be deactivated during rescaling
; note: width and height may not be bigger than current Graphics resolution, or they will be clipped!
 img_w#=ImageWidth(img)
 img_h#=ImageHeight(img)
 If img_w>1024 Then img_w=1024
 If img_h>1024 Then img_h=1024
 If img_w<1 Then img_w=1
 If img_h<1 Then img_h=1

 If w>1024 Then w=1024
 If h>1024 Then h=1024
 If w<1 Then w=1
 If h<1 Then h=1

 w_rel#=w#/img_w#
 h_rel#=h#/img_h#
 g_rel#=1024.0/GraphicsWidth()

 CopyRect 0,0,img_w,img_h,512-(img_w/2.0),512-(img_h/2.0),ImageBuffer(img),TextureBuffer(myResizeTex)
 ScaleEntity myResizeQuad,w_rel*g_rel,h_rel*g_rel,0.0001
 If cam0<>0 Then CameraProjMode cam0,0
 CameraProjMode myResizeCamera,1
 RenderWorld()
 CameraProjMode myResizeCamera,0
 If cam0<>0 Then CameraProjMode cam0,1

 img2=CreateImage(w,h)
 CopyRect (GraphicsWidth()/2.0)-(w/2.0),(GraphicsHeight()/2.0)-(h/2.0),w,h,0,0,BackBuffer(),ImageBuffer(img2)
 FreeImage img
 Return img2
End Function



Function CreateQuad()
  ; creates a quad, facing to the -Z side
  mesh=CreateMesh()
  surf=CreateSurface(mesh)
  v0=AddVertex(surf, -1.0,   1.0,0, 0,0 )
  v1=AddVertex(surf,  1.0,   1.0,0, 1,0 )
  v2=AddVertex(surf,  1.0,  -1.0,0, 1,1 )
  v3=AddVertex(surf, -1.0,  -1.0,0, 0,1 )
  AddTriangle(surf,v0,v1,v2)
  AddTriangle(surf,v0,v2,v3)
  UpdateNormals mesh
  Return mesh
End Function
