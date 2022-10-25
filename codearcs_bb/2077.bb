; ID: 2077
; Author: ZJP
; Date: 2007-07-25 09:15:40
; Title: Blitz3dSDK - C/C++ - Physx's Rubux Wrapper Part 1
; Description: How to use Physx's Rubux  wrapper and B3DSDK in C/C++ Project

// DO NOT OPEN With BB !!!.Save as "INCLUDE_physx.cpp"

HINSTANCE hDLL = LoadLibrary("Blitzpx.dll");
 
typedef int(WINAPI *DLL_pxBodyCreatePlane)(float x, float y, float z);
DLL_pxBodyCreatePlane pxBodyCreatePlane;
pxBodyCreatePlane = (DLL_pxBodyCreatePlane)GetProcAddress(hDLL,"_pxBodyCreatePlane@12");
// Use ==> int result = pxBodyCreatePlane(float x, float y, float z);
 
typedef int(WINAPI *DLL_pxBodyCreateCube)(float dx, float dy, float dz, float mass);
DLL_pxBodyCreateCube pxBodyCreateCube;
pxBodyCreateCube = (DLL_pxBodyCreateCube)GetProcAddress(hDLL,"_pxBodyCreateCube@16");
// Use ==> int result = pxBodyCreateCube(float dx, float dy, float dz, float mass);
 
typedef int(WINAPI *DLL_pxBodyCreateSphere)(float radius, float mass);
DLL_pxBodyCreateSphere pxBodyCreateSphere;
pxBodyCreateSphere = (DLL_pxBodyCreateSphere)GetProcAddress(hDLL,"_pxBodyCreateSphere@8");
// Use ==> int result = pxBodyCreateSphere(float radius, float mass);
 
typedef int(WINAPI *DLL_pxBodyCreateCapsule)(float height, float radius, float mass);
DLL_pxBodyCreateCapsule pxBodyCreateCapsule;
pxBodyCreateCapsule = (DLL_pxBodyCreateCapsule)GetProcAddress(hDLL,"_pxBodyCreateCapsule@12");
// Use ==> int result = pxBodyCreateCapsule(float height, float radius, float mass);
 
typedef int(WINAPI *DLL_pxBodyCreateCylinder)(float radius, float height, int nbEdge, float mass);
DLL_pxBodyCreateCylinder pxBodyCreateCylinder;
pxBodyCreateCylinder = (DLL_pxBodyCreateCylinder)GetProcAddress(hDLL,"_pxBodyCreateCylinder@16");
// Use ==> int result = pxBodyCreateCylinder(float radius, float height, int nbEdge, float mass);
 
typedef int(WINAPI *DLL_pxBodyCreateHull)(int *vbank, int nvert, float mass);
DLL_pxBodyCreateHull pxBodyCreateHull;
pxBodyCreateHull = (DLL_pxBodyCreateHull)GetProcAddress(hDLL,"_pxBodyCreateHull@12");
// Use ==> int result = pxBodyCreateHull(int *vbank, int nvert, float mass);
 
typedef int(WINAPI *DLL_pxBodyCreateHullFromSSM)(int surf, float mass);
DLL_pxBodyCreateHullFromSSM pxBodyCreateHullFromSSM;
pxBodyCreateHullFromSSM = (DLL_pxBodyCreateHullFromSSM)GetProcAddress(hDLL,"_pxBodyCreateHullFromSSM@8");
// Use ==> int result = pxBodyCreateHullFromSSM(int surf, float mass);
 
typedef int(WINAPI *DLL_pxCreateTriMeshPmap)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, char *file_name, int pMap);
DLL_pxCreateTriMeshPmap pxCreateTriMeshPmap;
pxCreateTriMeshPmap = (DLL_pxCreateTriMeshPmap)GetProcAddress(hDLL,"_pxCreateTriMeshPmap@24");
// Use ==> int result = pxCreateTriMeshPmap(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, char *file_name, int pMap);
 
typedef int(WINAPI *DLL_pxCreateTriMeshFromPmap)(int triangleMesh, float mass);
DLL_pxCreateTriMeshFromPmap pxCreateTriMeshFromPmap;
pxCreateTriMeshFromPmap = (DLL_pxCreateTriMeshFromPmap)GetProcAddress(hDLL,"_pxCreateTriMeshFromPmap@8");
// Use ==> int result = pxCreateTriMeshFromPmap(int triangleMesh, float mass);
 
typedef int(WINAPI *DLL_pxCreateTriMesh)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float mass);
DLL_pxCreateTriMesh pxCreateTriMesh;
pxCreateTriMesh = (DLL_pxCreateTriMesh)GetProcAddress(hDLL,"_pxCreateTriMesh@20");
// Use ==> int result = pxCreateTriMesh(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float mass);
 
typedef int(WINAPI *DLL_pxCreateTerrain)(int nSize, int *bank, float scale_x, float scale_y, float scale_z);
DLL_pxCreateTerrain pxCreateTerrain;
pxCreateTerrain = (DLL_pxCreateTerrain)GetProcAddress(hDLL,"_pxCreateTerrain@20");
// Use ==> int result = pxCreateTerrain(int nSize, int *bank, float scale_x, float scale_y, float scale_z);
 
typedef int(WINAPI *DLL_pxCreateTerrainFromMesh)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float axis);
DLL_pxCreateTerrainFromMesh pxCreateTerrainFromMesh;
pxCreateTerrainFromMesh = (DLL_pxCreateTerrainFromMesh)GetProcAddress(hDLL,"_pxCreateTerrainFromMesh@20");
// Use ==> int result = pxCreateTerrainFromMesh(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float axis);
 
typedef void(WINAPI *DLL_pxCreateTerrainPmap)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float axis, char *file_name, int pMap);
DLL_pxCreateTerrainPmap pxCreateTerrainPmap;
pxCreateTerrainPmap = (DLL_pxCreateTerrainPmap)GetProcAddress(hDLL,"_pxCreateTerrainPmap@28");
// Use ==> pxCreateTerrainPmap(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, float axis, char *file_name, int pMap);
 
typedef int(WINAPI *DLL_pxTestTriMesh)(int entity, float mass);
DLL_pxTestTriMesh pxTestTriMesh;
pxTestTriMesh = (DLL_pxTestTriMesh)GetProcAddress(hDLL,"_pxTestTriMesh@8");
// Use ==> int result = pxTestTriMesh(int entity, float mass);
 
typedef int(WINAPI *DLL_pxCreateTriMeshToFile)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, char *fname);
DLL_pxCreateTriMeshToFile pxCreateTriMeshToFile;
pxCreateTriMeshToFile = (DLL_pxCreateTriMeshToFile)GetProcAddress(hDLL,"_pxCreateTriMeshToFile@20");
// Use ==> int result = pxCreateTriMeshToFile(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, char *fname);
 
typedef int(WINAPI *DLL_pxCreateTriMeshFromFile)(char *fname, float mass);
DLL_pxCreateTriMeshFromFile pxCreateTriMeshFromFile;
pxCreateTriMeshFromFile = (DLL_pxCreateTriMeshFromFile)GetProcAddress(hDLL,"_pxCreateTriMeshFromFile@8");
// Use ==> int result = pxCreateTriMeshFromFile(char *fname, float mass);
 
typedef int(WINAPI *DLL_pxParticleCreateEmitter)();
DLL_pxParticleCreateEmitter pxParticleCreateEmitter;
pxParticleCreateEmitter = (DLL_pxParticleCreateEmitter)GetProcAddress(hDLL,"_pxParticleCreateEmitter@0");
// Use ==> int result = pxParticleCreateEmitter();
 
typedef void(WINAPI *DLL_pxParticleEmitSetAngDamping)();
DLL_pxParticleEmitSetAngDamping pxParticleEmitSetAngDamping;
pxParticleEmitSetAngDamping = (DLL_pxParticleEmitSetAngDamping)GetProcAddress(hDLL,"_pxParticleEmitSetAngDamping@8");
// Use ==> pxParticleEmitSetAngDamping();
 
typedef void(WINAPI *DLL_pxParticleEmitSetLinDamping)();
DLL_pxParticleEmitSetLinDamping pxParticleEmitSetLinDamping;
pxParticleEmitSetLinDamping = (DLL_pxParticleEmitSetLinDamping)GetProcAddress(hDLL,"_pxParticleEmitSetLinDamping@8");
// Use ==> pxParticleEmitSetLinDamping();
 
typedef void(WINAPI *DLL_pxParticleEmitSetMass)(int pEmitter, float mass);
DLL_pxParticleEmitSetMass pxParticleEmitSetMass;
pxParticleEmitSetMass = (DLL_pxParticleEmitSetMass)GetProcAddress(hDLL,"_pxParticleEmitSetMass@8");
// Use ==> pxParticleEmitSetMass(int pEmitter, float mass);
 
typedef void(WINAPI *DLL_pxParticleEmitSetRadius)(int pEmitter, float radius);
DLL_pxParticleEmitSetRadius pxParticleEmitSetRadius;
pxParticleEmitSetRadius = (DLL_pxParticleEmitSetRadius)GetProcAddress(hDLL,"_pxParticleEmitSetRadius@8");
// Use ==> pxParticleEmitSetRadius(int pEmitter, float radius);
 
typedef void(WINAPI *DLL_pxParticleEmitSetPosition)(int pEmitter, float x, float y, float z);
DLL_pxParticleEmitSetPosition pxParticleEmitSetPosition;
pxParticleEmitSetPosition = (DLL_pxParticleEmitSetPosition)GetProcAddress(hDLL,"_pxParticleEmitSetPosition@16");
// Use ==> pxParticleEmitSetPosition(int pEmitter, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxParticleEmitSetRotation)(int pEmitter, float pitch, float yaw, float roll);
DLL_pxParticleEmitSetRotation pxParticleEmitSetRotation;
pxParticleEmitSetRotation = (DLL_pxParticleEmitSetRotation)GetProcAddress(hDLL,"_pxParticleEmitSetRotation@16");
// Use ==> pxParticleEmitSetRotation(int pEmitter, float pitch, float yaw, float roll);
 
typedef void(WINAPI *DLL_pxParticleEmitSetRandRadius)(int pEmitter, float radius);
DLL_pxParticleEmitSetRandRadius pxParticleEmitSetRandRadius;
pxParticleEmitSetRandRadius = (DLL_pxParticleEmitSetRandRadius)GetProcAddress(hDLL,"_pxParticleEmitSetRandRadius@8");
// Use ==> pxParticleEmitSetRandRadius(int pEmitter, float radius);
 
