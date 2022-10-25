; ID: 2901
; Author: pexe
; Date: 2011-11-10 08:08:19
; Title: Binary Heaps
; Description: Multi-list ascending and descending binary heaps algorithm

;;;;                    binaryheaps.bb V1.0
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;                      Matheus Cansian
;;;;
;;;; This LIB provide tools to create Binary Heaps ordered lists. It
;;;; can handle more than one simultaneous list and ascending and 
;;;; descending order.
;;;;
;;;; HOW-TO: Create a list with New(), use one of the sorting 
;;;; constants to select with each sorting order your list will 
;;;; operate. Then use the functions Add(), Remove() and Modify(), to
;;;; manipulate the data within the list.
;;;; If you need a debug tool, you can use Draw() to show all your 
;;;; list elements.


;; LIB CONFIGURATIONS ;;
Const BinaryHeap_MaxElements% = 1000   ;Max of elements each list can store
Const BinaryHeap_MaxSimultaneous% = 5  ;Max simultaneous list you can have

;Note: Memory storage is calculated by multiplying MaxElements with MaxSimoultaneous.
;      eg: MaxElements = 1000 and MaxSimultaneous = 5
;          1000 * 5 = 5000 bytes or approx. 5 KB 


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; LIB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;; Probably there's nothing you need to change down there! ;;;;;;;;;;;;

;; LIB CONSTANTS ;;
Const BinaryHeap_Version$ = "1.0"
Const BinaryHeap_SORT_SMALLEST = 1
Const BinaryHeap_SORT_BIGGEST  = 2

;; LIB ARRAYS ;;
Dim BinaryHeap_Sort%(BinaryHeap_MaxSimultaneous%) ;What kind of sorting method?
Dim BinaryHeap_Elements%(BinaryHeap_MaxSimultaneous%) ;Count the number of elements
Dim BinaryHeap_Value%(BinaryHeap_MaxSimultaneous%, BinaryHeap_MaxElements%)
Dim BinaryHeap_Data% (BinaryHeap_MaxSimultaneous%, BinaryHeap_MaxElements%)

;;; <summary>Create a list ordered list</summary>
;;; <param name="SortMethod">How the list will be sorted? Use the constants above</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_New%(SortMethod%)
	For Cont% = 1 To BinaryHeap_MaxSimultaneous%
		If BinaryHeap_Sort%(Cont%) = 0 Then
			BinaryHeap_Sort%(Cont%) = SortMethod%
			Return Cont%
		Else
			DebugLog Cont
		EndIf
	Next
	Return 0
End Function

;;; <summary>Delete a binary heap thread</summary>
;;; <param name="BinaryHeapThread">BinaryHeap Thread ID</param>
;;; <remarks></remarks>
;;; <returns>1 if the thread was deleted, 0 if the thread dont exists</returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_Delete%(BinaryHeapThread%)
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0
	
	;Clear Sort and Elements variables
	BinaryHeap_Sort%(BinaryHeapThread%) = 0
	BinaryHeap_Elements%(BinaryHeapThread%) = 0
	
	;Clear Value and Data variables
	For Cont% = 1 To BinaryHeap_MaxElements%
		BinaryHeap_Value%(BinaryHeapThread%, Cont%) = 0
		BinaryHeap_Data% (BinaryHeapThread%, Cont%) = 0
	Next
	
	Return 1
End Function

