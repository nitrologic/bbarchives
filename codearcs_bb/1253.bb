; ID: 1253
; Author: Malice
; Date: 2005-01-06 06:26:47
; Title: 2D Colour chooser
; Description: Selects from a range of 2D colours

Function TxtCol(col$)

Select col$

Case "white"
RED=225
GREEN=225
BLUE=225

Case "brilliant"
RED=255
GREEN=255
BLUE=255

Case "black"
RED=0
GREEN=0
BLUE=0

Case "silver"
RED=180
GREEN=180
BLUE=195

Case "red"
RED=225
GREEN=0
BLUE=0

Case "emerald"
RED=0
GREEN=225
BLUE=0

Case "blue"
RED=0
GREEN=0
BLUE=225

Case "yellow"
RED=225
GREEN=225
BLUE=0

Case "magenta"
RED=225
GREEN=0
BLUE=225

Case "orange"
RED=195
GREEN=170
BLUE=0

Case "brown"
RED=80
GREEN=95
BLUE=0

Case "cyan"
RED=0
GREEN=225
BLUE=225

Case "crimson"
RED=80
GREEN=0
BLUE=0

Case "navy"
RED=0
GREEN=0
BLUE=80

Case "green"
RED=0
GREEN=80
BLUE=0

Case "light grey"
RED=150
GREEN=150
BLUE=150

Case "dark grey"
RED=80
GREEN=80
BLUE=80

Default
RED=255
GREEN=255
BLUE=255

End Select

Color RED,GREEN,BLUE

End Function

;Just add more colours as necessary (to improve speed, try to keep most popular colours at the top of the 'Select' list.
