; ID: 2311
; Author: Jim Teeuwen
; Date: 2008-09-09 14:29:31
; Title: (De)serialization llibrary
; Description: Small (de)serialization library for runtime saving/loading of complex objects to/from a file/stream

'// IFormatter.bmx
Rem
	The Interface that all Formatters implement.
	Makes for a somewhat more OOP-compliant approach.
End Rem
Type IFormatter Abstract

	Method Serialize(fs:TStream, obj:Object) Abstract
	Method Deserialize:Object(fs:TStream) Abstract

End Type



'// BinaryFormatter.bmx
Type BinaryFormatter Extends IFormatter

	Const TYPETKN_UNDEFINED:Byte = 0;
	Const TYPETKN_BYTE:Byte = 1;
	Const TYPETKN_SHORT:Byte = 2;
	Const TYPETKN_INT:Byte = 3;
	Const TYPETKN_LONG:Byte = 4;
	Const TYPETKN_FLOAT:Byte = 5;
	Const TYPETKN_DOUBLE:Byte = 6;
	Const TYPETKN_OBJECT:Byte = 7;
	Const TYPETKN_STRING:Byte = 8;
	Const TYPETKN_ARRAY:Byte = 9;

	Method Deserialize:Object(fs:TStream)
		Return Self.RecursiveDeserialize(fs);
	End Method
	Method Serialize(fs:TStream, obj:Object)
		Self.RecursiveSerialize(fs, obj);
	End Method
	Method RecursiveDeserialize:Object(fs:TStream)
		Local strlen:Int = fs.ReadInt();
		Local name:String = fs.ReadString(strlen); 
		Local typeid:TTypeId = TTypeId.ForName(name);
		Local obj:Object = typeid.NewObject();
		Local token:Byte = GetTypeToken(typeid);

		Select (token)
			Case TYPETKN_UNDEFINED;
				For Local fld:TField = EachIn typeid.EnumFields()
					token = GetTypeToken(fld.TypeId());
					strlen = fs.ReadInt();
					name = fs.ReadString(strlen);

					Select (token)
						Case TYPETKN_BYTE;
							fld.SetInt(obj, fs.ReadByte());
				
						Case TYPETKN_SHORT;
							fld.SetInt(obj, fs.ReadShort());
				
						Case TYPETKN_INT;
							fld.SetInt(obj, fs.ReadInt());
				
						Case TYPETKN_LONG;
							fld.SetLong(obj, fs.ReadLong());
				
						Case TYPETKN_FLOAT;
							fld.SetFloat(obj, fs.ReadFloat());
				
						Case TYPETKN_DOUBLE;
							fld.SetDouble(obj, fs.ReadDouble());
				
						Case TYPETKN_STRING;
							strlen = fs.ReadInt();
							fld.SetString(obj, fs.ReadString(strlen));

						Case TYPETKN_ARRAY;
							ReadArray(fs, fld.Get(obj));

						Default  '// Undefined, object
							fld.Set(obj, RecursiveDeserialize(fs));
				
					End Select
				Next
		End Select

		Return obj;
	End Method
	Method RecursiveSerialize(fs:TStream, obj:Object)
		Local typeid:TTypeId = TTypeId.ForObject(obj);
		Local token:Byte = GetTypeToken(typeid);

		fs.WriteInt( typeid.Name().Length );
		fs.WriteString( typeid.Name() );
		
		Select (token)
			Case TYPETKN_UNDEFINED;
				For Local fld:TField = EachIn typeid.EnumFields()
					token = GetTypeToken(fld.TypeId());
					fs.WriteInt(fld.Name().Length);
					fs.WriteString(fld.Name());
						
					Select (token)
						Case TYPETKN_BYTE;
							fs.WriteByte( Byte(fld.GetInt(obj)) );
				
						Case TYPETKN_SHORT;
							fs.WriteShort( Short(fld.GetInt(obj)) );
				
						Case TYPETKN_INT;
							fs.WriteInt( fld.GetInt(obj) );
				
						Case TYPETKN_LONG;
							fs.WriteLong( fld.GetLong(obj) );
				
						Case TYPETKN_FLOAT;
							fs.WriteFloat( fld.GetFloat(obj) );
				
						Case TYPETKN_DOUBLE;
							fs.WriteDouble( fld.GetDouble(obj) );
				
						Case TYPETKN_STRING;
							fs.WriteInt( fld.GetString(obj).Length );
							fs.WriteString( fld.GetString(obj) );
				
						Case TYPETKN_ARRAY;
							WriteArray(fs, fld.Get(obj));

						Default  '// Undefined, object
							RecursiveSerialize(fs, fld.Get(obj));
				
					End Select
				Next
			End Select
		
	End Method
	Method WriteArray(fs:TStream, arr:Object)
		Local typeid:TTypeId = TTypeId.ForObject(arr);
		Local alen:Int = typeid.ArrayLength(arr) ;
		fs.WriteInt(alen);
		For Local n:Int = 0 To alen -1
			Local o:Object = typeid.GetArrayElement(arr, n);
			Local token:Byte = GetTypeToken(typeid.ElementType());
			fs.WriteByte(token);
			If( token = TYPETKN_UNDEFINED ) Then
				RecursiveSerialize(fs, o);
			Else If( token = TYPETKN_ARRAY ) Then
				WriteArray(fs, o);
			Else
				fs.WriteInt(o.ToString().Length);
				fs.WriteString(o.ToString());
			End If
		Next
	End Method
	Method ReadArray(fs:TStream, arr:Object)
		Local typeid:TTypeId = TTypeId.ForObject(arr);
		Local alen:Int = fs.ReadInt();
		For Local n:Int = 0 To alen -1
			Local token:Byte = fs.ReadByte();
			Local o:Object = typeid.GetArrayElement(arr, n);
			If( token = TYPETKN_UNDEFINED ) Then
				typeid.SetArrayElement(arr, n, RecursiveDeserialize(fs));
			Else If( token = TYPETKN_ARRAY ) Then
				ReadArray(fs, o);
			Else
				Local strlen:Int = fs.ReadInt();
				Local val:String = fs.ReadString(strlen);
				typeid.SetArrayElement(arr, n, val);
			End If
		Next
	End Method
	Method GetTypeToken:Byte(tid:TTypeId)
		Select (tid)
			Case ByteTypeId; Return TYPETKN_BYTE;
			Case ShortTypeId; Return TYPETKN_SHORT;
			Case IntTypeId; Return TYPETKN_INT;
			Case LongTypeId; Return TYPETKN_LONG;
			Case FloatTypeId; Return TYPETKN_FLOAT;
			Case DoubleTypeId; Return TYPETKN_DOUBLE;
			Case ObjectTypeId; Return TYPETKN_OBJECT;
			Case StringTypeId; Return TYPETKN_STRING;
			Case ArrayTypeId; Return TYPETKN_ARRAY;
			Default;
				If(tid.ExtendsType( ArrayTypeId )) Then
					Return TYPETKN_ARRAY;
				End If
				Return TYPETKN_UNDEFINED;
		End Select
	End Method

