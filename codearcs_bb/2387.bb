; ID: 2387
; Author: Underwood
; Date: 2008-12-25 17:31:43
; Title: Point In Oval
; Description: Is a point inside of an oval?

Function PointInOval:Int(px#,py#,ox#,oy#,width#,height#)

	Return((px - ox)^2 / width^2 + (py - oy)^2 / height^2 < 1)

End Function
