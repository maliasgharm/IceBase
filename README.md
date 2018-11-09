# IcebaseExample
This is a communication service
[![](https://jitpack.io/v/maliasgharm/IcebaseExample.svg)](https://jitpack.io/#maliasgharm/IcebaseExample)
How to

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

    gradle
    maven
    sbt
    leiningen

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.maliasgharm:IcebaseExample:master'
	}

add Application class exstand with SocketIOApplication : 

```KOTLIN
class Application : SocketIOApplication() {
    override fun OnChanged(status: String) {
        super.OnChanged(status)
        Log.w("status",status)
    }

    override fun OnMessasgeReceive(eventItem: String, message: Any) {
        super.OnMessasgeReceive(eventItem, message)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        initialized(base)
    }
}
```


