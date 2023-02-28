@echo off

pushd apps
pushd home
call npm run build
robocopy dist ../../agilator-be/resources/public/ /S /is /it /MIR
popd

pushd retro
call npm run build
robocopy dist ../../agilator-be/resources/public/retro /S /is /it
popd


pushd poker
call npm run build
robocopy dist ../../agilator-be/resources/public/poker /S /is /it
popd

popd

robocopy . agilator-be/resources/public/ gator_blue.png

pushd agilator-be
call lein uberjar
popd

