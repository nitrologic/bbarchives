; ID: 2281
; Author: Ryan Burnside
; Date: 2008-06-30 06:36:13
; Title: Simple Dungeon Generator
; Description: Create traditional roguelike dungeons!

'.........................................................................................................
' Ryan Burnside Dungeon Engine Release 1.0
' ***Please credit "Ryan Burnside" if possible.***
' This dungeon generator is meant to do just that, nothing more and nothing less
' a dungeon holds an array and rooms that overlay spaces in the array
' the dungeon array is simply called "array"
' this array holds values for walls floor and will eventually hold special data for tiles
' it is up to the programmer to defing meaningful conventions for special values
' rooms can be seeded with special values using the "add_value" command
' it is important to note that the add_value command will overwrite values if the random room spot is taken
' because of this you will want to make your stairs last and items and monster values first
' once an item is obtained you will want to set the square back to 0 in the dungeon array
'.........................................................................................................

Strict
SeedRnd(MilliSecs()) 

' A wrapper object for an array and holder object for room instances
Type dungeon
	Field array:Byte[,] , rooms:TList = New TList, rooms_maxed:Byte = False, room_count:Int = 0
	
	Method add_room(min_width:Int, min_height:Int, max_width:Int, max_height:Int) 
		Local array_width:Int = array.dimensions()[0] 
		Local array_height:Int = array.dimensions()[1] 
		' first ensure that the room is not larger than the array
		If max_width > array_width
			max_width = array_width
		End If
		
		If max_height > array_height
			max_height = array_height
		End If
		
		' ensure that the min values are larger than 0
		If min_width < 2 min_width = 2
		If min_height < 2 min_height = 2
		' now set the size of this room
		Local width:Int = Rand(min_width, max_width) 
		Local height:Int = Rand(min_height, max_height) 
		Local search_width:Int = array_width - width
		Local search_height:Int = array_height - height
		Local search_x:Int = Rand(0, search_width) 
		Local search_y:Int = Rand(0, search_height) 
		Local max_checks:Int = search_width * search_height
		Local checked:Int = 0
		Local finished:Int = 0
		While Not finished
			If room_count > 0
				Local r:room = New room
				r.x = search_x
				r.y = search_y
				r.x2 = search_x + width - 1
				r.y2 = search_y + height - 1
				Local collisions:Int = 0
				For Local b:room = EachIn(rooms) 
					If rooms_collide(b, r) = True
						collisions:+1
					End If
				Next
				If Not collisions
					carve_room(r) 
					ListAddFirst(rooms, r) 
					room_count:+1
					finished = True
				Else
				End If
			EndIf
			
			If room_count = 0
				Local r:room = New room
				r.x = search_x
				r.y = search_y
				r.x2 = search_x + width - 1
				r.y2 = search_y + height - 1
				carve_room(r) 
				ListAddFirst(rooms, r) 
				room_count:+1
				finished = True
				Exit
			EndIf
			
			' add to the index
			search_x:+1
			If search_x > search_width
				search_x = 0
				search_y:+1
			End If
			If search_y > search_height
				search_y = 0
			End If
			checked:+1
			If checked = max_checks
				finished = True
			End If
		Wend
	End Method
	
	Method carve_room(r:room) 
		' take a room and carve out the space needed set squares to 0's
		For Local x = r.x To r.x2
			For Local y = r.y To r.y2
				array[x, y] = 0
			Next
		Next
	End Method
	
	Method ready_array(length:Int, height:Int) 
		' sets all indexes to 1 and readys the array for writing
		Local a:Byte[length, height] 
		array = a
		For Local x:Int = 0 To length - 1
			For Local y:Int = 0 To height - 1
				array[x, y] = 1
			Next
		Next
	End Method
	
	End Type

' A rectangular field that serves as a storage house for seeded values
Type room
	Field x:Int, y:Int, x2:Int, y2:Int
End Type

' return if rooms collide (used in create_dungeon)
Function rooms_collide:Int(room1:room, room2:room) 
	If room1.y2 + 3 < room2.y Return 0
	If room1.y - 3 > room2.y2 Return 0
	If room1.x2 + 3 < room2.x Return 0
	If room1.x - 3 > room2.x2 Return 0

	Return 1
End Function

