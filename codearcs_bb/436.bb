; ID: 436
; Author: Smokey
; Date: 2002-09-26 04:17:09
; Title: Simple progress bar
; Description: 3D progress bar

;A very simple Progress bar function for your 3D code !
;By Smokey , if there a better method to do this let me know
;komodo36@hotmail.com

;use it by calling function Sprite2D(percentage) before loading your stuff
; and just before you entering your loop just use 'FreeEntity sprite' to clear the last sprite

;ie

;Sprite2D(1) 

;Sprite2D(10)

;Sprite2D(20)

; etc 


Function Sprite2D(pourcent)

Select pourcent

Case 1
sprite=LoadSprite("01%.jpg")
Case 10
FreeEntity sprite
sprite=LoadSprite("10%.jpg")
Case 20
FreeEntity sprite
sprite=LoadSprite("20%.jpg")
Case 30
FreeEntity sprite
sprite=LoadSprite("30%.jpg")
Case 40
FreeEntity sprite
sprite=LoadSprite("40%.jpg")
Case 50
FreeEntity sprite
sprite=LoadSprite("50%.jpg")
Case 60
FreeEntity sprite
sprite=LoadSprite("60%.jpg")
Case 70
FreeEntity sprite
sprite=LoadSprite("70%.jpg")
Case 80
FreeEntity sprite
sprite=LoadSprite("80%.jpg")
Case 90
FreeEntity sprite
sprite=LoadSprite("90%.jpg")
Case 100
FreeEntity sprite
sprite=LoadSprite("100%.jpg")
End Select

ScaleSprite sprite,6,0.33
PositionEntity sprite,0,-6,0		


End Function
