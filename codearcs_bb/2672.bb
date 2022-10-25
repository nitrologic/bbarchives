; ID: 2672
; Author: Streaksy
; Date: 2010-03-22 05:17:10
; Title: Streaksy DATABASE Suite 1.4 - Lightening-fast DB building, reading, writing, etc. with definable field types
; Description: Universal, intuitive and portable database library.  Easily adopted and very versatile.  Uses banks so NO MEMORY IS WASTED!  Now with built-in database editor.  (FOURTH UPDATED VERSION: NOW v1.4)

Const MaxDBs=32				;Maximum number of databases
Const MaxFields=64			;Maximum number of fields in a database

Const DB_Byte=0 ;1 byte 			(0 to 255)
Const DB_SByte=1 ;2 byte 			(-128 to 127)
Const DB_Short=2 ;3 bytes			(0 to 65535
Const DB_SShort=3 ;4 bytes			(-32768 to 32767)
Const DB_Int=4  ;5 bytes			(- to )
Const DB_Float=5;6 bytes			(Anyone know the range of a float?)
Const DB_String=6;string size defined by DBFieldSize+2 bytes
Const DB_List=7;multiple choice	(0 to 255)

Global DBs
Dim DBIDAt(MaxDBs)
Dim DBName$(MaxDBs)
Dim DBActive(MaxDBs)
Dim DBBank(MaxDBs)
Dim DBFields(MaxDBs)
Dim DBRecordSize(MaxDBs)
Dim DBRecords(MaxDBs)
Dim DBDels(MaxDBs)
Dim DBField$(MaxDBs,MaxFields)
Dim DBFieldList$(MaxDBs,MaxFields) ;for multiple choice lists
Dim DBFieldLen(MaxDBs,MaxFields)
Dim DBFieldType(MaxDBs,MaxFields)
Dim DBFieldSize(MaxDBs,MaxFields) ;for strings
Dim DBFieldOffset(MaxDBs,MaxFields)
Global BasicDBMemoryUsage=(MaxDBs*4*7) + (MaxDBs*MaxFields*4*5)
	Global DBMaxQueries=50,DBQueries
	Dim DBQueryOp(DBMaxQueries)
	Dim DBQueryField(DBMaxQueries)
	Dim DBQueryValString$(DBMaxQueries)
	Dim DBQueryValFloat#(DBMaxQueries)
	Dim DBQueryValInt(DBMaxQueries)

Const MaxQueryResults=10000
Global DBListedRecords ;Query results are stored here
Dim DBListedRecord(MaxQueryResults)

Const MaxRecordCache=200
Dim DBRecordsInCache(MaxDBs)
Dim DBRecordCacheID(MaxDBs,MaxRecordCache)
Dim DBRecordCacheIndex(MaxDBs,MaxRecordCache)





; CRUDE DEMO
AppTitle "Database Demo"
Graphics 1024,768,32,2

;First create a database by defining it then building it
	DB=DefineDB("Items")
	AddStringField DB,"Name"
	AddListField DB,"Type","Weapon,Armour,Potion,Loot"
	AddByteField DB,"Level"
	AddIntField DB,"Cost"
	AddFloatField DB,"Weight"
	BuildDB DB

;Add a few records
;For t=1 To 2000:nowt=addrecord(db):Next ;Loads of records at start to see what difference it makes to speed

	r1=AddRecord(DB)
		SetDataString DB,r1,"Name","Longsword"
		SetDataString DB,r1,"Type","Weapon"
		SetData DB,r1,"Level",1
		SetData DB,r1,"Cost",8
		SetDataFloat DB,r1,"Weight",.5
	r2=AddRecord(DB)
		SetDataString DB,r2,"Name","Chainmail"
		SetDataString DB,r2,"Type","Armour"
		SetData DB,r2,"Level",2
		SetData DB,r2,"Cost",11
		SetDataFloat DB,r2,"Weight",.9
	r3=AddRecord(DB)
		SetDataString DB,r3,"Name","Elixir"
		SetDataString DB,r3,"Type","Potion"
		SetData DB,r3,"Level",1
		SetData DB,r3,"Cost",4
		SetDataFloat DB,r3,"Weight",.16
	r4=AddRecord(DB)
		SetDataString DB,r4,"Name","Jewel"
		SetDataString DB,r4,"Type","Loot"
		SetData DB,r4,"Level",2
		SetData DB,r4,"Cost",17
		SetDataFloat DB,r4,"Weight",.01
	r5=AddRecord(DB)
		SetDataString DB,r5,"Name","Platemail"
		SetDataString DB,r5,"Type","Armour"
		SetData DB,r5,"Level",4
		SetData DB,r5,"Cost",25
		SetDataFloat DB,r5,"Weight",1.6
	r6=AddRecord(DB)
		SetDataString DB,r6,"Name","Gold Nugget"
		SetDataString DB,r6,"Type","Loot"
		SetData DB,r6,"Level",3
		SetData DB,r6,"Cost",19
		SetDataFloat DB,r6,"Weight",.21
	r7=AddRecord(DB)
		SetDataString DB,r7,"Name","Staff"
		SetDataString DB,r7,"Type","Weapon"
		SetData DB,r7,"Level",1
		SetData DB,r7,"Cost",2
		SetDataFloat DB,r7,"Weight",.3


.restart
Cls:Locate 0,0
Print "A small demo database has been prepared.  Select an option:":Print ""

Print "ESC: Quit"
Print "1: Edit database"
Print "2: Measure database & do time trial (Requires the longsword to be still in the database)"

Repeat
If KeyHit(2) Then EditDB DB:Goto restart
If KeyHit(1) Then End
Until KeyHit(3)
FlushKeys

Cls:Locate 0,0
Color 255,255,0
Print "ADJUSTABLE ALLOCATION CONSTANTS:"
Color 155,255,0
Print "Current MaxDBs = "+MaxDBs
Print "Current MaxFields = "+MaxFields
Color 255,255,255
Print "The database library itself currently uses up "+((BasicDBMemoryUsage)/1024)+" kilobytes.  (Depends on MaxDBs and MaxFields)"
Print ""
Color 255,255,0
Print "USED BY DEMO DATABASE:"
Color 155,255,0
Print "Fields = "+DBFields(DB)
Print "Records = "+DBRecords(DB)
Print ""
Print ""
Color 255,255,255
Print "The demo database bank is "+BankSize(DBBank(DB))+" bytes. ("+(BankSize(DBBank(DB))/1024)+" kilobytes)"
Print "There are "+DBRecords(DB)+" records in this demo database and each record takes up "+DBRecordSize(DB)+" bytes."
reps#=100000
Color 255,255,0
Locate 0,200
Color 255,255,155:Print "MAIN FUNCTION EXECUTION TIME TRIALS:":Color 255,155,255:Print ""

ms1#=MilliSecs()
For t=1 To reps
GetData(DB,r1,"Cost")
Next
ms2#=MilliSecs()
Print "GetData() - "+Int(((reps/(ms2-ms1))*1000))+" times per second"

ms1#=MilliSecs()
For t=1 To reps
SetData(DB,r1,"Cost",0)
Next
ms2#=MilliSecs()
Print "SetData() - "+Int(((reps/(ms2-ms1))*1000))+" times per second"

Print "":Color 255,255,155:Print "SEEK FUNCTION EXECUTION TIME TRIALS:":Color 255,155,255:Print ""

ms1#=MilliSecs()
For t=1 To reps
FindRecord(DB,"Level","1")
Next
ms2#=MilliSecs()
Print "FindRecord() - "+Int(((reps/(ms2-ms1))*1000))+" times per second (SEARCHING BY A NUMERIC FIELD)"

ms1#=MilliSecs()
For t=1 To reps
FindRecord(DB,"Name","Longsword")
Next
ms2#=MilliSecs()
Print "FindRecord() - "+Int(((reps/(ms2-ms1))*1000))+" times per second (SEARCHING BY A STRING FIELD)"

Print "":Color 255,255,155:Print "QUERY FUNCTION EXECUTION TIME TRIALS:":Color 255,155,255:Print ""

ms1#=MilliSecs()
For t=1 To reps
ListRecords DB,"Level=1"
Next
ms2#=MilliSecs()
Print "ListRecords() (QUERY!!) - "+Int(((reps/(ms2-ms1))*1000))+" times per second (using simple query: "+Chr(34)+"Level=1"+Chr(34)+")"

Print ""
Print ""
Color 255,255,255
Print "Note: Execution speeds of ListRecords() will be slower with bigger databases."
WaitKey:Goto restart
















;*********** PUBLIC FUNCTIONS


Function ListRecords(DB,Query$)
lq=Len(query)	;tokenise queries
DBQueries=0
qu$=""
For qa=1 To lq ;go trough query list
m$=Mid(query,qa,1)
If m="," Or qa=lq Then
If qa=lq Then qu=qu+m
DBqueries=DBqueries+1
	phase=0:q1$="":q2$="":q3$=""
	For zz=1 To Len(qu) ;tokenise query components
	mmm$=Mid(qu,zz,1)
	sym=(mmm="<" Or mmm="=" Or mmm=">")
	If phase=0 And sym=0 Then q1=q1+mmm
	If phase=0 And sym Then phase=1
	If phase=1 And sym Then q2=q2+mmm
	If phase=1 And sym=0 Then phase=2
	If phase=2 And sym=0 Then q3=q3+mmm
	Next
		If q2="=" Then DBQueryOp(DBqueries)=1
		If q2="<" Then DBQueryOp(DBqueries)=2
		If q2=">" Then DBQueryOp(DBqueries)=3
		If q2="<=" Or q2="=<" Then DBQueryOp(DBqueries)=4
		If q2="=>" Or q2=">=" Then DBQueryOp(DBqueries)=5
		If q2="<>" Or q2="><" Then DBQueryOp(DBqueries)=6
	DBQueryField(DBQueries)=FindField(DB,q1):fld=DBQueryField(DBQueries)
	If DBFieldType(DB,Fld)=DB_Byte Then DBQueryValInt(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_SByte Then DBQueryValInt(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_Short Then DBQueryValInt(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_SShort Then DBQueryValInt(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_Int Then DBQueryValInt(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_Float Then DBQueryValFloat(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_String Then DBQueryValString(DBQueries)=q3
	If DBFieldType(DB,Fld)=DB_List Then DBQueryValString(DBQueries)=q3
qu=""
Else
qu=qu+m
EndIf
Next
DBListedRecords=0
For r=1 To DBRecords(DB)
	doit=1
	For q=1 To DBQueries
	; = (Equals)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=1 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=1 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=1 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=1 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=1 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=1 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)=DBQueryValFloat(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_String Then If DBQueryOp(q)=1 Then If Not (GetDataStringSimple(DB,DBQueryField(q),r)=DBQueryValString(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_List Then If DBQueryOp(q)=1 Then If Not (GetDataStringSimple(DB,DBQueryField(q),r)=DBQueryValString(q)) Then doit=0:Exit
	; < (Less Than)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=2 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=2 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=2 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=2 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=2 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=2 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)<DBQueryValFloat(q)) Then doit=0:Exit
	; > (More Than)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=3 Then If Not (GetDataSimple(DB,DBQueryField(q),r)>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=3 Then If Not (GetDataSimple(DB,DBQueryField(q),r)>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=3 Then If Not (GetDataSimple(DB,DBQueryField(q),r)>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=3 Then If Not (GetDataSimple(DB,DBQueryField(q),r)>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=3 Then If Not (GetDataSimple(DB,DBQueryField(q),r)>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=3 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)>DBQueryValFloat(q)) Then doit=0:Exit
	; =< (Equals or Less Than)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=4 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=4 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=4 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=4 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=4 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=<DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=4 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)=<DBQueryValFloat(q)) Then doit=0:Exit
	; => (Equals or More Than)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=5 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=5 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=5 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=5 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=5 Then If Not (GetDataSimple(DB,DBQueryField(q),r)=>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=5 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)=>DBQueryValFloat(q)) Then doit=0:Exit
	; <> (Not)
	If DBFieldType(DB,DBQueryField(q))=DB_Byte Then If DBQueryOp(q)=6 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SByte Then If DBQueryOp(q)=6 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Short Then If DBQueryOp(q)=6 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_SShort Then If DBQueryOp(q)=6 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Int Then If DBQueryOp(q)=6 Then If Not (GetDataSimple(DB,DBQueryField(q),r)<>DBQueryValInt(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_Float Then If DBQueryOp(q)=6 Then If Not (GetDataFloatSimple(DB,DBQueryField(q),r)<>DBQueryValFloat(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_String Then If DBQueryOp(q)=6 Then If Not (GetDataStringSimple(DB,DBQueryField(q),r)<>DBQueryValString(q)) Then doit=0:Exit
	If DBFieldType(DB,DBQueryField(q))=DB_List Then If DBQueryOp(q)=6 Then If Not (GetDataStringSimple(DB,DBQueryField(q),r)<>DBQueryValString(q)) Then doit=0:Exit
	Next
If doit Then
DBListedRecords=DBListedRecords+1
DBListedRecord(DBListedRecords)=DBRecordID(DB,r)
;Color 255,255,0
;Print GetDataStringSimple(db,1,r)
EndIf
Next
Return DBListedRecords
End Function



Function DefineDB(nam$="")
For t=1 To DBs
If DBFields(t)=0 And DBActive(t)=0 Then DaDB=t:Goto gotit
Next
If DBs>MaxDBs Then RuntimeError "Out of database space."
DBs=DBs+1:DaDB=DBs
.gotit
DBName(DaDB)=nam
DBActive(DaDB)=1
Return DaDB
End Function

Function BuildDB(DB)
If DBFields(DB)=0 Then RuntimeError "Cannot build a database with no fields."
sum=4
For t=1 To DBFields(DB)
DBFieldOffset(DB,t)=sum
If DBFieldType(DB,t)=DB_Byte Then sum=sum+1
If DBFieldType(DB,t)=DB_SByte Then sum=sum+1
If DBFieldType(DB,t)=DB_Short Then sum=sum+2
If DBFieldType(DB,t)=DB_SShort Then sum=sum+2
If DBFieldType(DB,t)=DB_Int Then sum=sum+4
If DBFieldType(DB,t)=DB_Float Then sum=sum+4
If DBFieldType(DB,t)=DB_String Then sum=sum+DBFieldSize(DB,t)
If DBFieldType(DB,t)=DB_List Then sum=sum+1
Next
DBRecordsInCache(DB)=0
DBRecordSize(DB)=sum
DBBank(DB)=CreateBank()
End Function


Function AddByteField(DB,N$)
Return AddField(DB,N$,DB_Byte)
End Function

Function AddSByteField(DB,N$)
Return AddField(DB,N$,DB_SByte)
End Function

Function AddShortField(DB,N$)
Return AddField(DB,N$,DB_Short)
End Function

Function AddSShortField(DB,N$)
Return AddField(DB,N$,DB_SShort)
End Function

Function AddIntField(DB,N$)
Return AddField(DB,N$,DB_Int)
End Function

Function AddFloatField(DB,N$)
Return AddField(DB,N$,DB_Float)
End Function

Function AddStringField(DB,N$,ln=25)
Return AddField(DB,N$,DB_String,ln)
End Function

Function AddListField(DB,N$,l$)
Return AddField(DB,N$,DB_List,0,l)
End Function


Function AddRecord(DB)
DBRecords(DB)=DBRecords(DB)+1:E=DBRecords(DB)
loc=BankSize(DBBank(DB))
ID=DBIDAt(DB)
If DBIDAt(DB)=2147483647 Then DBIDAt(DB)=-2147483648  Else DBIDAt(DB)=DBIDAt(DB)+1
ResizeBank DBBank(DB),loc+DBRecordSize(DB)
PokeInt DBBank(DB),loc,id
For f=1 To DBFields(DB)
If DBFieldType(DB,f)=DB_Byte Then WriteByteToDB db,f,e,0
If DBFieldType(DB,f)=DB_SByte Then WriteSByteToDB db,f,e,0
If DBFieldType(DB,f)=DB_Short Then WriteShortToDB db,f,e,0
If DBFieldType(DB,f)=DB_SShort Then WriteSShortToDB db,f,e,0
If DBFieldType(DB,f)=DB_Int Then WriteIntToDB db,f,e,0
If DBFieldType(DB,f)=DB_Float Then WriteFloatToDB db,f,e,0
If DBFieldType(DB,f)=DB_String Then WriteStringToDB db,f,e,""
If DBFieldType(DB,f)=DB_List Then WriteByteToDB db,f,e,0
Next
AddRecordToCache DB,DBRecords(DB)
Return DBRecordID(DB,E)
End Function


Function FreeRecord(DB,e)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record. (FindRecord)"
sz=DBRecordSize(db)
lc=DBRecordLocation(DB,rec)
	If rec<DBRecords(db)
	For s=lc To (BankSize(DBBank(DB))-sz)
	b=PeekByte(DBBank(db),s+sz)
	PokeByte DBBank(db),s,b
	Next
	EndIf
	ResizeBank DBBank(db),BankSize(DBBank(db))-sz
	DBRecords(DB)=DBRecords(DB)-1
	DBDels(DB)=DBDels(DB)+1
		.redel	
		For c=1 To DBRecordsInCache(db) ;remove any instances of the record from the cache
		If DBRecordCacheIndex(db,c)=rec Then
			For tt=1 To DBRecordsInCache(db)-1
			DBRecordCacheID(db,tt)=DBRecordCacheID(db,tt+1)
			DBRecordCacheIndex(db,tt)=DBRecordCacheIndex(db,tt+1)
			Next
		DBRecordsInCache(db)=DBRecordsInCache(db)-1
		Goto redel
		EndIf
		Next
			For c=1 To DBRecordsInCache(db) ;update record indices in cache
			If DBRecordCacheIndex(db,c)>rec Then DBRecordCacheIndex(db,c)=DBRecordCacheIndex(db,c)-1
			Next
End Function


Function SetData(DB,E,F$,val)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.1"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
SetDataSimple db,fld,rec, val
AddRecordToCache DB,rec
End Function

Function SetDataFloat(DB,E,F$,val#)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.2"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
SetDataFloatSimple db,fld,rec, val
AddRecordToCache DB,rec
End Function

Function SetDataString(DB,E,F$,val$)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.3"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
SetDataStringSimple db,fld,rec, val
AddRecordToCache DB,rec
End Function



Function GetData(DB,E,F$)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.4"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
AddRecordToCache DB,rec
Return GetDataSimple (db,fld,rec)
End Function

Function GetDataFloat#(DB,E,F$)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.5"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
AddRecordToCache DB,rec
Return GetDataFloatSimple (db,fld,rec)
End Function

Function GetDataString$(DB,E,F$)
rec=FindRecordByID(DB,e):If rec=0 Then RuntimeError "Database doesn't contain specified record.6"
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
AddRecordToCache DB,rec
Return GetDataStringSimple (db,fld,rec)
End Function






Function FindRecord(DB,f$,val$) ;this should first check the cache records!
fld=FindField(DB,F):If fld=0 Then RuntimeError "Database doesn't contain specified field (`"+f+"')."
ftyp=DBFieldType(DB,Fld)
;CHECK RECENTLY USED RECORDS
		If ftyp=DB_Byte Then
		valint=Int(val)
		For c=1 To DBRecordsInCache(DB)
		e=DBRecordCacheIndex(db,c)
		If ReadByteFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
		Next
		Return -1
		EndIf	
			If ftyp=DB_SByte Then
			valint=Int(val)
			For c=1 To DBRecordsInCache(DB)
			e=DBRecordCacheIndex(db,c)
			If ReadSByteFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
			Next
			Return -1
			EndIf	
				If ftyp=DB_Short Then
				valint=Int(val)
				For c=1 To DBRecordsInCache(DB)
				e=DBRecordCacheIndex(db,c)
				If ReadShortFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
				Next
				Return -1
				EndIf
			If ftyp=DB_SShort Then
			valint=Int(val)
			For c=1 To DBRecordsInCache(DB)
			e=DBRecordCacheIndex(db,c)
			If ReadSShortFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
			Next
			Return -1
			EndIf
		If ftyp=DB_Int Then
		valint=Int(val)
		For c=1 To DBRecordsInCache(DB)
		e=DBRecordCacheIndex(db,c)
		If ReadIntFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
		Next
		Return -1
		EndIf	
	If ftyp=DB_Float Then
	valfloat#=Float(val)
	For c=1 To DBRecordsInCache(DB)
	e=DBRecordCacheIndex(db,c)
	If ReadFloatFromDB(db,fld,e)=valfloat Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
	Next
	Return -1
	EndIf	
		If ftyp=DB_String Then
		ln=Len(val)
		For c=1 To DBRecordsInCache(DB)
		e=DBRecordCacheIndex(db,c)
		ln2=StringLength(db,fld,e):If ln=ln2 Then If ReadStringFromDB(db,fld,e)=val Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
;		If ReadStringFromDB(db,fld,e)=val Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
		Next
		Return -1
		EndIf	
			If ftyp=DB_List Then
			For c=1 To DBRecordsInCache(DB)
			e=DBRecordCacheIndex(db,c)
			If DBGetListString(db,fld,ReadByteFromDB(db,fld,e))=val Then AddRecordToCache DB,e:Return DBRecordCacheID(db,c)
			Next
			Return -1
			EndIf	

;CHECK ALL RECORDS
		If ftyp=DB_Byte Then
		valint=Int(val)
		For e=1 To DBRecords(DB)
		If ReadByteFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordID(db,e)
		Next
		Return -1
		EndIf	
			If ftyp=DB_SByte Then
			valint=Int(val)
			For e=1 To DBRecords(DB)
			If ReadSByteFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordID(db,e)
			Next
			Return -1
			EndIf	
				If ftyp=DB_Short Then
				valint=Int(val)
				For e=1 To DBRecords(DB)
				If ReadShortFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordID(db,e)
				Next
				Return -1
				EndIf	
			If ftyp=DB_SShort Then
			valint=Int(val)
			For e=1 To DBRecords(DB)
			If ReadSShortFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordID(db,e)
			Next
			Return -1
			EndIf	
		If ftyp=DB_Int Then
		valint=Int(val)
		For e=1 To DBRecords(DB)
		If ReadIntFromDB(db,fld,e)=valint Then AddRecordToCache DB,e:Return DBRecordID(db,e)
		Next
		Return -1
		EndIf	
	If ftyp=DB_Float Then
	valfloat#=Int(val)
	For e=1 To DBRecords(DB)
	If ReadFloatFromDB(db,fld,e)=valfloat Then AddRecordToCache DB,e:Return DBRecordID(db,e)
	Next
	Return -1
	EndIf	
		If ftyp=DB_String Then
		ln=Len(val)
		For e=1 To DBRecords(DB)
		ln2=StringLength(db,fld,e):If ln=ln2 Then If ReadStringFromDB(db,fld,e)=val Then AddRecordToCache DB,e:Return DBRecordID(db,e)
		Next
		Return -1
		EndIf	
			If ftyp=DB_List Then
			For e=1 To DBRecords(DB)
			If DBGetListString(db,fld,ReadByteFromDB(db,fld,e))=val Then AddRecordToCache DB,e:Return DBRecordID(db,e)
			Next
			Return -1
			EndIf	
End Function




Function FreeDB(DB)
If db<1 Or db>DBs Then RuntimeError "No such database for FreeDB."
If dbactive(db)=0 Then RuntimeError "No such database for FreeDB."
FreeBank DBBank(DB)
DBFields(DB)=0
DBRecords(DB)=0
DBIDAt(DB)=0
DBName(DB)=""
DBActive(DB)=0
DBRecordsInCache(DB)=0
End Function




Function SaveDB(DB,filename$)
fh=WriteFile(filename):If fh=0 Then Return
WriteDB fh,DB
CloseFile fh
Return 1
End Function


Function WriteDB(fil,DB)
WriteString fil,DBName$(DB)
WriteInt fil,DBIDAt(DB)
WriteInt fil,DBFields(DB)
WriteInt fil,DBRecordSize(DB)
WriteInt fil,DBRecords(DB)
	For f=1 To DBFields(DB)
	WriteByte fil,DBFieldType(DB,f)
	If dbfieldtype(db,f)=DB_String Then WriteInt fil,DBFieldSize(DB,f)
	WriteInt fil,DBFieldOffset(DB,f)
	If dbfieldtype(db,f)=DB_List Then WriteString fil,DBFieldList(DB,f)
	Next
WriteInt fil,BankSize(DBBank(DB))
WriteBytes DBBank(DB),fil,0,BankSize(DBBank(DB))
End Function




Function LoadDB(filename$)
fh=ReadFile(filename):If fh=0 Then Return
DB=ReadDB(fh)
CloseFile fh
Return DB
End Function


Function ReadDB(fil)
DB=DefineDB():DBActive(DB)=1
DBName$(DB)=ReadString(fil)
DBIDAt(DB)=ReadInt(fil)
DBFields(DB)=ReadInt(fil)
DBRecordSize(DB)=ReadInt(fil)
DBRecords(DB)=ReadInt(fil)
	For f=1 To DBFields(DB)
	DBFieldType(DB,f)=ReadByte(fil)
	If dbfieldtype(db,f)=DB_String Then DBFieldSize(DB,f)=ReadInt(fil)
	DBFieldOffset(DB,f)=ReadInt(fil)
	If dbfieldtype(db,f)=DB_List Then DBFieldList(DB,f)=ReadString(fil)
	Next
bs=ReadInt(fil):DBBank(DB)=CreateBank(bs)
ReadBytes DBBank(DB),fil,0,bs
Return DB
End Function







Function EditDBs() ;Edit all databases
If DBs=0 Then Return
buf=GraphicsBuffer():sred=ColorRed():sgreen=ColorGreen():sblue=ColorBlue()
w=GraphicsWidth():h=GraphicsHeight()
	Color 0,0,0
	For y=0 To h Step 2
	Line 0,y,w,y
	Next
;		For x=0 To w Step 3
;		Line x,0,x,h
;		Next
rempic=CreateImage(w,h):GrabImage rempic,0,0
w=GraphicsWidth():h=GraphicsHeight()
SetBuffer BackBuffer()
edbfont=LoadFont("verdana",20)
SetFont edbfont:fh=FontHeight()
dw=200
FlushKeys:FlushMouse
Repeat
DrawBlock rempic,0,0
wh=h-100:yspan=wh/(fh*1.5):xspan=(DBs-1)/yspan
ww=((xspan+1)*(dw+10))
If xspan=0 Then wh=fh*1.5*dbs
wy=(h/2)-(wh/2)
wx=(w/2)-(ww/2)
Color 30,30,50:Rect wx,wy,ww+20,wh+20,1
Color 230,230,250:Rect wx,wy,ww+20,wh+20,0
msx=MouseX():msy=MouseY():mh1=MouseHit(1)
wx=wx+10:wy=wy+10
dx=wx:dy=wy
For d=1 To dbs
Color 20,20,20
Rect dx,dy,dw,fh,1
Color 255,255,255
Text dx+(dw/2),dy,DBName(d)+" ("+DBRecords(d)+")",1
Color 48,48,48:Rect dx,dy,dw,fh,0
If msx=>dx And msy=>dy And msx<dx+dw And msy<dy+fh Then
Color 248,248,48:Rect dx,dy,dw,fh,0
If mh1 Then EditDB d:SetFont edbfont
EndIf
dy=dy+(fh*1.5):If dy>(wy+wh-10) Then dx=dx+(dw+10):dy=wy
Next
Color 255,255,255:Line Msx-1,msy,msx+1,msy:Line msx,msy-1,msx,msy+1
Flip
kh1=KeyHit(1):If kh1 And rightmenu Then rightmenu=0:kh1=0
Until kh1; Or MouseHit(2)
FlushKeys:FlushMouse
FreeFont edbfont
SetBuffer buf:Color sred,sgreen,sblue
DrawBlock rempic,0,0:FreeImage rempic
End Function


Function EditDB(DB) ;this changes the active font!  also causes problems if the current graphics buffer is an image buffer or texture buffer
If DBActive(DB)=0 Then RuntimeError "Database doesnt exist.
buf=GraphicsBuffer():sred=ColorRed():sgreen=ColorGreen():sblue=ColorBlue()
w=GraphicsWidth():h=GraphicsHeight()
rempic=CreateImage(w,h):GrabImage rempic,0,0
SetBuffer BackBuffer()
edbfont=LoadFont("verdana",17)
edbfont2=LoadFont("verdana",27)
SetFont edbfont
fh=FontHeight()
fw=w/8:If wf<100 Then wf=100
sw=20
ww=w-sw
wh=h-(sw+(fh*2))
yspan=wh/fh
xspan=ww/fw
FlushKeys:FlushMouse
Repeat
Color 30,30,50
Rect 0,0,w,h,1
msx=MouseX()
msy=MouseY()
mh1=MouseHit(1):If mh1 And rightmenu Then If msx<rmx Or msx=>rmx+rmw Or msy<rmy Or msy=>rmy+rmh Then rightmenu=0:mh1=0
mh2=MouseHit(2)
md1=MouseDown(1)
fof=DBscrollx+1:x=0
If dbscrollx>dbfields(db)-xspan Then dbscrollx=dbfields(db)-xspan
If dbscrollx<0 Then dbscrollx=0
If dbscrolly>dbrecords(db)-yspan Then dbscrolly=dbrecords(db)-yspan
If dbscrolly<0 Then dbscrolly=0
Repeat
If fof=>0 And fof<=DBFields(DB) And fof>0 Then
Color 255,255,255
Text x,0,DBField(DB,fof)
	rof=DBScrolly+1:y=fh
	Repeat
	If rof>0 And rof=<DBRecords(DB) And rof>0 Then
		dat$=GetDataStringSimple(DB,fof,rof)
		Color 0,0,0
		Rect x,y,fw,fh-2,1
		Color 30,30,50
		Rect x+fw-3,y,3,fh-2,1
			If msx<(w-fh) Then
			If (msy=>y And msy<y+fh And dragbar=0 And rightmenu=0) Or (rightmenu And recordsel=rof) Then
			recordsel=rof
			Color 55,55,125
			Rect x,y,fw-3,fh-2,1
			Color 255,155,255
			Text x,y,dat$
				If msx=>x And msx<x+fw And rightmenu=0 Then
				fieldsel=fof
				editx=x:edity=y
				Color 255,255,255
				Rect x,y,fw-3,fh-2,0
				EndIf
			Else
				Color 255,255,155
				Text x,y,dat$
			EndIf
			Else
				Color 255,255,155
				Text x,y,dat$
			EndIf
	EndIf
	rof=rof+1:y=y+fh
	Until y+fh>(wh+fh) Or rof>DBRecords(DB)
EndIf
fof=fof+1:x=x+fw
Until x>w Or fof>DBFields(DB)
If DBFields(db)>xspan Then ;H Scrollbar
Color 95,90,90
scx=0
scy=h-(fh*2)
scw=w-fh
sch=fh
Rect scx,scy,scw,sch,1
Color 255,190,100
CarretW=(xspan*scw)/DBFields(db)
CarretX=(DBscrollx*scw)/DBFields(db)
If dragbar=1 Then Color 255,255,255
Rect carretx,scy,carretw,sch,1
		If msx=>carretx And msy=>scy And msx<carretx+carretw And msy<scy+sch Then
		Color 255,255,255:Rect carretx,scy,carretw,sch,0
		If mh1 Then dragoffset=msx-(carretx+(carretw/2)):dragbar=1
		EndIf
	If dragbar=1 Then
	If msx-dragoffset<scx+(carretw/2) Then msx=scx+(carretw/2)+dragoffset
	If msx-dragoffset>scx+scw-(carretw/2) Then msx=scx+w-(carretw/2)+dragoffset
	;MoveMouse msx,scy+(fh/2)
	mmmp=(((msx-scx)-dragoffset)-(carretw/2))
	dbscrollx=(mmmp*(dbfields(DB)))/scw
	EndIf
	If md1=0 Then dragbar=0
EndIf
					mwspd=MouseZSpeed() ;mouse wheel
					If mwspd<>0 Then
					dbscrolly=dbscrolly-(mwspd*(yspan*.4))
					If dbscrolly>dbrecords(db)-yspan Then dbscrolly=dbrecords(db)-yspan
					If dbscrolly<0 Then dbscrolly=0
					EndIf
If DBRecords(db)>yspan Then ;V Scrollbar
Color 95,90,90
scx=w-fh
scy=fh
scw=fh
sch=h-(fh*3)
Rect scx,scy,scw,sch,1
Color 255,190,100
CarretH=(yspan*sch)/(DBRecords(db))
CarretY=scy+((DBscrolly*sch)/(DBRecords(db)))
If dragbar=2 Then Color 255,255,255
Rect scx,carrety,fh,carreth,1
		If msx=>scx And msy=>carrety And msx<scx+scw And msy<carrety+carreth Then
		Color 255,255,255:Rect scx,carrety,scw,carreth,0
		If mh1 Then dragoffset=msy-(carrety+(carreth/2)):dragbar=2
		EndIf
	If dragbar=2 Then
	If msy-dragoffset<scy+(carreth/2) Then msy=scy+(carreth/2)+dragoffset
	If msy-dragoffset>scy+sch+1-(carreth/2) Then msy=scy+sch+1-(carreth/2)+dragoffset
	;MoveMouse scx+(fh/2),msy
	mmmp=(((msy-scy)-dragoffset)-(carreth/2))
	dbscrolly=(mmmp*(dbrecords(DB)))/sch
	EndIf
	If md1=0 Then dragbar=0
EndIf
Color 60,60,160 ;status bar
Rect 0,h-fh,w,fh,1
Color 255,255,255
If DBName(DB)<>"" Then n$=DBName(DB) Else n$="Unnamed"
Text 1,(h-fh)+1,"Database: `"+n$+"'"
Text w*.25,(h-fh)+1,"Fields: "+DBFields(DB)
Text w*.5,(h-fh)+1,"Records: "+DBRecords(DB)
If recordsel>0 Then Text w*.78,(h-fh)+1,fieldsel+" x "+recordsel
		If mh1 And recordsel>0 And fieldsel>0 And rightmenu=0 Then ;edit record field
			dbbgpic2=CreateImage(w,h):GrabImage dbbgpic2,0,0
		
			If DBFieldType(db,fieldsel)<>DB_List Then ;typing into the box
			daval$=GetDataStringSimple(db,fieldsel,recordsel)
			Odaval$=daval
			Repeat
			DrawBlock dbbgpic2,0,0
			Color 0,0,0:Rect editx,edity,fw-3,fh,1
			Color 255,255,255:Rect editx-1,edity-1,fw+2-3,fh+1,0
			Color 60,255,60
			ms=MilliSecs()
			If ms-curstime > 100 Then curstik=curstik+1:curstime=ms:If curstik=2 Then curstik=0
			If curstik=1 Then cursor$="_" Else cursor$=""
			Text editx,edity,daval+cursor
			k=GetKey()
			If k>0 And k<>27 And k<>8 And k<>13 Then daval=daval+Chr(k)
			If k=8 Then If Len(daval)>0 Then daval=Left(daval,Len(daval)-1)
			msx2=MouseX():msy2=MouseY():Color 255,255,255:Line Msx2-1,msy2,msx2+1,msy2:Line msx2,msy2-1,msx2,msy2+1
			Flip
			Until k=27 Or k=13 Or MouseHit(1) Or MouseHit(2)
			FlushKeys:FlushMouse
			If k<>27 Then SetDataStringSimple(db,fieldsel,recordsel, daval)
			EndIf
			
				If DBFieldType(db,fieldsel)=DB_List Then ;list box (multiple choice)
				omsx=MouseX():omsy=MouseY()
							opts=0:minl=100
							Repeat
							kkk$=DBGetListString(db,fieldsel,opts)
							If minl<StringWidth(kkk)+20 Then minl=StringWidth(kkk)+20
							opts=opts+1
							Until kkk=""
							opts=opts-1
				Repeat
				mh1=MouseHit(1)
				DrawBlock dbbgpic2,0,0
				rmw=minl:rmh=20+(opts*(fh+5))-5
				rmx=msx-(rmw/2):rmy=msy-(rmh/2)
				If rmx<0 Then rmx=0
				If rmy<0 Then rmy=0
				If rmx>(w-rmw) Then rmx=(w-rmw)
				If rmy>(h-rmh) Then rmy=(h-rmh)
				Color 90,90,120:Rect rmx,rmy,rmw,rmh,1
				Color 255,255,255:Rect rmx,rmy,rmw,rmh,0
					optsel=-1
					For o=1 To opts
					Color 20,20,20
					optx=rmx+10:opty=rmy+10+((o-1)*(fh+5))
					optw=rmw-20
					Rect optx,opty,optw,fh,1
					If ReadByteFromDB(db,fieldsel,recordsel)=o-1 Then Color 0,255,0:Rect optx,opty,optw,fh,0
					Color 255,255,255
					opop$=DBGetListString(db,fieldsel,o-1)
					If MouseX()=>optx And MouseY()=>opty And MouseX()<optx+optw And MouseY()<opty+fh Then
					Color 255,255,55
					Rect optx,opty,optw,fh,0:Color 255,255,255
					If mh1 Then optsel=o-1
					EndIf
					Text optx+(optw/2),opty,opop,1
					Next
				msx2=MouseX():msy2=MouseY():Color 255,255,255:Line Msx2-1,msy2,msx2+1,msy2:Line msx2,msy2-1,msx2,msy2+1
				Flip
				Until KeyHit(1) Or mh1 Or MouseHit(2)
				If mh1 And optsel>-1 Then SetDataStringSimple db,fieldsel,recordsel,dbgetliststring(db,fieldsel,optsel):MoveMouse omsx,omsy
				FlushMouse:FlushKeys
				EndIf
		
		DrawBlock dbbgpic2,0,0:FreeImage dbbgpic2
		EndIf
rmw=150:rmh=116
If mh2 Then rightmenu=rightmenu+1:rmx=msx-(rmw/2):rmy=msy-(rmh/2):If rightmenu=2 Then rightmenu=0
If rightmenu Then
If rmx<0 Then rmx=0
If rmy<0 Then rmy=0
If rmx>(w-rmw) Then rmx=(w-rmw)
If rmy>(h-rmh) Then rmy=(h-rmh)
Color 150,150,150:Rect rmx,rmy,rmw,rmh,1
Color 255,255,255:Rect rmx,rmy,rmw,rmh,0
	optx=rmx+10:opty=rmy+10:optw=rmw-20:opth=fh
;	Color 0,0,0:Text optx,opty,dbrecordid(db,recordsel):opty=opty+(fh*1.5)
	opt$="New Record"
	Color 80,80,80:Rect optx,opty,optw,opth,1:Color 255,255,255:Text optx+(optw/2),opty,opt,1
	If msx=>optx And msy=>opty And msx<optx+optw And msy<opty+opth Then Color 255,255,0:Rect optx,opty,optw,opth,0:If mh1 Then AddRecord DB:DBScrollY=dbrecords(db)-yspan:recordsel=dbrecords(db)
		If recordsel>0 Then
		opty=opty+(fh*1.5):opt$="Clone Record"
		Color 80,80,80:Rect optx,opty,optw,opth,1:Color 255,255,255:Text optx+(optw/2),opty,opt,1
		If msx=>optx And msy=>opty And msx<optx+optw And msy<opty+opth Then Color 255,255,0:Rect optx,opty,optw,opth,0:If mh1 Then
			AddRecord DB
			CopyRecordSimple DB,RecordSel,DBRecords(DB)
			DBScrollY=dbrecords(db)-yspan
			recordsel=dbrecords(db)
		EndIf
		EndIf
		If recordsel>0 Then
		opty=opty+(fh*1.5):opt$="Delete Record"
		Color 80,80,80:Rect optx,opty,optw,opth,1:Color 255,255,255:Text optx+(optw/2),opty,opt,1
		If msx=>optx And msy=>opty And msx<optx+optw And msy<opty+opth Then Color 255,255,0:Rect optx,opty,optw,opth,0:If mh1 Then FreeRecord DB,DBRecordID(DB,recordsel);:rightmenu=0:mh1=0
		EndIf
	opty=opty+(fh*1.5):opt$="Save Database"
	Color 80,80,80:Rect optx,opty,optw,opth,1:Color 255,255,255:Text optx+(optw/2),opty,opt,1
	If msx=>optx And msy=>opty And msx<optx+optw And msy<opty+opth Then Color 255,255,0:Rect optx,opty,optw,opth,0:If mh1 Then
	result=SaveDB(DB,"Database.db")
	If result Then repo$="Successfully exported database to Database.db" Else repo$="Failed to write file!"
	Color 50,50,120
	Rect 0,0,w,h,1
	Color 255,255,255
	SetFont edbfont2
	Text w/2,h/2,repo,1,1
	SetFont edbfont
	Flip
	Repeat
	Until KeyHit(1) Or KeyHit(57) Or KeyHit(26) Or MouseHit(1) Or MouseHit(2)
	FlushKeys
	FlushMouse
	EndIf
EndIf
If recordsel<1 Then recordsel=1
If recordsel>DBRecords(DB) Then recordsel=DBRecords(DB)
If dbrecords(db)=0 Then recordsel=0:fieldsel=0
If rightmenu=0 Then recordsel=0:fieldsel=0

msx2=MouseX():msy2=MouseY():Color 255,255,255:Line Msx2-1,msy2,msx2+1,msy2:Line msx2,msy2-1,msx2,msy2+1
Flip
kh1=KeyHit(1):If kh1 And rightmenu Then rightmenu=0:kh1=0
Until kh1
FlushKeys
FreeFont edbfont
SetBuffer buf:Color sred,sgreen,sblue
DrawBlock rempic,0,0:FreeImage rempic
End Function








;*********** PRIVATE FUNCTIONS


Function FindField(DB,f$)
l=Len(f)
For t=1 To DBFields(DB)
If DBFieldLen(DB,t)=l Then If f=DBField(DB,t) Then Return t
Next
End Function

Function FindRecordByID(DB,lab)
		For c=DBRecordsInCache(DB) To 1 Step -1 ;first check the recently used records for a match (the whole point of the cache)
		If DBRecordCacheID(DB,c)=lab Then Return DBRecordCacheIndex(DB,c)
		Next
	lab2=lab-(DBDels(DB)+1):If lab2<1 Then lab2=1 ;failing that, take a educated guess at where to search from
	If lab2<=DBRecords(DB) Then
	For t=lab2 To DBRecords(DB)
	If DBRecordID(DB,t)=lab Then Return t
	Next
	EndIf
For t=1 To lab2; failing that, check every record that hasn't been checked yet
If DBRecordID(DB,t)=lab Then Return t
Next
End Function

Function GetDataSimple(DB,F,E)
If DBFieldType(DB,F)=DB_Byte Then Return ReadByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SByte Then Return ReadSByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Short Then Return ReadShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SShort Then Return ReadSShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Int Then Return ReadIntFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Float Then Return ReadFloatFromDB(db,f,e)
If DBFieldType(DB,F)=DB_List Then Return ReadByteFromDB(db,f,e)
End Function

Function GetDataFloatSimple#(DB,F,E)
If DBFieldType(DB,F)=DB_Byte Then Return ReadByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SByte Then Return ReadSByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Short Then Return ReadShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SShort Then Return ReadSShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Int Then Return ReadIntFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Float Then Return ReadFloatFromDB(db,f,e)
If DBFieldType(DB,F)=DB_List Then Return ReadByteFromDB(db,f,e)
End Function

Function GetDataStringSimple$(DB,F,E)
If DBFieldType(DB,F)=DB_Byte Then Return ReadByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SByte Then Return ReadSByteFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Short Then Return ReadShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_SShort Then Return ReadSShortFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Int Then Return ReadIntFromDB(db,f,e)
If DBFieldType(DB,F)=DB_Float Then Return ReadFloatFromDB(db,f,e)
If DBFieldType(DB,F)=DB_String Then Return ReadStringFromDB(db,f,e)
If DBFieldType(DB,F)=DB_List Then Return DBGetListSTring(db,f,ReadByteFromDB(db,f,e))
End Function


Function SetDataSimple(DB,F,E, Val)
If DBFieldType(DB,F)=DB_Byte Then WriteByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SByte Then WriteSByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Short Then WriteShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SShort Then WriteSShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Int Then WriteIntToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Float Then WriteStringToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_List Then WriteByteToDB(db,f,e, val):Return
End Function

Function SetDataFloatSimple(DB,F,E, Val#)
If DBFieldType(DB,F)=DB_Byte Then WriteByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SByte Then WriteSByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Short Then WriteShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SShort Then WriteSShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Int Then WriteIntToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Float Then WriteFloatToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_List Then WriteByteToDB(db,f,e, val):Return
End Function

Function SetDataStringSimple(DB,F,E, Val$)
If DBFieldType(DB,F)=DB_Byte Then WriteByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SByte Then WriteSByteToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Short Then WriteShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_SShort Then WriteSShortToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Int Then WriteIntToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_Float Then WriteFloatToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_String Then WriteStringToDB(db,f,e, val):Return
If DBFieldType(DB,F)=DB_List Then WriteByteToDB(db,f,e, DBGetListValue(db,f,val)):Return
End Function





Function ReadByteFromDB(db,f,e)
Return PeekByte(DBBank(DB),DBDataLocation(db,f,e))
End Function

Function ReadSByteFromDB(db,f,e)
Return PeekByte(DBBank(DB),DBDataLocation(db,f,e))-128
End Function

Function ReadShortFromDB(db,f,e)
v1=PeekByte(DBBank(db),DBDataLocation(db,f,e))
v2=PeekByte(DBBank(db),DBDataLocation(db,f,e)+1)
Return ((v1*256)+v2)
End Function

Function ReadSShortFromDB(db,f,e)
v1=PeekByte(DBBank(db),DBDataLocation(db,f,e))
v2=PeekByte(DBBank(db),DBDataLocation(db,f,e)+1)
Return ((v1*256)+v2)-32768
End Function

Function ReadIntFromDB(db,f,e)
Return PeekInt(DBBank(db),DBDataLocation(db,f,e))
End Function

Function ReadFloatFromDB#(db,f,e)
Return PeekFloat(DBBank(db),DBDataLocation(db,f,e))
End Function

Function ReadStringFromDB$(db,f,e)
ln1=PeekByte(DBBank(DB),DBDataLocation(db,f,e) )
ln2=PeekByte(DBBank(DB),DBDataLocation(db,f,e)+1 )
ln=(ln1*256)+ln2
If ln>DBFieldSize(DB,f) Then ln=DBFieldSize(DB,f)
If ln=0 Then Return ""
For s=1 To ln
result$=result$+Chr( PeekByte(DBBank(db),DBDataLocation(db,f,e)+s+1) )
Next
Return result
End Function


Function WriteByteToDB(db,f,e, val)
If val<0 Then val=0
If val>255 Then val=255
PokeByte DBBank(DB),DBDataLocation(db,f,e),val
End Function

Function WriteSByteToDB(db,f,e, val)
If val<-128 Then val=-128
If val>127 Then val=127
val=val+128
PokeByte DBBank(DB),DBDataLocation(db,f,e),val
End Function

Function WriteShortToDB(db,f,e, val)
If val>65535 Then val=65535
If val<0 Then val=0
val1=val/256
val2=val Mod 256
PokeByte DBBank(DB),DBDataLocation(db,f,e),val1
PokeByte DBBank(DB),DBDataLocation(db,f,e)+1,val2
End Function

Function WriteSShortToDB(db,f,e, val)
If val>32767 Then val=32767
If val<-32768 Then val=-32768
val=val+32768
If val<0 Then val=0
val1=val/256
val2=val Mod 256
PokeByte DBBank(DB),DBDataLocation(db,f,e),val1
PokeByte DBBank(DB),DBDataLocation(db,f,e)+1,val2
End Function

Function WriteIntToDB(db,f,e, val)
PokeInt DBBank(DB),DBDataLocation(db,f,e),val
End Function

Function WriteFloatToDB(db,f,e, val#)
PokeFloat DBBank(DB),DBDataLocation(db,f,e),val
End Function

Function WriteStringToDB(db,f,e, val$)
ln=Len(val):If ln>DBFieldSize(db,f) Then ln=DBFieldSize(db,f)
ln1=ln/256
ln2=ln Mod 256
PokeByte DBBank(DB),DBDataLocation(db,f,e),ln1
PokeByte DBBank(DB),DBDataLocation(db,f,e)+1,ln2
For s=1 To ln
PokeByte DBBank(DB),DBDataLocation(db,f,e)+s+1, Asc(Mid(val,s,1))
Next
End Function

Function StringLength(db,f,e)
If DBFieldType(db,f)<>DB_String Then Return
b1=PeekByte(DBBank(db),DBDataLocation(db,f,e))
b2=PeekByte(DBBank(db),DBDataLocation(db,f,e)+1)
Return (b1*256)+b2
End Function

Function CopyRecordSimple(DB,r1,r2)
For f=1 To DBFields(DB)
val$=GetDataStringSimple(DB,f,r1)
SetDataStringSimple DB,f,r2,val$
Next
End Function

Function AddField(DB,N$,Typ=DB_Int,StrLen=25,lst$="")
If DBBank(DB) Then RuntimeError "Cannot add fields to a finalized database."
If DBFields(DB)=MaxFields Then RuntimeError "Database has reached field limit. (While adding `"+n+"')"
DBFields(DB)=DBFields(DB)+1:F=DBFields(DB)
DBField(DB,F)=n
DBFieldType(DB,F)=Typ
DBFieldLen(DB,F)=Len(n)
If Typ=DB_List Then DBFieldList(DB,F)=lst$ Else DBFieldList(DB,F)=""
If Typ=DB_String Then DBFieldSize(DB,F)=StrLen+2 Else DBFieldSize(DB,F)=0 ;first 2 bytes of a string is length
End Function



Function DBGetListString$(db,f,val)
ss$=DBFieldList(db,f)
For l=1 To Len(ss)
cc$=Mid(ss,l,1)
If cc="," Then
valat=valat+1:If valat=val+1 Then Return oot$ Else oot$=""
Else
oot=oot$+cc
EndIf
Next
If valat=val Then Return oot
End Function

Function DBGetListValue(db,f,s$)
ss$=DBFieldList(db,f)
For l=1 To Len(ss)
cc$=Mid(ss,l,1)
If cc="," Then
valat=valat+1:If s=oot$ Then Return valat-1 Else oot$=""
Else
oot=oot$+cc
EndIf
Next
If s=oot Then Return valat
End Function







Function DBRecordID(DB,r)
Return PeekInt(DBBank(db),DBRecordLocation(db,r))
End Function


Function DBRecordLocation(DB,r)
Return (DBRecordSize(DB)*(r-1))
End Function

Function DBDataLocation(db,f,e)
Return (DBFieldOffset(DB,F)+DBRecordLocation(DB,e))-0
End Function





Function AddRecordToCache(DB,r)
If DBRecordsInCache(DB)>0 Then
If DBRecordCacheIndex(DB,DBRecordsInCache(DB))=r Then Return ;if its already at the top of the cache, no need to proceed
		For c=1 To DBRecordsInCache(DB) ;see if its already in the cache, and if so, move it to the top of the pile
		If DBRecordCacheIndex(DB,c)=r Then
		daid=DBRecordCacheID(DB,c)
			For t=c To DBRecordsInCache(DB)-1
			DBRecordCacheID(db,t)=DBRecordCacheID(db,t+1)
			DBRecordCacheIndex(db,t)=DBRecordCacheIndex(db,t+1)
			Next
		DBRecordCacheID(db,DBRecordsInCache(DB))=daid
		DBRecordCacheIndex(db,DBRecordsInCache(DB))=r
		Return
		EndIf
		Next
EndIf
	If DBRecordsInCache(DB)=MaxRecordCache Then
		For t=1 To MaxRecordCache-1;stack is full so shift them down
		DBRecordCacheID(db,t)=DBRecordCacheID(db,t+1)
		DBRecordCacheIndex(db,t)=DBRecordCacheIndex(db,t+1)
		Next
		Else
		DBRecordsInCache(DB)=DBRecordsInCache(DB)+1;stack isn't full so just add to it
	EndIf
c=DBRecordsInCache(DB)
DBRecordCacheID(db,c)=DBRecordID(db,r)
DBRecordCacheIndex(db,c)=r
End Function
