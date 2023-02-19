@echo off

pushd agilator
call npm run build
robocopy build ../agilator-be/resources/public /S /is /it
popd

pushd agilator-be
call lein uberjar
popd

