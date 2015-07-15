# JellyRefreshLayout
A pull-down-to-refresh layout inspired by Lollipop overscrolled effects

Preview
--------
![Preview](images/preview.gif)

Download
--------
Gradle:
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
dependencies {
    compile 'com.github.allan1st:JellyRefreshLayout:1.0.0'
}
```
or Maven
```xml
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.allan1st</groupId>
    <artifactId>JellyRefreshLayout</artifactId>
    <version>1.0.0</version>
</dependency>
```

Usage
--------
Wrap any RecyclerView/ScrollView/ListView with JellyRefreshLayout

```xml
<uk.co.imallan.jellyrefresh.JellyRefreshLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:text="@string/your_loading_text"
    android:textColor="@color/your_loading_text_color"
    app:jellyColor="@color/your_jelly_color"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
    
</uk.co.imallan.jellyrefresh.JellyRefreshLayout>
```

Call back when triggering refresh:
```java
mJellyLayout.setRefreshListener(new JellyRefreshLayout.JellyRefreshListener() {
    @Override
    public void onRefresh(final JellyRefreshLayout jellyRefreshLayout) {
        // your code here
    }
});
```
and finish refreshing when you are done:
```java
mJellyLayout.finishRefreshing();
```

You can also find more usages in the sample app included

License
--------
    The MIT License (MIT)

    Copyright (c) 2015 Yilun Chen

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
