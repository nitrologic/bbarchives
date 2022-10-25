; ID: 447
; Author: BlitzSupport
; Date: 2002-10-02 22:52:26
; Title: Terragen terrain loader
; Description: Loads a Terragen map as a Blitz terrain (mesh only -- no textures!)

; -----------------------------------------------------------------------------
; Terragen map (.ter) loader...
; -----------------------------------------------------------------------------
; Works only with maps created by Terragen 0.8.44 -- not guaranteed to work
; with later maps, or ".ter" maps created with other tools! Yes, it's nasty
; ol' hard-codin' time...
; -----------------------------------------------------------------------------
; james @ hi - toro . com
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; Demo -- use CURSORS plus A and Z...
; -----------------------------------------------------------------------------

Graphics3D 640, 480

cam = CreateCamera ()
CameraClsColor cam, 64, 96, 128
CameraRange cam, 0.1, 9000

light = CreateLight ()
TurnEntity light, 0, -45, 0

terrain = LoadTerragenMap ("blah.ter", 5000, 1, True) ; CHANGE NAME OF TERRAIN FILE!

PositionEntity cam, 0, TerrainY (terrain, 0, 0, 0) + 250, 0
TurnEntity cam, 20, -45, 0

Repeat

	If KeyDown (203) TurnEntity cam, 0, 1, 0, 1
	If KeyDown (205) TurnEntity cam, 0, -1, 0, 1
	If KeyDown (200) TurnEntity cam, 1, 0, 0
	If KeyDown (208) TurnEntity cam, -1, 0, 0
	If KeyDown (30) MoveEntity cam, 0, 0, 10
	If KeyDown (44) MoveEntity cam, 0, 0, -10
	
	UpdateWorld
	RenderWorld

	Flip
	
Until KeyHit (1)

End

