; ID: 796
; Author: Beaker
; Date: 2003-09-23 06:53:22
; Title: NextChild(entity) function
; Description: Traverses an entities hierarchy returning all children and grandchildren one after the other.

Function NextChild(ent)
	Local siblingcnt
	If CountChildren(ent)>0
		Return GetChild(ent,1)
	EndIf

	Local foundunused=False
	Local foundent = 0, parent,sibling
	While foundunused=False And ent<>0
		parent = GetParent(ent)
		If parent<>0
			If CountChildren(parent)>1
				If GetChild(parent,CountChildren(parent))<>ent
					For siblingcnt = 1 To CountChildren(parent)
						sibling = GetChild(parent,siblingcnt)
						If sibling=ent
							foundunused = True
							foundent = GetChild(parent,siblingcnt+1)
						EndIf
					Next
				EndIf
			EndIf
		EndIf
		ent = parent
	Wend
	Return foundent
End Function
