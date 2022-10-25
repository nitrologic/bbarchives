; ID: 2339
; Author: Nilium
; Date: 2008-10-20 20:15:42
; Title: Merge Sort TList
; Description: Sort a TList using the merge sort algorithm

Function MergeSort:TLink( head:TLink, num% )
	
	Local temp1:TLink, temp2:TLink
	Local ret:TLink
	
	If num <= 2 Then
		If num = 1 Then
			ret = head
		Else
			If head.Value().Compare(head._succ.Value()) < 0 Then
				ret = head
			Else
				temp1 = head
				temp2 = head._succ
				temp1._pred = temp2
				temp2._succ = temp1
				temp1._succ = Null
				temp2._pred = Null
				ret = temp2
			EndIf
		EndIf
	Else
		temp2 = head
		Local n1%, n2%
		n1 = num/2
		n2 = num-n1
		
		For Local idx:Int = 1 To n1-1
			temp2 = temp2._succ
		Next
		
		temp1 = temp2
		temp2 = temp2._succ
		temp1._succ = Null
		temp2._pred = Null
		temp1 = head
		
		temp1 = MergeSort( temp1, n1 )
		temp2 = MergeSort( temp2, n2 )
		
		Local l1:Int = False
		ret = temp2
		
		If temp1.Value().Compare(temp2.Value()) < 0 Then
			ret = temp1
			l1 = True
		EndIf
		
		While temp1 <> Null Or temp2 <> Null
		
			If l1 Then
				While temp1._succ And temp1._succ.Value().Compare(temp2.Value()) < 0
					temp1 = temp1._succ
				Wend
				temp2._pred = temp1
				temp1 = temp1._succ
				temp2._pred._succ = temp2
				If temp1 = Null Then
					Exit
				EndIf
			Else
				While temp2._succ And temp2._succ.Value().Compare(temp1.Value()) < 0
					temp2 = temp2._succ
				Wend
				temp1._pred = temp2
				temp2 = temp2._succ
				temp1._pred._succ = temp1
				If temp2 = Null Then
					Exit
				EndIf
			EndIf
			
			l1 = Not l1
			
		Wend
	EndIf
	
	Return ret
End Function
