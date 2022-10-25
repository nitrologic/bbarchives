; ID: 2012
; Author: jfk EO-11110
; Date: 2007-05-14 18:11:11
; Title: EntityExists(), iterate entities
; Description: Iterate trough all entities and check for existance

[code]
; This code will automaticly iterate trough all entities. Based on an idea by
; Halo, this one works with a simple "decls" userlib.


; Userlib declaration: RTLMoveMemory2 from the kernel32.dll is used,
; you need at least the following 2 lines in your kernel32.decls:

; .lib "kernel32.dll" 
; RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"


; Note: The pivot that is created in the init section must be the first entity
; that was created or loaded. You may have to recreate it after a "ClearWorld()"


Graphics3D 800,600,32,2
SetBuffer BackBuffer()

camera=CreateCamera()



; init Cycles entity iteration------------------------
Global Cycle_bank=CreateBank(16)
Const  Cycle_NextEntity=4
Const  Cycle_LastEntity=8
Global Cycle_FirstEntity=CreatePivot()
Global Cycle_CurrentEntityPointer=Cycle_FirstEntity
;----------------------------------------------------




; create some test entities
For i=0 To 7
 dudu=CreateCube()
 NameEntity dudu,Chr$(Rand(65,90))+""+Rand(1000)
Next



; how to cycle trough all entities:
While MoreEntities()
 entity= NextEntity()
 Print "Handle: " + entity
 Print "Name:   " + EntityName$(entity)
 Print "Class:  " + EntityClass$(entity)
 Print "-------------------------------"
Wend



; How to use EntityExists():

;freddy=CreatePivot() ; try unrem
Print
Print EntityExists(freddy)

WaitKey()
End






Function MoreEntities() ; check if there are further entities
 RtlMoveMemory2(Cycle_bank,Cycle_CurrentEntityPointer+Cycle_NextEntity,4)
 If PeekInt(Cycle_bank,0)<>0
  Return True
 Else
  Cycle_CurrentEntityPointer=Cycle_FirstEntity
 EndIf
End Function


Function NextEntity()
 Local entity
 RtlMoveMemory2(Cycle_bank,Cycle_CurrentEntityPointer+Cycle_NextEntity,4)
 entity=PeekInt(Cycle_bank,0)
 Cycle_CurrentEntityPointer = entity
 Return entity
End Function


Function EntityExists(entity)
 While MoreEntities()
  If NextEntity()=entity Then Return True
 Wend
End Function

[/code]
