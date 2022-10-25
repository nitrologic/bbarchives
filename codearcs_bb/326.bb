; ID: 326
; Author: GrahamK
; Date: 2002-05-21 06:41:31
; Title: Dynamic storage for entities
; Description: Store as many entities as you like, instead of being limited by an array

; Dynamic entity storage routines
; Blitztastic 2002

; overall store of entities
Type estore
	Field bank
	Field storecount
End Type

; store for an individual entity
Type estoreref
	Field id
	Field entity
End Type


; create a new store, must be called before using a store, 
; size is how big the initial store must be (by default gives storage for 256 entities) It will expand dynamically if you add more.
Function CreateStore.estore(size=256)
	es.estore = New estore
	es\bank = CreateBank(size*4)
	es\storecount = 0
	Return es
End Function

; delete a store, and all entities it's storing
Function DeleteStore(es.estore)
	i = 1
	While i <= es\storecount
		esr.estoreref = getstoreref(es,i)	
		FreeEntity esr\entity
		Delete esr
		i = i + 1
	Wend
	FreeBank es\bank
End Function

; get a store reference element (internal use)
Function getStoreRef.estoreRef(es.estore,id)
	p = PeekInt(es\bank,(id-1)*4)
	esr.estoreref = Object.estoreref(p)
	Return esr
End Function

; put an store reference element into store (internal use)
Function PutStoreRef(es.estore,esr.estoreref,id)
	If (id*4) >= BankSize(es\bank) Then ResizeBank es\bank,(id+1)*4
	PokeInt(es\bank,(id-1)*4,Handle(esr))
End Function

; add an entity to the store (returns the id of the element for future use)
Function AddEntity2Store(es.estore,entity)
	esr.estoreref = New estoreref
	es\storecount = es\storecount + 1
	esr\id = es\storecount
	esr\entity = entity	
	putstoreref(es,esr,esr\id)
	Return esr\id
End Function

; retrieve an entity from the store, by it's id
Function GetEntityFromStore(es.estore,id)
	esr.estoreref = getstoreref(es,id)
	Return esr\entity
End Function

; end of Dynamic entity storage routines ---------------------------------------


; test the functions
Graphics3D 640,480

piv = CreatePivot()
camera = CreateCamera(piv)
MoveEntity camera,0,0,-15

; create a new store
e.estore = createstore()

For i = 1 To 10
	; create and add an object the store
	s = CreateSphere()
	PositionEntity s,Rnd(-10,10),Rnd(-10,10),Rnd(-10,10)
	addentity2store(e,s)
Next



While Not KeyDown(1)
	TurnEntity piv,0,1,0
	;change color of entity 5
	EntityColor getentityfromstore(e,5),Rnd(255),Rnd(255),Rnd(255)
	RenderWorld
	Flip
Wend

; clean up the store at the end
deletestore(e)

FreeEntity camera
FreeEntity piv

End