typedef void(WINAPI *DLL_pxParticleEmitSetStartSpeed)(int pEmitter, float min, float max);
DLL_pxParticleEmitSetStartSpeed pxParticleEmitSetStartSpeed;
pxParticleEmitSetStartSpeed = (DLL_pxParticleEmitSetStartSpeed)GetProcAddress(hDLL,"_pxParticleEmitSetStartSpeed@12");
// Use ==> pxParticleEmitSetStartSpeed(int pEmitter, float min, float max);
 
typedef void(WINAPI *DLL_pxParticleEmitSetTDAcceleration)(int pEmitter, float x, float y, float z);
DLL_pxParticleEmitSetTDAcceleration pxParticleEmitSetTDAcceleration;
pxParticleEmitSetTDAcceleration = (DLL_pxParticleEmitSetTDAcceleration)GetProcAddress(hDLL,"_pxParticleEmitSetTDAcceleration@16");
// Use ==> pxParticleEmitSetTDAcceleration(int pEmitter, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxParticleEmitSetScaleFactor)(int pEmitter, float radius, float rate);
DLL_pxParticleEmitSetScaleFactor pxParticleEmitSetScaleFactor;
pxParticleEmitSetScaleFactor = (DLL_pxParticleEmitSetScaleFactor)GetProcAddress(hDLL,"_pxParticleEmitSetScaleFactor@12");
// Use ==> pxParticleEmitSetScaleFactor(int pEmitter, float radius, float rate);
 
typedef int(WINAPI *DLL_pxParticleEmitDeleteFirstParticle)(int pEmitter);
DLL_pxParticleEmitDeleteFirstParticle pxParticleEmitDeleteFirstParticle;
pxParticleEmitDeleteFirstParticle = (DLL_pxParticleEmitDeleteFirstParticle)GetProcAddress(hDLL,"_pxParticleEmitDeleteFirstParticle@4");
// Use ==> int result = pxParticleEmitDeleteFirstParticle(int pEmitter);
 
typedef void(WINAPI *DLL_pxParticleEmitDeleteParticle)(int pEmitter, int particle);
DLL_pxParticleEmitDeleteParticle pxParticleEmitDeleteParticle;
pxParticleEmitDeleteParticle = (DLL_pxParticleEmitDeleteParticle)GetProcAddress(hDLL,"_pxParticleEmitDeleteParticle@8");
// Use ==> pxParticleEmitDeleteParticle(int pEmitter, int particle);
 
typedef float(WINAPI *DLL_pxParticleEmitGetAngDamping)(int pEmitter);
DLL_pxParticleEmitGetAngDamping pxParticleEmitGetAngDamping;
pxParticleEmitGetAngDamping = (DLL_pxParticleEmitGetAngDamping)GetProcAddress(hDLL,"_pxParticleEmitGetAngDamping@4");
// Use ==> float result = pxParticleEmitGetAngDamping(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetLinDamping)(int pEmitter);
DLL_pxParticleEmitGetLinDamping pxParticleEmitGetLinDamping;
pxParticleEmitGetLinDamping = (DLL_pxParticleEmitGetLinDamping)GetProcAddress(hDLL,"_pxParticleEmitGetLinDamping@4");
// Use ==> float result = pxParticleEmitGetLinDamping(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetMass)(int pEmitter);
DLL_pxParticleEmitGetMass pxParticleEmitGetMass;
pxParticleEmitGetMass = (DLL_pxParticleEmitGetMass)GetProcAddress(hDLL,"_pxParticleEmitGetMass@4");
// Use ==> float result = pxParticleEmitGetMass(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetRadius)(int pEmitter);
DLL_pxParticleEmitGetRadius pxParticleEmitGetRadius;
pxParticleEmitGetRadius = (DLL_pxParticleEmitGetRadius)GetProcAddress(hDLL,"_pxParticleEmitGetRadius@4");
// Use ==> float result = pxParticleEmitGetRadius(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetPositionX)(int pEmitter);
DLL_pxParticleEmitGetPositionX pxParticleEmitGetPositionX;
pxParticleEmitGetPositionX = (DLL_pxParticleEmitGetPositionX)GetProcAddress(hDLL,"_pxParticleEmitGetPositionX@4");
// Use ==> float result = pxParticleEmitGetPositionX(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetPositionY)(int pEmitter);
DLL_pxParticleEmitGetPositionY pxParticleEmitGetPositionY;
pxParticleEmitGetPositionY = (DLL_pxParticleEmitGetPositionY)GetProcAddress(hDLL,"_pxParticleEmitGetPositionY@4");
// Use ==> float result = pxParticleEmitGetPositionY(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetPositionZ)(int pEmitter);
DLL_pxParticleEmitGetPositionZ pxParticleEmitGetPositionZ;
pxParticleEmitGetPositionZ = (DLL_pxParticleEmitGetPositionZ)GetProcAddress(hDLL,"_pxParticleEmitGetPositionZ@4");
// Use ==> float result = pxParticleEmitGetPositionZ(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetRotationPitch)(int pEmitter);
DLL_pxParticleEmitGetRotationPitch pxParticleEmitGetRotationPitch;
pxParticleEmitGetRotationPitch = (DLL_pxParticleEmitGetRotationPitch)GetProcAddress(hDLL,"_pxParticleEmitGetRotationPitch@4");
// Use ==> float result = pxParticleEmitGetRotationPitch(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetRotationYaw)(int pEmitter);
DLL_pxParticleEmitGetRotationYaw pxParticleEmitGetRotationYaw;
pxParticleEmitGetRotationYaw = (DLL_pxParticleEmitGetRotationYaw)GetProcAddress(hDLL,"_pxParticleEmitGetRotationYaw@4");
// Use ==> float result = pxParticleEmitGetRotationYaw(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetRotationRoll)(int pEmitter);
DLL_pxParticleEmitGetRotationRoll pxParticleEmitGetRotationRoll;
pxParticleEmitGetRotationRoll = (DLL_pxParticleEmitGetRotationRoll)GetProcAddress(hDLL,"_pxParticleEmitGetRotationRoll@4");
// Use ==> float result = pxParticleEmitGetRotationRoll(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetRandRadius)(int pEmitter);
DLL_pxParticleEmitGetRandRadius pxParticleEmitGetRandRadius;
pxParticleEmitGetRandRadius = (DLL_pxParticleEmitGetRandRadius)GetProcAddress(hDLL,"_pxParticleEmitGetRandRadius@4");
// Use ==> float result = pxParticleEmitGetRandRadius(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetStartSpeedMax)(int pEmitter);
DLL_pxParticleEmitGetStartSpeedMax pxParticleEmitGetStartSpeedMax;
pxParticleEmitGetStartSpeedMax = (DLL_pxParticleEmitGetStartSpeedMax)GetProcAddress(hDLL,"_pxParticleEmitGetStartSpeedMax@4");
// Use ==> float result = pxParticleEmitGetStartSpeedMax(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetStartSpeedMin)(int pEmitter);
DLL_pxParticleEmitGetStartSpeedMin pxParticleEmitGetStartSpeedMin;
pxParticleEmitGetStartSpeedMin = (DLL_pxParticleEmitGetStartSpeedMin)GetProcAddress(hDLL,"_pxParticleEmitGetStartSpeedMin@4");
// Use ==> float result = pxParticleEmitGetStartSpeedMin(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetTDAccelerationX)(int pEmitter);
DLL_pxParticleEmitGetTDAccelerationX pxParticleEmitGetTDAccelerationX;
pxParticleEmitGetTDAccelerationX = (DLL_pxParticleEmitGetTDAccelerationX)GetProcAddress(hDLL,"_pxParticleEmitGetTDAccelerationX@4");
// Use ==> float result = pxParticleEmitGetTDAccelerationX(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetTDAccelerationY)(int pEmitter);
DLL_pxParticleEmitGetTDAccelerationY pxParticleEmitGetTDAccelerationY;
pxParticleEmitGetTDAccelerationY = (DLL_pxParticleEmitGetTDAccelerationY)GetProcAddress(hDLL,"_pxParticleEmitGetTDAccelerationY@4");
// Use ==> float result = pxParticleEmitGetTDAccelerationY(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetTDAccelerationZ)(int pEmitter);
DLL_pxParticleEmitGetTDAccelerationZ pxParticleEmitGetTDAccelerationZ;
pxParticleEmitGetTDAccelerationZ = (DLL_pxParticleEmitGetTDAccelerationZ)GetProcAddress(hDLL,"_pxParticleEmitGetTDAccelerationZ@4");
// Use ==> float result = pxParticleEmitGetTDAccelerationZ(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetScaleFactorRadius)(int pEmitter);
DLL_pxParticleEmitGetScaleFactorRadius pxParticleEmitGetScaleFactorRadius;
pxParticleEmitGetScaleFactorRadius = (DLL_pxParticleEmitGetScaleFactorRadius)GetProcAddress(hDLL,"_pxParticleEmitGetScaleFactorRadius@4");
// Use ==> float result = pxParticleEmitGetScaleFactorRadius(int pEmitter);
 
typedef float(WINAPI *DLL_pxParticleEmitGetScaleFactorRate)(int pEmitter);
DLL_pxParticleEmitGetScaleFactorRate pxParticleEmitGetScaleFactorRate;
pxParticleEmitGetScaleFactorRate = (DLL_pxParticleEmitGetScaleFactorRate)GetProcAddress(hDLL,"_pxParticleEmitGetScaleFactorRate@4");
// Use ==> float result = pxParticleEmitGetScaleFactorRate(int pEmitter);
 
typedef int(WINAPI *DLL_pxParticleEmitGetNumberParticles)(int pEmitter);
DLL_pxParticleEmitGetNumberParticles pxParticleEmitGetNumberParticles;
pxParticleEmitGetNumberParticles = (DLL_pxParticleEmitGetNumberParticles)GetProcAddress(hDLL,"_pxParticleEmitGetNumberParticles@4");
// Use ==> int result = pxParticleEmitGetNumberParticles(int pEmitter);
 
typedef int(WINAPI *DLL_pxParticleEmitAddParticle)(int pEmitter, int entity);
DLL_pxParticleEmitAddParticle pxParticleEmitAddParticle;
pxParticleEmitAddParticle = (DLL_pxParticleEmitAddParticle)GetProcAddress(hDLL,"_pxParticleEmitAddParticle@8");
// Use ==> int result = pxParticleEmitAddParticle(int pEmitter, int entity);
 
