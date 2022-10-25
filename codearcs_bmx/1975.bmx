; ID: 1975
; Author: ninjarat
; Date: 2007-03-25 07:10:06
; Title: Game Menu
; Description: Displays and Uses a game menu

Type MenuStruct
	Field Items$[]
	Field X,Y
	Field SelectedColor[]
	Field DefaultColor[]
	Field IsShadowed
End Type

Function CreateMenu_:MenuStruct(items$[],x,y,scr,scg,scb,dcr,dcg,dcb,shadow)
	Local menu:MenuStruct=New MenuStruct
	menu.Items=items
	menu.X=x;menu.Y=y
	menu.SelectedColor=[scr,scg,scb]
	menu.DefaultColor=[dcr,dcg,dcb]
	menu.IsShadowed=shadow
	Return menu
End Function

Function RunMenuStruct(menu:MenuStruct)
	Return RunMenu(menu.Items,menu.X,menu.Y,..
	 menu.SelectedColor,menu.DefaultColor,menu.IsShadowed)
End Function

'colors use struct {r,g,b}
Function RunMenu(items$[],x,y,selectedcolor[],defaultcolor[],shadow)
	Local itembdt[items.length]
	Local itembdb[items.length]
	Local itembdl[items.length]
	Local itembdr[items.length]
	Local selection[items.length]
	gfxxc=GraphicsWidth()/2
	thght=TextHeight("X")+3
	
	If x<0 Then center=True Else center=False
	
	'logical setup
	itemctr=0
	For ctr=0 To items.length-1
		If center Then
			itembdl[ctr]=gfxxc-TextWidth(items[ctr])/2
		Else
			itembdl[ctr]=x
		End If
		itembdr[ctr]=itembdl[ctr]+TextWidth(items[ctr])
		itembdt[ctr]=y+ctr*thght
		itembdb[ctr]=itembdt[ctr]+thght
	Next
	
	'logical check
	msx=MouseX()
	msy=MouseY()
	msh=MouseHit(1)
	btnclicked=-1
	For ctr=0 To items.length-1
		selection[ctr]=False
		If msx>itembdl[ctr] And msx<itembdr[ctr] Then
			If msy>itembdt[ctr] And msy<itembdb[ctr] Then
				selection[ctr]=True
				If msh Then btnclicked=ctr
			End If
		End If
	Next
	
	'get the old color so we can put it back
	GetColor _red,_grn,_blu
	
	'draw based on selection
	For ctr=0 To items.length-1
		x=itembdl[ctr]
		y=itembdt[ctr]
		If shadow Then
			SetColor 0,0,0
			SetAlpha .7
			DrawText items[ctr],x+2,y+2
			SetAlpha 1
		End If
		If selection[ctr] Then
			SetColor selectedcolor[0],selectedcolor[1],selectedcolor[2]
		Else
			SetColor defaultcolor[0],defaultcolor[1],defaultcolor[2]
		End If
		DrawText items[ctr],x,y
	Next
	
	'put the old color back
	SetColor _red,_grn,_blu
	
	itembdt=Null
	itembdb=Null
	itembdl=Null
	itembdr=Null
	selection=Null
	
	Return btnclicked
End Function
