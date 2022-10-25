; ID: 2308
; Author: JoshK
; Date: 2008-09-03 20:15:32
; Title: Pub.EFX
; Description: EFX module for enabling hardware reverb and echo effects

efx.bmx:
[code]Strict

Module pub.efx

Include "efx-creative.bmx"
Include "efx-util.bmx"

'/**
' * OpenAL cross platform effects extension audio library
' * Copyright (C) 2005-2006 by authors.
' * This library is free software; you can redistribute it And/Or
' *  modify it under the terms of the GNU Library General Public
' *  License as published by the Free Software Foundation; either
' *  version 2 of the License, Or (at your option) any later version.
' *
' * This library is distributed in the hope that it will be useful,
' *  but WITHOUT ANY WARRANTY; without even the implied warranty of
' *  MERCHANTABILITY Or FITNESS For A PARTICULAR PURPOSE.  See the GNU
' *  Library General Public License For more details.
' *
' * Go to http://www.gnu.org/copyleft/lgpl.html
' */

Const ALC_EXT_EFX_NAME$                      =            "ALC_EXT_EFX"

'/**
' * Context definitions To be used with alcCreateContext.
' * These values must be unique And Not conflict with other 
' * al context values.
' */
Const ALC_EFX_MAJOR_VERSION                  =            $20001
Const ALC_EFX_MINOR_VERSION                  =            $20002
Const ALC_MAX_AUXILIARY_SENDS                =            $20003

'/**
' * Listener definitions To be used with alListener functions.
' * These values must be unique And Not conflict with other 
' * al listener values.
' */
Const AL_METERS_PER_UNIT                     =            $20004

'/**
' * Source definitions To be used with alSource functions.
' * These values must be unique And Not conflict with other 
' * al source values.
' */
Const AL_DIRECT_FILTER                       =            $20005
Const AL_AUXILIARY_SEND_FILTER               =            $20006
Const AL_AIR_ABSORPTION_FACTOR               =            $20007
Const AL_ROOM_ROLLOFF_FACTOR                 =            $20008
Const AL_CONE_OUTER_GAINHF                   =            $20009
Const AL_DIRECT_FILTER_GAINHF_AUTO           =            $2000A
Const AL_AUXILIARY_SEND_FILTER_GAIN_AUTO     =            $2000B
Const AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO   =            $2000C

'/**
' * Effect Object definitions To be used with alEffect functions.
' *
' * Effect parameter value definitions, ranges, And defaults
' * appear farther down in this file.
' */

'/* Reverb Parameters */
Const AL_REVERB_DENSITY                      =            $0001
Const AL_REVERB_DIFFUSION                    =            $0002
Const AL_REVERB_GAIN                         =            $0003
Const AL_REVERB_GAINHF                       =            $0004
Const AL_REVERB_DECAY_TIME                   =            $0005
Const AL_REVERB_DECAY_HFRATIO                =            $0006
Const AL_REVERB_REFLECTIONS_GAIN             =            $0007
Const AL_REVERB_REFLECTIONS_DELAY            =            $0008
Const AL_REVERB_LATE_REVERB_GAIN             =            $0009
Const AL_REVERB_LATE_REVERB_DELAY            =            $000A
Const AL_REVERB_AIR_ABSORPTION_GAINHF        =            $000B 
Const AL_REVERB_ROOM_ROLLOFF_FACTOR          =            $000C
Const AL_REVERB_DECAY_HFLIMIT                =            $000D

'/* Chorus Parameters */
Const AL_CHORUS_WAVEFORM                     =            $0001
Const AL_CHORUS_PHASE                      	 =            $0002
Const AL_CHORUS_RATE                         =            $0003
Const AL_CHORUS_DEPTH                        =            $0004
Const AL_CHORUS_FEEDBACK                     =            $0005
Const AL_CHORUS_DELAY                        =            $0006

'/* Distortion Parameters */
Const AL_DISTORTION_EDGE                     =            $0001
Const AL_DISTORTION_GAIN                     =            $0002
Const AL_DISTORTION_LOWPASS_CUTOFF           =            $0003
Const AL_DISTORTION_EQCENTER                 =            $0004
Const AL_DISTORTION_EQBANDWIDTH              =            $0005

'/* Echo Parameters */
Const AL_ECHO_DELAY                          =            $0001
Const AL_ECHO_LRDELAY                        =            $0002
Const AL_ECHO_DAMPING                        =            $0003
Const AL_ECHO_FEEDBACK                       =            $0004
Const AL_ECHO_SPREAD                         =            $0005

'/* Flanger Parameters */
Const AL_FLANGER_WAVEFORM                    =            $0001
Const AL_FLANGER_PHASE                       =            $0002
Const AL_FLANGER_RATE                        =            $0003
Const AL_FLANGER_DEPTH                       =            $0004
Const AL_FLANGER_FEEDBACK                    =            $0005
Const AL_FLANGER_DELAY                       =            $0006

'/* Frequencyshifter Parameters */
Const AL_FREQUENCY_SHIFTER_FREQUENCY         =            $0001
Const AL_FREQUENCY_SHIFTER_LEFT_DIRECTION    =            $0002
Const AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION   =            $0003

'/* Vocalmorpher Parameters */
Const AL_VOCAL_MORPHER_PHONEMEA                 =         $0001
Const AL_VOCAL_MORPHER_PHONEMEA_COARSE_TUNING   =         $0002
Const AL_VOCAL_MORPHER_PHONEMEB                 =         $0003
Const AL_VOCAL_MORPHER_PHONEMEB_COARSE_TUNING   =         $0004
Const AL_VOCAL_MORPHER_WAVEFORM                 =         $0005
Const AL_VOCAL_MORPHER_RATE                     =         $0006

'/* Pitchshifter Parameters */
Const AL_PITCH_SHIFTER_COARSE_TUNE              =         $0001
Const AL_PITCH_SHIFTER_FINE_TUNE                =         $0002

'/* Ringmodulator Parameters */
Const AL_RING_MODULATOR_FREQUENCY               =         $0001
Const AL_RING_MODULATOR_HIGHPASS_CUTOFF         =         $0002
Const AL_RING_MODULATOR_WAVEFORM                =         $0003

'/* Autowah Parameters */
Const AL_AUTOWAH_ATTACK_TIME                    =         $0001
Const AL_AUTOWAH_RELEASE_TIME                   =         $0002
Const AL_AUTOWAH_RESONANCE                      =         $0003
Const AL_AUTOWAH_PEAK_GAIN                      =         $0004

'/* Compressor Parameters */
Const AL_COMPRESSOR_ONOFF                       =         $0001

'/* Equalizer Parameters */
Const AL_EQUALIZER_LOW_GAIN                     =         $0001
Const AL_EQUALIZER_LOW_CUTOFF                   =         $0002
Const AL_EQUALIZER_MID1_GAIN                    =         $0003
Const AL_EQUALIZER_MID1_CENTER                  =         $0004
Const AL_EQUALIZER_MID1_WIDTH                   =         $0005
Const AL_EQUALIZER_MID2_GAIN                    =         $0006
Const AL_EQUALIZER_MID2_CENTER                  =         $0007
Const AL_EQUALIZER_MID2_WIDTH                   =         $0008
Const AL_EQUALIZER_HIGH_GAIN                    =         $0009
Const AL_EQUALIZER_HIGH_CUTOFF                  =         $000A

'/* Effect Type */
Const AL_EFFECT_FIRST_PARAMETER                 =         $0000
Const AL_EFFECT_LAST_PARAMETER                  =         $8000
Const AL_EFFECT_TYPE                            =         $8001

'/* Effect Type definitions To be used with AL_EFFECT_TYPE. */
Const AL_EFFECT_NULL                            =         $0000  '/* Can also be used as an Effect Object ID */
Const AL_EFFECT_REVERB                          =         $0001
Const AL_EFFECT_CHORUS                          =         $0002
Const AL_EFFECT_DISTORTION                      =         $0003
Const AL_EFFECT_ECHO                            =         $0004
Const AL_EFFECT_FLANGER                         =         $0005
Const AL_EFFECT_FREQUENCY_SHIFTER               =         $0006
Const AL_EFFECT_VOCAL_MORPHER                   =         $0007
Const AL_EFFECT_PITCH_SHIFTER                   =         $0008
Const AL_EFFECT_RING_MODULATOR                  =         $0009
Const AL_EFFECT_AUTOWAH                         =         $000A
Const AL_EFFECT_COMPRESSOR                      =         $000B
Const AL_EFFECT_EQUALIZER                       =         $000C

'/**
' * Auxiliary Slot Object definitions To be used with alAuxiliaryEffectSlot functions.
' */
Const AL_EFFECTSLOT_EFFECT                      =         $0001
Const AL_EFFECTSLOT_GAIN                        =         $0002
Const AL_EFFECTSLOT_AUXILIARY_SEND_AUTO         =         $0003

'/**
' * Value To be used as an Auxiliary Slot ID To disable a source send..
' */
Const AL_EFFECTSLOT_NULL                        =         $0000

'/**
' * Filter Object definitions To be used with alFilter functions.
' */

