
# FileScrapper ~ Downloading become easy ;)

This is an android library (aar) by which you can use any file hosting service as an alternative to firebase storage. Alternative because you can only download files, you can't upload (will be available in future).
You can download files from Mediafires, Drive, Anonfiles (more in next release).

## Implementation 

In your project build.gradle

```groovy
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
In your module (app) build.gradle
```groovy
dependencies {
	        implementation 'com.github.ErrorxCode:FileScrapper:1.0'
	}
```

## Usage / Examples

To download a file from google drive :-
```java
 try {
      boolean success = FileScrapper.download(Provider.DRIVE,"https://drive.google.com/file/d/13XLRUKgU-O_qrGbpM9GcafRGkHU8Rxq9/view?usp=sharing", Environment.DIRECTORY_DOWNLOADS);
    } catch (DriveException e) {
        e.printStackTrace();
    }
```
You can select between ```Provider.DRIVE, Provider.ANONFILES, Provider.MEDIAFIRE```.

**Note : In case of drive, make sure that file size is not large & file link is sharable (Anyone with the link can download). Otherwise ```DriveException``` will be thrown.**
  
