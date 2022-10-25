; ID: 3113
; Author: Yasha
; Date: 2014-03-09 14:52:17
; Title: Functional programming primitives
; Description: Imitate a Haskell/ML style in BlitzMax

' Helper functions and types for imitating a Haskell style in BlitzMax

SuperStrict

' Delegates: curry-able functions
' (slightly unsafe: don't get the argument count wrong, don't use with primitive types)
Type TDelegate
	Const CMAX:Int = 7, ABUF:Int = 2	'Max of 7 arguments may be curried, 6 if using call2
	Field _cargs:Object[], _ctr:Int
	Field f:Object(_c0:Object, _c1:Object, _c2:Object, _c3:Object, _c4:Object, _c5:Object, _c6:Object, _c7:Object)
	
	Function Make:TDelegate(f:Byte Ptr)	'Constructor, accepts any function pointer operating on objects
		Local d:TDelegate = New TDelegate
		d.f = f ; d._cargs = New Object[CMAX + ABUF] ; d._ctr = 0
		Return d
	End Function
	
	Method curry:TDelegate(a:Object)
		If _ctr >= CMAX Then RuntimeError "Cannot curry more arguments onto " + ToString() + "; out of room"
		Local r:TDelegate = Make(f)
		r._ctr = _ctr ; r._cargs = New Object[CMAX + ABUF]
		For Local a:Int = 0 Until _ctr
			r._cargs[a] = _cargs[a]
		Next
		r._cargs[_ctr] = a ; r._ctr :+ 1
		Return r
	End Method
	Method compose:TDelegate(g:TDelegate)
		Local c:TComposedDelegate = New TComposedDelegate
		c.f = Self ; c.g = g
		Return c
	End Method
	
	Method call:Object(a:Object)
		_cargs[_ctr] = a
		Local ret:Object = f(_cargs[0], _cargs[1], _cargs[2], _cargs[3], _cargs[4], _cargs[5], _cargs[6], _cargs[7])
		_cargs[_ctr] = Null ; Return ret	'delegate should not gcretain its argument
	End Method
	Method call2:Object(a0:Object, a1:Object)
		_cargs[_ctr] = a0 ; _cargs[_ctr + 1] = a1
		Local ret:Object = f(_cargs[0], _cargs[1], _cargs[2], _cargs[3], _cargs[4], _cargs[5], _cargs[6], _cargs[7])
		_cargs[_ctr] = Null ; _cargs[_ctr + 1] = Null ; Return ret
	End Method
End Type

Private
Type TComposedDelegate Extends TDelegate
	Field f:TDelegate, g:TDelegate
	Method call:Object(a:Object)
		Return f.call(g.call(a))
	End Method
	Method call2:Object(a0:Object, a1:Object)
		Return f.call(g.call2(a0, a1))
	End Method
	Method curry:TDelegate(a:Object)
		Return f.compose(g.curry(a))
	End Method
End Type
Public


