; ID: 3276
; Author: Alex. L.
; Date: 2016-06-18 08:27:24
; Title: Xilvan Design's Sprite Lib
; Description: Two new librairies for 2D Sprites

Const Max_Spr = 16384

Global iSprite_ = -1

Dim Sprite_(Max_Spr)
Dim Sprite_Tex_(Max_Spr)
Dim Sprite_(Max_Spr)
Dim Sprite_Blend_(Max_Spr)
Dim Sprite_piv(Max_Spr)
Dim Sprite_Order_(Max_Spr)
Dim Spr_x#(Max_Spr)
Dim Spr_y#(Max_Spr)
Dim Spr_Angle(Max_Spr)
Dim Spr_Dir(Max_Spr)
Dim Spr_dx(Max_Spr)
Dim Spr_t(Max_Spr)
Dim Spr_tx#(Max_Spr)
Dim Spr_ty#(Max_Spr)
Dim Spr_bAnim(Max_Spr)
Dim Spr_Path$(Max_Spr)
Dim Spr_face(Max_Spr)
Dim Tex_Filter#(Max_Spr)
Dim Sprite_a#(Max_Spr)
Dim Spr_(Max_Spr)

Global Sprite2_
Global BaseTuiles_
Global SprPack = LoadMesh("Data\SprMesh.b3d")
Global Path$
Global iil#

iil# = 1.57657

Const Max_P_ = 2600
Dim P_(Max_P_)

Global iPart_ = 0
Global dimResize# = 640.0/Float(gx)


P_(0) = LoadAnimImage_("Images\Tuiles\Blank.bmp",32,32,0,1,11)

For i = 1 To Max_P_ - 1

	P_(i) = CopyEntity_(P_(0))

	PositionEntity Sprite_(P_(i)),-2048,-2048,1.01
	HideEntity Sprite_(P_(i))

Next


HideEntity Sprite_(P_(0))


