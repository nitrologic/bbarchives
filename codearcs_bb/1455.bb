; ID: 1455
; Author: Techlord
; Date: 2005-08-27 15:32:09
; Title: Binary Decision Tree
; Description: Binary Decision Tree AI Algorithm

;//TreeNode 

Type TreeNode
	Field m_strQuestOrAns$;
	Field m_iNodeID%
	Field m_pYesBranch.TreeNode
	Field m_pNoBranch.TreeNode
End Type

Function TreeNodeNew.TreeNode(nodeID%=0,newQorA$="");
	;set the objects pointers To Null in Default constructor
	this.TreeNode = New TreeNode
	this\m_iNodeID = nodeID
	this\m_strQuestOrAns = newQorA
	this\m_pYesBranch = Null
	this\m_pNoBranch= Null
	Return this
End Function

Function TreeNodeDelete(this.TreeNode)
	Delete this
End Function

;// DecisionTree

Type DecisionTree
	Field m_pRootNode.TreeNode;
End Type

Function DecisionTreeRemoveNode(this.DecisionTree,node.TreeNode)
	If(node <> Null)
		If(node\m_pYesBranch <> Null) DecisionTreeRemoveNode(this,node\m_pYesBranch);
		If(node\m_pNoBranch <> Null) DecisionTreeRemoveNode(this,node\m_pNoBranch);
		Print("deleting node "+node\m_iNodeID);
		Delete node;
	EndIf
End Function

Function DecisionTreeOutputBinaryTree(this.DecisionTree,tag$,currentNode.TreeNode)
	If(currentNode = Null) Return
		
	Write("[" + tag$ + "] node id = ")
	Write(currentNode\m_iNodeID)
	Write(", question/answer = ")
	Print(currentNode\m_strQuestOrAns)
	
	; Go down yes branch
	DecisionTreeOutputBinaryTree(this,tag$+".1",currentNode\m_pYesBranch);
	; Go down no branch
	DecisionTreeOutputBinaryTree(this,tag$+".2",currentNode\m_pNoBranch);
End Function

Function DecisionTreeOutput(this.DecisionTree)
	DecisionTreeOutputBinaryTree(this,"1", this\m_pRootNode);
End Function

Function DecisionTreeAskQuestion(this.DecisionTree,node.Treenode)
	Print(node\m_strQuestOrAns + " (enter yes or no)")
	answer$=Input()
	If(answer = "yes")
		DecisionTreeQueryBinaryTree(this,node\m_pYesBranch)
	ElseIf(answer = "no")
		DecisionTreeQueryBinaryTree(this,node\m_pNoBranch)
	Else
		Print("Error please answer yes or no.")
		DecisionTreeAskQuestion(this,node)
	EndIf
End Function

Function DecisionTreeQuery(this.DecisionTree)
	DecisionTreeQueryBinaryTree(this,this\m_pRootNode);
End Function

Function DecisionTreeQueryBinaryTree(this.DecisionTree,currentnode.TreeNode)
	If(currentNode\m_pYesBranch = Null)
		;If both the yes And no branch pointers are Null 
		;the tree is at a decision outcome state so output
		;the String
		If(currentNode\m_pNoBranch = Null)
			Print(currentNode\m_strQuestOrAns)
		Else
			Print("Missing yes branch at " + currentNode\m_strQuestOrAns + " question")
		EndIf		
		Return
	EndIf
	If(currentNode\m_pNoBranch = Null)
		Print("Missing no branch at " + currentNode\m_strQuestOrAns + " question")
		Return
	EndIf
	;otherwise Default To asking the question at the currentNode
	DecisionTreeAskQuestion(this,currentNode);
End Function

Function DecisionTreeAddYesNode(this.DecisionTree,existingNodeID%,newNodeID%,newQorA$)
	;If you dont have a root node you cant add another node
	If(this\m_pRootNode = Null)
		Write("Error - no root node in AddYesNode()")
		Return
	EndIf
	;otherwise query tree And add node
	If(DecisionTreeSearchTreeAndAddYesNode(this,this\m_pRootNode,existingNodeID%,newNodeID%,newQorA$))
		Write("Added 'yes' node")
		Write(newNodeID)
		Write(" onto 'yes' branch of node ")
		Print(existingNodeID)
	Else
		Write("'yes' Node ")
		Write(existingNodeID)
		Print(" not found")
	EndIf
