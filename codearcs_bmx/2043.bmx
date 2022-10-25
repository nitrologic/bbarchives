; ID: 2043
; Author: Otus
; Date: 2007-06-24 14:00:29
; Title: Sparse array Tree
; Description: Stores sparse arrays more efficiently in a tree-like data structure.

'The T4Tree stores a sparse two dimensional
' array in a tree-like structure, where the
' leaves store the array values.
'By Otus,
' released to public domain.

SuperStrict

'Rem	'Comment this line to test
Framework BRL.Basic
Import BRL.Random
Rem
End Rem


'Private

Function POW:Int(a:Int, b:Int)
	'This is needed, because ^-operator
	' doesn't work well with integers.
	'Replace with a faster alternative,
	' if you can find one.
	Local a2:Int = a
	b:-1
	While b
		a2:*a
		b:-1
	Wend
	Return a2
End Function

Type TNode
	'A node points to 4 objects below it,
	' which are either other nodes, or 
	' values, depending on the depth.
	'Nodes which don't point to anything
	' don't (shouldn't) exist.
	
	Field ul:Object, ur:Object
	Field bl:Object, br:Object
	
	Method Get:Object(x:Int, y:Int, d:Int=1)
		If d=1
			If x
				If y Return br Else Return bl
			Else
				If y Return ur Else Return ul
			End If
		End If
		If x<d
			If y<d
				If ul=Null
					Return Null
				Else
					Return TNode(ul).Get(x, y, d/2)
				End If
			Else
				If bl=Null
					Return Null
				Else
					Return TNode(bl).Get(x, y - d, d/2)
				End If
			End If
		Else
			If y<d
				If ur=Null
					Return Null
				Else
					Return TNode(ur).Get(x - d, y, d/2)
				End If
			Else
				If br=Null
					Return Null
				Else
					Return TNode(br).Get(x - d, y - d, d/2)
				End If
			End If
		End If
	End Method
	
	Method Set(o:Object, x:Int, y:Int, d:Int=1)
		If d=1
			If x
				If y br=o Else bl=o
				Return
			Else
				If y ur=o Else ul=o
				Return
			End If
		End If
		If x<d
			If y<d
				If ul=Null Then ul = New TNode
				TNode(ul).Set(o,x, y, d/2)
			Else
				If bl=Null Then bl = New TNode
				TNode(bl).Set(o,x, y - d, d/2)
			End If
		Else
			If y<d
				If ur=Null Then ur = New TNode
				TNode(ur).Set(o,x - d, y, d/2)
			Else
				If br=Null Then br = New TNode
				TNode(br).Set(o,x - d, y - d, d/2)
			End If
		End If
	End Method
	
	Method Remove:Byte(x:Int, y:Int, d:Int=1)
		If d=1
			If x
				If y br=Null Else bl=Null
			Else
				If y ur=Null Else ul=Null
			End If
			If ul=Null And ur=Null And bl=Null And br=Null Return 1 Else Return 0
		End If
		
		If x<d
			If y<d
				If ul=Null Then ul = New TNode
				If TNode(ul).Remove(x, y, d/2)
					ul=Null
					If ur=Null And bl=Null And br=Null Return 1 Else Return 0
				End If
			Else
				If bl=Null Then bl = New TNode
				If TNode(bl).Remove(x, y - d, d/2)
					bl=Null
					If ur=Null And ul=Null And br=Null Return 1 Else Return 0
				End If
			End If
		Else
			If y<d
				If ur=Null Then ur = New TNode
				If TNode(ur).Remove(x - d, y, d/2)
					ur=Null
					If ul=Null And bl=Null And br=Null Return 1 Else Return 0
				End If
			Else
				If br=Null Then br = New TNode
				If TNode(br).Remove(x - d, y - d, d/2)
					br=Null
					If ur=Null And bl=Null And ul=Null Return 1 Else Return 0
				End If
			End If
		End If
	End Method

End Type

'Public

Type T4Tree Extends TNode
	
	Field depth:Short
	
	Method Get:Object(x:Int, y:Int, d:Int=0)
		'Get value at indices. d parameter
		' is legacy of the base type method.
		Return Super.Get(x,y,POW(2,depth-1))
	End Method
	
	Method Set(o:Object, x:Int, y:Int, d:Int=0)
		'Get value at indices to o. d parameter
		' is again legacy of the base type method.
		If o=Null
			Super.Remove(x,y,POW(2,depth-1))
		Else
			Super.Set o, x, y, POW(2,depth-1)
		End If
	End Method
	
	Method Remove:Byte(x:Int, y:Int, d:Int=1)
		Super.Remove(x,y,POW(2,depth-1))
	End Method
	
	Function Create:T4Tree(width:Int, height:Int=0)
		Local t:T4Tree = New T4Tree
		width=Max(width, height)
		t.depth=Max(Ceil(Log(width)/Log(2)),1)
		Return t
	End Function
	
	Function FromArray:T4Tree(array:Object[,])
		Local dim:Int[] = array.Dimensions()
		Local size:Int = Max(dim[0],dim[1])
		
		Local t:T4Tree = New T4Tree
		t.depth=Max(Ceil(Log(size)/Log(2)),1)
		
		For Local i:Int = 0 Until dim[0]
			For Local j:Int = 0 Until dim[1]
				If Not array[i,j]=Null t.Set(array[i,j],i,j)
			Next
		Next
		
		Return t
	End Function
	
End Type

'Rem	'Comment this line to test
SeedRnd MilliSecs()

Local s:String="1"

Const tsize:Int = 2000

Local mem:Int = GCMemAlloced()
Local array:Object[tsize,tsize]
Local mem0:Int = GCMemAlloced() - mem

For Local i:Int = 0 Until tsize
	For Local j:Int = 0 Until tsize
		If Rand(100) = 1 Then array[i,j]=s	'Store a '1' in approx. 1/100 cells
	Next
Next

GCCollect()
mem = GCMemAlloced()
Local t:T4Tree = T4Tree.FromArray(array)
GCCollect()
Local mem1:Int = GCMemAlloced() - mem

Print "Array took: "+mem0+"B, T4Tree took: "+mem1+"B"

For Local i:Int = 1 To 10
	Local x:Int = Rand(0,tsize-1), y:Int = Rand(0,tsize-1)
	Print String(array[x,y])+" - "+String(t.Get(x,y))
Next

For Local i:Int = 1 To 1000
	t.Remove(Rand(0,tsize-1),Rand(0,tsize-1))
Next
GCCollect()
mem1=GCMemAlloced()-mem

Print "After some removal takes: "+mem1+"B"

Rem
End Rem
