; ID: 1795
; Author: tonyg
; Date: 2006-08-24 12:19:22
; Title: DX UV wrap
; Description: DX UV wrap code.

Strict
Graphics 800,600
Cls
Function tg_setuv(image:TImage,u0#,v0#,u1#,v1#,frame:Int=0)
  TD3D7ImageFrame(image.frame(frame)).SetUV(u0#,v0#,u1#,v1#)
End Function
Function tg_resetuv(image:TImage,frame:Int=0)
    TD3D7ImageFrame(image.frame(frame)).SetUV(0.0,0.0,1.0,1.0)
End Function
Local base:Timage = LoadImage("max.png")
Local u0#			= 0
Local v0#			= 0
Local u1#			= 0.5
Local v1#			= 1
Local frame:Byte	 = 0
Local wrap_u0# = 0.0
Local wrap_v0#			= 0.0
Local wrap_u1#			= 2.0
Local wrap_v1#			= 2.0
While Not KeyHit(KEY_ESCAPE)
	Cls
	tg_SetUV(base,u0#,v0#,u1#,v1#)
	DrawImage(base,0,0)
	setwrap()
	tg_SETUV(base,wrap_u0#,wrap_v0#,wrap_u1#,wrap_v1#)
	DrawImage base , 400 , 0
	tg_resetuv(base)
	setnowrap()
	DrawImage base , 0 , 200
	Flip
Wend
Function setwrap()
	PrimaryDevice.device.SetTextureStageState( 0, D3DTSS_ADDRESS,	D3DTADDRESS_WRAP);
End Function
Function setnowrap()
	PrimaryDevice.device.SetTextureStageState( 0 , D3DTSS_ADDRESS , 	D3DTADDRESS_CLAMP) ; 
End Function
