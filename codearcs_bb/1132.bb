; ID: 1132
; Author: gman
; Date: 2004-08-12 05:24:59
; Title: Advance Popup Menu extras
; Description: adds GetPopupMenuText, FindPopupHandle, SetPopupDataItem, GetPopupDataItem functions and better control over popup position

; ================================
; Author: gman
; Date: 2004_08_10
; Title: FindPopupHandle
; Description:	- addon For the Advance PopupMenu by Ed From Mars (ID: 697)
;			- gets the popupmenu Handle based on menu_id
;
; Requirements: - stock Advance PopupMenu
; ================================
Function FindPopupHandle(menu_id%)
	Local pm_find.POPUP_MENU
	
	For pm_find=Each POPUP_MENU
		If pm_find\menu_id=menu_id
			Return Handle(pm_find)
		EndIf
	Next
	RuntimeError "Invalid menu_id passed to FindPopupHandle !"
End Function

; ================================
; Author: gman
; Date: 2004_08_10
; Title: GetPopupMenuText
; Description:	- addon For the Advance PopupMenu by Ed From Mars (ID: 697)
;			- gets the text of a popupmenu based on its menu_id
;
; Requirements: - stock Advance PopupMenu
;			- FindPopupHandle by gman
; ================================
Function GetPopupMenuText$(menu_id%)
	; get the popup based on menu_id.  will fail during find if menu_id doesnt exist.
	Local pm.POPUP_MENU=Object.POPUP_MENU(FindPopupHandle(menu_id))
	
	If pm\parent=Null Then RuntimeError "GetPopupMenuText does not work with popupmenu root !"
		
	Return pm\txt$
End Function

; ================================
; Author: gman
; Date: 2004_08_10
; Title: SetPopupDataItem
; Description: 	- addon for the Advance PopupMenu by Ed From Mars (ID: 697)
;			- sets a value in the mndata$ field
;
; Requirements: - Advance PopupMenu, modified to have a mndata$ field in the POPUP_MENU type
;			- gettok function by skn3[ac] (ID: 290)
;			- GetPopupDataItem function by gman
; ================================
Function SetPopupDataItem(item$,value$,ppmenu)

	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	If (Handle(pm)<=0) Then RuntimeError "Invalid handle passed to SetPopupDataItem !"

	item$=Upper(item$)

	If (item$="") Then RuntimeError "Empty item passed to SetPopupDataItem !"

	Local curval$=GetPopupDataItem(item$,ppmenu)

	If (Instr(pm\mndata$,"|"+item$+"=")=0)
		pm\mndata$=pm\mndata$+"|"+item$+"="+value$
	Else
		pm\mndata$=Replace(pm\mndata$,"|"+item$+"="+curval$,"|"+item$+"="+value$)
	EndIf 

End Function

; ================================
; Author: gman
; Date: 2004_08_10
; Title: GetPopupDataItem
; Description: 	- addon for the Advance PopupMenu by Ed From Mars (ID: 697)
;			- gets a value from the mndata$ field
;
; Requirements: - Advance PopupMenu, modified to have a mndata$ field in the POPUP_MENU type
;			- gettok function by skn3[ac] (ID: 290)
; ================================
Function GetPopupDataItem$(item$,ppmenu)
	Local pm.POPUP_MENU=Object.POPUP_MENU(ppmenu)
	If (Handle(pm)<=0) Then RuntimeError "Invalid handle passed to GetPopupDataItem !"

	item$=Upper(item$)

	If (item$="") Then RuntimeError "Empty item passed to GetPopupDataItem !"

	Local nCnt=0
	Local temp$=""
	
	Repeat 
		nCnt=nCnt+1
		temp$=gettok(pm\mndata$,nCnt,"|")
		If gettok(temp$,1,"=")=item$ Then Return gettok(temp$,2,"=")
	Until (temp$="")

	Return ""
End Function

; ================================
; Author: gman
; Date: 2004_11_12
; Title: ClearMenu
; Description: 	- addon for the Advance PopupMenu by Ed From Mars (ID: 697)
;				- clears out the POPUP_MENU list
;				- this is a must if you are building dynamic menus that rebuild each time
;
; Requirements: - Advance PopupMenu
; ================================
Function ClearMenu()
	Delete Each POPUP_MENU
End Function
