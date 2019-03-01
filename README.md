## MobAd SDK


## Getting Started

These instructions will get you a copy of the project up and running on your Android App for development and testing purposes.

### How to import

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency

```
	dependencies {
	        implementation 'com.github.mounzeragha:MobAdSdk:0.1.8'
	}
```

### How to use:

Include the code below in your MainActivity:

```
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       ...
        
	//Initilaze MobAd
        mobAd = new MobAd(this);

        if (mobAd.hasReadPhoneStatePermission()) {
            //You already have the permission, just go ahead.
            mobAd.registerPhoneCallsReceiver();
        } else {
            //request the permission
            mobAd.requestReadPhoneStatePermission();
        }
	
	...

    }

    @Override
    protected void onResume() {
        super.onResume();
        //request overlay permission if it has not granted.
        mobAd.requestDrawOverAppsPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mobAd.hasReadPhoneStatePermissionGranted(requestCode, permissions, grantResults)) {
            Log.d("onPermissionsGranted: ", "Read Phone State Permission Granted.");
            mobAd.registerPhoneCallsReceiver();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mobAd.unregisterPhoneCallsReceiver();
    }
```

That's all you need to do.

### License:

```
Copyright 2019 Oqunet

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