End Function

Function DecisionTreeAddNoNode(this.DecisionTree,existingNodeID%,newNodeID%,newQorA$)
	If(this\m_pRootNode = Null)
		Print("Error no root node in AddNoNode()")
		Return
	EndIf
	If(DecisionTreeSearchTreeAndAddNoNode(this,this\m_pRootNode, existingNodeID, newNodeID, newQorA))
		Write("Added 'no' node")
		Write(newNodeID)
		Write(" onto 'no' branch of node ")
		Print(existingNodeID)
	Else
		Write("'no' Node ")
		Write(existingNodeID)
		Print(" not found")
	EndIf
End Function

Function DecisionTreeSearchTreeAndAddYesNode(this.DecisionTree,currentNode.TreeNode,existingNodeID%,newNodeID%,newQorA$)
	If(currentNode\m_iNodeID = existingNodeID)
		;create node
		If(currentNode\m_pYesBranch = Null)
			currentNode\m_pYesBranch = TreeNodeNew(newNodeID%,newQorA$)
		Else
			currentNode\m_pYesBranch = TreeNodeNew(newNodeID%,newQorA$)
		EndIf
		Return True
	Else
		;try yes branch If it exists
		If(currentNode\m_pYesBranch <> Null)
			If(DecisionTreeSearchTreeAndAddYesNode(this,currentNode\m_pYesBranch,existingNodeID,newNodeID,newQorA))
				Return True
			Else
				;try no branch If it exists
				If(currentNode\m_pNoBranch <> Null)
					Return(DecisionTreeSearchTreeAndAddYesNode(this,currentNode\m_pNoBranch,existingNodeID,newNodeID,newQorA))
				Else
					Return False
				EndIf	
			EndIf
		EndIf
		Return False
	EndIf
End Function

Function DecisionTreeSearchTreeAndAddNoNode%(this.DecisionTree,currentNode.TreeNode,existingNodeID%,newNodeID%,newQorA$)
	If(currentNode\m_iNodeID = existingNodeID)
		If(currentNode\m_pNoBranch = Null)
			currentNode\m_pNoBranch = TreeNodeNew(newNodeID,newQorA$)
		Else
			currentNode\m_pNoBranch = TreeNodeNew(newNodeID,newQorA$)
		EndIf
		Return True
	Else
		If(currentNode\m_pYesBranch <> Null)
			If(DecisionTreeSearchTreeAndAddNoNode(this,currentNode\m_pYesBranch,existingNodeID%,newNodeID%,newQorA$))
				Return True
			Else
				If(currentNode\m_pNoBranch <> Null)
					Return(DecisionTreeSearchTreeAndAddNoNode(this,currentNode\m_pNoBranch,existingNodeID%,newNodeID%,newQorA$))
				Else
					Return False
				EndIf
			EndIf

		Else 
			Return False
		EndIf	
	EndIf
End Function

Function DecisionTreeCreateRootNode(this.DecisionTree,nodeID%,newQorA$);
	this\m_pRootNode = TreeNodeNew(nodeID%,newQorA$);
End Function
	
	
Function DecisionTreeNew.DecisionTree()
	this.DecisionTree = New DecisionTree
	this\m_pRootNode = Null
	Return this
End Function

Function DecisionTreeDelete(this.DecisionTree)
	 DecisionTreeRemoveNode(this,this\m_pRootNode)
End Function

;;main
Graphics(800,600,16,2)

;create he New decision tree Object
newTree.DecisionTree = DecisionTreeNew();

;add the required root node
DecisionTreeCreateRootNode(newTree,1,"Have you got a weapon?");
	
;add subsequent nodes based on problem definition
DecisionTreeAddYesNode(newTree,1,2,"Are you close enough to attack?");
DecisionTreeAddNoNode(newTree,1,3,"Can you tackle bare-handed?");
DecisionTreeAddYesNode(newTree,2,4,"Attack!!!");
DecisionTreeAddNoNode(newTree,2,5,"Don't attack!!!");
DecisionTreeAddYesNode(newTree,3,6,"Attack!!!");
DecisionTreeAddNoNode(newTree,3,7,"Don't attack!!!");

;output the created tree 
DecisionTreeOutput(newTree);

;query the tree
DecisionTreeQuery(newTree);

Print("Press any key to quit...")

;pause
WaitKey()

Delete newTree