typedef void(WINAPI *DLL_pxParticleEmitDelete)(int pEmitter);
DLL_pxParticleEmitDelete pxParticleEmitDelete;
pxParticleEmitDelete = (DLL_pxParticleEmitDelete)GetProcAddress(hDLL,"_pxParticleEmitDelete@4");
// Use ==> pxParticleEmitDelete(int pEmitter);
 
typedef void(WINAPI *DLL_pxParticleUpdateEmitter)(int pEmitter);
DLL_pxParticleUpdateEmitter pxParticleUpdateEmitter;
pxParticleUpdateEmitter = (DLL_pxParticleUpdateEmitter)GetProcAddress(hDLL,"_pxParticleUpdateEmitter@4");
// Use ==> pxParticleUpdateEmitter(int pEmitter);
 
typedef int(WINAPI *DLL_pxParticleGetEntity)(int particle);
DLL_pxParticleGetEntity pxParticleGetEntity;
pxParticleGetEntity = (DLL_pxParticleGetEntity)GetProcAddress(hDLL,"_pxParticleGetEntity@4");
// Use ==> int result = pxParticleGetEntity(int particle);
 
typedef int(WINAPI *DLL_pxParticleGetBody)(int particle);
DLL_pxParticleGetBody pxParticleGetBody;
pxParticleGetBody = (DLL_pxParticleGetBody)GetProcAddress(hDLL,"_pxParticleGetBody@4");
// Use ==> int result = pxParticleGetBody(int particle);
 
typedef float(WINAPI *DLL_pxParticleGetradius)(int particle);
DLL_pxParticleGetradius pxParticleGetradius;
pxParticleGetradius = (DLL_pxParticleGetradius)GetProcAddress(hDLL,"_pxParticleGetradius@4");
// Use ==> float result = pxParticleGetradius(int particle);
 
typedef void(WINAPI *DLL_pxKinematicSet)(int body);
DLL_pxKinematicSet pxKinematicSet;
pxKinematicSet = (DLL_pxKinematicSet)GetProcAddress(hDLL,"_pxKinematicSet@4");
// Use ==> pxKinematicSet(int body);
 
typedef void(WINAPI *DLL_pxKinematicClear)(int body);
DLL_pxKinematicClear pxKinematicClear;
pxKinematicClear = (DLL_pxKinematicClear)GetProcAddress(hDLL,"_pxKinematicClear@4");
// Use ==> pxKinematicClear(int body);
 
typedef void(WINAPI *DLL_pxKinematicMove)(int body, float x, float y, float z);
DLL_pxKinematicMove pxKinematicMove;
pxKinematicMove = (DLL_pxKinematicMove)GetProcAddress(hDLL,"_pxKinematicMove@16");
// Use ==> pxKinematicMove(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxKinematicSetPosition)(int body, float x, float y, float z);
DLL_pxKinematicSetPosition pxKinematicSetPosition;
pxKinematicSetPosition = (DLL_pxKinematicSetPosition)GetProcAddress(hDLL,"_pxKinematicSetPosition@16");
// Use ==> pxKinematicSetPosition(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxKinematicSetRotation)(int body, float pitch, float yaw, float roll);
DLL_pxKinematicSetRotation pxKinematicSetRotation;
pxKinematicSetRotation = (DLL_pxKinematicSetRotation)GetProcAddress(hDLL,"_pxKinematicSetRotation@16");
// Use ==> pxKinematicSetRotation(int body, float pitch, float yaw, float roll);
 
typedef int(WINAPI *DLL_pxCreateMagnet)(float minforce, float middleforce, float maxforce);
DLL_pxCreateMagnet pxCreateMagnet;
pxCreateMagnet = (DLL_pxCreateMagnet)GetProcAddress(hDLL,"_pxCreateMagnet@12");
// Use ==> int result = pxCreateMagnet(float minforce, float middleforce, float maxforce);
 
typedef void(WINAPI *DLL_pxMagnetActivate)(int mdata, int mmode, int fmode);
DLL_pxMagnetActivate pxMagnetActivate;
pxMagnetActivate = (DLL_pxMagnetActivate)GetProcAddress(hDLL,"_pxMagnetActivate@12");
// Use ==> pxMagnetActivate(int mdata, int mmode, int fmode);
 
typedef void(WINAPI *DLL_pxMagnetSetPosition)(int mdata, float pos_x, float pos_y, float pos_z);
DLL_pxMagnetSetPosition pxMagnetSetPosition;
pxMagnetSetPosition = (DLL_pxMagnetSetPosition)GetProcAddress(hDLL,"_pxMagnetSetPosition@16");
// Use ==> pxMagnetSetPosition(int mdata, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxMagnetSetMaxRadius)(int mdata, float radius);
DLL_pxMagnetSetMaxRadius pxMagnetSetMaxRadius;
pxMagnetSetMaxRadius = (DLL_pxMagnetSetMaxRadius)GetProcAddress(hDLL,"_pxMagnetSetMaxRadius@8");
// Use ==> pxMagnetSetMaxRadius(int mdata, float radius);
 
typedef void(WINAPI *DLL_pxMagnetSetMinRadius)(int mdata, float radius);
DLL_pxMagnetSetMinRadius pxMagnetSetMinRadius;
pxMagnetSetMinRadius = (DLL_pxMagnetSetMinRadius)GetProcAddress(hDLL,"_pxMagnetSetMinRadius@8");
// Use ==> pxMagnetSetMinRadius(int mdata, float radius);
 
typedef void(WINAPI *DLL_pxMagnetSetMaxForce)(int mdata, float force);
DLL_pxMagnetSetMaxForce pxMagnetSetMaxForce;
pxMagnetSetMaxForce = (DLL_pxMagnetSetMaxForce)GetProcAddress(hDLL,"_pxMagnetSetMaxForce@8");
// Use ==> pxMagnetSetMaxForce(int mdata, float force);
 
typedef void(WINAPI *DLL_pxMagnetSetMidForce)(int mdata, float force);
DLL_pxMagnetSetMidForce pxMagnetSetMidForce;
pxMagnetSetMidForce = (DLL_pxMagnetSetMidForce)GetProcAddress(hDLL,"_pxMagnetSetMidForce@8");
// Use ==> pxMagnetSetMidForce(int mdata, float force);
 
typedef void(WINAPI *DLL_pxMagnetSetMinForce)(int mdata, float force);
DLL_pxMagnetSetMinForce pxMagnetSetMinForce;
pxMagnetSetMinForce = (DLL_pxMagnetSetMinForce)GetProcAddress(hDLL,"_pxMagnetSetMinForce@8");
// Use ==> pxMagnetSetMinForce(int mdata, float force);
 
typedef void(WINAPI *DLL_pxMagnetSetMask)(int mdata, int mask);
DLL_pxMagnetSetMask pxMagnetSetMask;
pxMagnetSetMask = (DLL_pxMagnetSetMask)GetProcAddress(hDLL,"_pxMagnetSetMask@8");
// Use ==> pxMagnetSetMask(int mdata, int mask);
 
typedef float(WINAPI *DLL_pxMagnetGetPositionX)();
DLL_pxMagnetGetPositionX pxMagnetGetPositionX;
pxMagnetGetPositionX = (DLL_pxMagnetGetPositionX)GetProcAddress(hDLL,"_pxMagnetGetPositionX@4");
// Use ==> float result = pxMagnetGetPositionX();
 
typedef float(WINAPI *DLL_pxMagnetGetPositionY)();
DLL_pxMagnetGetPositionY pxMagnetGetPositionY;
pxMagnetGetPositionY = (DLL_pxMagnetGetPositionY)GetProcAddress(hDLL,"_pxMagnetGetPositionY@4");
// Use ==> float result = pxMagnetGetPositionY();
 
typedef float(WINAPI *DLL_pxMagnetGetPositionZ)();
DLL_pxMagnetGetPositionZ pxMagnetGetPositionZ;
pxMagnetGetPositionZ = (DLL_pxMagnetGetPositionZ)GetProcAddress(hDLL,"_pxMagnetGetPositionZ@4");
// Use ==> float result = pxMagnetGetPositionZ();
 
typedef float(WINAPI *DLL_pxMagnetGetMaxRadius)(int mdata);
DLL_pxMagnetGetMaxRadius pxMagnetGetMaxRadius;
pxMagnetGetMaxRadius = (DLL_pxMagnetGetMaxRadius)GetProcAddress(hDLL,"_pxMagnetGetMaxRadius@4");
// Use ==> float result = pxMagnetGetMaxRadius(int mdata);
 
typedef float(WINAPI *DLL_pxMagnetGetMinRadius)(int mdata);
DLL_pxMagnetGetMinRadius pxMagnetGetMinRadius;
pxMagnetGetMinRadius = (DLL_pxMagnetGetMinRadius)GetProcAddress(hDLL,"_pxMagnetGetMinRadius@4");
// Use ==> float result = pxMagnetGetMinRadius(int mdata);
 
typedef float(WINAPI *DLL_pxMagnetGetMaxForce)(int mdata);
DLL_pxMagnetGetMaxForce pxMagnetGetMaxForce;
pxMagnetGetMaxForce = (DLL_pxMagnetGetMaxForce)GetProcAddress(hDLL,"_pxMagnetGetMaxForce@4");
// Use ==> float result = pxMagnetGetMaxForce(int mdata);
 
typedef float(WINAPI *DLL_pxMagnetGetMidForce)(int mdata);
DLL_pxMagnetGetMidForce pxMagnetGetMidForce;
pxMagnetGetMidForce = (DLL_pxMagnetGetMidForce)GetProcAddress(hDLL,"_pxMagnetGetMidForce@4");
// Use ==> float result = pxMagnetGetMidForce(int mdata);
 
typedef float(WINAPI *DLL_pxMagnetGetMinForce)(int mdata);
DLL_pxMagnetGetMinForce pxMagnetGetMinForce;
pxMagnetGetMinForce = (DLL_pxMagnetGetMinForce)GetProcAddress(hDLL,"_pxMagnetGetMinForce@4");
// Use ==> float result = pxMagnetGetMinForce(int mdata);
 
typedef int(WINAPI *DLL_pxMagnetGetMask)(int mdata);
DLL_pxMagnetGetMask pxMagnetGetMask;
pxMagnetGetMask = (DLL_pxMagnetGetMask)GetProcAddress(hDLL,"_pxMagnetGetMask@4");
// Use ==> int result = pxMagnetGetMask(int mdata);
 
