; ID: 952
; Author: Techlord
; Date: 2004-02-28 13:10:52
; Title: Project PLASMA FPS 2004: Gadget.bb
; Description: 3D Graphic User Interface Module

;============================
;BITMAPFONT MODULE
;============================
Const BITMAPFONT_MAX%=32
Dim bitmapfontId.bitmapfont(BITMAPFONT_MAX%)
Global bitmapfontIndex.stack=stackIndexCreate(BITMAPFONT_MAX%)

Type bitmapfont
	Field id%
	Field typeid%
	Field textureid%
	Field characterbank%
	Field scale.vector
	Field offsetwidthbank%
	Field offsetheightbank%
	Field spacing#
End Type

Function bitmapfontStop()
	For this.bitmapfont=Each bitmapfont
		bitmapfontDelete(this)
	Next
End Function

Function bitmapfontNew.bitmapfont()
	this.bitmapfont=New bitmapfont
	this\id%=0
	this\typeid%=0
	this\textureid%=0
	this\characterbank%=0
	this\scale.vector=vectorNew()
	this\offsetwidthbank%=0
	this\offsetheightbank%=0
	this\spacing#=0.0
	this\id%=StackPop(bitmapfontIndex.stack)
	bitmapfontId(this\id)=this
	Return this
End Function

Function bitmapfontDelete(this.bitmapfont)
	bitmapfontId(this\id)=Null
	StackPush(bitmapfontIndex.stack,this\id%)
	this\spacing#=0.0
	vectorDelete(this\scale.vector)
	Delete this
End Function

Function bitmapfontUpdate()
	For this.bitmapfont=Each bitmapfont
	Next
End Function

Function bitmapfontRead.bitmapfont(file)
	this.bitmapfont=New bitmapfont
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\textureid%=ReadInt(file)
	this\characterbank%=ReadInt(file)
	this\scale.vector=vectorRead(file)
	this\offsetwidthbank%=ReadInt(file)
	this\offsetheightbank%=ReadInt(file)
	this\spacing#=ReadFloat(file)
	Return this
End Function

