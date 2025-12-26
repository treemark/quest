# CMake configuration for OpenXR VR integration
cmake_minimum_required(VERSION 3.22.1)
project("questvr")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find OpenXR loader (provided by Meta in the APK)
# We'll load it dynamically at runtime

# Add the native library
add_library(questvr SHARED
    openxr_bridge.cpp
    vr_renderer.cpp
)

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(egl-lib EGL)
find_library(gles3-lib GLESv3)

# Link libraries
target_link_libraries(questvr
    ${log-lib}
    ${android-lib}
    ${egl-lib}
    ${gles3-lib}
)

# Include directories
target_include_directories(questvr PRIVATE
    ${CMAKE_SOURCE_DIR}
)

