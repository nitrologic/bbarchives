; ID: 460
; Author: skn3[ac]
; Date: 2002-10-14 06:35:53
; Title: Linked Lists / Dynamic Lists / Threads / Etc
; Description: A Type of Types !!!

;Right Before you start reading, here is how it works. If you didn't know.
;You have 1 Type For your parenst, that stores the locaiton of the start And End 
;of the list. 

;The list is your second Type, which stores all child types from all parents.
;Like a community pool.
;this could be usefull For having groups that you want easy access too, Or anythign that needs
;lists upon lists.


Type parent
	Field 			name$
	Field StartList.child
	Field   EndList.child
End Type

Type child
	Field name$
End Type

Function CreateParent.Parent(name$)
	P.parent = New parent
	P\StartList.Child = Null
	P\EndList.child   = Null
	P\name$=name$
	Return P.parent
End Function

Function CreateChild.Child(P.Parent,Name$)
	;If this is the first child to be added to parent
	;Set the start and end of list to the newly created child    c.child
	;we can tell if there are no items left 
	;by checking if a pointer EG p.parent = null
	If P\StartList.Child = Null Then
		C.Child = New Child
		P\StartList.Child = C.Child
		P\EndList.Child = C.Child
		C\Name$=Name$
	;If there are currently childs in this parent
	;Add the new child After end of list pointer
	;Then set the end of list point to the position of c.child
	;EG parent End of list = Parent end of list +1
	Else
		C.Child = New Child
		Insert C.Child After P\EndList.Child
		P\EndList.Child = C.Child
		C\name$=Name$
	End If
	Return C.Child
End Function

;Create parent 1 with 4 childs
Get.Parent=CreateParent.Parent("skn3")

CreateChild.Child(Get.Parent,"MiniSkn3")
CreateChild.Child(Get.Parent,"something")
CreateChild.Child(Get.Parent,"dodie")
CreateChild.Child(Get.Parent,"acoders")

;To prove its not just readin the list in teh order created
;Set a point to the current get.pointer and see after creating parent 2...
old.parent = get.parent


;create  parent 2 with 3 childs
Get.Parent=CreateParent.Parent("jennifer lopez")

CreateChild.Child(Get.Parent,"p diddy")
CreateChild.Child(Get.Parent,"don king")
CreateChild.Child(Get.Parent,"ronald")


;////////////[ok aftre creating parent 2]//////////////
;we add another To parent 1 ... After parent 2 is actualy the freshest in memory.
CreateChild.Child(old.Parent,"sadsack")


;Loop like normal thru parents
For P.Parent = Each parent
	;To get the loop structure for childs 
	;Set a tmep handle to the stat postion
	Temp.Child = P\StartList.Child
	Print "PARENT :"+P\name$
	
	;Then
	;While the temp handle is different from 'nothing'
	;we do the loop
	While Temp.Child <> Null 
		
		;To determin if this child , in the big list of childs
		;Belongs to the parent::
		;We are already know the child is greater than (in effect) the starting position,
		;so we just need to check its lower than or  equal to end
		
		If Temp.Child <> P\EndList.Child Then
			Print "|_child :"+Temp\name$
		;if it equals end pointer location
		;Exit loop
		ElseIf Temp.Child = P\EndList.Child
			Print "|_child :"+Temp\name$
			Exit
		End If
		Temp.Child = After Temp.Child
	Wend
Next
