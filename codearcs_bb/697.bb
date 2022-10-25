; ID: 697
; Author: Red
; Date: 2003-05-18 19:50:59
; Title: Advance PopupMenu
; Description: Easy popup menu

; ================================
; Load popupmenu
; ================================
Type POPUP_MENU
	Field txt$ = ""
	Field flag
	Field menu_id = 0
	Field popup.POPUP_MENU = Null	
	Field parent.POPUP_MENU = Null
	Field root =  0
End Type

Function PopupMenu%(txt$="",menu_id=0,parent=0)	
	Local pm.POPUP_MENU=New POPUP_MENU
	Local p.POPUP_MENU=Object.POPUP_MENU(parent)
	If p=Null
		;root
		pm\popup=pm
		pm\parent=Null		
	Else		
		;link parent to root of new popupmenu
		If p\popup=Null 
			p\popup=New POPUP_MENU
			p\popup\parent=Null				
		EndIf 
		;node
		pm\txt=txt	
		pm\menu_id=menu_id	
		pm\parent=p\popup
	EndIf 
	
	Local ppmenu=Handle(pm)
	Return ppmenu
End Function 

; ================================
; Create popupmenu
; ================================

Function UpdatePopupMenu(ppmenu)	
	Local MF_STRING = $0
	Local MF_SEPARATOR = $800
	Local MF_POPUP = $10
	
	Local MF_DISABLED=$02
	Local MF_GRAYED=$01
	Local MF_CHECKED=$08	
	
	Local hmenu
	Local pmRoot.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	Local pm.POPUP_MENU	
	Local submenu.POPUP_MENU	

	If pmRoot\parent<>Null RuntimeError "UpdatePopupMenu works only with popupmenu root !"
	
	;create root
	pmRoot\root=CreatePopupMenu()			
	
	;create menu items
	For pm=Each POPUP_MENU
 
		If pm\parent=pmRoot						
			;state of item	
			pm\flag=pm\flag And (MF_CHECKED Or MF_GRAYED Or MF_DISABLED)
			If pm\txt="" 
				pm\flag=pm\flag Or MF_SEPARATOR		
			Else
				pm\flag=pm\flag Or MF_STRING
			EndIf
			If pm\popup<>Null
				;create submenu
				submenu=pm\popup
				UpdatePopupMenu Handle(submenu)										
				;link to menu item
				pm\flag=pm\flag Or MF_POPUP And ~MF_SEPARATOR
				pm\menu_id=submenu\root 
			EndIf 

			;create item 
			hmenu=pm\parent\root
			AppendMenu hmenu,pm\flag,pm\menu_id,pm\txt$
		EndIf 
	Next 	
End Function

; ================================
; Append a popupmenu
; ================================

Function AppendPopupMenu(ppmenu,ppmenuToAppend)
	Local MF_BYPOSITION = $400
	
	Local MF_STRING = $0
	Local MF_POPUP = $10
	
	Local hmenu
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	Local pmA.POPUP_MENU=Object.POPUP_MENU(ppmenuToAppend)
	Local pm_.POPUP_MENU
	
	If pm=Null Or pmA=Null 
		RuntimeError "A parameter misses in AppendPopupMenu Call..."
	EndIf
	If pm\parent=Null And pmA\popup\parent<>Null  
		RuntimeError "AppendPopupMenu needs a popupmenu item and a popupmenu root !"
	EndIf


	;broke last link
	DetachSubMenu ppmenu
	
	;link
	For pm_=Each POPUP_MENU
		If pm_=pm 
			Exit
		Else
			If pm_\parent=pm\parent Then pos=pos+1
		EndIf 
	Next
	pm\flag=MF_STRING Or MF_POPUP Or MF_BYPOSITION	
	pm\popup=pmA
	
	;update item	
	hmenu=pm\parent\root
	ModifyMenu hmenu,pos,pm\flag,pm\popup\root,pm\txt	

End Function

; ================================
; Detach a submenu
; ================================

Function DetachSubMenu%(ppmenu)
	Local MF_STRING = $0
	Local MF_POPUP = $10
	Local MF_BYPOSITION = $400

	Local pm_.POPUP_MENU
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	Local pmD.POPUP_MENU

	If pm\parent=Null Then RuntimeError " DetachPopupMenu : it's a popupmenu root, there's nothing to detach !"

	;link
	For pm_=Each POPUP_MENU
		If pm_=pm 
			Exit
		Else
			If pm_\parent=pm\parent Then pos=pos+1
		EndIf 
	Next	
	pmD=pm\popup
	pm\popup=Null
	pm\flag=MF_STRING Or MF_BYPOSITION	

	;update menu	
	DestroyMenu pm\parent\root
	UpdatePopupMenu Handle(pm\parent)

	;recreate submenu	
	If pmD<>Null
		DestroyMenu pmD\root
		UpdatePopupMenu Handle(pmD)
	EndIf
	
	Return Handle(pmD)
End Function

; ================================
; Modify text
; ================================

Function ModifyPopupMenuText$(ppmenu,txt$)
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)

	If pm\parent=Null Then RuntimeError "ModifyPopupMenuText does not work with popupmenu root !"
	
	Local hmenu=pm\parent\root
	Local oldtxt$=pm\txt
	pm\txt=txt
	ModifyMenu hmenu,pm\menu_id,pm\flag,pm\menu_id,pm\txt
	
	Return oldtxt$
End Function

; ================================
; Modify state
; ================================

Function ModifyPopupMenuState(ppmenu,state) ;1=checked ;2=grayed ;4=disabled
	Local MF_DISABLED=$02
	Local MF_GRAYED=$01
	Local MF_CHECKED=$08
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	
	If pm\parent=Null Then RuntimeError "ModifyPopupMenuState does not work with popupmenu root !"

	pm\flag=0
	If (state And $01) Then pm\flag=MF_CHECKED
	If (state And $02) Then pm\flag=pm\flag Or MF_GRAYED
	If (state And $04) Then pm\flag=pm\flag Or MF_DISABLED

	Local hmenu=pm\parent\root
	ModifyMenu hmenu,pm\menu_id,pm\flag,pm\menu_id,pm\txt
	
End Function

; ================================
; Get state
; ================================

Function GetPopupMenuState%(ppmenu)
	Local MF_DISABLED=$02
	Local MF_GRAYED=$01
	Local MF_CHECKED=$08
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	
	If pm\parent=Null Then RuntimeError "ModifyPopupMenuState does not work with popupmenu root !"
	
	If (pm\flag And MF_CHECKED) Then state=1
	If (pm\flag And MF_GRAYED)  Then state=state Or 2
	If (pm\flag And MF_DISABLED) Then state=state Or 4
	
	Return state
End Function

; ================================
; Show popupmenu at mouse position
; ================================

Function ShowPopupMenu%(ppmenu,win=0)
	Local TPM_RETURNCMD = $100
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	Local hmenu=pm\root
	
	If win=0 Then win=ActiveWindow()
		
	Local menu_id=TrackPopupMenuEx(hmenu,TPM_RETURNCMD,MouseX(),MouseY(),QueryObject(win,1),0)
	Return menu_id	
End Function
