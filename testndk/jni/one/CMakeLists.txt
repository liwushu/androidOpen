cmake_minimum_required(VERSION 3.6.0)

set(ANDROID_NDK_REVISION 15)

# now build app's shared lib
# set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall")

#设置生成的so动态库最后输出的路径
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

add_library(Hello-ndk SHARED
           com_flying_testndk_MainActivity.cpp
           elfhook/elfhook.cpp
           elfhook/elfio.cpp
           elfhook/elfutils.cpp
           )

find_library(log
             GLESv2)

# add lib dependencies
target_link_libraries(
                      Hello-ndk
                      log
                      GLESv2)