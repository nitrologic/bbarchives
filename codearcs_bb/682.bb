; ID: 682
; Author: Dr Derek Doctors
; Date: 2003-05-10 06:58:20
; Title: Cache Map / Edge Update Scroller
; Description: Very fast tilemap scrolling at bargain CPU prices! ;)

; Cache Map - Copyright 2003 - Graham Goring (AKA Dr Derek Doctors)

; For lovely smooth scrolling when using titchy tiles with none of the horrid processor overhead associated with a bazillion
; blit instructions.

; The reason I wrote this code was because I was writing a scrolling game which had 16x16 pixel tiles in it and it was
; taking a good few milliseconds just to draw a single layer of the screen. With something like 5 layers I was chewing up
; all of my frame time on something that I didn't even want to spend a tenth of that time on. And so I realised that
; I could save a lot of time at the expense of a little memory if I dumped the tiles I drew into a dummy image and then
; just drew that every frame, updating the contents of that image whenever I scrolled. However you can't have images wider
; than the screen with some video cards and so getting it all to fit into an image that's precisely the size of the screen
; when you've got fractions of tiles meant I had to do a bit of thinking and ask a few colleagues what they'd recommend.

; Anyhoo, this is what I came up with and it works a treat. During normal operation I can happily draw 4 layers of 16x16
; tiles in about a millisecond on my home PC. It obviously slows down a bit when you scroll the screen, but unless I
; moved the screen by more than a few tiles every frame (which is VERY fast) then it didn't tick over the 2 milliseconds mark.

; And so, in a display of generosity and goodwill I share this code with everyone else in Blitz land, so that you might
; produce a game which runs happily on a P2-300 instead of an Athlon 1.2Ghz T-bird.

; The dependancies of this code is that your map array has at least 3 dimensions, those being x and y, naturally, and
; also a layer. Now it might well be that you don't want multiple map layers in your game (what? no parallax?! For shame!)
; so just DIM the array as "map (width,height,0)" and the functions won't need altering at all. Alternatively you could
; strip all the layer bumph out of this code, which should only entail buggering about with the "CMAP_fill_boxes" function.

; Note that all functions are prefixed with "CMAP_" and all arrays, constants and globals with "cm_" so that you don't
; get any conflicts. Unless you inexplicably chose to prefix your own stuff with this, too, in which case all I can say
; is "Woooh! S-s-spooky!".

; Oh, and it only uses square tiles. You could adapt it to rectangular ones I expect, but what the hell would you be using
; those for?! And you can't have maps smaller than the screen. Well, I don't think so. It'd probably fall over if you did.

; Now, to get this working you'll need to pop over to the "CMAP_fill_boxes" function and alter the bit which draws the tiles
; to the screen so that it uses the correct image handle and also the tile array conforms to the name and format of your own
; one. Also note if you're using offset tiles (ie, tiles can be drawn off-grid to allow for more organic levels) then the
; there's a little bit of overdraw built into the routine to allow for this, but you should only be offsetting your tiles
; down-right (which is easist, anyway).

; After that just replace the 6 ?'s below with suitable values or constants.

; To set up a map layer, simply call "CMAP_create_layer" followed by "CMAP_initialise_layer" with the attendant parameters. Look at
; the functions for more of an explanation.

; Then you just need to call "CMAP_scroll_display" for each layer with the distances that they've moved every frame, then
; just "CMAP_blit" each layer (note that you can supply a pair of offsets to the bltting routine if you have a panel at the top or
; left of the screen).

; Note that all the functions are called on a layer by layer basis so if you have 3 layers you'll need to call it 3 times for
; layers 0, 1 and 2 with the appropriate parameters. I didn't want to automate this at all with parallax code built in so
; that you were free to do whatever you liked with the layers.

