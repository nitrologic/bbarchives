; ID: 2500
; Author: Warpy
; Date: 2009-06-08 07:14:40
; Title: B-Trees
; Description: like a binary tree, but with more branches

Type btreeenumerator
	Field enum:bnodeenumerator
	Method objectenumerator:bnodeenumerator()
		Return enum
	End Method
End Type

Type bnodeenumerator
	Field b:bnode,i
	
	Method hasnext()
		Return b<>Null
	End Method

	Method nextobject:Object()
		res:bnode=b
		If i>0
			While b=res
				findnext
			Wend
			res=b
		EndIf
		While b=res
			findnext
		Wend
		Return res
	End Method	
	
	Method findnext()
		i:+1
		If b.leaf
			If i>=b.n
				If b.parent
					i=b.parent.childindex(b)
				EndIf
				b=b.parent
				While b And i=b.n
					If b.parent
						i=b.parent.childindex(b)
					EndIf
					b=b.parent
				Wend
			EndIf
		Else
			b=b.children[i]
			While Not b.leaf
				b=b.children[0]
			Wend
			i=0
		EndIf
	End Method
End Type

Type bkeyenumerator Extends bnodeenumerator
	
	Method nextobject:Object()
		res:Object=b.keys[i]
		findnext()
		Return res
	End Method
	
End Type

Type bvalueenumerator Extends bkeyenumerator
	Method nextobject:Object()
		res:Object=b.values[i]
		findnext()
		Return res
	End Method
End Type