'/* Lowpass parameters. */
Const AL_LOWPASS_GAIN                           =         $0001
Const AL_LOWPASS_GAINHF                         =         $0002

'/* Highpass Parameters */
Const AL_HIGHPASS_GAIN                          =         $0001
Const AL_HIGHPASS_GAINLF                        =         $0002

'/* Bandpass Parameters */
Const AL_BANDPASS_GAIN                          =         $0001
Const AL_BANDPASS_GAINLF                        =         $0002
Const AL_BANDPASS_GAINHF                        =         $0003

'/* Filter Type */
Const AL_FILTER_FIRST_PARAMETER                 =         $0000
Const AL_FILTER_LAST_PARAMETER                  =         $8000
Const AL_FILTER_TYPE                            =         $8001

'/* Filter Type definitions To be used with AL_FILTER_TYPE. */
Const AL_FILTER_NULL                            =         $0000  '/* Can also be used as a Filter Object ID */
Const	AL_FILTER_LOWPASS                       =         $0001
Const AL_FILTER_HIGHPASS                        =         $0002
Const AL_FILTER_BANDPASS                        =         $0003

'/**********************************************************
' * Filter ranges And defaults.
' */

'/**
' * Lowpass filter
' */

Const LOWPASS_MIN_GAIN#                        =           0.0
Const LOWPASS_MAX_GAIN#                        =           1.0
Const LOWPASS_DEFAULT_GAIN#                    =           1.0

Const LOWPASS_MIN_GAINHF#                      =           0.0
Const LOWPASS_MAX_GAINHF#                      =           1.0
Const LOWPASS_DEFAULT_GAINHF#                  =           1.0

'/**
' * Highpass filter
' */

Const HIGHPASS_MIN_GAIN#                       =           0.0
Const HIGHPASS_MAX_GAIN#                       =           1.0
Const HIGHPASS_DEFAULT_GAIN#                   =           1.0

Const HIGHPASS_MIN_GAINLF#                     =           0.0
Const HIGHPASS_MAX_GAINLF#                     =           1.0
Const HIGHPASS_DEFAULT_GAINLF#                 =           1.0

'/**
' * Bandpass filter
' */

Const BANDPASS_MIN_GAIN#                       =           0.0
Const BANDPASS_MAX_GAIN#                       =           1.0
Const BANDPASS_DEFAULT_GAIN#                   =           1.0

Const BANDPASS_MIN_GAINHF#                     =           0.0
Const BANDPASS_MAX_GAINHF#                     =           1.0
Const BANDPASS_DEFAULT_GAINHF#                 =           1.0

Const BANDPASS_MIN_GAINLF#                     =           0.0
Const BANDPASS_MAX_GAINLF#                     =           1.0
Const BANDPASS_DEFAULT_GAINLF#                 =           1.0

' /**********************************************************
' * Effect parameter structures, value definitions, ranges And defaults.
' */
'
'/**
' * AL reverb effect parameter ranges And defaults
' */
Const AL_REVERB_MIN_DENSITY#                         =     0.0
Const AL_REVERB_MAX_DENSITY#                         =     1.0
Const AL_REVERB_DEFAULT_DENSITY#                     =     1.0

Const AL_REVERB_MIN_DIFFUSION#                       =     0.0
Const AL_REVERB_MAX_DIFFUSION#                       =     1.0
Const AL_REVERB_DEFAULT_DIFFUSION#                   =     1.0

Const AL_REVERB_MIN_GAIN#                            =     0.0
Const AL_REVERB_MAX_GAIN#                            =     1.0
Const AL_REVERB_DEFAULT_GAIN#                        =     0.32

Const AL_REVERB_MIN_GAINHF#                          =     0.0
Const AL_REVERB_MAX_GAINHF#                          =     1.0
Const AL_REVERB_DEFAULT_GAINHF#                      =     0.89

Const AL_REVERB_MIN_DECAY_TIME#                      =     0.1
Const AL_REVERB_MAX_DECAY_TIME#                      =     20.0
Const AL_REVERB_DEFAULT_DECAY_TIME#                  =     1.49

Const AL_REVERB_MIN_DECAY_HFRATIO#                   =     0.1
Const AL_REVERB_MAX_DECAY_HFRATIO#                   =     2.0
Const AL_REVERB_DEFAULT_DECAY_HFRATIO#               =     0.83

Const AL_REVERB_MIN_REFLECTIONS_GAIN#                =     0.0
Const AL_REVERB_MAX_REFLECTIONS_GAIN#                =     3.16
Const AL_REVERB_DEFAULT_REFLECTIONS_GAIN#            =     0.05

Const AL_REVERB_MIN_REFLECTIONS_DELAY#               =     0.0
Const AL_REVERB_MAX_REFLECTIONS_DELAY#               =     0.3
Const AL_REVERB_DEFAULT_REFLECTIONS_DELAY#           =     0.007

Const AL_REVERB_MIN_LATE_REVERB_GAIN#                =     0.0
Const AL_REVERB_MAX_LATE_REVERB_GAIN#                =     10.0
Const AL_REVERB_DEFAULT_LATE_REVERB_GAIN#            =     1.26

Const AL_REVERB_MIN_LATE_REVERB_DELAY#               =     0.0
Const AL_REVERB_MAX_LATE_REVERB_DELAY#               =     0.1
Const AL_REVERB_DEFAULT_LATE_REVERB_DELAY#           =     0.011

Const AL_REVERB_MIN_AIR_ABSORPTION_GAINHF#           =     0.892
Const AL_REVERB_MAX_AIR_ABSORPTION_GAINHF#           =     1.0
Const AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF#       =     0.994

Const AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR#             =     0.0
Const AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR#             =     10.0
Const AL_REVERB_DEFAULT_ROOM_ROLLOFF_FACTOR#         =     0.0

Const AL_REVERB_MIN_DECAY_HFLIMIT#                   =     0'AL_FALSE
Const AL_REVERB_MAX_DECAY_HFLIMIT#                   =     1'AL_TRUE
Const AL_REVERB_DEFAULT_DECAY_HFLIMIT#               =     1'AL_TRUE

'/**
' * AL chorus effect parameter ranges And defaults
' */
Const AL_CHORUS_MIN_WAVEFORM                         =    0
Const AL_CHORUS_MAX_WAVEFORM                         =    1
Const AL_CHORUS_DEFAULT_WAVEFORM                     =    1

Const AL_CHORUS_WAVEFORM_SINUSOID                    =    0
Const AL_CHORUS_WAVEFORM_TRIANGLE                    =    1

Const AL_CHORUS_MIN_PHASE                            =    (-180)
Const AL_CHORUS_MAX_PHASE                            =    180
Const AL_CHORUS_DEFAULT_PHASE                        =    90

Const AL_CHORUS_MIN_RATE#                             =    0.0
Const AL_CHORUS_MAX_RATE#                             =    10.0
Const AL_CHORUS_DEFAULT_RATE#                         =    1.1

Const AL_CHORUS_MIN_DEPTH#                            =    0.0
Const AL_CHORUS_MAX_DEPTH#                            =    1.0
Const AL_CHORUS_DEFAULT_DEPTH#                        =    0.1

Const AL_CHORUS_MIN_FEEDBACK#                         =    -1.0
Const AL_CHORUS_MAX_FEEDBACK#                         =    1.0
Const AL_CHORUS_DEFAULT_FEEDBACK#                     =    0.25

Const AL_CHORUS_MIN_DELAY#                            =    0.0
Const AL_CHORUS_MAX_DELAY#                            =    0.016
Const AL_CHORUS_DEFAULT_DELAY#                        =    0.016

'/**
' * AL distortion effect parameter ranges And defaults
' */
Const AL_DISTORTION_MIN_EDGE#                         =    0.0
Const AL_DISTORTION_MAX_EDGE#                         =    1.0
Const AL_DISTORTION_DEFAULT_EDGE#                     =    0.2

Const AL_DISTORTION_MIN_GAIN#                         =    0.01
Const AL_DISTORTION_MAX_GAIN#                         =    1.0
Const AL_DISTORTION_DEFAULT_GAIN#                     =    0.05

Const AL_DISTORTION_MIN_LOWPASS_CUTOFF#               =    80.0
Const AL_DISTORTION_MAX_LOWPASS_CUTOFF#               =    24000.0
Const AL_DISTORTION_DEFAULT_LOWPASS_CUTOFF#           =    8000.0

Const AL_DISTORTION_MIN_EQCENTER#                     =    80.0
Const AL_DISTORTION_MAX_EQCENTER#                     =    24000.0
Const AL_DISTORTION_DEFAULT_EQCENTER#                 =    3600.0

Const AL_DISTORTION_MIN_EQBANDWIDTH#                  =    80.0
Const AL_DISTORTION_MAX_EQBANDWIDTH#                  =    24000.0
Const AL_DISTORTION_DEFAULT_EQBANDWIDTH#              =    3600.0

'/**
' * AL echo effect parameter ranges And defaults
' */
Const AL_ECHO_MIN_DELAY#                              =    0.0
Const AL_ECHO_MAX_DELAY#                              =    0.207
Const AL_ECHO_DEFAULT_DELAY#                          =    0.1

