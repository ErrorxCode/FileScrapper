
# FileScrapper ~ Downloading become easy ;)
<p align="left">
  <a href="#"><img alt="Languages-Java" src="https://img.shields.io/badge/Language-Java-1DA1F2?style=flat-square&logo=java"></a>
  <a href="https://www.instagram.com/x__coder__x/"><img alt="Instagram - x__coder__" src="https://img.shields.io/badge/Instagram-x____coder____x-lightgrey"></a>
  <a href="#"><img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/ErrorxCode/OTP-Verification-Api?style=social"></a>
  </p>

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

#### To download a file from google drive :-
```java
 try {
      boolean success = FileScrapper.download(Provider.DRIVE,"https://drive.google.com/file/d/13XLRUKgU-O_qrGbpM9GcafRGkHU8Rxq9/view?usp=sharing", Environment.DIRECTORY_DOWNLOADS);
    } catch (DriveException e) {
        e.printStackTrace();
    }
```
You can select between ```Provider.DRIVE, Provider.ANONFILES, Provider.MEDIAFIRE```.

  
#### To get final download link :-
```java
 try {
      String link = FileScrapper.getDownloadLink(Provider.MEDIAFIRE,"https://www.mediafire.com/file/zg8qixmkuym04j6/1.5.0_NO_GRASS_%252B_90_FPS_CONFIG.zip/file");
      // Manually download or fire this link in browser.
    } catch (DriveException e) {
        e.printStackTrace();
    }
```
## API Reference


| Method | Return type     | Description                |
| :-------- | :------- | :------------------------- |
| `getDownloadLink(Provider provider,String link)` | `string` | This method return's the final download of link of the file which is given by the provider. |
| `download(Provider provider,String link,String download_directory)`      | `boolean` | This method download file from the link synchronously. |
| ` downloadInBackground(Provider provider,String link,String download_directory)`      | `boolean` | This method download's the file in a background thread. Calling this is equivalent to calling download(Provider, String, String) in a separate thread |
| `downloadWithProgress(Context context,Provider provider,String link,String download_directory)`      | `boolean` | This method download's the file synchronously in a separate thread. This method also shows an progress dialog while downloading the file. You don't need to put this in thread or async task |
| `downloadWithNotification(@NonNull Context context,Provider provider,String link,String download_directory,String title,String description)`      | `boolean` | This method use android DownloadManager to download the file. This also shows the notification of the download. |

**Thanks for using my library 🙏💙**