Function LoadAnimImage_(Sprite$,tttx#,ttty#,dx,taille,flag_=1,texture_=0)

	iSprite_ = iSprite_ + 1
	
	Path$ = Sprite$
	Sprite_(iSprite_) = CopyMesh(SprPack)
	Spr_Path$(iSprite_) = Sprite$
	Sprite_Tex_(iSprite_) = LoadAnimTexture(Sprite$,flag_,Int(tttx#),Int(ttty#),dx,taille)

	EntityTexture Sprite_(iSprite_),Sprite_Tex_(iSprite_)
	
	texture_ = Sprite_Tex_(iSprite_)
	Tex_Filter(iSprite_) = Tex_Filter(theSprite_)
	
	Sprite_Blend_(iSprite_) = 1

	TextureBlend Sprite_Tex_(iSprite_),2
	TextureFilter Sprite_Tex_(iSprite_),Tex_Filter(iSprite_)
	
	EntityBlend_ iSprite_,Sprite_Blend_(iSprite_)
	EntityParent Sprite_(iSprite_),parent_
	EntityFX Sprite_(iSprite_),1
	
	Spr_tx#(iSprite_) = tttx#
	Spr_ty#(iSprite_) = ttty#
	
	Tex_Filter(iSprite_) = flag_
	ScaleEntity Sprite_(iSprite_),(Spr_tx#(iSprite_)/131.0)*dimResize#,(Spr_ty#(iSprite_)/128.9)*dimResize#,1

	Spr_bAnim(iSprite_) = True
	EntityAlpha_ iSprite_,0
	EntityOrder_ iSprite_,-1024
	PositionEntity Sprite_(iSprite_),-2048,-2048,1.01
	HideEntity Sprite_(iSprite_)
	Return iSprite_
	
End Function


Function LoadImage_(Sprite$,tttx#=256,ttty#=256,flag_=4,flag2 = 2)

	iSprite_ = iSprite_ + 1
	
	Sprite_(iSprite_) = CopyMesh(SprPack)
	Path$ = Sprite$
	Spr_Path$(iSprite_) = Sprite$
	Sprite_Tex_(iSprite_) = LoadTexture(Sprite$,flag_)
	EntityTexture Sprite_(iSprite_),Sprite_Tex_(iSprite_)

	Tex_Filter(iSprite_) = Tex_Filter(theSprite_)
	
	Sprite_Blend_(iSprite_) = 1
	TextureBlend Sprite_Tex_(iSprite_),flag2
	TextureFilter Sprite_Tex_(iSprite_),Tex_Filter(iSprite_)
	
	EntityBlend_ iSprite_,Sprite_Blend_(iSprite_)
	EntityParent Sprite_(iSprite_),parent_
	EntityFX Sprite_(iSprite_),1
	
	Spr_tx#(iSprite_) = tttx#
	Spr_ty#(iSprite_) = ttty#
	
	ScaleEntity Sprite_(iSprite_),(Spr_tx#(iSprite_)/131.0)*dimResize#,(Spr_ty#(iSprite_)/128.8)*dimResize#,1
	Tex_Filter(iSprite_) = flag_
	Spr_bAnim(iSprite_) = False
	EntityOrder_ iSprite_,-1024
	EntityBlend_ iSprite,Sprite_Blend_(theSprite_)
	PositionEntity Sprite_(iSprite_),-2048,-2048,1.01
	HideEntity Sprite_(iSprite_)
	Return iSprite_

End Function


Function CopyEntity_(theSprite_,parent_=0)

	iSprite_ = iSprite_ + 1

	Path$ = Spr_Path$(theSprite_)
	Spr_Path$(iSprite_) = Spr_Path$(theSprite_)
	Sprite_Tex_(iSprite_) = Sprite_Tex_(theSprite_)
	Sprite_(iSprite_) = CopyMesh(SprPack)
	Sprite_Blend_(iSprite_) = Sprite_Blend_(theSprite_)
	Tex_Filter(iSprite_) = Tex_Filter(theSprite_)
	
	Sprite_Blend_(iSprite_) = 1

	TextureBlend Sprite_Tex_(iSprite_),2
	TextureFilter Sprite_Tex_(iSprite_),Tex_Filter(iSprite_)
	
	EntityBlend_ iSprite_,Sprite_Blend_(iSprite_)
	
	EntityTexture Sprite_(iSprite_),Sprite_Tex_(iSprite_)
	EntityParent Sprite_(iSprite_),parent_
	EntityFX Sprite_(iSprite_),1
	
	Spr_tx#(iSprite_) = Spr_tx#(theSprite_)
	Spr_ty#(iSprite_) = Spr_ty#(theSprite_)
	
	Spr_bAnim(iSprite_) = Spr_bAnim(theSprite_)
	ScaleEntity Sprite_(iSprite_),(Spr_tx#(iSprite_)/131.0)*dimResize#,(Spr_ty#(iSprite_)/127.8)*dimResize#,1
	EntityOrder_ iSprite_,-1024
	PositionEntity Sprite_(iSprite_),-2048,-2048,1.01
	HideEntity Sprite_(iSprite_)
	Return iSprite_

End Function


Function EntityBlend_(theSprite_,blend2_)

	Sprite_Blend_(theSprite_) = blend2_

	EntityBlend Sprite_(theSprite_),blend2_

End Function


Function ScaleSprite_(theSprite_,ScaleX#,ScaleY#)

	ScaleEntity Sprite_(theSprite_),(ScaleX#/131.0)*dimResize#,(ScaleY#/127.9)*dimResize#,1.01

End Function


Function ScaleSprite2_(theSprite_,ScaleX#,ScaleY#)

	ScaleEntity Sprite_(theSprite_),(ScaleX#/138.0)*dimResize#,(ScaleY#/138.0)*dimResize#,1.01

End Function


Function RotateSprite_(theSprite_,angle_,Cam=0)

	Sprite_Dir = EntityRoll(Sprite_(theSprite_))

	RotateEntity Sprite_(theSprite_),EntityPitch(Came),EntityYaw(Came),angle_

End Function


Function RotateEntity_(theSprite_,ax#,ay#,az#)

	RotateEntity Sprite_(theSprite_),ax#,ay#,az#

End Function


Function TurnSprite_(theSprite_,angle_)

	TurnEntity Sprite_(theSprite_),EntityPitch(Came),EntityYaw(Came),angle_

	Sprite_Dir = EntityRoll(Sprite_(theSprite_))

End Function


Function TurnEntity_(theSprite_,dx#,dy#,dz#)

	TurnEntity Sprite_(theSprite_),dx#,dy#,dz#

End Function


Function SpriteViewMode_(theSprite_,viewmode_)		

	If theSprite_ < Max_Spr Then

		Spr_face(theSprite_) = ((viewmode_ = Sprite_(theSprite_)))

	End If

End Function


Function DrawImage_(theSprite_,x,y,anim=-1)

	;If KeyDown(200) Then iil# = iil# - 0.00001 : Delay 10
	;If KeyDown(208) Then iil# = iil# + 0.00001 : Delay 10
	
	PositionEntity Sprite_(theSprite_),(((Float(x+DecalX)*0.005)/iil#)*dimResize#)-1,(1.20-((Float(y+DecalY)*0.005)/iil#)*dimResize#)-0.3,1.01
	Spr_x#(theSprite_) = x
	Spr_y#(theSprite_) = y

	If anim > -1 Then

		EntityTexture Sprite_(theSprite_),Sprite_Tex_(theSprite_),anim,0

	End If

	ShowEntity Sprite_(theSprite_)

End Function


Function DrawImage2_(theSprite_,x,y,anim=-1)

	If KeyDown(200) Then iil# = iil# - 0.00001 : Delay 10
	If KeyDown(208) Then iil# = iil# + 0.00001 : Delay 10
	
	PositionEntity Sprite_(theSprite_),(((Float(x+DecalX)*0.0047)/iil#)*dimResize#)-1,(1.20-((Float(y+DecalY)*0.0047)/iil#)*dimResize#)-0.3,1.01

	Spr_x#(theSprite_) = x
	Spr_y#(theSprite_) = y

	If anim > -1 Then

		EntityTexture Sprite_(theSprite_),Sprite_Tex_(theSprite_),anim,0

	End If

	ShowEntity Sprite_(theSprite_)

End Function


Function AnimSprite_(theSprite_,Anim = 0)

	DrawImage_ theSprite_,Spr_x#(theSprite_),Spr_y#(theSprite_),Anim

End Function

Function DrawTexture_(theSprite_,SpriteTex_,x,y,tx,ty,r=255,g=255,b=255)

	EntityTexture_ theSprite_,SpriteTex_
	Sprite_Tex_(theSprite_) = SpriteTex_
	DrawImage_ theSprite_,x,y
	EntityColor_ theSprite_,r,g,b
	EntityBlend_ theSprite_,1
	ScaleSprite_ theSprite_,tx,ty
	EntityAlpha_ theSprite_,1
	Spr_bAnim(theSprite_) = True

	ShowEntity Sprite_(theSprite_)

End Function

Function DrawAnimTexture_(theSprite_,SpriteTex_,x,y,tx,ty,Anim=0,r=255,g=255,b=255)

	EntityTexture_ theSprite_,SpriteTex_,Anim
	Sprite_Tex_(theSprite_) = SpriteTex_
	DrawImage_ theSprite_,x,y,Anim
	EntityColor_ theSprite_,r,g,b
	EntityBlend_ theSprite_,1
	ScaleSprite_ theSprite_,tx,ty
	EntityAlpha_ theSprite_,1
	Spr_bAnim(theSprite_) = True

	ShowEntity Sprite_(theSprite_)

End Function

Function EntityTexture_(theSprite_,SpriteTex_,anim=0,id=0)

	EntityTexture Sprite_(theSprite_),SpriteTex_,anim,id

	Spr_bAnim(theSprite_) = True

End Function


Function MidHandle_(Sprite)

	;

End Function


Function GetTexture_(theSprite_)

	Local brush=GetEntityBrush(Sprite_(theSprite_))
	Local tex=GetBrushTexture(brush)

	Return tex

End Function


Function EntityOrder_(theSprite_,order_=0)

	EntityOrder Sprite_(theSprite_),order_

End Function


Function EntityAlpha_(theSprite_,alpha#=0.0)

	EntityAlpha Sprite_(theSprite_),alpha#

End Function


Function EntityColor_(theSprite_,cr,cg,cb)

	EntityColor Sprite_(theSprite_),cr,cg,cb

End Function


Function EntityFX_(theSprite_,fx_)

	EntityFX Sprite_(theSprite_),fx_

End Function


Function MoveSprite_(theSprite_,dx#,dy#,dz#)

	MoveEntity Sprite_(theSprite_),dx#,dy#,dz#

End Function


Function TranslateSprite_(theSprite_,dx#,dy#,dz#)

	TranslateEntity Sprite_(theSprite_),dx#,dy#,dz#

End Function


Function EntityParent_(theSprite_,parent_)

	EntityParent Sprite_(theSprite_),parent_ 

End Function


Function TextureBlend_(theSprite_,value_)

	TextureBlend Sprite_Tex_(theSprite_),value_

End Function


Function HideEntity_(TheSprite_)

	HideEntity Sprite_(TheSprite_)

End Function


Function ShowEntity_(TheSprite_)

	ShowEntity Sprite_(TheSprite_)

End Function
