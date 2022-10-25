; ID: 820
; Author: Ken Lynch
; Date: 2003-11-04 06:36:45
; Title: Entity properties without using types
; Description: Entity properties without using types

;================================================
;
; Entity Property Library
;
; (c)2003 Ken Lynch
;
;================================================

;
; SetProperty entity,property$,value$
;
Function SetProperty(entity, property$, value$)
	Local props, p, v

	props = FindChild(entity, "properties")
	If props = 0 Then
		props = CreatePivot(entity)
		NameEntity props, "properties"
	End If
	p = FindChild(props, property)
	If p = 0 Then
		p = CreatePivot(props)
		NameEntity p, property
	End If
	v = GetChild(p, 1)
	If v = 0 Then v = CreatePivot(p)
	NameEntity v, value
End Function

;
; value$ = GetProperty$(entity,property$)
;
Function GetProperty$(entity, property$)
	Local props,p, v, value$

	props = FindChild(entity, "properties")
	If props > 0 Then
		p = FindChild(entity, property)
		If p > 0 Then
			v = GetChild(p, 1)
			If v > 0 Then value$ = EntityName(v)
		End If
	End If
	Return value$
End Function

;
; DeleteProperty entity,property$
;
Function DeleteProperty(entity, property$)
	Local props,p

	props = FindChild(entity, "properties")
	If props > 0 Then
		p = FindChild(entity, property)
		If p > 0 Then FreeEntity p
	End If
End Function