Const AL_ECHO_MIN_LRDELAY#                            =    0.0
Const AL_ECHO_MAX_LRDELAY#                            =    0.404
Const AL_ECHO_DEFAULT_LRDELAY#                        =    0.1

Const AL_ECHO_MIN_DAMPING#                            =    0.0
Const AL_ECHO_MAX_DAMPING#                            =    0.99
Const AL_ECHO_DEFAULT_DAMPING#                        =    0.5

Const AL_ECHO_MIN_FEEDBACK#                           =    0.0
Const AL_ECHO_MAX_FEEDBACK#                           =    1.0
Const AL_ECHO_DEFAULT_FEEDBACK#                       =    0.5

Const AL_ECHO_MIN_SPREAD#                             =    (-1.0)
Const AL_ECHO_MAX_SPREAD#                             =    1.0
Const AL_ECHO_DEFAULT_SPREAD#                         =    (-1.0)

'/**
' * AL flanger effect parameter ranges And defaults
' */
Const AL_FLANGER_MIN_WAVEFORM                        =    0
Const AL_FLANGER_MAX_WAVEFORM                        =    1
Const AL_FLANGER_DEFAULT_WAVEFORM                    =    1

Const AL_FLANGER_WAVEFORM_SINUSOID                   =    0
Const AL_FLANGER_WAVEFORM_TRIANGLE                   =    1

Const AL_FLANGER_MIN_PHASE                           =    (-180)
Const AL_FLANGER_MAX_PHASE                           =    180
Const AL_FLANGER_DEFAULT_PHASE                       =    90

Const AL_FLANGER_MIN_RATE#                           =     0.0
Const AL_FLANGER_MAX_RATE#                           =     10.0
Const AL_FLANGER_DEFAULT_RATE#                       =    0.27

Const AL_FLANGER_MIN_DEPTH#                          =     0.0
Const AL_FLANGER_MAX_DEPTH#                          =     1.0
Const AL_FLANGER_DEFAULT_DEPTH#                      =     1.0

Const AL_FLANGER_MIN_FEEDBACK#                       =     (-1.0)
Const AL_FLANGER_MAX_FEEDBACK#                       =     1.0
Const AL_FLANGER_DEFAULT_FEEDBACK#                   =     (-0.5)

Const AL_FLANGER_MIN_DELAY#                          =     0.0
Const AL_FLANGER_MAX_DELAY#                          =     0.004
Const AL_FLANGER_DEFAULT_DELAY#                      =     0.002

'/**
' * AL frequency shifter effect parameter ranges And defaults
' */
Const AL_FREQUENCY_SHIFTER_MIN_FREQUENCY#             =    0.0
Const AL_FREQUENCY_SHIFTER_MAX_FREQUENCY#             =    24000.0
Const AL_FREQUENCY_SHIFTER_DEFAULT_FREQUENCY#         =    0.0

Const AL_FREQUENCY_SHIFTER_MIN_LEFT_DIRECTION         =   0
Const AL_FREQUENCY_SHIFTER_MAX_LEFT_DIRECTION         =   2
Const AL_FREQUENCY_SHIFTER_DEFAULT_LEFT_DIRECTION     =   0

Const AL_FREQUENCY_SHIFTER_MIN_RIGHT_DIRECTION        =   0
Const AL_FREQUENCY_SHIFTER_MAX_RIGHT_DIRECTION        =   2
Const AL_FREQUENCY_SHIFTER_DEFAULT_RIGHT_DIRECTION    =   0

Const AL_FREQUENCY_SHIFTER_DIRECTION_DOWN             =   0
Const AL_FREQUENCY_SHIFTER_DIRECTION_UP               =   1
Const AL_FREQUENCY_SHIFTER_DIRECTION_OFF              =   2

'/**
' * AL vocal morpher effect parameter ranges And defaults
' */
Const AL_VOCAL_MORPHER_MIN_PHONEMEA                   =   0
Const AL_VOCAL_MORPHER_MAX_PHONEMEA                   =   29
Const AL_VOCAL_MORPHER_DEFAULT_PHONEMEA               =   0

Const AL_VOCAL_MORPHER_MIN_PHONEMEA_COARSE_TUNING	  =   -24
Const AL_VOCAL_MORPHER_MAX_PHONEMEA_COARSE_TUNING	  =   24
Const AL_VOCAL_MORPHER_DEFAULT_PHONEMEA_COARSE_TUNING =   0

Const AL_VOCAL_MORPHER_MIN_PHONEMEB                   =   0
Const AL_VOCAL_MORPHER_MAX_PHONEMEB                   =   29
Const AL_VOCAL_MORPHER_DEFAULT_PHONEMEB               =   10

Const AL_VOCAL_MORPHER_PHONEME_A                   =      0
Const AL_VOCAL_MORPHER_PHONEME_E                   =      1
Const AL_VOCAL_MORPHER_PHONEME_I                   =      2
Const AL_VOCAL_MORPHER_PHONEME_O                   =      3
Const AL_VOCAL_MORPHER_PHONEME_U                   =      4
Const AL_VOCAL_MORPHER_PHONEME_AA                  =      5
Const AL_VOCAL_MORPHER_PHONEME_AE                  =      6
Const AL_VOCAL_MORPHER_PHONEME_AH                  =      7
Const AL_VOCAL_MORPHER_PHONEME_AO                  =      8
Const AL_VOCAL_MORPHER_PHONEME_EH                  =      9
Const AL_VOCAL_MORPHER_PHONEME_ER                  =      10
Const AL_VOCAL_MORPHER_PHONEME_IH                  =      11
Const AL_VOCAL_MORPHER_PHONEME_IY                  =      12
Const AL_VOCAL_MORPHER_PHONEME_UH                  =      13
Const AL_VOCAL_MORPHER_PHONEME_UW                  =      14
Const AL_VOCAL_MORPHER_PHONEME_B                   =      15
Const AL_VOCAL_MORPHER_PHONEME_D                   =      16
Const AL_VOCAL_MORPHER_PHONEME_F                   =      17
Const AL_VOCAL_MORPHER_PHONEME_G                   =      18
Const AL_VOCAL_MORPHER_PHONEME_J                   =      19
Const AL_VOCAL_MORPHER_PHONEME_K                   =      20
Const AL_VOCAL_MORPHER_PHONEME_L                   =      21
Const AL_VOCAL_MORPHER_PHONEME_M                   =      22
Const AL_VOCAL_MORPHER_PHONEME_N                   =      23
Const AL_VOCAL_MORPHER_PHONEME_P                   =      24
Const AL_VOCAL_MORPHER_PHONEME_R                   =      25
Const AL_VOCAL_MORPHER_PHONEME_S                   =      26
Const AL_VOCAL_MORPHER_PHONEME_T                   =      27
Const AL_VOCAL_MORPHER_PHONEME_V                   =      28
Const AL_VOCAL_MORPHER_PHONEME_Z                   =      29

Const AL_VOCAL_MORPHER_MIN_PHONEMEB_COARSE_TUNING      =  (-24)
Const AL_VOCAL_MORPHER_MAX_PHONEMEB_COARSE_TUNING      =  24
Const AL_VOCAL_MORPHER_DEFAULT_PHONEMEB_COARSE_TUNING  =  0

Const AL_VOCAL_MORPHER_MIN_WAVEFORM                    =  0
Const AL_VOCAL_MORPHER_MAX_WAVEFORM                    =  2
Const AL_VOCAL_MORPHER_DEFAULT_WAVEFORM                =  0

Const AL_VOCAL_MORPHER_WAVEFORM_SINUSOID               =  0
Const AL_VOCAL_MORPHER_WAVEFORM_TRIANGLE               =  1
Const AL_VOCAL_MORPHER_WAVEFORM_SAWTOOTH               =  2

Const AL_VOCAL_MORPHER_MIN_RATE#                        =  0.0
Const AL_VOCAL_MORPHER_MAX_RATE#                        =  10.0
Const AL_VOCAL_MORPHER_DEFAULT_RATE#                    =  1.41

'/**
' * AL pitch shifter effect parameter ranges And defaults
' */
Const AL_PITCH_SHIFTER_MIN_COARSE_TUNE           =        (-12)
Const AL_PITCH_SHIFTER_MAX_COARSE_TUNE           =        12
Const AL_PITCH_SHIFTER_DEFAULT_COARSE_TUNE       =        12

Const AL_PITCH_SHIFTER_MIN_FINE_TUNE             =        (-50)
Const AL_PITCH_SHIFTER_MAX_FINE_TUNE             =        50
Const AL_PITCH_SHIFTER_DEFAULT_FINE_TUNE         =        0

'/**
' * AL ring modulator effect parameter ranges And defaults
' */
Const AL_RING_MODULATOR_MIN_FREQUENCY#               =     0.0
Const AL_RING_MODULATOR_MAX_FREQUENCY#               =     8000.0
Const AL_RING_MODULATOR_DEFAULT_FREQUENCY#           =     440.0

Const AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF#         =     0.0
Const AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF#         =     24000.0
Const AL_RING_MODULATOR_DEFAULT_HIGHPASS_CUTOFF#     =     800.0