typedef void(WINAPI *DLL_pxMagnetDelete)(int mdata);
DLL_pxMagnetDelete pxMagnetDelete;
pxMagnetDelete = (DLL_pxMagnetDelete)GetProcAddress(hDLL,"_pxMagnetDelete@4");
// Use ==> pxMagnetDelete(int mdata);
 
typedef int(WINAPI *DLL_pxCreateWaterCirPlane)(float radius, float depth);
DLL_pxCreateWaterCirPlane pxCreateWaterCirPlane;
pxCreateWaterCirPlane = (DLL_pxCreateWaterCirPlane)GetProcAddress(hDLL,"_pxCreateWaterCirPlane@8");
// Use ==> int result = pxCreateWaterCirPlane(float radius, float depth);
 
typedef int(WINAPI *DLL_pxCreateWaterRectPlane)(float width, float height, float depth);
DLL_pxCreateWaterRectPlane pxCreateWaterRectPlane;
pxCreateWaterRectPlane = (DLL_pxCreateWaterRectPlane)GetProcAddress(hDLL,"_pxCreateWaterRectPlane@12");
// Use ==> int result = pxCreateWaterRectPlane(float width, float height, float depth);
 
typedef int(WINAPI *DLL_pxCreateWaterInfinPlane)(float depth);
DLL_pxCreateWaterInfinPlane pxCreateWaterInfinPlane;
pxCreateWaterInfinPlane = (DLL_pxCreateWaterInfinPlane)GetProcAddress(hDLL,"_pxCreateWaterInfinPlane@4");
// Use ==> int result = pxCreateWaterInfinPlane(float depth);
 
typedef void(WINAPI *DLL_pxWaterSetDimension)(int water, float width, float height);
DLL_pxWaterSetDimension pxWaterSetDimension;
pxWaterSetDimension = (DLL_pxWaterSetDimension)GetProcAddress(hDLL,"_pxWaterSetDimension@12");
// Use ==> pxWaterSetDimension(int water, float width, float height);
 
typedef void(WINAPI *DLL_pxWaterSetRadius)(int water, float radius);
DLL_pxWaterSetRadius pxWaterSetRadius;
pxWaterSetRadius = (DLL_pxWaterSetRadius)GetProcAddress(hDLL,"_pxWaterSetRadius@8");
// Use ==> pxWaterSetRadius(int water, float radius);
 
typedef void(WINAPI *DLL_pxWaterSetPosition)(int water, float pos_x, float pos_y, float pos_z);
DLL_pxWaterSetPosition pxWaterSetPosition;
pxWaterSetPosition = (DLL_pxWaterSetPosition)GetProcAddress(hDLL,"_pxWaterSetPosition@16");
// Use ==> pxWaterSetPosition(int water, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxWaterSetRotation)(int water, float angle);
DLL_pxWaterSetRotation pxWaterSetRotation;
pxWaterSetRotation = (DLL_pxWaterSetRotation)GetProcAddress(hDLL,"_pxWaterSetRotation@8");
// Use ==> pxWaterSetRotation(int water, float angle);
 
typedef void(WINAPI *DLL_pxWaterSetFluxion)(int water, float fl_x, float fl_y, float fl_z);
DLL_pxWaterSetFluxion pxWaterSetFluxion;
pxWaterSetFluxion = (DLL_pxWaterSetFluxion)GetProcAddress(hDLL,"_pxWaterSetFluxion@16");
// Use ==> pxWaterSetFluxion(int water, float fl_x, float fl_y, float fl_z);
 
typedef float(WINAPI *DLL_pxWaterGetWidth)(int water);
DLL_pxWaterGetWidth pxWaterGetWidth;
pxWaterGetWidth = (DLL_pxWaterGetWidth)GetProcAddress(hDLL,"_pxWaterGetWidth@4");
// Use ==> float result = pxWaterGetWidth(int water);
 
typedef float(WINAPI *DLL_pxWaterGetHeight)(int water);
DLL_pxWaterGetHeight pxWaterGetHeight;
pxWaterGetHeight = (DLL_pxWaterGetHeight)GetProcAddress(hDLL,"_pxWaterGetHeight@4");
// Use ==> float result = pxWaterGetHeight(int water);
 
typedef float(WINAPI *DLL_pxWaterGetRadius)(int water);
DLL_pxWaterGetRadius pxWaterGetRadius;
pxWaterGetRadius = (DLL_pxWaterGetRadius)GetProcAddress(hDLL,"_pxWaterGetRadius@4");
// Use ==> float result = pxWaterGetRadius(int water);
 
typedef float(WINAPI *DLL_pxWaterGetPositionX)(int water);
DLL_pxWaterGetPositionX pxWaterGetPositionX;
pxWaterGetPositionX = (DLL_pxWaterGetPositionX)GetProcAddress(hDLL,"_pxWaterGetPositionX@4");
// Use ==> float result = pxWaterGetPositionX(int water);
 
typedef float(WINAPI *DLL_pxWaterGetPositionY)(int water);
DLL_pxWaterGetPositionY pxWaterGetPositionY;
pxWaterGetPositionY = (DLL_pxWaterGetPositionY)GetProcAddress(hDLL,"_pxWaterGetPositionY@4");
// Use ==> float result = pxWaterGetPositionY(int water);
 
typedef float(WINAPI *DLL_pxWaterGetPositionZ)(int water);
DLL_pxWaterGetPositionZ pxWaterGetPositionZ;
pxWaterGetPositionZ = (DLL_pxWaterGetPositionZ)GetProcAddress(hDLL,"_pxWaterGetPositionZ@4");
// Use ==> float result = pxWaterGetPositionZ(int water);
 
typedef float(WINAPI *DLL_pxWaterGetRotation)(int water);
DLL_pxWaterGetRotation pxWaterGetRotation;
pxWaterGetRotation = (DLL_pxWaterGetRotation)GetProcAddress(hDLL,"_pxWaterGetRotation@4");
// Use ==> float result = pxWaterGetRotation(int water);
 
typedef float(WINAPI *DLL_pxWaterGetDepth)(int water);
DLL_pxWaterGetDepth pxWaterGetDepth;
pxWaterGetDepth = (DLL_pxWaterGetDepth)GetProcAddress(hDLL,"_pxWaterGetDepth@4");
// Use ==> float result = pxWaterGetDepth(int water);
 
typedef void(WINAPI *DLL_pxWaterDelete)(int water);
DLL_pxWaterDelete pxWaterDelete;
pxWaterDelete = (DLL_pxWaterDelete)GetProcAddress(hDLL,"_pxWaterDelete@4");
// Use ==> pxWaterDelete(int water);
 
typedef int(WINAPI *DLL_pxCreateKep)(float buo, float radius, float maxdis);
DLL_pxCreateKep pxCreateKep;
pxCreateKep = (DLL_pxCreateKep)GetProcAddress(hDLL,"_pxCreateKep@12");
// Use ==> int result = pxCreateKep(float buo, float radius, float maxdis);
 
typedef void(WINAPI *DLL_pxKepAddToBody)(int kdata, int body);
DLL_pxKepAddToBody pxKepAddToBody;
pxKepAddToBody = (DLL_pxKepAddToBody)GetProcAddress(hDLL,"_pxKepAddToBody@8");
// Use ==> pxKepAddToBody(int kdata, int body);
 
typedef void(WINAPI *DLL_pxKepSetLocalPosition)(int kdata, float pos_x, float pos_y, float pos_z);
DLL_pxKepSetLocalPosition pxKepSetLocalPosition;
pxKepSetLocalPosition = (DLL_pxKepSetLocalPosition)GetProcAddress(hDLL,"_pxKepSetLocalPosition@16");
// Use ==> pxKepSetLocalPosition(int kdata, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxKepSetGlobalPosition)(int kdata, float pos_x, float pos_y, float pos_z);
DLL_pxKepSetGlobalPosition pxKepSetGlobalPosition;
pxKepSetGlobalPosition = (DLL_pxKepSetGlobalPosition)GetProcAddress(hDLL,"_pxKepSetGlobalPosition@16");
// Use ==> pxKepSetGlobalPosition(int kdata, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxKepSetAngularDamping)(int kdata, float angdamp);
DLL_pxKepSetAngularDamping pxKepSetAngularDamping;
pxKepSetAngularDamping = (DLL_pxKepSetAngularDamping)GetProcAddress(hDLL,"_pxKepSetAngularDamping@8");
// Use ==> pxKepSetAngularDamping(int kdata, float angdamp);
 
typedef void(WINAPI *DLL_pxKepSetLinearDamping)(int kdata, float lindamp);
DLL_pxKepSetLinearDamping pxKepSetLinearDamping;
pxKepSetLinearDamping = (DLL_pxKepSetLinearDamping)GetProcAddress(hDLL,"_pxKepSetLinearDamping@8");
// Use ==> pxKepSetLinearDamping(int kdata, float lindamp);
 
typedef void(WINAPI *DLL_pxWaterUpdate)(int water);
DLL_pxWaterUpdate pxWaterUpdate;
pxWaterUpdate = (DLL_pxWaterUpdate)GetProcAddress(hDLL,"_pxWaterUpdate@4");
// Use ==> pxWaterUpdate(int water);
 
typedef float(WINAPI *DLL_pxKepGetPosX)(int kdata);
DLL_pxKepGetPosX pxKepGetPosX;
pxKepGetPosX = (DLL_pxKepGetPosX)GetProcAddress(hDLL,"_pxKepGetPosX@4");
// Use ==> float result = pxKepGetPosX(int kdata);
 
typedef float(WINAPI *DLL_pxKepGetPosY)(int kdata);
DLL_pxKepGetPosY pxKepGetPosY;
pxKepGetPosY = (DLL_pxKepGetPosY)GetProcAddress(hDLL,"_pxKepGetPosY@4");
// Use ==> float result = pxKepGetPosY(int kdata);
 
typedef float(WINAPI *DLL_pxKepGetPosZ)(int kdata);
DLL_pxKepGetPosZ pxKepGetPosZ;
pxKepGetPosZ = (DLL_pxKepGetPosZ)GetProcAddress(hDLL,"_pxKepGetPosZ@4");
// Use ==> float result = pxKepGetPosZ(int kdata);
 
typedef int(WINAPI *DLL_pxKepGetNumber)(int body);
DLL_pxKepGetNumber pxKepGetNumber;
pxKepGetNumber = (DLL_pxKepGetNumber)GetProcAddress(hDLL,"_pxKepGetNumber@4");
// Use ==> int result = pxKepGetNumber(int body);
 
