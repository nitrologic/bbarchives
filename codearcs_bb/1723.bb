; ID: 1723
; Author: bradford6
; Date: 2006-05-26 13:41:09
; Title: array of types
; Description: array of types within a type

Graphics 640,480


Type Tveggie
	Field name:String
	Field freshness:Int

End Type


Type Tpot
	Field name:String
	Field x:Int
	Field y:Int
	Field PotContents:Tveggie[10]
	Field MAXVEGGIES:Int = 1000
	Field Veggiecount:Int
	
	Method AddVeggie(vname:String="Generic Vegetable",fresh:Int=100) 
		
		veggiecount:+1
		self.PotContents = self.PotContents[..veggiecount]
		Print "Potcontents="+Len(potcontents)
		PotContents[veggiecount-1] = New Tveggie
		PotContents[veggiecount-1].name = vname
		PotContents[veggiecount-1].freshness = fresh
	
	End Method
	
	Method DeleteVeggie(vegname:String,all:Int=-1,index:Int=-1)
		For Local t:Int = 0 To veggiecount - 1
			If vegname = PotContents[t].name 
			    Local deleted:Int = 1
				Print "Index:="+t
				Print "Deleting:"+PotContents[t].name
				
				Local PL:Int = Len(self.PotContents)
				Print "PL="+PL
				
				
				For XX = 0 To PL-1
					Print "xx:"+xx+"  potindex:"+t
					If XX+potindex>PL-1 Then Exit
					
					Print self.Potcontents[xx].name+"::"+self.potcontents[xx+potindex].name
					
					If XX = t Then potindex=1
					If XX = PL-1 Then potindex=0
					self.Potcontents[xx] = self.potcontents[XX+potindex]
								
				Next
				
				
				veggiecount:-1
				self.PotContents = self.PotContents[..veggiecount]
				Exit
				
				
			EndIf
			
		Next
		
		Return Deleted
	End Method
	
	
	
	
	Method Display()
		SetColor 255,0,0
		DrawText name,x,y
		SetColor 255,255,0
		Local TH=TextHeight("H")
		For Local t:Int = 0 To veggiecount - 1
	     	DrawText t+":"+PotContents[t].name,x,y+(TH+t*TH)
		Next
		DrawText "Total Veggies:"+veggiecount,x,y+(TH*(veggiecount+1))
		DrawText "array size:"+Len(potcontents),x,y+(TH*(veggiecount+2))

	
	End Method
	
	Method Exists(vegname:String)
		For Local t:Int = 0 To veggiecount - 1
			If vegname = PotContents[t].name 
				Return True
			EndIf
		Next
	
	Return False
	
	End Method
	
	Function create:Tpot(pname:String,xp,yp)
		Local temp:Tpot = New Tpot
		temp.name = pname
		temp.x = xp
		temp.y = yp
		
	     temp.PotContents[0] = New Tveggie
		
	
		
		Return temp
	End Function
End Type


Local Dinner:Tpot = Tpot.create("Bill's Stew",100,100)
Dinner.AddVeggie("Carrot",5)
Dinner.AddVeggie("Beet",80)
Dinner.AddVeggie("Lettuce",5)
Dinner.AddVeggie("Dirty Sock",80)
Dinner.AddVeggie("Okra",80)
Dinner.AddVeggie("Peas",80)
Dinner.AddVeggie("Telephone",80)



Repeat
	
	If Dinner.exists("Dirty Sock") = 1 Then DrawText "Left Click to remove Sock",MouseX(),MouseY()
	If Dinner.exists("Telephone") = 1Then DrawText "Right Click to remove Telephone",MouseX(),MouseY()+16
	Dinner.display()
	If MouseDown(1)=1 
		d = Dinner.DeleteVeggie("Dirty Sock")
		Print "Deleted = "+d
	EndIf
	If MouseDown(2)=1 
		d = Dinner.DeleteVeggie("Telephone")
		Print "Deleted = "+d
	EndIf




Flip
Cls
Until KeyDown(KEY_ESCAPE)