' connect 2 rooms (used in create_dungeon)
Function connect_rooms(room1:room, room2:room, d:dungeon) 
	' first pick between 2 connection styles
	' we always draw from left to right so we must choose what order to process the rooms
	
	Local x1:Int = (room1.x + room1.x2) / 2.0
	Local y1:Int = (room1.y + room1.y2) / 2.0
	Local x2:Int = (room2.x + room2.x2) / 2.0
	Local y2:Int = (room2.y + room2.y2) / 2.0
	' make sure the values for each of the x's and y's are EVEN so no corridors touch
	If Float(x1 Mod 2.0) 
		x1:+1
	End If
	If Float(x2 Mod 2) 
		x2:+1
	End If
	If Float(y1 Mod 2) 
		y1:+1
	End If
	If Float(y2 Mod 2) 
		y2:+1
	End If
	draw_hori(y1, x1, x2, d) 
	draw_vert(x2, y1, y2, d) 
End Function

' draw a verticle line on the array (used in create_dungeon)
Function draw_vert(x1:Int, y1:Int, y2:Int, d:dungeon) 
	' see if step multiplier is negative
	Local dist:Int = Abs(y1 - y2) 
	Local mult:Int = 1
		If y1 > y2
			mult = -1
		End If
		' draw 
		For Local i = 0 To dist
			d.array[x1, y1 + (i * mult)] = 0
		Next
	EndFunction

' draw a horizontal line on the array (used in create_dungeon)
Function draw_hori(y1:Int, x1:Int, x2:Int, d:dungeon) 
	' see if step multiplier is negative
	Local dist:Int = Abs(x1 - x2) 
	Local mult:Int = 1
		If x1 > x2
			mult = -1
		End If
		' draw 
		For Local i = 0 To dist
			d.array[x1 + (i * mult), y1] = 0
		Next
EndFunction

' *IMPORTANT* lets a programmer seed the dungeon with item, monster and exit values as needed
Function add_value(d:dungeon, value:Int, attempts:Int) 
	For Local i:Int = 0 To attempts - 1
	Local r:room = room(d.rooms.ValueAtIndex(Rand(0, CountList(d.rooms) - 1))) 
	d.array[Rand(r.x, r.x2), Rand(r.y, r.y2)] = value
	Next
End Function

' *IMPORTANT* returns a freshly made dungeon, the workhorse of the program!
Function create_dungeon:dungeon(width:Int, height:Int, room_count:Int, room_min_height:Int, room_max_height:Int, room_min_width:Int, room_max_width:Int) 
	Local d:dungeon = New dungeon
	'ready the array
	d.ready_array(width, height) 
	'add rooms
	For Local i = 0 To room_count - 1
		d.add_room(room_min_width, room_min_height, room_max_width, room_max_height) 
	Next
	'connect rooms in a loop
	Local length:Int = CountList(d.rooms) 
 
	For Local j:Int = 0 To length
		If j + 1 < length
			connect_rooms(room(d.rooms.ValueAtIndex(j)) , room(d.rooms.ValueAtIndex(j + 1)) , d) 
		EndIf
	Next
	connect_rooms(room(d.rooms.ValueAtIndex(0)) , room(d.rooms.ValueAtIndex(length - 1)) , d) 
	
	Return d
End Function


'ENGINE ENDS!

	
'test here------DELETE THE FOLLOWING TO JUST HAVE THE BASE ENGINE!---------------
Local t:Float = MilliSecs() 
' make a new dungeon with our create_dungeon command 
Local d:dungeon = create_dungeon(40, 40, 6, 3, 12, 3, 12) 
' seed the rooms with about 5 "2", these could be treasure chests if you wanted 
add_value(d, 2, 5) 
' seed the rooms with 1 stairway, we will call this value "3"
add_value(d, 3, 1) 
Notify("Generated in :" + String((MilliSecs() - t) *.001) + " seconds") 

' test draw dungeon please
AppTitle = "Ryan Burnside Dungeon Engine"
Graphics(640, 480) 


While Not KeyHit(KEY_ESCAPE) 
' draw the dungeon values as tiles 
For Local i = 0 To d.array.dimensions()[0] - 1
	For Local j = 0 To d.array.dimensions()[1] - 1
		If d.array[i, j] 
		 	Select d.array[i, j] 
			Case 1
			SetColor 128, 128, 128
			DrawRect(i * 8, j * 8, 7, 7) 
			Case 2
			SetColor 255, 128, 0
			DrawRect(i * 8, j * 8, 7, 7) 
			Case 3
			SetColor 0, 128, 255
			DrawRect(i * 8, j * 8, 7, 7) 
			EndSelect
		End If
		
		If Not d.array[i, j] 
			SetColor 200, 200, 200
			DrawRect(i * 8, j * 8, 7, 7)
		End If
	Next
Next

If KeyHit(KEY_SPACE)  ' new dungeon! 
Cls
d = create_dungeon(40, 40, 6, 3, 12, 3, 12) 
add_value(d, 2, 5) 
add_value(d, 3, 1) 
End If
Flip
Wend