typedef int(WINAPI *DLL_pxKepGetKepFromBody)(int body, int num);
DLL_pxKepGetKepFromBody pxKepGetKepFromBody;
pxKepGetKepFromBody = (DLL_pxKepGetKepFromBody)GetProcAddress(hDLL,"_pxKepGetKepFromBody@8");
// Use ==> int result = pxKepGetKepFromBody(int body, int num);
 
typedef int(WINAPI *DLL_pxTriggerCreateCube)(float dx, float dy, float dz);
DLL_pxTriggerCreateCube pxTriggerCreateCube;
pxTriggerCreateCube = (DLL_pxTriggerCreateCube)GetProcAddress(hDLL,"_pxTriggerCreateCube@12");
// Use ==> int result = pxTriggerCreateCube(float dx, float dy, float dz);
 
typedef int(WINAPI *DLL_pxTriggerCreateSphere)(float radius);
DLL_pxTriggerCreateSphere pxTriggerCreateSphere;
pxTriggerCreateSphere = (DLL_pxTriggerCreateSphere)GetProcAddress(hDLL,"_pxTriggerCreateSphere@4");
// Use ==> int result = pxTriggerCreateSphere(float radius);
 
typedef int(WINAPI *DLL_pxTriggerCreateCapsule)(float height, float radius);
DLL_pxTriggerCreateCapsule pxTriggerCreateCapsule;
pxTriggerCreateCapsule = (DLL_pxTriggerCreateCapsule)GetProcAddress(hDLL,"_pxTriggerCreateCapsule@8");
// Use ==> int result = pxTriggerCreateCapsule(float height, float radius);
 
typedef int(WINAPI *DLL_pxTriggerCreateCylinder)(float radius, float height, int nbEdge);
DLL_pxTriggerCreateCylinder pxTriggerCreateCylinder;
pxTriggerCreateCylinder = (DLL_pxTriggerCreateCylinder)GetProcAddress(hDLL,"_pxTriggerCreateCylinder@12");
// Use ==> int result = pxTriggerCreateCylinder(float radius, float height, int nbEdge);
 
typedef int(WINAPI *DLL_pxTriggerCreateHull)(int *vbank, int nvert);
DLL_pxTriggerCreateHull pxTriggerCreateHull;
pxTriggerCreateHull = (DLL_pxTriggerCreateHull)GetProcAddress(hDLL,"_pxTriggerCreateHull@8");
// Use ==> int result = pxTriggerCreateHull(int *vbank, int nvert);
 
typedef void(WINAPI *DLL_pxTriggerSetPosition)(int body, float x, float y, float z);
DLL_pxTriggerSetPosition pxTriggerSetPosition;
pxTriggerSetPosition = (DLL_pxTriggerSetPosition)GetProcAddress(hDLL,"_pxTriggerSetPosition@16");
// Use ==> pxTriggerSetPosition(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxTriggerSetRotation)(int body, float pitch, float yaw, float roll);
DLL_pxTriggerSetRotation pxTriggerSetRotation;
pxTriggerSetRotation = (DLL_pxTriggerSetRotation)GetProcAddress(hDLL,"_pxTriggerSetRotation@16");
// Use ==> pxTriggerSetRotation(int body, float pitch, float yaw, float roll);
 
typedef int(WINAPI *DLL_pxTriggerGetNumBody)(int trigger);
DLL_pxTriggerGetNumBody pxTriggerGetNumBody;
pxTriggerGetNumBody = (DLL_pxTriggerGetNumBody)GetProcAddress(hDLL,"_pxTriggerGetNumBody@4");
// Use ==> int result = pxTriggerGetNumBody(int trigger);
 
typedef int(WINAPI *DLL_pxTriggerGetBody)(int trigger, int num);
DLL_pxTriggerGetBody pxTriggerGetBody;
pxTriggerGetBody = (DLL_pxTriggerGetBody)GetProcAddress(hDLL,"_pxTriggerGetBody@8");
// Use ==> int result = pxTriggerGetBody(int trigger, int num);
 
typedef int(WINAPI *DLL_pxTriggerGetBodyTrigger)(int body);
DLL_pxTriggerGetBodyTrigger pxTriggerGetBodyTrigger;
pxTriggerGetBodyTrigger = (DLL_pxTriggerGetBodyTrigger)GetProcAddress(hDLL,"_pxTriggerGetBodyTrigger@4");
// Use ==> int result = pxTriggerGetBodyTrigger(int body);
 
typedef void(WINAPI *DLL_pxUpdateTriggers)();
DLL_pxUpdateTriggers pxUpdateTriggers;
pxUpdateTriggers = (DLL_pxUpdateTriggers)GetProcAddress(hDLL,"_pxUpdateTriggers@0");
// Use ==> pxUpdateTriggers();
 
typedef int(WINAPI *DLL_pxCreateCompoundDesc)();
DLL_pxCreateCompoundDesc pxCreateCompoundDesc;
pxCreateCompoundDesc = (DLL_pxCreateCompoundDesc)GetProcAddress(hDLL,"_pxCreateCompoundDesc@0");
// Use ==> int result = pxCreateCompoundDesc();
 
typedef int(WINAPI *DLL_pxCompoundAddCubeShape)(int compoundDesc, float dx, float dy, float dz);
DLL_pxCompoundAddCubeShape pxCompoundAddCubeShape;
pxCompoundAddCubeShape = (DLL_pxCompoundAddCubeShape)GetProcAddress(hDLL,"_pxCompoundAddCubeShape@16");
// Use ==> int result = pxCompoundAddCubeShape(int compoundDesc, float dx, float dy, float dz);
 
typedef int(WINAPI *DLL_pxCompoundAddSphereShape)(int compoundDesc, float radius);
DLL_pxCompoundAddSphereShape pxCompoundAddSphereShape;
pxCompoundAddSphereShape = (DLL_pxCompoundAddSphereShape)GetProcAddress(hDLL,"_pxCompoundAddSphereShape@8");
// Use ==> int result = pxCompoundAddSphereShape(int compoundDesc, float radius);
 
typedef int(WINAPI *DLL_pxCompoundAddCapsuleShape)(int compoundDesc, float radius, float height);
DLL_pxCompoundAddCapsuleShape pxCompoundAddCapsuleShape;
pxCompoundAddCapsuleShape = (DLL_pxCompoundAddCapsuleShape)GetProcAddress(hDLL,"_pxCompoundAddCapsuleShape@12");
// Use ==> int result = pxCompoundAddCapsuleShape(int compoundDesc, float radius, float height);
 
typedef int(WINAPI *DLL_pxCompoundAddCylinderShape)(int compoundDesc, float radius, float height, int nbEdge);
DLL_pxCompoundAddCylinderShape pxCompoundAddCylinderShape;
pxCompoundAddCylinderShape = (DLL_pxCompoundAddCylinderShape)GetProcAddress(hDLL,"_pxCompoundAddCylinderShape@16");
// Use ==> int result = pxCompoundAddCylinderShape(int compoundDesc, float radius, float height, int nbEdge);
 
typedef int(WINAPI *DLL_pxCompoundAddHullShape)(int compoundDesc, int *vbank, int nvert);
DLL_pxCompoundAddHullShape pxCompoundAddHullShape;
pxCompoundAddHullShape = (DLL_pxCompoundAddHullShape)GetProcAddress(hDLL,"_pxCompoundAddHullShape@12");
// Use ==> int result = pxCompoundAddHullShape(int compoundDesc, int *vbank, int nvert);
 
typedef void(WINAPI *DLL_pxCompoundSetShapePos)(int shape, float x, float y, float z);
DLL_pxCompoundSetShapePos pxCompoundSetShapePos;
pxCompoundSetShapePos = (DLL_pxCompoundSetShapePos)GetProcAddress(hDLL,"_pxCompoundSetShapePos@16");
// Use ==> pxCompoundSetShapePos(int shape, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxCompoundSetShapeRot)(int shape, float pitch, float yaw, float roll);
DLL_pxCompoundSetShapeRot pxCompoundSetShapeRot;
pxCompoundSetShapeRot = (DLL_pxCompoundSetShapeRot)GetProcAddress(hDLL,"_pxCompoundSetShapeRot@16");
// Use ==> pxCompoundSetShapeRot(int shape, float pitch, float yaw, float roll);
 
typedef int(WINAPI *DLL_pxCreateCompound)(int compoundDesc, float mass);
DLL_pxCreateCompound pxCreateCompound;
pxCreateCompound = (DLL_pxCreateCompound)GetProcAddress(hDLL,"_pxCreateCompound@8");
// Use ==> int result = pxCreateCompound(int compoundDesc, float mass);
 
typedef int(WINAPI *DLL_pxCreateStaticCompound)(int compoundDesc, float mass);
DLL_pxCreateStaticCompound pxCreateStaticCompound;
pxCreateStaticCompound = (DLL_pxCreateStaticCompound)GetProcAddress(hDLL,"_pxCreateStaticCompound@8");
// Use ==> int result = pxCreateStaticCompound(int compoundDesc, float mass);
 
typedef int(WINAPI *DLL_pxCreateCloth)(int entity, int surf);
DLL_pxCreateCloth pxCreateCloth;
pxCreateCloth = (DLL_pxCreateCloth)GetProcAddress(hDLL,"_pxCreateCloth@8");
// Use ==> int result = pxCreateCloth(int entity, int surf);
 
typedef int(WINAPI *DLL_pxCreateTearableCloth)(int entity, int surf);
DLL_pxCreateTearableCloth pxCreateTearableCloth;
pxCreateTearableCloth = (DLL_pxCreateTearableCloth)GetProcAddress(hDLL,"_pxCreateTearableCloth@8");
// Use ==> int result = pxCreateTearableCloth(int entity, int surf);
 
typedef int(WINAPI *DLL_pxCreateMetalCloth)(int entity, int surf, int coreActor, float impThr, float depth);
DLL_pxCreateMetalCloth pxCreateMetalCloth;
pxCreateMetalCloth = (DLL_pxCreateMetalCloth)GetProcAddress(hDLL,"_pxCreateMetalCloth@20");
// Use ==> int result = pxCreateMetalCloth(int entity, int surf, int coreActor, float impThr, float depth);
 
typedef int(WINAPI *DLL_pxCreateClothSpec)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES);
DLL_pxCreateClothSpec pxCreateClothSpec;
pxCreateClothSpec = (DLL_pxCreateClothSpec)GetProcAddress(hDLL,"_pxCreateClothSpec@16");
// Use ==> int result = pxCreateClothSpec(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES);
 
