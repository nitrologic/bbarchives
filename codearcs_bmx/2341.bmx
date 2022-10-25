; ID: 2341
; Author: Warpy
; Date: 2008-10-20 23:05:59
; Title: Algebraic Data Types
; Description: yes, seriously. Functional programming invades bmax!

'This is the main functor type.
'The idea is you extend this to make another type whose 'func' method returns
'an array of objects which is the result of evaluating the object.
'You should also make a constructor function which creates the functor.
'A functor object has a field called 'kind' which identifies it for the purposes of
'pattern matching.
Type functor
	Field kind$
	
	Method func:Object[]()
		Return Null
	End Method
	
End Type

'This is an example which represents a tree structure
'It looks like this:
'Tree
'      Empty
'      Leaf (String)
'      Node (Tree, Tree)

Function empty:functor()
	e:FEmpty=New FEmpty
	e.kind="empty"
	Return e
End Function
Type FEmpty Extends functor
	Method func:Object[]()
		Return Null
	End Method
End Type	

Function leaf:functor( txt$ )
	l:FLeaf = New FLeaf
	l.kind="leaf"
	l.txt = txt
	Return l
End Function
Type FLeaf Extends functor
	Field txt$
	Method func:Object[]()
		Return [txt]
	End Method
End Type

Function node:functor(l:functor, r:functor)
	n:FNode = New FNode
	n.kind="node"
	n.l=l
	n.r=r
	Return n
End Function
Type FNode Extends functor
	Field l:functor, r:functor
	
	Method func:Object[]()
		Return [l,r]
	End Method
End Type

'This function works out the depth of a tree
'you pass it the functor at the top of the tree,
'and it works its way down to the bottom,
'and returns the number of steps it took

Function depth( f:functor)
	'first evaluate the current functor
	Local results:Object[]=f.func( ) 
	
	'next use pattern matching to work out what kind of functor
	'this is, and act accordingly
	Select f.kind 
	Case "empty"
		Return 0
	Case "leaf"
		Return 1
	Case "node"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		d1=depth(l)
		d2=depth(r)
		If d1>d2 Return d1+1 Else Return d2+1
	End Select
End Function

'this function prints out all the leaves in the tree
Function printout( f:functor, spaces$="")
	Local results:Object[]=f.func()
	Select f.kind
	Case "empty"
	Case "leaf"
		Print spaces+String(results[0])
	Case "node"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		printout(l, spaces+" ")
		printout(r, spaces+" ")
	End Select
End Function

'this function draws a diagram of the tree
Function diagram( f:functor, depth, spaces$="")
	If depth=0
		Print spaces+"|- ..."
		Return
	EndIf
	Local results:Object[]=f.func()
	Select f.kind
	Case "empty"
		Print spaces+"|-"+"empty"
	Case "leaf"
		Print spaces+"|-"+"leaf "+String(results[0])
	Case "node"
		Print spaces+"|-"+"node"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		diagram(l, depth-1, spaces+"  ")
		diagram(r, depth-1, spaces+"  ")
	End Select
End Function

'an example of the tree structure
f:functor=node( leaf("hello"), node( node( leaf("what"), leaf("there") ), leaf("dude") ) )
Print "~nDIAGRAM"
diagram f, 3

Print "~nDEPTH"
Print depth( f )

Print "~nPRINTOUT"
printout f




'The next example is a list structure
'It looks like this:
'List
'     Nil
'     Stack (String, List)

Function nil:functor()
	n:FNil = New FNil
	n.kind="nil"
	Return New FNil
End Function
Type FNil Extends functor
	Method func:Object[]()
		Return Null
	End Method
End Type

Function stack:functor(txt$, nxt:functor )
	s:FStack=New FStack
	s.kind="stack"
	s.txt=txt
	s.nxt=nxt
	Return s
End Function
Type FStack Extends functor
	Field txt$
	Field nxt:functor
	
	Method func:Object[]()
		Return [Object(txt),Object(nxt)]
	End Method
End Type