; There are no restrictions for the use of this code, however I completely wash my hands of it if you manage to blow up
; your computer due to it's sheer excellence or bizarrely murder someone as a result of using it. You may change it as you
; wish however I ask that you give me a credit for the code if you ever release anything using it (in fact if you make
; something commercial with it I *insist* on a credit because I ain't getting any wonga out of the deal) and that you
; don't redistribute it or claim that you wrote it. If you do I'll squeeze your head until it pops like a water balloon full
; of liquefied meat.

; Oh, in case you're wondering what all the references to "offset tiles" means, it's when you have a pair of values in
; the map array for each tile which say how far it's offset from it's default position (usually it's a value from 0 to TILESIZE-1)
; on each axis. To be honest, it's not often used because it's easy to carve odd holes in your map with it, but it can
; have its applications.

Const cm_map_width = ? ; width of the map array in tiles - probably be set to another constant in your own code
Const cm_map_height = ? ; height of the map array in tiles - probably be set to another constant in your own code

Const cm_max_cache_map_layers = ? ; this is the number of layers in the cache map minus one (because arrays start at 0)
Const cm_display_width = ? ; this is the width of the display and must be a multiple of the tilesize or it all goes kerplooey
Const cm_display_height = ? ; this is the height of the display and must be a multiple of the tilesize or it all goes kerplooey

Const cm_tilesize = ? ; this is the width and height of the tiles in your tileset.

Type cm_box
	Field tlx,tly,width,height,tilex,tiley
End Type

Type cm_cachemapglobals
	Field x_divider [cm_max_cache_map_layers] ; this and the below variable point to the vertical and horizontal dividers of the cache tilemap
	Field y_divider [cm_max_cache_map_layers]
	Field x_offset [cm_max_cache_map_layers] ; this and the below variable point to the position within the tilemap of the current top-left block of the image
	Field y_offset [cm_max_cache_map_layers]
	Field temp_x_offset [cm_max_cache_map_layers] ; this is used to store the old position of the offset and is very important. Least I think so...
	Field temp_y_offset [cm_max_cache_map_layers]
	Field img [cm_max_cache_map_layers] ; this is a table of pointers to images used to store the cached images
	Field array_start_layer [cm_max_cache_map_layers] ; this is the start layer of the map array from which the tiles for this display layer are gotten from
	Field array_end_layer [cm_max_cache_map_layers] ; and this is the end layer of same
End Type

Global cm_cmg.cm_cachemapglobals = New cm_cachemapglobals ; this is the global structure which contains all the operating variables of the cache map functions

;CMAP_create_layer (0,0,0) ; creates a layer which should be done for every layer at the start of the game taking the layer number,
	; the first and last array layers which contribute to the layer and three optional values for Red, Green and Blue
	; mask components. It defaults to bright pink (255,0,255)
	
; After calling the create layer stuff for each display layer (remember each layer uses up a fair chunk of memory - if you're running in
; 16bit mode and at 640,480 then it'll use 600K) then you'll probably want to correct each of the cmg\x_offset[layer] and cmg\y_offset[layer]
; to point to the correct place for the start of your game and then refresh the screen using the relevant function (refresh_screen (layer))

; Then it's just a case of keeping track of how far it scrolls in any direction and pushing the image appropriately. I'd suggest
; keeping an array like layer_positions (cm_max_cache_map_layers,1,1) where you store the x and y of each layer and the previous frames x and y
; too and then see how far it's changed and push away.



Function CMAP_scroll_display (x_push,y_push,layer)
	; This routine is called every frame with the distances you want to scroll the screen
	; on the x and y axis. It deals with horizontal and vertical movement separately to
	; avoid rogue blocks appearing. It took a little while to fix that bug down despite my
	; knowing exactly what was causing it from the outset. But then that's the joy of
	; programming, innit?
	
	; Oh, except don't bother calling it if you've not moved the screen as it won't create any
	; boxes at all and will just be a waste of time.

	cm_cmg\temp_x_offset[layer] = cm_cmg\x_offset[layer]
	cm_cmg\temp_y_offset[layer] = cm_cmg\y_offset[layer]
	cm_cmg\x_offset[layer] = cm_cmg\x_offset[layer] + x_push
	cm_cmg\y_offset[layer] = cm_cmg\y_offset[layer] + y_push

	If (x_push<>0)
		CMAP_push_horizontal(x_push,layer)
		CMAP_split_boxes()
		CMAP_fill_boxes(layer)
	EndIf

	cm_cmg\x_divider[layer] = (cm_cmg\x_divider[layer] + x_push + cm_display_width) Mod cm_display_width
	cm_cmg\temp_x_offset[layer] = cm_cmg\x_offset[layer]

	If (y_push<>0)
		CMAP_push_vertical(y_push,layer)
		CMAP_split_boxes()
		CMAP_fill_boxes(layer)
	EndIf

	cm_cmg\y_divider[layer] = (cm_cmg\y_divider[layer] + y_push + cm_display_height) Mod cm_display_height

End Function



Function CMAP_push_horizontal (x_push,layer)
	; This routine defines the necessary blocks to scroll the screen left or right. ie,
	; those areas of the screen which need to be redrawn to accomodate the new position
	; of the x_divider (the line which says where the left edge of the screen is in the
	; image "cm_cmg\img[layer]")

	If (x_push>0)
		b.cm_box = New cm_box
		b\tlx = cm_cmg\x_divider[layer]
		b\tly = cm_cmg\y_divider[layer]
		b\width = x_push
		b\height = cm_display_height
		b\tilex = cm_cmg\temp_x_offset[layer] + cm_display_width
		b\tiley = cm_cmg\temp_y_offset[layer]
	EndIf

	If (x_push<0)
		b.cm_box = New cm_box
		b\tlx = cm_cmg\x_divider[layer] + x_push
		b\tly = cm_cmg\y_divider[layer]
		b\width = Abs (x_push)
		b\height = cm_display_height
		b\tilex = cm_cmg\x_offset[layer]
		b\tiley = cm_cmg\temp_y_offset[layer]
	EndIf	
	
End Function



Function CMAP_push_vertical (y_push,layer)
	; This routine defines the necessary blocks to scroll the screen up or down. ie,
	; those areas of the screen which need to be redrawn to accomodate the new position
	; of the y_divider (the line which says where the top edge of the screen is in the
	; image "cm_cmg\img[layer]").


	If (y_push>0)	
		b.cm_box = New cm_box
		b\tly = cm_cmg\y_divider[layer]
		b\tlx = cm_cmg\x_divider[layer]
		b\height = y_push
		b\width = cm_display_width
		b\tiley = cm_cmg\temp_y_offset[layer] + cm_display_height
		b\tilex = cm_cmg\temp_x_offset[layer]
	EndIf
	
	If (y_push<0)
		b.cm_box = New cm_box
		b\tly = cm_cmg\y_divider[layer] + y_push
		b\tlx = cm_cmg\x_divider[layer]
		b\height = Abs (y_push)
		b\width = cm_display_width
		b\tiley = cm_cmg\y_offset[layer]
		b\tilex = cm_cmg\temp_x_offset[layer]
	EndIf

End Function



Function CMAP_split_boxes ()
	; This routine moves those boxes which are completely outside the edge of the screen
	; so that they are within it, and also breaks those boxes which go over the edge of
	; the screen into two new boxes. It works recursively so as to chop up every last box
	; if necessary, though I suspect the recursive part of it really isn't necessary - I'm
	; just too scared to take it out... ;)
	
	Repeat
	
		flag=0

		For b.cm_box=Each cm_box
	
			If ( (b\tlx < 0) And (b\tlx+b\width-1 < 0) ) Or ( (b\tlx > cm_display_width-1) And (b\tlx+b\width-1 > cm_display_width-1) )
				b\tlx=(b\tlx+cm_display_width) Mod cm_display_width
				flag=1
			EndIf
		
			If ( (b\tly < 0) And (b\tly+b\height-1 < 0) ) Or ( (b\tly > cm_display_width-1) And (b\tly+b\height-1 > cm_display_width-1) )
				b\tly=(b\tly+cm_display_height) Mod cm_display_height
				flag=1
			EndIf
		
			If (b\tlx < 0) ; box starts off the left edge of screen
				b\tlx=b\tlx+cm_display_width ; bumps it forward so the next line catches it. Easier for me. :)
			EndIf
		
			If (b\tlx+b\width > cm_display_width) ; box goes off right edge of screen
				a.cm_box = New cm_box
				a\tlx = 0
				a\tly = b\tly
				a\width = (b\tlx + b\width) - cm_display_width
				a\height = b\height
				b\width = b\width - a\width
				a\tiley = b\tiley
				a\tilex = b\tilex+b\width
				flag = 1
			EndIf
		
			If (b\tly < 0) ; box starts off the top edge of screen
				b\tly=b\tly+cm_display_height ; bumps it forward so the next line catches it. Easier for me. :)
			EndIf
		
			If (b\tly+b\height > cm_display_height) ; box goes off bottom edge of screen
				a.cm_box = New cm_box
				a\tly = 0
				a\tlx = b\tlx
				a\height = (b\tly + b\height) - cm_display_height
				a\width = b\width
				b\height = b\height - a\height
				a\tilex = b\tilex
				a\tiley = b\tiley+b\height
				flag = 1
			EndIf
		
		Next
	
	Until (flag=0)
		
