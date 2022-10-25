; ID: 417
; Author: Inner
; Date: 2002-09-04 18:54:44
; Title: Arrays of types
; Description: Using dim to make a set of types

Type TEST
    Field x
    Field y
    Field name$
    Field f#
End Type 

Global mine.TEST Dim all_mine.TEST( 100 ) 

all_mine(0)=New TEST
all_mine(0)\x=0
all_mine(0)\y=1000
all_mine(0)\name$="FOO"
all_mine(0)\f#=1.68

Print all_mine(0)\x
Print all_mine(0)\y
Print all_mine(0)\name$
Print all_mine(0)\f#

Delete all_mine(0)

WaitKey
End
