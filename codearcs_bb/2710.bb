; ID: 2710
; Author: Yasha
; Date: 2010-05-07 17:24:03
; Title: BlitzClass
; Description: Polymorphic object system using B3D banks

;BlitzClass example

Include "BlitzClass.bb"		;Needs to appear before first use of the classes


;Because classes are defined at runtime they also need to be defined before first use


;Let's copy Wikipedia's polymorphism example


; Class Animal:

Global Animal.BlitzClass = NewClass("Animal")	;You don't actually have to name them but it helps error messages

AddField Animal, "name", BC_STRING		;Strings absolutely need type declarations - other types don't really

Function Animal_New(self.BCObject, name$)
	set_ self, "name", bs_(name)
End Function											; Naming a method "new" will set it as a constructor, and it will be called
Global Animal_New_Ptr = AddMethod(Animal, "New")		; automatically at instantiation (after the super's constructor)

Function Animal_PrintName(self.BCObject)		;Name the actual functions as you like, so long as it's unique (obviously)
	Print "Name: " + us_(get_(self, "name"))
End Function
Global Animal_PrintName_Ptr = AddMethod(Animal, "PrintName")	; It is ++ABSOLUTELY ESSENTIAL++ that the function declarations
												; and method declarations are in the same order! Best to keep them
												; side by side like this.


; Class Cat extends Animal

Global Cat.BlitzClass = Extend(NewClass("Cat"), Animal)

Function Cat_Talk(self.BCObject)
	Print "Meowww!"
End Function
Global Cat_Talk_Ptr = AddMethod(Cat, "Talk")


; Class Dog extends Animal

Global Dog.BlitzClass = Extend(NewClass("Dog"), Animal)

Function Dog_Talk(self.BCObject)
	Print "Arf! Arf!"
End Function
Global Dog_Talk_Ptr = AddMethod(Dog, "Talk")


; Let's test it

Local myAnimal.BCObject[3], i

myAnimal[1] = new_(Cat, bs_("Missie"))
myAnimal[2] = new_(Cat, bs_("Mr. Mistoffelees"))
myAnimal[3] = new_(Dog, bs_("Lassie"))


For i = 1 To 3
	If BC_CheckType(myAnimal[i], Animal)	;Manually check type (totally unnecessary, shouldn't bother but this proves it works)
		
		msg_(myAnimal[i], "PrintName")
		msg_(myAnimal[i], "talk")
		
	EndIf
Next

Print "":Print "Press a key to end..."
WaitKey
End
