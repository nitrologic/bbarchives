; ID: 1022
; Author: skn3[ac]
; Date: 2004-05-10 10:42:39
; Title: Simple ini functions
; Description: Ini file read/write access, without hte headache

; ============================================================================================================
; example
SetIniItem("settings.ini","group1","x",50)
SetIniItem("settings.ini","group1","y",120)
SetIniItem("settings.ini","group2","open",False)
SetIniItem("settings.ini","group2","show",True)
RuntimeError GetIniItem("data\settings.ini","group1","y")
; ============================================================================================================


; ============================================================================================================
; functions
Function SetIniItem(ini$,groupname$,itemname$,itemvalue$)
	Local group.INI_groups,item.INI_items

	;load ini
	INI_LoadIni(ini$)
	;create group & item where needed
	group = INI_FindGroup(groupname$)
	If group = Null group = INI_CreateGroup(groupname$)
	item  = INI_FindItem(group,itemname$)
	If item = Null 
		item = INI_CreateItem(itemname$,itemvalue$,group)
	Else
		item\value$ = itemvalue$
	End If
	;save ini
	INI_SaveIni(ini$)
	Return True
End Function
Function GetIniItem$(ini$,groupname$,itemname$)
	Local group.INI_groups,item.INI_items
	
	;load ini
	INI_LoadIni(ini$)
	;check group & item exist
	group = INI_FindGroup(groupname$)
	If group = Null Return ""
	item  = INI_FindItem(group,itemname$)
	If item = Null Return ""
	;return item value
	Return item\value$
End Function
Function CountIniGroups(ini$)
	Local group.INI_groups,count
	
	If INI_LoadIni(ini$) = False
		Return 0
	Else
		count = 0
		For group = Each INI_groups
			count = count + 1
		Next
		Return count
	End If
End Function
Function CountIniGroupItems(ini$,name$)
	Local group.INI_groups,item.INI_items,count
	
	If INI_LoadIni(ini$) = False
		Return 0
	Else
		group = INI_FindGroup(name$)
		If group = Null
			Return 0
		Else
			If group\s = Null
				Return 0
			Else
				item  = group\s
				count = 0
				Repeat
					count = count + 1
					If item = group\e Return count
					item = After item
				Forever
			End If
		End If
	End If
End Function
; ============================================================================================================


; ============================================================================================================
; this stuff doesn't need to be touched at all
Type INI_groups
	Field inactive
	Field name$
	Field s.INI_items
	Field e.INI_items
End Type
Type INI_items
	Field inactive
	Field name$
	Field value$
End Type
Function INI_FindGroup.INI_groups(name$)
	Local group.INI_groups
	
	name$ = Lower$(name$)
	
	For group = Each INI_groups
		If group\name$ = name$ Return group
	Next
	Return Null
End Function
Function INI_FindItem.INI_items(group.INI_groups,name$)
	Local item.INI_items
	
	name$ = Lower$(name$)
	
	If group\s <> Null
		item = group\s
		Repeat
			If item\name$ = name$ Return item
			If item = group\e Return Null
			item = After item
		Forever
	End If
	Return Null
End Function
Function INI_CreateGroup.INI_groups(name$)
	Local found,group.INI_groups
	
	name$ = Lower$(name$)
	
	;make sure existing group is not using same name
	For group = Each INI_groups
		If group\name$ = name$ Return Null
	Next
	found = False
	;look for inactive group to reuse
	For group = Each INI_groups
		If group\inactive
			found = True
			Exit
		End If
	Next
	If found = False group = New INI_groups
	group\inactive = False
	group\name$    = name$
	
	Return group
End Function
Function INI_CreateItem.INI_items(name$,value$,group.INI_groups)
	Local found,item.INI_items
	
	name$ = Lower$(name$)
	
	;make sure existing variable is not using same name
	If group\s <> Null
		item = group\s
		Repeat
			If item\name$ = name$
				Return Null
			End If
			If item = group\e Exit
			item = After item
		Forever
	End If
	;look for inactive item to reuse
	found = False
	For item = Each INI_items
		If item\inactive
			Insert item After Last INI_items
			found = True
			Exit
		End If
	Next
	If found = False item = New INI_items
	item\inactive = False
	item\name$    = name$
	item\value$   = value$
	;update group this item belongs too
	If group\s = Null
		group\s = item
	Else
		Insert item After group\e
	End If
	group\e       = item
	
	Return item
End Function
Function INI_LoadIni(path$)
	Local group.INI_groups,item.INI_items,file,getline$,found
	
	;check for error in path
	If FileType(path$) <> 1 Return False
	file = ReadFile(path$)
	If file = False Return False
	
	;clear old groups
	For group = Each INI_groups
		group\inactive = True
		group\name$    = ""
		group\s        = Null
		group\e        = Null
		;clear old items for group
		If group\s <> Null
			item = group\s
			Repeat
				item\inactive = True
				item\name$    = ""
				item\value$   = ""
				If item = group\e Exit
				item = After item
			Forever
		End If
	Next
		
	;load ini file
	group = Null
	item  = Null
	While Eof(file) = False
		getline$ = Trim$(ReadLine$(file))
		Select Left$(getline$,1)
			Case "["     ; ini group
				;create new group
				group = INI_CreateGroup(Mid$(getline$,2,Len(getline$)-2))
			Case ";","#","" ; ini comment, do nothing
			Default      ; ini item
				;create new item
				item = INI_CreateItem(Trim$(Left$(getline$,Instr(getline$,"=")-1)),Right$(getline$,Len(getline$)-Instr(getline$,"=")),group)		
		End Select
	Wend
	CloseFile(file)
	Return True
End Function
Function INI_SaveIni(path$)
	Local file,group.INI_groups,item.INI_items,firstgroup
	
	file       = WriteFile(path$)
	firstgroup = True
	For group = Each INI_groups
		If firstgroup = False
			WriteLine(file,"")
		Else
			firstgroup = False
		End If
		WriteLine(file,"["+group\name$+"]")
		If group\s <> Null
			item = group\s
			Repeat
				WriteLine(file,item\name$+"="+item\value$)
				If item = group\e Exit
				item = After item
			Forever
		End If
	Next
	CloseFile(file)
End Function
; ============================================================================================================
