; ID: 1729
; Author: Fuller
; Date: 2006-06-07 16:52:12
; Title: Coder2d(for beginning your code)
; Description: Generates Base Code

Type typ
Field name$
Field playmove$
Field fielamount
Field fiel$
Field val
Field pre$
Field fiel2$
Field val2
Field fiel3$
Field val3
Field fiel4$
Field val4
Field fiel5$
Field val5
End Type  

Type image
Field name$
Field img$
Field r$
Field g$
Field b$
Field link$
End Type 

Type var
Field name$
Field val
End Type 

While Not KeyDown(1)

code=WriteFile("Code.txt") 

Print "-----------------"
Print "Auto Code Creator"
Print "By Nicholas F."
Print "-----------------"
Print ""

Print "Graphics mode"
Print "-------------" 
Print "code will terminate if wrong mode set"
gx=Input$ ("Graphics x size: ")
gy=Input$ ("Graphics y size: ")   

WriteLine(code,"Graphics " + gx + "," + gy)
WriteLine(code,"SetBuffer Backbuffer()")
WriteLine(code,"Automidhandle False")    
WriteLine(code,"") 

Print ""
Print "-------"
Print "Types"
types=Input$("Amount of types: ")   
Print "Type Fields"
For x=1 To types
t.typ=New typ
t\name$=Input$ ("Name of type: ")
t\pre$=Input$ ("Prefix for blank.blank: ")
WriteLine(code,"Type " + t\name$)
t\playmove$=Input$ ("May player move image")
t\fielamount=Input$ ("Amount of fields(min2:max5): ")
;2
If t\fielamount=2
t\fiel$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel$)
Print "Value of Field"
t\val=Input$ ("Value: ") 
t\fiel2$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel2$)
Print "Value of Field"
t\val2=Input$ ("Value: ") 
;3
Else If t\fielamount=3
t\fiel$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel$)
t\val=Input$ ("Value: ") 
t\fiel2$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel2$)
Print "Value of Field"
t\val2=Input$ ("Value: ") 
t\fiel3$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel3$)
Print "Value of Field"
t\val3=Input$ ("Value: ") 
;4
Else If t\fielamount=4  
t\fiel$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel$)
Print "Value of Field"
t\val=Input$ ("Value: ") 
t\fiel2$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel2$)
Print "Value of Field"
t\val2=Input$ ("Value: ") 
t\fiel3$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel3$)
Print "Value of Field"
t\val3=Input$ ("Value: ") 
t\fiel4$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel4$)
Print "Value of Field"
t\val4=Input$ ("Value: ") 
;5
Else If t\fielamount=5
t\fiel$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel$)
Print "Value of Field"
t\val=Input$ ("Value: ") 
t\fiel2$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel2$)
Print "Value of Field"
t\val2=Input$ ("Value: ") 
t\fiel3$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel3$)
Print "Value of Field"
t\val3=Input$ ("Value: ") 
t\fiel4$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel4$)
Print "Value of Field"
t\val4=Input$ ("Value: ") 
t\fiel5$=Input$ ("Fieldname: ")   
WriteLine(code,"Field " + t\fiel5$)
Print "Value of Field"
t\val5=Input$ ("Value: ") 
EndIf 

WriteLine(code,"End Type")
WriteLine(code,"")  
WriteLine(code,t\pre$ + "." + t\name$ + "=New " + t\name$) 

