; ID: 1053
; Author: D4NM4N
; Date: 2004-05-28 09:16:12
; Title: B3d loader for cartography shop
; Description: b3d loader

;B3D DATA LOADER By DanmaN, for use with CS4.1 

;LIST OF CShop KEYS: UPPER OR LOWERCASE (it gets converted anyway)
;KEY			VALUE			PURPOSE
;------------------------------------------------------------------------------------------------------
;CLASSNAME		Light   		lights only, will recreate the lighting using CS4 rules
;range
;color
;etc

;LIST OF SPECIAL KEYS : (uc/lc case gets converted anyway)
;KEY			VALUE			PURPOSE
;-----------------------------------------------------------------------------------------------------
;NAME			any				any mesh or entity, just an easy way of finding the object in records 
;								later in your program
;ALPHA			#0-1			any mesh, transparancy 1-solid 0-invisible 0.5-ghost etc.


;note to get rid of text/visuals search for 'testcode'

;the usual
Graphics3D 800,600,16,1
SetBuffer BackBuffer()
AmbientLight 0,0,0

;def. object data structure
Global map
Type Obj_Data
	Field handler    		;entity handle
	Field Name$				;name as defined in cs4 with 'NAME   =   Value'
	Field key$[8]			;Data key
	Field value$[8]			;Data key value
	Field rawdata$[16]		;Only used for parsing can do something else in own program.
End Type


map=loadb3d("dans.b3d")

Print"Hit mouse To walk map...."
Repeat 
Until MouseHit(1)
walkmap()


End;



;-----------------------------------------------------------------------------------------------------
;####################################################################################################


Function loadb3d(filename$)


;load map & count children
;TextureFilter "",2
TextureFilter "MSK",4
TextureFilter "TRN",2
map=LoadAnimMesh(filename)
cc=CountChildren(map)


For ent=1 To cc;for each entity

	
	;get Handle & 'name' of child
	chi=GetChild (map,ent) 
	attr$=EntityName(chi)
	;create record & store handle
	If attr<>"" 
	
		;new record
		obj.obj_data = New obj_data
		obj\handler  = chi
		
		;string parser to delete unwanted chars & separate lines
		attr$=Replace$(attr$,Chr(34),"")
		attr$=Replace$(attr$,Chr(10),"#")
		currentstr=1
		For b=1 To Len(attr)
			If Mid(attr,b,1)="#" Then 
				currentstr=currentstr+1
			Else
				obj\rawdata[currentstr]=obj\rawdata[currentstr]+Mid(attr,b,1)
			EndIf
		Next	

		;string parser 2 divide into Data segs For fields
		For dat=1 To 8
			switch=0
			attr=obj\rawdata[dat]
			For b=1 To Len(attr)	
				
				If Mid(attr,b,1)="=" Then 
					switch=1
				Else
					If switch=0 Then obj\key[dat]   = obj\key[dat]   + Mid(attr,b,1)
					If switch=1 Then obj\value[dat] = obj\value[dat] + Mid(attr,b,1)
				EndIf
			Next	
			;extract real name value using the CS4 key 'NAME'
			If Upper(obj\key[dat])="NAME" 
				obj\name = Upper(obj\value[dat])
				
			EndIf	
		Next			

		;reset raw Data
		For dat=1 To 16
			obj\rawdata[dat]=""
		Next

		;TESTCODE - remove this ################################
		Print "-------------------------------------------------"
		Print "NAME: "+obj\name+"     Handle:"+Str(obj\handler)
		For dat=1 To 8
			Print "DATA: "+obj\key[dat]+"<=>"+obj\value[dat]
		Next	
		Delay(100)
		;########################################################

		;alpha
		For a=1 To 8
			If 	Upper(obj\key[a])="ALPHA" Then EntityAlpha obj\handler,Float(obj\value[a])		
		Next
		;create lights
		If Upper(obj\key[1])="CLASSNAME" And Upper(obj\Value[1])="LIGHT"
			lh=CreateLight(2)
			;testcode - remove this ################
			lb=CreateSphere(10):EntityFX lb,1
			;#######################################			
			For a=1 To 8
				PositionEntity lh,EntityX(obj\handler),EntityY(obj\handler),EntityZ(obj\handler)
				;testcode - remove this ###############
				PositionEntity lb,EntityX(obj\handler),EntityY(obj\handler),EntityZ(obj\handler)
				;#######################################			
				obj\handler=lh
				If Upper(obj\key[a])="RANGE"
					LightRange lh, Float(obj\value[a])
				EndIf
				If Upper(obj\key[a])="COLOR"
					;parse colors
					cR$="":cG$="":cB$="":switch=1
					For ch=1 To Len(obj\value[a])
						If Mid (obj\value[a],ch,1)=" "
							switch=switch+1
						Else
							If switch=1 Then cR=cR+Mid (obj\value[a],ch,1)
							If switch=2 Then cg=cg+Mid (obj\value[a],ch,1)
							If switch=3 Then cb=cb+Mid (obj\value[a],ch,1)
						EndIf
					Next
					
					LightColor lh, Float(cr),Float(cg),Float(cb)
					;testcode - remove this ################
					EntityColor lb, Float(cr),Float(cg),Float(cb)
					;#######################################			
				EndIf
		
				
			Next
		EndIf
		
	EndIf
	
	;Set Surfaces For receiving hardware lights
	nv=CountSurfaces(chi)
	EntityFX chi,0
	Print nv:Delay(100)

	If nv>0 Then 
		surf=GetSurface (chi,1)
		brsh=GetSurfaceBrush(surf)
		BrushFX brsh,0
		tex=GetBrushTexture(brsh,1)
		TextureBlend tex,2 ;USE 0 to toggle LM on-off 2 TO ADD, USE 3 TO MULTIPLY
		BrushTexture brsh,tex,0,1
		PaintMesh chi,brsh
		FreeBrush brsh
		FreeTexture tex
		;PaintEntity chi,brsh
		
	EndIf
	UpdateNormals chi
		
	
Next;object

Return map
End Function


;####################################################################################################



Function walkmap()

cam=CreateCamera()
CameraRange cam,10,1000000
TurnEntity cam,0,35,0
sph=CreateSphere (10,cam)
MoveEntity sph,0,0,100
ScaleEntity sph,4,4,4


MoveEntity cam,0,200,-1500
;CameraZoom cam,1.5

l=CreateLight()
LightColor l,255,0,0
LightRange l,200

While Not KeyHit(1)
;RotateEntity map,180,MilliSecs()/100.0,0
RenderWorld
Flip
	Text 0,0,Str (EntityX(cam))+" "+Str (EntityY(cam))+" "+Str (EntityZ(cam))+"        "
	; primative Mouse look
	; ----------

	; Mouse x and y speed
	mxs=MouseXSpeed()
	mys=MouseYSpeed()
	
	
	TurnEntity cam,mys,-mxs,0
	RotateEntity  cam,EntityPitch(cam),EntityYaw(cam),0
	a=a+1:If a=200 Then a=0
	
	
cc=CountChildren(map)
	
	If a>100 Then 
			LightColor l,255,0,0 
			;ShowEntity map2
		Else
		 	LightColor l,0,0,0
			;HideEntity map2
	EndIf
	; Rest mouse position to centre of screen
	MoveMouse 320,200;
	
If MouseDown(1) MoveEntity cam,0,0,79
If MouseDown(2) MoveEntity cam,0,0,-79

Wend
End Function