; -----------------------------------------------------------------------------
; LoadTerragenMap (terr$, [detail, scaler#, autotex, texwidth, texheight])
; -----------------------------------------------------------------------------
; Loads a Terragen (0.8.44) terrain map and returns a Blitz terrain handle...
; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; Required parameters...
; -----------------------------------------------------------------------------

; 	terr$		-- a Terragen [0.8.44] terrain file (.ter)

; -----------------------------------------------------------------------------
; Optional parameters...
; -----------------------------------------------------------------------------

;	detail		-- terrain detail (see TerrainDetail commands docs)
; 	scaler#		-- amount to scale resulting terrain by
; 	autotex		-- True to apply crude texturing
; 	texwidth	-- over-ride texture width
; 	texheight	-- over-ride texture height

; 	Regarding the last two -- textures default to the size of the map (eg.
; 	256 x 256, 512 x 512, etc). If you over-ride the texture size, make sure
; 	it's SMALLER than the map or it'll screw up! You must set both -- best
; 	to use the same value for width AND height...

; -----------------------------------------------------------------------------
; Examples:
; -----------------------------------------------------------------------------

; terrain = LoadTerragenMap ("blah.ter")
; terrain = LoadTerragenMap ("blah.ter", 5000, 0.25, False)
; terrain = LoadTerragenMap ("blah.ter", 2000, 1, True, 64, 64)

Function LoadTerragenMap (terr$, detail = 2000, scaler# = 1.0, autotex = False, texwidth = 0, texheight = 0)

	tbuffer = GraphicsBuffer () ; Store current buffer in case we write to texture...

	total = FileSize (terr$)	; Total size of file

	ter = ReadFile (terr$)

	If ter

		; "Let's parse!" -- Sandra Bullock in forthcoming hi-tech action/hacking movie...

		; -------------------------------------------------------------------------
		; Terragen .ter identifier...
		; -------------------------------------------------------------------------
	
		For b = 1 To 16
			terrinfo$ = terrinfo$ + Chr (ReadByte (ter))
		Next
		
		If terrinfo$ <> "TERRAGENTERRAIN "
			RuntimeError "Not a Terragen terrain file!"
		EndIf
	
		; -------------------------------------------------------------------------
		; Size...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			sizeinfo$ = sizeinfo$ + Chr (ReadByte (ter))
		Next
		
		If sizeinfo$ <> "SIZE"
			RuntimeError "File corrupt in SIZE section!"
		EndIf
		
		size = ReadInt (ter)
		
		; -------------------------------------------------------------------------
		; X Points...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			xinfo$ = xinfo$ + Chr (ReadByte (ter))
		Next
		
		If xinfo$ <> "XPTS"
			RuntimeError "File corrupt in XPTS section!"
		EndIf
		
		xpts = ReadInt (ter)
	
		; -------------------------------------------------------------------------
		; Y Points...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			yinfo$ = yinfo$ + Chr (ReadByte (ter))
		Next
		
		If yinfo$ <> "YPTS"
			RuntimeError "File corrupt in YPTS section!"
		EndIf
		
		ypts = ReadInt (ter)
	
		; -------------------------------------------------------------------------
		; Scale...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			scaleinfo$ = scaleinfo$ + Chr (ReadByte (ter))
		Next
		
		If scaleinfo$ <> "SCAL"
			RuntimeError "File corrupt in SCAL section!"
		EndIf
		
		xscale# = ReadFloat (ter)
		yscale# = ReadFloat (ter)
		zscale# = ReadFloat (ter)
	
		terrain = CreateTerrain (xpts - 1)
		If terrain = 0 Then RuntimeError "Doh @ CreateTerrain!"
		TerrainDetail terrain, 5000, True
		
		; -------------------------------------------------------------------------
		; Planet radius...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			cradinfo$ = cradinfo$ + Chr (ReadByte (ter))
		Next
		
		If cradinfo$ <> "CRAD"
			RuntimeError "File corrupt in CRAD section!"
		EndIf
		
		crad# = ReadFloat (ter)
	
		; -------------------------------------------------------------------------
		; Curved terrain ("CRVM")... switches between 1 and 0, but uses 4 bytes...?
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			crvminfo$ = crvminfo$ + Chr (ReadByte (ter))
		Next
		
		If crvminfo$ <> "CRVM"
			RuntimeError "File corrupt in CRVM section!"
		EndIf
		
		crvm = ReadInt (ter)
		
		; -------------------------------------------------------------------------
		; Altitude data in 16-bit words...
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			altwinfo$ = altwinfo$ + Chr (ReadByte (ter))
		Next
		
		If altwinfo$ <> "ALTW"
			RuntimeError "File corrupt in ALTW section!"
		EndIf
		
		hscale	= ReadShort (ter)
		hbase	= ReadShort (ter)
		
		; -------------------------------------------------------------------------
		; Automatically create texture (if requested)...
		; -------------------------------------------------------------------------

		If autotex
			If (texwidth = 0) Or (texheight = 0)
				texwidth = xpts - 1
				texheight = ypts - 1
			EndIf
			tex = CreateTexture (texwidth, texheight)
			If tex
				divx# = (xpts - 1) / TextureWidth (tex)
				divy# = (ypts - 1) / TextureHeight (tex)			
				SetBuffer TextureBuffer (tex)
			EndIf
		EndIf
		
		; Start position for linear data traversal...
		
		xpt = 1
		ypt = ypts
	
		; -------------------------------------------------------------------------
		; Main data...
		; -------------------------------------------------------------------------

		While Not Eof (ter)
	
			height = ReadShort (ter)
			newheight# = (height / 65536.0) - 0.5 ; Blitz'll wrap the values (they're wrong :)
			
			If tex
				; Draw to texture... don't ask what all this means. Dunno.
				rgb = (255 - ((newheight + 0.5) * 127)) / 2
				Color rgb * 0.65, rgb * 0.85, rgb * 0.45	
				Plot (xpt - 1) / divx, (ypt - 1) / divy
			EndIf

			count = count + 1 ; Data byte count...

			If FilePos (ter) > (total - 7) Then Exit ; Reached end of data...

			; Traversing the linear data...
			
			xpt = xpt + 1: If xpt > xpts Then xpt = 1: ypt = ypt - 1
	
			; Applying the data...
			
			ModifyTerrain terrain, xpt - 2, ypts - (ypt - 2), newheight
					
		Wend
		
		; -------------------------------------------------------------------------
		; Two null characters...? Too lazy to check the docs now (which I only found AFTER figuring the format out!)
		; -------------------------------------------------------------------------
	
		ReadByte (ter)
		ReadByte (ter)
		
		; -------------------------------------------------------------------------
		; End of file marker ("EOF ")... 
		; -------------------------------------------------------------------------
	
		For b = 1 To 4
			eofinfo$ = eofinfo$ + Chr (ReadByte (ter))
		Next
		
		If eofinfo$ <> "EOF "
			RuntimeError "File corrupt in EOF[ ] section!"
		EndIf
	
		CloseFile ter
			
	EndIf

	If tex
		ScaleTexture tex, xpts - 1, ypts - 1
		EntityTexture terrain, tex
	EndIf
	
	TerrainDetail terrain, detail, True
	TerrainShading terrain, True
	ScaleEntity terrain, scaler * xscale, scaler * hscale * yscale, scaler * zscale

	SetBuffer tbuffer
	
	Return terrain
		
End Function
