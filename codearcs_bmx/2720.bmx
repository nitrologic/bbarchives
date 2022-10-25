; ID: 2720
; Author: beanage
; Date: 2010-05-21 06:25:29
; Title: Linear Binary Search Algorithm
; Description: Performs a binary search up to an arbitrary precision based on an initial interval and an evaluator callback.

'------------------------------
'Linear binary search algorithm
'------------------------------

'#####################
'    C 2008-2009 by
'    B.e.A.n.A.g.e.
'       L.a.b.s.
'#####################

'//////////
SuperStrict
'//////////

'------------------------------------------------------------------------------------
Rem
bbdoc: Binary Linear Search Algorithm
about: 
EndRem
Module beanage.BSearch

ModuleInfo "Version: 1.0.00"
ModuleInfo "License: GNU GPL"
ModuleInfo "Copyright: BeAnAge Labs 2010"
ModuleInfo "Author: Joseph Birkner"
ModuleInfo "Modserver: beanage"

'#Region "History"
ModuleInfo "History: 20/05/10 < 1.0.00 < Initial Release"
'#End Region 
'------------------------------------------------------------------------------------

Import brl.standardio

Type BSearch
	
	Field _min:Double
	Field _max:Double
	Field _result:Double
	Field _result_tolerance:Double
	Field _evaluator:Byte( a_:Double, b_:Double ) 'Callback shall return <True> if <this> is better than <than>

	Field info_interval:Double[]
	Field info_num_cycles:Int = 0
	Field info_best_probe_index:Int = 0
	
	Rem
	A <probe> callback shall return an initial guess on the first execution, or a potentially better guess based on <sugg> if otherwise. 
	The <sugg> parameter will be "NaN" on the first execution of a <probe> callback.
	The callback will be executed until its new guess is "worse" than the the corresponding suggestion.
	The last results of all probe callbacks will be compared to find the best probe result.
	End Rem
	Field _probes:Double[]( sugg_:Double[] )[] 'returns [new_guess, suggestion]
	
	Function _doubleToNAN( ptr_:Byte Ptr )
?LittleEndian
		ptr_[0] = $00
		ptr_[1] = $00
		ptr_[6] = $F8
		ptr_[7] = $7F
?BigEndian
		ptr_[0] = $7F
		ptr_[1] = $F8
		ptr_[6] = $00
		ptr_[7] = $00
?
		Int Ptr( VarPtr ptr_[2] )[0] = Null
	End Function
	
	Function Create:BSearch( min_result_:Double, max_result_:Double, evaluator_:Byte(a_:Double,b_:Double), result_tolerance_:Double=.01, probes_:Double[](sugg_:Double[])[]= Null )
		Local ret_:BSearch = New BSearch
		ret_._min = min_result_
		ret_._max = max_result_
		ret_._evaluator = evaluator_
		ret_._result_tolerance = result_tolerance_
		ret_._probes = probes_
		
		_doubleToNAN VarPtr ret_._result
		Return ret_
	End Function
	
	Method ThisIsBetterThan:Byte( this_:Double, than_:Double )
		info_num_cycles:+ 1
		Return _evaluator( this_, than_ )
	End Method
	
	Method Search()
		info_num_cycles = Null
		Local interval_:Double[]
		
		'-----------------------------------------------------------------
		'Find an initial search interval using the given probing callbacks
		'-----------------------------------------------------------------
		If _probes
			Local results_:Double[][_probes.Length ]
			'perform provided initial test probes
			For Local i_:Int = 0 Until _probes.Length
				Local probe_:Double[2]
				
				'get initial probe guess
				_doubleToNAN VarPtr probe_[1]
				probe_ = _probes[ i_ ]( probe_ )
				Local prev_worse_guess_:Double = probe_[0]
				
				Repeat
					probe_[1] = probe_[0] 'make the new gues the suggestion
					probe_ = _probes[ i_ ]( probe_ ) 'get the new guess based on the suggestion
					If probe_[0]>= _max Or probe_[0]<= _min Or Not ThisIsBetterThan( probe_[0], probe_[1] )
						Exit
					Else
						prev_worse_guess_ = probe_[1] 'save the old suggestion
					End If
				Forever
				If prev_worse_guess_<> probe_[1] And ThisIsBetterThan( prev_worse_guess_, probe_[0] ) 'maybe the old suggestion is actually better than the new one? (tho both aint optimal)
					probe_[0] = prev_worse_guess_
				End If
				results_[ i_ ] = probe_
			Next
			'find best of probe results
			Local j_:Int = 0
			
			For Local result_:Double[] = EachIn results_
				If interval_
					If ( result_[0]>= _min ) And ( result_[0]<= _max )
						If ThisIsBetterThan( result_[0], interval_[0] )
							interval_ = result_
							info_best_probe_index = j_
							
						End If
					
					End If
				Else
					interval_ = result_
					info_best_probe_index = j_
				
				End If
				j_:+ 1
			Next
		Else
			interval_ = [ _min, _max ]
		
		End If
		info_interval = [ interval_[0], interval_[1] ]
		'-----------------------------------------------------------------
		'binary search the solution within the estimated solution interval
		'-----------------------------------------------------------------
		Local dstep_:Double = Abs( interval_[0]- interval_[1] )
		
		While dstep_> _result_tolerance
			'interval[1] = Worse of Interval; Interval[0] = Better of Interval
			If ThisIsBetterThan( interval_[1], interval_[0] )
				Local better_:Double = interval_[1]
				
				interval_[1] = interval_[0]
				interval_[0] = better_
			
			End If
			'binary division
			interval_[1] = ( interval_[0]+ interval_[1] )/ 2
			dstep_ = Abs( interval_[0]- interval_[1] )
		Wend
		_result = interval_[1]
	End Method
	
	Method GetResult:Double()
		Return _result
	End Method

End Type