If t\fielamount=1
WriteLine(code,t\pre$ + "\" + t\fiel + "=" + t\val)
Else If t\fielamount=2
WriteLine(code,t\pre$ + "\" + t\fiel + "=" + t\val)
WriteLine(code,t\pre$ + "\" + t\fiel2 + "=" + t\val2)
Else If t\fielamount=3
WriteLine(code,t\pre$ + "\" + t\fiel + "=" + t\val)
WriteLine(code,t\pre$ + "\" + t\fiel2 + "=" + t\val2)
WriteLine(code,t\pre$ + "\" + t\fiel3 + "=" + t\val3)
Else If t\fielamount=4
WriteLine(code,t\pre$ + "\" + t\fiel + "=" + t\val)
WriteLine(code,t\pre$ + "\" + t\fiel2 + "=" + t\val2)
WriteLine(code,t\pre$ + "\" + t\fiel3 + "=" + t\val3)
WriteLine(code,t\pre$ + "\" + t\fiel4 + "=" + t\val4)
Else If t\fielamount=5
WriteLine(code,t\pre$ + "\" + t\fiel + "=" + t\val)
WriteLine(code,t\pre$ + "\" + t\fiel2 + "=" + t\val2)
WriteLine(code,t\pre$ + "\" + t\fiel3 + "=" + t\val3)
WriteLine(code,t\pre$ + "\" + t\fiel4 + "=" + t\val4)
WriteLine(code,t\pre$ + "\" + t\fiel5 + "=" + t\val5)
EndIf 

WriteLine(code,"")

Next  

Print "----------------"
Print "Images"
Print ""
Print "Loadimages"
Print ""
imnum=Input$ ("How many images to load: ")
For z=1 To imnum
i.image=New image
i\img$=Input$ ("Image Path(qoutes): ") 
i\name$=Input$ ("Name of Image: ")
i\r$=Input$ ("MaskImage(Red): ") 
i\g$=Input$ ("MaskImage(Green): ") 
i\b$=Input$ ("MaskImage(Blue): ") 
Print "Link image to Type"
i\link$=Input$ ("Type name: ")
Next 

For i.image=Each image
WriteLine(code,"Global " + i\name$ + "=" + "LoadImage(" + i\img$ + ")")   
WriteLine(code,"Maskimage " + i\name$ + "," + i\r + "," + i\g + "," + i\b)
Next 

WriteLine(code,"") 

Print ""
Print "--------------"
Print "Variables"
.varstart
v.var=New var
v\name$=Input$ ("Variable: ")
v\val=Input$ ("Value: ")
WriteLine(code,"Global " + v\name$ + "=" + v\val) 
res$=Input$ ("New Variable? ")
If res$="yes" Or "Yes" Or "y" Or "Y"
Goto varstart
EndIf 

WriteLine(code,"")
WriteLine(code,"While Not Keydown(1)")
WriteLine(code,"")
WriteLine(code,"Moveimage()") 
WriteLine(code,"Drawimages()")
WriteLine(code,"")
WriteLine(code,"Wend")    
WriteLine(code,"")

Print "-------------"
Print "Keys-Moveimage"
Print ""
WriteLine(code,"Function Moveimage()")
WriteLine(code,"")
For t.typ=Each typ
If t\playmove$ = "yes" Or "Yes" Or "y" Or "Y"
WriteLine(code,"For " + t\pre$ + "." + t\name$ + "=" + "Each " + t\name$)
WriteLine(code,"If Keydown(203) Then " + t\pre$ + "\" + t\fiel$ + "=" + t\pre$ + "\" + t\fiel$ + "- 3"  )
WriteLine(code,"If Keydown(205) Then " + t\pre$ + "\" + t\fiel$ + "=" + t\pre$ + "\" + t\fiel$ + "+ 3"  )
WriteLine(code,"If Keydown(200) Then " + t\pre$ + "\" + t\fiel2$ + "=" + t\pre$ + "\" + t\fiel2$ + "- 3"  )
WriteLine(code,"If Keydown(208) Then " + t\pre$ + "\" + t\fiel2$ + "=" + t\pre$ + "\" + t\fiel2$ + "+ 3"  )
WriteLine(code,"Next")
WriteLine(code,"")
EndIf 
Next 

WriteLine(code,"End Function")
WriteLine(code,"") 

WriteLine(code,"Function Drawimages()")
WriteLine(code,"")
WriteLine(code,"Cls")
WriteLine(code,"")
For i.image=Each image
For t.typ=Each typ
If i\link$=t\name$
WriteLine(code,"For " + t\pre$ + "." + t\name$ + "= Each " + t\name$)
WriteLine(code,"Drawimage " + i\name$ + "," + t\pre$ +"\" + t\fiel$ + "," + t\pre$ + "\" + t\fiel2$)
WriteLine(code,"Next")
EndIf
Next 
Next  
WriteLine(code,"")
WriteLine(code,"Flip")
WriteLine(code,"")
WriteLine(code,"End Function")

  
End 
Wend
