@echo off

pushd apps
pushd retro
call npm run build
robocopy dist ../../agilator-be/resources/public/retro /S /is /it
popd
popd

pushd agilator-be
call lein uberjar
popd

