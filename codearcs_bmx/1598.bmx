; ID: 1598
; Author: RepeatUntil
; Date: 2006-01-15 00:33:10
; Title: CollideImage2
; Description: Function similar to CollideImage, but allows collision of images in the same layer

' Function checking the collision, but will remove the testedObject from the list of collision. This allows
' collision for objects of the same layer.
Function CollideImage2:Object[](image:TImage, x, y, frame, collideMask%, writeMask%,  id:Object, testedObject:Object=Null)
	Local collidedObjects:Object[] = CollideImage(image, x, y, frame, collideMask, writeMask, id)
	' Test if the object is present in the collision list
	Local testedObjectPresent:Byte = False
	For Local collidedObject:Object = EachIn collidedObjects
		If collidedObject = testedObject Then testedObjectPresent = True
	Next
	' Create the new array we will return
	Local dim
	If testedObjectPresent Then dim = collidedObjects.length - 1 Else dim = collidedObjects.length
	Local collidedObjectsReturned:Object[dim]
	' Remove the object from the collision list if it's present
	Local i = 0
	For Local collidedObject:Object = EachIn collidedObjects
		If collidedObject <> testedObject Then 
			collidedObjectsReturned[i] = collidedObject
			i:+1
		EndIf
	Next
	Return collidedObjectsReturned
End Function
