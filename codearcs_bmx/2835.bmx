; ID: 2835
; Author: matibee
; Date: 2011-04-03 06:19:50
; Title: Simple prefix tree
; Description: A fast look up tree implementation for use in word games

SuperStrict 

Type wordTreeNode
	Field move:Object[26] 	' pointers to the next node for each letter [a-z]
	Field stop:Int[26]  	' denotes a word ending with at this node
	
	Method AddWord( word$ )
		Local ch:Int = word$[0] - 97
		If ( Len (word$ ) = 1 )
			stop[ ch ] = True 
		Else 
			If ( Not move[ ch ] )
				move[ ch ] = New wordTreeNode
			End If 
			wordTreeNode( move[ ch ] ).AddWord( Right( word$, Len( word$ ) - 1 ) )
		End If 
	End Method 
	
	Method CheckWord:Int ( word$ )
		Local ch:Int = word$[0] - 97
		If ( Len ( word$ ) = 1 )
			Return stop[ ch ]
		Else If ( move[ ch ] )
			Return wordTreeNode( move[ ch ] ).CheckWord( Right( word$, Len( word$ ) - 1 ) )
		End If 
		Return False 
	End Method 
	
End Type 

Type wordTree
	Field base:wordTreeNode[26]
	
	Method AddWord( word$ )
		Assert( Len( word$ ) > 1 ) Else "Word too short ~q" + word$ + "~q"
		Local ch:Int = word$[0] - 97
		If ( Not base[ ch ] ) base[ ch ] = New wordTreeNode
		base[ ch ].AddWord( Right( word$, Len( word$ ) - 1 ) )
	End Method 	
	
	Method CheckWord:Int ( word$ )
		If ( Len( word$ ) < 2 ) Return False 
		Local ch:Int = word$[0] - 97
		If ( base[ ch ] ) Return base[ ch ].CheckWord( Right( word$, Len( word$ ) - 1 ) )
		Return False 
	End Method 
	
End Type 

' example usage..
Local tree:wordTree = New wordTree
tree.Addword( "this" )
tree.Addword( "establishment" )
tree.Addword( "produces" )
tree.AddWord( "aardvark" )
tree.AddWord( "meat" )
tree.Addword( "establishments" ) 	' creates only 1 new node in the tree
tree.Addword( "establish" ) 		' creates NO new nodes!

Print tree.CheckWord( "establishment" ) ' check succeeds
Print tree.CheckWord( "aardvar" ) 		' check fails
Print tree.CheckWord( "aardvark" ) 	' check succeeds