End Type




'// AsciiFormatter.bmx
Type AsciiFormatter Extends IFormatter

	Const TYPETKN_UNDEFINED:Byte = 0;
	Const TYPETKN_BYTE:Byte = 1;
	Const TYPETKN_SHORT:Byte = 2;
	Const TYPETKN_INT:Byte = 3;
	Const TYPETKN_LONG:Byte = 4;
	Const TYPETKN_FLOAT:Byte = 5;
	Const TYPETKN_DOUBLE:Byte = 6;
	Const TYPETKN_OBJECT:Byte = 7;
	Const TYPETKN_STRING:Byte = 8;
	Const TYPETKN_ARRAY:Byte = 9;

	Field Indent:Int;

	Method Deserialize:Object(fs:TStream)
		Return Self.RecursiveDeserialize( fs );
	End Method
	Method Serialize(fs:TStream, obj:Object)
		Indent = 0;
		Self.RecursiveSerialize(fs, obj);
	End Method
	Method RecursiveDeserialize:Object( fs:TStream )
		Local typeid:TTypeId = TTypeId.ForName( fs.ReadLine().Trim() );
		Local obj:Object = typeid.NewObject();
		Local token:Byte = GetTypeToken(typeid);

		Select (token)
			Case TYPETKN_UNDEFINED;
				For Local fld:TField = EachIn typeid.EnumFields()
					token = GetTypeToken( fld.TypeId() );

					Select (token)
						Case TYPETKN_ARRAY;
							ReadArray( fs, fld.Get(obj) );

						Case TYPETKN_UNDEFINED, TYPETKN_OBJECT;
							fld.Set( obj, RecursiveDeserialize( fs ) );

						Default;
							fld.Set( obj, GetVal(fs)[1] );
				
						End Select
				Next
		End Select
		Return obj;
	End Method
	Method RecursiveSerialize(fs:TStream, obj:Object)
		Local typeid:TTypeId = TTypeId.ForObject(obj);
		Local token:Byte = GetTypeToken(typeid);

		Write( fs, typeid.Name() );
		Indent :+ 1;
	
		Select (token)
			Case TYPETKN_UNDEFINED;
				For Local fld:TField = EachIn typeid.EnumFields()
					token = GetTypeToken(fld.TypeId());
	
					Select (token)
						Case TYPETKN_ARRAY;
							WriteArray(fs, fld.Get(obj), fld.Name());

						Case TYPETKN_UNDEFINED,  TYPETKN_OBJECT;
							RecursiveSerialize(fs, fld.Get(obj));
				
						Default;
							Write(fs, fld.Name() + "=" + fld.GetString(obj));

					End Select
				Next
		End Select

		Indent :- 1;

	End Method
	Method WriteArray( fs:TStream, arr:Object, name:String)
		Local typeid:TTypeId = TTypeId.ForObject(arr);
		Local alen:Int = typeid.ArrayLength(arr) ;
		Write(fs, name + " Size=" + alen);
		Indent :+ 1;
		For Local n:Int = 0 To alen -1
			Local o:Object = typeid.GetArrayElement(arr, n);
			Local token:Byte = GetTypeToken(typeid.ElementType());
			If( token = TYPETKN_UNDEFINED Or  token = TYPETKN_OBJECT) Then
				RecursiveSerialize(fs, o);
			Else If( token = TYPETKN_ARRAY ) Then
				WriteArray(fs, o, name);
			Else
				Write(fs, typeid.ElementType().Name() + "=" + o.ToString());
			End If
		Next
		Indent :- 1;
	End Method
	Method ReadArray( fs:TStream, arr:Object)
		Local typeid:TTypeId = TTypeId.ForObject(arr);
		Local alen:Int = Int(GetVal(fs)[1]);
		Local token:Byte = GetTypeToken( typeid.ElementType() );
		For Local n:Int = 0 To alen -1
			Local o:Object = typeid.GetArrayElement(arr, n);
			If( token = TYPETKN_UNDEFINED ) Then
				typeid.SetArrayElement(arr, n, RecursiveDeserialize( fs ));
			Else If( token = TYPETKN_ARRAY ) Then
				ReadArray(fs, o);
			Else
				typeid.SetArrayElement(arr, n, GetVal(fs)[1]);
			End If
		Next
	End Method
	Method GetTypeToken:Byte(tid:TTypeId)
		Select (tid)
			Case ByteTypeId; Return TYPETKN_BYTE;
			Case ShortTypeId; Return TYPETKN_SHORT;
			Case IntTypeId; Return TYPETKN_INT;
			Case LongTypeId; Return TYPETKN_LONG;
			Case FloatTypeId; Return TYPETKN_FLOAT;
			Case DoubleTypeId; Return TYPETKN_DOUBLE;
			Case ObjectTypeId; Return TYPETKN_OBJECT;
			Case StringTypeId; Return TYPETKN_STRING;
			Case ArrayTypeId; Return TYPETKN_ARRAY;
			Default;
				If(tid.ExtendsType( ArrayTypeId )) Then
					Return TYPETKN_ARRAY;
				End If
				Return TYPETKN_UNDEFINED;
		End Select
	End Method
	Method GetVal:String[](fs:TStream)
		Local line:String = fs.ReadLine();
		Local ret:String[] = [line[..line.Find("=")], line[line.Find("=") + 1..]];
		
		For Local n:Int = 0 To ret.Length -1
			ret[n] = ret[n].Trim();
		Next

		Return ret;
	End Method
	Method Write(fs:TStream, val:String)
		Local padding:String ="";
		For Local n:Int = 0 To Indent - 1
			padding :+ "  ";
		Next
		fs.WriteLine(padding + val);
	End Method

