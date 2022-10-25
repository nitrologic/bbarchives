; ID: 1917
; Author: Leon Drake
; Date: 2007-02-05 02:22:12
; Title: Attach Body Parts
; Description: Attach Body parts to a skeleton

Graphics3D 800,600,16,2





SetBuffer BackBuffer()


global skeleton,part

Type child

Field child,childcount,parentmesh

End Type

lit = createlight()
cam = createcamer()
skeleton = LoadAnimMesh(tbodyfile$)
addtoskeleton(tpartfile$,ttextfile$)

Repeat
Cls


UpdateWorld()
RenderWorld()
Flip

Until KeyHit(1)


End

Function addtoskeleton(tpartfile$,ttextfile$)

	
		part = LoadAnimMesh(tpartfile$)
		parttex = LoadTexture(ttextfile$,1)
		EntityTexture part,parttex

		PositionEntity part,EntityX(skeleton),EntityY(skeleton),EntityZ(skeleton)
		getchildren(part)
		getchildren(skeleton)
		For c.child = Each child
		If c\parentmesh = part Then
		For x.child = Each child
		If x\parentmesh = skeleton Then
		If EntityName(x\child) = EntityName(c\child) Then
		 DebugLog "Attaching "+EntityName(c\child)+" to "+EntityName(x\child)

		EntityParent(c\child,0)
			
			PositionEntity c\child,EntityX(x\child,True),EntityY(x\child,True),EntityZ(x\child,True)
			
			
			RotateEntity c\child,EntityPitch(x\child,True),EntityYaw(x\child,True),EntityRoll(x\child,True)
			
			EntityParent(c\child,x\child)
			

		Exit
		EndIf
		EndIf
		Next
		EndIf
		
		Next
		

		

	
For c.child = Each child 
Delete c
Next
End Function





Function getchildren(ent)
	entp = ent
	While ent
	c.child = New child
	c\child = ent
	c\parentmesh = entp
	ent = NextChild(ent)
	Wend

End Function

;NextChild() function by Beaker
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
