@echo off

chdir /d message-service
start /b gradle bootBuildImage

chdir /d ../session-service
start /b gradle bootBuildImage

chdir /d ../gateway-api
start /b gradle bootBuildImage

pause