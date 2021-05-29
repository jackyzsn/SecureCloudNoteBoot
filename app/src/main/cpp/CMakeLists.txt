cmake_minimum_required(VERSION 3.4.1)

add_library(load-env SHARED
            load-env.c)

# Include libraries needed for load-env lib
target_link_libraries(load-env
                      android
                      log)