End Function



Function CMAP_fill_boxes (layer)
	; This plonks the relevant tiles into the boxes defined by the other routines. You'll most likely need to alter
	; the line "DrawImage gfx_handle,xx*cm_tilesize,yy*cm_tilesize,map(tx,ty,l,0)" unless there's been an astounding
	; coincidence...

	SetBuffer ImageBuffer(cm_cmg\img[layer])
	
	For b.cm_box = Each cm_box
	
		Viewport b\tlx , b\tly , b\width , b\height
		
		Cls
		
		For l=cm_cmg\array_start_layer [layer] To cm_cmg\array_end_layer [layer] ; comment out if no layers!
			For xx=(b\tlx/cm_tilesize)-1 To ((b\tlx+b\width-1)/cm_tilesize)	
				For yy=(b\tly/cm_tilesize)-1 To ((b\tly+b\height-1)/cm_tilesize)
				
					tx=(b\tilex/cm_tilesize) + ( xx - (b\tlx/cm_tilesize) )
					ty=(b\tiley/cm_tilesize) + ( yy - (b\tly/cm_tilesize) )
		
					If (tx>=0) And (ty>=0) And (tx<cm_map_width) And (ty<cm_map_height)
						DrawImage gfx_handle,xx*cm_tilesize,yy*cm_tilesize,map(tx,ty,l,0) ; alter this line to match the graphic handle and map array of your program
					EndIf
				
				Next
			Next
		Next ; comment out if no layers!
	
		Delete b
	
	Next
	
	SetBuffer BackBuffer()