End Type



'// DEMO.bmx
SuperStrict
Framework brl.basic
Import brl.reflection

Include "IFormatter.bmx"
Include "BinaryFormatter.bmx"
Include "AsciiFormatter.bmx"

'// Some basic Game objects
Type Player
	Field Score:Int;
	Field Name:String;
	Field Location:Float[];

	Method New()
		Score = 0;
		Name = "Unnamed Player";
		Location = New Float[2];
	End Method

	Method ToString:String()
		Return (Name + " (" + Score + ")");
	End Method
End Type

Type Game
	Field Players:Player[];
	Field RandSeed:Int;
	Field Level:Int;

	Method New()
		Level = 1;
		RandSeed = MilliSecs();
		SeedRnd(RandSeed);

		'// All array objects MUST be initialized for the binaryformatter to work properly!
		'// The reason for this is that the routine that puts the values back in these lists
		'// expects the objects to be valid instances.
		Players = New Player[2];
		Players[0] = New Player;
		Players[1] = New Player;
	End Method

	Method ToString:String()
		Return ("Level: " + Level + ", " + Players[0].ToString() + ", " + Players[1].ToString() );
	End Method

End Type


'// Define our objects : (Un)comment here to use a different formatter.
Local bf:IFormatter = New BinaryFormatter;
'Local bf:IFormatter = New AsciiFormatter;