Const AL_RING_MODULATOR_MIN_WAVEFORM                 =    0
Const AL_RING_MODULATOR_MAX_WAVEFORM                 =    2
Const AL_RING_MODULATOR_DEFAULT_WAVEFORM             =    0

Const AL_RING_MODULATOR_SINUSOID                     =    0
Const AL_RING_MODULATOR_SAWTOOTH                     =    1
Const AL_RING_MODULATOR_SQUARE                       =    2

'/**
' * AL autowah effect parameter ranges And defaults
' */
Const AL_AUTOWAH_MIN_ATTACK_TIME#                    =     0.0001
Const AL_AUTOWAH_MAX_ATTACK_TIME#                    =     1.0
Const AL_AUTOWAH_DEFAULT_ATTACK_TIME#                =     0.06

Const AL_AUTOWAH_MIN_RELEASE_TIME#                   =     0.0001
Const AL_AUTOWAH_MAX_RELEASE_TIME#                   =     1.0
Const AL_AUTOWAH_DEFAULT_RELEASE_TIME#               =     0.06

Const AL_AUTOWAH_MIN_RESONANCE#                      =     2.0
Const AL_AUTOWAH_MAX_RESONANCE#                      =     1000.0
Const AL_AUTOWAH_DEFAULT_RESONANCE#                  =     1000.0
	
Const AL_AUTOWAH_MIN_PEAK_GAIN#                      =     0.00003
Const AL_AUTOWAH_MAX_PEAK_GAIN#                      =     31621.0
Const AL_AUTOWAH_DEFAULT_PEAK_GAIN#                  =     11.22

'/**
' * AL compressor effect parameter ranges And defaults
' */
Const AL_COMPRESSOR_MIN_ONOFF                        =    0
Const AL_COMPRESSOR_MAX_ONOFF                        =    1
Const AL_COMPRESSOR_DEFAULT_ONOFF                    =    1

'/**
' * AL equalizer effect parameter ranges And defaults
' */
Const AL_EQUALIZER_MIN_LOW_GAIN#                     =     0.126
Const AL_EQUALIZER_MAX_LOW_GAIN#                     =     7.943
Const AL_EQUALIZER_DEFAULT_LOW_GAIN#                 =     1.0

Const AL_EQUALIZER_MIN_LOW_CUTOFF#                   =     50.0
Const AL_EQUALIZER_MAX_LOW_CUTOFF#                   =     800.0
Const AL_EQUALIZER_DEFAULT_LOW_CUTOFF#               =     200.0

Const AL_EQUALIZER_MIN_MID1_GAIN#                    =     0.126
Const AL_EQUALIZER_MAX_MID1_GAIN#                    =     7.943
Const AL_EQUALIZER_DEFAULT_MID1_GAIN#                =     1.0

Const AL_EQUALIZER_MIN_MID1_CENTER#                  =     200.0
Const AL_EQUALIZER_MAX_MID1_CENTER#                  =     3000.0
Const AL_EQUALIZER_DEFAULT_MID1_CENTER#              =     500.0

Const AL_EQUALIZER_MIN_MID1_WIDTH#                   =     0.01
Const AL_EQUALIZER_MAX_MID1_WIDTH#                   =     1.0
Const AL_EQUALIZER_DEFAULT_MID1_WIDTH#               =     1.0

Const AL_EQUALIZER_MIN_MID2_GAIN#                    =     0.126
Const AL_EQUALIZER_MAX_MID2_GAIN#                    =     7.943
Const AL_EQUALIZER_DEFAULT_MID2_GAIN#                =     1.0

Const AL_EQUALIZER_MIN_MID2_CENTER#                  =     1000.0
Const AL_EQUALIZER_MAX_MID2_CENTER#                  =     8000.0
Const AL_EQUALIZER_DEFAULT_MID2_CENTER#              =     3000.0

Const AL_EQUALIZER_MIN_MID2_WIDTH#                   =     0.01
Const AL_EQUALIZER_MAX_MID2_WIDTH#                   =     1.0
Const AL_EQUALIZER_DEFAULT_MID2_WIDTH#               =     1.0

Const AL_EQUALIZER_MIN_HIGH_GAIN#                    =     0.126
Const AL_EQUALIZER_MAX_HIGH_GAIN#                    =     7.943
Const AL_EQUALIZER_DEFAULT_HIGH_GAIN#                =     1.0

Const AL_EQUALIZER_MIN_HIGH_CUTOFF#                  =     4000.0
Const AL_EQUALIZER_MAX_HIGH_CUTOFF#                  =     16000.0
Const AL_EQUALIZER_DEFAULT_HIGH_CUTOFF#              =     6000.0

'/**********************************************************
' * Source parameter value definitions, ranges And defaults.
' */
Const AL_MIN_AIR_ABSORPTION_FACTOR#                  =    0.0
Const AL_MAX_AIR_ABSORPTION_FACTOR#                  =    10.0
Const AL_DEFAULT_AIR_ABSORPTION_FACTOR#              =    0.0

Const AL_MIN_ROOM_ROLLOFF_FACTOR#                    =    0.0
Const AL_MAX_ROOM_ROLLOFF_FACTOR#                    =    10.0
Const AL_DEFAULT_ROOM_ROLLOFF_FACTOR#                =    0.0

Const AL_MIN_CONE_OUTER_GAINHF#                      =    0.0
Const AL_MAX_CONE_OUTER_GAINHF#                      =    1.0
Const AL_DEFAULT_CONE_OUTER_GAINHF#                  =    1.0

Const AL_MIN_DIRECT_FILTER_GAINHF_AUTO               =    0'AL_FALSE
Const AL_MAX_DIRECT_FILTER_GAINHF_AUTO               =    1'AL_TRUE
Const AL_DEFAULT_DIRECT_FILTER_GAINHF_AUTO           =    1'AL_TRUE

Const AL_MIN_AUXILIARY_SEND_FILTER_GAIN_AUTO         =    0'AL_FALSE
Const AL_MAX_AUXILIARY_SEND_FILTER_GAIN_AUTO         =    1'AL_TRUE
Const AL_DEFAULT_AUXILIARY_SEND_FILTER_GAIN_AUTO     =    1'AL_TRUE

Const AL_MIN_AUXILIARY_SEND_FILTER_GAINHF_AUTO       =    0'AL_FALSE
Const AL_MAX_AUXILIARY_SEND_FILTER_GAINHF_AUTO       =    1'AL_TRUE
Const AL_DEFAULT_AUXILIARY_SEND_FILTER_GAINHF_AUTO   =    1'AL_TRUE

'/**********************************************************
' * Listener parameter value definitions, ranges And defaults.
' */
'Const AL_MIN_METERS_PER_UNIT                         =    'FLT_MIN
'Const AL_MAX_METERS_PER_UNIT                         =    'FLT_MAX
Const AL_DEFAULT_METERS_PER_UNIT#                    =    1.0[/code]

efx-creative.bmx:
[code]'/**
' * Effect Object definitions To be used with alEffect functions.
' *
' * Effect parameter value definitions, ranges, And defaults
' * appear farther down in this file.
' */

'/* AL EAXReverb effect parameters. */
Const AL_EAXREVERB_DENSITY                             =  $0001
Const AL_EAXREVERB_DIFFUSION                           =  $0002
Const AL_EAXREVERB_GAIN                                =  $0003
Const AL_EAXREVERB_GAINHF                              =  $0004
Const AL_EAXREVERB_GAINLF                              =  $0005
Const AL_EAXREVERB_DECAY_TIME                          =  $0006
Const AL_EAXREVERB_DECAY_HFRATIO                       =  $0007
Const AL_EAXREVERB_DECAY_LFRATIO                       =  $0008
Const AL_EAXREVERB_REFLECTIONS_GAIN                    =  $0009
Const AL_EAXREVERB_REFLECTIONS_DELAY                   =  $000A
Const AL_EAXREVERB_REFLECTIONS_PAN                     =  $000B
Const AL_EAXREVERB_LATE_REVERB_GAIN                    =  $000C
Const AL_EAXREVERB_LATE_REVERB_DELAY                   =  $000D
Const AL_EAXREVERB_LATE_REVERB_PAN                     =  $000E
Const AL_EAXREVERB_ECHO_TIME                           =  $000F
Const AL_EAXREVERB_ECHO_DEPTH                          =  $0010
Const AL_EAXREVERB_MODULATION_TIME                     =  $0011
Const AL_EAXREVERB_MODULATION_DEPTH                    =  $0012
Const AL_EAXREVERB_AIR_ABSORPTION_GAINHF               =  $0013 
Const AL_EAXREVERB_HFREFERENCE                         =  $0014 
Const AL_EAXREVERB_LFREFERENCE                         =  $0015 
Const AL_EAXREVERB_ROOM_ROLLOFF_FACTOR                 =  $0016
Const AL_EAXREVERB_DECAY_HFLIMIT                       =  $0017

'/* Effect Type definitions To be used with AL_EFFECT_TYPE. */
Const AL_EFFECT_EAXREVERB                              =  $8000

' /**********************************************************
' * Effect parameter structures, value definitions, ranges And defaults.
' */