' Functional single-linked list
' All of the operations that don't take Ints can be used as delegates for functional composition
Type ConsList
	Field val:Object, nx:ConsList
	Function Cons:ConsList(val:Object, nx:ConsList)
		Local c:ConsList = New ConsList
		c.val = val ; c.nx = nx
		Return c
	End Function
	Function FromArray:ConsList(a:Object[])
		Local c:ConsList = Null
		For Local e:Int = a.Length - 1 To 0 Step -1
			c = Cons(a[e], c)
		Next
		Return c
	End Function
	Function ToArray:Object[](l:ConsList)
		Local a:Object[] = New Object[ConsList.Length(l)]
		For Local e:Int = 0 Until a.Length
			a[e] = l.val ; l = l.nx
		Next
		Return a
	End Function
	
	Function Map:ConsList(f:TDelegate, l:ConsList)
		Local r:ConsList = Cons(Null, Null), ret:ConsList = r
		While l
			r.nx = Cons(f.call(l.val), Null) ; r = r.nx ; l = l.nx
		Wend
		Return ret.nx
	End Function
	Function ZipWith:ConsList(f:TDelegate, l:ConsList, r:ConsList)
		Local d:ConsList = Cons(Null, Null), ret:ConsList = d
		While l And r
			d.nx = Cons(f.call2(l.val, r.val), Null) ; d = d.nx ; r = r.nx ; l = l.nx
		Wend
		Return ret.nx
	End Function
	Function FoldL:Object(f:TDelegate, st:Object, l:ConsList)
		While l
			st = f.call2(st, l.val) ; l = l.nx
		Wend
		Return st
	End Function
	Function FoldL1:Object(f:TDelegate, l:ConsList)
		If l Then Return FoldL(f, l.val, l.nx) Else Return Null
	End Function
	Function FoldR:Object(f:TDelegate, st:Object, l:ConsList)
		Return FoldL(f, st, Reverse(l))
	End Function
	Function Reverse:ConsList(l:ConsList)
		Local r:ConsList = Null
		While l
			r = Cons(l.val, r) ; l = l.nx
		Wend
		Return r
	End Function
	Function Length:Int(l:ConsList)
		Local i:Int = 0
		While l
			i :+ 1 ; l = l.nx
		Wend
		Return i
	End Function
	Function Index:Object(l:ConsList, i:Int)
		While i
			If l Then l = l.nx Else Return Null
			i :- 1
		Wend
		Return l.val
	End Function
	Function Take:ConsList(l:ConsList, i:Int)
		Local r:ConsList = Cons(Null, Null), ret:ConsList = r
		While i And (l <> Null)
			r.nx = Cons(l.val, Null) ; r = r.nx ; l = l.nx ; i :- 1
		Wend
		Return ret.nx
	End Function
	Function Drop:ConsList(l:ConsList, i:Int)
		While i And (l <> Null)
			l = l.nx ; i :- 1
		Wend
		Return l
	End Function
	Function Last:Object(l:ConsList)
		If l = Null Then Return Null
		While l.nx ; l = l.nx ; Wend
		Return l.val
	End Function
End Type


' Pattern matching "unpackers"
Type RefCell Abstract
	Global GetCons:String = "GETCONS"
	Global Any:RefCell = New RefCell_Any
	Field _:Object
	Function Make:RefCell() Return New RefCell_Recv End Function
	Function unCons:RefCell(me:Object, nx:RefCell)
		If nx = Null Then nx = unVal(Null)
		Local r:RefCell_Cons = New RefCell_Cons ; r._ = me ; r.nx = nx ; Return r
	End Function
	Function unVal:RefCell(me:Object)
		Local r:RefCell = New RefCell_Val ; r._ = me ; Return r
	End Function
	Function unType:RefCell(me:Object, chk:Object(_:Object))
		Local r:RefCell_Type = New RefCell_Type ; r._ = me ; r.chk = chk ; Return r
	End Function
	Function unMaybe:RefCell(me:RefCell)
		RefCell_Recv(me).nullable = True ; Return me
	End Function
	Method match:Int(me:Object) Abstract
End Type

Private
Type RefCell_Val Extends RefCell
	Method match:Int(me:Object)
		If me Then Return me.Compare(_) = 0 Else Return me = _
	End Method
End Type
Type RefCell_Type Extends RefCell
	Field chk:Object(_:Object)
	Method match:Int(me:Object)
		If RefCell(_).match(me) Then Return chk(me) <> Null Else Return 0
	End Method
End Type
Type RefCell_Cons Extends RefCell
	Field nx:RefCell
	Method match:Int(me:Object)
		If me = Null Then Return 0
		Local c:ConsList = ConsList(me) ; If c = Null Then c = ConsList(me.SendMessage(RefCell.GetCons, Null))
		If c Then Return RefCell(_).match(c.val) And nx.match(c.nx) Else Return 0
	End Method
End Type
Type RefCell_Recv Extends RefCell_Cons
	Field nullable:Int
	Method match:Int(me:Object) _ = me ; Return nullable Or (_ <> Null) End Method
End Type
Type RefCell_Any Extends RefCell
	Method match:Int(_:Object) Return 1 End Method
End Type
Public
