; ID: 2239
; Author: Andres
; Date: 2008-04-07 19:04:11
; Title: Breadth-first pathfinder
; Description: A simple pathfinding engine, only 4 functions

Const FLAG1:Int = $01
Const FLAG2:Int = $02
Const FLAG4:Int = $04
Const FLAG8:Int = $08
Const FLAG16:Int = $10
Const FLAG32:Int = $20
Const FLAG64:Int = $40
Const FLAG128:Int = $80

Function CheckFlag:Int(flags:Int, flag:Int)
	If flags & flag Then Return True Else Return False
End Function

Const PATH_TOP:Int = FLAG1
Const PATH_BOTTOM:Int = FLAG2
Const PATH_LEFT:Int = FLAG4
Const PATH_RIGHT:Int = FLAG8
Const PATH_CEIL:Int = FLAG16
Const PATH_FLOOR:Int = FLAG32
Const PATH_ALL:Int = FLAG64

' List used for path find
Global pathmap_path_list:TList = CreateList()

' Pathmap type
Type TPathmap
	Field width:Long, height:Long, depth:Long
	
	Field map:TBank
End Type

Type TPath
	Field x:Long, y:Long, z:Long
	Field path:String
End Type

' Create a new pathmap
Function CreatePathmap:TPathmap(width:Long, height:Long, depth:Long = 1)
	Local this:TPathmap = New TPathmap
		this.width = width
		this.height = height
		this.depth = depth
		
		this.map = CreateBank(this.width * this.height * this.depth)
		
	Return this
End Function

' Insert flags to pathmap slot
Function PathmapSlot(this:TPathmap, flags:Int, x:Long, y:Long, z:Long = 0)
	Local offset:Long = (z * this.width * this.height) + (y * this.width) + x
	PokeByte(this.map, offset, flags)
End Function