Local file:TStream = Null;
Local savefile:String = "game.sav";
Local g:Game = New Game;

'// Setup some basic game parameters with initial values to simulate a game in progress.
g.Level = 10;
g.Players[0].Name ="Bob";
g.Players[0].Score = Rand(0, 1000);
g.Players[0].Location[0] = Rand(0, 500);
g.Players[0].Location[1] = Rand(0, 500);
g.Players[1].Name ="Mike";
g.Players[1].Score = Rand(0, 1000);
g.Players[1].Location[0] = Rand(0, 500);
g.Players[1].Location[1] = Rand(0, 500);


'// Show the Initial output
Print("--- PRESAVE TEST ----------------------------------------------");
Print(g.ToString());
Print("x: " + g.Players[1].Location[0] + " y:" + g.Players[1].Location[1] );


'// Save the entire game to a file in it's current state.
Try
	file = WriteFile(savefile);
	bf.Serialize( file, g );
Catch e:Object
	Print(e.ToString());
End Try

If(file <> Null) Then 
	file.Close();
End If


'// clear game so we can load it up fresh from a file.
g = Null;
 
Try
	file = ReadFile(savefile);
	g = Game(bf.Deserialize( file ));
Catch e:Object
	Print(e.ToString());
End Try

If(file <> Null) Then 
	file.Close();
End If

'// bog out when something went wrong.
If( g = Null ) Then End;

'// Show the new output
'// If all went well, this should be identical to the presave output.
Print("--- POSTSAVE TEST ----------------------------------------------");
Print(g.ToString());
Print("x: " + g.Players[1].Location[0] + " y:" + g.Players[1].Location[1] );

End;
