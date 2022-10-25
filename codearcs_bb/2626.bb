; ID: 2626
; Author: superStruct
; Date: 2009-12-08 09:54:30
; Title: Cellular Model
; Description: A program to replicate cellular life.

Graphics3D 600,612,0,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
AppTitle "CELLULAR MODEL"

.init

Include "lib.bb"

Color 255,255,255

farm_width = width/Sqr(farms)
farm_height = height/Sqr(farms)

Locate 0,0

farms = Input("NUMBER OF FARMS: ")
num = Input("NUMBER OF CELLS: ")

If num > 35000
	RuntimeError "MAX NUMBER OF CELLS EXCEEDED"
EndIf

MultiCell(num)

farm.farm = New farm
farm\x = 0
farm\y = 12
farm\supply = 100
farm\regen_r = 50
farm\regen = 10
farm\width = width/Sqr(farms)
farm\height = height/Sqr(farms)

y# = 12

For i = 1 To farms - 1
	farm.farm = New farm
	If x# >= width - width/Ceil(Sqr(farms))
		y# = y# + height/Ceil(Sqr(farms))
		x# = 0
	Else
		x# = x# + width/Ceil(Sqr(farms))
	EndIf
	farm\x = x
	farm\y = y
	farm\supply = 100
	farm\regen_r = 50
	farm\regen = 10
	farm\width = width/Sqr(farms)
	farm\height = height/Sqr(farms)
Next

Cls

While Not KeyDown(1)
	Cls
	
	Text 0,0,"GENERATION: " + generation
	
	If KeyHit(88)
		If shown = True
			shown = False
		ElseIf shown = False
			shown = True
		EndIf
	EndIf
	
	If KeyDown(19)
		For cell.cell = Each cell
			Delete cell
		Next
		Goto init
	EndIf
	
	population = 0
	food = 0
	
	For cell.cell = Each cell
		For farm.farm = Each farm
			If RectsOverlap(farm\x,farm\y,farm\width,farm\height,cell\x,cell\y,10,10)
				If cell\hunger > 0 And farm\supply > 0
					farm\supply = farm\supply - 1
					cell\hunger = cell\hunger - 1
				EndIf
			EndIf
		Next
		
		If generation = event_next
			event = 1
			If cell\luck > event_power Or cell\strength > event_power
				cell\live = 1
			Else
				cell\live = 0
			EndIf
		EndIf
		
		temp_x = cell\x
		temp_y = cell\y			
		
		cell\age = generation - cell\birth
		cell\counter = cell\counter + 1
		cell\hun_cn = cell\hun_cn + 1
		
		If cell\hun_cn - cell\hunger_r = 0
			cell\hun_cn = 0
			cell\hunger = cell\hunger + 1
		EndIf
				
		If cell\age = cell\longevity
			cell\live = 0
			cells(cell\x/10,cell\y/10) = 0
		EndIf
		
		If cell\hunger > 10
			cell\check = cell\check + 1
			If cell\check > 500
				cell\live = 0
			EndIf
		EndIf			
		
		If cell\counter - cell\reproduction = 0 And cell\live = 1 And cell\hunger = 0
			cell\counter = 0
			temp_x = cell\x
			temp_y = cell\y
			temp_lon = cell\longevity
			temp_rep = cell\reproduction
			temp_r = cell\r
			temp_g = cell\g
			temp_b = cell\b
			temp_mutation_r = cell\mutation_r
			temp_strength = cell\strength
			temp_hun_r = cell\hunger_r
			For i = 1 To cell\child
				If cell\mutation = Rand(1,cell\mutation_r)
					Mutation(temp_x,temp_y,temp_lon,temp_rep,temp_mutation_r,temp_strength,temp_hun_r)
				Else
					NewCell(temp_x,temp_y,temp_r,temp_g,temp_b,temp_lon,temp_rep,temp_mutation_r,temp_strength,temp_hun_r)
				EndIf
			Next
		EndIf
		
		If cell\live = 1
			If cell\x + 10 > width
				cell\x = width - 10
			ElseIf cell\x < 0
				cell\x = 0
			EndIf
			If cell\y + 10 > height
				cell\y = height - 10
			ElseIf cell\y < 12
				cell\y = 12
			EndIf
			
			population = population + 1
			
			Color cell\r,cell\g,cell\b
			Oval cell\x,cell\y,4,4,0
		Else
			Delete cell
		EndIf
	Next
	
	If event = 1
		event = 0
		event_next = Rand(2000,10000) + generation
		event_power = Rand(-3,10)
	EndIf
	
	generation = generation + 1
	
	Color 255,255,255
	Rect 0,12,height,width,0
	
	For farm.farm = Each farm
		If shown = True And farm\supply > 0
			Rect farm\x,farm\y,farm\width,farm\height,0
		EndIf
		If (generation Mod farm\regen) = 0 And farm\supply + farm\regen< 100
			farm\supply = farm\supply + farm\regen
		EndIf
		food = food + farm\supply
	Next
	
	Flip
Wend