End Function



Function CMAP_refresh_tiles (x,y,width,height,layer)
	; This function is for when you want to refresh part of the display without the hassle
	; of re-drawing the whole caboodle - which is obviously what we wanted to avoid in writing
	; this whole damn shebang.
	; First of all it chops off any edges of the refreshed area that are outside the visible
	; screen and then it creates a "cm_box", which is passed through the regular splitting and
	; filling functions.
	
	; A practical example of when you'd use this is when you blow up a tile in your game that's
	; currently on-screen. Unless you refresh that part of the display it won't actually disappear
	; despite your updating of the map array.
	
	; In instances where you have offset tiles you'll obviously want to refresh a slightly larger box so that
	; offset tiles aren't chopped off, which would be a tragedy of immense proportions, possibly leading to
	; downfall of Rome (if that hasn't already happened).
	
	; The variables passed to it are full-size world co-ordinates (ie, not divided by tilesize).
	
	If (x < cm_cmg\x_offset[layer]) ; if the box starts off the left of the screen we need to chop that edge off of it.
		width = width - (cm_cmg\x_offset[layer] - x)
		x = cm_cmg\x_offset[layer]
	EndIf

	If (y < cm_cmg\y_offset[layer]) ; if the box starts off the top of the screen, chop!
		height = height - (cm_cmg\y_offset[layer] - y)
		y = cm_cmg\y_offset[layer]
	EndIf

	If (x + width >= cm_display_width + cm_cmg\x_offset[layer]) ; if it trails off the right of the screen...
		width = (cm_display_width + cm_cmg\x_offset[layer]) - x
	EndIf

	If (y + height >= cm_display_height + cm_cmg\y_offset[layer]) ; if it trails off the bottom of the screen...
		height = (cm_display_height + cm_cmg\y_offset[layer]) - y
	EndIf

	If (width>0) And (height>0) And (x < cm_display_width + cm_cmg\x_offset[layer]) And (y < cm_display_height + cm_cmg\y_offset[layer]) ; if the box is actually anywhere on the screen

		b.cm_box = New cm_box
	
		b\tilex = x
		b\tiley = y
		b\tlx = (x - cm_cmg\x_offset[layer]) + cm_cmg\x_divider[layer]
		b\tly = (y - cm_cmg\y_offset[layer]) + cm_cmg\y_divider[layer]
		b\width = width
		b\height = height
	
		CMAP_split_boxes()
		CMAP_fill_boxes(layer)

	EndIf

End Function



Function CMAP_refresh_screen (layer)
	; Just a shorthand to make refreshing the whole screen easier for first timers.

	CMAP_refresh_tiles (cm_cmg\x_offset[layer],cm_cmg\y_offset[layer],cm_display_width,cm_display_height,layer)

End Function



Function CMAP_blit (layer,offsetx=0,offsety=0)
	; This plonks the contents of "img" to the screen at the right places, though the contents
	; of the "img" drawn as-is looks kinda' odd as it will appear to have been rolled in the x
	; and y axis.
	
	; Try un-commenting the following line to see exactly how the screen display works and it'll
	; help you gain a better understanding of why this method of scrolling is so fast (you'll need
	; to comment out the four following lines as well or they'll just draw over it).
	
;	DrawImageRect cmg\img[layer],0,0,0,0,screenwidth,screenheight

	DrawImageRect cm_cmg\img[layer] , offsetx , offsety , cm_cmg\x_divider[layer] , cm_cmg\y_divider[layer] , cm_display_width-cm_cmg\x_divider[layer] , cm_display_height-cm_cmg\y_divider[layer] ; bottom-right chunk of the screen
	
	DrawImageRect cm_cmg\img[layer] , (cm_display_width-cm_cmg\x_divider[layer])+offsetx , (cm_display_height-cm_cmg\y_divider[layer])+offsety , 0 , 0 , cm_cmg\x_divider[layer] , cm_cmg\y_divider[layer] ; top-left chunk of the screen

	DrawImageRect cm_cmg\img[layer] , offsetx , (cm_display_height-cm_cmg\y_divider[layer])+offsety , cm_cmg\x_divider[layer] , 0 , cm_display_width-cm_cmg\x_divider[layer] , cm_cmg\y_divider[layer] ; bottom-left chunk of the screen (I think)
 
	DrawImageRect cm_cmg\img[layer] , (cm_display_width-cm_cmg\x_divider[layer])+offsetx , offsety , 0 , cm_cmg\y_divider[layer] , cm_cmg\x_divider[layer] , cm_display_height-cm_cmg\y_divider[layer] ; top-right chunk of the screen (again, I think)

End Function



Function CMAP_clear_layer (layer)
	; Clear the given layer

	SetBuffer ImageBuffer(cm_cmg\img[layer])
	Cls
	SetBuffer BackBuffer()
	
End Function

	

Function CMAP_create_layer (layer,start_array,end_array,maskr=0,maskg=0,maskb=0)
	; Should be called to set up the globals. If you don't call it then a horrible monster will eat your eyes out.
	
	; layer = The raster layer number
	; start_array = This is the first layer in the array where tiles for this layer are drawn from
	; end_array = This is the last layer in the array where tiles for this layer are drawn from
	; maskr, maskg and maskb are preset to bright pink and are the mask colours for this layer
	
	; For instance, assume your map structure has 5 layers to it (numbered 0 to 4 naturally) and you want the third
	; drawn (rastered) layer (which in an array would be number 2) to contain a composite of map layers 2 to 4, with
	; preset mask colours, you'd call:
	
	; CMAP_create_layer (2,2,4)

	cm_cmg\x_divider [layer] = 0
	cm_cmg\y_divider [layer] = 0
	cm_cmg\x_offset [layer] = 0
	cm_cmg\y_offset [layer] = 0
	cm_cmg\array_start_layer [layer] = start_array
	cm_cmg\array_end_layer [layer] = end_array
	
	cm_cmg\img [layer] = CreateImage (cm_display_width, cm_display_height)
	MaskImage cm_cmg\img[layer],maskr,maskg,maskb

End Function



Function CMAP_initialise_layer (layer,x,y)
	; This will erase the contents of a layer, set it's new position, reset the divider and then fill it with tiles again.
	; As with "CMAP_refresh_tiles" the co-ordinates are world co-ordinates.

	cm_cmg\x_divider [layer] = 0
	cm_cmg\y_divider [layer] = 0
	cm_cmg\x_offset [layer] = x
	cm_cmg\y_offset [layer] = y

	CMAP_clear_layer (layer)
	CMAP_refresh_screen (layer)

End Function



Function CMAP_destroy_layer (layer)
	; This will free up the memory used by the cached image for this layer. Call this function after Game Over so you haven't
	; got a few meg of images clogging up the RAM when you don't need them.

	If cm_cmg\img [layer] > 0
		FreeImage cm_cmg\img [layer]
		cm_cmg\img [layer] = 0
	EndIf

End Function
