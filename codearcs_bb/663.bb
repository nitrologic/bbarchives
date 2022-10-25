; ID: 663
; Author: PsychicParrot
; Date: 2003-05-01 05:56:59
; Title: TerrainY equivalent for mesh terrains
; Description: TerrainY equivalent for mesh terrains

Function getmeshterrainy#(ent)

pick=LinePick(EntityX(ent),EntityY(ent)+20,EntityZ(ent),0,-30,0)
Return (PickedY())

End Function
