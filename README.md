#### [HIRE US](http://vrgsoft.net/)
# ArcProgressBar
[![](https://jitpack.io/v/VRGsoftUA/ArcProgressBar.svg)](https://jitpack.io/#VRGsoftUA/ArcProgressBar)

Library contains the arc progress bar with gradient</br></br>
<img src="https://github.com/VRGsoftUA/ArcProgressBar/blob/master/demo.gif" width="270" height="480" />

# Usage
*For a working implementation, Have a look at the Sample Project - app*
1. Include the library as local library project.
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.VRGsoftUA:ArcProgressBar:1.0.0'
}
```
2. Customize your `ArcProgressBar`

| Property | Type | Description |
| ------- | --- | --- |
| arc_progress | integer | current progress value |
| arc_max | integer | max progress value |
| arc_thickness | dimension | arc line thickness |
| arc_unfinished_color | color | background color |
| arc_progress_start_color | color | start gradient progress color  |
| arc_progress_end_color | color | end gradient progress color |
| arc_text_size | dimension | progress value text size |
| arc_text_color | color | progress value text color |
| arc_suffix_text | string | text in the right top corner (`%` by default) |
| arc_suffix_text_size | dimension | suffix text size |
| arc_suffix_text_padding | dimension | suffix text padding |

3. Just set `progress` value to component 

#### Contributing
* Contributions are always welcome
* If you want a feature and can code, feel free to fork and add the change yourself and make a pull request