Type btree
	Field order	'maximum number of children a tree node can have
	Field root:bnode
	
	Function Create:btree(order)
		If order<3 order=3
		
		bt:btree=New btree
		bt.order=order
		bt.root=bnode.Create(order,Null,True)
		Return bt
	End Function
	
	Method nodes:btreeenumerator()
		enum:bnodeenumerator=New bnodeenumerator
		b:bnode=root
		While Not b.leaf
			b=b.children[0]
		Wend
		enum.b=b
		tenum:btreeenumerator=New btreeenumerator
		tenum.enum=enum
		Return tenum
	End Method

	Method keys:btreeenumerator()
		enum:bnodeenumerator=New bkeyenumerator
		b:bnode=root
		While Not b.leaf
			b=b.children[0]
		Wend
		enum.b=b
		tenum:btreeenumerator=New btreeenumerator
		tenum.enum=enum
		Return tenum
	End Method
	
	Method values:btreeenumerator()
		enum:bnodeenumerator=New bvalueenumerator
		b:bnode=root
		While Not b.leaf
			b=b.children[0]
		Wend
		enum.b=b
		tenum:btreeenumerator=New btreeenumerator
		tenum.enum=enum
		Return tenum
	End Method
	
	Method contains(key:Object)	'does tree contain given key?
		u:bnode=search(key,i)
		If i<u.n And key.compare(u.keys[i])=0 Return True
	End Method
	
	Method valueforkey:Object(key:Object)
		u:bnode=search(key,i)
		If i=u.n Or key.compare(u.keys[i])<>0 Return Null
		Return u.values[i]
	End Method
	
	Method search:bnode(key:Object, i Var)	'find node which does/should stor given key, and put its index in parameter 'i'
		u:bnode=root
		While Not u.leaf
			i=u.index(key)
			If i<u.n And key.compare(u.keys[i])=0
				Return u
			EndIf
			u=u.children[i]
		Wend
		i=u.index(key)
		Return u
	End Method
	
	Method insert(key:Object,value:Object)
		u:bnode=search(key,i)	'find appropriate node and index to insert key in
		
		If i<u.n And key.compare(u.keys[i])=0	'key already in this leaf, so just change value
			u.values[i]=value
			Return
		EndIf
		
		For c=u.n To i+1 Step -1	'shift bigger keys along
			u.keys[c]=u.keys[c-1]
			u.values[c]=u.values[c-1]
		Next
		
		u.keys[i]=key	'insert new key
		u.values[i]=value
		u.n:+1
		
		If u.n=order	'if node is full, we need to split it
			t=(order+1)/2-1		'median index
			While u.n=order		'while node we're looking at is full
			
				v:bnode=bnode.Create(order,u.parent,u.leaf)	'create a new node to store the big half of the keys
				
				For c=t+1 To order-1	'fill in its keys
					v.keys[c-t-1]=u.keys[c]
					v.values[c-t-1]=u.values[c]
				Next
				For c=t+1 To order	'fill in its children
					v.children[c-t-1]=u.children[c]
					If u.children[c]
						u.children[c].parent=v
					EndIf
				Next
				
				u.n=t	'nodes now have order-1 keys in total, with left node having t keys
				v.n=order-1-t
				
				If Not u.parent	'if this is the root, it doesn't have a parent, so make one
					root=bnode.Create(order,Null,0)
					u.parent=root
					v.parent=root
					root.children[0]=u
				EndIf
				
				p:bnode=u.parent
				v.parent=p	'new node's parent is same as left node
				
				i=p.index(u.keys[t])	'find index to insert key which is being pushed up

				v._left=u		'update sibling pointers
				v._right=u._right
				u._right=v
				
				If Not u.leaf
					u.children[u.n]._right=Null
					v.children[0]._left=Null
				EndIf
				
				For c=p.n To i+1 Step -1	'shift keys along
					p.keys[c]=p.keys[c-1]
					p.values[c]=p.values[c-1]
					p.children[c+1]=p.children[c]
				Next
				
				p.keys[i]=u.keys[t]	'insert ascending key in parent
				p.values[i]=u.values[t]
				p.children[i+1]=v		'insert v as new child
				p.n:+1	'p has one more key
				p.leaf=0	'p is not a leaf any more, if it was one
				u=p	'now we want to check if p is full
			Wend
		EndIf
	End Method
	
	Method remove(key:Object)
		u:bnode=search(key,i)	'find node/index of key
		If i=u.n	'key is not in the tree
			Return
		EndIf
		
		If u.leaf
			removeleaf(u,i)	'can simply delete key
		Else
			ch:bnode=u.children[i+1]
			While Not ch.leaf 	'find successor
				ch=ch.children[0]
			Wend
			u.keys[i]=ch.keys[0]	'replace key by its successor
			u.values[i]=ch.values[0]
			removeleaf(ch,0)	'remove successor from child node
		EndIf
	End Method
	
	Method removeleaf(u:bnode,i)
		key:Object=u.keys[i]
		For c=i To u.n-2	'shift bigger keys left
			u.keys[c]=u.keys[c+1]
			u.values[c]=u.values[c+1]
		Next
		u.n:-1	'node has one fewer key

		t=(order+1)/2-1	'minimum size of a node
		
		While u<>root And u.n<t	'if node is too small
			p:bnode=u.parent
			i=p.childindex(u)		'find index of u in parent's children
			If i<p.n	'if u has a right sibling
				r:bnode=p.children[i+1]
				If r.n>t	'borrow a key from right sibling
					u.keys[u.n]=p.keys[i]	'add parent's separator value to the end of u
					u.values[u.n]=p.values[i]
					If Not r.leaf
						u.children[u.n+1]=r.children[0]	'u steals r's first child
						u.children[u.n+1].parent=u	'u is now stolen child's parent
						
						u.children[u.n]._right=u.children[u.n+1]	'update sibling pointers
						u.children[u.n+1]._left=u.children[u.n]
						u.children[u.n+1]._right=Null
						r.children[1]._left=Null
					EndIf
					u.n:+1	'u has one more key
					p.keys[i]=r.keys[0]	'insert right sibling's first key as new separator value
					p.values[i]=r.values[0]
					For c=1 To r.n-1	'shift right sibling's keys left
						r.keys[c-1]=r.keys[c]
						r.values[c-1]=r.values[c]
					Next
					For c=1 To r.n	'shift right sibling's children left
						r.children[c-1]=r.children[c]
					Next
					r.n:-1	'right sibling has one less key
					Return
				EndIf
			EndIf
			If i>0	'if u has a left sibling
				l:bnode=p.children[i-1]
				If l.n>t	'borrow a key from left sibling
					For c=u.n To 1 Step -1	'shift u's keys right
						u.keys[c]=u.keys[c-1]
						u.values[c]=u.values[c-1]
					Next
					For c=u.n+1 To 1 Step -1	'shift u's children right
						u.children[c]=u.children[c-1]
					Next
					u.n:+1
					u.keys[0]=p.keys[i-1]	'insert parent's separator value at the start of u
					u.values[0]=p.values[i-1]
					If Not u.leaf
						u.children[0]=l.children[l.n]	'u steals l's last child
						u.children[0].parent=u		'u is now stolen child's parent
						
						u.children[0]._left=Null	'update sibling pointers
						u.children[0]._right=u.children[1]
						u.children[1]._left=u.children[0]
						l.children[l.n-1]._right=Null
					EndIf
					p.keys[i-1]=l.keys[l.n-1]	'insert left sibling's last key as new separator value
					p.values[i-1]=l.values[l.n-1]
					l.n:-1	'left sibling has one less key
					Return
				EndIf
			EndIf
			
			If i=p.n	'if u does not have a right sibling, pretend we're merging the left sibling with u
				i:-1
				u=p.children[i]
			EndIf
			
			'no siblings have enough keys, so merge right
			u.keys[u.n]=p.keys[i]	'insert separator on the end of this node
			u.values[u.n]=p.values[i]
			u.n:+1
			
			r:bnode=p.children[i+1]
			For c=0 To r.n-1		'add right sibling's keys to the end of this node
				u.keys[u.n+c]=r.keys[c]
				u.values[u.n+c]=r.values[c]
			Next
			If Not r.leaf
				For c=0 To r.n	'add right sibling's children to this node
					u.children[u.n+c]=r.children[c]
					r.children[c].parent=u
				Next
				
				u.children[u.n-1]._right=u.children[u.n]	'update sibling pointers
				u.children[u.n]._left=u.children[u.n-1]
			EndIf
			u.n:+r.n
			
			u._right=r._right
			
			For c=i+1 To p.n-1	'shift parent's keys left
				p.keys[c-1]=p.keys[c]
				p.values[c-1]=p.values[c]
			Next
			For c=i+2 To p.n	'shift parent's children left
				p.children[c-1]=p.children[c]
			Next
			p.n:-1	'p has one less key
			

			u=p	'now we want to check if p is too small
			
		Wend
		If p=root And p.n=0
			root=p.children[0]
			root.parent=Null
		EndIf
	End Method
	
	Method repr$(u:bnode=Null,spaces$="")	'represent this tree in text form
		If Not u u=root
		out$=spaces+u.repr()

		If u.leaf=0
			For i=0 To u.n
				out:+"~n"+repr(u.children[i],spaces+" `")
				If u.children[i].parent<>u out:+"!!!!!!!"+u.parent.repr()
			Next
		EndIf
		Return out
	End Method
