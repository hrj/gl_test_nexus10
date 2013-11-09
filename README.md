This is a minimal app that crashes on Nexus 10, likely due to a bug in the Nexus 10 system. It works fine on other devices.

This test-case is based on a [real app](https://play.google.com/store/apps/details?id=com.lavadip.skeyepro) that is also reported to crash on the Nexus 10.

#### What the app does
The app draws a texture N=25 times on the screen. Each time, the texture is updated in place using `glTexSubImage2D`.
A video of how this appears on a Nexus 4 can be viewed [here](http://www.youtube.com/watch?v=5y8ci3X2VLM).

#### Bug Description
On the Nexus 10, this app crashes silently. There is no exception stack in the log, nor is a "Forced Close" dialog shown to the user.

If the the number of draws (N) is reduced, there is no crash. I have tried with N=2 and it didn't crash.

I haven't found the exact N at which the crash behavior begins (because I don't own a Nexus 10 myself).

#### Guide to the code
The main action is in `MyRenderer.java`. A bitmap is created and then a canvas that draws on the bitmap is created. The Canvas APIs are used to draw an arc into the bitmap.
The bitmap is then read out into a byte buffer, and then the texure is updated using `glTexSubImage2D`. This is then shown on the screen
using a GLSL shader.