'this function prints out everything on the stack between
'the indexes 'start' and 'stop'
Function printstack$( f:functor, start, stop )
	'analyse(args)
	If stop=0
		Return ""
	EndIf
	Local results:Object[]=f.func()
	Select f.kind
	Case "nil"
		Return "END"
	Case "stack"
		otxt$=printstack(functor(results[1]), start-1, stop-1)
		If start<=0 
			otxt=String(results[0])+otxt
		EndIf
		Return otxt
	End Select
End Function

'this function makes a stack by splitting up the given word into letters
Function makestack:functor(word$)
	of:functor=nil()
	While Len(word)
		of=stack( word[Len(word)-1..] , of )
		word=word[..Len(word)-1]
	Wend
	Return of
End Function

'an example stack
Print "~nSTACK"
txt$="hello there how are you today"
f:functor=makestack(txt)
Print printstack( f, 0, 100 )

'check that the printstack function gives the same output as an array slice on the original string
Print printstack( f, 20, 25 )+"   (ADT method)"
Print txt[20..25]+"   (slice)"



'the final example represents a mathematical expression
'It looks like this:
'Op
'    Val number
'    Add (Op, Op)
'    Sub (Op, Op)
'    Mul (Op, Op)
'    Div (Op, Op)

Function val:functor( num )
	v:FVal = New FVal
	v.kind="val"
	v.num=num
	Return v
End Function
Type FVal Extends functor
	Field num

	Method func:Object[]()
		Return [String(num)]
	End Method
End Type

Function add:functor( l:functor, r:functor )
	a:FAdd=New FAdd
	a.kind="add"
	a.l=l
	a.r=r
	Return a
End Function
Type FAdd Extends functor
	Field l:functor, r:functor

	Method func:Object[]()
		Return [l,r]
	End Method
End Type

Function sub:functor( l:functor, r:functor )
	s:FSub = New FSub
	s.kind="sub"
	s.l=l
	s.r=r
	Return s
End Function
Type FSub Extends functor
	Field l:functor, r:functor

	Method func:Object[]()
		Return [l,r]
	End Method
End Type

Function mul:functor( l:functor, r:functor )
	m:FMul = New FMul
	m.kind="mul"
	m.l=l
	m.r=r
	Return m
End Function
Type FMul Extends functor
	Field l:functor, r:functor

	Method func:Object[]()
		Return [l,r]
	End Method
End Type

Function div:functor( l:functor, r:functor )
	d:FDiv = New FDiv
	d.kind="div"
	d.l=l
	d.r=r
	Return d
End Function
Type FDiv Extends functor
	Field l:functor, r:functor

	Method func:Object[]()
		Return [l,r]
	End Method
End Type

Function eval( f:functor )
	Local results:Object[]=f.func()
	Select f.kind
	Case "val"
		Return Int(String(results[0]))
	Case "add"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		Return eval(l) + eval(r)
	Case "sub"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		Return eval(l) - eval(r)
	Case "mul"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		Return eval(l) * eval(r)
	Case "div"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		Return eval(l) / eval(r)
	End Select
End Function

Function render$( f:functor )
	Local results:Object[]=f.func()
	
	Select f.kind
	Case "val"
		Return String(results[0])
	Case "add"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		ltxt$=render(l)
		rtxt$=render(r)
		Return ltxt+" + "+rtxt
	Case "sub"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		ltxt$=render(l)
		rtxt$=render(r)
		Return ltxt+" - "+rtxt
	Case "mul"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		ltxt$=render(l)
		rtxt$=render(r)
		Select l.kind
		Case "add","sub"
			ltxt="( "+ltxt+" )"
		End Select
		Select r.kind
		Case "add","sub"
			rtxt="( "+rtxt+" )"
		End Select
		Return ltxt+" * "+rtxt
	Case "div"
		l:functor=functor(results[0])
		r:functor=functor(results[1])
		ltxt$=render(l)
		rtxt$=render(r)
		Select l.kind
		Case "add","sub"
			ltxt="( "+ltxt+" )"
		End Select
		Select r.kind
		Case "add","sub"
			rtxt="( "+rtxt+" )"
		End Select
		Return ltxt+" / "+rtxt
	End Select
End Function

'an example expression
f:functor=div( val(26) , add( val(12), sub( val(4), mul( val(2), val(7) ) ) ))
Print "~nEVAL"
Print render( f )+" = "+eval( f )