typedef int(WINAPI *DLL_pxCreateMetalClothSpec)(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, int coreActor, float impThr, float depth);
DLL_pxCreateMetalClothSpec pxCreateMetalClothSpec;
pxCreateMetalClothSpec = (DLL_pxCreateMetalClothSpec)GetProcAddress(hDLL,"_pxCreateMetalClothSpec@28");
// Use ==> int result = pxCreateMetalClothSpec(int *vbank, int *fbank, int MESH_NBVERTICES, int MESH_NBFACES, int coreActor, float impThr, float depth);
 
typedef float(WINAPI *DLL_pxClothGetAttachmentResponseCoefficient)(int Cloth);
DLL_pxClothGetAttachmentResponseCoefficient pxClothGetAttachmentResponseCoefficient;
pxClothGetAttachmentResponseCoefficient = (DLL_pxClothGetAttachmentResponseCoefficient)GetProcAddress(hDLL,"_pxClothGetAttachmentResponseCoefficient@4");
// Use ==> float result = pxClothGetAttachmentResponseCoefficient(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetAttachmentTearFactor)(int Cloth);
DLL_pxClothGetAttachmentTearFactor pxClothGetAttachmentTearFactor;
pxClothGetAttachmentTearFactor = (DLL_pxClothGetAttachmentTearFactor)GetProcAddress(hDLL,"_pxClothGetAttachmentTearFactor@4");
// Use ==> float result = pxClothGetAttachmentTearFactor(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetBendingStiffness)(int Cloth);
DLL_pxClothGetBendingStiffness pxClothGetBendingStiffness;
pxClothGetBendingStiffness = (DLL_pxClothGetBendingStiffness)GetProcAddress(hDLL,"_pxClothGetBendingStiffness@4");
// Use ==> float result = pxClothGetBendingStiffness(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetCollisionResponseCoefficient)(int Cloth);
DLL_pxClothGetCollisionResponseCoefficient pxClothGetCollisionResponseCoefficient;
pxClothGetCollisionResponseCoefficient = (DLL_pxClothGetCollisionResponseCoefficient)GetProcAddress(hDLL,"_pxClothGetCollisionResponseCoefficient@4");
// Use ==> float result = pxClothGetCollisionResponseCoefficient(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetDampingCoefficient)(int Cloth);
DLL_pxClothGetDampingCoefficient pxClothGetDampingCoefficient;
pxClothGetDampingCoefficient = (DLL_pxClothGetDampingCoefficient)GetProcAddress(hDLL,"_pxClothGetDampingCoefficient@4");
// Use ==> float result = pxClothGetDampingCoefficient(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetDensity)(int Cloth);
DLL_pxClothGetDensity pxClothGetDensity;
pxClothGetDensity = (DLL_pxClothGetDensity)GetProcAddress(hDLL,"_pxClothGetDensity@4");
// Use ==> float result = pxClothGetDensity(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetFriction)(int Cloth);
DLL_pxClothGetFriction pxClothGetFriction;
pxClothGetFriction = (DLL_pxClothGetFriction)GetProcAddress(hDLL,"_pxClothGetFriction@4");
// Use ==> float result = pxClothGetFriction(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetPressure)(int Cloth);
DLL_pxClothGetPressure pxClothGetPressure;
pxClothGetPressure = (DLL_pxClothGetPressure)GetProcAddress(hDLL,"_pxClothGetPressure@4");
// Use ==> float result = pxClothGetPressure(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetSleepLinearVelocity)(int Cloth);
DLL_pxClothGetSleepLinearVelocity pxClothGetSleepLinearVelocity;
pxClothGetSleepLinearVelocity = (DLL_pxClothGetSleepLinearVelocity)GetProcAddress(hDLL,"_pxClothGetSleepLinearVelocity@4");
// Use ==> float result = pxClothGetSleepLinearVelocity(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetStretchingStiffness)(int Cloth);
DLL_pxClothGetStretchingStiffness pxClothGetStretchingStiffness;
pxClothGetStretchingStiffness = (DLL_pxClothGetStretchingStiffness)GetProcAddress(hDLL,"_pxClothGetStretchingStiffness@4");
// Use ==> float result = pxClothGetStretchingStiffness(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetTearFactor)(int Cloth);
DLL_pxClothGetTearFactor pxClothGetTearFactor;
pxClothGetTearFactor = (DLL_pxClothGetTearFactor)GetProcAddress(hDLL,"_pxClothGetTearFactor@4");
// Use ==> float result = pxClothGetTearFactor(int Cloth);
 
typedef float(WINAPI *DLL_pxClothGetThickness)(int Cloth);
DLL_pxClothGetThickness pxClothGetThickness;
pxClothGetThickness = (DLL_pxClothGetThickness)GetProcAddress(hDLL,"_pxClothGetThickness@4");
// Use ==> float result = pxClothGetThickness(int Cloth);
 
typedef int(WINAPI *DLL_pxClothGetNumVertices)(int Cloth);
DLL_pxClothGetNumVertices pxClothGetNumVertices;
pxClothGetNumVertices = (DLL_pxClothGetNumVertices)GetProcAddress(hDLL,"_pxClothGetNumVertices@4");
// Use ==> int result = pxClothGetNumVertices(int Cloth);
 
typedef int(WINAPI *DLL_pxClothIsSleeping)(int Cloth);
DLL_pxClothIsSleeping pxClothIsSleeping;
pxClothIsSleeping = (DLL_pxClothIsSleeping)GetProcAddress(hDLL,"_pxClothIsSleeping@4");
// Use ==> int result = pxClothIsSleeping(int Cloth);
 
typedef int(WINAPI *DLL_pxClothGetEntity)(int Cloth);
DLL_pxClothGetEntity pxClothGetEntity;
pxClothGetEntity = (DLL_pxClothGetEntity)GetProcAddress(hDLL,"_pxClothGetEntity@4");
// Use ==> int result = pxClothGetEntity(int Cloth);
 
typedef int(WINAPI *DLL_pxClothGetUserData)(int Cloth);
DLL_pxClothGetUserData pxClothGetUserData;
pxClothGetUserData = (DLL_pxClothGetUserData)GetProcAddress(hDLL,"_pxClothGetUserData@4");
// Use ==> int result = pxClothGetUserData(int Cloth);
 
typedef void(WINAPI *DLL_pxClothPutToSleep)(int Cloth);
DLL_pxClothPutToSleep pxClothPutToSleep;
pxClothPutToSleep = (DLL_pxClothPutToSleep)GetProcAddress(hDLL,"_pxClothPutToSleep@4");
// Use ==> pxClothPutToSleep(int Cloth);
 
typedef void(WINAPI *DLL_pxClothWakeUp)(int Cloth);
DLL_pxClothWakeUp pxClothWakeUp;
pxClothWakeUp = (DLL_pxClothWakeUp)GetProcAddress(hDLL,"_pxClothWakeUp@4");
// Use ==> pxClothWakeUp(int Cloth);
 
typedef void(WINAPI *DLL_pxClothSetTeareble)(int Cloth);
DLL_pxClothSetTeareble pxClothSetTeareble;
pxClothSetTeareble = (DLL_pxClothSetTeareble)GetProcAddress(hDLL,"_pxClothSetTeareble@4");
// Use ==> pxClothSetTeareble(int Cloth);
 
typedef void(WINAPI *DLL_pxClothSetTearFactor)(int Cloth, float coef);
DLL_pxClothSetTearFactor pxClothSetTearFactor;
pxClothSetTearFactor = (DLL_pxClothSetTearFactor)GetProcAddress(hDLL,"_pxClothSetTearFactor@8");
// Use ==> pxClothSetTearFactor(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetAttachmentResponseCoefficient)(int Cloth, float coef);
DLL_pxClothSetAttachmentResponseCoefficient pxClothSetAttachmentResponseCoefficient;
pxClothSetAttachmentResponseCoefficient = (DLL_pxClothSetAttachmentResponseCoefficient)GetProcAddress(hDLL,"_pxClothSetAttachmentResponseCoefficient@8");
// Use ==> float result = pxClothSetAttachmentResponseCoefficient(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetAttachmentTearFactor)(int Cloth, float coef);
DLL_pxClothSetAttachmentTearFactor pxClothSetAttachmentTearFactor;
pxClothSetAttachmentTearFactor = (DLL_pxClothSetAttachmentTearFactor)GetProcAddress(hDLL,"_pxClothSetAttachmentTearFactor@8");
// Use ==> float result = pxClothSetAttachmentTearFactor(int Cloth, float coef);
 
typedef void(WINAPI *DLL_pxClothSetBending)(int Cloth);
DLL_pxClothSetBending pxClothSetBending;
pxClothSetBending = (DLL_pxClothSetBending)GetProcAddress(hDLL,"_pxClothSetBending@4");
// Use ==> pxClothSetBending(int Cloth);
 
typedef float(WINAPI *DLL_pxClothSetBendingStiffness)(int Cloth, float coef);
DLL_pxClothSetBendingStiffness pxClothSetBendingStiffness;
pxClothSetBendingStiffness = (DLL_pxClothSetBendingStiffness)GetProcAddress(hDLL,"_pxClothSetBendingStiffness@8");
// Use ==> float result = pxClothSetBendingStiffness(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetCollisionResponseCoefficient)(int Cloth, float coef);
DLL_pxClothSetCollisionResponseCoefficient pxClothSetCollisionResponseCoefficient;
pxClothSetCollisionResponseCoefficient = (DLL_pxClothSetCollisionResponseCoefficient)GetProcAddress(hDLL,"_pxClothSetCollisionResponseCoefficient@8");
// Use ==> float result = pxClothSetCollisionResponseCoefficient(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetDampingCoefficient)(int Cloth, float coef);
DLL_pxClothSetDampingCoefficient pxClothSetDampingCoefficient;
pxClothSetDampingCoefficient = (DLL_pxClothSetDampingCoefficient)GetProcAddress(hDLL,"_pxClothSetDampingCoefficient@8");
// Use ==> float result = pxClothSetDampingCoefficient(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetComDampingCoefficient)(int Cloth, float coef);
DLL_pxClothSetComDampingCoefficient pxClothSetComDampingCoefficient;
pxClothSetComDampingCoefficient = (DLL_pxClothSetComDampingCoefficient)GetProcAddress(hDLL,"_pxClothSetComDampingCoefficient@8");
// Use ==> float result = pxClothSetComDampingCoefficient(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetFriction)(int Cloth, float coef);
DLL_pxClothSetFriction pxClothSetFriction;
pxClothSetFriction = (DLL_pxClothSetFriction)GetProcAddress(hDLL,"_pxClothSetFriction@8");
// Use ==> float result = pxClothSetFriction(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetPressure)(int Cloth, float coef);
DLL_pxClothSetPressure pxClothSetPressure;
pxClothSetPressure = (DLL_pxClothSetPressure)GetProcAddress(hDLL,"_pxClothSetPressure@8");
// Use ==> float result = pxClothSetPressure(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetSleepLinearVelocity)(int Cloth, float coef);
DLL_pxClothSetSleepLinearVelocity pxClothSetSleepLinearVelocity;
pxClothSetSleepLinearVelocity = (DLL_pxClothSetSleepLinearVelocity)GetProcAddress(hDLL,"_pxClothSetSleepLinearVelocity@8");
// Use ==> float result = pxClothSetSleepLinearVelocity(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetStretchingStiffness)(int Cloth, float coef);
DLL_pxClothSetStretchingStiffness pxClothSetStretchingStiffness;
pxClothSetStretchingStiffness = (DLL_pxClothSetStretchingStiffness)GetProcAddress(hDLL,"_pxClothSetStretchingStiffness@8");
// Use ==> float result = pxClothSetStretchingStiffness(int Cloth, float coef);
 
