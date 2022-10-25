; ID: 1951
; Author: Matt Merkulov
; Date: 2007-03-14 10:59:44
; Title: Database functions
; Description: DBF reading/modifying/writing functions

;Database (DBF) reading/modifying/writing functions by Matt Merkulov

Const maxdbf=100, dbq=2
Dim dbfile(dbq),fleng(dbq),stpos(dbq),rowq(dbq),colq(dbq),dbbank(dbq),dbbankc(dbq)
Dim dbc(dbq,maxdbf,1)

Function opendb(db,file$)
f=OpenFile(file$)
dbfile(db)=f
SeekFile f,8
stpos(db)=ReadShort(f)
fleng(db)=ReadShort(f)
rowq(db)=(FileSize(file$)-stpos(db))/fleng(db)

dbbank(db)=CreateBank(fleng(db))
dbbankc(db)=CreateBank(fleng(db))
For n=0 To fleng(db)-1
 PokeByte dbbankc(db),n,32
Next

dbc(db,0,0)=1
cq=0
Repeat
 fp=32*(cq+1)
 SeekFile f,fp
 If ReadByte(f)=13 Then Exit
 SeekFile f,fp+16
 dbc(db,cq,1)=ReadShort(f)
 If colq(db)>0 Then dbc(db,cq,0)=dbc(db,cq-1,0)+dbc(db,cq-1,1)
 cq=cq+1
Forever
colq(db)=cq
End Function

Function closedb(db)
f=dbfile(db)
SeekFile f,4
WriteShort f,rowq(db)
CloseFile f
FreeBank dbbank(db)
FreeBank dbbankc(db)
End Function

Function readrow(db,row)
SeekFile dbfile(db),stpos(db)+fleng(db)*row
ReadBytes dbbank(db),dbfile(db),0,fleng(db)
End Function

Function writerow(db,row)
SeekFile dbfile(db),stpos(db)+fleng(db)*row
WriteBytes dbbank(db),dbfile(db),0,fleng(db)
End Function

Function readdbf$(db,col)
For n=0 To dbc(db,col,1)-1
 m$=m$+Chr$(PeekByte(dbbank(db),dbc(db,col,0)+n))
Next
Return m$
End Function

Function writedbf(db,col,m$)
l=Len(m$)
For n=0 To dbc(db,col,1)-1
 If n<l Then v=Asc(Mid$(m$,n+1,1)) Else v=32
 PokeByte dbbank(db),n+dbc(db,col,0),v
Next
End Function

Function trimdbf$(db,col)
Return Trim$(readdbf$(db,col))
End Function

Function createdb(db,file$)
dbfile(db)=OpenFile(file$)
WriteInt dbfile(db),3+1 Shl 8+1 Shl 16
For n=1 To 7
 WriteInt dbfile(db),0
Next
stpos(db)=32
fleng(db)=1
rowq(db)=0
End Function

Function addfield(db,name$,l)
f=dbfile(db)
For n=1 To 11
 WriteByte f,Asc(Mid$(name$,n,1))
Next
WriteByte f,66
WriteInt f,fleng(db)
fld=(stpos(db) Shr 5)-1
dbc(db,fld,0)=fleng(db)
WriteShort f,l
dbc(db,fld,1)=l
For n=1 To 7
 WriteShort f,0
Next
fleng(db)=fleng(db)+l
stpos(db)=stpos(db)+32
End Function

Function closeheader()
f=dbfile(db)
WriteByte f,13
SeekFile f,8
stpos(db)=stpos(db)+1
WriteShort f,stpos(db)
WriteShort f,fleng(db)
End Function

Function addrow(db)
SeekFile dbfile(db),stpos(db)+fleng(db)*rowq(db)
WriteBytes dbbank(db),dbfile(db),0,fleng(db)
rowq(db)=rowq(db)+1
End Function

Function clearbnk(db)
CopyBank dbbankc(db),0,dbbank(db),0,fleng(db)
End Function
