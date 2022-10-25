; ID: 2802
; Author: jfk EO-11110
; Date: 2010-12-26 19:34:48
; Title: Turn Bilinear Filtering off
; Description: title says it all

; propertiy for DX7_SetTextureStageState
Const     D3DTSS_MAGFILTER      = 16;, /* D3DTEXTUREMAGFILTER filter To use For magnification */
Const     D3DTSS_MINFILTER      = 17;, /* D3DTEXTUREMINFILTER filter To use For minification */
Const     D3DTSS_MIPFILTER      = 18;, /* D3DTEXTUREMIPFILTER filter To use between mipmaps during minification */

;For other things you can do with DX7_SetTextureStageState see any DirectX documention, that is part of the SDK (actually, DX7 and DX8 SDK Docs seem to be ok for this command)
; Constants shoud be listed in the docs, alternatively they are found in d3dtypes.h (aslo part of SDK), with a short description at the line end (as seen here).

;parameters for D3DTEXTUREMAGFILTER
Const    D3DTFG_POINT        = 1;,    // nearest
Const    D3DTFG_LINEAR       = 2;,    // linear interpolation (blitz3d default)
Const    D3DTFG_FLATCUBIC    = 3;,    // cubic
Const    D3DTFG_GAUSSIANCUBIC = 4;,   // different cubic kernel
Const    D3DTFG_ANISOTROPIC  = 5;,    //
Const    D3DTFG_FORCE_DWORD  = $7fffffff;,   // force 32-bit size enum

;parameters for D3DTEXTUREMINFILTER
Const    D3DTFN_POINT        = 1;,    // nearest
Const    D3DTFN_LINEAR       = 2;,    // linear interpolation  (blitz3d default)
Const    D3DTFN_ANISOTROPIC  = 3;,    //
Const    D3DTFN_FORCE_DWORD  = $7fffffff;,   // force 32-bit size enum

;parameters for D3DTEXTUREMIPFILTER
Const    D3DTFP_NONE         = 1;,    // mipmapping disabled (use MAG filter)
Const    D3DTFP_POINT        = 2;,    // nearest
Const    D3DTFP_LINEAR       = 3;,    // linear interpolation  (blitz3d default afaik)
Const    D3DTFP_FORCE_DWORD  = $7fffffff;,   // force 32-bit size enum




Graphics3D 800,600,32,2
SetBuffer BackBuffer()


light=CreateLight()
RotateEntity light,45,45,0
cube=CreateCube()


w=256
tex1=CreateTexture(w,w)
For i=0 To 100000
 Color Rand(255),Rand(255),Rand(255)
 Plot Rand(0,255),Rand(0,255)
Next
EntityTexture cube,tex1



; init DC's extended DX7 wrapper
If DX7_SetSystemProperties(SystemProperty("Direct3D7"), SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"), SystemProperty("AppHWND"), SystemProperty("AppHINSTANCE")) Then RuntimeError "Error initializing dx7."

;DX7_SetTextureStageState%(n,D3DTSS_MINFILTER, D3DTFN_POINT) ; may be useful too
;DX7_SetTextureStageState%(n,D3DTSS_MIPFILTER, D3DTFP_NONE)  ; may be useful too

 DX7_SetTextureStageState%(0,D3DTSS_MAGFILTER, D3DTFG_POINT)  ; turns off bilinear filtering on magnified texels (close to camera texels)
;DX7_SetTextureStageState%(0,D3DTSS_MAGFILTER, D3DTFG_LINEAR) ; restore Blitz3D default setting


CopyRect 0,0,256,256,0,0,BackBuffer(), TextureBuffer(tex1)


camera=CreateCamera()
CameraRange camera,0.001,50
TranslateEntity camera,0,0,-1.5



While KeyHit(1)=0
 TurnEntity cube,0,.1,0
 RenderWorld()
 Flip
 Delay 10
Wend


; de-init DC's extended DX7 wrapper (guess you should not forget that)
DX7_RemoveSystemProperties()


End