Function BinaryHeap_Add%(BinaryHeapThread%, Value%, HeapData%)
	;Check HeapThread
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0 ;Thread dont exists
	
	;Check if max elements reached
	If BinaryHeap_Elements%(BinaryHeapThread%) >= BinaryHeap_MaxElements% Then Return 0 ;Max elements reached
	
	;Add Elements counter
	BinaryHeap_Elements%(BinaryHeapThread%) = BinaryHeap_Elements%(BinaryHeapThread%) + 1
	
	;Get the last element position
	Local MyElement% = BinaryHeap_Elements%(BinaryHeapThread%)
	
	;Add element to the end of the list
	BinaryHeap_Value%(BinaryHeapThread%, MyElement)  = Value%
	BinaryHeap_Data%(BinaryHeapThread%, MyElement)   = HeapData%
	
	;Get sorting method
	Local Sort_Method% = BinaryHeap_Sort%(BinaryHeapThread%)
	
	Local MyValue%, ParentValue%, ParentElement%
	Repeat
		;Get parent position
		ParentElement% = Floor(MyElement%/2)
		If ParentElement% <= 0 Then Exit
		
		;Get elements values
		MyValue% = BinaryHeap_Value%(BinaryHeapThread%, MyElement)
		ParentValue% = BinaryHeap_Value%(BinaryHeapThread%, ParentElement)
		
		;Compare data
		If (MyValue% >= ParentValue% And Sort_Method% = BinaryHeap_SORT_SMALLEST) Or (MyValue% <= ParentValue% And Sort_Method% = BinaryHeap_SORT_BIGGEST) Then
			;Leave it alone
			Exit
		Else
			;Swap elements
			BinaryHeap_Private_Swap(BinaryHeapThread%, MyElement, ParentElement)
			 
			 ;Get new element position
			 MyElement = ParentElement
		EndIf
	Forever
	Return 1
End Function

;;; <summary>Get the first heap element</summary>
;;; <param name="BinaryHeapThread"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_Remove%(BinaryHeapThread%)
	;Check HeapThread
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0 ;Thread dont exists
	
	;Check if the are elements
	If BinaryHeap_Elements%(BinaryHeapThread%) <= 0 Then Return 0 ;No elements
	
	;Decrease Elements counter
	BinaryHeap_Elements%(BinaryHeapThread%) = BinaryHeap_Elements%(BinaryHeapThread%) - 1
	
	;Save first element data
	Local FirstData% = BinaryHeap_Data% (BinaryHeapThread%, 1)
	
	;Delete first element
	BinaryHeap_Value% (BinaryHeapThread%, 1) = 0
	BinaryHeap_Data%  (BinaryHeapThread%, 1) = 0
	
	;Swap last element with first
	BinaryHeap_Private_Swap(BinaryHeapThread%, 1, BinaryHeap_Elements%(BinaryHeapThread%)+1)
	
	;Get sorting method
	Local Sort_Method% = BinaryHeap_Sort%(BinaryHeapThread%)
	
	Local MyValue%, Child1Value%, Child2Value%, Child1Active%, Child2Active%, Child1Element%, Child2Element%, MyElement% = 1
	Repeat
		;Get element child
		Child1Element% = Floor(MyElement%*2)
		Child2Element% = Floor(MyElement%*2)+1
		
		;Get elements value
		MyValue% = BinaryHeap_Value%(BinaryHeapThread%, MyElement%)
		If (Child1Element%<=BinaryHeap_MaxElements%) Then Child1Value% = BinaryHeap_Value%(BinaryHeapThread%, Child1Element%):Else:Child1Value% = 0
		If (Child2Element%<=BinaryHeap_MaxElements%) Then Child2Value% = BinaryHeap_Value%(BinaryHeapThread%, Child2Element%):Else:Child2Value% = 0
		
		;Get elements status
		If (Child1Element%<=BinaryHeap_MaxElements%) Then Child1Active% = (BinaryHeap_Data%(BinaryHeapThread%, Child1Element%)<>0):Else:Child1Active% = 0
		If (Child2Element%<=BinaryHeap_MaxElements%) Then Child2Active% = (BinaryHeap_Data%(BinaryHeapThread%, Child2Element%)<>0):Else:Child2Active% = 0
		
		;Compare data
		If (Child1Active% And ((MyValue% >= Child1Value% And Sort_Method% = BinaryHeap_SORT_SMALLEST) Or (MyValue% <= Child1Value% And Sort_Method% = BinaryHeap_SORT_BIGGEST))) Or (Child2Active% And ((MyValue% >= Child2Value% And Sort_Method% = BinaryHeap_SORT_SMALLEST) Or (MyValue% <= Child2Value% And Sort_Method% = BinaryHeap_SORT_BIGGEST))) Then
			If Child1Active% And ((Child1Value% <= Child2Value% And Sort_Method% = BinaryHeap_SORT_SMALLEST) Or (Child1Value% >= Child2Value% And Sort_Method% = BinaryHeap_SORT_BIGGEST))
				;Swap with child 1
				BinaryHeap_Private_Swap(BinaryHeapThread%, MyElement, Child1Element)
				;Get new element position
				MyElement = Child1Element
			Else	
				;Swap with child 2
				BinaryHeap_Private_Swap(BinaryHeapThread%, MyElement, Child2Element)
				;Get new element position
				MyElement = Child2Element
			EndIf
		Else
			;Leave it alone
			Exit
		EndIf
	Forever
	
	Return FirstData%
