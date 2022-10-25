; ID: 2027
; Author: _33
; Date: 2007-06-02 02:04:37
; Title: Automatic Starfield generator
; Description: It gives you an animated starfield

; FILENAME: func_stars.bb
;----------------------------------------------------------------------------------------
; starfield management
;----------------------------------------------------------------------------------------
; Define the type for each stars in a starfield
Type star_info
   Field ptr
   Field xpos#
   Field ypos#
   Field zpos#
   Field velocity#
End Type

Global star_count% = 0

Function AnimateStarfield(x#=0,y#=0,z#=0,occ%=6)
   Local overlap_x# = Rnd(0, 2500.0)
   Local overlap_z# = Rnd(0, 5000.0)

   If Rand(1,occ%) = occ% Then AddStar(x#, y#, z#, overlap_x#, overlap_z#)

   For star.star_info = Each star_info
       star\xpos# = star\xpos# + star\velocity#

       If (star\xpos#) > (x# + overlap_x# + 2500.0) Then
          FreeEntity star\ptr
          Delete star.star_info
          star_count% = star_count% - 1
       Else
          ;PositionMesh star\ptr, star\velocity#, 0, 0
          PositionEntity star\ptr, star\xpos#, star\ypos#, star\zpos#
       EndIf
   Next
End Function


Function DeleteAllStars()
   For star.star_info = Each star_info
      FreeEntity star\ptr
      Delete star.star_info
   Next
   star_count% = 0
End Function


Function AddStar(xref#, yref#, zref#, overlap_x#, overlap_z#)
   Local starsize# = Rnd(0.5,2.01)
   Local speed# = Rnd(2.0,8.0)

   If starsize# > 2 Then
      starsize# = 18.0 - speed#
   EndIf

   star.star_info = New star_info

;   star\ptr      = make_star (2 + Int starsize#)
   star\ptr       = CreateSphere(2 + Int starsize#)
   star\xpos#     = xref# - overlap_x# - 2500.0
   star\zpos#     = zref# - overlap_z# + 2500.0
   star\ypos#     = 650.0 + Rnd(0, 350.0) 
   star\velocity# = speed#

  ; EntityTexture star\ptr, ptr_texture(127),0,1
   EntityBlend star\ptr, 3
   EntityFX star\ptr, 1
   ScaleEntity star\ptr, starsize#, starsize#, starsize#
   EntityColor star\ptr, 255, 255, 255
   star_count% = star_count% + 1
;   PositionEntity star\ptr, star\xpos#, star\ypos#, star\zpos#

End Function
