; ID: 1699
; Author: Nilium
; Date: 2006-05-05 19:58:05
; Title: StreamBank
; Description: A bank interface to a stream

SuperStrict

Import Brl.Bank
Import Brl.Stream

Public
Type IStreamBank Extends TBank
    Field _proxy:TStream
    
    Method Delete( )
        _proxy = Null
    End Method
    
    Method Resize( size% )
        Throw "Cannot resize IStreamBanks"
    End Method
    
    Method PeekByte%( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadByte( )
    End Method
    
    Method PeekShort%( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadShort( )
    End Method
    
    Method PeekInt%( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadInt( )
    End Method
    
    Method PeekLong:Long( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadLong( )
    End Method
    
    Method PeekFloat#( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadFloat( )
    End Method
    
    Method PeekDouble!( idx% )
        _proxy.Seek( idx )
        Return _proxy.ReadDouble( )
    End Method
    
    Method PokeByte( idx%, value% )
        _proxy.Seek( idx )
        _proxy.WriteByte( value )
    End Method
    
    Method PokeShort( idx%, value% )
        _proxy.Seek( idx )
        _proxy.WriteShort( value )
    End Method
    
    Method PokeInt( idx%, value% )
        _proxy.Seek( idx )
        _proxy.WriteInt( value )
    End Method
    
    Method PokeLong( idx%, value:Long )
        _proxy.Seek( idx )
        _proxy.WriteLong( value )
    End Method
    
    Method PokeFloat( idx%, value# )
        _proxy.Seek( idx )
        _proxy.WriteFloat( value )
    End Method
    
    Method PokeDouble( idx%, value! )
        _proxy.Seek( idx )
        _proxy.WriteDouble( value )
    End Method
    
    Method Size%( )
        Return _proxy.Size( )
    End Method
    
    Method PeekBytes@ Ptr( into@ Ptr, off%, size% )
        _proxy.Seek( off )
        _proxy.ReadBytes( into, size )
    End Method
    
    Method PokeBytes( from@ Ptr, off%, size% )
        _proxy.Seek( off )
        _proxy.WriteBytes( from, size )
    End Method
    
    Method Buf@ Ptr( )
        Return Null
    End Method
End Type

Function CreateStreamBank:TBank( stream:TStream )
    Local o:IStreamBank = New IStreamBank
    o._proxy = stream
    Return o
End Function