'/**
' * AL reverb effect parameter ranges And defaults
' */
Const AL_EAXREVERB_MIN_DENSITY#                        =   0.0
Const AL_EAXREVERB_MAX_DENSITY#                        =   1.0
Const AL_EAXREVERB_DEFAULT_DENSITY#                    =   1.0

Const AL_EAXREVERB_MIN_DIFFUSION#                      =   0.0
Const AL_EAXREVERB_MAX_DIFFUSION#                      =   1.0
Const AL_EAXREVERB_DEFAULT_DIFFUSION#                  =   1.0

Const AL_EAXREVERB_MIN_GAIN#                           =   0.0
Const AL_EAXREVERB_MAX_GAIN#                           =   1.0
Const AL_EAXREVERB_DEFAULT_GAIN#                       =   0.32

Const AL_EAXREVERB_MIN_GAINHF#                         =   0.0
Const AL_EAXREVERB_MAX_GAINHF#                         =   1.0
Const AL_EAXREVERB_DEFAULT_GAINHF#                     =   0.89

Const AL_EAXREVERB_MIN_GAINLF#                         =   0.0
Const AL_EAXREVERB_MAX_GAINLF#                         =   1.0
Const AL_EAXREVERB_DEFAULT_GAINLF#                     =   1.0

Const AL_EAXREVERB_MIN_DECAY_TIME#                     =   0.1
Const AL_EAXREVERB_MAX_DECAY_TIME#                     =   20.0
Const AL_EAXREVERB_DEFAULT_DECAY_TIME#                 =   1.49

Const AL_EAXREVERB_MIN_DECAY_HFRATIO#                  =   0.1
Const AL_EAXREVERB_MAX_DECAY_HFRATIO#                  =   2.0
Const AL_EAXREVERB_DEFAULT_DECAY_HFRATIO#              =   0.83

Const AL_EAXREVERB_MIN_DECAY_LFRATIO#                  =   0.1
Const AL_EAXREVERB_MAX_DECAY_LFRATIO#                  =   2.0
Const AL_EAXREVERB_DEFAULT_DECAY_LFRATIO#              =   1.0

Const AL_EAXREVERB_MIN_REFLECTIONS_GAIN#               =   0.0
Const AL_EAXREVERB_MAX_REFLECTIONS_GAIN#               =   3.16
Const AL_EAXREVERB_DEFAULT_REFLECTIONS_GAIN#           =   0.05

Const AL_EAXREVERB_MIN_REFLECTIONS_DELAY#              =   0.0
Const AL_EAXREVERB_MAX_REFLECTIONS_DELAY#              =   0.3
Const AL_EAXREVERB_DEFAULT_REFLECTIONS_DELAY#          =   0.007

Global AL_EAXREVERB_DEFAULT_REFLECTIONS_PAN#[]    	   =   [0.0, 0.0, 0.0]

Const AL_EAXREVERB_MIN_LATE_REVERB_GAIN#               =   0.0
Const AL_EAXREVERB_MAX_LATE_REVERB_GAIN#               =   10.0
Const AL_EAXREVERB_DEFAULT_LATE_REVERB_GAIN#           =   1.26

Const AL_EAXREVERB_MIN_LATE_REVERB_DELAY#              =   0.0
Const AL_EAXREVERB_MAX_LATE_REVERB_DELAY#              =   0.1
Const AL_EAXREVERB_DEFAULT_LATE_REVERB_DELAY#          =   0.011

Global AL_EAXREVERB_DEFAULT_LATE_REVERB_PAN#[]         =   [0.0, 0.0, 0.0]

Const AL_EAXREVERB_MIN_ECHO_TIME#                      =   0.075
Const AL_EAXREVERB_MAX_ECHO_TIME#                      =   0.25
Const AL_EAXREVERB_DEFAULT_ECHO_TIME#                  =   0.25

Const AL_EAXREVERB_MIN_ECHO_DEPTH#                     =   0.0
Const AL_EAXREVERB_MAX_ECHO_DEPTH#                     =   1.0
Const AL_EAXREVERB_DEFAULT_ECHO_DEPTH#                 =   0.0

Const AL_EAXREVERB_MIN_MODULATION_TIME#                =   0.04
Const AL_EAXREVERB_MAX_MODULATION_TIME#                =   4.0
Const AL_EAXREVERB_DEFAULT_MODULATION_TIME#            =   0.25

Const AL_EAXREVERB_MIN_MODULATION_DEPTH#               =   0.0
Const AL_EAXREVERB_MAX_MODULATION_DEPTH#               =   1.0
Const AL_EAXREVERB_DEFAULT_MODULATION_DEPTH#           =   0.0

Const AL_EAXREVERB_MIN_AIR_ABSORPTION_GAINHF#          =   0.892
Const AL_EAXREVERB_MAX_AIR_ABSORPTION_GAINHF#          =   1.0
Const AL_EAXREVERB_DEFAULT_AIR_ABSORPTION_GAINHF#      =   0.994

Const AL_EAXREVERB_MIN_HFREFERENCE#                    =   1000.0
Const AL_EAXREVERB_MAX_HFREFERENCE#                    =   20000.0
Const AL_EAXREVERB_DEFAULT_HFREFERENCE#                =   5000.0

Const AL_EAXREVERB_MIN_LFREFERENCE#                    =   20.0
Const AL_EAXREVERB_MAX_LFREFERENCE#                    =   1000.0
Const AL_EAXREVERB_DEFAULT_LFREFERENCE#                =   250.0

Const AL_EAXREVERB_MIN_ROOM_ROLLOFF_FACTOR#            =   0.0
Const AL_EAXREVERB_MAX_ROOM_ROLLOFF_FACTOR#            =   10.0
Const AL_EAXREVERB_DEFAULT_ROOM_ROLLOFF_FACTOR#        =   0.0

Const AL_EAXREVERB_MIN_DECAY_HFLIMIT#                  =   0'AL_FALSE
Const AL_EAXREVERB_MAX_DECAY_HFLIMIT#                  =   1'AL_TRUE
Const AL_EAXREVERB_DEFAULT_DECAY_HFLIMIT#              =   1'AL_TRUE[/code]

efx-util.bmx:
[code]'/*******************************************************************\
'*                                                                   *
'*  EFX-UTIL.H - EFX Utilities functions And Reverb Presets          *
'*                                                                   *
'*               File revision 1.0                                   *
'*                                                                   *
'\*******************************************************************/


Type EAXVECTOR
	Field x:Float
	Field y:Float
	Field z:Float
EndType

Type EAXREVERBPROPERTIES

    Field ulEnvironment:Int				' unsigned
    Field flEnvironmentSize:Float
    Field flEnvironmentDiffusion:Float
    Field lRoom:Int
    Field lRoomHF:Int
    Field lRoomLF:Int
    Field flDecayTime:Float
    Field flDecayHFRatio:Float
    Field flDecayLFRatio:Float
    Field lReflections:Int
    Field flReflectionsDelay:Float
    Field vReflectionsPan:Float[3]
    Field lReverb:Int
    Field flReverbDelay:Float
    Field vReverbPan:Float[3]
    Field flEchoTime:Float
    Field flEchoDepth:Float
    Field flModulationTime:Float
    Field flModulationDepth:Float
    Field flAirAbsorptionHF:Float
    Field flHFReference:Float
    Field flLFReference:Float
    Field flRoomRolloffFactor:Float=1.0
    Field ulFlags:Int					' unsigned

	Function Create:EAXREVERBPROPERTIES(ulEnvironment:Int,flEnvironmentSize:Float,flEnvironmentDiffusion:Float,lRoom:Int,lRoomHF:Int,lRoomLF:Int,flDecayTime:Float,flDecayHFRatio:Float,flDecayLFRatio:Float,lReflections:Int,flReflectionsDelay:Float,vReflectionsPanX:Float,vReflectionsPanY:Float,vReflectionsPanZ:Float,lReverb:Int,flReverbDelay:Float,vReverbPanX:Float,vReverbPanY:Float,vReverbPanZ:Float,flEchoTime:Float,flEchoDepth:Float,flModulationTime:Float,flModulationDepth:Float,flAirAbsorptionHF:Float,flHFReference:Float,flLFReference:Float,flRoomRolloffFactor:Float,ulFlags:Int)
		Local This:EAXREVERBPROPERTIES = New EAXREVERBPROPERTIES
	    This.ulEnvironment 			= ulEnvironment 
	    This.flEnvironmentSize 		= flEnvironmentSize 
	    This.flEnvironmentDiffusion = flEnvironmentDiffusion
	    This.lRoom 					= lRoom
	    This.lRoomHF 				= lRoomHF
	    This.lRoomLF 				= lRoomLF
	    This.flDecayTime 			= flDecayTime
	    This.flDecayHFRatio 		= flDecayHFRatio
	    This.flDecayLFRatio 		= flDecayLFRatio
	    This.lReflections 			= lReflections
	    This.flReflectionsDelay 	= flReflectionsDelay
	    This.vReflectionsPan 		= [vReflectionsPanX,vReflectionsPanY,vReflectionsPanZ]
	    This.lReverb 				= lReverb
	    This.flReverbDelay 			= flReverbDelay
	    This.vReverbPan 			= [vReverbPanX,vReverbPanY,vReverbPanZ]
	    This.flEchoTime 			= flEchoTime
	    This.flEchoDepth 			= flEchoDepth
	    This.flModulationTime 		= flModulationTime
	    This.flModulationDepth 		= flModulationDepth
	    This.flAirAbsorptionHF 		= flAirAbsorptionHF
	    This.flHFReference 			= flHFReference
	    This.flLFReference 			= flLFReference
	    This.flRoomRolloffFactor 	= flRoomRolloffFactor
	    This.ulFlags 				= ulFlags
		Return This
	EndFunction
	
