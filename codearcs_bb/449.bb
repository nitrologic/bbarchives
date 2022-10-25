; ID: 449
; Author: Shagwana
; Date: 2002-10-03 15:55:01
; Title: Rectangle/Box overlap code (Boolean way)
; Description: A faster way to tell if a rect overlaps!

;Coded by Stephen Greener
;Shagwana@sublimegames.com

;Returns 0 if no overlap else <any> other number if it does!
;The source and dest boxs need to be sorted so that coord 1 are top-left and coords 2 are bottom-right.
Function RectOverlap(iSourceXPos1,iSourceYPos1,iSourceXPos2,iSourceYPos2,iDestXPos1,iDestYPos1,iDestXPos2,iDestYPos2)
  Return ((((iDestXPos2-iSourceXPos1) Xor (iDestXPos1-iSourceXPos2)) And ((iDestYPos1-iSourceYPos2) Xor (iDestYPos2-iSourceYPos1))) And $80000000)
  End Function
;Should beat the blitz method too, but might do strange things on negative coords.