typedef float(WINAPI *DLL_pxClothSetThickness)(int Cloth, float coef);
DLL_pxClothSetThickness pxClothSetThickness;
pxClothSetThickness = (DLL_pxClothSetThickness)GetProcAddress(hDLL,"_pxClothSetThickness@8");
// Use ==> float result = pxClothSetThickness(int Cloth, float coef);
 
typedef void(WINAPI *DLL_pxClothSetUserData)(int Cloth, int userdata);
DLL_pxClothSetUserData pxClothSetUserData;
pxClothSetUserData = (DLL_pxClothSetUserData)GetProcAddress(hDLL,"_pxClothSetUserData@8");
// Use ==> pxClothSetUserData(int Cloth, int userdata);
 
typedef void(WINAPI *DLL_pxClothMaskSet)(int Cloth, int mask);
DLL_pxClothMaskSet pxClothMaskSet;
pxClothMaskSet = (DLL_pxClothMaskSet)GetProcAddress(hDLL,"_pxClothMaskSet@8");
// Use ==> pxClothMaskSet(int Cloth, int mask);
 
typedef void(WINAPI *DLL_pxClothMaskCombineSet)(int Cloth, int mask);
DLL_pxClothMaskCombineSet pxClothMaskCombineSet;
pxClothMaskCombineSet = (DLL_pxClothMaskCombineSet)GetProcAddress(hDLL,"_pxClothMaskCombineSet@8");
// Use ==> pxClothMaskCombineSet(int Cloth, int mask);
 
typedef void(WINAPI *DLL_pxClothSetVertexPos)(int Cloth, int *vbank, int numVert);
DLL_pxClothSetVertexPos pxClothSetVertexPos;
pxClothSetVertexPos = (DLL_pxClothSetVertexPos)GetProcAddress(hDLL,"_pxClothSetVertexPos@12");
// Use ==> pxClothSetVertexPos(int Cloth, int *vbank, int numVert);
 
typedef void(WINAPI *DLL_pxClothGetVertexPos)(int Cloth, int *vbank, int numVert);
DLL_pxClothGetVertexPos pxClothGetVertexPos;
pxClothGetVertexPos = (DLL_pxClothGetVertexPos)GetProcAddress(hDLL,"_pxClothGetVertexPos@12");
// Use ==> pxClothGetVertexPos(int Cloth, int *vbank, int numVert);
 
typedef void(WINAPI *DLL_pxClothGetNormals)(int Cloth, int *nbank);
DLL_pxClothGetNormals pxClothGetNormals;
pxClothGetNormals = (DLL_pxClothGetNormals)GetProcAddress(hDLL,"_pxClothGetNormals@8");
// Use ==> pxClothGetNormals(int Cloth, int *nbank);
 
typedef void(WINAPI *DLL_pxClothAttachVertexToPos)(int Cloth, int vID, float pos_x, float pos_y, float pos_z);
DLL_pxClothAttachVertexToPos pxClothAttachVertexToPos;
pxClothAttachVertexToPos = (DLL_pxClothAttachVertexToPos)GetProcAddress(hDLL,"_pxClothAttachVertexToPos@20");
// Use ==> pxClothAttachVertexToPos(int Cloth, int vID, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxClothAttachToCollidingShapes)(int Cloth);
DLL_pxClothAttachToCollidingShapes pxClothAttachToCollidingShapes;
pxClothAttachToCollidingShapes = (DLL_pxClothAttachToCollidingShapes)GetProcAddress(hDLL,"_pxClothAttachToCollidingShapes@4");
// Use ==> pxClothAttachToCollidingShapes(int Cloth);
 
typedef void(WINAPI *DLL_pxClothAddForceAtPos)(int Cloth, float pos_x, float pos_y, float pos_z, float magnitude, float radius);
DLL_pxClothAddForceAtPos pxClothAddForceAtPos;
pxClothAddForceAtPos = (DLL_pxClothAddForceAtPos)GetProcAddress(hDLL,"_pxClothAddForceAtPos@24");
// Use ==> pxClothAddForceAtPos(int Cloth, float pos_x, float pos_y, float pos_z, float magnitude, float radius);
 
typedef void(WINAPI *DLL_pxClothAddForceAtVertex)(int Cloth, float nx, float ny, float nz, int vID);
DLL_pxClothAddForceAtVertex pxClothAddForceAtVertex;
pxClothAddForceAtVertex = (DLL_pxClothAddForceAtVertex)GetProcAddress(hDLL,"_pxClothAddForceAtVertex@20");
// Use ==> pxClothAddForceAtVertex(int Cloth, float nx, float ny, float nz, int vID);
 
typedef void(WINAPI *DLL_pxUpdateCloth)();
DLL_pxUpdateCloth pxUpdateCloth;
pxUpdateCloth = (DLL_pxUpdateCloth)GetProcAddress(hDLL,"_pxUpdateCloth@0");
// Use ==> pxUpdateCloth();
 
typedef void(WINAPI *DLL_pxClothDelete)(int Cloth);
DLL_pxClothDelete pxClothDelete;
pxClothDelete = (DLL_pxClothDelete)GetProcAddress(hDLL,"_pxClothDelete@4");
// Use ==> pxClothDelete(int Cloth);
 
typedef int(WINAPI *DLL_pxCreateSpringAndDamperEffector)(int body1, int body2);
DLL_pxCreateSpringAndDamperEffector pxCreateSpringAndDamperEffector;
pxCreateSpringAndDamperEffector = (DLL_pxCreateSpringAndDamperEffector)GetProcAddress(hDLL,"_pxCreateSpringAndDamperEffector@8");
// Use ==> int result = pxCreateSpringAndDamperEffector(int body1, int body2);
 
typedef void(WINAPI *DLL_pxSetLinearSpring)(int spring, float SpringRelaxed, float CompressForce, float CompressSaturate, float StretchForce, float StretchSaturate);
DLL_pxSetLinearSpring pxSetLinearSpring;
pxSetLinearSpring = (DLL_pxSetLinearSpring)GetProcAddress(hDLL,"_pxSetLinearSpring@24");
// Use ==> pxSetLinearSpring(int spring, float SpringRelaxed, float CompressForce, float CompressSaturate, float StretchForce, float StretchSaturate);
 
typedef void(WINAPI *DLL_pxSetLinearDamper)(int spring, float CompressForce, float CompressSaturate, float StretchForce, float StretchSaturate);
DLL_pxSetLinearDamper pxSetLinearDamper;
pxSetLinearDamper = (DLL_pxSetLinearDamper)GetProcAddress(hDLL,"_pxSetLinearDamper@20");
// Use ==> pxSetLinearDamper(int spring, float CompressForce, float CompressSaturate, float StretchForce, float StretchSaturate);
 
typedef void(WINAPI *DLL_pxDeleteEffector)(int effector);
DLL_pxDeleteEffector pxDeleteEffector;
pxDeleteEffector = (DLL_pxDeleteEffector)GetProcAddress(hDLL,"_pxDeleteEffector@4");
// Use ==> pxDeleteEffector(int effector);
 
typedef int(WINAPI *DLL_pxWheelAddToBody)(int body, float pos_x, float pos_y, float pos_z);
DLL_pxWheelAddToBody pxWheelAddToBody;
pxWheelAddToBody = (DLL_pxWheelAddToBody)GetProcAddress(hDLL,"_pxWheelAddToBody@16");
// Use ==> int result = pxWheelAddToBody(int body, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxWheelSetRadius)(int wheel, float radius);
DLL_pxWheelSetRadius pxWheelSetRadius;
pxWheelSetRadius = (DLL_pxWheelSetRadius)GetProcAddress(hDLL,"_pxWheelSetRadius@8");
// Use ==> pxWheelSetRadius(int wheel, float radius);
 
typedef void(WINAPI *DLL_pxWheelSetRotation)(int wheel, float pitch, float yaw, float roll);
DLL_pxWheelSetRotation pxWheelSetRotation;
pxWheelSetRotation = (DLL_pxWheelSetRotation)GetProcAddress(hDLL,"_pxWheelSetRotation@16");
// Use ==> pxWheelSetRotation(int wheel, float pitch, float yaw, float roll);
 
typedef void(WINAPI *DLL_pxWheelSetMotorTorque)(int wheel, float torque);
DLL_pxWheelSetMotorTorque pxWheelSetMotorTorque;
pxWheelSetMotorTorque = (DLL_pxWheelSetMotorTorque)GetProcAddress(hDLL,"_pxWheelSetMotorTorque@8");
// Use ==> pxWheelSetMotorTorque(int wheel, float torque);
 
typedef void(WINAPI *DLL_pxWheelSetSteerAngle)(int wheel, float angle);
DLL_pxWheelSetSteerAngle pxWheelSetSteerAngle;
pxWheelSetSteerAngle = (DLL_pxWheelSetSteerAngle)GetProcAddress(hDLL,"_pxWheelSetSteerAngle@8");
// Use ==> pxWheelSetSteerAngle(int wheel, float angle);
 