Function bitmapfontWrite(file,this.bitmapfont)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	WriteInt(file,this\textureid%)
	WriteInt(file,this\characterbank%)
	vectorWrite(file,this\scale.vector)
	WriteInt(file,this\offsetwidthbank%)
	WriteInt(file,this\offsetheightbank%)
	WriteFloat(file,this\spacing#)
End Function

Function bitmapfontSave(filename$="Default")
	file=WriteFile(filename$+".bitmapfont")
	For this.bitmapfont= Each bitmapfont
		bitmapfontWrite(file,this)
	Next
	CloseFile(file)
End Function

Function bitmapfontOpen(filename$="Default")
	file=ReadFile(filename+".bitmapfont")
	Repeat
		bitmapfontRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function bitmapfontCopy.bitmapfont(this.bitmapfont)
	copy.bitmapfont=New bitmapfont
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\textureid%=this\textureid%
	copy\characterbank%=this\characterbank%
	copy\scale.vector=vectorCopy(this\scale.vector)
	copy\offsetwidthbank%=this\offsetwidthbank%
	copy\offsetheightbank%=this\offsetheightbank%
	copy\spacing#=this\spacing#
	Return copy
End Function

Function bitmapfontMimic(mimic.bitmapfont,this.bitmapfont)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\textureid%=this\textureid%
	mimic\characterbank%=this\characterbank%
	vectorMimic(mimic\scale.vector,this\scale.vector)
	mimic\offsetwidthbank%=this\offsetwidthbank%
	mimic\offsetheightbank%=this\offsetheightbank%
	mimic\spacing#=this\spacing#
End Function

Function bitmapfontCreate.bitmapfont(id%,typeid%,textureid%,characterbank%,scale.vector,offsetwidthbank%,offsetheightbank%,spacing#)
	this.bitmapfont=bitmapfontNew()
	this\id%=id%
	this\typeid%=typeid%
	this\textureid%=textureid%
	this\characterbank%=characterbank%
	this\scale.vector=scale.vector
	this\offsetwidthbank%=offsetwidthbank%
	this\offsetheightbank%=offsetheightbank%
	this\spacing#=spacing#
	Return this
End Function

Function bitmapfontSet(this.bitmapfont,id%,typeid%,textureid%,characterbank%,scale.vector,offsetwidthbank%,offsetheightbank%,spacing#)
	this\id%=id%
	this\typeid%=typeid%
	this\textureid%=textureid%
	this\characterbank%=characterbank%
	this\scale.vector=scale.vector
	this\offsetwidthbank%=offsetwidthbank%
	this\offsetheightbank%=offsetheightbank%
	this\spacing#=spacing#
End Function

;============================
;DESIGN MODULE
;============================
Const DESIGN_MAX%=128
Dim designId.design(DESIGN_MAX%)
Global designIndex.stack=stackIndexCreate(DESIGN_MAX%)

Type design
	Field id%
	Field typeid%
	Field color.color
	Field mask.color
	Field alpha#
	Field textureid%
	Field bitmapfontid%
	Field caption$
	Field captionalign.vector
	Field border3D%
	Field fx%
	Field action.action
End Type

Function designStop()
	For this.design=Each design
		designDelete(this)
	Next
End Function

Function designNew.design()
	this.design=New design
	this\id%=0
	this\typeid%=0
	this\color.color=colorNew()
	this\mask.color=colorNew()
	this\alpha#=0.0
	this\textureid%=0
	this\bitmapfontid%=0
	this\caption$=""
	this\captionalign.vector=vectorNew()
	this\border3D%=0
	this\fx%=0
	this\action.action=actionNew()
	this\id%=StackPop(designIndex.stack)
	designId(this\id)=this
	Return this
End Function

Function designDelete(this.design)
	designId(this\id)=Null
	StackPush(designIndex.stack,this\id%)
	actionDelete(this\action.action)
	vectorDelete(this\captionalign.vector)
	this\caption$=""
	this\alpha#=0.0
	colorDelete(this\mask.color)
	colorDelete(this\color.color)
	Delete this
End Function

Function designUpdate()
	For this.design=Each design
	Next
End Function

Function designRead.design(file)
	this.design=New design
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\color.color=colorRead(file)
	this\mask.color=colorRead(file)
	this\alpha#=ReadFloat(file)
	this\textureid%=ReadInt(file)
	this\bitmapfontid%=ReadInt(file)
	this\caption$=ReadLine(file)
	this\captionalign.vector=vectorRead(file)
	this\border3D%=ReadInt(file)
	this\fx%=ReadInt(file)
	this\action.action=actionRead(file)
	Return this
End Function

Function designWrite(file,this.design)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	colorWrite(file,this\color.color)
	colorWrite(file,this\mask.color)
	WriteFloat(file,this\alpha#)
	WriteInt(file,this\textureid%)
	WriteInt(file,this\bitmapfontid%)
	WriteLine(file,this\caption$)
	vectorWrite(file,this\captionalign.vector)
	WriteInt(file,this\border3D%)
	WriteInt(file,this\fx%)
	actionWrite(file,this\action.action)
End Function

Function designSave(filename$="Default")
	file=WriteFile(filename$+".design")
	For this.design= Each design
		designWrite(file,this)
	Next
	CloseFile(file)
End Function

Function designOpen(filename$="Default")
	file=ReadFile(filename+".design")
	Repeat
		designRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function designCopy.design(this.design)
	copy.design=New design
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\color.color=colorCopy(this\color.color)
	copy\mask.color=colorCopy(this\mask.color)
	copy\alpha#=this\alpha#
	copy\textureid%=this\textureid%
	copy\bitmapfontid%=this\bitmapfontid%
	copy\caption$=this\caption$
	copy\captionalign.vector=vectorCopy(this\captionalign.vector)
	copy\border3D%=this\border3D%
	copy\fx%=this\fx%
	copy\action.action=actionCopy(this\action.action)
	Return copy
End Function

Function designMimic(mimic.design,this.design)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	colorMimic(mimic\color.color,this\color.color)
	colorMimic(mimic\mask.color,this\mask.color)
	mimic\alpha#=this\alpha#
	mimic\textureid%=this\textureid%
	mimic\bitmapfontid%=this\bitmapfontid%
	mimic\caption$=this\caption$
	vectorMimic(mimic\captionalign.vector,this\captionalign.vector)
	mimic\border3D%=this\border3D%
	mimic\fx%=this\fx%
	actionMimic(mimic\action.action,this\action.action)
End Function

Function designCreate.design(id%,typeid%,color.color,mask.color,alpha#,textureid%,bitmapfontid%,caption$,captionalign.vector,border3D%,fx%,action.action)
	this.design=designNew()
	this\id%=id%
	this\typeid%=typeid%
	this\color.color=color.color
	this\mask.color=mask.color
	this\alpha#=alpha#
	this\textureid%=textureid%
	this\bitmapfontid%=bitmapfontid%
	this\caption$=caption$
	this\captionalign.vector=captionalign.vector
	this\border3D%=border3D%
	this\fx%=fx%
	this\action.action=action.action
	Return this
End Function

Function designSet(this.design,id%,typeid%,color.color,mask.color,alpha#,textureid%,bitmapfontid%,caption$,captionalign.vector,border3D%,fx%,action.action)
	this\id%=id%
	this\typeid%=typeid%
	this\color.color=color.color
	this\mask.color=mask.color
	this\alpha#=alpha#
	this\textureid%=textureid%
	this\bitmapfontid%=bitmapfontid%
	this\caption$=caption$
	this\captionalign.vector=captionalign.vector
	this\border3D%=border3D%
	this\fx%=fx%
	this\action.action=action.action
End Function

;============================
;ANIMATION MODULE
;============================
Const ANIMATION_MAX%=255
Dim animationId.animation(ANIMATION_MAX%)
Global animationIndex.stack=stackIndexCreate(ANIMATION_MAX%)

Type animation
	Field id%
	Field typeid%
	Field pathbank%
	Field scale.vector[2] ;from=1, to=2
	Field rotate.vector[2];from=1, to=2
	Field textureframe%[2];from=1, to=2
	Field textureposition.vector[2];from=1, to=2
	Field Color.Color[2];from=1, to=2
	Field alpha#[2];from=1, to=2
	Field soundid%
	Field timingevent%
	Field timingspeed#
	Field order%
End Type

Function animationStop()
	For this.animation=Each animation
		animationDelete(this)
	Next
End Function

Function animationNew.animation()
	this.animation=New animation
	this\id%=0
	this\typeid%=0
	this\pathbank%=0
	this\soundid%=0
	this\timingevent%=0
	this\timingspeed#=0.0
	this\order%=0
	this\id%=StackPop(animationIndex.stack)
	animationId(this\id)=this
	Return this
End Function

Function animationDelete(this.animation)
	animationId(this\id)=Null
	StackPush(animationIndex.stack,this\id%)
	this\timingspeed#=0.0
	Delete this
End Function

Function animationUpdate()
	For this.animation=Each animation
	Next
End Function

Function animationRead.animation(file)
	this.animation=New animation
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\pathbank%=ReadInt(file)
	For loop=1 To 2:this\scale.vector[loop]=vectorRead(file):Next
	For loop=1 To 2:this\rotate.vector[loop]=vectorRead(file):Next
	For loop=1 To 2:this\textureframe%[loop]=ReadInt(file):Next
	For loop=1 To 2:this\textureposition.vector[loop]=vectorRead(file):Next
	For loop=1 To 2:this\Color.Color[loop]=colorRead(file):Next
	For loop=1 To 2:this\alpha#[loop]=ReadFloat(file):Next
	this\soundid%=ReadInt(file)
	this\timingevent%=ReadInt(file)
	this\timingspeed#=ReadFloat(file)
	this\order%=ReadInt(file)
	Return this
End Function

Function animationWrite(file,this.animation)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	WriteInt(file,this\pathbank%)
	For loop=1 To 2:vectorWrite(file,this\scale.vector[loop]):Next
	For loop=1 To 2:vectorWrite(file,this\rotate.vector[loop]):Next
	For loop=1 To 2:WriteInt(file,this\textureframe%[loop]):Next
	For loop=1 To 2:vectorWrite(file,this\textureposition.vector[loop]):Next
	For loop=1 To 2:colorWrite(file,this\Color.Color[loop]):Next
	For loop=1 To 2:WriteFloat(file,this\alpha#[loop]):Next
	WriteInt(file,this\soundid%)
	WriteInt(file,this\timingevent%)
	WriteFloat(file,this\timingspeed#)
	WriteInt(file,this\order%)
End Function

Function animationSave(filename$="Default")
	file=WriteFile(filename$+".animation")
	For this.animation= Each animation
		animationWrite(file,this)
	Next
	CloseFile(file)
End Function

Function animationOpen(filename$="Default")
	file=ReadFile(filename+".animation")
	Repeat
		animationRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function animationCopy.animation(this.animation)
	copy.animation=New animation
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\pathbank%=this\pathbank%
	For loop=1 To 2:copy\scale.vector[loop]=vectorCopy(this\scale.vector[loop]):Next
	For loop=1 To 2:copy\rotate.vector[loop]=vectorCopy(this\rotate.vector[loop]):Next
	For loop=1 To 2:copy\textureframe%[loop]=this\textureframe%[loop]:Next
	For loop=1 To 2:copy\textureposition.vector[loop]=vectorCopy(this\textureposition.vector[loop]):Next
	For loop=1 To 2:copy\Color.Color[loop]=colorCopy(this\Color.Color[loop]):Next
	For loop=1 To 2:copy\alpha#[loop]=this\alpha#[loop]:Next
	copy\soundid%=this\soundid%
	copy\timingevent%=this\timingevent%
	copy\timingspeed#=this\timingspeed#
	copy\order%=this\order%
	Return copy
End Function

Function animationMimic(mimic.animation,this.animation)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\pathbank%=this\pathbank%
	For loop=1 To 2:vectorMimic(mimic\scale.vector[loop],this\scale.vector[loop]):Next
	For loop=1 To 2:vectorMimic(mimic\rotate.vector[loop],this\rotate.vector[loop]):Next
	For loop=1 To 2:mimic\textureframe%[loop]=this\textureframe%[loop]:Next
	For loop=1 To 2:vectorMimic(mimic\textureposition.vector[loop],this\textureposition.vector[loop]):Next
	For loop=1 To 2:colorMimic(mimic\Color.Color[loop],this\Color.Color[loop]):Next
	For loop=1 To 2:mimic\alpha#[loop]=this\alpha#[loop]:Next
	mimic\soundid%=this\soundid%
	mimic\timingevent%=this\timingevent%
	mimic\timingspeed#=this\timingspeed#
	mimic\order%=this\order%
End Function

Function animationCreate.animation(id%,typeid%,pathbank%,scale1.vector,scale2.vector,rotate1.vector,rotate2.vector,textureframe1%,textureframe2%,textureposition1.vector,textureposition2.vector,color1.Color,color2.Color,alpha1#,alpha2#,soundid%,timingevent%,timingspeed#,order%)
	this.animation=animationNew()
	this\id%=id%
	this\typeid%=typeid%
	this\pathbank%=pathbank%
	this\scale.vector[1]=scale1.vector
	this\scale.vector[2]=scale2.vector
	this\rotate.vector[1]=rotate1.vector
	this\rotate.vector[2]=rotate2.vector
	this\textureframe%[1]=textureframe1%
	this\textureframe%[2]=textureframe2%
	this\textureposition.vector[1]=textureposition1.vector
	this\textureposition.vector[2]=textureposition2.vector
	this\Color.Color[1]=color1.Color
	this\Color.Color[2]=color2.Color
	this\alpha#[1]=alpha1#
	this\alpha#[2]=alpha2#
	this\soundid%=soundid%
	this\timingevent%=timingevent%
	this\timingspeed#=timingspeed#
	this\order%=order%
	Return this
End Function

Function animationSet(this.animation,id%,typeid%,pathbank%,scale1.vector,scale2.vector,rotate1.vector,rotate2.vector,textureframe1%,textureframe2%,textureposition1.vector,textureposition2.vector,color1.Color,color2.Color,alpha1#,alpha2#,soundid%,timingevent%,timingspeed#,order%)
	this\id%=id%
	this\typeid%=typeid%
	this\pathbank%=pathbank%
	this\scale.vector[1]=scale1.vector
	this\scale.vector[2]=scale2.vector
	this\rotate.vector[1]=rotate1.vector
	this\rotate.vector[2]=rotate2.vector
	this\textureframe%[1]=textureframe1%
	this\textureframe%[2]=textureframe2%
	this\textureposition.vector[1]=textureposition1.vector
	this\textureposition.vector[2]=textureposition2.vector
	this\Color.Color[1]=color1.Color
	this\Color.Color[2]=color2.Color
	this\alpha#[1]=alpha1#
	this\alpha#[2]=alpha2#
	this\soundid%=soundid%
	this\timingevent%=timingevent%
	this\timingspeed#=timingspeed#
	this\order%=order%
End Function

;============================
;GADGET MODULE
;============================
Const	GADGET_MAX%	=	1024
Const	GADGET_MIN%	=	1
Const	GADGET_BITMAP_FONT	=	16

Const	GADGET_CANVAS%	=	1
Const	GADGET_POINTER%	=	2
Const	GADGET_WINDOW%	=	3
Const	GADGET_WINDOW_BAR%	=	4
Const	GADGET_WINDOW_MINIMIZE%	=	5
Const	GADGET_WINDOW_MAXIMIZE%	=	6
Const	GADGET_WINDOW_CLOSE%	=	7
Const	GADGET_WINDOW_ICON%	=	8
Const	GADGET_TAB%	=	9
Const 	GADGET_TAB_PANEL%	=	10
Const	GADGET_FRAME%	=	11
Const	GADGET_BOARD%	=	12
Const	GADGET_MENU%	=	13
Const	GADGET_MENU_PANEL%	=	14
Const	GADGET_MENU_TAB%	=	15
Const	GADGET_IMAGE_BOX%	=	16
Const	GADGET_ITEM%	=	17
Const	GADGET_BUTTON%	=	18
Const	GADGET_ICON%	=	19
Const	GADGET_IMAGE%	=	20
Const	GADGET_OPTION%	=	21
Const	GADGET_CHECKBOX%	=	22
Const	GADGET_LINK%	=	23
Const	GADGET_CLICKFIELD%	=	24
Const	GADGET_LABEL%	=	25
Const	GADGET_SLIDERH%	=	26
Const	GADGET_SLIDERV%	=	27
Const	GADGET_SLIDERLEVER%	=	28
Const	GADGET_SLIDERDIRECTION1%	=	29
Const	GADGET_SLIDERDIRECTION2%	=	30
Const 	GADGET_SLIDERBOX%	=	31
Const	GADGET_SLIDERBOXLEVER	=	32
Const	GADGET_TOGGLESWITCH%	=	33
Const	GADGET_TEXTFIELD%	=	34
Const	GADGET_TEXTFIELDCURSOR%	=	35
Const	GADGET_TEXTAREA%	=	36
Const	GADGET_TEXTAREAPANEL%	=	37
Const	GADGET_TEXTAREASLIDERH%	=	38
Const	GADGET_TEXTAREASLIDERV%	=	39
Const	GADGET_TEXTAREATEXTFIELD%	=	40
Const	GADGET_COMBOBOX%	=	41
Const	GADGET_COMBOBOXPANEL%	=	42
Const	GADGET_COMBOBOXDROP%	=	43
Const	GADGET_COMBOBOXSLIDERV%	=	44
Const	GADGET_ROLLOUTTAB%	=	45
Const	GADGET_ROLLOUTPANEL%	=	46
Const	GADGET_DRIVEBOX%	=	47
Const	GADGET_DIRBOX%	=	48
Const	GADGET_FILEBOX%	=	49
Const	GADGET_STOPWATCH%	=	50
Const	GADGET_TEXT%	=	51
Const	GADGET_KNOB%	=	52
Const	GADGET_METER%	=	53
Const	GADGET_GAUGE%	=	54
Const	GADGET_RADAR%	=	55
Const	GADGET_VIEWPORT	=	56

Const	GADGET_MOUSE_EVENTS%	=	4
Const	GADGET_SELECT%	=	1
Const	GADGET_DESELECT%	=	2
Const	GADGET_CLICK%	=	3
Const	GADGET_DISABLE%	=	4

Const	GADGET_CLICK_SINGLE%	=	8
Const	GADGET_CLICK_DOUBLE%	=	16
Const	GADGET_CLICK_HOLD%	=	32
Const	GADGET_CLICK_RELEASE%	=	64
Const	GADGET_CLICK_ROLL%	=	128

Const	GADGET_CHECKED%	=	1
Const	GADGET_UNCHECKED%	=	2

Dim gadgetId.gadget(GADGET_MAX%)
Global gadgetIndex.stack=stackIndexCreate(GADGET_MAX%)
Global gadgetAvail.stack=stackIndexCreate(GADGET_MAX%)
Global pointer.gadget
Global gadgetInFocus.gadget

Type gadget
	Field id%
	Field typeid%
	Field parent.gadget
	Field children%
	Field childrenbank%
	Field childoffset%
	Field entity%
	Field name$
	Field absolute.vector
	Field position.vector
	Field scale.vector
	Field angle.vector
	Field cameraid%
	Field visible%
	Field texture.texture[GADGET_MOUSE_EVENTS%]
;	Field design.design[GADGET_MOUSE_EVENTS%]
;	Field viewport.screen
	Field pointerid%
	Field soundid%[GADGET_MOUSE_EVENTS%]
	Field audible%
	Field animationid%[GADGET_MOUSE_EVENTS%]
	Field event%
	Field oldevent%
	Field active%
	Field pickmode%
	Field collision%
	Field Collisions%
	Field reset.clock
	Field actionevent%[GADGET_MOUSE_EVENTS%]
	Field action.action[GADGET_MOUSE_EVENTS%]
	Field actionkey%[GADGET_MOUSE_EVENTS%]
	Field targetid%[GADGET_MOUSE_EVENTS%]
	Field state%
	Field value#
	Field text$
	Field tag$
	Field min#
	Field max#
	Field inc#
	Field help$
End Type

Function gadgetStart()
	;get sound
	;load textures
		;load bitmapfont
	;create pointer
End Function

Function gadgetStop()
	For this.gadget=Each gadget
		gadgetDelete(this)
	Next
End Function

Function gadgetNew.gadget()
	this.gadget=New gadget
	this\id%=0
	this\typeid%=0
	;this\parentid%=0
	this\children%=0
	this\childrenbank%=0
	this\childoffset%=0
	this\entity%=0
	this\name$=""
	this\absolute.vector=vectorNew()
	this\position.vector=vectorNew()
	this\scale.vector=vectorNew()
	this\angle.vector=vectorNew()
	this\cameraid%=0
	this\visible%=0
;	this\viewport.screen=screenNew()
	this\pointerid%=0
	this\audible%=0
	this\event%=0
	this\oldevent%=0
	this\active%=0
	this\pickmode%=0
	this\collision%=0
	this\Collisions%=0
	this\reset.clock=clockNew()
	this\state%=0
	this\value#=0.0
	this\text$=""
	this\tag$=""
	this\min#=0.0
	this\max#=0.0
	this\inc#=0.0
	this\help$=""
	this\id%=StackPop(gadgetIndex.stack)
	gadgetId(this\id)=this
	Return this
End Function

Function gadgetDelete(this.gadget)
	gadgetId(this\id)=Null
	StackPush(gadgetIndex.stack,this\id%)
	this\help$=""
	this\inc#=0.0
	this\max#=0.0
	this\min#=0.0
	this\tag$=""
	this\text$=""
	this\value#=0.0
	clockDelete(this\reset.clock)
;	screenDelete(this\viewport.screen)
	vectorDelete(this\angle.vector)
	vectorDelete(this\scale.vector)
	vectorDelete(this\position.vector)
	vectorDelete(this\absolute.vector)
	this\name$=""
	Freeentity this\entity%
	Delete this
End Function

Function gadgetUpdate()
	For this.gadget=Each gadget
	Next
End Function

Function gadgetRead.gadget(file)
	this.gadget=New gadget
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\parent.gadget=gadgetID(ReadInt(file))
	this\children%=ReadInt(file)
	this\childrenbank%=ReadInt(file)
	this\childoffset%=ReadInt(file)
	this\entity%=ReadInt(file)
	this\name$=ReadLine(file)
	this\absolute.vector=vectorRead(file)
	this\position.vector=vectorRead(file)
	this\scale.vector=vectorRead(file)
	this\angle.vector=vectorRead(file)
	this\cameraid%=ReadInt(file)
	this\visible%=ReadInt(file)
	For loop=1 To GADGET_MOUSE_EVENTS%:this\texture.texture[loop]=textureRead(file):Next
;	For loop=1 To GADGET_MOUSE_EVENTS%:this\design.design[loop]=designRead(file):Next
;	this\viewport.screen=screenRead(file)
	this\pointerid%=ReadInt(file)
	For loop=1 To GADGET_MOUSE_EVENTS%:this\soundid%[loop]=ReadInt(file):Next
	this\audible%=ReadInt(file)
	For loop=1 To GADGET_MOUSE_EVENTS%:this\animationid%[loop]=ReadInt(file):Next
	this\event%=ReadInt(file)
	this\oldevent%=ReadInt(file)
	this\active%=ReadInt(file)
	this\pickmode%=ReadInt(file)
	this\collision%=ReadInt(file)
	this\Collisions%=ReadInt(file)
	this\reset.clock=clockRead(file)
	For loop=1 To GADGET_MOUSE_EVENTS%:this\actionevent%[loop]=ReadInt(file):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:this\action.action[loop]=actionRead(file):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:this\actionkey%[loop]=ReadInt(file):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:this\targetid%[loop]=ReadInt(file):Next
	this\state%=ReadInt(file)
	this\value#=ReadFloat(file)
	this\text$=ReadLine(file)
	this\tag$=ReadLine(file)
	this\min#=ReadFloat(file)
	this\max#=ReadFloat(file)
	this\inc#=ReadFloat(file)
	this\help$=ReadLine(file)
	Return this
End Function

Function gadgetWrite(file,this.gadget)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	WriteInt(file,this\parent\id%)
	WriteInt(file,this\children%)
	WriteInt(file,this\childrenbank%)
	WriteInt(file,this\childoffset%)
	WriteInt(file,this\entity%)
	WriteLine(file,this\name$)
	vectorWrite(file,this\absolute.vector)
	vectorWrite(file,this\position.vector)
	vectorWrite(file,this\scale.vector)
	vectorWrite(file,this\angle.vector)
	WriteInt(file,this\cameraid%)
	WriteInt(file,this\visible%)
	For loop=1 To GADGET_MOUSE_EVENTS%:textureWrite(file,this\texture.texture[loop]):Next
;	For loop=1 To GADGET_MOUSE_EVENTS%:designWrite(file,this\design.design[loop]):Next
;	screenWrite(file,this\viewport.screen)
	WriteInt(file,this\pointerid%)
	For loop=1 To GADGET_MOUSE_EVENTS%:WriteInt(file,this\soundid%[loop]):Next
	WriteInt(file,this\audible%)
	For loop=1 To GADGET_MOUSE_EVENTS%:WriteInt(file,this\animationid%[loop]):Next
	WriteInt(file,this\event%)
	WriteInt(file,this\oldevent%)
	WriteInt(file,this\active%)
	WriteInt(file,this\pickmode%)
	WriteInt(file,this\collision%)
	WriteInt(file,this\Collisions%)
	clockWrite(file,this\reset.clock)
	For loop=1 To GADGET_MOUSE_EVENTS%:WriteInt(file,this\actionevent%[loop]):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:actionWrite(file,this\action.action[loop]):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:WriteInt(file,this\actionkey%[loop]):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:WriteInt(file,this\targetid%[loop]):Next
	WriteInt(file,this\state%)
	WriteFloat(file,this\value#)
	WriteLine(file,this\text$)
	WriteLine(file,this\tag$)
	WriteFloat(file,this\min#)
	WriteFloat(file,this\max#)
	WriteFloat(file,this\inc#)
	WriteLine(file,this\help$)
End Function

Function gadgetSave(filename$="Default")
	file=WriteFile(filename$+".gadget")
	For this.gadget= Each gadget
		gadgetWrite(file,this)
	Next
	CloseFile(file)
End Function

Function gadgetOpen(filename$="Default")
	file=ReadFile(filename+".gadget")
	Repeat
		gadgetRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function gadgetCopy.gadget(this.gadget)
	copy.gadget=New gadget
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\parent.gadget=gadgetID(this\parent\id%)
	copy\children%=this\children%
	copy\childrenbank%=this\childrenbank%
	copy\childoffset%=this\childoffset%
	copy\entity%=this\entity%
	copy\name$=this\name$
	copy\absolute.vector=vectorCopy(this\absolute.vector)
	copy\position.vector=vectorCopy(this\position.vector)
	copy\scale.vector=vectorCopy(this\scale.vector)
	copy\angle.vector=vectorCopy(this\angle.vector)
	copy\cameraid%=this\cameraid%
	copy\visible%=this\visible%
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\texture.texture[loop]=textureCopy(this\texture.texture[loop]):Next
;	For loop=1 To GADGET_MOUSE_EVENTS%:copy\design.design[loop]=designCopy(this\design.design[loop]):Next
;	copy\viewport.screen=screenCopy(this\viewport.screen)
	copy\pointerid%=this\pointerid%
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\soundid%[loop]=this\soundid%[loop]:Next
	copy\audible%=this\audible%
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\animationid%[loop]=this\animationid%[loop]:Next
	copy\event%=this\event%
	copy\oldevent%=this\oldevent%
	copy\active%=this\active%
	copy\pickmode%=this\pickmode%
	copy\collision%=this\collision%
	copy\Collisions%=this\Collisions%
	copy\reset.clock=clockCopy(this\reset.clock)
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\actionevent%[loop]=this\actionevent%[loop]:Next
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\action.action[loop]=actionCopy(this\action.action[loop]):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\actionkey%[loop]=this\actionkey%[loop]:Next
	For loop=1 To GADGET_MOUSE_EVENTS%:copy\targetid%[loop]=this\targetid%[loop]:Next
	copy\state%=this\state%
	copy\value#=this\value#
	copy\text$=this\text$
	copy\tag$=this\tag$
	copy\min#=this\min#
	copy\max#=this\max#
	copy\inc#=this\inc#
	copy\help$=this\help$
	Return copy
End Function

Function gadgetMimic(mimic.gadget,this.gadget)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\parent.gadget=gadgetID(this\parent\id%)
	mimic\children%=this\children%
	mimic\childrenbank%=this\childrenbank%
	mimic\childoffset%=this\childoffset%
	mimic\entity%=this\entity%
	mimic\name$=this\name$
	vectorMimic(mimic\absolute.vector,this\absolute.vector)
	vectorMimic(mimic\position.vector,this\position.vector)
	vectorMimic(mimic\scale.vector,this\scale.vector)
	vectorMimic(mimic\angle.vector,this\angle.vector)
	mimic\cameraid%=this\cameraid%
	mimic\visible%=this\visible%
	For loop=1 To GADGET_MOUSE_EVENTS%:textureMimic(mimic\texture.texture[loop],this\texture.texture[loop]):Next
;	For loop=1 To GADGET_MOUSE_EVENTS%:designMimic(mimic\design.design[loop],this\design.design[loop]):Next
;	screenMimic(mimic\viewport.screen,this\viewport.screen)
	mimic\pointerid%=this\pointerid%
	For loop=1 To GADGET_MOUSE_EVENTS%:mimic\soundid%[loop]=this\soundid%[loop]:Next
	mimic\audible%=this\audible%
	For loop=1 To GADGET_MOUSE_EVENTS%:mimic\animationid%[loop]=this\animationid%[loop]:Next
	mimic\event%=this\event%
	mimic\oldevent%=this\oldevent%
	mimic\active%=this\active%
	mimic\pickmode%=this\pickmode%
	mimic\collision%=this\collision%
	mimic\Collisions%=this\Collisions%
	clockMimic(mimic\reset.clock,this\reset.clock)
	For loop=1 To GADGET_MOUSE_EVENTS%:mimic\actionevent%[loop]=this\actionevent%[loop]:Next
	For loop=1 To GADGET_MOUSE_EVENTS%:actionMimic(mimic\action.action[loop],this\action.action[loop]):Next
	For loop=1 To GADGET_MOUSE_EVENTS%:mimic\actionkey%[loop]=this\actionkey%[loop]:Next
	For loop=1 To GADGET_MOUSE_EVENTS%:mimic\targetid%[loop]=this\targetid%[loop]:Next
	mimic\state%=this\state%
	mimic\value#=this\value#
	mimic\text$=this\text$
	mimic\tag$=this\tag$
	mimic\min#=this\min#
	mimic\max#=this\max#
	mimic\inc#=this\inc#
	mimic\help$=this\help$
End Function

Function gadgetCreate.gadget(parent.gadget,typeid%,entity%,name$,x#,y#,z#)
	this.gadget=gadgetNew()
	this\parent.gadget=parent.gadget
	this\typeid%=typeid%
	this\entity%=entity%
	this\name$=name$
	this\position\x#=x#
	this\position\y#=y#
	this\position\z#=z#
	PositionEntity this\entity%,this\position\x#,this\position\y#,this\position\z#
	EntityBlend this\entity%,3
	EntityOrder this\entity%,-1
	EntityPickMode this\entity%,2 
	Return this
End Function

Function gadgetSet(this.gadget,id%,typeid%,parentid%,children%,childrenbank%,childoffset%,entity%,name$,absolute.vector,position.vector,scale.vector,angle.vector,cameraid%,visible%,pointerid%,soundid1%,soundid2%,soundid3%,soundid4%,audible%,animationid1%,animationid2%,animationid3%,animationid4%,event%,oldevent%,active%,pickmode%,collision%,Collisions%,reset.clock,actionevent1%,actionevent2%,actionevent3%,actionevent4%,action1.action,action2.action,action3.action,action4.action,actionkey1%,actionkey2%,actionkey3%,actionkey4%,targetid1%,targetid2%,targetid3%,targetid4%,state%,value#,Text$,tag$,min#,max#,inc#,help$)
	this\id%=id%
	this\typeid%=typeid%
	this\parent.gadget=gadgetID(parentid%)
	this\children%=children%
	this\childrenbank%=childrenbank%
	this\childoffset%=childoffset%
	this\entity%=entity%
	this\name$=name$
	this\absolute.vector=absolute.vector
	this\position.vector=position.vector
	this\scale.vector=scale.vector
	this\angle.vector=angle.vector
	this\cameraid%=cameraid%
	this\visible%=visible%
;	this\design.design[1]=design1.design
;	this\design.design[2]=design2.design
;	this\design.design[3]=design3.design
;	this\design.design[4]=design4.design
;	this\viewport.screen=viewport.screen
	this\pointerid%=pointerid%
	this\soundid%[1]=soundid1%
	this\soundid%[2]=soundid2%
	this\soundid%[3]=soundid3%
	this\soundid%[4]=soundid4%
	this\audible%=audible%
	this\animationid%[1]=animationid1%
	this\animationid%[2]=animationid2%
	this\animationid%[3]=animationid3%
	this\animationid%[4]=animationid4%
	this\event%=event%
	this\oldevent%=oldevent%
	this\active%=active%
	this\pickmode%=pickmode%
	this\collision%=collision%
	this\Collisions%=Collisions%
	this\reset.clock=reset.clock
	this\actionevent%[1]=actionevent1%
	this\actionevent%[2]=actionevent2%
	this\actionevent%[3]=actionevent3%
	this\actionevent%[4]=actionevent4%
	this\action.action[1]=action1.action
	this\action.action[2]=action2.action
	this\action.action[3]=action3.action
	this\action.action[4]=action4.action
	this\actionkey%[1]=actionkey1%
	this\actionkey%[2]=actionkey2%
	this\actionkey%[3]=actionkey3%
	this\actionkey%[4]=actionkey4%
	this\targetid%[1]=targetid1%
	this\targetid%[2]=targetid2%
	this\targetid%[3]=targetid3%
	this\targetid%[4]=targetid4%
	this\state%=state%
	this\value#=value#
	this\Text$=Text$
	this\tag$=tag$
	this\min#=min#
	this\max#=max#
	this\inc#=inc#
	this\help$=help$
End Function

Function gadgetMouseEvent(this.gadget)

	If this\visible%

     Select this\pickmode%
        Case 0 Return 0 ;no collision
        Case 1,2,3 ;3D object
           If this\entity%=PickedEntity()
             collision=True
             this\Collisions%=this\Collisions%+1
           EndIf
		Case 4 ;2D box collision algo
           If pointer\position\x#>=this\absolute\x# And pointer\position\x#<=this\absolute\x+this\scale\x#
				If pointer\position\y#>=this\absolute\y# And pointer\position\y#<=this\absolute\y+this\scale\y#
					If pointer\position\z#>=this\absolute\z# And pointer\position\z#<=this\absolute\y+this\scale\y#
						collision=True
						this\Collisions%=this\Collisions%+1
					EndIf
				EndIf
			EndIf
	 End Select

	If collision%

		;set gadget focus on gadget on top sets current gadget
		gadgetInFocus\event=gadgetInFocus\oldevent%
		gadgetInFocus=this
		gadgetInFocus\oldevent%=gadgetInFocus\event

		If this\active%

            ;clicknone
            Select pointer\event%
                Case none this\event=GADGET_SELECT%

			    Case GADGET_CLICK_SINGLE%,GADGET_CLICK_HOLD%,GADGET_CLICK_RELEASE%
			       this\reset\count%=this\reset\value%
				   this\event=GADGET_CLICK%
				   Return

			    Case GADGET_CLICK_ROLL%
				   ;return

			    Default
				    this\event=GADGET_DESELECT%
				    If Not this\reset\count% ;sync release with timer
				    EndIf
				    Return
      		End Select

		Else ;this not active, setfocus on mousedown(1) if window or parent active

			;DISABLE, disabled gadget
			this\event%=GADGET_DISABLE%
			Return

		EndIf
	Else	;no collision
		If this\collision% And this\active%
			;deselected
			this\event=GADGET_DESELECT%
            this\Collisions%=reset%
			Return
		Else
			;DISABLE
			this\event=GADGET_DISABLE
            this\Collisions%=reset%
			Return
		EndIf
	EndIf
  EndIf
End Function

Function gadgetBehavior(this.gadget)
	Select this\typeid%
		Case	GADGET_CANVAS%	
		Case	GADGET_POINTER%	
		Case	GADGET_WINDOW%	
		Case	GADGET_WINDOW_BAR%	
		Case	GADGET_WINDOW_MINIMIZE%	
		Case	GADGET_WINDOW_MAXIMIZE%	
		Case	GADGET_WINDOW_CLOSE%	
		Case	GADGET_WINDOW_ICON%	
		Case	GADGET_TAB%	
		Case 	GADGET_TAB_PANEL%	
		Case	GADGET_FRAME%	
		Case	GADGET_BOARD%	
		Case	GADGET_MENU%	
		Case	GADGET_MENU_PANEL%	
		Case	GADGET_MENU_TAB%	
		Case	GADGET_IMAGE_BOX%	
		Case	GADGET_ITEM%	
		Case	GADGET_BUTTON%	
		Case	GADGET_ICON%	
		Case	GADGET_IMAGE%	
		Case	GADGET_OPTION%	
		Case	GADGET_CHECKBOX%	
		Case	GADGET_LINK%	
		Case	GADGET_CLICKFIELD%	
		Case	GADGET_LABEL%	
		Case	GADGET_SLIDERH%	
		Case	GADGET_SLIDERV%	
		Case	GADGET_SLIDERLEVER%	
		Case	GADGET_SLIDERDIRECTION1%	
		Case	GADGET_SLIDERDIRECTION2%	
		Case 	GADGET_SLIDERBOX%	
		Case	GADGET_SLIDERBOXLEVER	
		Case	GADGET_TOGGLESWITCH%	
		Case	GADGET_TEXTFIELD%	
		Case	GADGET_TEXTFIELDCURSOR%	
		Case	GADGET_TEXTAREA%	
		Case	GADGET_TEXTAREAPANEL%	
		Case	GADGET_TEXTAREASLIDERH%	
		Case	GADGET_TEXTAREASLIDERV%	
		Case	GADGET_TEXTAREATEXTFIELD%	
		Case	GADGET_COMBOBOX%	
		Case	GADGET_COMBOBOXPANEL%	
		Case	GADGET_COMBOBOXDROP%	
		Case	GADGET_COMBOBOXSLIDERV%	
		Case	GADGET_ROLLOUTTAB%	
		Case	GADGET_ROLLOUTPANEL%	
		Case	GADGET_DRIVEBOX%	
		Case	GADGET_DIRBOX%	
		Case	GADGET_FILEBOX%	
		Case	GADGET_STOPWATCH%	
		Case	GADGET_TEXT%	
		Case	GADGET_KNOB%	
		Case	GADGET_METER%	
		Case	GADGET_GAUGE%	
		Case	GADGET_LAYER%	
		Default 
	End Select	
End Function

Function gadgetDraw(this.gadget)
End Function

Function gadgetDisplay(this.gadget)
End Function

Function gadgetSound(this.gadget)
End Function

Function gadgetAnimation(this.gadget)
		;effect 
			;1=entry
			;2=emphasis
			;3=exit
			
			;entryMotion
				;Path bank
				;Scale
				;Rotate
			;AnimateTexture
			;TextureOrientation
				;Animated Texture
				;Texture Orientation
				;ColorTransition
				;AlphaTransition
		;Sound
			;	
		;Timing
			;On event
			;After previous event
			;Speed
		;Order			
End Function

Function gadgetAction(this.gadget)
End Function
