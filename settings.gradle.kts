rootProject.name = "supervisor"
include( "redis-repository")
include("core")
include("configuration")
include("yaml-configuration")
include("util")

include("dummy")
include("toml-configuration")
include("mongo-repository")
include("itemstack-repository")
include("menu")