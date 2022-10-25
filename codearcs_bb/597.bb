; ID: 597
; Author: Milky Joe
; Date: 2003-02-21 15:56:17
; Title: Zones Library
; Description: Create screen Zones and interact with them

; Library:			Zones
; Version:			1.0

; Author:			Leigh Bowers
;				Based on material provided by GFK (I think), but heavily modified and enhanced.
; Email:			leigh.bowers@curvesoftware.co.uk
; Homepage:		www.curvesoftware.co.uk/blitz

; SetZoneCount	Set the maximum number of zones to use (reserves some memory). 
; CreateZone		Define a zone. Must be between 1 and the number specified in SetZoneCount 
; DeleteZone		Delete a zone. 
; FreeZones		Delete all zones and free up reserved memory. 
; MouseZone()		Returns the number of the zone the mouse pointer is in. 
; ZoneProperty()	Returns either the X, Y, Width or Height of the given zone number.
; Zone()			Return the number of the zone at given X and Y co-ordinates.

Global gintZoneBank%, gintZoneCount%

Function SetZoneCount(Num%) 
    gintZoneCount = Num
    gintZoneBank = CreateBank(gintZoneCount * 8) 
End Function 

Function CreateZone(pintZone%, pintX%, pintY%, pintWidth%, pintHeight%) 
    intBankPosition% = (pintZone - 1) * 8 
    PokeShort gintZoneBank, intBankPosition, pintX
    PokeShort gintZoneBank, intBankPosition + 2, pintY
    PokeShort gintZoneBank, intBankPosition + 4, pintWidth
    PokeShort gintZoneBank, intBankPosition + 6, pintHeight
End Function

Function ZoneProperty%(pintZone%, pintProperty%)
	; Property: 1 = X, 2 = Y, 3 = Width, 4 = Height
	intBankPosition% = (pintZone - 1) * 8
	Return PeekShort (gintZoneBank, intBankPosition + ((pintProperty - 1) * 2))
End Function 

Function DeleteZone(pintZone%) 
	intBankPosition% = (pintZone - 1) * 8
	PokeShort gintZoneBank, intBankPosition, 0
	PokeShort gintZoneBank, intBankPosition+ 2, 0
	PokeShort gintZoneBank, intBankPosition+ 4, 0
	PokeShort gintZoneBank, intBankPosition+ 6, 0
End Function

Function FreeZones() 
	gintZoneCount = 0
	FreeBank gintZoneBank 
End Function 

Function MouseZone%() 
	Return Zone(MouseX(), MouseY())
End Function

Function Zone%(pintX%, pintY%)

	intZoneNum% = 0

	intCurrentZone% = 0
	While ((intCurrentZone < gintZoneCount) And (intZoneNum = 0))
		intBankPosition% = intCurrentZone * 8 
		intX1% = PeekShort(gintZoneBank, intBankPosition) 
		intY1% = PeekShort(gintZoneBank, intBankPosition + 2) 
		intX2% = (intX1 + PeekShort(gintZoneBank, intBankPosition + 4))
		intY2% = (intY1 + PeekShort(gintZoneBank, intBankPosition + 6))
		If ((((pintX - intX1) Xor (pintX - intX2)) And ((pintY - intY1) Xor (pintY - intY2))) And $80000000) Then
			intZoneNum = intCurrentZone + 1
		End If 
		intCurrentZone = intCurrentZone + 1
	Wend
	
	Return intZoneNum 

End Function