End Type

Type bnode
	Field leaf			'is it a leaf?
	Field n			'number of keys
	Field parent:bnode	'parent node
	Field keys:Object[]	'array of keys (separation values)
	Field values:Object[]	'array of values corresponding to keys
	Field children:bnode[]	'array of child nodes
	
	Field _left:bnode,_right:bnode	'pointers to siblings
	
	Function Create:bnode(order,parent:bnode,leaf)	'order=2t-1
		bn:bnode=New bnode
		bn.keys=New Object[order]
		bn.values=New Object[order]
		bn.children=New bnode[order+1]
		bn.parent=parent
		bn.leaf=leaf
		Return bn
	End Function
	
	Method index(key:Object)	'key index corresponding to given key
		i=0
		While i<n And key.compare(keys[i])>0
			i:+1
		Wend
		Return i
	End Method
	
	Method childindex(b:bnode)
		For i=0 To n
			If children[i]=b Return i
		Next
	End Method
	
	Method repr$()	'represent keys list as a nice string
		out$="["
		For i=0 To n-1
			If i out:+", "
			out:+String(keys[i])+":"+String(values[i])
		Next
		out:+"]"
		Return out
	End Method
	
End Type



'EXAMPLE

'create a tree of order 4
bt:btree=btree.Create(4)

'fill the tree with the letters of the alphabet
For i=0 To 25
	ch$=Chr(Asc("a")+i)
	bt.insert ch,Upper(Ch)
Next

'remove some keys at random
Print "-----------------------------~n"
For i=0 To 6
	c=Rand(0,25)
	ch$=Chr(Asc("a")+c)
	bt.remove ch
Next

Print "TREE"
Print bt.repr()+"~n~n"

Print "KEYS: VALUES"
For ch$=EachIn bt.keys()
	Print ch+": "+String(bt.valueforkey(ch))
Next

Print "ALL VALUES"
For ch=EachIn bt.values()
	Print ch
Next

Print "ALL NODES"
For b:bnode=EachIn bt.nodes()
	Print b.repr()
Next