EndType

Type EFXEAXREVERBPROPERTIES
	Field flDensity:Float 
	Field flDiffusion:Float
	Field flGain:Float
	Field flGainHF:Float
	Field flGainLF:Float
	Field flDecayTime:Float
	Field flDecayHFRatio:Float
	Field flDecayLFRatio:Float
	Field flReflectionsGain:Float
	Field flReflectionsDelay:Float
	Field flReflectionsPan:Float[3]
	Field flLateReverbGain:Float
	Field flLateReverbDelay:Float
	Field flLateReverbPan:Float[3]
	Field flEchoTime:Float
	Field flEchoDepth:Float
	Field flModulationTime:Float
	Field flModulationDepth:Float
	Field flAirAbsorptionGainHF:Float
	Field flHFReference:Float
	Field flLFReference:Float
	Field flRoomRolloffFactor:Float=1.0
	Field iDecayHFLimit:Int
EndType

Type EAXOBSTRUCTIONPROPERTIES
    Field lObstruction:Int
    Field flObstructionLFRatio:Float
EndType

Type EAXOCCLUSIONPROPERTIES
	Field lOcclusion:Int
	Field flOcclusionLFRatio:Float
	Field flOcclusionRoomRatio:Float
	Field flOcclusionDirectRatio:Float
EndType

Type EAXEXCLUSIONPROPERTIES
    Field lExclusion:Int
    Field flExclusionLFRatio:Float
EndType

Type EFXLOWPASSFILTER
	Field flGain:Float
	Field flGainHF:Float
EndType

'void ConvertReverbParameters(EAXREVERBPROPERTIES *pEAXProp, EFXEAXREVERBPROPERTIES *pEFXEAXReverb);
'void ConvertObstructionParameters(EAXOBSTRUCTIONPROPERTIES *pObProp, EFXLOWPASSFILTER *pDirectLowPassFilter);
'void ConvertExclusionParameters(EAXEXCLUSIONPROPERTIES *pExProp, EFXLOWPASSFILTER *pSendLowPassFilter);
'void ConvertOcclusionParameters(EAXOCCLUSIONPROPERTIES *pOcProp, EFXLOWPASSFILTER *pDirectLowPassFilter, EFXLOWPASSFILTER *pSendLowPassFilter);

Extern
	Function ConvertReverbParameters 		(pEAXProp:Byte Ptr, pEFXEAXReverb:Byte Ptr)
	Function ConvertObstructionParameters 	(pObProp:Byte Ptr, 	pDirectLowPassFilter:Byte Ptr)
	Function ConvertExclusionParameters 	(pExProp:Byte Ptr, 	pSendLowPassFilter:Byte Ptr)
	Function ConvertOcclusionParameters 	(pOcProp:Byte Ptr, 	pDirectLowPassFilter:Byte Ptr, pSendLowPassFilter:Byte Ptr)
EndExtern	

'Global ConvertReverbParameters 		(pEAXProp:Byte Ptr, pEFXEAXReverb:Byte Ptr)
'Global ConvertObstructionParameters (pObProp:Byte Ptr, 	pDirectLowPassFilter:Byte Ptr)
'Global ConvertExclusionParameters 	(pExProp:Byte Ptr, 	pSendLowPassFilter:Byte Ptr)
'Global ConvertOcclusionParameters 	(pOcProp:Byte Ptr, 	pDirectLowPassFilter:Byte Ptr, pSendLowPassFilter:Byte Ptr)

'/***********************************************************************************************\
'*
'* EAX Reverb Presets in legacy format - use ConvertReverbParameters() To convert To
'* EFX EAX Reverb Presets For use with the OpenAL Effects Extension.
'*
'************************************************************************************************/