' Search for a path within pathmap; 1 - north, 2 - south, 3 - west, 4 - east, 5 - up, 6 - down
Function FindPath:String(this:TPathmap, x1:Long, y1:Long, z1:Long, x2:Long, y2:Long, z2:Long)
	Local offset:Long, offset_top:Long, offset_bottom:Long, offset_left:Long, offset_right:Long, offset_ceil:Long, offset_floor:Long
	
	Local temp_bank:TBank = CreateBank(BankSize(this.map))
	CopyBank(this.map, 0, temp_bank, 0, BankSize(this.map))
	
	ClearList(pathmap_path_list)
	Local that:TPath = New TPath
		that.x = x1
		that.y = y1
		that.z = z1
		that.path = ""
		ListAddFirst(pathmap_path_list, that)
		ListAddFirst(pathmap_path_list, that)
	
	For that:TPath = EachIn pathmap_path_list
		offset = (that.z * this.width * this.height) + (that.y * this.width) + that.x
		
		' TOP
		If that.y > 0
			offset_top = (that.z * this.width * this.height) + ((that.y - 1) * this.width) + that.x
			If Not CheckFlag(PeekByte(temp_bank, offset), PATH_TOP) And Not CheckFlag(PeekByte(temp_bank, offset_top), PATH_BOTTOM) And Not CheckFlag(PeekByte(temp_bank, offset_top), PATH_ALL)
				Local pth:TPath = New TPath
					pth.x = that.x
					pth.y = that.y - 1
					pth.z = that.z
					pth.path = that.path + "1"
					ListAddLast(pathmap_path_list, pth)
					PokeByte(temp_bank, offset_top, PATH_ALL)
			EndIf
		EndIf
		' LEFT
		If that.x > 0
			offset_left = (that.z * this.width * this.height) + (that.y * this.width) + that.x - 1
			If Not CheckFlag(PeekByte(temp_bank, offset), PATH_LEFT) And Not CheckFlag(PeekByte(temp_bank, offset_left), PATH_RIGHT) And Not CheckFlag(PeekByte(temp_bank, offset_left), PATH_ALL)
				Local pth:TPath = New TPath
					pth.x = that.x - 1
					pth.y = that.y
					pth.z = that.z
					pth.path = that.path + "3"
					ListAddLast(pathmap_path_list, pth)
					PokeByte(temp_bank, offset_left, PATH_ALL)
			EndIf
		EndIf
		' BOTTOM
		If that.y + 1 < this.height
			offset_bottom = (that.z * this.width * this.height) + ((that.y + 1) * this.width) + that.x
			If Not CheckFlag(PeekByte(temp_bank, offset), PATH_BOTTOM) And Not CheckFlag(PeekByte(temp_bank, offset_bottom), PATH_TOP) And Not CheckFlag(PeekByte(temp_bank, offset_bottom), PATH_ALL)
				Local pth:TPath = New TPath
					pth.x = that.x
					pth.y = that.y + 1
					pth.z = that.z
					pth.path = that.path + "2"
					ListAddLast(pathmap_path_list, pth)
					PokeByte(temp_bank, offset_bottom, PATH_ALL)
			EndIf
		EndIf
		' RIGHT
		If that.x + 1 < this.width
			offset_right = (that.z * this.width * this.height) + (that.y * this.width) + that.x + 1
			If Not CheckFlag(PeekByte(temp_bank, offset), PATH_RIGHT) And Not CheckFlag(PeekByte(temp_bank, offset_right), PATH_LEFT) And Not CheckFlag(PeekByte(temp_bank, offset_right), PATH_ALL)
				Local pth:TPath = New TPath
					pth.x = that.x + 1
					pth.y = that.y
					pth.z = that.z
					pth.path = that.path + "4"
					ListAddLast(pathmap_path_list, pth)
					PokeByte(temp_bank, offset_right, PATH_ALL)
			EndIf
		EndIf
		
		' If the pathmap has 3 dimensions
		If this.depth > 1
			If that.z > 0
				offset_ceil = ((that.z - 1) * this.width * this.height) + (that.y * this.width) + that.x
				If Not CheckFlag(PeekByte(temp_bank, offset), PATH_CEIL) And Not CheckFlag(PeekByte(temp_bank, offset_ceil), PATH_FLOOR) And Not CheckFlag(PeekByte(temp_bank, offset_ceil), PATH_ALL)
					Local pth:TPath = New TPath
						pth.x = that.x
						pth.y = that.y - 1
						pth.z = that.z
						pth.path = that.path + "5"
						ListAddLast(pathmap_path_list, pth)
						PokeByte(temp_bank, offset_ceil, PATH_ALL)
				EndIf
			EndIf
			If that.z + 1< this.depth
				offset_floor = ((that.z + 1) * this.width * this.height) + (that.y * this.width) + that.x
				If Not CheckFlag(PeekByte(temp_bank, offset), PATH_FLOOR) And Not CheckFlag(PeekByte(temp_bank, offset_floor), PATH_CEIL) And Not CheckFlag(PeekByte(temp_bank, offset_floor), PATH_ALL)
					Local pth:TPath = New TPath
						pth.x = that.x
						pth.y = that.y + 1
						pth.z = that.z
						pth.path = that.path + "6"
						ListAddLast(pathmap_path_list, pth)
						PokeByte(temp_bank, offset_floor, PATH_ALL)
				EndIf
			EndIf
		EndIf
		
		' CORRECT PATH FOUND
		If that.x = x2 And that.y = y2 And that.z = z2
			Local path:String = that.path
			ClearList(pathmap_path_list)
			temp_bank = Null
			Return path
		EndIf
		
		' Return false if there is no solution
		If ListIsEmpty(pathmap_path_list) Then
			temp_bank = Null
			Return "[ERROR]"
		EndIf
				
		' Remove the parent
		ListRemove(pathmap_path_list, that)
		that = Null
	Next
	Return "FAILED"
End Function

' Remove pathmap from memory
Function FreePathmap(this:TPathmap)
	this.map = Null
End Function