typedef void(WINAPI *DLL_pxWheelSetBrakeTorque)(int wheel, float torque);
DLL_pxWheelSetBrakeTorque pxWheelSetBrakeTorque;
pxWheelSetBrakeTorque = (DLL_pxWheelSetBrakeTorque)GetProcAddress(hDLL,"_pxWheelSetBrakeTorque@8");
// Use ==> pxWheelSetBrakeTorque(int wheel, float torque);
 
typedef void(WINAPI *DLL_pxWheelSetSuspension)(int wheel, float susp, float rest, float damping);
DLL_pxWheelSetSuspension pxWheelSetSuspension;
pxWheelSetSuspension = (DLL_pxWheelSetSuspension)GetProcAddress(hDLL,"_pxWheelSetSuspension@16");
// Use ==> pxWheelSetSuspension(int wheel, float susp, float rest, float damping);
 
typedef void(WINAPI *DLL_pxWheelSetFrictionToSide)(int wheel, float friction);
DLL_pxWheelSetFrictionToSide pxWheelSetFrictionToSide;
pxWheelSetFrictionToSide = (DLL_pxWheelSetFrictionToSide)GetProcAddress(hDLL,"_pxWheelSetFrictionToSide@8");
// Use ==> pxWheelSetFrictionToSide(int wheel, float friction);
 
typedef void(WINAPI *DLL_pxWheelSetFrictionToFront)(int wheel, float friction);
DLL_pxWheelSetFrictionToFront pxWheelSetFrictionToFront;
pxWheelSetFrictionToFront = (DLL_pxWheelSetFrictionToFront)GetProcAddress(hDLL,"_pxWheelSetFrictionToFront@8");
// Use ==> pxWheelSetFrictionToFront(int wheel, float friction);
 
typedef void(WINAPI *DLL_pxWheelSetCollisionGroup)(int wheel, int group);
DLL_pxWheelSetCollisionGroup pxWheelSetCollisionGroup;
pxWheelSetCollisionGroup = (DLL_pxWheelSetCollisionGroup)GetProcAddress(hDLL,"_pxWheelSetCollisionGroup@8");
// Use ==> pxWheelSetCollisionGroup(int wheel, int group);
 
typedef float(WINAPI *DLL_pxWheelGetSteerAngle)(int wheel);
DLL_pxWheelGetSteerAngle pxWheelGetSteerAngle;
pxWheelGetSteerAngle = (DLL_pxWheelGetSteerAngle)GetProcAddress(hDLL,"_pxWheelGetSteerAngle@4");
// Use ==> float result = pxWheelGetSteerAngle(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetAxleSpeed)(int wheel);
DLL_pxWheelGetAxleSpeed pxWheelGetAxleSpeed;
pxWheelGetAxleSpeed = (DLL_pxWheelGetAxleSpeed)GetProcAddress(hDLL,"_pxWheelGetAxleSpeed@4");
// Use ==> float result = pxWheelGetAxleSpeed(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetRadius)(int wheel);
DLL_pxWheelGetRadius pxWheelGetRadius;
pxWheelGetRadius = (DLL_pxWheelGetRadius)GetProcAddress(hDLL,"_pxWheelGetRadius@4");
// Use ==> float result = pxWheelGetRadius(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetSuspensionTravel)(int wheel);
DLL_pxWheelGetSuspensionTravel pxWheelGetSuspensionTravel;
pxWheelGetSuspensionTravel = (DLL_pxWheelGetSuspensionTravel)GetProcAddress(hDLL,"_pxWheelGetSuspensionTravel@4");
// Use ==> float result = pxWheelGetSuspensionTravel(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetSuspensionRestitution)(int wheel);
DLL_pxWheelGetSuspensionRestitution pxWheelGetSuspensionRestitution;
pxWheelGetSuspensionRestitution = (DLL_pxWheelGetSuspensionRestitution)GetProcAddress(hDLL,"_pxWheelGetSuspensionRestitution@4");
// Use ==> float result = pxWheelGetSuspensionRestitution(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetSuspensionDamping)(int wheel);
DLL_pxWheelGetSuspensionDamping pxWheelGetSuspensionDamping;
pxWheelGetSuspensionDamping = (DLL_pxWheelGetSuspensionDamping)GetProcAddress(hDLL,"_pxWheelGetSuspensionDamping@4");
// Use ==> float result = pxWheelGetSuspensionDamping(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetPositionX)(int wheel);
DLL_pxWheelGetPositionX pxWheelGetPositionX;
pxWheelGetPositionX = (DLL_pxWheelGetPositionX)GetProcAddress(hDLL,"_pxWheelGetPositionX@4");
// Use ==> float result = pxWheelGetPositionX(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetPositionY)(int wheel);
DLL_pxWheelGetPositionY pxWheelGetPositionY;
pxWheelGetPositionY = (DLL_pxWheelGetPositionY)GetProcAddress(hDLL,"_pxWheelGetPositionY@4");
// Use ==> float result = pxWheelGetPositionY(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetPositionZ)(int wheel);
DLL_pxWheelGetPositionZ pxWheelGetPositionZ;
pxWheelGetPositionZ = (DLL_pxWheelGetPositionZ)GetProcAddress(hDLL,"_pxWheelGetPositionZ@4");
// Use ==> float result = pxWheelGetPositionZ(int wheel);
 
typedef void(WINAPI *DLL_pxWheelUpdateSpec)(int wheel, int mode);
DLL_pxWheelUpdateSpec pxWheelUpdateSpec;
pxWheelUpdateSpec = (DLL_pxWheelUpdateSpec)GetProcAddress(hDLL,"_pxWheelUpdateSpec@8");
// Use ==> pxWheelUpdateSpec(int wheel, int mode);
 
typedef float(WINAPI *DLL_pxWheelGetPositionXSpec)(int wheel);
DLL_pxWheelGetPositionXSpec pxWheelGetPositionXSpec;
pxWheelGetPositionXSpec = (DLL_pxWheelGetPositionXSpec)GetProcAddress(hDLL,"_pxWheelGetPositionXSpec@4");
// Use ==> float result = pxWheelGetPositionXSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetPositionYSpec)(int wheel);
DLL_pxWheelGetPositionYSpec pxWheelGetPositionYSpec;
pxWheelGetPositionYSpec = (DLL_pxWheelGetPositionYSpec)GetProcAddress(hDLL,"_pxWheelGetPositionYSpec@4");
// Use ==> float result = pxWheelGetPositionYSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetPositionZSpec)(int wheel);
DLL_pxWheelGetPositionZSpec pxWheelGetPositionZSpec;
pxWheelGetPositionZSpec = (DLL_pxWheelGetPositionZSpec)GetProcAddress(hDLL,"_pxWheelGetPositionZSpec@4");
// Use ==> float result = pxWheelGetPositionZSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetRotationPitchSpec)(int wheel);
DLL_pxWheelGetRotationPitchSpec pxWheelGetRotationPitchSpec;
pxWheelGetRotationPitchSpec = (DLL_pxWheelGetRotationPitchSpec)GetProcAddress(hDLL,"_pxWheelGetRotationPitchSpec@4");
// Use ==> float result = pxWheelGetRotationPitchSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetRotationYawSpec)(int wheel);
DLL_pxWheelGetRotationYawSpec pxWheelGetRotationYawSpec;
pxWheelGetRotationYawSpec = (DLL_pxWheelGetRotationYawSpec)GetProcAddress(hDLL,"_pxWheelGetRotationYawSpec@4");
// Use ==> float result = pxWheelGetRotationYawSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetRotationRollSpec)(int wheel);
DLL_pxWheelGetRotationRollSpec pxWheelGetRotationRollSpec;
pxWheelGetRotationRollSpec = (DLL_pxWheelGetRotationRollSpec)GetProcAddress(hDLL,"_pxWheelGetRotationRollSpec@4");
// Use ==> float result = pxWheelGetRotationRollSpec(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactX)(int wheel);
DLL_pxWheelGetContactX pxWheelGetContactX;
pxWheelGetContactX = (DLL_pxWheelGetContactX)GetProcAddress(hDLL,"_pxWheelGetContactX@4");
// Use ==> float result = pxWheelGetContactX(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactY)(int wheel);
DLL_pxWheelGetContactY pxWheelGetContactY;
pxWheelGetContactY = (DLL_pxWheelGetContactY)GetProcAddress(hDLL,"_pxWheelGetContactY@4");
// Use ==> float result = pxWheelGetContactY(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactZ)(int wheel);
DLL_pxWheelGetContactZ pxWheelGetContactZ;
pxWheelGetContactZ = (DLL_pxWheelGetContactZ)GetProcAddress(hDLL,"_pxWheelGetContactZ@4");
// Use ==> float result = pxWheelGetContactZ(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactForce)(int wheel);
DLL_pxWheelGetContactForce pxWheelGetContactForce;
pxWheelGetContactForce = (DLL_pxWheelGetContactForce)GetProcAddress(hDLL,"_pxWheelGetContactForce@4");
// Use ==> float result = pxWheelGetContactForce(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLatDirectionX)(int wheel);
DLL_pxWheelGetContactLatDirectionX pxWheelGetContactLatDirectionX;
pxWheelGetContactLatDirectionX = (DLL_pxWheelGetContactLatDirectionX)GetProcAddress(hDLL,"_pxWheelGetContactLatDirectionX@4");
// Use ==> float result = pxWheelGetContactLatDirectionX(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLatDirectionY)(int wheel);
DLL_pxWheelGetContactLatDirectionY pxWheelGetContactLatDirectionY;
pxWheelGetContactLatDirectionY = (DLL_pxWheelGetContactLatDirectionY)GetProcAddress(hDLL,"_pxWheelGetContactLatDirectionY@4");
// Use ==> float result = pxWheelGetContactLatDirectionY(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLatDirectionZ)(int wheel);
DLL_pxWheelGetContactLatDirectionZ pxWheelGetContactLatDirectionZ;
pxWheelGetContactLatDirectionZ = (DLL_pxWheelGetContactLatDirectionZ)GetProcAddress(hDLL,"_pxWheelGetContactLatDirectionZ@4");
// Use ==> float result = pxWheelGetContactLatDirectionZ(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLatImpulse)(int wheel);
DLL_pxWheelGetContactLatImpulse pxWheelGetContactLatImpulse;
pxWheelGetContactLatImpulse = (DLL_pxWheelGetContactLatImpulse)GetProcAddress(hDLL,"_pxWheelGetContactLatImpulse@4");
// Use ==> float result = pxWheelGetContactLatImpulse(int wheel);
 
// End of Part 1