'																							Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_GENERIC:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(0,		7.5,	1.000,	-1000,		-100,		0,		1.49,		0.83,		1.00,		-2602,	0.007,	0.00,0.00,0.00,	200,		0.011,	0.00,0.00,0.00,	0.250,	0.000,	0.250,	0.000,	-5.0,		5000.0,	250.0,	0.00,		$3 )
Global REVERB_PRESET_PADDEDCELL:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(1,		1.4,	1.000,	-1000,		-6000,	0,		0.17,		0.10,		1.00,		-1204,	0.001, 	0.00,0.00,0.00,  	207,		0.002,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,		5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_ROOM:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(2,		1.9,	1.000,	-1000,		-454,		0,		0.40,		0.83,		1.00,  	-1646,	0.002, 	0.00,0.00,0.00,	53,		0.003,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_BATHROOM:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(3,		1.4,	1.000,	-1000,  	-1200,	0,		1.49,		0.54,		1.00,  	-370,		0.007, 	0.00,0.00,0.00,	1030,		0.011,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_LIVINGROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(4,		2.5,	1.000,	-1000,  	-6000,	0,		0.50,		0.10,		1.00,  	-1376,	0.003, 	0.00,0.00,0.00,	-1104,	0.004,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_STONEROOM:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(5,		11.6,	1.000,  -1000, 		-300,		0,		2.31,		0.64,		1.00,		-711,		0.012, 	0.00,0.00,0.00,	83,		0.017,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_AUDITORIUM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(6,		21.6,	1.000,  -1000,		-476,		0,		4.32,		0.59,		1.00,		-789,		0.020, 	0.00,0.00,0.00,	-289,		0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000,	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_CONCERTHALL:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(7,		19.6,	1.000,  -1000,		-500,		0,		3.92,		0.70,		1.00, 	-1230,	0.020, 	0.00,0.00,0.00,  	-02,		0.029,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_CAVE:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(8,		14.6,	1.000,  -1000,		0,		0,		2.91,		1.30,		1.00, 	-602,		0.015, 	0.00,0.00,0.00,	-302,		0.022,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,	0.00,		$1f )
Global REVERB_PRESET_ARENA:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(9,		36.2,	1.000,  -1000,		-698,		0,		7.24,		0.33,		1.00, 	-1166,	0.020, 	0.00,0.00,0.00,  	16,		0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_HANGAR:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(10,	50.3,	1.000,  -1000,		-1000,	0,		10.05, 	0.23,		1.00, 	-602,		0.020, 	0.00,0.00,0.00,  	198,		0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_CARPETTEDHALLWAY:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(11,	1.9,	1.000,	-1000,		-4000,	0,		0.30,		0.10,		1.00, 	-1831,	0.002, 	0.00,0.00,0.00,	-1630,	0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_HALLWAY:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(12,	1.8,	1.000,	-1000,		-300,		0,		1.49,		0.59,		1.00, 	-1219,	0.007, 	0.00,0.00,0.00,  	441,		0.011,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_STONECORRIDOR:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(13,	13.5,	1.000,	-1000,		-237,		0,		2.70,		0.79,		1.00, 	-1214,	0.013, 	0.00,0.00,0.00,  	395,		0.020,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_ALLEY:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(14,	7.5,	0.300,	-1000,		-270,		0,		1.49,		0.86,		1.00, 	-1204,	0.007, 	0.00,0.00,0.00,  	-4,		0.011,	0.00,0.00,0.00,	0.125, 	0.950, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_FOREST:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(15,	38.0,	0.300,	-1000,		-3300,	0,		1.49,		0.54,		1.00,  	-2560,	0.162, 	0.00,0.00,0.00,	-229,		0.088,	0.00,0.00,0.00,	0.125, 	1.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,	0.00,		$3f )
Global REVERB_PRESET_CITY:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(16,	7.5,	0.500,	-1000,		-800,		0,		1.49,		0.67,		1.00,  	-2273,	0.007, 	0.00,0.00,0.00,	-1691,	0.011,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_MOUNTAINS:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(17,	100.0, 	0.270,	-1000,		-2500,	0,		1.49,		0.21,		1.00,  	-2780,	0.300, 	0.00,0.00,0.00,	-1434,	0.100,	0.00,0.00,0.00,	0.250, 	1.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$1f )
Global REVERB_PRESET_QUARRY:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(18,	17.5,	1.000,	-1000,		-1000,	0,		1.49,		0.83,		1.00,		-10000, 	0.061, 	0.00,0.00,0.00,  	500,		0.025,	0.00,0.00,0.00,	0.125, 	0.700, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_PLAIN:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(19,	42.5,	0.210,	-1000,		-2000,	0,		1.49,		0.50,		1.00, 	-2466,	0.179, 	0.00,0.00,0.00,	-1926,	0.100,	0.00,0.00,0.00,	0.250, 	1.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_PARKINGLOT:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(20,	8.3,	1.000,	-1000,		0,		0,		1.65,		1.50,		1.00,  	-1363,	0.008, 	0.00,0.00,0.00,	-1153,	0.012,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$1f )
Global REVERB_PRESET_SEWERPIPE:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(21,	1.7,	0.800,	-1000,		-1000,	0,		2.81,		0.14,		1.00,		429,		0.014, 	0.00,0.00,0.00,	1023,		0.021,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_UNDERWATER:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(22,	1.8,	1.000,	-1000,  	-4000,	0,		1.49,		0.10,		1.00,  	-449,		0.007, 	0.00,0.00,0.00,	1700,		0.011,	0.00,0.00,0.00,	0.250, 	0.000, 	1.180, 	0.348, 	-5.0,  	5000.0,	250.0, 	0.00,		$3f )
Global REVERB_PRESET_DRUGGED:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(23,	1.9,	0.500,	-1000,		0,		0,		8.39,		1.39,		1.00,  	-115,		0.002, 	0.00,0.00,0.00,  	985,		0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	1.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$1f )
Global REVERB_PRESET_DIZZY:EAXREVERBPROPERTIES 				= EAXREVERBPROPERTIES.Create	(24,	1.8,	0.600,	-1000,		-400,		0,		17.23, 	0.56,		1.00,  	-1713,	0.020, 	0.00,0.00,0.00,	-613,		0.030,	0.00,0.00,0.00,	0.250, 	1.000, 	0.810, 	0.310, 	-5.0,  	5000.0,	250.0, 	0.00,		$1f )
Global REVERB_PRESET_PSYCHOTIC:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(25,	1.0,	0.500,	-1000,		-151,		0,		7.56,		0.91,		1.00,  	-626,		0.020, 	0.00,0.00,0.00,  	774,		0.030,	0.00,0.00,0.00,	0.250, 	0.000, 	4.000, 	1.000, 	-5.0,  	5000.0,	250.0, 	0.00,		$1f )

' CASTLE PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_CASTLE_SMALLROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.890,	-1000,	-800,		-2000,	1.22,		0.83,		0.31,		-100,		0.022, 	0.00,0.00,0.00,	600,		0.011,	0.00,0.00,0.00,	0.138,	0.080,	0.250,	0.000,	-5.0,		5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_SHORTPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.890, 	-1000,  	-1000,  	-2000,  	2.32,		0.83,		0.31,		-100,		0.007, 	0.00,0.00,0.00,  	200,		0.023,	0.00,0.00,0.00,	0.138, 	0.080, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_MEDIUMROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.930, 	-1000,  	-1100,  	-2000,  	2.04,		0.83,		0.46,  	-400,		0.022, 	0.00,0.00,0.00,	400,		0.011,	0.00,0.00,0.00,	0.155, 	0.030, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_LONGPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.890, 	-1000,  	-800,		-2000,  	3.42,		0.83,		0.31,  	-100,		0.007, 	0.00,0.00,0.00,	300,		0.023,	0.00,0.00,0.00,	0.138, 	0.080, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_LARGEROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.820, 	-1000,  	-1100,  	-1800,  	2.53,		0.83,		0.50,  	-700,		0.034, 	0.00,0.00,0.00,	200,		0.016,	0.00,0.00,0.00,	0.185, 	0.070, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_HALL:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.810, 	-1000,  	-1100,  	-1500,  	3.14,		0.79,		0.62,  	-1500,	0.056, 	0.00,0.00,0.00,	100,		0.024,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_CUPBOARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.890, 	-1000,  	-1100,  	-2000,  	0.67,		0.87,		0.31,  	300,		0.010, 	0.00,0.00,0.00,	1100,		0.007,	0.00,0.00,0.00,	0.138, 	0.080, 	0.250, 	0.000, 	-5.0,  	5168.6,	139.5,  	0.00, 	$20 )
Global REVERB_PRESET_CASTLE_COURTYARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.420, 	-1000,  	-700,   	-1400,	2.13,		0.61,		0.23,  	-1300,	0.160, 	0.00,0.00,0.00,	-300,		0.036,	0.00,0.00,0.00,	0.250, 	0.370, 	0.250, 	0.000, 	-5.0, 	5000.0,	250.0,  	0.00, 	$1f )
Global REVERB_PRESET_CASTLE_ALCOVE:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   	8.3,	0.890,	-1000,  	-600,		-2000,  	1.64,		0.87,		0.31,  	00,		0.007, 	0.00,0.00,0.00,	300,		0.034,	0.00,0.00,0.00,	0.138, 	0.080, 	0.250, 	0.000, 	-5.0,		5168.6,	139.5,  	0.00, 	$20 )

' FACTORY PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_FACTORY_ALCOVE:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.8,	0.590,  -1200, 	-200,   	-600,		3.14,		0.65,		1.31,  	300,		0.010, 	0.00,0.00,0.00,	000,		0.038,	0.00,0.00,0.00,	0.114, 	0.100, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_SHORTPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.8,	0.640,  -1200, 	-200,   	-600,		2.53,		0.65,		1.31,  	0,		0.010, 	0.00,0.00,0.00,	200,		0.038,	0.00,0.00,0.00,	0.135, 	0.230, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_MEDIUMROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.9,	0.820,  -1200, 	-200,   	-600,		2.76,		0.65,		1.31,  	-1100,	0.022, 	0.00,0.00,0.00,	300,		0.023,	0.00,0.00,0.00,	0.174, 	0.070, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_LONGPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.8,	0.640,  -1200, 	-200,   	-600,		4.06,		0.65,		1.31,  	0,		0.020, 	0.00,0.00,0.00,	200,		0.037,	0.00,0.00,0.00,	0.135, 	0.230, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_LARGEROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.9,	0.750,  -1200, 	-300,   	-400,		4.24,		0.51,		1.31,  	-1500,	0.039, 	0.00,0.00,0.00,	100,		0.023,	0.00,0.00,0.00,	0.231, 	0.070, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_HALL:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   	1.9,	0.750,  -1000, 	-300,   	-400,		7.43,		0.51,		1.31,  	-2400,	0.073, 	0.00,0.00,0.00,	-100,		0.027,	0.00,0.00,0.00,	0.250, 	0.070, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_CUPBOARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.7,	0.630,  -1200, 	-200,   	-600,		0.49,		0.65,		1.31,  	200,		0.010, 	0.00,0.00,0.00,	600,		0.032,	0.00,0.00,0.00,	0.107, 	0.070, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_COURTYARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.7,	0.570,  -1000, 	-1000,  	-400,		2.32,		0.29,		0.56,  	-1300,	0.140, 	0.00,0.00,0.00,	-800,		0.039,	0.00,0.00,0.00,	0.250, 	0.290, 	0.250, 	0.000, 	-5.0,  	3762.6,	362.5,  	0.00, 	$20 )
Global REVERB_PRESET_FACTORY_SMALLROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.8,	0.820,  -1000,	-200,   	-600,		1.72,		0.65,		1.31,  	-300,		0.010, 	0.00,0.00,0.00,	500,		0.024,	0.00,0.00,0.00,	0.119, 	0.070, 	0.250, 	0.000, 	-5.0,		3762.6,	362.5,  	0.00, 	$20 )

' ICE PALACE PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_ICEPALACE_ALCOVE:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   2.7,		0.840, 	-1000,  	-500,		-1100,  	2.76,		1.46,		0.28,  	100,		0.010, 	0.00,0.00,0.00,	-100,		0.030,	0.00,0.00,0.00,	0.161, 	0.090, 	0.250, 	0.000,	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_SHORTPASSAGE:EAXREVERBPROPERTIES = EAXREVERBPROPERTIES.Create	(26,   2.7,		0.750, 	-1000,  	-500,		-1100,  	1.79,		1.46,		0.28,  	-600,		0.010, 	0.00,0.00,0.00,	100,		0.019,	0.00,0.00,0.00,	0.177, 	0.090, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_MEDIUMROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.7,		0.870, 	-1000,  	-500,   	-700,		2.22,		1.53,		0.32,  	-800,		0.039, 	0.00,0.00,0.00,	100,		0.027,	0.00,0.00,0.00,	0.186, 	0.120, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_LONGPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.7,		0.770, 	-1000,  	-500,   	-800,		3.01,		1.46,		0.28,  	-200,		0.012, 	0.00,0.00,0.00,	200,		0.025,	0.00,0.00,0.00,	0.186, 	0.040, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_LARGEROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.9,		0.810, 	-1000,  	-500,   	-700,		3.14,		1.53,		0.32,  	-1200,	0.039, 	0.00,0.00,0.00,	000,		0.027,	0.00,0.00,0.00,	0.214, 	0.110, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_HALL:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   2.9,		0.760, 	-1000,  	-700,   	-500,		5.49,		1.53,		0.38,  	-1900,	0.054, 	0.00,0.00,0.00,	-400,		0.052,	0.00,0.00,0.00,	0.226, 	0.110, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_CUPBOARD:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.7,		0.830, 	-1000,  	-600,		-1300,  	0.76,		1.53,		0.26,  	100,		0.012, 	0.00,0.00,0.00,	600,		0.016,	0.00,0.00,0.00,	0.143, 	0.080, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_COURTYARD:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.9,		0.590, 	-1000,  	-1100,  	-1000,  	2.04,		1.20,		0.38,  	-1000,	0.173, 	0.00,0.00,0.00,	-1000,	0.043,	0.00,0.00,0.00,	0.235, 	0.480, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )
Global REVERB_PRESET_ICEPALACE_SMALLROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   2.7,		0.840, 	-1000,  	-500,		-1100,  	1.51,		1.53,		0.27,		-100,		0.010, 	0.00,0.00,0.00,	300,		0.011,	0.00,0.00,0.00,	0.164, 	0.140, 	0.250, 	0.000, 	-5.0,		12428.5,	99.6,  	0.00,		$20 )

' SPACE STATION PRESETS
'																									Env		Size	Diffus	Room	RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_SPACESTATION_ALCOVE:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.5,	0.780, 	-1000,  -300,   	-100,		1.16,		0.81,		0.55,  	300,		0.007, 	0.00,0.00,0.00,	000,		0.018,	0.00,0.00,0.00,	0.192, 	0.210, 	0.250, 	0.000,	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_MEDIUMROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.5,	0.750, 	-1000,  -400,   	-100,		3.01,		0.50,		0.55,  	-800,		0.034, 	0.00,0.00,0.00,	100,		0.035,	0.00,0.00,0.00,	0.209, 	0.310, 	0.250, 	0.000,	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_SHORTPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.5,	0.870, 	-1000,  -400,   	-100,		3.57,		0.50,		0.55,  	0,		0.012, 	0.00,0.00,0.00,	100,		0.016,	0.00,0.00,0.00,	0.172, 	0.200, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_LONGPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.9,	0.820, 	-1000,  -400,   	-100,		4.62,		0.62,		0.55,  	0,		0.012, 	0.00,0.00,0.00,	200,		0.031,	0.00,0.00,0.00,	0.250, 	0.230, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_LARGEROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.8,	0.810, 	-1000,  -400,   	-100,		3.89,		0.38,		0.61,  	-1000,	0.056, 	0.00,0.00,0.00,	-100,		0.035,	0.00,0.00,0.00,	0.233, 	0.280, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_HALL:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   	1.9,	0.870, 	-1000,  -400,   	-100,		7.11,		0.38,		0.61,  	-1500,	0.100, 	0.00,0.00,0.00,	-400,		0.047,	0.00,0.00,0.00,	0.250, 	0.250, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_CUPBOARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.4,	0.560, 	-1000,  -300,   	-100,		0.79,		0.81,		0.55,  	300,		0.007, 	0.00,0.00,0.00,	500,		0.018,	0.00,0.00,0.00,	0.181, 	0.310, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPACESTATION_SMALLROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.5,	0.700, 	-1000,  -300,   	-100,		1.72,		0.82,		0.55,		-200,		0.007, 	0.00,0.00,0.00,	300,		0.013,	0.00,0.00,0.00,	0.188, 	0.260, 	0.250, 	0.000, 	-5.0,  	3316.1,	458.2,  	0.00, 	$20 )

' WOODEN GALLEON PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_WOODEN_ALCOVE:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-1800, 	-1000,  	1.22,		0.62,		0.91,		100,		0.012, 	0.00,0.00,0.00,	-300,		0.024,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_SHORTPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-1800,  	-1000,  	1.75,		0.50,		0.87,		-100,		0.012, 	0.00,0.00,0.00,	-400,		0.024,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_MEDIUMROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-2000,  	-1100,  	1.47,		0.42,		0.82,		-100,		0.049, 	0.00,0.00,0.00,	-100,		0.029,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_LONGPASSAGE:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-2000,  	-1000,  	1.99,		0.40,		0.79,		000,		0.020, 	0.00,0.00,0.00,	-700,		0.036,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_LARGEROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-2100,  	-1100,  	2.65,		0.33,		0.82,		-100,		0.066, 	0.00,0.00,0.00,	-200,		0.049,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_HALL:EAXREVERBPROPERTIES 			= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-2200,  	-1100,  	3.45,		0.30,		0.82,		-100,		0.088, 	0.00,0.00,0.00,	-200,		0.063,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_CUPBOARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-1700,  	-1000,  	0.56,		0.46,		0.91,		100,		0.012, 	0.00,0.00,0.00,	100,		0.028,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_SMALLROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   7.5,		1.000, 	-1000,  	-1900,  	-1000,  	0.79,		0.32,		0.87,		00,		0.032, 	0.00,0.00,0.00,	-100,		0.029,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )
Global REVERB_PRESET_WOODEN_COURTYARD:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   7.5,		0.650, 	-1000,  	-2200,  	-1000,  	1.79,		0.35,		0.79,		-500,		0.123, 	0.00,0.00,0.00,	-2000,	0.032,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	4705.0,	99.6,  	0.00,		$3f )

' SPORTS PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_SPORT_EMPTYSTADIUM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	7.2,	1.000, 	-1000,  	-700,   	-200,		6.26,		0.51,		1.10,  	-2400,	0.183, 	0.00,0.00,0.00,	-800,		0.038,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,  	0.00, 	$20 )
Global REVERB_PRESET_SPORT_SQUASHCOURT:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	7.5,	0.750, 	-1000,  	-1000,  	-200,		2.22,		0.91,		1.16,  	-700,		0.007, 	0.00,0.00,0.00,	-200,		0.011,	0.00,0.00,0.00,	0.126, 	0.190, 	0.250, 	0.000, 	-5.0,  	7176.9,	211.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPORT_SMALLSWIMMINGPOOL:EAXREVERBPROPERTIES= EAXREVERBPROPERTIES.Create	(26,  	36.2,	0.700, 	-1000,  	-200,   	-100,		2.76,		1.25,		1.14,  	-400,		0.020, 	0.00,0.00,0.00,	-200,		0.030,	0.00,0.00,0.00,	0.179, 	0.150, 	0.895, 	0.190, 	-5.0,  	5000.0,	250.0,  	0.00, 	$0  )
Global REVERB_PRESET_SPORT_LARGESWIMMINGPOOL:EAXREVERBPROPERTIES= EAXREVERBPROPERTIES.Create	(26,  	36.2,	0.820, 	-1000,  	-200,   	0,		5.49,		1.31,		1.14,  	-700,		0.039, 	0.00,0.00,0.00,	-600,		0.049,	0.00,0.00,0.00,	0.222, 	0.550, 	1.159, 	0.210, 	-5.0,  	5000.0,	250.0,  	0.00, 	$0  )
Global REVERB_PRESET_SPORT_GYMNASIUM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	7.5,	0.810, 	-1000,  	-700,   	-100,		3.14,		1.06,		1.35,  	-800,		0.029, 	0.00,0.00,0.00,	-500,		0.045,	0.00,0.00,0.00,	0.146, 	0.140, 	0.250, 	0.000, 	-5.0,  	7176.9,	211.2,  	0.00, 	$20 )
Global REVERB_PRESET_SPORT_FULLSTADIUM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	7.2,	1.000, 	-1000,  	-2300,  	-200,		5.25,		0.17,		0.80,  	-2000,	0.188, 	0.00,0.00,0.00,	-1100,	0.038,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,  	0.00, 	$20 )
Global REVERB_PRESET_SPORT_STADIUMTANNOY:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	3.0,	0.780, 	-1000,   	-500,   	-600,		2.53,		0.88,		0.68,  	-1100,	0.230, 	0.00,0.00,0.00,	-600,		0.063,	0.00,0.00,0.00,	0.250, 	0.200, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,  	0.00, 	$20 )

' PREFAB PRESETS
'																								Env		Size	Diffus	Room		RoomHF	RoomLF	DecTm		DcHF		DcLF		Refl		RefDel	Ref Pan		Revb		RevDel	Rev Pan		EchTm		EchDp		ModTm		ModDp		AirAbs	HFRef		LFRef		RRlOff	FLAGS
Global REVERB_PRESET_PREFAB_WORKSHOP:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.9,	1.000, 	-1000,  	-1700,  	-800,		0.76,		1.00,		1.00,		0,		0.012, 	0.00,0.00,0.00,	100,		0.012,	0.00,0.00,0.00,	0.250, 	0.000, 	0.250, 	0.000, 	-5.0,  	5000.0,	250.0,  	0.00, 	$0  )
Global REVERB_PRESET_PREFAB_SCHOOLROOM:EAXREVERBPROPERTIES 		= EAXREVERBPROPERTIES.Create	(26,   	1.86,	0.690, 	-1000,  	-400,   	-600,		0.98,		0.45,		0.18,  	300,		0.017, 	0.00,0.00,0.00,  	300,		0.015,	0.00,0.00,0.00,	0.095, 	0.140, 	0.250, 	0.000, 	-5.0,  	7176.9,	211.2,  	0.00, 	$20 )
Global REVERB_PRESET_PREFAB_PRACTISEROOM:EAXREVERBPROPERTIES 	= EAXREVERBPROPERTIES.Create	(26,   	1.86,	0.870, 	-1000,  	-800,   	-600