End Function

;;; <summary>Change an element value</summary>
;;; <param name="BinaryHeapThread"></param>
;;; <param name="Value"></param>
;;; <param name="HeapData"></param>
;;; <param name="NewValue"></param>
;;; <remarks></remarks>
;;; <returns>1 if succeed, 0 if fail</returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_Modify%(BinaryHeapThread%, Value%, HeapData%, NewValue%)
	;Check HeapThread
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0 ;Thread dont exists
	
	;Store number of elements
	TotalElements% = BinaryHeap_Elements%(BinaryHeapThread%)
	
	;Search for the element
	For Cont% = 1 To TotalElements%
		If BinaryHeap_Value%(BinaryHeapThread%, Cont) = Value% And BinaryHeap_Data%(BinaryHeapThread%, Cont) = HeapData% Then
			MyElement% = Cont%
			Exit
		EndIf
	Next
	
	;Change element data
	BinaryHeap_Value%(BinaryHeapThread%, MyElement%) = NewValue%
	
	;Get sorting method
	Local Sort_Method% = BinaryHeap_Sort%(BinaryHeapThread%)
	
	Local MyValue%, ParentValue%, ParentElement%
	Repeat
		;Get parent position
		ParentElement% = Floor(MyElement%/2)
		If ParentElement% <= 0 Then Exit
		
		;Get elements values
		MyValue% = BinaryHeap_Value%(BinaryHeapThread%, MyElement)
		ParentValue% = BinaryHeap_Value%(BinaryHeapThread%, ParentElement)
		
		;Compare data
		If (MyValue% >= ParentValue% And Sort_Method% = BinaryHeap_SORT_SMALLEST) Or (MyValue% <= ParentValue% And Sort_Method% = BinaryHeap_SORT_BIGGEST) Then
			;Leave it alone
			Exit
		Else
			;Swap elements
			BinaryHeap_Private_Swap(BinaryHeapThread%, MyElement, ParentElement)
			 
			 ;Get new element position
			 MyElement = ParentElement
		EndIf
	Forever
	Return 1
End Function

;;; <summary>Draw the BinaryHeaps structure in the screen</summary>
;;; <param name="BinaryHeapThread"></param>
;;; <param name="SkipKey">Keycode to exit, or 0 to exit automatically</param>
;;; <param name="Levels">Number of levels to draw, or 0 to draw all</param>
;;; <remarks></remarks>
;;; <returns>Level drawn or 0 for failure</returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_Draw%(BinaryHeapThread%, SkipKey%=0, Levels%=0)
	;Check HeapThread
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0 ;Thread dont exists

	;Get graphics size
	Local Width% = GraphicsWidth()
	Local Height% = GraphicsHeight()
	
	;Clear screen
	SetBuffer BackBuffer():ClsColor 200,200,200
	
	;Get total os elements
	Local MaxElements% = BinaryHeap_Elements%(BinaryHeapThread%)
	
	;Get number of levels
	Local Elements% = 0, MaxLevels%
	Repeat
		Elements% = Elements% * 2
		Elements% = Elements% + 1
		MaxLevels% = MaxLevels% + 1
		If Elements% >= MaxElements% Then Exit
	Forever
	If Levels% =  0 Then Levels% = MaxLevels%
	If MaxElements% <=  1 Then Levels% = 2
	
	Local FirstPosition%, LevelSpacing%, LevelElements%, LevelSpacingSteps%, LastLevelSize%
	Local OffsetX%, OffsetY%, Value%, HeapActive%
	Repeat
		;Get input keys
		If KeyDown(200) Then OffsetY = OffsetY + 10
		If KeyDown(208) Then OffsetY = OffsetY - 10
		If KeyDown(205) Then OffsetX = OffsetX - 10
		If KeyDown(203) Then OffsetX = OffsetX + 10
		If KeyHit(201) And Levels% < MaxLevels% Then Levels% = Levels% + 1
		If KeyHit(209) And Levels% > 1 Then Levels% = Levels% - 1
		
		Cls
		
		;Draw header
		Color 0,0,0
		Text OffsetX+Width%/2, OffsetY+5,"Exploring BinaryHeap: "+BinaryHeapThread%+" | TotalElements: "+MaxElements%,1,0
		Text OffsetX+Width%/2, OffsetY+25,"Use PAGEUP/DOWN to change the number of levels and keyboard arrows to move",1,0
		
		
		;Draw Elements
		LastLevelSize% = ((2^Levels%)/2)*StringWidth(" 100 ")*2
		For Level% = 1 To Levels%
			FirstPosition% = 2^(Level%-1)
			LevelSpacing% = LastLevelSize% / 2^Level%
			LevelElements% = (2^Level%)/2
			LevelSpacingSteps% = -(LevelElements%-1)
			
			For Pos% = 0 To LevelElements%-1			
				;Check if maximum reached
				If FirstPosition%+Pos% > BinaryHeap_MaxElements% Then Exit
				
				;Get element value and data%
				Value% = BinaryHeap_Value%(BinaryHeapThread%, FirstPosition%+Pos%)
				HeapActive% = (BinaryHeap_Data%(BinaryHeapThread%, FirstPosition%+Pos%)<>0)
				
				;Draw child lines
				If Level% < Levels%
					Color 180,180,180
					;These lines are a total mess!!
					Line OffsetX+(Width%/2)+LevelSpacing%*(LevelSpacingSteps%+2*Pos%), OffsetY+Level%*20+40, OffsetX+(Width%/2)+(LastLevelSize% / 2^(Level%+1))*((-((2^(Level%+1))/2-1))+2*Pos%*2), OffsetY+(Level%+1)*20+40
					Line OffsetX+(Width%/2)+LevelSpacing%*(LevelSpacingSteps%+2*Pos%), OffsetY+Level%*20+40, OffsetX+(Width%/2)+(LastLevelSize% / 2^(Level%+1))*((-((2^(Level%+1))/2-1))+2*((Pos%*2)+1)), OffsetY+(Level%+1)*20+40
				EndIf
				
				;Draw element value
				If HeapActive% <> 0 Then Color 0,0,0:Else:Color 230,100,100
				Text OffsetX+(Width%/2)+LevelSpacing%*(LevelSpacingSteps%+2*Pos%), OffsetY+Level%*20+40,Value%,1,1
			Next
		Next
		Flip
	Until (KeyHit(SkipKey)) Or SkipKey%=0
	
	Return Levels%
End Function

;;; <summary>Swap two elements</summary>
;;; <param name="BinaryHeapThread">BinaryHeap thread ID</param>
;;; <param name="Position1">Position of the first element</param>
;;; <param name="Position2">Position of the second element</param>
;;; <remarks></remarks>
;;; <returns>1 for successful 0 for failure</returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function BinaryHeap_Private_Swap(BinaryHeapThread%, Position1%, Position2%)
	;Check HeapThread
	If (BinaryHeapThread% <= 0) Or (BinaryHeapThread% > BinaryHeap_MaxSimultaneous%) Then Return 0
	If BinaryHeap_Sort%(BinaryHeapThread%) = 0 Then Return 0 ;Thread dont exists

	;Store temp variables
	Local TempValue%  = BinaryHeap_Value% (BinaryHeapThread%, Position1%)
	Local TempData%   = BinaryHeap_Data%  (BinaryHeapThread%, Position1%)
	
	;Change first
	BinaryHeap_Value% (BinaryHeapThread%, Position1%) = BinaryHeap_Value% (BinaryHeapThread%, Position2%)
	BinaryHeap_Data%  (BinaryHeapThread%, Position1%) = BinaryHeap_Data%  (BinaryHeapThread%, Position2%)
	
	BinaryHeap_Value%(BinaryHeapThread%, Position2%) = TempValue%
	BinaryHeap_Data% (BinaryHeapThread%, Position2%) = TempData%
	
	Return 1
End Function
